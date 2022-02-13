package com.kairlec.koj.engine

import jnr.unixsocket.UnixSocket
import jnr.unixsocket.UnixSocketAddress
import jnr.unixsocket.UnixSocketChannel
import java.io.File
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.SocketAddress


internal class TunnelingUnixSocket(private val path: File, channel: UnixSocketChannel) : UnixSocket(channel) {
    private var inetSocketAddress: InetSocketAddress? = null

    constructor(path: File, channel: UnixSocketChannel, address: InetSocketAddress) : this(path, channel) {
        inetSocketAddress = address
    }

    override fun connect(endpoint: SocketAddress) {
        inetSocketAddress = endpoint as InetSocketAddress
        super.connect(UnixSocketAddress(path), 0)
    }

    override fun connect(endpoint: SocketAddress, timeout: Int) {
        inetSocketAddress = endpoint as InetSocketAddress
        super.connect(UnixSocketAddress(path), timeout)
    }

    override fun getInetAddress(): InetAddress {
        return inetSocketAddress!!.address
    }
}
