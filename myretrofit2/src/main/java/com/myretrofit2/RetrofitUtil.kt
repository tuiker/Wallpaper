package com.myretrofit2

import android.util.Log
import com.myretrofit2.api.AppUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitUtil {
    companion object {
        fun getGetCall(token: String? = null): RetrofitService.ApiGetService {
            return getRetrofitBuild(token).create(RetrofitService.ApiGetService::class.java)
        }

        fun getPostCall(token: String? = null): RetrofitService.ApiPostService {
            return getRetrofitBuild(token).create(RetrofitService.ApiPostService::class.java)
        }

        private fun getRetrofitBuild(token: String? = null): Retrofit {
            return Retrofit.Builder()
                .baseUrl(AppUrl.API) // 替换为你的 API base URL
                .client(getClient(token))
                .addConverterFactory(GsonConverterFactory.create()) // 使用 Gson 转换器
                .build()
        }

        private fun getClient(token: String? = null): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // 设置连接超时时间
                .readTimeout(30, TimeUnit.SECONDS) // 设置读取超时时间
                .writeTimeout(30, TimeUnit.SECONDS) // 设置写入超时时间
                .addInterceptor { chain ->
                    // 获取原始的请求
                    val originalRequest = chain.request()
                    // 创建一个新的请求构建器，并添加 Authorization 请求头
                    val newRequestBuilder = originalRequest.newBuilder()
                        .header(
                            "Authorization",
                            "Bearer $token"
                        )//8579df9f5096457bb427dcfce2ff1c17
                    // 使用新的请求构建器创建一个新的请求
                    val newRequest = newRequestBuilder.build()
                    // 继续执行链中的下一个拦截器，并返回响应
                    chain.proceed(newRequest)
                }
                .build()
        }
    }
}