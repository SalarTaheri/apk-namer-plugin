import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.impl.VariantOutputImpl
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ApkNamerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("apkNamer", ApkNamerExtension::class.java)

        project.pluginManager.withPlugin("com.android.application") {
            val androidComponents = project.extensions
                .getByType(ApplicationAndroidComponentsExtension::class.java)

            // کش کردن git sha به صورت لِیزی برای پرهیز از اجرای مکرر دستور سیستم
            var cachedGitSha: String? = null
            var gitShaQueried = false

            androidComponents.onVariants { variant ->
                if (!extension.enabled) return@onVariants

                val buildType = variant.buildType
                if (extension.targetBuildTypes.isNotEmpty() && (buildType == null || !extension.targetBuildTypes.contains(buildType))) {
                    return@onVariants
                }
                if (extension.excludeBuildTypes.isNotEmpty() && buildType != null && extension.excludeBuildTypes.contains(buildType)) {
                    return@onVariants
                }

                val firstOutput = variant.outputs.firstOrNull()
                val versionName = firstOutput?.versionName?.orNull
                val versionCode = firstOutput?.versionCode?.orNull

                val flavorName = variant.flavorName?.takeIf { it.isNotEmpty() }

                val formattedDate = try {
                    SimpleDateFormat(extension.dateFormat, Locale.getDefault()).format(Date())
                } catch (_: Exception) {
                    SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
                }

                val needsGitSha = extension.includeGitSha || (extension.pattern?.contains("{gitSha}") == true)
                val gitSha = if (needsGitSha) {
                    if (!gitShaQueried) {
                        cachedGitSha = GitHelper.getShortGitSha(project)
                        gitShaQueried = true
                    }
                    cachedGitSha
                } else null

                val baseName = extension.baseName?.takeIf { it.isNotBlank() } ?: project.rootProject.name

                val context = VariantContext(
                    baseName = baseName,
                    projectName = project.name,
                    rootProjectName = project.rootProject.name,
                    moduleName = project.name,
                    flavorName = flavorName,
                    buildType = buildType,
                    versionName = versionName,
                    versionCode = versionCode,
                    date = formattedDate,
                    gitSha = gitSha,
                    variantName = variant.name
                )

                val apkName = ApkNameFormatter.format(context, extension)

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
