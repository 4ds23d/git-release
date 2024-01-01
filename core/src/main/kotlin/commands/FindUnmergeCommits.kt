package org.example.commands

import java.nio.file.Path

class FindUnmergeCommits(private val workingDirectory: Path) {

    private fun execute(target: String, source: String): FindUnmergeCommitsResponse {
        val command = OsCommand("git rev-list $source ^$target", workingDirectory).executeOrThrow()
        return FindUnmergeCommitsResponse(command.response)
    }

    fun commitMessages(target: String, source: String): List<GitLogCommandResponse> {
        val resp = this.execute(target, source)
        return resp.getHashes().map {
            LogCommand(workingDirectory, it).execute()
        }
    }
}

class FindUnmergeCommitsResponse(private val response: List<String>) {
    fun getHashes(): List<String>  = this.response
}