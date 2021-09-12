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

object Publish {
    var ciPropValue = System.getProperty("aws.crt.ci")
    var isCI = ciPropValue != null && java.lang.Boolean.valueOf(ciPropValue)
    var clientId = "test-" + UUID.randomUUID().toString()
    var rootCaPath: String = "/workspace/cert/AmazonRootCA1.pem"
    var certPath: String = "/workspace/cert/65556c5c050dc1f2de95c9e6ce21c9b8b77e8738d15411db00c30784e496712c-certificate.pem.crt"
    var keyPath: String = "/workspace/cert/65556c5c050dc1f2de95c9e6ce21c9b8b77e8738d15411db00c30784e496712c-private.pem.key"
    var endpoint: String = "a2hasg13ybb0qa-ats.iot.ap-northeast-1.amazonaws.com"
    var topic = "topic"
    var messagesToPublish = 10
    var message = "aa"


    /*
     * When called during a CI run, throw an exception that will escape and fail the exec:java task
     * When called otherwise, print what went wrong (if anything) and just continue (return from main)
     */
    fun onApplicationFailure(cause: Throwable?) {
        if (isCI) {
            throw RuntimeException("BasicPubSub execution failure", cause)
        } else if (cause != null) {
            println("Exception encountered: $cause")
        }
    }
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            EventLoopGroup(1).use { eventLoopGroup -> HostResolver(eventLoopGroup).use { resolver ->
                ClientBootstrap(eventLoopGroup, resolver).use { clientBootstrap ->
                    AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(certPath, keyPath).use { builder ->
                        if (rootCaPath != null) builder.withCertificateAuthorityFromPath(null, rootCaPath)
                        builder.withBootstrap(clientBootstrap)
                                .withClientId(clientId)
                                .withEndpoint(endpoint)
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
                            while (count++ < messagesToPublish) {
                                val published: CompletableFuture<Int> = connection.publish(MqttMessage(topic, message.toByteArray(), QualityOfService.AT_LEAST_ONCE, false))
                                published.get()
                                println("$message[$count] to the $topic")
                                Thread.sleep(1000)
                            }
                            val disconnected: CompletableFuture<Void> = connection.disconnect()
                            disconnected.get()
                        }
                    }
                }
            }}
        } catch (ex: CrtRuntimeException) {
            onApplicationFailure(ex)
        } catch (ex: InterruptedException) {
            onApplicationFailure(ex)
        } catch (ex: ExecutionException) {
            onApplicationFailure(ex)
        }
        CrtResource.waitForNoResources()
        println("Complete!")
    }
}