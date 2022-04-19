package com.kairlec.koj.common.exception

import com.kairlec.koj.common.HttpStatusCode

/**
 * @author : Kairlec
 * @since : 2022/2/14
 **/
enum class GlobalErrorCode(
    val code: Int,
    val message: String,
    val httpStatusCode: HttpStatusCode = HttpStatusCode.Companion.ClientError.BadRequest,
) {
    // 0为无错误
    NO_ERROR(0, "无错误", HttpStatusCode.Companion.Successful.OK),
    SUCCESS(0, "无错误", HttpStatusCode.Companion.Successful.OK),

    // 登录认证相关
    ACCOUNT_NOT_LOGIN(10001, "账户未登录", HttpStatusCode.Companion.ClientError.Unauthorized),
    USERNAME_PASSWORD_WRONG(10002, "用户名或密码错误", HttpStatusCode.Companion.ClientError.Unauthorized),
    USER_HAS_BE_BLOCKED(10003, "用户被禁用", HttpStatusCode.Companion.ClientError.Forbidden),

    //10004
    ILLEGAL_REQUEST(10007, "非法请求", HttpStatusCode.Companion.ClientError.Forbidden),
    ILLEGAL_RESPONSE_TYPE(10008, "无效的请求类型", HttpStatusCode.Companion.ClientError.BadRequest),
    USER_DISABLED(10011, "用户已被禁用", HttpStatusCode.Companion.ClientError.Forbidden),

    // 请求出错
    MISMATCH_ARGUMENTS(20000, "参数出错", HttpStatusCode.Companion.ClientError.BadRequest),
    UNKOWN_REQUEST(20001, "未知的请求", HttpStatusCode.Companion.ClientError.NotFound),
    METHOD_NOT_ALLOW(20002, "无效的请求方法", HttpStatusCode.Companion.ClientError.MethodNotAllowed),
    CONTENT_NOT_FOUND(20004, "无法找到相应的内容", HttpStatusCode.Companion.ClientError.NotFound),
    CONTENT_NOT_MODIFIED(20005, "内容未修改", HttpStatusCode.Companion.Redirection.NotModified),
    REQUEST_CONFLICT(20007, "请求冲突", HttpStatusCode.Companion.ClientError.Conflict),
    DATA_TOO_LONG(20008, "数据过长", HttpStatusCode.Companion.ClientError.RequestEntityTooLarge),
    COMPETITION_PWD_ERROR(20009, "竞赛密码错误", HttpStatusCode.Companion.ClientError.Forbidden),
    TOKEN_VERIFY_FAILED(20010, "token验证失败", HttpStatusCode.Companion.ClientError.Forbidden),
    TOKEN_EXPIRED(20011, "token已过期", HttpStatusCode.Companion.ClientError.Forbidden),
    TOKEN_INVALID(20012, "token无效", HttpStatusCode.Companion.ClientError.Forbidden),
    PERMISSION_DENIED(20013, "权限不足", HttpStatusCode.Companion.ClientError.Forbidden),
    COMPETITION_OVER(20014, "比赛已结束", HttpStatusCode.Companion.ClientError.BadRequest),
    COMPETITION_FROZEN(20015, "比赛已冻结", HttpStatusCode.Companion.ClientError.BadRequest),
    COMPETITION_NOT_STARTED_YET(20016, "比赛未开始", HttpStatusCode.Companion.ClientError.BadRequest),

    // 业务问题
    USERNAME_EXISTS(30000, "用户名已存在", HttpStatusCode.Companion.ClientError.Conflict),
    USER_CREATE_FAILED(30003, "用户创建失败", HttpStatusCode.Companion.ServerError.InternalServerError),
    CREATE_SUBMIT_FAILED(30004, "创建提交失败", HttpStatusCode.Companion.ServerError.InternalServerError),
    CREATE_CODE_RECORD_FAILED(30005, "创建代码记录失败", HttpStatusCode.Companion.ServerError.InternalServerError),

    // 服务器方面错误或未知的错误,9开头
    DATA_ERROR(90001, "数据错误", HttpStatusCode.Companion.ServerError.InternalServerError),
    UNKNOWN_ERROR(90002, "未知错误", HttpStatusCode.Companion.ServerError.InternalServerError),
    AN_EXCEPTION_OCCURRED(90003, "发生了一个异常", HttpStatusCode.Companion.ServerError.InternalServerError),
    MISSING_REQUIRE_ATTRIBUTE(90005, "缺少所需的属性", HttpStatusCode.Companion.ServerError.InternalServerError),
    GENERATE_TOKEN_FAILED(90006, "生成token失败", HttpStatusCode.Companion.ServerError.InternalServerError),
    TOKEN_ERROR(90007, "token操作异常", HttpStatusCode.Companion.ServerError.InternalServerError),

    ;
}
