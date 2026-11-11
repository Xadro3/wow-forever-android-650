package app.gamenative.ui.screen.wow

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class LowLoadGraphicsTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun replacesMixedCaseAndDuplicateSettings_preservesUnrelatedLinesAndOriginalBackup() {
        val config = tempFolder.newFile("Config.wtf")
        val original = "SET portal \"EU\"\nSET GamePadEnable \"1\"\nSET RenderScale \"2\"\n  set\trenderscale\t\"1\"\nSET RAIDworldBaseMip \"0\"\n"
        config.writeText(original)

        applyLowLoadGraphics(config)

        val lines = config.readLines()
        assertTrue(lines.contains("SET portal \"EU\""))
        assertTrue(lines.contains("SET GamePadEnable \"1\""))
        assertEquals(listOf("SET RenderScale \"0.75\""), lines.filter { it.contains("renderscale", ignoreCase = true) })
        assertTrue(lines.contains("SET RAIDworldBaseMip \"2\""))
        assertTrue(lines.contains("SET useMaxFPS \"1\""))
        assertTrue(lines.contains("SET maxFPS \"30\""))
        assertTrue(lines.contains("SET projectedTextures \"1\""))
        val backup = File(config.parentFile, "Config.wtf.before-low-load")
        assertEquals(original, backup.readText())

        val applied = config.readText()
        applyLowLoadGraphics(config)
        assertEquals(applied, config.readText())
        config.appendText("SET maxFPS \"200\"\n")
        applyLowLoadGraphics(config)
        assertEquals(applied, config.readText())
        assertEquals(original, backup.readText())
    }
}
