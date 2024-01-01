package org.example

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.math.log


private val logger = KotlinLogging.logger {}
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val command = OsCommand("pwd")

    val response = command.execute()


    logger.info { response }


}