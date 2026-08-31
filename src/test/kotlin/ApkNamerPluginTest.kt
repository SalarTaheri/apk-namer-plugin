import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ApkNamerPluginTest {

    @Test
    fun `plugin registers extension successfully`() {
        val project = ProjectBuilder.builder().withName("MyTestApp").build()
        project.pluginManager.apply("ir.miranmahaleh.salar.apk-namer")

        val extension = project.extensions.findByName("apkNamer") as? ApkNamerExtension
        assertNotNull(extension)
        assertEquals(true, extension.enabled)
        assertEquals("_", extension.separator)
    }

    @Test
    fun `apk name formatting logic joins rootProject name flavor buildType and versionName`() {
        val rootProjectName = "MyCoolApp"
        val sep = "_"
        val flavorName: String? = "free"
        val buildType: String? = "release"
        val versionName = "1.2.3"

        val components = listOfNotNull(
            rootProjectName,
            flavorName,
            buildType,
            versionName
        )
        val apkName = components.joinToString(separator = sep) + ".apk"

        assertEquals("MyCoolApp_free_release_1.2.3.apk", apkName)
    }

    @Test
    fun `apk name formatting logic without flavor`() {
        val rootProjectName = "MyCoolApp"
        val sep = "_"
        val flavorName: String? = null
        val buildType: String? = "debug"
        val versionName = "2.0.0"

        val components = listOfNotNull(
            rootProjectName,
            flavorName,
            buildType,
            versionName
        )
        val apkName = components.joinToString(separator = sep) + ".apk"

        assertEquals("MyCoolApp_debug_2.0.0.apk", apkName)
    }
}
