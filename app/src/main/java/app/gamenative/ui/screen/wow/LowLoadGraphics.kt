package app.gamenative.ui.screen.wow

import java.io.File

/** Explicit opt-in: override existing configs as well as new installs, without touching login or controls. */
internal fun applyLowLoadGraphics(config: File) {
    val original = config.readText()
    val values = linkedMapOf(
        "useMaxFPS" to "1",
        "maxFPS" to "30",
        "useMaxFPSBk" to "1",
        "maxFPSBk" to "8",
        "useTargetFPS" to "0",
        "RenderScale" to "0.75",
        "ResampleQuality" to "0",
        "MSAAQuality" to "0",
        "MSAAAlphaTest" to "0",
        "ffxAntiAliasingMode" to "0",
        "worldPreloadHighResTextures" to "0",
    )
    // Mirror both normal and raid settings so entering a raid cannot restore expensive effects.
    val scene = linkedMapOf(
        "graphicsQuality" to "0",
        "graphicsTextureResolution" to "0",
        "worldBaseMip" to "2",
        "terrainMipLevel" to "1",
        "componentTextureLevel" to "1",
        "graphicsViewDistance" to "0",
        "graphicsEnvironmentDetail" to "0",
        "graphicsGroundClutter" to "0",
        "farclip" to "400",
        "horizonClip" to "400",
        "shadowMode" to "0",
        "graphicsShadowQuality" to "0",
        "graphicsLiquidDetail" to "0",
        "waterDetail" to "0",
        "reflectionMode" to "0",
        "refraction" to "0",
        "sunShafts" to "0",
        "SSAO" to "0",
        "graphicsSSAO" to "0",
        "graphicsDepthEffects" to "0",
        "DepthBasedOpacity" to "0",
        "graphicsComputeEffects" to "0",
        "graphicsParticleDensity" to "0",
        "particleDensity" to "10",
        "groundEffectDensity" to "16",
        "groundEffectDist" to "40",
        "textureFilteringMode" to "0",
        // Keep ground combat indicators visible even in the low-load profile.
        "projectedTextures" to "1",
        "graphicsProjectedTextures" to "1",
    )
    values.putAll(scene)
    scene.forEach { (key, value) -> values["RAID$key"] = value }
    val keys = values.keys.map { it.lowercase(java.util.Locale.ROOT) }.toSet()
    val assignment = Regex("^\\s*SET\\s+(\\S+)\\s+.*$", RegexOption.IGNORE_CASE)
    val kept = original.lines().filterNot { line ->
        val key = assignment.matchEntire(line)?.groupValues?.get(1)?.lowercase(java.util.Locale.ROOT)
        key != null && key in keys
    }.dropLastWhile { it.isEmpty() }
    val updated = (kept + values.map { (key, value) -> "SET $key \"$value\"" }).joinToString("\n") + "\n"
    if (updated == original) return
    // Never replace the original backup on subsequent launches, even if the game rewrites its config.
    val backup = File(config.parentFile, "${config.name}.before-low-load")
    if (!backup.exists()) config.copyTo(backup)
    config.writeText(updated)
}
