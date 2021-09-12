package main

data class ConfigureData(
    val rootCaPath: String = "/workspace/cert/AmazonRootCA1.pem",
    val certPath: String = "/workspace/cert/65556c5c050dc1f2de95c9e6ce21c9b8b77e8738d15411db00c30784e496712c-certificate.pem.crt",
    val keyPath: String = "/workspace/cert/65556c5c050dc1f2de95c9e6ce21c9b8b77e8738d15411db00c30784e496712c-private.pem.key",
    val endpoint: String = "a2hasg13ybb0qa-ats.iot.ap-northeast-1.amazonaws.com",
    val topic: String = "topic",
    val port: Int = 8883
)
