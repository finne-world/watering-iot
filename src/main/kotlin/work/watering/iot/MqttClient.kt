package work.watering.iot

import software.amazon.awssdk.crt.io.ClientBootstrap
import software.amazon.awssdk.crt.io.EventLoopGroup
import software.amazon.awssdk.crt.io.HostResolver
import software.amazon.awssdk.crt.mqtt.MqttClientConnection
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder

class MqttClient(
    private val eventLoopGroup: EventLoopGroup,
    private val hostResolver: HostResolver,
    private val clientBootstrap: ClientBootstrap,
    private val mqttClientConnection: MqttClientConnection
): AutoCloseable {
    var isConnected: Boolean = false

    fun connect() {
        this.mqttClientConnection.connect().get().also { this.isConnected = true }
    }

    fun disconnect() {
        this.mqttClientConnection.disconnect().get().also { this.isConnected = false }
    }

    fun getConnection(): MqttClientConnection {
        return this.mqttClientConnection
    }

    override fun close() {
        this.eventLoopGroup.close()
        this.hostResolver.close()
        this.clientBootstrap.close()
        this.mqttClientConnection.close()
    }

    //TODO: 適当すぎる
    class Builder(
        private val clientId: String,
        private val endpoint: String,
        private val certPath: String,
        private val keyPath: String,
        private val rootCaPath: String
    ) {
        private var cleanSession: Boolean = true
        private var protocolOperationTimeoutMs: Int = 60000
        private var threadsNum: Int = 5

        fun build(): MqttClient {
            val eventLoopGroup: EventLoopGroup = EventLoopGroup(this.threadsNum)
            val hostResolver: HostResolver = HostResolver(eventLoopGroup)
            val clientBootstrap: ClientBootstrap = ClientBootstrap(eventLoopGroup, hostResolver)
            AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(certPath, keyPath).also {
                it.withBootstrap(clientBootstrap)
                it.withClientId(this.clientId)
                it.withEndpoint(this.endpoint)
                it.withCleanSession(this.cleanSession)
                it.withProtocolOperationTimeoutMs(this.protocolOperationTimeoutMs)
                it.withCertificateAuthorityFromPath(null, rootCaPath)
            }
            .build()
            .also {
                return MqttClient(
                    eventLoopGroup = eventLoopGroup,
                    hostResolver = hostResolver,
                    clientBootstrap = clientBootstrap,
                    mqttClientConnection = it
                )
            }
        }
    }
}