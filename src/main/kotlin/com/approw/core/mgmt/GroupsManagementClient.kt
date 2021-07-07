package com.approw.core.mgmt

import com.approw.core.graphql.GraphQLCall
import com.approw.core.graphql.GraphQLException
import com.approw.core.graphql.GraphQLResponse
import com.approw.core.http.HttpCall
import com.approw.core.types.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Group Management
 */
class GroupsManagementClient(private val client: ManagementClient) {
    /**
     * Create a group
     */
    fun create(param: CreateGroupParam): GraphQLCall<CreateGroupResponse, Group> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<CreateGroupResponse>>() {}) {
            it.result
        }
    }

    /**
     * Delete a group
     */
    fun delete(code: String): GraphQLCall<DeleteGroupsResponse, CommonMessage> {
        val param = DeleteGroupsParam(listOf(code))
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<DeleteGroupsResponse>>() {}) {
            it.result
        }
    }


    /**
     * Update a group
     */
    fun update(param: UpdateGroupParam): GraphQLCall<UpdateGroupResponse, Group> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<UpdateGroupResponse>>() {}) {
            it.result
        }
    }

    /**
     * Get group details
     */
    fun detail(code: String): GraphQLCall<GroupResponse, Group> {
        val param = GroupParam(code)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<GroupResponse>>() {}) {
            it.result
        }
    }

    /**
     * Get the group list
     */
    fun list(param: GroupsParam): GraphQLCall<GroupsResponse, PaginatedGroups> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<GroupsResponse>>() {}) {
            it.result
        }
    }

    /**
     * Bulk delete groups
     */
    fun deleteMany(codeList: List<String>): GraphQLCall<DeleteGroupsResponse, CommonMessage> {
        val param = DeleteGroupsParam(codeList)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<DeleteGroupsResponse>>() {}) {
            it.result
        }
    }

    /**
     * Get users list of the group
     */
    fun listUsers(code: String): GraphQLCall<GroupWithUsersResponse, PaginatedUsers> {
        val param = GroupWithUsersParam(code)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<GroupWithUsersResponse>>() {}) {
            it.result.users
        }
    }
    /**
     * Get users list of the group with page parameter
     */
    fun listUsers(param: GroupWithUsersParam): GraphQLCall<GroupWithUsersResponse, PaginatedUsers> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<GroupWithUsersResponse>>() {}) {
            it.result.users
        }
    }

    /**
     * Add users to a group in bulk
     */
    fun addUsers(param: AddUserToGroupParam): GraphQLCall<AddUserToGroupResponse, CommonMessage> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<AddUserToGroupResponse>>() {}) {
            it.result
        }
    }

    /**
     * Batch delete users from a group
     */
    fun removeUsers(param: RemoveUserFromGroupParam): GraphQLCall<RemoveUserFromGroupResponse, CommonMessage> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RemoveUserFromGroupResponse>>() {}) {
            it.result
        }
    }

    fun listAuthorizedResources(
        param: ListGroupAuthorizedResourcesParam
    ): GraphQLCall<ListGroupAuthorizedResourcesResponse, Group> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<ListGroupAuthorizedResourcesResponse>>() {}
        ) {
            it.result
        }
    }
}
