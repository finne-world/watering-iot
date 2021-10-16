package work.watering.iot

import software.amazon.awssdk.crt.mqtt.MqttMessage
import software.amazon.awssdk.crt.mqtt.QualityOfService
import java.lang.IllegalStateException

class Subscriber(
    private val topic: String,
){
    private val mqttClient: MqttClient

    init{
        MqttClient.Builder(
            clientId = "subscriber-deviceID",
            endpoint = "a2hasg13ybb0qa-ats.iot.ap-northeast-1.amazonaws.com",
            certPath = "/home/pi/IoT/cert/65556c5c050dc1f2de95c9e6ce21c9b8b77e8738d15411db00c30784e496712c-certificate.pem.crt",
            keyPath = "/home/pi/IoT/cert/65556c5c050dc1f2de95c9e6ce21c9b8b77e8738d15411db00c30784e496712c-private.pem.key",
            rootCaPath = "/home/pi/IoT/cert/AmazonRootCA1.pem"
        )
        .build()
        .also{
            this.mqttClient = it
        }
        this.mqttClient.connect()
    }

    private fun isConnected(): Boolean {
        return this.mqttClient.isConnected
    }

    fun subscribe(
        qos: QualityOfService = QualityOfService.AT_MOST_ONCE,
        block: (MqttMessage) -> Unit,
    ) {
        if (isConnected().not()) {
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
