package com.approw.core.mgmt

import com.approw.core.graphql.GraphQLCall
import com.approw.core.graphql.GraphQLResponse
import com.approw.core.types.*
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * udf management class
 */
class UdfManagementClient(private val client: ManagementClient) {
    /**
     * get udf metadata list
     */
    fun list(targetType: UdfTargetType): GraphQLCall<UdfResponse, List<UserDefinedField>> {
        val param = UdfParam(targetType)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<UdfResponse>>() {}) {
            it.result
        }
    }

    /**
     * set metadata. If it doesn't exist, it will create one.
     */
    fun set(
        targetType: UdfTargetType,
        key: String,
        dataType: UdfDataType,
        label: String
    ): GraphQLCall<SetUdfResponse, UserDefinedField> {
        val param = SetUdfParam(targetType, key, dataType, label)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<SetUdfResponse>>() {}) {
            it.result
        }
    }

    /**
     * remove metadata
     */
    fun remove(targetType: UdfTargetType, key: String): GraphQLCall<RemoveUdfResponse, CommonMessage> {
        val param = RemoveUdfParam(targetType, key)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RemoveUdfResponse>>() {}) {
            it.result
        }
    }

    /**
     * Batch add user defined data
     */
    fun setUdvBatch(
        targetType: UdfTargetType,
        targetId: String,
        udvList: List<UserDefinedDataInput>? = null
    ): GraphQLCall<SetUdvBatchResponse, List<UserDefinedData>> {
        val param = SetUdvBatchParam(targetType, targetId).withUdvList(udvList)

        udvList?.forEach { item -> item.value = GsonBuilder().create().toJson(item.value) }

        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<SetUdvBatchResponse>>() {}) { it.result }
    }

    /**
     * get user defined data list of an entity
     */
    fun listUdv(
        targetType: UdfTargetType,
        targetId: String
    ): GraphQLCall<UdvResponse, List<UserDefinedData>> {
        val param = UdvParam(targetType, targetId)

        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<UdvResponse>>() {}) { it.result }

    }
}
