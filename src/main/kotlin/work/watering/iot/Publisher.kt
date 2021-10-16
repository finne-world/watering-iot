package work.watering.iot

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import software.amazon.awssdk.crt.mqtt.MqttMessage
import software.amazon.awssdk.crt.mqtt.QualityOfService
import java.lang.IllegalStateException

class Publisher(
    private val topic: String,
){
    private val mqttClient: MqttClient

    init{
        MqttClient.Builder(
            clientId = "publisher-deviceID",
            endpoint = "a2hasg13ybb0qa-ats.iot.ap-northeast-1.amazonaws.com",
            certPath = "/home/pi/IoT/cert/65556c5c050dc1f2de95c9e6ce21c9b8b77e8738d15411db00c30784e496712c-certificate.pem.crt",
            keyPath = "/home/pi/IoT/cert/65556c5c050dc1f2de95c9e6ce21c9b8b77e8738d15411db00c30784e496712c-private.pem.key",
            rootCaPath = "/home/pi/IoT/cert/AmazonRootCA1.pem"
        )
        .build()
        .also{
            this.mqttClient = it
        }
    }

    private fun isConnected(): Boolean {
        return this.mqttClient.isConnected
    }

    private fun publish(message: MqttMessage) {
        this.mqttClient.connect()

        if (this.isConnected().not()) {
            throw IllegalStateException("mqtt client connection is not connected.")
        }
        this.mqttClient.getConnection().publish(message).get()

        this.mqttClient.disconnect()
    }

    fun publish(
        content: Any,
        qos: QualityOfService = QualityOfService.AT_MOST_ONCE,
        retain: Boolean = false
    ) {
        this.publish(
            MqttMessage(
                this.topic,
                jacksonObjectMapper().writeValueAsString(content).toByteArray(),
                qos,
                retain
            )
        )
    }
}
