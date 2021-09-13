package work.watering.iot.message

import java.util.UUID

data class SampleMessage(
    val deviceSerial: UUID,
    val amount: Int,
    val timestamp: String
)
