import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ApkNamerPluginTest {

    @Test
    fun `plugin registers extension successfully with default values`() {
        val project = ProjectBuilder.builder().withName("MyTestApp").build()
        project.pluginManager.apply("ir.miranmahaleh.salar.apk-namer")

        val extension = project.extensions.findByName("apkNamer") as? ApkNamerExtension
        assertNotNull(extension)
        assertEquals(true, extension.enabled)
        assertEquals("_", extension.separator)
        assertEquals(null, extension.baseName)
        assertEquals(null, extension.pattern)
        assertEquals("", extension.prefix)
        assertEquals("", extension.suffix)
        assertEquals(true, extension.includeBaseName)
        assertEquals(true, extension.includeFlavor)
        assertEquals(true, extension.includeBuildType)
        assertEquals(true, extension.includeVersionName)
        assertEquals(false, extension.includeVersionCode)
        assertEquals(false, extension.includeDate)
        assertEquals(false, extension.includeGitSha)
        assertEquals(CaseFormat.PRESERVE, extension.caseFormat)
    }

    private fun createContext(
        baseName: String = "GatePay",
        projectName: String = "app",
        rootProjectName: String = "GatePayProject",
        moduleName: String = "app",
        flavorName: String? = "urovoProd",
        buildType: String? = "debug",
        versionName: String? = "1.0.0",
        versionCode: Int? = 10,
        date: String = "20260929",
        gitSha: String? = "a1b2c3d",
        variantName: String = "urovoProdDebug"
    ): VariantContext {
        return VariantContext(
            baseName = baseName,
            projectName = projectName,
            rootProjectName = rootProjectName,
            moduleName = moduleName,
            flavorName = flavorName,
            buildType = buildType,
            versionName = versionName,
            versionCode = versionCode,
            date = date,
            gitSha = gitSha,
            variantName = variantName
        )
    }

    @Test
    fun `default formatting behaves backward-compatible with version 1_4_0`() {
        val extension = ApkNamerExtension()
        val context = createContext(baseName = "MyCoolApp", flavorName = "free", buildType = "release", versionName = "1.2.3")

        val result = ApkNameFormatter.format(context, extension)
        assertEquals("MyCoolApp_free_release_1.2.3.apk", result)
    }

    @Test
    fun `default formatting without flavor`() {
        val extension = ApkNamerExtension()
        val context = createContext(baseName = "MyCoolApp", flavorName = null, buildType = "debug", versionName = "2.0.0")

        val result = ApkNameFormatter.format(context, extension)
        assertEquals("MyCoolApp_debug_2.0.0.apk", result)
    }

    @Test
    fun `custom baseName is used`() {
        val extension = ApkNamerExtension().apply {
            baseName = "GatePayCustom"
        }
        val context = createContext(baseName = "GatePayCustom")

        val result = ApkNameFormatter.format(context, extension)
        assertEquals("GatePayCustom_urovoProd_debug_1.0.0.apk", result)
    }

    @Test
    fun `pattern token replacement works with all tokens`() {
        val extension = ApkNamerExtension().apply {
            pattern = "{baseName}-{flavor}-{buildType}-v{versionName}-c{versionCode}-{date}-{gitSha}"
        }
        val context = createContext()

        val result = ApkNameFormatter.format(context, extension)
        assertEquals("GatePay-urovoProd-debug-v1.0.0-c10-20260929-a1b2c3d.apk", result)
    }

    @Test
    fun `pattern cleans consecutive and boundary separators when flavor is null`() {
        val extension = ApkNamerExtension().apply {
            separator = "_"
            pattern = "{baseName}_{flavor}_{buildType}_{versionName}"
        }
        val context = createContext(flavorName = null)

        val result = ApkNameFormatter.format(context, extension)
        assertEquals("GatePay_debug_1.0.0.apk", result)
    }

    @Test
    fun `includeVersionCode date and gitSha flags in default format`() {
        val extension = ApkNamerExtension().apply {
            includeVersionCode = true
            includeDate = true
            includeGitSha = true
        }
        val context = createContext()

        val result = ApkNameFormatter.format(context, extension)
        assertEquals("GatePay_urovoProd_debug_1.0.0_10_20260929_a1b2c3d.apk", result)
    }

    @Test
    fun `prefix and suffix are applied correctly`() {
        val extension = ApkNamerExtension().apply {
            prefix = "Release_"
            suffix = "_final"
        }
        val context = createContext()

        val result = ApkNameFormatter.format(context, extension)
        assertEquals("Release_GatePay_urovoProd_debug_1.0.0_final.apk", result)
    }

    @Test
    fun `case formatting converts to lowercase and kebab case`() {
        val extensionLower = ApkNamerExtension().apply {
            caseFormat = CaseFormat.LOWERCASE
        }
        val context = createContext()
        assertEquals("gatepay_urovoprod_debug_1.0.0.apk", ApkNameFormatter.format(context, extensionLower))

        val extensionKebab = ApkNamerExtension().apply {
            separator = "-"
            caseFormat = CaseFormat.KEBAB_CASE
        }
        assertEquals("gate-pay-urovo-prod-debug-1.0.0.apk", ApkNameFormatter.format(context, extensionKebab))
    }

    @Test
    fun `customNameResolver lambda completely overrides naming logic`() {
        val extension = ApkNamerExtension()
        extension.outputFileName { ctx ->
            "CustomApp_${ctx.flavorName}_${ctx.versionName}_QA"
        }
        val context = createContext()

        val result = ApkNameFormatter.format(context, extension)
        assertEquals("CustomApp_urovoProd_1.0.0_QA.apk", result)
    }

    @Test
    fun `case formatting converts to snake case and uppercase`() {
        val extensionUpper = ApkNamerExtension().apply {
            caseFormat = CaseFormat.UPPERCASE
        }
        val context = createContext()
        assertEquals("GATEPAY_UROVOPROD_DEBUG_1.0.0.apk", ApkNameFormatter.format(context, extensionUpper))

        val extensionSnake = ApkNamerExtension().apply {
            separator = "_"
            caseFormat = CaseFormat.SNAKE_CASE
        }
        assertEquals("gate_pay_urovo_prod_debug_1.0.0.apk", ApkNameFormatter.format(context, extensionSnake))
    }

    @Test
    fun `customNameResolver with existing apk extension does not duplicate it`() {
        val extension = ApkNamerExtension()
        extension.outputFileName { "CustomOutput.apk" }
        val context = createContext()

        val result = ApkNameFormatter.format(context, extension)
        assertEquals("CustomOutput.apk", result)
    }

    @Test
    fun `pattern with moduleName and variantName`() {
        val extension = ApkNamerExtension().apply {
            pattern = "{moduleName}_{variantName}"
        }
        val context = createContext(moduleName = "payment", variantName = "paxLiveRelease")

        val result = ApkNameFormatter.format(context, extension)
        assertEquals("payment_paxLiveRelease.apk", result)
    }
}

