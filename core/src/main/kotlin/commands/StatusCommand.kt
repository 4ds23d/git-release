package org.example.commands

import java.nio.file.Path

class StatusCommand(private val workingDirectory: Path) {

    fun hasUncommitedChanges(): Boolean {
        val command = OsCommand("git status --porcelain", workingDirectory).executeOrThrow()
        return command.response.isNotEmpty()
    }
}