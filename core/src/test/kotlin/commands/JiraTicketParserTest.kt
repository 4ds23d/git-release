package commands


import org.assertj.core.api.Assertions.assertThat
import org.example.Ticket
import org.example.ticket.JiraTicketParser
import org.example.ticket.TicketParser
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class JiraTicketParserTest {

    private val parser : TicketParser = JiraTicketParser()

    @Test
    fun noJiraTicketInMessage() {
        // when
        val results = parser.findTickets("Merge develop into master")

        // then
        assertThat(results).hasSize(0)
    }

    @ParameterizedTest
    @CsvSource(value = [
        "ala ma kota GIN-123|GIN-123",
        "AR-123,BT-111ala ma kota GIN-123|AR-123,BT-111,GIN-123",
    ], delimiter = '|')
    fun properlyParse(input: String, expectedTickets: String) {
        // when
        val results = parser.findTickets(input)

        // then
        assertThat(results)
            .containsExactlyInAnyOrderElementsOf(expectedTickets.split(",").map { Ticket(it) })
    }
}