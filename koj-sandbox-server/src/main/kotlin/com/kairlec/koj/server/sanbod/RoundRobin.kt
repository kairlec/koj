package com.kairlec.koj.server.sanbod

class RoundRobin<T> {
    private data class Element<T>(
        val data: T,
        val weight: Int,
        var currentWeight: Int,
    )

    private val elements = mutableListOf<Element<T>>()

    fun add(data: T, weight: Int) = apply {
        elements.add(Element(data, weight, weight))
    }

    fun next(): T? {
        if (elements.isEmpty()) {
            return null
        }
        if (elements.size == 1) {
            return elements[0].data
        }
        var total = 0
        var p = elements[0]
        for (current in elements) {
            total += current.weight
            current.currentWeight += current.weight
            if (current.currentWeight > p.currentWeight) {
                p = current
            }
        }
        p.currentWeight -= total
        return p.data
    }
}
