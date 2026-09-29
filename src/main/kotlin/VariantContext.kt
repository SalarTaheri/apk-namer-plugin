data class VariantContext(
    val baseName: String,
    val projectName: String,
    val rootProjectName: String,
    val moduleName: String,
    val flavorName: String?,
    val buildType: String?,
    val versionName: String?,
    val versionCode: Int?,
    val date: String,
    val gitSha: String?,
    val variantName: String
)
