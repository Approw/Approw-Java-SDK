package com.approw.core.mgmt

import com.approw.core.BaseClient
import com.approw.core.graphql.GraphQLCall
import com.approw.core.graphql.GraphQLRequest
import com.approw.core.graphql.GraphQLResponse
import com.approw.core.http.HttpCall
import com.approw.core.types.*
import com.auth0.jwt.JWT
import com.google.gson.reflect.TypeToken
import java.util.*

class ManagementClient(userPoolId: String, secret: String) : BaseClient() {

    init {
        this.userPoolId = userPoolId

        this.secret = secret
    }


    override fun <TData, TResult> createGraphQLCall(
        request: GraphQLRequest,
        typeToken: TypeToken<GraphQLResponse<TData>>,
        resolver: (data: TData) -> TResult
    ): GraphQLCall<TData, TResult> {
        this.checkToken()
        return super.createGraphQLCall(request, typeToken, resolver)
    }

    override fun <TData, TResult> createHttpGetCall(
        url: String,
        typeToken: TypeToken<TData>,
        resolver: (data: TData) -> TResult
    ): HttpCall<TData, TResult> {
        this.checkToken()
        return super.createHttpGetCall(url, typeToken, resolver)
    }

    override fun <TData, TResult> createHttpDeleteCall(
        url: String,
        typeToken: TypeToken<TData>,
        resolver: (data: TData) -> TResult
    ): HttpCall<TData, TResult> {
        this.checkToken()
        return super.createHttpDeleteCall(url, typeToken, resolver)
    }

    override fun <TData, TResult> createHttpPatchCall(
        url: String,
        body: String,
        typeToken: TypeToken<TData>,
        resolver: (data: TData) -> TResult
    ): HttpCall<TData, TResult> {
        this.checkToken()
        return super.createHttpPatchCall(url, body, typeToken, resolver)
    }

    override fun <TData, TResult> createHttpPostCall(
        url: String,
        body: String,
        typeToken: TypeToken<TData>,
        resolver: (data: TData) -> TResult
    ): HttpCall<TData, TResult> {
        this.checkToken()
        return super.createHttpPostCall(url, body, typeToken, resolver)
    }

    override fun <TData, TResult> createHttpPutCall(
        url: String,
        body: String,
        typeToken: TypeToken<TData>,
        resolver: (data: TData) -> TResult
    ): HttpCall<TData, TResult> {
        this.checkToken()
        return super.createHttpPutCall(url, body, typeToken, resolver)
    }

    /**
     * Get the administrator access token, and you can use other interfaces after obtaining it successfully
     */
    fun requestToken(): GraphQLCall<AccessTokenResponse, AccessTokenRes> {
        val param = AccessTokenParam(userPoolId!!, secret!!)
        return super@ManagementClient.createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<AccessTokenResponse>>() {}) {
            token = it.result.accessToken!!
            accessTokenExpiresAt = JWT.decode(token).claims["exp"]?.asLong()?.times(1000)
            return@createGraphQLCall it.result
        }
    }

    private fun checkToken() {
        if (this.token == null)
            this.requestToken().execute()
        if (accessTokenExpiresAt!! < Date().time + 3600 * 1000)
            this.requestToken().execute()
    }

    /**
     * Check login status
     */
    fun checkLoginStatus(param: CheckLoginStatusParam): GraphQLCall<CheckLoginStatusResponse, JwtTokenStatus> {
        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<CheckLoginStatusResponse>>() {}) {
            it.result
        }
    }

    /**
     * Get UsersManagementClient
     */
    fun users(): UsersManagementClient {
        return UsersManagementClient(this)
    }

    /**
     * Get RolesManagementClient
     */
    fun roles(): RolesManagementClient {
        return RolesManagementClient(this)
    }

    /**
     * Get AclManagementClient
     */
    fun acl(): AclManagementClient {
        return AclManagementClient(this)
    }

    /**
     * Get UdfManagementClient
     */
    fun udf(): UdfManagementClient {
        return UdfManagementClient(this)
    }

    /**
     * Get PoliciesManagementClient
     */
    fun policies(): PoliciesManagementClient {
        return PoliciesManagementClient(this)
    }

    /**
     * Get UserpoolManagementClient
     */
    fun userpool(): UserpoolManagementClient {
        return UserpoolManagementClient(this)
    }

    /**
     * Get WhitelistManagementClient
     */
    fun whitelist(): WhitelistManagementClient {
        return WhitelistManagementClient(this)
    }

    /**
     * Get GroupsManagementClient
     */
    fun group(): GroupsManagementClient {
        return GroupsManagementClient(this)
    }

    /**
     * Get OrgManagementClient
     */
    fun org(): OrgManagementClient {
        return OrgManagementClient(this)
    }

    /**
     * Get ApplicationManagementClient
     */
    fun application(): ApplicationManagementClient {
        return ApplicationManagementClient(this)
    }

    /**
     * Get StatisticsManagementClient
     */
    fun statistics(): StatisticsManagementClient {
        return StatisticsManagementClient(this)
    }

    fun mfa(): MFAManagementClient {
        return MFAManagementClient(this)
    }
}
