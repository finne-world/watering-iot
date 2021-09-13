package work.watering.iot

open class AWSIoTEndpoint(
    protected val topic: String,
    protected val mqttClient: MqttClient
) {
    fun isConnected(): Boolean {
        return this.mqttClient.isConnected
    }
}
