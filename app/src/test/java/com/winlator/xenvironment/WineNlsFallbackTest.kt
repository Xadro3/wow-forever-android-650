package com.winlator.xenvironment

import java.io.File
import java.io.IOException
import java.nio.file.Files
import org.junit.Assert.assertArrayEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class WineNlsFallbackTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun freshRuntime_getsNlsFromSelectedWine() {
        val wine = temporaryFolder.newFolder("proton")
        val root = temporaryFolder.newFolder("imagefs")
        val source = File(wine, "share/wine/nls/l_intl.nls")
        Files.createDirectories(source.toPath().parent)
        source.writeBytes(byteArrayOf(1, 0, 2, 0))

        ImageFsInstaller.ensureWineNlsFallback(wine, root)

        assertArrayEquals(source.readBytes(), File(root, "usr/share/wine/nls/l_intl.nls").readBytes())
    }

    @Test
    fun runtimeUpgrade_replacesOldNlsWithSelectedVersion() {
        val wine = temporaryFolder.newFolder("proton")
        val root = temporaryFolder.newFolder("imagefs")
        val source = File(wine, "share/wine/nls/l_intl.nls")
        Files.createDirectories(source.toPath().parent)
        source.writeBytes(byteArrayOf(1, 0))
        ImageFsInstaller.ensureWineNlsFallback(wine, root)
        source.writeBytes(byteArrayOf(2, 0, 3, 0))

        ImageFsInstaller.ensureWineNlsFallback(wine, root)

        assertArrayEquals(source.readBytes(), File(root, "usr/share/wine/nls/l_intl.nls").readBytes())
    }

    @Test(expected = IOException::class)
    fun missingRuntimeNls_failsBeforeStartingWine() {
        ImageFsInstaller.ensureWineNlsFallback(temporaryFolder.newFolder("proton"), temporaryFolder.newFolder("imagefs"))
    }
}
