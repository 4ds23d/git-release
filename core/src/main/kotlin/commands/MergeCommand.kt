package org.example.commands

import org.example.UnmergeBranch
import java.nio.file.Path

internal class MergeCommand(private val workingDirectory: Path) {

    fun execute(branch: UnmergeBranch) {
        OsCommand("git checkout ${branch.target}", workingDirectory).executeOrThrow()
        OsCommand("git merge --no-edit --no-ff ${branch.from}", workingDirectory).executeOrThrow()
    }
}
