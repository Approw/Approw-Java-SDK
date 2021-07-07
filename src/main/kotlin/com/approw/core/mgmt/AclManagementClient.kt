package com.approw.core.mgmt

import com.approw.core.graphql.GraphQLCall
import com.approw.core.graphql.GraphQLResponse
import com.approw.core.http.HttpCall
import com.approw.core.types.*
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * Access Management
 */
class AclManagementClient(private val client: ManagementClient) {
    /**
     * Allow a user to perform an operation on a resource
     */
    @JvmOverloads
    fun allow(
        resource: String,
        action: String,
        userId: String? = null,
        role: String? = null
    ): GraphQLCall<AllowResponse, CommonMessage> {
        val param = AllowParam(resource, action).withUserId(userId).withRoleCode(role)
        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<AllowResponse>>() {}) {
            it.result
        }
    }

    /**
     * Check whether a user has operation authority of a resource
     */
    fun isAllowed(userId: String, resource: String, action: String): GraphQLCall<IsActionAllowedResponse, Boolean> {
        val param = IsActionAllowedParam(resource, action, userId)

        return client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<IsActionAllowedResponse>>() {}) {
            it.result
        }
    }

    /**
     * Access to resources
     */
    @JvmOverloads
    fun getResources(
        namespaceCode: String? = null,
        type: ResourceType? = null,
        limit: Number? = 10,
        page: Number? = 1
    ): HttpCall<RestfulResponse<Pagination<IResourceResponse>>, Pagination<IResourceResponse>> {
        var url = "${client.host}/api/v2/resources?limit=$limit&page=$page"

        url += if (namespaceCode != null) "&namespaceCode=${namespaceCode}" else ""
        url += if (type != null) "&type=${type}" else ""

        println(url)
        return this.client.createHttpGetCall(
            url,
            object : TypeToken<RestfulResponse<Pagination<IResourceResponse>>>() {}) { it.data }

    }

    fun listResources(params: AclListResourcesParams): HttpCall<RestfulResponse<Pagination<IResourceResponse>>, Pagination<IResourceResponse>> {
        var (namespaceCode, type, limit, page, fetchAll) = params
        if (fetchAll) limit = -1
        return this.listResources(namespaceCode, type, limit, page)
    }

    /**
     * Create resources
     */
    fun createResource(options: IResourceDto): HttpCall<RestfulResponse<IResourceResponse>, IResourceResponse> {

        return this.client.createHttpPostCall(
            "${client.host}/api/v2/resources",
            GsonBuilder().create().toJson(options),
            object : TypeToken<RestfulResponse<IResourceResponse>>() {}) { it.data }
    }

    @JvmOverloads
    fun findResourceByCode(
        code: String,
        namespace: String? = null
    ): HttpCall<RestfulResponse<IResourceResponse>, IResourceResponse> {
        var url = "${client.host}/api/v2/resources/by-code/$code"

        url += if (namespace != null) "?namespace=$namespace" else ""

        return this.client.createHttpGetCall(
            url,
            object : TypeToken<RestfulResponse<IResourceResponse>>() {}) { it.data }
    }

    /**
     * Update resources
     */
    fun updateResource(
        code: String,
        options: IResourceDto
    ): HttpCall<RestfulResponse<IResourceResponse>, IResourceResponse> {

        val data = Gson().toJson(options);
        return this.client.createHttpPostCall(
            "${client.host}/api/v2/resources/$code",
            Gson().toJson(options),
            object : TypeToken<RestfulResponse<IResourceResponse>>() {}) { it.data }
    }

    /**
     * Delete resources
     */
    fun deleteResource(code: String, namespaceCode: String): HttpCall<RestfulResponse<Boolean>, Boolean> {
        return this.client.createHttpDeleteCall(
            "${client.host}/api/v2/resources/$code?namespaceCode=$namespaceCode",
            object : TypeToken<RestfulResponse<Boolean>>() {}) { it.code == 200 }
    }

    /**
     * Get application access policy
     */
    fun getApplicationAccessPolicies(options: IAppAccessPolicyQueryFilter):
            HttpCall<RestfulResponse<Pagination<IApplicationAccessPolicies>>, Pagination<IApplicationAccessPolicies>> {
        val url =
            "${client.host}/api/v2/applications/${options.appId}/authorization/records?limit=${options.limit}&page=${options.page}"

        return this.client.createHttpGetCall(
            url,
            object : TypeToken<RestfulResponse<Pagination<IApplicationAccessPolicies>>>() {}) { it.data }
    }

    /**
     * Enable application access policy
     */
    fun enableApplicationAccessPolicy(options: IAppAccessPolicy): HttpCall<RestfulResponse<Boolean>, Boolean> {
        val url =
            "${client.host}/api/v2/applications/${options.appId}/authorization/enable-effect"

        return this.client.createHttpPostCall(
            url,
            GsonBuilder().create().toJson(options),
            object : TypeToken<RestfulResponse<Boolean>>() {}) { it.code == 200 }
    }

    /**
     * Disable application access policy
     */
    fun disableApplicationAccessPolicy(options: IAppAccessPolicy): HttpCall<RestfulResponse<Boolean>, Boolean> {
        val url =
            "${client.host}/api/v2/applications/${options.appId}/authorization/disable-effect"

        return this.client.createHttpPostCall(
            url,
            GsonBuilder().create().toJson(options),
            object : TypeToken<RestfulResponse<Boolean>>() {}) { it.code == 200 }
    }

    /**
     * Delete application access policy
     */
    fun deleteApplicationAccessPolicy(options: IAppAccessPolicy): HttpCall<RestfulResponse<Boolean>, Boolean> {
        val url =
            "${client.host}/api/v2/applications/${options.appId}/authorization/revoke"

        return this.client.createHttpPostCall(
            url,
            GsonBuilder().create().toJson(options),
            object : TypeToken<RestfulResponse<Boolean>>() {}) { it.code == 200 }
    }

    /**
     * Configure the policy of "Allow subjects (users, roles, groups, organizational nodes) to access applications"
     */
    fun allowAccessApplication(options: IAppAccessPolicy): HttpCall<RestfulResponse<Boolean>, Boolean> {
        val url = "${client.host}/api/v2/applications/${options.appId}/authorization/allow"

        return this.client.createHttpPostCall(
            url,
            GsonBuilder().create().toJson(options),
            object : TypeToken<RestfulResponse<Boolean>>() {}) { it.code == 200 }

    }

    /**
     * Configure the policy of "Deny subjects (users, roles, groups, organizational nodes) to access applications"
     */
    fun denyAccessApplication(options: IAppAccessPolicy): HttpCall<RestfulResponse<Boolean>, Boolean> {
        val url = "${client.host}/api/v2/applications/${options.appId}/authorization/deny"

        return this.client.createHttpPostCall(
            url,
            GsonBuilder().create().toJson(options),
            object : TypeToken<RestfulResponse<Boolean>>() {}) { it.code == 200 }

    }

    /**
     * Change the default application access policy (by default all users are denied access to applications, and all users are allowed to access applications by default)
     */
    fun updateDefaultApplicationAccessPolicy(options: IDefaultAppAccessPolicy): HttpCall<RestfulResponse<Application>, Application> {
        val url = "${client.host}/api/v2/applications/${options.appId}"

        return this.client.createHttpPostCall(
            url,
            GsonBuilder().create().toJson(options),
            object : TypeToken<RestfulResponse<Application>>() {}) { it.data }

    }

    /**
     * grant a (type of) resource to user/role/group/org and can specify different operation access
     */
    fun authorizeResource(
        namespace: String,
        resource: String,
        opts: List<AuthorizeResourceOptInput>
    ): GraphQLCall<AuthorizeResourceResponse, CommonMessage> {
        val param = AuthorizeResourceParam(namespace).withResource(resource).withOpts(opts)

        return this.client.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<AuthorizeResourceResponse>>() {}) { it.result }
    }

    /**
     * refresh programmatic access account key
     * @param options.id programmatic access account ID
     * @param options.secret programmatic access account Secret
     * @returns ProgrammaticAccessAccount
     */
    fun refreshProgrammaticAccessAccountSecret(
        options: IProgrammaticAccessAccountProps
    ): HttpCall<RestfulResponse<ProgrammaticAccessAccount>, ProgrammaticAccessAccount> {
        val url = "${client.host}/api/v2/applications/programmatic-access-accounts"

        if (options.secret == null) options.secret = randomString(32)

        return this.client.createHttpPatchCall(
            url,
            GsonBuilder().create().toJson(options),
            object : TypeToken<RestfulResponse<ProgrammaticAccessAccount>>() {}) { it.data }

    }

    /**
     * programmatic access account list
     * @param options.appId 
     * @param options.page current page
     * @param options.limit records per page
     * @returns Pagination<ProgrammaticAccessAccount>
     */
    fun programmaticAccessAccountList(
        options: IProgrammaticAccessAccountListProps
    ): HttpCall<RestfulResponse<Pagination<ProgrammaticAccessAccount>>, Pagination<ProgrammaticAccessAccount>> {

        val url =
            "${client.host}/api/v2/applications/${options.appId}/programmatic-access-accounts?limit=${options.limit}&page=${options.page}"

        return this.client.createHttpGetCall(
            url,
            object : TypeToken<RestfulResponse<Pagination<ProgrammaticAccessAccount>>>() {}) { it.data }
    }

    /**
     * add a programmatic access account
     *
     * @param options.appId 
     * @param options.tokenLifetime AccessToken expire time (seconds)
     * @param options.remarks description
     * @returns ProgrammaticAccessAccount
     */
    fun createProgrammaticAccessAccount(
        options: ICreateProgrammaticAccessAccountProps
    ): HttpCall<RestfulResponse<ProgrammaticAccessAccount>, ProgrammaticAccessAccount> {
        val url =
            "${client.host}/api/v2/applications/${options.appId}/programmatic-access-accounts"

        return this.client.createHttpPostCall(
            url,
            GsonBuilder().create().toJson(options),
            object : TypeToken<RestfulResponse<ProgrammaticAccessAccount>>() {})
        { it.data }

    }

    /**
     * delete programmatic access account
     * @param programmaticAccessAccountId programmatic access account ID
     * @returns Boolean
     */
    fun deleteProgrammaticAccessAccount(
        programmaticAccessAccountId: String
    ): HttpCall<RestfulResponse<Boolean>, Boolean> {
        val url =
            "${client.host}/api/v2/applications/programmatic-access-accounts?id=${programmaticAccessAccountId}"

        return this.client.createHttpDeleteCall(
            url,
            object : TypeToken<RestfulResponse<Boolean>>() {}) { it.code == 200 }

    }

    /**
     * enable programmatic access account
     * @param programmaticAccessAccountId programmatic access account ID
     * @returns ProgrammaticAccessAccount
     */
    fun enableProgrammaticAccessAccount(
        programmaticAccessAccountId: String
    ): HttpCall<RestfulResponse<ProgrammaticAccessAccount>, ProgrammaticAccessAccount> {
        val url =
            "${client.host}/api/v2/applications/programmatic-access-accounts"

        return this.client.createHttpPatchCall(
            url,
            GsonBuilder().create().toJson(IEnableProgrammaticAccessAccount(programmaticAccessAccountId, true)),
            object : TypeToken<RestfulResponse<ProgrammaticAccessAccount>>() {}) { it.data }

    }

    /**
     * disable programmatic access account
     * @param programmaticAccessAccountId programmatic access account ID
     * @returns ProgrammaticAccessAccount
     */
    fun disableProgrammaticAccessAccount(
        programmaticAccessAccountId: String
    ): HttpCall<RestfulResponse<ProgrammaticAccessAccount>, ProgrammaticAccessAccount> {
        val url =
            "${client.host}/api/v2/applications/programmatic-access-accounts"

        return this.client.createHttpPatchCall(
            url,
            GsonBuilder().create().toJson(IEnableProgrammaticAccessAccount(programmaticAccessAccountId, false)),
            object : TypeToken<RestfulResponse<ProgrammaticAccessAccount>>() {}) { it.data }

    }

    /**
     * create a permission group
     */
    @JvmOverloads
    fun createNamespace(
        code: String,
        name: String,
        description: String? = null
    ): HttpCall<RestfulResponse<ResourceNamespace>, ResourceNamespace> {
        val url = "${client.host}/api/v2/resource-namespace/${client.userPoolId}"
        return this.client.createHttpPostCall(
            url,
            GsonBuilder().create().toJson(CreateNamespaceBody(code, name, description)),
            object : TypeToken<RestfulResponse<ResourceNamespace>>() {}) { it.data }
    }

    /**
     * get permission group list
     */
    @JvmOverloads
    fun listNamespaces(
        page: Int? = 1,
        limit: Int? = 10
    ): HttpCall<RestfulResponse<Pagination<ResourceNamespace>>, Pagination<ResourceNamespace>> {
        val url = "${client.host}/api/v2/resource-namespace/${client.userPoolId}?limit=$limit&page=$page"
        return this.client.createHttpGetCall(
            url,
            object : TypeToken<RestfulResponse<Pagination<ResourceNamespace>>>() {}) { it.data }
    }

    /**
     * update namespace
     */
    fun updateNamespace(
        code: String,
        updates: UpdateNamespaceParams
    ): HttpCall<RestfulResponse<ResourceNamespace>, ResourceNamespace> {
        val url = "${client.host}/api/v2/resource-namespace/${client.userPoolId}/code/${code}"

        return client.createHttpPutCall(
            url,
            Gson().toJson(updates),
            object : TypeToken<RestfulResponse<ResourceNamespace>>() {}
        ) { it.data }
    }

    fun deleteNamespace(code: String): HttpCall<RestfulResponse<Boolean>, Boolean> {
        val url = "${this.client.host}/api/v2/resource-namespace/${this.client.userPoolId}/code/${code}"

        return client.createHttpDeleteCall(
            url,
            object : TypeToken<RestfulResponse<Boolean>>() {}
        ) { it.code == 200 }
    }

    /**
     * get authorized resources of a (type of) user/group/role/org
     */
    fun getAuthorizedTargets(
        options: AuthorizedTargetsParam
    ): GraphQLCall<AuthorizedTargetsResponse, PaginatedAuthorizedTargets> {

        return client.createGraphQLCall(
            options.createRequest(),
            object : TypeToken<GraphQLResponse<AuthorizedTargetsResponse>>() {}
        ) { it.result }
    }
}
