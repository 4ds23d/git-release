package org.example.commands

import java.nio.file.Path

class FindBranchesToMerge(private val workingDirectory: Path) {

    fun execute(target: String): List<String> {
        val command = OsCommand("git branch --no-merged $target --format=%(refname:short)", workingDirectory).executeOrThrow()
        return command.response
    }
}
