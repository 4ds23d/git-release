package org.example.commands

import java.nio.file.Path

class LogCommand(private val workingDirectory: Path, private val hash: String) {
    fun execute(): GitLogCommandResponse {
        val logResponse = OsCommand("git log -n 1 --pretty=format:%B $hash", this.workingDirectory).executeOrThrow()
        return GitLogCommandResponse(logResponse.response.joinToString(separator = "\n"))
    }
}

class GitLogCommandResponse(val log: String) {

    override fun toString(): String {
        return log
    }
}