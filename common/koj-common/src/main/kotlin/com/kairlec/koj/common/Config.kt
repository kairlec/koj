package com.kairlec.koj.common

const val taskTopicPrefix = "sandbox-task-topic-"
const val resultTopicPrefix = "sandbox-result-topic"
const val statusTopicPrefix = "sandbox-status-topic"

fun taskTopic(languageId: String): String {
    if (languageId.startsWith(taskTopicPrefix)) {
        return languageId.substring(taskTopicPrefix.length + 1)
    }
    return "$taskTopicPrefix$languageId"
}

fun resultTopic(): String {
    return resultTopicPrefix
}

fun statusTopic(): String {
    return statusTopicPrefix
}

fun deadLetterTopic(): String {
    return "sandbox-task-dlq"
}

interface LanguageIdContent {
    val id: String
}