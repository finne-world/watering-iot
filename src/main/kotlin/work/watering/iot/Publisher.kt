package work.watering.iot

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import software.amazon.awssdk.crt.mqtt.MqttMessage
import software.amazon.awssdk.crt.mqtt.QualityOfService
import java.lang.IllegalStateException

class Publisher(
    topic: String,
    mqttClient: MqttClient
): AWSIoTEndpoint(topic, mqttClient) {
    fun publish(message: MqttMessage) {
        if (this.isConnected().not()) {
            throw IllegalStateException("mqtt client connection is not connected.")
        }
        this.mqttClient.getConnection().publish(message).get()
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
