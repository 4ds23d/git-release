package org.example.commands


import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path


internal class OsCommand(private var command: String,
                private var path: Path
) {
    private val logger = KotlinLogging.logger {}

    private fun execute(): OsCommandResponse {
        try {
            logger.trace { "Working directory $path" }
            logger.info { "Execute command $command"}

            val processBuilder = ProcessBuilder(this.command.split("\\s".toRegex()))
                .redirectErrorStream(true)

            path.let { processBuilder.directory(path.toFile()) }

            val process = processBuilder
                .start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?

            val response = mutableListOf<String>()

            while (reader.readLine().also { line = it } != null) {
                line.let { response.add(it!!) }
            }

            val exitCode = process.waitFor()

            val osResponse = OsCommandResponse(response, exitCode)

            logger.trace { "Response $osResponse"  }
            return osResponse
        } catch (e: Exception) {
            logger.error (e) { "Exception while executing [$command] $e"   }
            return OsCommandResponse(listOf(), -1)
        }
    }

    fun executeOrThrow(): OsCommandResponse {
        val response = execute()
        if (response.isSuccess()) {
            return response
        }
        throw OsCommandException(response)
    }
}

internal class OsCommandException(response: OsCommandResponse) : Exception(response.toString())

internal class OsCommandResponse(val response: List<String>,
                        private val statusCode: Int)  {

    fun isSuccess(): Boolean = statusCode == 0

    override fun toString(): String {
        return "Status: ${statusCode}\n $response"
    }
}