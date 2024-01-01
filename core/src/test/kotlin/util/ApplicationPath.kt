package util

import java.nio.file.Path

class ApplicationPath {

    fun gitTest1(): Path {
        return Path.of(buildDirectory().toString(), "test1")
    }

    fun testDirectory(): Path {
        return Path.of(
            System.getProperty("user.dir"), "src", "test"
        )
    }

    private fun buildDirectory(): Path {
        return Path.of(
            System.getProperty("user.dir"), "build"
        )
    }
}