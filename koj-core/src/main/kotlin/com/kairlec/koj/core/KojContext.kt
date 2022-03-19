package com.kairlec.koj.core

import com.kairlec.koj.common.TempDirectory
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import mu.KotlinLogging
import org.reflections.Reflections
import org.reflections.scanners.Scanners

data class RunConfig(
    val maxTime: Int,
    val maxMemory: Long,
    val maxOutputSize: Long,
    val maxStack: Long = -1,
    val maxProcessNumber: Int = 1,
    val args: List<String> = emptyList(),
    val env: List<String> = emptyList(),
)

interface KojContext {
    val id: Long
    val namespace: String
    val tempDirectory: TempDirectory
    val useLanguage: Language
    val runConfig: RunConfig
    val stdin: String
    val factory: KojFactory
    val code: String
    val state: Flow<State>
    val debug: Boolean

    suspend fun run()
}

interface KojContextFactory : LanguageSupport {
    fun createCompileConfig(context: KojContext): CompileConfig
    fun createExecutorConfig(context: KojContext): ExecutorConfig {
        context.runConfig.let {
            return ExecutorConfig(
                it.maxTime,
                it.maxMemory,
                it.maxOutputSize,
                it.maxStack,
                it.maxProcessNumber,
                it.args,
                it.env,
                context.debug
            )
        }
    }
}

interface KojFactory : LanguageSupport {
    fun create(
        id: Long,
        namespace: String,
        tempDirectory: TempDirectory,
        useLanguage: Language,
        code: String,
        stdin: String,
        runConfig: RunConfig,
        debug: Boolean
    ): KojContext {
        return KojContextImpl(
            id = id,
            namespace = namespace,
            tempDirectory = tempDirectory,
            useLanguage = useLanguage,
            runConfig = runConfig,
            stdin = stdin,
            code = code,
            factory = this,
            debug = debug
        )
    }

    val supportLanguages: Set<Language>

    fun chooseCompiler(language: Language): KojCompiler
    fun chooseCompilers(language: Language): List<KojCompiler>

    fun chooseExecutor(language: Language): KojExecutor
    fun chooseExecutors(language: Language): List<KojExecutor>

    fun chooseContextFactory(context: KojContext): KojContextFactory
    fun chooseContextFactories(context: KojContext): List<KojContextFactory>

    data class Supports(
        val compilers: List<KojCompiler>,
        val executors: List<KojExecutor>,
        val contextFactories: List<KojContextFactory>
    )

    companion object : KojFactory {
        private val log = KotlinLogging.logger {}
        val languages: Set<Language>
        private val compiler: Set<KojCompiler>
        private val executor: Set<KojExecutor>
        private val kojContextFactories: Set<KojContextFactory>
        private val languageSupports: MutableMap<Language, Supports> = mutableMapOf()
        override val supportLanguages: Set<Language>
            get() = languageSupports.keys

        init {
            val reflections = Reflections("com.kairlec.koj", Scanners.SubTypes)
            languages = reflections.getSubTypesOf(Language::class.java).mapNotNull {
                it?.kotlin?.objectInstance
            }.toSet()
            compiler = reflections.getSubTypesOf(KojCompiler::class.java).mapNotNull {
                it?.kotlin?.objectInstance
            }.toSet()
            executor = reflections.getSubTypesOf(KojExecutor::class.java).mapNotNull {
                it?.kotlin?.objectInstance
            }.toSet()
            kojContextFactories = reflections.getSubTypesOf(KojContextFactory::class.java).mapNotNull {
                it?.kotlin?.objectInstance
            }.toSet()
            log.info { "Loaded languages: ${languages.joinToString { "${it.name}(${it.version})" }}" }
            log.info { "Loaded compilers: ${compiler.joinToString { it.name }}" }
            log.info { "Loaded executors: ${executor.joinToString { it.name }}" }
            log.info { "Loaded context factories: ${kojContextFactories.joinToString { it::class.simpleName ?: "" }}" }
            languages.forEach { language ->
                val supports = Supports(
                    compiler.filter { it.isSupported(language) },
                    executor.filter { it.isSupported(language) },
                    kojContextFactories.filter { it.isSupported(language) }
                )
                if (supports.compilers.isEmpty()) {
                    log.error { "No compiler found for language ${language.name}" }
                    return@forEach
                }
                if (supports.executors.isEmpty()) {
                    log.error { "No executor found for language ${language.name}" }
                    return@forEach
                }
                if (supports.contextFactories.isEmpty()) {
                    log.error { "No context factory found for language ${language.name}" }
                    return@forEach
                }
                languageSupports[language] = supports
                log.info { "Language $language supports: ${languageSupports[language]}" }
            }
        }

        override fun chooseCompiler(language: Language): KojCompiler {
            return languageSupports[language]?.compilers?.firstOrNull()
                ?: throw IllegalArgumentException("No compiler for language $language")
        }

        override fun chooseCompilers(language: Language): List<KojCompiler> {
            return languageSupports[language]?.compilers ?: emptyList()
        }

        override fun chooseExecutor(language: Language): KojExecutor {
            return languageSupports[language]?.executors?.firstOrNull()
                ?: throw IllegalArgumentException("No executor for language $language")
        }

        override fun chooseExecutors(language: Language): List<KojExecutor> {
            return languageSupports[language]?.executors ?: emptyList()
        }

        override fun chooseContextFactory(context: KojContext): KojContextFactory {
            return languageSupports[context.useLanguage]?.contextFactories?.firstOrNull()
                ?: throw IllegalArgumentException("No context factory for language ${context.useLanguage}")
        }

        override fun chooseContextFactories(context: KojContext): List<KojContextFactory> {
            return languageSupports[context.useLanguage]?.contextFactories ?: emptyList()
        }

        override fun isSupported(language: Language): Boolean {
            return language in languages
        }
    }
}

data class KojContextImpl(
    override val id: Long,
    override val namespace: String,
    override val tempDirectory: TempDirectory,
    override val useLanguage: Language,
    override val runConfig: RunConfig,
    override val stdin: String,
    override val code: String,
    override val factory: KojFactory,
    private val stateChannel: Channel<State> = Channel(6),
    override val debug: Boolean,
) : KojContext {
    override val state: Flow<State> = stateChannel.receiveAsFlow()
    private var stateValue = State()

    companion object {
        private val log = KotlinLogging.logger {}
    }

    private suspend fun nextState(
        stdout: String? = null,
        stderr: String? = null,
        time: Long? = null,
        memory: Long? = null
    ) {
        stateValue = stateValue.next(stdout, stderr, time, memory)
        stateChannel.send(stateValue)
    }

    private suspend fun nextState(value: Int, stdout: String? = null, stderr: String? = null) {
        stateValue = stateValue.next(value, stdout, stderr)
        stateChannel.send(stateValue)
    }

    private suspend fun errorState(cause: Throwable, stdout: String? = null, stderr: String? = null) {
        stateValue = stateValue.error(cause, stdout, stderr)
        stateChannel.send(stateValue)
    }

    private suspend fun runInternal() {
        nextState() // inited
        val contextFactory = factory.chooseContextFactory(this)
        val compiler = factory.chooseCompiler(useLanguage)
        log.debug { "choose compile:${compiler}" }
        val executor = factory.chooseExecutor(useLanguage)
        log.debug { "choose executor:${executor}" }
        val compileConfig = contextFactory.createCompileConfig(this)
        log.debug { "create compile config:${compileConfig}" }
        val executorConfig = contextFactory.createExecutorConfig(this)
        log.debug { "create executor config:${executorConfig}" }
        val compileResult = compiler.compile(this, compileConfig)
        log.info { "compile result:${compileResult}" }
        if (compileResult is CompileSuccess) {
            nextState() //compiled
            log.info { "Compile success" }
            val executeResult = executor.execute(this, compileResult, stdin, executorConfig)
            log.info { "execute result:${executeResult}" }
            if (executeResult is ExecuteSuccess) {
                log.info { "Execute success" }
                if (executeResult.type != ExecuteResultType.AC) {
                    errorState(ExecuteResultException(executeResult.type), stderr = executeResult.stderr)
                } else {
                    nextState(
                        executeResult.stdout,
                        executeResult.stderr,
                        executeResult.time,
                        executeResult.memory
                    ) // executed
                }
            } else {
                errorState(
                    (executeResult as ExecuteFailure).cause
                        ?: IllegalStateException("Execute failure:${executeResult.message}"),
                    stderr = executeResult.stderr
                )
            }
        } else {
            errorState(
                (compileResult as CompileFailure).cause
                    ?: IllegalStateException("Compile failure:${compileResult.message}"),
                stderr = compileResult.stderr
            )
        }
    }

    override suspend fun run() {
        if (debug) {
            runInternal()
        } else {
            tempDirectory.use {
                runInternal()
            }
        }
        nextState(State.END) // end
        stateChannel.close()
    }
}