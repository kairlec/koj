package com.kairlec.koj.engine.extend

import com.kairlec.koj.engine.DockerClientDSL
import com.kairlec.koj.engine.HttpDockerClient
import com.kairlec.koj.engine.buildQuery
import com.kairlec.koj.engine.model.ContainersListRequestBuilder
import com.kairlec.koj.engine.model.CreateContainerRequest
import com.kairlec.koj.engine.model.CreateContainerResponse
import io.ktor.client.call.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import mu.KotlinLogging
import java.io.InputStream
import java.io.OutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.nio.ByteBuffer
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@DockerClientDSL
class ContainerScope(private val httpDockerClient: HttpDockerClient) {
    @DockerClientDSL
    suspend fun list(block: ContainersListRequestBuilder.() -> Unit): String {
        val request = ContainersListRequestBuilder().apply {
            block()
        }
        val filters = request.filters?.let(httpDockerClient.context.json::writeValueAsString)
        val url = buildQuery("/containers/json") {
            request.all?.let { "all" on it }
            request.limit?.let { "limit" on it }
            request.size?.let { "size" on it }
            filters?.let { "filters" on it }
        }
        return httpDockerClient.context.httpClient.get(url).body()
    }

    private val nameRegex = "^/?[a-zA-Z0-9][a-zA-Z0-9_.-]+\$".toRegex()

    @DockerClientDSL
    suspend fun create(
        name: String? = null,
        block: CreateContainerRequest.() -> Unit
    ): CreateContainerResponse {
        if (name != null) {
            require(name.matches(nameRegex)) { "Invalid container name" }
        }
        val url = buildQuery("/containers/create") {
            if (!name.isNullOrEmpty()) {
                "name" on name
            }
        }
        return httpDockerClient.context.httpClient.post(url) {
            contentType(ContentType.Application.Json)
            setBody(CreateContainerRequest().apply {
                block()
            })
        }.body()
    }

    @DockerClientDSL
    suspend fun start(containerId: String, detachKeys: String? = null) {
        val url = buildQuery("/containers/$containerId/start") {
            if (detachKeys != null) {
                "detachKeys" on detachKeys
            }
        }
        httpDockerClient.context.httpClient.post(url)
    }

    @DockerClientDSL
    suspend fun stop(containerId: String, timeout: Int? = null) {
        val url = buildQuery("/containers/$containerId/stop") {
            if (timeout != null) {
                "t" on timeout
            }
        }
        httpDockerClient.context.httpClient.post(url)
    }

    @DockerClientDSL
    suspend fun restart(containerId: String, timeout: Int? = null) {
        val url = buildQuery("/containers/$containerId/restart") {
            if (timeout != null) {
                "t" on timeout
            }
        }
        httpDockerClient.context.httpClient.post(url)
    }

    @DockerClientDSL
    suspend fun kill(containerId: String, signal: String? = null) {
        val url = buildQuery("/containers/$containerId/kill") {
            if (signal != null) {
                "signal" on signal
            }
        }
        httpDockerClient.context.httpClient.post(url)
    }

    data class AttachConfig(
        var detachKeys: String? = null,
        var logs: Boolean? = null,
        var stream: Boolean? = null,
        var stdin: Boolean? = null,
        var stdout: Boolean? = null,
        var stderr: Boolean? = null
    )

    data class AttachResult(
        val stdin: OutputStream,
        val stdout: InputStream,
        val stderr: InputStream
    )

    @DockerClientDSL
    suspend fun attach(
        containerId: String,
        block: AttachConfig.() -> Unit = {},
        result: suspend AttachResult.() -> Unit
    ) {
        coroutineScope {
            val stdin = makePiped()
            val stdout = makePiped()
            val stderr = makePiped()
            launch {
                httpDockerClient.context.httpClient.webSocket(path = buildQuery("/containers/${containerId}/attach/ws") {
                    AttachConfig().apply(block).let { config ->
                        config.detachKeys.notNull { "detachKeys" on it }
                        config.logs.notNull { "logs" on it }
                        config.stream.notNull { "stream" on it }
                        config.stdin.notNull { "stdin" on it }
                        config.stdout.notNull { "stdout" on it }
                        config.stderr.notNull { "stderr" on it }
                    }
                }.apply { println(this) }) {
                    launch {
                        write(stdin.first)
                    }
                    launch {
                        receive(stdout.second, stderr.second)
                    }
                }
            }
            result(AttachResult(stdin.second, stdout.first, stderr.first))
        }
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }

}

fun makePiped(): Pair<InputStream, OutputStream> {
    val (input, output) = PipedOutputStream().let {
        PipedInputStream(it) to it
    }
    return Pair(input, output)
}

private enum class ReceiveMode {
    HEAD, BODY
}

private enum class StreamType(val typeCode: Byte) {
    STDIN(0), STDOUT(1), STDERR(2);

    companion object {
        fun fromCode(code: Byte): StreamType {
            return values().first { it.typeCode == code }
        }
    }
}

class ByteArrayFrame {
    private val queue = LinkedList<ByteBuffer>()
    fun append(byteArray: ByteArray) {
        queue.add(ByteBuffer.wrap(byteArray))
    }

    fun hasNext(size: Int): Boolean {
        var remaining = 0
        queue.forEach {
            remaining += it.remaining()
            if (remaining >= size) {
                return true
            }
        }
        return false
    }

    fun nextHeader(): ByteArray? {
        return nextFrame(8)
    }

    fun nextFrame(size: Int): ByteArray? {
        if (!hasNext(size)) {
            return null
        }
        var leftLength = size
        val byteArray = ByteArray(size)
        for (byteBuffer in queue) {
            val remaining = byteBuffer.remaining()
            if (remaining < leftLength) {
                byteBuffer.get(byteArray, 0, remaining)
                leftLength -= remaining
            } else {
                byteBuffer.get(byteArray, 0, leftLength)
            }
        }
        queue.removeIf { it.remaining() == 0 }
        return byteArray
    }
}

suspend fun WebSocketSession.write(stdin: InputStream) {
    withContext(Dispatchers.IO) {
        while (isActive) {
            val buffer = ByteArray(1024)
            if (stdin.available() <= 0) {
                delay(1)
                continue
            }
            val read = withContext(Dispatchers.IO) {
                stdin.read(buffer)
            }
            if (read == -1) {
                break
            }
            val header = ByteArray(4)
            ByteBuffer.wrap(header).putInt(read)
            send(byteArrayOf(StreamType.STDIN.typeCode, 0, 0, 0, *header))
            val byteArray = ByteArray(read)
            System.arraycopy(buffer, 0, byteArray, 0, read)
            send(Frame.Binary(true, byteArray))
            flush()
        }
    }
}

suspend fun WebSocketSession.receive(stdout: OutputStream, stderr: OutputStream) {
    val byteArrayFrame = ByteArrayFrame()
    var receiveMode = ReceiveMode.HEAD
    var leftLength = 0
    var streamType: StreamType? = null
    for (message in incoming) {
        if (message is Frame.Binary) {
            byteArrayFrame.append(message.data)
            while (true) {
                if (receiveMode == ReceiveMode.HEAD) {
                    val header = byteArrayFrame.nextHeader() ?: break
                    leftLength = ByteBuffer.wrap(header.sliceArray(4..7)).int
                    streamType = StreamType.fromCode(header[0])
                    receiveMode = ReceiveMode.BODY
                } else {
                    val data = byteArrayFrame.nextFrame(leftLength) ?: break
                    withContext(Dispatchers.IO) {
                        when (streamType) {
                            StreamType.STDOUT -> {
                                stdout.write(data)
                                stdout.flush()
                            }
                            StreamType.STDERR -> {
                                stderr.write(data)
                                stderr.flush()
                            }
                            null, StreamType.STDIN -> {
                                // ignore
                            }
                        }
                    }
                    receiveMode = ReceiveMode.HEAD
                }
            }
        }
    }
}

@OptIn(ExperimentalContracts::class)
fun <T> Any?.notNull(block: (Any) -> T): T? {
    contract {
        returnsNotNull() implies (this@notNull != null)
        callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
    }
    return if (this != null) {
        block(this)
    } else {
        null
    }
}