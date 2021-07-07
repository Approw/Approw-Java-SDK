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
 * role management
 */
class RolesManagementClient(private val client: ManagementClient) {

    /**
     * Get roles list
     */
    @JvmOverloads
    fun list(
        page: Int? = null,
        limit: Int? = null,
        sortBy: SortByEnum? = null,
        namespace: String? = null
    ): HttpCall<RestfulResponse<PaginatedRoles>, PaginatedRoles> {
        val param = RolesParam(namespace, page, limit, sortBy)
        return list(param)
    }

    /**
     * Get roles list
     */
    fun list(param: RolesParam): HttpCall<RestfulResponse<PaginatedRoles>, PaginatedRoles> {
        var url = "${client.host}/api/v2/roles"
        url += if (param.page != null) "?page=${param.page}" else "?page=1"
        url += if (param.limit != null) "&limit=${param.limit}" else "&limit=10"
        url += if (param.sortBy != null) "&sortBy=${param.sortBy}" else ""

        return client.createHttpGetCall(
            url,
            object : TypeToken<RestfulResponse<PaginatedRoles>> () {}
        ) {
            it.data
        }
    }

    /**
     * Create a role
     */
    @JvmOverloads
    fun create(
        code: String,
        description: String? = null,
        parent: String? = null,
        namespace: String? = null
    ): HttpCall<RestfulResponse<Role>, Role> {
        val param = CreateRoleParam(namespace, code, description, parent)
        return create(param)
    }

    /**
     * Create a role
     */
    fun create(param: CreateRoleParam): HttpCall<RestfulResponse<Role>, Role> {
        return client.createHttpPostCall(
            "${client.host}/api/v2/roles",
            GsonBuilder().create().toJson(param),
            object : TypeToken<RestfulResponse<Role>> () {}
        ) {
            it.data
        }
    }

    /**
     * Get role details
     */
    @Deprecated("use findByCode", ReplaceWith("this.findByCode(param)"))
    fun detail(code: String): GraphQLCall<RoleResponse, Role> {
        val param = RoleParam(code)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RoleResponse>>() {}) {
            it.result
        }
    }

    fun findByCode(param: RoleParam): GraphQLCall<RoleResponse, Role> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RoleResponse>>() {}) {
            it.result
        }
    }

    /**
     * Update a role
     */
    @JvmOverloads
    fun update(
        code: String,
        description: String? = null,
        newCode: String? = null
    ): GraphQLCall<UpdateRoleResponse, Role> {
        val param = UpdateRoleParam(code, description, newCode)
        return update(param)
    }

    /**
     * Update a role
     */
    fun update(param: UpdateRoleParam): GraphQLCall<UpdateRoleResponse, Role> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<UpdateRoleResponse>>() {}) {
            it.result
        }
    }

    /**
     * Delete a role
     */
    fun delete(code: String): GraphQLCall<DeleteRoleResponse, CommonMessage> {
        val param = DeleteRoleParam(code)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<DeleteRoleResponse>>() {}) {
            it.result
        }
    }

    fun delete(param: DeleteRoleParam): GraphQLCall<DeleteRoleResponse, CommonMessage> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<DeleteRoleResponse>>() {}) {
            it.result
        }
    }

    /**
     * Bulk delete roles
     */
    fun deleteMany(codeList: List<String>): GraphQLCall<DeleteRolesResponse, CommonMessage> {
        val param = DeleteRolesParam(codeList)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<DeleteRolesResponse>>() {}) {
            it.result
        }
    }

    fun deleteMany(param: DeleteRolesParam): GraphQLCall<DeleteRolesResponse, CommonMessage> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<DeleteRolesResponse>>() {}) {
            it.result
        }
    }

    /**
     * Get user list of a role
     */
    fun listUsers(code: String): GraphQLCall<RoleWithUsersResponse, PaginatedUsers> {
        val param = RoleWithUsersParam(code)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RoleWithUsersResponse>>() {}) {
            it.result.users
        }
    }

    fun listUsers(param: RoleWithUsersParam): GraphQLCall<RoleWithUsersResponse, PaginatedUsers> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RoleWithUsersResponse>>() {}) {
            it.result.users
        }
    }

    /**
     * batch add users
     */
    fun addUsers(code: String, userIds: List<String>): GraphQLCall<AssignRoleResponse, CommonMessage> {
        val param = AssignRoleParam().withUserIds(userIds).withRoleCode(code)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<AssignRoleResponse>>() {}) {
            it.result
        }
    }

    fun addUsers(param: AssignRoleParam): GraphQLCall<AssignRoleResponse, CommonMessage> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<AssignRoleResponse>>() {}) {
            it.result
        }
    }

    /**
     * batch remove users
     */
    fun removeUsers(code: String, userIds: List<String>): GraphQLCall<RevokeRoleResponse, CommonMessage> {
        val param = RevokeRoleParam(code).withUserIds(userIds)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RevokeRoleResponse>>() {}) {
            it.result
        }
    }

    fun removeUsers(param: RevokeRoleParam): GraphQLCall<RevokeRoleResponse, CommonMessage> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RevokeRoleResponse>>() {}) {
            it.result
        }
    }

    /**
     * Get the role policy list
     */
    @JvmOverloads
    fun listPolicies(
        code: String,
        page: Int? = null,
        limit: Int? = null,
        namespace: String? = null,
        targetIdentifier: String? = null
    ): GraphQLCall<PolicyAssignmentsResponse, PaginatedPolicyAssignments> {
        val param =
            PolicyAssignmentsParam(namespace, code, PolicyAssignmentTargetType.ROLE, targetIdentifier, page, limit)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<PolicyAssignmentsResponse>>() {}) {
            it.result
        }
    }

    /**
     * Add policies
     */
    fun addPolicies(
        code: String,
        policies: List<String>
    ): GraphQLCall<AddPolicyAssignmentsResponse, CommonMessage> {
        val param =
            AddPolicyAssignmentsParam(policies, PolicyAssignmentTargetType.ROLE).withTargetIdentifiers(listOf(code))
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<AddPolicyAssignmentsResponse>>() {}) {
            it.result
        }
    }

    /**
     * Remove policies
     */
    fun removePolicies(
        code: String,
        policies: List<String>
    ): GraphQLCall<RemovePolicyAssignmentsResponse, CommonMessage> {
        val param =
            RemovePolicyAssignmentsParam(policies, PolicyAssignmentTargetType.ROLE).withTargetIdentifiers(listOf(code))
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RemovePolicyAssignmentsResponse>>() {}) {
            it.result
        }
    }

    fun listAuthorizedResources(
        param: ListRoleAuthorizedResourcesParam
    ): GraphQLCall<ListRoleAuthorizedResourcesResponse, PaginatedAuthorizedResources?> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<ListRoleAuthorizedResourcesResponse>>() {}
        ) { it.result.authorizedResources }
    }

    fun getUdfValue(roleCode: String): GraphQLCall<UdvResponse, Map<String, Any>> {
        val param = UdvParam(UdfTargetType.ROLE, roleCode)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<UdvResponse>>() {}) {
            convertUdvToKeyValuePair(it.result)
        }
    }

    fun getUdfValueBatch(roleCodes: List<String>): GraphQLCall<UdfValueBatchResponse, Map<String, Map<String, Any>>> {
        if (roleCodes.isEmpty()) {
            throw Exception("userIds can't be null")
        }

        val param = UdfValueBatchParam(UdfTargetType.ROLE, roleCodes)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<UdfValueBatchResponse>>() {}) {
            val hashtable = Hashtable<String, Map<String, Any>>()
            it.result.map { hashtable.put(it.targetId, convertUdvToKeyValuePair(it.data)) }
            hashtable
        }
    }

    fun setUdfValue(
        roleCode: String,
        data: Map<String, String>
    ): GraphQLCall<SetUdvBatchResponse, List<UserDefinedData>> {
        val udvList = data.entries.map { UserDefinedDataInput(it.key, Gson().toJson(it.value)) }
        val param = SetUdvBatchParam(UdfTargetType.ROLE, roleCode, udvList)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<SetUdvBatchResponse>>() {}) {
            it.result
        }
    }

    fun setUdfValue(
        roleCode: String,
        key: String,
        value: String
    ): GraphQLCall<SetUdvResponse, List<UserDefinedData>> {
        val param = SetUdvParam(
            targetType = UdfTargetType.ROLE,
            targetId = roleCode,
            key = key,
            value = Gson().toJson(value)
        )

        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<SetUdvResponse>>() {}
        ) { it.result }
    }

    fun setUdfValueBatch(input: List<RoleSetUdfValueBatchParams>): GraphQLCall<SetUdvBatchResponse, List<UserDefinedData>> {
        if (input.isEmpty()) {
            throw Exception("empty input list")
        }

        val param = SetUdfValueBatchParam(UdfTargetType.ROLE,
            input.flatMap { item ->
                item.data.map { SetUdfValueBatchInput(item.roleCode, it.key, Gson().toJson(it.value)) }
            }
        )

        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<SetUdvBatchResponse>>() {})
        {
            it.result
        }
    }

    fun removeUdfValue(roleCode: String, key: String): GraphQLCall<RemoveUdvResponse, List<UserDefinedData>> {
        val param = RemoveUdvParam(UdfTargetType.ROLE, roleCode, key)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RemoveUdvResponse>>() {}) {
            it.result
        }
    }

}
