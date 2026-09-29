object ApkNameFormatter {

    fun format(context: VariantContext, extension: ApkNamerExtension): String {
        // ۱. اگر کاربر لامبدای سفارشی تعریف کرده باشد
        val custom = extension.customNameResolver?.invoke(context)
        if (custom != null) {
            return ensureApkExtension(custom)
        }

        val sep = extension.separator

        val coreName = if (!extension.pattern.isNullOrBlank()) {
            formatPattern(context, extension.pattern!!, sep)
        } else {
            formatDefaultComponents(context, extension)
        }

        val casedName = extension.caseFormat.apply(coreName)
        val finalName = "${extension.prefix}$casedName${extension.suffix}"
        return ensureApkExtension(finalName)
    }

    private fun formatPattern(context: VariantContext, pattern: String, separator: String): String {
        val replacements = mapOf(
            "{baseName}" to context.baseName,
            "{rootProject}" to context.rootProjectName,
            "{moduleName}" to context.moduleName,
            "{project}" to context.projectName,
            "{flavor}" to (context.flavorName ?: ""),
            "{flavorName}" to (context.flavorName ?: ""),
            "{buildType}" to (context.buildType ?: ""),
            "{versionName}" to (context.versionName ?: ""),
            "{versionCode}" to (context.versionCode?.toString() ?: ""),
            "{date}" to context.date,
            "{timestamp}" to context.date,
            "{gitSha}" to (context.gitSha ?: ""),
            "{variantName}" to context.variantName
        )

        var result = pattern
        for ((token, value) in replacements) {
            result = result.replace(token, value)
        }

        // تمیزکاری جداکننده‌های تکراری پشت سر هم ناشی از توکن‌های خالی
        if (separator.isNotEmpty()) {
            val escapedSep = Regex.escape(separator)
            result = result.replace(Regex("$escapedSep{2,}"), separator)
            result = result.removePrefix(separator).removeSuffix(separator)
        }
        result = result.replace(Regex("-{2,}"), "-").removePrefix("-").removeSuffix("-")

        return result
    }

    private fun formatDefaultComponents(context: VariantContext, extension: ApkNamerExtension): String {
        val components = mutableListOf<String>()

        if (extension.includeBaseName && context.baseName.isNotEmpty()) {
            components.add(context.baseName)
        }
        if (extension.includeFlavor && !context.flavorName.isNullOrEmpty()) {
            components.add(context.flavorName)
        }
        if (extension.includeBuildType && !context.buildType.isNullOrEmpty()) {
            components.add(context.buildType)
        }
        if (extension.includeVersionName && !context.versionName.isNullOrEmpty()) {
            components.add(context.versionName)
        }
        if (extension.includeVersionCode && context.versionCode != null) {
            components.add(context.versionCode.toString())
        }
        if (extension.includeDate && context.date.isNotEmpty()) {
            components.add(context.date)
        }
        if (extension.includeGitSha && !context.gitSha.isNullOrEmpty()) {
            components.add(context.gitSha)
        }

        return components.joinToString(separator = extension.separator)
    }

    private fun ensureApkExtension(name: String): String {
        return if (name.endsWith(".apk", ignoreCase = true)) name else "$name.apk"
    }
}
