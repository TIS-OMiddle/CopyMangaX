package com.crow.base.extensions

import com.crow.base.viewmodel.ViewStateException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.UnknownHostException

/*************************
 * @Machine: RedmiBook Pro 15 Win11
 * @Path: lib_base/src/main/java/cn/barry/base/network
 * @Time: 2022/5/1 13:54
 * @Author: BarryAllen
 * @Description: Flow扩展
 * @formatter:off
 **************************/

/**
 * 异步请求
 */
internal fun <R> ProducerScope<R>.callEnqueueFlow(call: Call<R>) {
    call.enqueue(object : Callback<R> {
        override fun onResponse(call: Call<R>, response: Response<R>) {
            response.logMsg()
            processing(response)
        }

        override fun onFailure(call: Call<R>, t: Throwable) {
            t.message?.logError()
            if (t is UnknownHostException) {
                close(ViewStateException("解析地址错误！请检查您的网络！", t))
            }
            close(t)
        }
    })
}

/**
 * 同步请求
 */
internal fun <R> ProducerScope<R>.callFlow(call: Call<R>) {
    runCatching {
        processing(call.execute())
    }.onFailure {
        cancel(CancellationException(it.localizedMessage, it))
    }
}

internal fun <R> ProducerScope<R>.processing(response: Response<R>) {

    //HttpCode 为 200
    if (response.isSuccessful) {
        val body = response.body()
        // 204: 执行成功但是没有返回数据
        if (body == null || response.code() == 204) {
            cancel(CancellationException("HTTP status code: ${response.code()}"))
        } else {
            trySendBlocking(body)
                .onSuccess { close() }
                .onClosed { cancel(CancellationException(it?.localizedMessage, it)) }
                .onFailure { cancel(CancellationException(it?.localizedMessage, it)) }
        }
    } else {
        val msg = response.errorBody()?.string()
        cancel(CancellationException((msg ?: response.message())))
    }
}

suspend inline fun <T> Flow<T>.toData(): T? {
    var data: T? = null
    collect {
        data = it
    }
    return data
}