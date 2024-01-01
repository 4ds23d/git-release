package org.example

import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.BufferedReader
import java.io.InputStreamReader

private val logger = KotlinLogging.logger {}

class OsCommand(private var command: String) {

    fun execute(): OsCommandResponse {
        try {
            val process = ProcessBuilder(this.command.split("\\s".toRegex()))
                .redirectErrorStream(true)
                .start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?

            var response = mutableListOf<String>();

            while (reader.readLine().also { line = it } != null) {
                line.let { response.add(it!!) }
            }

            val exitCode = process.waitFor()
            return OsCommandResponse(response, exitCode)
        } catch (e: Exception) {
            logger.error (e) { "Exception while executing [$command] $e"   }
            return OsCommandResponse(listOf(), -1)
        }
    }
}