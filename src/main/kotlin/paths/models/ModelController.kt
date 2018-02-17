package paths.models

import java.time.LocalDateTime

class FlowController {

    fun getFlows(tags: List<String>, limit: Int): List<Flow> {
        return MOCK_FLOWS
    }


    private val MOCK_METADATA by lazy {
        Metadata(
                LocalDateTime.now(),
                UserSummary(1, "Joe"),
                LocalDateTime.now(),
                UserSummary(1, "Joe"),
                UserSummary(1, "Joe")
        )
    }
    private val MOCK_FLOWS by lazy {
        listOf(
                Flow(MOCK_METADATA, 1, "Learn Python in 12 days", "Lalal"),
                Flow(MOCK_METADATA, 2, "Koltin from 0 to 100", "Lalal")
        )
    }

    private val MOCK_ISLANDS by lazy {
        listOf(
                Island("Kotlin", Country("Russia", "RU")),
                Island("Stewart Island", Country("New Zealand", "NZ")),
                Island("Cockatoo Island", Country("Australia", "AU")),
                Island("Tasmania", Country("Australia", "AU"))
        )
    }
}