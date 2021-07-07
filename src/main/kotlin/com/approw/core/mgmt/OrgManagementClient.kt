package com.approw.core.mgmt

import com.approw.core.Utils
import com.approw.core.graphql.GraphQLCall
import com.approw.core.graphql.GraphQLResponse
import com.approw.core.http.HttpCall
import com.approw.core.types.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * org management client
 */
class OrgManagementClient(private val client: ManagementClient) {
    /**
     * create org
     */
    fun create(param: CreateOrgParam): GraphQLCall<CreateOrgResponse, Org> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<CreateOrgResponse>>() {}) {
            it.result
        }
    }

    /**
     * Delete an Organization
     */
    fun deleteById(id: String): GraphQLCall<DeleteOrgResponse, CommonMessage> {
        val param = DeleteOrgParam(id)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<DeleteOrgResponse>>() {}) {
            it.result
        }
    }

    /**
     * Get organizations list
     */
    fun list(param: OrgsParam): HttpCall<RestfulResponse<PaginatedOrgs>, PaginatedOrgs> {
        var url = "${client.host}/api/v2/orgs/pagination"
        url += "?limit=${param.limit ?: -1}"
        url += if (param.page != null) "&page=${param.page}" else ""
        url += if (param.sortBy != null) "&sortBy=${param.sortBy}" else ""

        return client.createHttpGetCall(
            url,
            object : TypeToken<RestfulResponse<PaginatedOrgs>> () {}
        ) {
            it.data
        }
    }

    /**
     * Query nodes by node Id
     */
    fun findNodeById(nodeId: String): GraphQLCall<NodeByIdResponse, Node> {
        val param = NodeByIdParam(id = nodeId)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<NodeByIdResponse>>() {}) {
            it.result
        }
    }

    /**
     * Add a node
     */
    fun addNode(param: AddNodeV2Param): HttpCall<RestfulResponse<Node>, Node> {
        return client.createHttpPostCall(
            "${client.host}/api/v2/orgs/${param.orgId}/nodes",
            GsonBuilder().create().toJson(param),
            object : TypeToken<RestfulResponse<Node>> () {}
        ) {
            it.data
        }
    }

    /**
     * Update a node
     */
    fun updateNode(param: UpdateNodeParam): GraphQLCall<UpdateNodeResponse, Node> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<UpdateNodeResponse>>() {}) {
            it.result
        }
    }

    /**
     * Delete a node
     */
    fun deleteNode(param: DeleteNodeParam): GraphQLCall<DeleteNodeResponse, CommonMessage> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<DeleteNodeResponse>>() {}) {
            it.result
        }
    }

    /**
     * Move a node
     */
    fun moveNode(orgId: String, nodeId: String, targetParentId: String): GraphQLCall<MoveNodeResponse, Org> {
        val param = MoveNodeParam(orgId, nodeId, targetParentId);
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<MoveNodeResponse>>() {}) {
            it.result
        }
    }

    /**
     * Check whether it is the root node
     */
    fun isRootNode(nodeId: String, orgId: String): GraphQLCall<IsRootNodeResponse, Boolean> {
        val param = IsRootNodeParam(nodeId = nodeId, orgId = orgId)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<IsRootNodeResponse>>() {}) {
            it.result
        }
    }

    /**
     * Get the root node
     */
    fun rootNode(param: RootNodeParam): GraphQLCall<RootNodeResponse, Node> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RootNodeResponse>>() {}) {
            it.result
        }
    }

    /**
     * Get child nodes list
     */
    fun listChildren(orgId: String, nodeId: String): GraphQLCall<ChildrenNodesResponse, List<Node>> {
        val param = ChildrenNodesParam(orgId = orgId, nodeId = nodeId)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<ChildrenNodesResponse>>() {}) {
            it.result
        }
    }

    /**
     * Fuzzy search organization node
     */
    fun searchNodes(param: SearchNodesParam): HttpCall<RestfulResponse<List<Node>>, List<Node>> {
        return client.createHttpGetCall(
            "${client.host}/api/v2/orgs/nodes/search?q=${param.keyword}",
            object : TypeToken<RestfulResponse<List<Node>>> () {}
        ) {
            it.data
        }
    }

    /**
     * Add members
     */
    fun addMembers(nodeId: String, userIds: List<String>): HttpCall<RestfulResponse<Node>, Node> {
        val options = RestAddMembersParams(nodeId, userIds)
        return client.createHttpPostCall(
            "${client.host}/api/v2/orgs/nodes/${options.nodeId}/members",
            GsonBuilder().create().toJson(options),
            object : TypeToken<RestfulResponse<Node>> () {}
        ) {
            it.data
        }
    }

    /**
     * Get node members
     */
    fun listMembers(param: NodeByIdWithMembersParam): GraphQLCall<NodeByIdWithMembersResponse, Node> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<NodeByIdWithMembersResponse>>() {}) {
            it.result
        }
    }

    /**
     * Delete members
     */
    fun removeMembers(nodeId: String, userIds: List<String>): GraphQLCall<RemoveMemberResponse, Node> {
        val param = RemoveMemberParam(nodeId = nodeId, userIds = userIds)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RemoveMemberResponse>>() {}) {
            it.result
        }
    }

    /**
     * Get Organization details
     */
    fun findById(id: String): GraphQLCall<OrgResponse, Org> {
        val param = OrgParam(id)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<OrgResponse>>() {}) {
            it.result
        }
    }

    /**
     * Import org by a JSON tree structure
     */
    fun importByJson(json: String): HttpCall<RestfulResponse<OrgNode>, OrgNode> {
        val gson = Gson()
        val node = gson.fromJson(json, Map::class.java)
        val data = mapOf("filetype" to "json", "file" to node)

        val dataStr = gson.toJson(data)
        return client.createHttpPostCall(
            "${client.host}/api/v2/orgs/import",
            dataStr,
            object : TypeToken<RestfulResponse<OrgNode>>() {}) {
            it.data
        }
    }

    /**
     * Export all organization data
     */
    fun exportAll(): HttpCall<RestfulResponse<List<OrgNode>>, List<OrgNode>> {
        return client.createHttpGetCall(
            "${client.host}/api/v2/orgs/export",
            object : TypeToken<RestfulResponse<List<OrgNode>>>() {}) {
            it.data
        }
    }

    /**
     * Import an organization's data
     */
    fun exportByOrgId(orgId: String): HttpCall<RestfulResponse<OrgNode>, OrgNode> {
        return client.createHttpGetCall(
            "${client.host}/api/v2/orgs/export?org_id=${orgId}",
            object : TypeToken<RestfulResponse<OrgNode>>() {}) {
            it.data
        }
    }

    fun listAuthorizedResourcesByNodeId(
        param: ListNodeByIdAuthorizedResourcesParam
    ): GraphQLCall<ListNodeByCodeAuthorizedResourcesResponse, Node> {
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<ListNodeByCodeAuthorizedResourcesResponse>>() {}
        ) { it.result }
    }
}
