package org.example

import java.nio.file.Path

class GitSettings(val path: Path,
                  val releaseBranch: String,
                  val developBranch: String) {

    fun isReleaseBranch(branch: String): Boolean {
        return releaseBranch == branch
    }
}