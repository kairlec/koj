package com.kairlec.koj.core

import com.kairlec.koj.common.TempDirectory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    val state: StateFlow<State>

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
                it.env
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
        runConfig: RunConfig
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
    override val state: MutableStateFlow<State> = MutableStateFlow(State()),
) : KojContext {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    private fun MutableStateFlow<State>.next(
        stdout: String? = null,
        stderr: String? = null,
        time: Long = -1,
        memory: Long = -1
    ) {
        state.value = state.value.next(stdout, stderr, time, memory)
    }

    private fun MutableStateFlow<State>.next(value: Int, stdout: String? = null, stderr: String? = null) {
        state.value = state.value.next(value, stdout, stderr)
    }

    private fun MutableStateFlow<State>.error(cause: Throwable, stdout: String? = null, stderr: String? = null) {
        state.value = state.value.error(cause, stdout, stderr)
    }

    override suspend fun run() {
        tempDirectory.use {
            state.next() // inited
            val contextFactory = factory.chooseContextFactory(this)
            val compiler = factory.chooseCompiler(useLanguage)
            val executor = factory.chooseExecutor(useLanguage)
            val compileConfig = contextFactory.createCompileConfig(this)
            val executorConfig = contextFactory.createExecutorConfig(this)
            val compileResult = compiler.compile(this, compileConfig)
            if (compileResult is CompileSuccess) {
                state.next() //compiled
                log.info { "Compile success" }
                val executeResult = executor.execute(this, compileResult, stdin, executorConfig)
                if (executeResult is ExecuteSuccess) {
                    log.info { "Execute success" }
                    if (executeResult.type != ExecuteResultType.AC) {
                        state.error(ExecuteResultException(executeResult.type), stderr = executeResult.stderr)
                    } else {
                        state.next(
                            executeResult.stdout,
                            executeResult.stderr,
                            executeResult.time,
                            executeResult.memory
                        ) // executed
                    }
                } else {
                    state.error(
                        (executeResult as ExecuteFailure).cause
                            ?: IllegalStateException("Execute failure:${executeResult.message}"),
                        stderr = executeResult.stderr
                    )
                }
            } else {
                state.error(
                    (compileResult as CompileFailure).cause
                        ?: IllegalStateException("Compile failure:${compileResult.message}"),
                    stderr = compileResult.stderr
                )

            }
        }
        state.next(State.END) // end
    }
}