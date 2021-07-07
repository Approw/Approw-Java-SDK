package com.approw.core.mgmt

import com.approw.core.graphql.GraphQLCall
import com.approw.core.graphql.GraphQLException
import com.approw.core.graphql.GraphQLResponse
import com.approw.core.types.*
import com.google.gson.reflect.TypeToken

/**
 * Whitelist management class.
 */
class WhitelistManagementClient(private val client: ManagementClient) {

    /**
     * Get whitelist records.
     */
    fun list(type: WhitelistType): GraphQLCall<WhitelistResponse, List<WhiteList>> {
        val param = WhitelistParam(type)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<WhitelistResponse>>() {}) {
            it.result
        }
    }

    /**
     * Add whitelist.
     * @param {WhitelistType} type means the type of whitelist, you can use: USERNAME Email Phone。
     * @param {string[]} list Whitelist. Email address is not case sensitive.
     */
    fun add(param: AddWhitelistParam): GraphQLCall<AddWhitelistResponse, List<WhiteList>> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<AddWhitelistResponse>>() {}) {
            it.result
        }
    }

    /**
     * Remove a whitelist.
     */
    fun remove(param: RemoveWhitelistParam): GraphQLCall<RemoveWhitelistResponse, List<WhiteList>> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RemoveWhitelistResponse>>() {}) {
            it.result
        }
    }

    /**
     * Enable a whitelist.
     */
    fun enable(type: WhitelistType): GraphQLCall<UpdateUserpoolResponse, UserPool> {
        return enableOrDisable(type, true)
    }

    /**
     * Disable a whitelist.
     */
    fun disable(type: WhitelistType): GraphQLCall<UpdateUserpoolResponse, UserPool> {
        return enableOrDisable(type, false)
    }

    private fun enableOrDisable(type: WhitelistType, action: Boolean): GraphQLCall<UpdateUserpoolResponse, UserPool> {
        val updatesWhite = RegisterWhiteListConfigInput()
        when (type) {
            WhitelistType.EMAIL -> updatesWhite.emailEnabled = action
            WhitelistType.PHONE -> updatesWhite.phoneEnabled = action
            WhitelistType.USERNAME -> updatesWhite.usernameEnabled = action
            else -> {
                throw GraphQLException(message = "Not supported whitelist type")
            }
        }

        val updates = UpdateUserpoolInput(whitelist = updatesWhite)
        return client.userpool().update(updates)
    }
}
