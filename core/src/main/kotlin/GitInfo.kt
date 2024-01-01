package org.example

import org.example.commands.BranchFilter
import org.example.commands.FindBranchesToMerge
import org.example.commands.MergeCommand
import org.example.commands.FindUnmergeCommits
import org.example.ticket.JiraTicketParser

class GitInfo(private val settings: GitSettings) {

    fun findWorkingTickets(from: String? = null): List<String> {
        val command = FindUnmergeCommits(settings.path)
        return command.commitMessages(settings.releaseBranch, from ?: settings.developBranch)
            .map { it.log }
            .flatMap { JiraTicketParser().findTickets(it) }
            .distinct()
    }

    fun findUnmergeBranches(): List<UnmergeBranch> {
        val command = FindBranchesToMerge(settings.path)
        val unmergeBranchToRelease = command.execute(settings.releaseBranch)
            .filter {
                BranchFilter.HOTFIX
                    .or(BranchFilter.RELEASE)
                    .test(it)
            }
            .map { UnmergeBranch(settings.releaseBranch, it) }

        val unmergeBranchToDevelop = command.execute(settings.developBranch)
            .filter {
                BranchFilter.HOTFIX
                    .or(BranchFilter.RELEASE)
                    .or { b -> b.equals(settings.releaseBranch) }
                    .test(it)
            }
            .map { UnmergeBranch(settings.developBranch, it) }

        return unmergeBranchToRelease + unmergeBranchToDevelop
    }

    fun mergeBranches(branches: List<UnmergeBranch>): List<Branch> {
        val mergedBranches = branches.map {
            MergeCommand(settings.path).execute(it)
            Branch(it.target)
        }

        MergeCommand(settings.path).execute(UnmergeBranch(settings.developBranch, settings.releaseBranch))

        return mergedBranches + Branch(settings.developBranch)
    }

}

data class Branch(val name: String);
data class UnmergeBranch(val target: String, val from: String)