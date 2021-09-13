package work.watering.iot

import software.amazon.awssdk.crt.mqtt.MqttMessage
import software.amazon.awssdk.crt.mqtt.QualityOfService
import java.lang.IllegalStateException

class Subscriber(
    topic: String,
    mqttClient: MqttClient
): AWSIoTEndpoint(topic, mqttClient) {
    fun subscribe(
        block: (MqttMessage) -> Unit,
        qos: QualityOfService = QualityOfService.AT_MOST_ONCE
    ) {
        if (this.isConnected().not()) {
            throw IllegalStateException("mqtt client is not connected.")
        }
        this.mqttClient.getConnection().subscribe(
            this.topic,
            qos,
            block
        )
        .also {
            it.get()
        }
    }
}
