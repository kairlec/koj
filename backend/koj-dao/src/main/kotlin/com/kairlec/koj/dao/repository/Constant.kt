package com.kairlec.koj.dao.repository

import kotlinx.coroutines.flow.Flow

data class PageData<T>(
    val data: Flow<T>,
    val total: Int,
)

infix fun <T> Flow<T>.pg(total: Int): PageData<T> = PageData(this, total)