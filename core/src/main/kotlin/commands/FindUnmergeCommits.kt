package org.example.commands

import org.example.Branch
import java.nio.file.Path

internal class FindUnmergeCommits(private val workingDirectory: Path) {

    private fun execute(target: Branch, source: Branch): FindUnmergeCommitsResponse {
        val command = OsCommand("git rev-list ${source.name} ^${target.name}", workingDirectory).executeOrThrow()
        return FindUnmergeCommitsResponse(command.response)
    }

    fun commitMessages(target: Branch, source: Branch): List<GitLogCommandResponse> {
        val resp = this.execute(target, source)
        return resp.getHashes().map {
            LogCommand(workingDirectory, it).execute()
        }
    }
}

internal class FindUnmergeCommitsResponse(private val response: List<String>) {
    fun getHashes(): List<String>  = this.response
}