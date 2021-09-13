package work.watering.iot

import work.watering.iot.message.SampleMessage
import java.time.LocalDateTime
import java.util.UUID

fun main() {
    Main().also {
        it.start()
    }
}

const val topic: String = "topic"

class Main {
    val mqttClient: MqttClient
    val publisher: Publisher
    val subscriber: Subscriber

    init {
        MqttClient.Builder(
            clientId = "client_id",
            endpoint = "a2hasg13ybb0qa-ats.iot.ap-northeast-1.amazonaws.com",
            certPath = "cert/65556c5c050dc1f2de95c9e6ce21c9b8b77e8738d15411db00c30784e496712c-certificate.pem.crt",
            keyPath = "cert/65556c5c050dc1f2de95c9e6ce21c9b8b77e8738d15411db00c30784e496712c-private.pem.key"
        )
        .build()
        .also {
            this.mqttClient = it
            this.publisher = Publisher(topic, it)
            this.subscriber = Subscriber(topic, it)
        }
    }

    fun start() {
        this.publisher.publish(
            SampleMessage(
                deviceSerial = UUID.randomUUID(),
                amount = 1000,
                timestamp = LocalDateTime.now()
            )
        )
    }
}
