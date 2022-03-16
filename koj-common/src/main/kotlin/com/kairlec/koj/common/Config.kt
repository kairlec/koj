package com.kairlec.koj.common

private const val taskTopic = "sandbox-task-topic"
private const val resultTopic = "sandbox-result-topic"
private const val statusTopic = "sandbox-status-topic"

fun taskTopic(languageId: String): String {
    return "$taskTopic-$languageId"
}

fun resultTopic(): String {
    return resultTopic
}

fun statusTopic(): String {
    return statusTopic
}