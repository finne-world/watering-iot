package work.watering.iot.message

import java.util.UUID

data class WateringHistoryMessage(
    val serial: UUID,
    val timestamp: String,
    val amount: Int
)
