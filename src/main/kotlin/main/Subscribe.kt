package main

/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

import software.amazon.awssdk.crt.CRT
import software.amazon.awssdk.crt.CrtResource
import software.amazon.awssdk.crt.CrtRuntimeException
import software.amazon.awssdk.crt.io.*
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents
import software.amazon.awssdk.crt.mqtt.MqttMessage
import software.amazon.awssdk.crt.mqtt.QualityOfService
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.function.Consumer

object Subscribe {
    var ciPropValue = System.getProperty("aws.crt.ci")
    var isCI = ciPropValue != null && java.lang.Boolean.valueOf(ciPropValue)
    var clientId = "test-" + UUID.randomUUID().toString()
    var rootCaPath: String = "/workspace/cert/AmazonRootCA1.pem"
    var certPath: String = "/workspace/cert/65556c5c050dc1f2de95c9e6ce21c9b8b77e8738d15411db00c30784e496712c-certificate.pem.crt"
    var keyPath: String = "/workspace/cert/65556c5c050dc1f2de95c9e6ce21c9b8b77e8738d15411db00c30784e496712c-private.pem.key"
    var endpoint: String = "a2hasg13ybb0qa-ats.iot.ap-northeast-1.amazonaws.com"
    var topic = "topic"
    var port = 8883
    var messagesToPublish = 10
    var message = "z"
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
        val callbacks: MqttClientConnectionEvents = object : MqttClientConnectionEvents {
            override fun onConnectionInterrupted(errorCode: Int) {
                if (errorCode != 0) {
                    println("Connection interrupted: " + errorCode + ": " + CRT.awsErrorString(errorCode))
                }
            }
            override fun onConnectionResumed(sessionPresent: Boolean) {
                println("Connection resumed: " + (if (sessionPresent) "existing session" else "clean session"))
            }
        }
        try {
            EventLoopGroup(1).use { eventLoopGroup -> HostResolver(eventLoopGroup).use { resolver ->
                ClientBootstrap(eventLoopGroup, resolver).use { clientBootstrap ->
                    AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(certPath, keyPath).use { builder ->
                        if (rootCaPath != null) builder.withCertificateAuthorityFromPath(null, rootCaPath)
                        builder.withBootstrap(clientBootstrap)
                                .withConnectionEventCallbacks(callbacks)
                                .withClientId(clientId)
                                .withEndpoint(endpoint)
                                .withPort(port.toShort())
                                .withCleanSession(true)
                                .withProtocolOperationTimeoutMs(60000)

                        builder.build().use { connection ->
                            val connected: CompletableFuture<Boolean> = connection.connect()
                            try {
                                val sessionPresent: Boolean = connected.get()
                                println("Connected to " + (if (!sessionPresent) "new" else "existing") + " session!")
                            } catch (ex: Exception) {
                                throw RuntimeException("Exception occurred during connect", ex)
                            }
                            val countDownLatch: CountDownLatch = CountDownLatch(messagesToPublish)
                            val subscribed: CompletableFuture<Int> = connection.subscribe(topic, QualityOfService.AT_LEAST_ONCE, Consumer { message: MqttMessage ->
                                val payload: String = String(message.getPayload(), StandardCharsets.UTF_8)
                                println("MESSAGE: $payload")
                                countDownLatch.countDown()
                            })
                            subscribed.get()
                            countDownLatch.await()
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