package com.kairlec.koj.engine

import jnr.unixsocket.UnixSocketChannel
import java.io.File
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import javax.net.SocketFactory


class UnixDomainSocketFactory(private val path: File) : SocketFactory() {
    override fun createSocket(): Socket {
        return TunnelingUnixSocket(path, UnixSocketChannel.open())
    }

    override fun createSocket(host: String, port: Int): Socket {
        val result = createSocket()
        result.connect(InetSocketAddress(host, port))
        return result
    }

    override fun createSocket(
        host: String, port: Int, localHost: InetAddress, localPort: Int
    ): Socket {
        return createSocket(host, port)
    }

    override fun createSocket(host: InetAddress, port: Int): Socket {
        return createSocket().apply { connect(InetSocketAddress(host, port)) }
    }

    override fun createSocket(
        host: InetAddress, port: Int, localAddress: InetAddress, localPort: Int
    ): Socket {
        return createSocket(host, port)
    }
}
