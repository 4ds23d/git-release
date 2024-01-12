package org.example.ticket

import org.example.Ticket

interface TicketParser {
    fun findTickets(message: String): List<Ticket>
}

class JiraTicketParser : TicketParser {
    companion object private var jiraPattern = Regex("([A-Z]{2,}-\\d+)")

    override fun findTickets(message: String): List<Ticket> {
        return jiraPattern.findAll(message)
            .map { Ticket(it.value) }
            .toList()
    }
}