package org.example.commands

import org.example.Branch
import java.nio.file.Path

internal class FindBranchesToMerge(private val workingDirectory: Path) {

    fun execute(target: Branch): List<String> {
        val command = OsCommand("git branch --no-merged ${target.name} --format=%(refname:short)", workingDirectory).executeOrThrow()
        return command.response
    }
}
