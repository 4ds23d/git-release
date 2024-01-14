package org.example

import org.example.commands.*
import org.example.commands.BranchFilter
import org.example.commands.FindBranchesToMerge
import org.example.commands.FindUnmergeCommits
import org.example.commands.MergeCommand
import org.example.ticket.JiraTicketParser

class GitInfo(private val settings: GitSettings) {

    fun hasUncommitedChanges(): Boolean {
        val command = StatusCommand(settings.path)
        return command.hasUncommitedChanges()
    }

    fun pullBranch(branch: Branch) {
        OsCommand("git pull origin ${branch}", settings.path).executeOrThrow()
    }

    fun findWorkingTickets(from: Branch? = null): List<Ticket> {
        val command = FindUnmergeCommits(settings.path)
        return command.commitMessages(settings.releaseBranch, from ?: settings.developBranch)
            .map { it.messageLog }
            .flatMap { JiraTicketParser().findTickets(it) }
            .distinct()
    }

    fun findWorkingCommits(from: Branch? = null): List<Commit> {
        val command = FindUnmergeCommits(settings.path)
        return command.commitMessages(settings.releaseBranch, from ?: settings.developBranch)
            .map { Commit(it.messageLog) }
    }

    fun findUnmergeBranches(): List<UnmergeBranch> {
        val command = FindBranchesToMerge(settings.path)
        val unmergeBranchToRelease = command.execute(settings.releaseBranch)
            .filter {
                BranchFilter.HOTFIX
                    .or(BranchFilter.RELEASE)
                    .test(it)
            }
            .map { UnmergeBranch(settings.releaseBranch, Branch(it)) }

        val unmergeBranchToDevelop = command.execute(settings.developBranch)
            .filter {
                BranchFilter.HOTFIX
                    .or(BranchFilter.RELEASE)
                    .or { b -> b.equals(settings.releaseBranch) }
                    .test(it)
            }
            .map { UnmergeBranch(settings.developBranch, Branch(it)) }

        return unmergeBranchToRelease + unmergeBranchToDevelop
    }

    fun mergeBranches(branches: List<UnmergeBranch>): List<Branch> {
        val mergedBranches = branches.map {
            MergeCommand(settings.path).execute(it)
            it.target
        }

        MergeCommand(settings.path).execute(
            UnmergeBranch(
                settings.developBranch,
                settings.releaseBranch
            )
        )

        return mergedBranches + settings.developBranch
    }

}

data class Commit(val name: String) {
    override fun toString(): String = name
}

data class Ticket(val name: String) {
    override fun toString(): String = name
}

data class Branch(val name: String) {
    override fun toString(): String = name
}

data class UnmergeBranch(val target: Branch, val from: Branch)