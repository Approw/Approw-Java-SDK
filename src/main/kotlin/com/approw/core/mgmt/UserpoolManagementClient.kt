package com.approw.core.mgmt

import com.approw.core.graphql.GraphQLCall
import com.approw.core.graphql.GraphQLResponse
import com.approw.core.http.HttpCall
import com.approw.core.types.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * userpool management class
 */
class UserpoolManagementClient(private val client: ManagementClient) {
    /**
     * query userpool configuration
     */
    fun detail(): GraphQLCall<UserpoolResponse, UserPool> {
        val param = UserpoolParam()
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<UserpoolResponse>>() {}) {
            it.result
        }
    }

    /**
     * update userpool configuration
     */
    fun update(updates: UpdateUserpoolInput): GraphQLCall<UpdateUserpoolResponse, UserPool> {
        val param = UpdateUserpoolParam(updates)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<UpdateUserpoolResponse>>() {}) {
            it.result
        }
    }

    /**
     * get environment variable list
     */
    fun listEnv(): HttpCall<RestfulResponse<List<Env>>, List<Env>> {
        return client.createHttpGetCall(
            "${client.host}/api/v2/env",
            object : TypeToken<RestfulResponse<List<Env>>>() {}) {
            it.data
        }
    }

    /**
     * add environment variables
     */
    fun addEnv(key: String, value: Any): HttpCall<RestfulResponse<Env>, Env> {
        val json = Gson()
        val data = "{\"key\": \"$key\", \"value\": ${json.toJson(value)}}"
        return client.createHttpPostCall(
            "${client.host}/api/v2/env",
            data,
            object : TypeToken<RestfulResponse<Env>>() {}) {
            it.data
        }
    }

    /**
     * remove environment variable
     */
    fun removeEnv(key: String): HttpCall<CommonMessage, CommonMessage> {
        return client.createHttpDeleteCall("${client.host}/api/v2/env/$key", object : TypeToken<CommonMessage>() {}) {
            it
        }
    }
}
