package com.asiradnan.periodictable

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ProjectStructureTest {

    @Test
    fun projectStructure_requiredDirectoriesExist() {
        // "user.dir" typically points to the 'app' module root when running Android unit tests
        val projectRoot = System.getProperty("user.dir")

        // Build the path to your source code package
        // Adjust "com/asiradnan/periodictable" if your package name is different
        val packagePath = "$projectRoot/src/main/java/com/asiradnan/periodictable"

        val dataDir = File("$packagePath/data")
        val componentsDir = File("$packagePath/components")
        val utilsDir = File("$packagePath/utils")

        // Assert Data Directory
        assertTrue(
            "CRITICAL: 'data' directory missing at ${dataDir.absolutePath}",
            dataDir.exists() && dataDir.isDirectory
        )

        // Assert Components Directory
        assertTrue(
            "CRITICAL: 'components' directory missing at ${componentsDir.absolutePath}",
            componentsDir.exists() && componentsDir.isDirectory
        )

        // Assert Utils Directory
        assertTrue(
            "CRITICAL: 'utils' directory missing at ${utilsDir.absolutePath}",
            utilsDir.exists() && utilsDir.isDirectory
        )
    }
}