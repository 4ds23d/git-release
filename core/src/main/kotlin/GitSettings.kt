package org.example

import java.nio.file.Path

class GitSettings(
    val path: Path,
    val releaseBranch: Branch,
    val developBranch: Branch,
    val synchronizeRepository: Boolean = true
) {

    fun isReleaseBranch(branch: Branch): Boolean {
        return releaseBranch == branch
    }
}