import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.impl.VariantOutputImpl
import org.gradle.api.Plugin
import org.gradle.api.Project

class ApkNamerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("apkNamer", ApkNamerExtension::class.java)

        project.pluginManager.withPlugin("com.android.application") {
            val androidComponents = project.extensions
                .getByType(ApplicationAndroidComponentsExtension::class.java)

            androidComponents.onVariants { variant ->
                if (!extension.enabled) return@onVariants

                val sep = extension.separator
                val versionName = variant.outputs.firstOrNull()
                    ?.versionName?.orNull ?: "unknown"

                val flavorName = variant.flavorName?.takeIf { it.isNotEmpty() }
                val buildType = variant.buildType?.takeIf { it.isNotEmpty() }

                val apkName = buildString {
                    flavorName?.let { append(it) }
                    append("$sep$versionName")
                    buildType?.let { append("$sep$it") }
                    append(".apk")
                }

                variant.outputs.forEach { output ->
                    if (output is VariantOutputImpl) {
                        output.outputFileName.set(apkName)
                    }
                }

                project.logger.lifecycle("ApkName=$apkName (${variant.name})")
            }
        }
    }
}
