package com.approw.core.mgmt

import com.approw.core.graphql.GraphQLCall
import com.approw.core.http.HttpCall
import com.approw.core.types.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Application Management
 */
class ApplicationManagementClient(private val client: ManagementClient) {

    private val acl: AclManagementClient = client.acl()
    private val role: RolesManagementClient = client.roles()

    fun create(
        options: CreateAppParams
    ): HttpCall<RestfulResponse<Application>, Application> {
        val url = "${this.client.host}/api/v2/applications"

        return client.createHttpPostCall(
            url,
            Gson().toJson(options),
            object : TypeToken<RestfulResponse<Application>>() {}
        ) { it.data }
    }

    fun delete(
        appId: String
    ): HttpCall<RestfulResponse<Boolean>, Boolean> {
        val url = "${this.client.host}/api/v2/applications/${appId}"

        return client.createHttpDeleteCall(
            url,
            object : TypeToken<RestfulResponse<Boolean>>() {}
        ) { it.code == 200 }
    }

    /**
     * Get application list
     */
    @JvmOverloads
    @Deprecated("Method deprecated, please use ApplicationManagementClient.list(page, limit) instead")
    fun listApplications(
        page: Int? = 1,
        limit: Int? = 10
    ): HttpCall<RestfulResponse<ListApplicationResponse>, List<Application>> {
        return this.list(page, limit)
    }

    /**
     * Get application list
     */
    @JvmOverloads
    fun list(
        page: Int? = 1,
        limit: Int? = 10
    ): HttpCall<RestfulResponse<ListApplicationResponse>, List<Application>> {
        return client.createHttpGetCall(
            "${client.host}/api/v2/applications?limit=$limit&page=$page",
            object : TypeToken<RestfulResponse<ListApplicationResponse>>() {}) {
            it.data.list
        }
    }

    /**
     * Get application details
     */
    @Deprecated("Method deprecated, please use ApplicationManagementClient.finById(appId) instead")
    fun detail(appId: String): HttpCall<RestfulResponse<Application>, Application> {
        return this.findById(appId)
    }

    fun findById(appId: String): HttpCall<RestfulResponse<Application>, Application> {
        return client.createHttpGetCall(
            "${client.host}/api/v2/applications/${appId}",
            object : TypeToken<RestfulResponse<Application>>() {}) {
            it.data
        }
    }

    /**
     * refresh app secret
     */
    fun refreshApplicationSecret(
        appId: String
    ): HttpCall<RestfulResponse<Application>, Application> {
        val url = "${client.host}/api/v2/application/${appId}/refresh-secret"

        return this.client.createHttpPatchCall(
            url,
            "",
            object : TypeToken<RestfulResponse<Application>>() {}) { it.data }
    }

    /**
     * check active (logged in) users in the application
     */
    fun activeUsers(
        option: IActiveUsersParam
    ): HttpCall<RestfulResponse<Pagination<ActiveUser>>, Pagination<ActiveUser>> {
        val url =
            "${client.host}/api/v2/applications/${option.appId}/active-users?page=${option.page}&limit=${option.limit}"

        return this.client.createHttpGetCall(
            url,
            object : TypeToken<RestfulResponse<Pagination<ActiveUser>>>() {}) { it.data }

    }

    fun listResources(
        options: ListResourcesParams
    ): HttpCall<RestfulResponse<Pagination<IResourceResponse>>, Pagination<IResourceResponse>> {
        val (appId, type, limit, page) = options
        return acl.listResources(appId, type, limit, page)
    }

    fun createResource(
        appId: String,
        options: ResourceOptionsParams
    ): HttpCall<RestfulResponse<IResourceResponse>, IResourceResponse> {
        return acl.createResource(
            IResourceDto(
                options.code,
                options.type,
                options.description,
                options.actions,
                appId
            )
        )
    }

    fun findResourceByCode(
        appId: String,
        code: String
    ): HttpCall<RestfulResponse<IResourceResponse>, IResourceResponse> {
        return acl.findResourceByCode(code, appId)
    }

    fun updateResource(
        appId: String,
        options: ResourceOptionsParams
    ): HttpCall<RestfulResponse<IResourceResponse>, IResourceResponse> {
        return acl.updateResource(
            options.code,
            IResourceDto(
                code = options.code,
                type = options.type,
                description = options.description,
                actions = options.actions,
                namespace = appId
            )
        )
    }

    fun deleteResource(
        appId: String,
        code: String
    ): HttpCall<RestfulResponse<Boolean>, Boolean> {
        return acl.deleteResource(code, appId)
    }

    @JvmOverloads
    fun getAccessPolicies(
        appId: String,
        options: PageOptions = PageOptions()
    ): HttpCall<RestfulResponse<Pagination<IApplicationAccessPolicies>>, Pagination<IApplicationAccessPolicies>> {
        return acl.getApplicationAccessPolicies(
            IAppAccessPolicyQueryFilter(
                appId,
                options.page,
                options.limit
            )
        )
    }

    fun enableAccessPolicy(
        appId: String,
        options: IAccessPolicyParams
    ): HttpCall<RestfulResponse<Boolean>, Boolean> {
        return acl.enableApplicationAccessPolicy(
            IAppAccessPolicy(
                appId = appId,
                targetType = options.targetType,
                targetIdentifiers = options.targetIdentifiers,
                inheritByChildren = options.inheritByChildren,
                namespace = appId
            )
        )
    }

    fun disableAccessPolicy(
        appId: String,
        options: IAccessPolicyParams
    ): HttpCall<RestfulResponse<Boolean>, Boolean> {
        return acl.disableApplicationAccessPolicy(
            IAppAccessPolicy(
                appId = appId,
                targetType = options.targetType,
                targetIdentifiers = options.targetIdentifiers,
                inheritByChildren = options.inheritByChildren,
                namespace = appId
            )
        )
    }

    fun deleteAccessPolicy(
        appId: String,
        options: IAccessPolicyParams
    ): HttpCall<RestfulResponse<Boolean>, Boolean> {
        return acl.deleteApplicationAccessPolicy(
            IAppAccessPolicy(
                appId = appId,
                targetType = options.targetType,
                targetIdentifiers = options.targetIdentifiers,
                inheritByChildren = options.inheritByChildren,
                namespace = appId
            )
        )
    }

    fun allowAccess(
        appId: String,
        options: IAccessPolicyParams
    ): HttpCall<RestfulResponse<Boolean>, Boolean> {
        return acl.allowAccessApplication(
            IAppAccessPolicy(
                appId = appId,
                targetType = options.targetType,
                targetIdentifiers = options.targetIdentifiers,
                inheritByChildren = options.inheritByChildren,
                namespace = appId
            )
        )
    }

    fun denyAccess(
        appId: String,
        options: DenyAccessParams
    ): HttpCall<RestfulResponse<Boolean>, Boolean> {
        return acl.denyAccessApplication(
            IAppAccessPolicy(
                appId = appId,
                targetType = options.targetType,
                targetIdentifiers = options.targetIdentifiers,
                inheritByChildren = options.inheritByChildren,
                namespace = appId
            )
        )
    }

    fun updateDefaultAccessPolicy(
        appId: String,
        defaultStrategy: DefaultStrategy
    ): HttpCall<RestfulResponse<Application>, Application> {
        return acl.updateDefaultApplicationAccessPolicy(
            IDefaultAppAccessPolicy(appId, defaultStrategy)
        )
    }

    fun createRole(
        appId: String,
        options: CreateRoleParams
    ): HttpCall<RestfulResponse<Role>, Role> {
        return role.create(
            CreateRoleParam(code = options.code).withNamespace(appId).withDescription(options.description)
        )
    }

    fun deleteRole(
        appId: String,
        code: String
    ): GraphQLCall<DeleteRoleResponse, CommonMessage> {
        return role.delete(
            DeleteRoleParam(code).withNamespace(appId)
        )
    }

    fun deleteRoles(
        appId: String,
        codes: List<String>
    ): GraphQLCall<DeleteRolesResponse, CommonMessage> {
        return role.deleteMany(
            DeleteRolesParam(codes, appId)
        )
    }

    fun updateRole(
        appId: String,
        options: UpdateRoleParams
    ): GraphQLCall<UpdateRoleResponse, Role> {
        return role.update(
            UpdateRoleParam(code = options.code, namespace = appId)
                .withDescription(options.description)
                .withNewCode(options.newCode)
        )
    }

    fun findRole(
        appId: String,
        code: String
    ): GraphQLCall<RoleResponse, Role> {
        return role.findByCode(RoleParam(code).withNamespace(appId))
    }

    @JvmOverloads
    fun getRoles(
        appId: String,
        options: PageOptions = PageOptions()
    ): HttpCall<RestfulResponse<PaginatedRoles>, PaginatedRoles> {
        return role.list(
            RolesParam(namespace = appId).withPage(options.page).withLimit(options.limit)
        )
    }

    fun getUsersByRoleCode(
        appId: String,
        code: String
    ): GraphQLCall<RoleWithUsersResponse, PaginatedUsers> {
        return role.listUsers(RoleWithUsersParam(code).withNamespace(appId))
    }

    fun addUsersToRole(
        appId: String,
        code: String,
        userIds: List<String>
    ): GraphQLCall<AssignRoleResponse, CommonMessage> {
        return role.addUsers(
            AssignRoleParam(
                roleCode = code,
                userIds = userIds,
                namespace = appId
            )
        )
    }

    fun removeUsersFromRole(
        appId: String,
        code: String,
        userIds: List<String>
    ): GraphQLCall<RevokeRoleResponse, CommonMessage> {
        return role.removeUsers(
            RevokeRoleParam(
                roleCode = code,
                userIds = userIds,
                namespace = appId
            )
        )
    }

    @JvmOverloads
    fun listAuthorizedResourcesByRole(
        appId: String,
        code: String,
        resourceType: ResourceType? = null
    ): GraphQLCall<ListRoleAuthorizedResourcesResponse, PaginatedAuthorizedResources?> {
        return role.listAuthorizedResources(
            ListRoleAuthorizedResourcesParam(
                code = code,
                namespace = appId
            ).withResourceType(resourceType.toString())
        )
    }

    fun createAgreement(
        appId: String,
        agreement: AgreementParams
    ): HttpCall<RestfulResponse<AgreementDetail>, AgreementDetail> {
        val url = "${client.host}/api/v2/applications/${appId}/agreements"

        return client.createHttpPostCall(
            url,
            Gson().toJson(agreement),
            object : TypeToken<RestfulResponse<AgreementDetail>>() {}
        ) { it.data }
    }

    fun listAgreement(appId: String): HttpCall<RestfulResponse<Pagination<AgreementDetail>>, Pagination<AgreementDetail>> {
        val url = "${client.host}/api/v2/applications/${appId}/agreements"

        return client.createHttpGetCall(
            url,
            object : TypeToken<RestfulResponse<Pagination<AgreementDetail>>>() {}
        ) { it.data }
    }

    fun modifyAgreement(
        appId: String,
        agreementId: String,
        updates: AgreementParams
    ): HttpCall<RestfulResponse<AgreementDetail>, AgreementDetail> {
        val url = "${client.host}/api/v2/applications/${appId}/agreements/${agreementId}"

        return client.createHttpPutCall(
            url,
            Gson().toJson(updates),
            object : TypeToken<RestfulResponse<AgreementDetail>>() {}
        ) { it.data }
    }

    fun deleteAgreement(
        appId: String,
        agreementId: String
    ): HttpCall<RestfulResponse<Boolean>, Boolean> {
        val url = "${client.host}/api/v2/applications/${appId}/agreements/${agreementId}"

        return client.createHttpDeleteCall(
            url,
            object : TypeToken<RestfulResponse<Boolean>>() {}
        ) { it.code == 200 }
    }

    fun sortAgreement(
        appId: String,
        order: List<String>
    ): HttpCall<RestfulResponse<Boolean>, Boolean> {
        val url = "${client.host}/api/v2/applications/${appId}/agreements/sort"

        return client.createHttpPostCall(
            url,
            "{ ids: ${Gson().toJson(order)} }",
            object : TypeToken<RestfulResponse<Boolean>>() {}
        ) { it.code == 200 }
    }

    @JvmOverloads
    fun activeUsers(
        appId: String,
        options: PageOptions? = PageOptions()
    ): HttpCall<RestfulResponse<ActiveUser>, ActiveUser> {
        val url =
            "${client.host}/api/v2/applications/${appId}/active-users?page=${options?.page}&limit=${options?.limit}"

        return client.createHttpGetCall(
            url,
            object : TypeToken<RestfulResponse<ActiveUser>>() {}
        ) { it.data }
    }
}
