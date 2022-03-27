import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.internal.catalog.ExternalModuleDependencyFactory
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.exclude

fun <T : ModuleDependency> T.exclude(provider: Provider<MinimalExternalModuleDependency>): T =
    provider.get().module.let { module ->
        exclude(module.group, module.name)
    }

fun <T : ModuleDependency> T.exclude(dep: ExternalModuleDependencyFactory.DependencyNotationSupplier): T =
    exclude(dep.asProvider())
