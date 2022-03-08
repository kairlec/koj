package com.kairlec.koj.core

import com.kairlec.koj.common.TempDirectory
import mu.KotlinLogging
import org.reflections.Reflections
import org.reflections.scanners.Scanners

enum class DiffResult {
    ACCEPT,
    PE,
    WA
}

data class RunConfig(
    val maxTime: Int,
    val maxMemory: Long,
    val maxOutputSize: Long,
    val maxStack: Long = -1,
    val maxProcessNumber: Int = 1,
    val args: List<String> = emptyList(),
    val env: List<String> = emptyList(),
)

interface Problem {
    val id: Long
    val stdin: String
    val special: Boolean
    fun getRunConfig(language: Language): RunConfig
    fun diff(stdout: String): DiffResult
}

fun Problem(
    id: Long,
    stdin: String,
    special: Boolean,
    runConfigs: Map<Language, RunConfig>,
): Problem {
    return ProblemImpl(id, stdin, special, runConfigs)
}

internal data class ProblemImpl(
    override val id: Long,
    override val stdin: String,
    override val special: Boolean,
    val runConfigs: Map<Language, RunConfig>
) : Problem {
    override fun diff(stdout: String): DiffResult {
        println("out:${stdout}")
        return DiffResult.ACCEPT
    }

    override fun getRunConfig(language: Language): RunConfig {
        return runConfigs[language] ?: throw IllegalArgumentException("No run config for language $language")
    }
}

interface ProblemFactory {
    operator fun get(id: Long): Problem
}

interface KojContext {
    val id: Long
    val namespace: String
    val tempDirectory: TempDirectory
    val useLanguage: Language
    val problem: Problem
    val factory: KojFactory
    val input: String

    suspend fun run()
}

interface KojContextFactory : LanguageSupport {
    fun createCompileConfig(context: KojContext): CompileConfig
    fun createExecutorConfig(context: KojContext): ExecutorConfig {
        context.problem.getRunConfig(context.useLanguage).let {
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
        input: String,
        problem: Problem
    ): KojContext {
        return KojContextImpl(
            id,
            namespace,
            tempDirectory,
            useLanguage,
            problem,
            input,
            this
        )
    }

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
        private val languages: Set<Language>
        private val compiler: Set<KojCompiler>
        private val executor: Set<KojExecutor>
        private val kojContextFactories: Set<KojContextFactory>
        private val languageSupports: MutableMap<Language, Supports> = mutableMapOf()

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
                languageSupports[language] = Supports(
                    compiler.filter { it.isSupported(language) },
                    executor.filter { it.isSupported(language) },
                    kojContextFactories.filter { it.isSupported(language) }
                )
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
    override val problem: Problem,
    override val input: String,
    override val factory: KojFactory,
) : KojContext {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    override suspend fun run() {
//        tempDirectory.use{
            val contextFactory = factory.chooseContextFactory(this)
            val compiler = factory.chooseCompiler(useLanguage)
            val executor = factory.chooseExecutor(useLanguage)
            val compileConfig = contextFactory.createCompileConfig(this)
            val executorConfig = contextFactory.createExecutorConfig(this)
            val compileResult = compiler.compile(this, compileConfig)
            if (compileResult is CompileSuccess) {
                log.info { "Compile success" }
                val executeResult = executor.execute(this, compileResult, problem.stdin, executorConfig)
                if (executeResult is ExecuteSuccess) {
                    log.info { "Execute success" }
                    if (executeResult.type == ExecuteResultType.AC) {
                        when (problem.diff(executeResult.stdout)) {
                            DiffResult.PE -> executeResult.type = ExecuteResultType.PE
                            DiffResult.WA -> executeResult.type = ExecuteResultType.WA
                            else -> {}
                        }
                    }
                    if (executeResult.type == ExecuteResultType.AC) {
                        throw ProblemExecuteException(problem, executeResult.type)
                    }
                } else {
                    log.info { "Execute failed" }
                    (executeResult as ExecuteFailure).cause?.let { throw it }
                        ?: throw IllegalStateException("Execute failure:${executeResult.message}")
                }
            } else {
                log.info { "Compile failed" }
                (compileResult as CompileFailure).cause?.let { throw it }
                    ?: throw IllegalStateException("Compile failure:${compileResult.message}")
            }
//        }
    }
}