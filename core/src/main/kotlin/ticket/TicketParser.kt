package org.example.ticket

interface TicketParser {
    fun findTickets(message: String): List<String>
}

class JiraTicketParser : TicketParser {
    companion object private var jiraPattern = Regex("([A-Z]{2,}-\\d+)")

    override fun findTickets(message: String): List<String> {
        return jiraPattern.findAll(message)
            .map { it.value }
            .toList()
    }
}