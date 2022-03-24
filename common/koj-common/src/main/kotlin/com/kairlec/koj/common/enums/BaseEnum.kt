package com.kairlec.koj.common.enums

/**
 * @author : Kairlec
 * @since : 2022/2/14
 **/
interface BaseEnum {
    /**
     * 通用的基类枚举类型,让spring可以去直接将code类型的序列化成对应的枚举
     *
     * @return id编码
     */
    val code: Int

    /**
     * 通用的基类枚举类型,让spring可以去直接将code类型的序列化成对应的枚举
     */
    val value: String

    /**
     * 获取枚举的友好名称,这个是可以给人读的
     */
    val friendName: String get() = value
}
