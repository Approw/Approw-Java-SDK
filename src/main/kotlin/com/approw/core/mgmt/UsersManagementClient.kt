package com.approw.core.mgmt

import com.approw.core.Utils
import com.approw.core.graphql.GraphQLCall
import com.approw.core.graphql.GraphQLResponse
import com.approw.core.http.HttpCall
import com.approw.core.types.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.util.*

/**
 * user management class
 */
class UsersManagementClient(private val client: ManagementClient) {
    @JvmOverloads
    fun list(
        page: Int? = null,
        limit: Int? = null,
        sortBy: SortByEnum? = null
    ): GraphQLCall<UsersResponse, PaginatedUsers> {
        val param = UsersParam(page, limit, sortBy)
        return list(param)
    }

    /**
     * gt the user list of the userpool
     */
    fun list(param: UsersParam): GraphQLCall<UsersResponse, PaginatedUsers> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<UsersResponse>>() {}) {
            it.result
        }
    }

    /**
     * create a new user
     */
    @JvmOverloads
    fun create(userInfo: CreateUserInput, options: CreateUserOptions? = null): HttpCall<RestfulResponse<User>, User> {
        val param = CreateUserParam(userInfo).withKeepPassword(options?.keepPassword)
        if (param.userInfo.password !== null) {
            param.userInfo.password = client.encrypt(param.userInfo.password!!)
        }
        return client.createHttpPostCall(
            "${client.host}/api/v2/users",
            GsonBuilder().create().toJson(param),
            object : TypeToken<RestfulResponse<User>> () {}
        ) { it.data }
    }

    /**
     * create a new user (password will not be encrypted stored)
     */
    fun update(userId: String, updates: UpdateUserInput): HttpCall<RestfulResponse<User>, User> {

        return client.createHttpPostCall(
            "${client.host}/api/v2/users/${userId}",
            GsonBuilder().create().toJson(updates),
            object : TypeToken<RestfulResponse<User>> () {}
        ) {
            it.data
        }

    }

    /**
     * get user information, need to pass user ID
     */
    fun detail(userId: String): HttpCall<RestfulResponse<User>, User>  {
        return client.createHttpGetCall(
            "${client.host}/api/v2/users/$userId",
            object : TypeToken<RestfulResponse<User>> () {}
        ) { it.data }
    }

    /**
     * blur search user
     */
    @JvmOverloads
    fun search(
        query: String, fields: List<String>? = null, page: Int? = null, limit: Int? = null
    ): GraphQLCall<SearchUserResponse, PaginatedUsers> {
        val param = SearchUserParam(query, fields, page, limit)
        return search(param)
    }

    /**
     * blur search user
     */
    fun search(param: SearchUserParam): GraphQLCall<SearchUserResponse, PaginatedUsers> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<SearchUserResponse>>() {}) {
            it.result
        }
    }

    /**
     * find a user
     */
    fun find(param: FindUserParam): GraphQLCall<FindUserResponse, User> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<FindUserResponse>>() {}) {
            it.result
        }
    }

    /**
     * batch get user information through a user ID list
     */
    @JvmOverloads
    fun batch(
        identifiers: List<String>,
        options: BatchGetUserOptions? = null
    ): HttpCall<RestfulResponse<List<User>>, List<User>> {
        return client.createHttpPostCall(
            "${client.host}/api/v2/users/batch",
            Gson().toJson(BatchGetUserPostData(identifiers, options?.queryField)),
            object : TypeToken<RestfulResponse<List<User>>>() {}) {
            it.data
        }
    }

    /**
     * delete a single user
     */
    fun delete(userId: String): GraphQLCall<DeleteUserResponse, CommonMessage> {
        val param = DeleteUserParam(userId)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<DeleteUserResponse>>() {}) {
            it.result
        }
    }

    /**
     * batch delete multiple users
     */
    fun deleteMany(userIds: List<String>): GraphQLCall<DeleteUsersResponse, CommonMessage> {
        val param = DeleteUsersParam(userIds)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<DeleteUsersResponse>>() {}) {
            it.result
        }
    }

    /**
     * check whether the user exists. Now supported parameters are: username, email and phone number.
     */
    fun exists(param: IsUserExistsParam): HttpCall<RestfulResponse<Boolean>, Boolean> {
        val url = "${client.host}/api/v2/users/is-user-exists"

        return client.createHttpGetCall(
            Utils().getQueryUrl(url, param),
            object : TypeToken<RestfulResponse<Boolean>> () {}
        ) {
            it.data
        }
    }

    /**
     * Get a user's role list.
     * TODO: Removed in high version.
     */
    @Deprecated("Please use listRoles(userId: String, namespace: String?) instead of this function.")
    fun listRoles(userId: String): HttpCall<RestfulResponse<PaginatedRoles>, PaginatedRoles> {
        return listRoles(userId, null);
    }

    /**
     * Query a user's role list.
     */
    fun listRoles(userId: String, namespace: String?): HttpCall<RestfulResponse<PaginatedRoles>, PaginatedRoles> {
        var url = "${client.host}/api/v2/users/${userId}/roles"

        url += if (namespace != null) "?namespace=${namespace}" else ""

        return client.createHttpGetCall(
            url,
            object : TypeToken<RestfulResponse<PaginatedRoles>> () {}
        ) {
            it.data
        }
    }

    /**
     * Batch add roles to a user.
     * TODO: Removed in high version.
     */
    @Deprecated("Please use addRoles(userId: String, roles: List<String>, namespace: String?) instead of this function.")
    fun addRoles(userId: String, roleCodes: List<String>): HttpCall<RestfulResponse<CommonMessage>, CommonMessage> {
        return addRoles(userId, roleCodes, null);
    }

    /**
     *  Batch add roles to a user.=
     */
    fun addRoles(
        userId: String,
        roleCodes: List<String>,
        namespace: String?
    ): HttpCall<RestfulResponse<CommonMessage>, CommonMessage> {
        val options = RestAddRolesParams(userId = userId, namespace = namespace, list = roleCodes)
        return client.createHttpPostCall(
            "${client.host}/api/v2/users/${options.userId}/roles",
            GsonBuilder().create().toJson(options),
            object : TypeToken<RestfulResponse<CommonMessage>> () {}
        ) {
            it.data
        }
    }

    /**
     * Batch remove roles from a user.
     * TODO: Removed in high version.
     */
    @Deprecated("Please use removeRoles(userId: String, roles: List<String>, namespace: String?) instead of using function.")
    fun removeRoles(userId: String, roleCodes: List<String>): GraphQLCall<RevokeRoleResponse, CommonMessage> {
        return removeRoles(userId, roleCodes, null);
    }

    /**
     * Batch remove roles from a user.
     */
    fun removeRoles(
        userId: String,
        roleCodes: List<String>,
        namespace: String?
    ): GraphQLCall<RevokeRoleResponse, CommonMessage> {
        val param = RevokeRoleParam().withUserIds(listOf(userId)).withRoleCodes(roleCodes).withNamespace(namespace)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RevokeRoleResponse>>() {}) {
            it.result
        }
    }

    /**
     * Refresh user token
     */
    fun refreshToken(userId: String): GraphQLCall<RefreshTokenResponse, RefreshToken> {
        val param = RefreshTokenParam(userId)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RefreshTokenResponse>>() {}) {
            it.result
        }
    }

    /**
     * Get the group list of a user.
     */
    fun listGroups(userId: String): GraphQLCall<GetUserGroupsResponse, PaginatedGroups> {
        val param = GetUserGroupsParam(userId)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<GetUserGroupsResponse>>() {}) {
            it.result.groups!!
        }
    }

    /**
     * Add the user to a group.
     */
    fun addGroup(userId: String, group: String): GraphQLCall<AddUserToGroupResponse, CommonMessage> {
        val param = AddUserToGroupParam(listOf(userId), group)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<AddUserToGroupResponse>>() {}) {
            it.result
        }
    }

    /**
     * User quits a group.
     */
    fun removeGroup(userId: String, group: String): GraphQLCall<RemoveUserFromGroupResponse, CommonMessage> {
        val param = RemoveUserFromGroupParam(listOf(userId), group)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RemoveUserFromGroupResponse>>() {}) {
            it.result
        }
    }

    /**
     * Get the policy list of a user.
     */
    @JvmOverloads
    fun listPolicies(
        userId: String,
        page: Int = 1,
        limit: Int = 10
    ): GraphQLCall<PolicyAssignmentsResponse, PaginatedPolicyAssignments> {
        val param =
            PolicyAssignmentsParam().withTargetType(PolicyAssignmentTargetType.USER).withTargetIdentifier(userId)
                .withPage(page).withLimit(limit)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<PolicyAssignmentsResponse>>() {}) {
            it.result
        }
    }

    /**
     * Batch add policies for a user.
     */
    fun addPolicies(
        userId: String,
        policies: List<String>
    ): GraphQLCall<AddPolicyAssignmentsResponse, CommonMessage> {
        val param =
            AddPolicyAssignmentsParam(policies, PolicyAssignmentTargetType.USER).withTargetIdentifiers(listOf(userId))
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<AddPolicyAssignmentsResponse>>() {}) {
            it.result
        }
    }

    /**
     * Batch remove policies from a user.
     */
    fun removePolicies(
        userId: String,
        policies: List<String>
    ): GraphQLCall<RemovePolicyAssignmentsResponse, CommonMessage> {
        val param = RemovePolicyAssignmentsParam(
            policies,
            PolicyAssignmentTargetType.USER
        ).withTargetIdentifiers(listOf(userId))
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RemovePolicyAssignmentsResponse>>() {}) {
            it.result
        }
    }

    /**
     * Get the user-defined-data list of the current user.
     */
    fun listUdv(userId: String): GraphQLCall<UdvResponse, List<UserDefinedData>> {
        val param = UdvParam(UdfTargetType.USER, userId)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<UdvResponse>>() {}) {
            it.result
        }
    }

    /**
     * Add user-defined-data.
     */
    fun setUdv(userId: String, key: String, value: Any): GraphQLCall<SetUdvResponse, List<UserDefinedData>> {
        val json = Gson()
        val param = SetUdvParam(UdfTargetType.USER, userId, key, json.toJson(value))
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<SetUdvResponse>>() {}) {
            it.result
        }
    }

    /**
     * Remove user-defined-data.
     */
    fun removeUdv(userId: String, key: String): GraphQLCall<RemoveUdvResponse, List<UserDefinedData>> {
        val param = RemoveUdvParam(UdfTargetType.USER, userId, key)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RemoveUdvResponse>>() {}) {
            it.result
        }
    }

    /**
     * Get the organization list of the user.
     */
    fun listOrgs(userId: String): HttpCall<RestfulResponse<List<List<Org>>>, List<List<Org>>> {
        return client.createHttpGetCall(
            "${client.host}/api/v2/users/${userId}/orgs",
            object : TypeToken<RestfulResponse<List<List<Org>>>>() {}) {
            it.data
        }
    }

    /**
     * Get a list of all authorized resources of the user.
     */
    fun listAuthorizedResources(
        userId: String,
        namespace: String
    ): GraphQLCall<ListUserAuthorizedResourcesResponse, PaginatedAuthorizedResources> {
        val param = ListUserAuthorizedResourcesParam(userId, namespace)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<ListUserAuthorizedResourcesResponse>>() {}) {
            it.result.authorizedResources!!
        }
    }

    fun listAuthorizedResources(
        param: ListUserAuthorizedResourcesParam
    ): GraphQLCall<ListUserAuthorizedResourcesResponse, PaginatedAuthorizedResources> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<ListUserAuthorizedResourcesResponse>>() {}) {
            it.result.authorizedResources!!
        }
    }

    /**
     * Get all user-defined-data of current user.
     *
     */
    fun getUdfValue(userId: String): HttpCall<RestfulResponse<List<UserDefinedData>>, Map<String, Any>> {

        return client.createHttpGetCall(
            "${client.host}/api/v2/udfs/values?targetId=${userId}&targetType=${UdfTargetType.USER}",
            object : TypeToken<RestfulResponse<List<UserDefinedData>>> () {}
        ) {
            convertUdvToKeyValuePair(it.data)
        }

    }

    /**
     * Butch get user-defined-data of multiple users.
     *
     */
    fun getUdfValueBatch(userIds: List<String>): GraphQLCall<UdfValueBatchResponse, Map<String, Map<String, Any>>> {
        if (userIds.isEmpty()) {
            throw Exception("userIds can't be null")
        }

        val param = UdfValueBatchParam(UdfTargetType.USER, userIds)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<UdfValueBatchResponse>>() {}) {
            val hashtable = Hashtable<String, Map<String, Any>>()
            it.result.map { hashtable.put(it.targetId, convertUdvToKeyValuePair(it.data)) }
            hashtable
        }
    }

    /**
     * Set user-defined-data of a user.
     */
    fun setUdfValue(
        userId: String,
        data: Map<String, String>
    ): HttpCall<RestfulResponse<List<UserDefinedData>>, List<UserDefinedData>> {
        val params = RestSetUdfValueParams("USER", userId, data)

        return client.createHttpPostCall(
            "${client.host}/api/v2/udvs",
            GsonBuilder().create().toJson(params),
            object : TypeToken<RestfulResponse<List<UserDefinedData>>> () {}
        ) {
            it.data
        }
    }

    /**
     * Batch set user-defined-data of a user.
     */
    fun setUdfValueBatch(input: List<SetUdfValueBatchInputItem>): GraphQLCall<SetUdvBatchResponse, List<UserDefinedData>> {
        if (input.isEmpty()) {
            throw Exception("empty input list")
        }

        val inputList = input.flatMap { item ->
            item.data.map { SetUdfValueBatchInput(item.userId, it.key, Gson().toJson(it.value)) }
        }

        println(input)
        val param = SetUdfValueBatchParam(UdfTargetType.USER, inputList)

        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<SetUdvBatchResponse>>() {})
        {
            it.result
        }
    }

    /**
     * Remove user-defined-data of a user.
     */
    fun removeUdfValue(userId: String, key: String): GraphQLCall<RemoveUdvResponse, List<UserDefinedData>> {
        return removeUdv(userId, key);
    }

    /**
     * check whether the user has a specific role
     */
    fun hasRole(
        option: IHasRoleParam
    ): Boolean {
        val (totalCount, list) = this.listRoles(option.userId, option.namespace).execute()

        if (totalCount < 1) return false

        var hasRole = false;

        list.forEach { item -> if (item.code === option.roleCode) hasRole = true }

        return hasRole
    }

    /**
     * force log out a batch of user
     */
    fun kick(
        userIds: List<String>
    ): HttpCall<RestfulResponse<Boolean>, Boolean> {
        val url =
            "${client.host}/api/v2/users/kick"

        val body = "{ \"userIds\": ${GsonBuilder().create().toJson(userIds)} }"

        return this.client.createHttpPostCall(
            url,
            body,
            object : TypeToken<RestfulResponse<Boolean>>() {}) { it.code == 200 }
    }

    @JvmOverloads
    fun listUserActions(
        options: ListUserActionsParams? = ListUserActionsParams()
    ): HttpCall<RestfulResponse<Pagination<Any>>, Pagination<Any>> {
        var url = "${client.host}/api/v2/analysis/user-action?page=${options?.page}&limit=${options?.limit}"

        url += if (options?.clientIp != null) "&clientip=${options.clientIp}" else ""
        url += if (options?.operationName != null) "&operation_name=${options.operationName}" else ""
        url += if (options?.operatoArn != null) "&operator_arn=${options.operatoArn}" else ""

        return client.createHttpGetCall(
            url,
            object : TypeToken<RestfulResponse<Pagination<Any>>>() {}
        ) { it.data }
    }

    fun listDepartment(
        userId: String
    ): HttpCall<RestfulResponse<Pagination<UserDepartment>>, Pagination<UserDepartment>> {
        return client.createHttpGetCall(
            "${client.host}/api/v2/users/${userId}/departments",
            object : TypeToken<RestfulResponse<Pagination<UserDepartment>>> () {}
        ) {
            it.data
        }
    }

    fun checkLoginStatus(
        param: CheckLoginStatusParams
    ): HttpCall<RestfulResponse<UserCheckLoginStatusResponse>, UserCheckLoginStatusResponse> {
        var url = "${client.host}/api/v2/users/login-status?userId=${param.userId}"

        url += if (param.appId != null) "&appId=${param.appId}" else ""
        url += if (param.deviceId != null) "&deviceId=${param.deviceId}" else ""

        return client.createHttpGetCall(
            url,
            object : TypeToken<RestfulResponse<UserCheckLoginStatusResponse>>() {}) { it.data }
    }

    fun logout(options: UserLogoutParams): HttpCall<RestfulResponse<Boolean>, Boolean> {

        var url = "${client.host}/logout?userId=${options.userId}"

        url += if (options.appId != null) "&appId=${options.appId}" else ""

        return client.createHttpGetCall(
            url,
            object : TypeToken<RestfulResponse<Boolean>> () {}
        ) {
            it.code == 200
        }
    }

}
