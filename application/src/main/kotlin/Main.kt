package org.example

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.boolean
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.path
import java.nio.file.Path


class YesOrNoPrompt(promptName: String) : CliktCommand() {
    val answerer by option().boolean().default(true).prompt(promptName)
    override fun run() {
    }
}

class MultipleChoice(choices: Map<String, String>, prompt: String) : CliktCommand() {
    val choice by option().choice(choices, ignoreCase = true).prompt(prompt)

    override fun run() {
    }

    companion object {
        fun build(choices: List<UnmergeBranch>, prefix: String): String {
            val map = choices
                .map { it.from }
                .sorted()
                .withIndex()
                .associate { (index, value) -> (index + 1).toString() to value }

            val mapStr = map.entries.joinToString { (index, value) -> "$index: $value" }

            val multipleChoices = MultipleChoice(map, "$mapStr\n${prefix}")
            multipleChoices.main(listOf())
            return multipleChoices.choice
        }
    }
}

class CommandLineApp : CliktCommand() {
    private val releaseBranch: String by option().default("main").help("Release branch name")
    private val developBranch: String by option().default("develop").help("Develop branch name")
    private val workingPath by option().path().default(Path.of(".")).help("Repository path")
    private val showTickets by option().flag(default = true).help("Show ticket jira from commits")
    private val showCommits by option().flag(default = true).help("Show commits")

    override fun run() {
        val gitSettings = GitSettings(workingPath, releaseBranch, developBranch)
        val gitInfo = GitInfo(gitSettings)

        echo("Starting working in Git repository: ${gitSettings.path.toAbsolutePath()}")

        val unmergeBranches = gitInfo.findUnmergeBranches()

        if (showTickets || showCommits) {
            val branchFrom = MultipleChoice.build(unmergeBranches.filter { !gitSettings.isReleaseBranch(it.target) },
                prefix="Unmerge branches")

            if (showCommits) {
                echo("Commits: ")
                echo(gitInfo.findWorkingCommits(branchFrom).withIndex().joinToString("\n"){ (index, value) -> "${index + 1}: $value" })
            }

            if (showTickets) {
                echo("Detected tickets")
                echo(gitInfo.findWorkingTickets(branchFrom).map { it.name })
            }
        }

        unmergeBranches.forEach {
            val prompt = YesOrNoPrompt("Would you like to merge branch ${it.from} -> ${it.target} ")
            prompt.main(listOf())
            if (prompt.answerer) {
                gitInfo.mergeBranches(listOf(it))
            }
        }
    }
}

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            CommandLineApp().main(args)
        }
    }
}