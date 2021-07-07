package com.approw.core.mgmt

import com.approw.core.graphql.GraphQLCall
import com.approw.core.graphql.GraphQLResponse
import com.approw.core.types.*
import com.google.gson.reflect.TypeToken

/**
 * Policies Management
 */
class PoliciesManagementClient(private val client: ManagementClient) {
    /**
     * Get policy list
     */
    @JvmOverloads
    fun list(
        page: Int? = null,
        limit: Int? = null,
        namespace: String? = null
    ): GraphQLCall<PoliciesResponse, PaginatedPolicies> {
        val param = PoliciesParam(page, limit, namespace)
        return list(param)
    }

    /**
     * Get policy list
     */
    fun list(param: PoliciesParam): GraphQLCall<PoliciesResponse, PaginatedPolicies> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<PoliciesResponse>>() {}) {
            it.result
        }
    }

    /**
     * Create a policy
     */
    @JvmOverloads
    fun create(
        code: String,
        statements: List<PolicyStatementInput>,
        description: String? = null,
        namespace: String? = null
    ): GraphQLCall<CreatePolicyResponse, Policy> {
        val param = CreatePolicyParam(namespace, code, description, statements)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<CreatePolicyResponse>>() {}) {
            it.result
        }
    }

    /**
     * Get policy details
     */
    fun detail(code: String, namespace: String? = null): GraphQLCall<PolicyResponse, Policy> {
        val param = PolicyParam(namespace, code)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<PolicyResponse>>() {}) {
            it.result
        }
    }

    /**
     * Update a policy
     */
    @JvmOverloads
    fun update(
        code: String,
        statements: List<PolicyStatementInput>? = null,
        description: String? = null,
        newCode: String? = null,
        namespace: String? = null
    ): GraphQLCall<UpdatePolicyResponse, Policy> {
        val param = UpdatePolicyParam(namespace, code).withStatements(statements).withDescription(description).withNewCode(newCode)
        return update(param)
    }

    /**
     * Update a policy
     */
    fun update(param: UpdatePolicyParam): GraphQLCall<UpdatePolicyResponse, Policy> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<UpdatePolicyResponse>>() {}) {
            it.result
        }
    }

    /**
     * Delete a policy
     */
    fun delete(code: String): GraphQLCall<DeletePolicyResponse, CommonMessage> {
        val param = DeletePolicyParam(code)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<DeletePolicyResponse>>() {}) {
            it.result
        }
    }

    /**
     * Bulk delete policies
     */
    fun deleteMany(codeList: List<String>): GraphQLCall<DeletePoliciesResponse, CommonMessage> {
        val param = DeletePoliciesParam(codeList)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<DeletePoliciesResponse>>() {}) {
            it.result
        }
    }

    /**
     * Get policy assignment record
     */
    @JvmOverloads
    fun listAssignments(
        code: String,
        page: Int? = null,
        limit: Int? = null
    ): GraphQLCall<PolicyAssignmentsResponse, PaginatedPolicyAssignments> {
        val param = PolicyAssignmentsParam(code, page = page, limit = limit)
        return listAssignments(param)
    }

    /**
     * Get policy assignment record
     */
    fun listAssignments(param: PolicyAssignmentsParam): GraphQLCall<PolicyAssignmentsResponse, PaginatedPolicyAssignments> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<PolicyAssignmentsResponse>>() {}) {
            it.result
        }
    }

    /**
     * Add a policy assignment
     */
    fun addAssignments(
        policies: List<String>,
        targetType: PolicyAssignmentTargetType,
        targetIdentifiers: List<String>
    ): GraphQLCall<AddPolicyAssignmentsResponse, CommonMessage> {
        val param = AddPolicyAssignmentsParam(policies, targetType, targetIdentifiers)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<AddPolicyAssignmentsResponse>>() {}) {
            it.result
        }
    }

    /**
     * Remove a policy assignment
     */
    fun removeAssignments(
        policies: List<String>,
        targetType: PolicyAssignmentTargetType,
        targetIdentifiers: List<String>
    ): GraphQLCall<RemovePolicyAssignmentsResponse, CommonMessage> {
        val param = RemovePolicyAssignmentsParam(policies, targetType, targetIdentifiers)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RemovePolicyAssignmentsResponse>>() {}) {
            it.result
        }
    }
}
