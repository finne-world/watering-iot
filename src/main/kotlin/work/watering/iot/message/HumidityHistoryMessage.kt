package work.watering.iot.message

import java.util.UUID

data class HumidityHistoryMessage(
    val serial: UUID,
    val timestamp: String,
    val value: Float
)