package com.approw.core.http

import com.approw.core.graphql.GraphQLResponse
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import okhttp3.Call
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class HttpCall<TData, TResult>(
    private val call: Call,
    private val adapter: TypeAdapter<TData>,
    private val resolver: (data: TData) -> TResult
) :
    com.approw.core.http.Call<TData, TResult> {
    /**
     * Gson object, used to serialize Json
     */
    private val gson = GsonBuilder().create()

    /**
     * Start sync request
     */
    @Throws(IOException::class)
    override fun execute(): TResult {
        // Start sync request
        val response: Response = call.execute()

        // Processing returned data
        if (response.isSuccessful) {
            val body = response.body?.string()
            val data = adapter.fromJson(body)
            return resolver(data)
        } else {
            throw IOException("Unexpected code $response\n${response.body?.string()}")
        }
    }

    /**
     * Start asynchronous request
     */
    override fun enqueue(callback: Callback<TResult>) {
        // Create a wrapper to process the returned raw data
        val callbackWrapper: okhttp3.Callback = object : okhttp3.Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val data = adapter.fromJson(response.body?.string())
                    callback.onSuccess(resolver(data))
                } else {
                    throw IOException("Unexpected code $response\n${response.body?.string()}")
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                callback.onFailure(GraphQLResponse.ErrorInfo(500, e.message))
            }
        }
        // Start asynchronous request
        call.enqueue(callbackWrapper);
    }

    override fun isExecuted(): Boolean {
        return call.isExecuted()
    }

    override fun cancel() {
        return call.cancel()
    }

    override fun isCanceled(): Boolean {
        return call.isCanceled()
    }

    override fun request(): Request? {
        return call.request()
    }

}