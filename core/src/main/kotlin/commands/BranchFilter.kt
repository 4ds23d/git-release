package org.example.commands

import java.util.function.Predicate

internal enum class BranchFilter(private val regex: Regex) : Predicate<String> {
    RELEASE("""^release/.*""".toRegex()),
    HOTFIX("""^hotfix/.*""".toRegex());

    override fun test(t: String): Boolean = regex.matches(t)
}