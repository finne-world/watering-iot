package main

import software.amazon.awssdk.crt.CrtResource
import software.amazon.awssdk.crt.CrtRuntimeException
import software.amazon.awssdk.crt.io.ClientBootstrap
import software.amazon.awssdk.crt.io.EventLoopGroup
import software.amazon.awssdk.crt.io.HostResolver
import software.amazon.awssdk.crt.mqtt.MqttMessage
import software.amazon.awssdk.crt.mqtt.QualityOfService
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException

object Main {
    val configuration: ConfigureData = ConfigureData()
    var ciPropValue = System.getProperty("aws.crt.ci")
    var isCI = ciPropValue != null && java.lang.Boolean.valueOf(ciPropValue)
    val pubClientID: String = "publish-" + UUID.randomUUID().toString()
    val subClientID: String = "subscribe-" + UUID.randomUUID().toString()
    val rootCaPath: String = configuration.rootCaPath
    var certPath: String = configuration.certPath
    var keyPath: String = configuration.keyPath
    var endpoint: String = configuration.endpoint
    var topic: String = configuration.topic
    var port: Int = configuration.port
    var messagesToPublish = 10

    fun onApplicationFailure(cause: Throwable?) {
        if (Publish.isCI) {
            throw RuntimeException("BasicPubSub execution failure", cause)
        } else if (cause != null) {
            println("Exception encountered: $cause")
        }
    }

    fun main(args: Array<String>) {
        try {
            EventLoopGroup(1).use { eventLoopGroup -> HostResolver(eventLoopGroup).use { resolver ->
                ClientBootstrap(eventLoopGroup, resolver).use { clientBootstrap ->
                    AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(Publish.certPath, Publish.keyPath).use { builder ->
                        if (rootCaPath != null) builder.withCertificateAuthorityFromPath(null, rootCaPath)
                        builder.withBootstrap(clientBootstrap)
                            .withClientId(Publish.clientId)
                            .withEndpoint(Publish.endpoint)
                            .withCleanSession(true)
                            .withProtocolOperationTimeoutMs(60000)

                        builder.build().use { connection ->
                            val connected: CompletableFuture<Boolean> = connection.connect()
                            try {
                                println("Connected to session!")
                            } catch (ex: Exception) {
                                throw RuntimeException("Exception occurred during connect", ex)
                            }
                            var count: Int = 0
                            while (count++ < Publish.messagesToPublish) {
                                val published: CompletableFuture<Int> = connection.publish(MqttMessage(Publish.topic, Publish.message.toByteArray(), QualityOfService.AT_LEAST_ONCE, false))
                                published.get()
                                println("${Publish.message}[$count] to the ${Publish.topic}")
                                Thread.sleep(1000)
                            }
                            val disconnected: CompletableFuture<Void> = connection.disconnect()
                            disconnected.get()
                        }
                    }
                }
            }}
        } catch (ex: CrtRuntimeException) {
            Publish.onApplicationFailure(ex)
        } catch (ex: InterruptedException) {
            Publish.onApplicationFailure(ex)
        } catch (ex: ExecutionException) {
            Publish.onApplicationFailure(ex)
        }
        CrtResource.waitForNoResources()
        println("Complete!")
    }
}
