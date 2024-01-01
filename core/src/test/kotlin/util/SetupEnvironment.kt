package util

import java.nio.file.Path



enum class SetupEnvironment(private val scriptName: String) {
    SETUP_TEST1("setup-test1.sh");

    fun execute() {
        val appPath = ApplicationPath()

        val path = Path.of(
            appPath.testDirectory().toAbsolutePath().toString(), scriptName)
        return executeBashScript(path.toAbsolutePath())
    }

    private fun executeBashScript(scriptPath: Path) {
        try {
            val processBuilder = ProcessBuilder("bash", scriptPath.toString())
            val process = processBuilder.start()
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                throw RuntimeException("Script execution failed with exit code: $exitCode")
            }
        } catch (e: Exception) {
            throw RuntimeException("Error executing script", e)
        }
    }
}
