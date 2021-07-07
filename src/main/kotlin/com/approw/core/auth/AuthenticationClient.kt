package com.approw.core.auth

import com.approw.core.BaseClient
import com.approw.core.graphql.GraphQLCall
import com.approw.core.graphql.GraphQLResponse
import com.approw.core.http.HttpCall
import com.approw.core.types.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.FormBody
import okhttp3.Request
import java.io.IOException
import java.net.http.HttpRequest
import java.nio.Buffer
import java.util.*
import java.util.regex.Pattern

class AuthenticationClient : BaseClient {
    constructor(userPoolId: String) {
        this.userPoolId = userPoolId
    }

    constructor(appId: String, appHost: String) {
        this.appId = appId

        this.host = appHost
    }

    private var user: User? = null

    fun mfa(): MfaAuthenticationClient {
        return MfaAuthenticationClient(this)
    }

    /**
     * get current user profile
     */
    fun getCurrentUser(): GraphQLCall<UserResponse, User> {
        val param = UserParam()
        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<UserResponse>>() {}) {
            user = it.result
            token = it.result.token ?: token
            return@createGraphQLCall it.result
        }
    }

    fun checkLoggedIn(): Boolean {
        if (this.user == null) return false

        if (this.token == null) return false

        return true
    }

    /**
     * Log in through WeChat to get user information
     */
    @JvmOverloads
    fun loginByWechat(
        code: String,
        country: String? = null,
        lang: String? = null,
        state: String? = null
    ): HttpCall<RestfulResponse<User>, User> {
        var url = "$host/connection/social/wechat:mobile/$userPoolId/callback?code=$code"
        url += if (country != null) "&country=$country" else ""
        url += if (lang != null) "&lang=$lang" else ""
        url += if (state != null) "&state=$state" else ""
        url += if (appId != null) "&app_id=$appId" else ""

        return createHttpGetCall(url, object : TypeToken<RestfulResponse<User>>() {}) {
            user = it.data
            token = it.data.token ?: token
            it.data
        }
    }

    /**
     * Login with username and password
     */
    fun loginByUsername(input: LoginByUsernameInput): GraphQLCall<LoginByUsernameResponse, User> {
        val param = LoginByUsernameParam(input)
        param.input.password = encrypt(param.input.password)

        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<LoginByUsernameResponse>>() {}) {
            user = it.result
            token = it.result.token ?: token
            return@createGraphQLCall it.result
        }
    }

    /**
     * Login with email password
     */
    fun loginByEmail(input: LoginByEmailInput): GraphQLCall<LoginByEmailResponse, User> {
        val param = LoginByEmailParam(input)
        param.input.password = encrypt(param.input.password)

        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<LoginByEmailResponse>>() {}) {
            user = it.result
            token = it.result.token ?: token
            return@createGraphQLCall it.result
        }
    }

    /**
     * Login with mobile phone number and verification code
     */
    fun loginByPhoneCode(input: LoginByPhoneCodeInput): GraphQLCall<LoginByPhoneCodeResponse, User> {
        val param = LoginByPhoneCodeParam(input)

        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<LoginByPhoneCodeResponse>>() {}) {
            user = it.result
            token = it.result.token ?: token
            return@createGraphQLCall it.result
        }
    }

    /**
     * Login with mobile phone number and password
     */
    fun loginByPhonePassword(input: LoginByPhonePasswordInput): GraphQLCall<LoginByPhonePasswordResponse, User> {
        val param = LoginByPhonePasswordParam(input)
        param.input.password = encrypt(param.input.password)

        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<LoginByPhonePasswordResponse>>() {}) {
            user = it.result
            token = it.result.token ?: token
            return@createGraphQLCall it.result
        }
    }

    fun loginBySubAccount(
        options: LoginBySubAccountParam
    ): GraphQLCall<LoginBySubAccountResponse, User> {
        return createGraphQLCall(
            options.createRequest(),
            object : TypeToken<GraphQLResponse<LoginBySubAccountResponse>>() {}
        ) {
            user = it.result
            token = it.result.token ?: token
            return@createGraphQLCall it.result
        }
    }

    /**
     * Check password strength
     */
    fun checkPasswordStrength(password: String): GraphQLCall<CheckPasswordStrengthResponse, CheckPasswordStrengthResult> {
        val param = CheckPasswordStrengthParam(password);
        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<CheckPasswordStrengthResponse>>() {}) {
            return@createGraphQLCall it.result
        }
    }

    /**
     * Register by username
     */
    fun registerByUsername(input: RegisterByUsernameInput): GraphQLCall<RegisterByUsernameResponse, User> {
        val param = RegisterByUsernameParam(input)
        param.input.password = encrypt(param.input.password)

        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RegisterByUsernameResponse>>() {}) {
            user = it.result
            token = it.result.token ?: token
            return@createGraphQLCall it.result
        }
    }

    /**
     * Register by email
     */
    fun registerByEmail(input: RegisterByEmailInput): GraphQLCall<RegisterByEmailResponse, User> {
        val param = RegisterByEmailParam(input)
        param.input.password = encrypt(param.input.password)

        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RegisterByEmailResponse>>() {}) {
            user = it.result
            token = it.result.token ?: token
            return@createGraphQLCall it.result
        }
    }

    /**
     * Register by SMS verification code
     */
    fun registerByPhoneCode(input: RegisterByPhoneCodeInput): GraphQLCall<RegisterByPhoneCodeResponse, User> {
        val param = RegisterByPhoneCodeParam(input)
        if (param.input.password !== null) {
            param.input.password = encrypt(param.input.password!!)
        }

        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RegisterByPhoneCodeResponse>>() {}) {
            user = it.result
            token = it.result.token ?: token
            return@createGraphQLCall it.result
        }
    }

    /**
     * Send SMS verification code
     */
    fun sendSmsCode(phone: String): HttpCall<CommonMessage, CommonMessage> {
        val url = "$host/api/v2/sms/send"
        val json = "{ \"phone\": \"$phone\" }"

        return createHttpPostCall(url, json, object : TypeToken<CommonMessage>() {}) {
            if (it.code != 200) {
                throw IOException(it.message)
            } else {
                it
            }
        }
    }

    /**
     * Check current login status
     */
    fun checkLoginStatus(): GraphQLCall<CheckLoginStatusResponse, JwtTokenStatus> {
        val param = CheckLoginStatusParam(token = token)
        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<CheckLoginStatusResponse>>() {}) {
            it.result
        }
    }

    /**
     * Send email
     */
    fun sendEmail(email: String, scene: EmailScene): GraphQLCall<SendEmailResponse, CommonMessage> {
        val param = SendEmailParam(email, scene)
        return createGraphQLCall(param.createRequest(), object : TypeToken<GraphQLResponse<SendEmailResponse>>() {}) {
            it.result
        }
    }

    /**
     * Reset password by email verification code
     */
    fun resetPasswordByEmailCode(
        email: String,
        code: String,
        newPassword: String
    ): GraphQLCall<ResetPasswordResponse, CommonMessage> {
        val param = ResetPasswordParam(null, email, code, encrypt(newPassword))

        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<ResetPasswordResponse>>() {}) {
            it.result
        }
    }

    /**
     * Reset password by SMS verification code
     */
    fun resetPasswordByPhoneCode(
        phone: String,
        code: String,
        newPassword: String
    ): GraphQLCall<ResetPasswordResponse, CommonMessage> {
        val param = ResetPasswordParam(phone, null, code, encrypt(newPassword))

        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<ResetPasswordResponse>>() {}) {
            it.result
        }
    }

    /**
     * Update user information, the ID parameter is the current user ID
     */
    fun updateProfile(input: UpdateUserInput): GraphQLCall<UpdateUserResponse, User> {
        val param = UpdateUserParam(input = input)
        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<UpdateUserResponse>>() {}) {
            user = it.result
            token = it.result.token ?: token
            return@createGraphQLCall it.result
        }
    }

    /**
     * Update user password
     */
    @JvmOverloads
    fun updatePassword(newPassword: String, oldPassword: String? = null): GraphQLCall<UpdatePasswordResponse, User> {
        val param = UpdatePasswordParam(encrypt(newPassword), encrypt(oldPassword))

        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<UpdatePasswordResponse>>() {}) {
            user = it.result
            token = it.result.token ?: token
            return@createGraphQLCall it.result
        }
    }

    /**
     * Update user phone number
     */
    @JvmOverloads
    fun updatePhone(
        phone: String,
        phoneCode: String,
        oldPhone: String? = null,
        oldPhoneCode: String? = null
    ): GraphQLCall<UpdatePhoneResponse, User> {
        val param = UpdatePhoneParam(phone, phoneCode, oldPhone, oldPhoneCode)

        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<UpdatePhoneResponse>>() {}) {
            user = it.result
            token = it.result.token ?: token
            return@createGraphQLCall it.result
        }
    }

    /**
     * Update user email
     */
    @JvmOverloads
    fun updateEmail(
        email: String,
        emailCode: String,
        oldEmail: String? = null,
        oldEmailCode: String? = null
    ): GraphQLCall<UpdateEmailResponse, User> {
        val param = UpdateEmailParam(email, emailCode, oldEmail, oldEmailCode)

        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<UpdateEmailResponse>>() {}) {
            user = it.result
            token = it.result.token ?: token
            return@createGraphQLCall it.result
        }
    }

    /**
     * Bind email
     */
    fun bindEmail(email: String, emailCode: String): GraphQLCall<BindEmailResponse, User> {
        val param = BindEmailParam(email, emailCode)
        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<BindEmailResponse>>() {}) {
            user = it.result
            token = it.result.token ?: token
            return@createGraphQLCall it.result
        }
    }

    /**
     * Unbind email
     */
    fun unbindEmail(): GraphQLCall<UnbindEmailResponse, User> {
        val param = UnbindEmailParam()
        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<UnbindEmailResponse>>() {}) {
            user = it.result
            token = it.result.token ?: token
            return@createGraphQLCall it.result
        }
    }

    /**
     * Associate the account, bind the social account to the main account (mobile phone number, email account)
     * primaryUserToken: main account Token
     * secondaryUserToken: social account Token
     */
    fun linkAccount(primaryUserToken: String, secondaryUserToken: String): HttpCall<CommonMessage, CommonMessage> {
        val url = "$host/api/v2/users/link"
        val json = "{ \"primaryUserToken\": \"$primaryUserToken\", \"secondaryUserToken\": \"$secondaryUserToken\" }"

        return createHttpPostCall(url, json, object : TypeToken<CommonMessage>() {}) {
            if (it.code != 200) {
                throw IOException(it.message)
            } else {
                it
            }
        }
    }

    fun unLinkAccount(options: UnLinkAccountParam): HttpCall<RestfulResponse<Boolean>, Boolean> {
        val url = "$host/api/v2/users/unlink"

        return createHttpPostCall(
            url,
            Gson().toJson(options),
            object : TypeToken<RestfulResponse<Boolean>>() {}
        ) { it.code == 200 }
    }

    /**
     * Refresh access token
     */
    fun refreshToken(): GraphQLCall<RefreshTokenResponse, RefreshToken> {
        val param = RefreshTokenParam()
        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RefreshTokenResponse>>() {}) {
            token = it.result.token
            return@createGraphQLCall it.result
        }
    }

    /**
     * Bind mobile phone number
     */
    fun bindPhone(phone: String, phoneCode: String): GraphQLCall<BindPhoneResponse, User> {
        val param = BindPhoneParam(phone, phoneCode)
        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<BindPhoneResponse>>() {}) {
            user = it.result
            token = it.result.token ?: token
            return@createGraphQLCall it.result
        }
    }

    /**
     * Unbind mobile phone number
     */
    fun unbindPhone(): GraphQLCall<UnbindPhoneResponse, User> {
        val param = UnbindPhoneParam()
        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<UnbindPhoneResponse>>() {}) {
            user = it.result
            token = it.result.token ?: token
            return@createGraphQLCall it.result
        }
    }

    /**
     * Log out the current user
     */
    fun logout(): HttpCall<RestfulResponse<Unit>, Unit> {
        if (appId == null) {
            throw Exception("appId cannot be null")
        }
        val url = "$host/api/v2/logout?app_id=$appId"
        return createHttpGetCall(
            url,
            object : TypeToken<RestfulResponse<Unit>>() {}
        ) {
            user = null
            token = null
        }
    }

    /**
     * Get the current user-defined value list
     */
    fun listUdv(): GraphQLCall<UdvResponse, List<UserDefinedData>> {
        if (user == null) {
            throw Exception("login first")
        }
        val param = UdvParam(UdfTargetType.USER, user!!.id)
        return createGraphQLCall(param.createRequest(), object : TypeToken<GraphQLResponse<UdvResponse>>() {}) {
            it.result
        }
    }

    /**
     * Set user-defined value
     */
    fun setUdv(key: String, value: Any): GraphQLCall<SetUdvResponse, List<UserDefinedData>> {
        if (user == null) {
            throw Exception("login first")
        }
        val json = Gson()
        val param = SetUdvParam(UdfTargetType.USER, user!!.id, key, json.toJson(value))
        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<SetUdvResponse>>() {}) {
            it.result
        }
    }

    fun setUdvValue(param: SetUdfValueBatchParam): GraphQLCall<SetUdvResponse, List<UserDefinedData>> {
        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<SetUdvResponse>>() {}
        ) { it.result }
    }

    /**
     * Remove user-defined value
     */
    private fun removeUdv(key: String): GraphQLCall<RemoveUdvResponse, List<UserDefinedData>> {
        if (user == null) {
            throw Exception("login first")
        }
        val param = RemoveUdvParam(UdfTargetType.USER, user!!.id, key)
        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<RemoveUdvResponse>>() {}) {
            it.result
        }
    }

    /**
     * Get the user's organization data list
     */
    fun listOrgs(): HttpCall<RestfulResponse<List<List<Org>>>, List<List<Org>>> {
        return this.createHttpGetCall(
            "${this.host}/api/v2/users/me/orgs",
            object : TypeToken<RestfulResponse<List<List<Org>>>>() {}) {
            it.data
        }
    }

    /**
     * Login with LDAP username, If your user pool is configured with login failure detection, the user will be asked to enter a graphic verification code (code 2000) when the login fails multiple times under the same IP.
     */
    fun loginByLdap(param: LoginByLdapParam): HttpCall<RestfulResponse<User>, User> {
        return this.createHttpPostCall(
            "${this.host}/api/v2/ldap/verify-user",
            GsonBuilder().create().toJson(param),
            object : TypeToken<RestfulResponse<User>>() {}) {
            user = it.data
            token = it.data.token ?: token
            it.data
        }
    }

    /**
     * Login with AD username
     */
    fun loginByAd(username: String, password: String): HttpCall<RestfulResponse<User>, User> {
        return this.createHttpPostCall(
            "ws.${this.host}/api/v2/ad/verify-user",
            "{ \"username\": \"$username\", \"password\": \"$password\" }",
            object : TypeToken<RestfulResponse<User>>() {}) {
            user = it.data
            token = it.data.token ?: token
            it.data
        }
    }

    /**
     * Get all resources authorized by the user
     */
    fun listAuthorizedResources(namespace: String): GraphQLCall<ListUserAuthorizedResourcesResponse, PaginatedAuthorizedResources> {
        if (user == null) {
            throw Exception("login first")
        }
        val param = ListUserAuthorizedResourcesParam(user!!.id, namespace)
        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<ListUserAuthorizedResourcesResponse>>() {}) {
            it.result.authorizedResources!!
        }
    }

    /**
     * Get the user's security rating
     * This interface can be called after logging in
     */
    fun getSecurityLevel(): HttpCall<RestfulResponse<SecurityLevel>, SecurityLevel> {
        if (user == null) {
            throw Exception("login first")
        }

        return this.createHttpGetCall(
            "${this.host}/api/v2/users/me/security-level",
            object : TypeToken<RestfulResponse<SecurityLevel>>() {}) {
            it.data
        }
    }

    /**
     * Get value of user-defined value
     *
     */
    fun getUdfValue(): GraphQLCall<UdvResponse, Map<String, Any>> {
        if (user == null) {
            throw Exception("login first")
        }
        val param = UdvParam(UdfTargetType.USER, user!!.id)
        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<UdvResponse>>() {}) {
            convertUdvToKeyValuePair(it.result)
        }
    }

    /**
     * Set value of user-defined value
     */
    fun setUdfValue(data: Map<String, String>): GraphQLCall<SetUdvBatchResponse, List<UserDefinedData>> {
        if (user == null) {
            throw Exception("login first")
        }

        val udvList = data.entries.map { UserDefinedDataInput(it.key, Gson().toJson(it.value)) }
        val param = SetUdvBatchParam(UdfTargetType.USER, user!!.id, udvList)
        return createGraphQLCall(
            param.createRequest(),
            object : TypeToken<GraphQLResponse<SetUdvBatchResponse>>() {}) {
            it.result
        }
    }

    /**
     * Remove value of user-defined value
     */
    fun removeUdfValue(key: String): GraphQLCall<RemoveUdvResponse, List<UserDefinedData>> {
        return removeUdv(key)
    }

    /**
     * Get AccessToken according to the code
     */
    fun getAccessTokenByCode(code: String): HttpCall<Any, Any> {

        if (this.secret.isNullOrBlank()
            && this.tokenEndPointAuthMethod != AuthMethodEnum.NONE
        ) {
            throw Exception("Please pass in the appId and secret parameters when initializing the AuthenticationClient")
        }

        val url = "${this.host}/oidc/token"
        when (this.tokenEndPointAuthMethod) {
            AuthMethodEnum.CLIENT_SECRET_POST -> {
                return HttpCall(
                    okHttpClient.newCall(
                        Request.Builder()
                            .url(url)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .post(
                                FormBody.Builder().add("client_id", this.appId!!)
                                    .add("client_secret", this.secret!!)
                                    .add("grant_type", "authorization_code")
                                    .add("code", code)
                                    .add("redirect_uri", this.redirectUri!!).build()
                            )
                            .build()
                    ), json.getAdapter(object : TypeToken<Any>() {})
                ) {
                    it
                }
            }
            AuthMethodEnum.CLIENT_SECRET_BASIC -> {
                val basic64Str =
                    "Basic " + Base64.getEncoder().encodeToString(("${this.appId}:${this.secret}").toByteArray())

                return HttpCall(
                    okHttpClient.newCall(
                        Request.Builder()
                            .url(url)
                            .addHeader("Authorization", basic64Str)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .post(
                                FormBody.Builder()
                                    .add("grant_type", "authorization_code")
                                    .add("code", code)
                                    .add("redirect_uri", this.redirectUri!!).build()
                            )
                            .build()
                    ), json.getAdapter(object : TypeToken<Any>() {})
                ) {
                    it
                }
            }
            else -> {//AuthMethodEnum.NONE
                return HttpCall(
                    okHttpClient.newCall(
                        Request.Builder()
                            .url(url)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .post(
                                FormBody.Builder().add("client_id", this.appId!!)
                                    .add("grant_type", "authorization_code")
                                    .add("code", code)
                                    .add("redirect_uri", this.redirectUri!!).build()
                            )
                            .build()
                    ), json.getAdapter(object : TypeToken<Any>() {})
                ) {
                    it
                }
            }
        }
    }

    /**
     * Get AccessToken according to ClientCredentials
     */
    fun getAccessTokenByClientCredentials(scope: String, options: ClientCredentialInput?): HttpCall<Any, Any> {
        if (scope.isBlank()) {
            throw Exception("Please pass parameter 'scope', check the doc:https://docs.approw.com/en/guides/authorization/m2m-authz.html")
        }

        if (options == null) {
            throw Exception("Please pass the { accessKey: string, accessSecret: string } when call this method, check the doc: https://docs.approw.com/v2/en/guides/authorization/m2m-authz.html")
        }

        return HttpCall(
            okHttpClient.newCall(
                Request.Builder()
                    .url("${this.host}/oidc/token")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(
                        FormBody.Builder().add("client_id", options.accessKey)
                            .add("client_secret", options.accessSecret)
                            .add("grant_type", "client_credentials")
                            .add("scope", scope).build()
                    )
                    .build()
            ), json.getAdapter(object : TypeToken<Any>() {})
        ) {
            it
        }
    }

    /**
     * Get user information according to token
     */
    fun getUserInfoByAccessToken(accessToken: String): HttpCall<Any, Any> {
        return HttpCall(
            okHttpClient.newCall(
                Request.Builder()
                    .url("${this.host}/oidc/me?access_token=${accessToken}")
                    .get()
                    .build()
            ), json.getAdapter(object : TypeToken<Any>() {})
        ) {
            it
        }
    }


    /**
     * Get the apps that the current user can access
     */
    @JvmOverloads
    fun listApplications(
        page: Int = 1,
        limit: Int = 10
    ): HttpCall<RestfulResponse<Pagination<ApplicationPublicDetail>>, Pagination<ApplicationPublicDetail>> {
        val url = "$host/api/v2/users/me/applications/allowed?page=$page&limit=$limit"

        return createHttpGetCall(
            url,
            object : TypeToken<RestfulResponse<Pagination<ApplicationPublicDetail>>>() {}) { it.data }
    }

    fun computedPasswordSecurityLevel(
        password: String
    ): PasswordSecurityLevel {
        val highLevel = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[\\^]{12,}\$"
        val middleLevel = "^(?=.*[a-zA-Z])(?=.*\\d)[\\^]{8,}\$"

        if (Pattern.matches(highLevel, password)) {
            return PasswordSecurityLevel.HIGH;
        }
        if (Pattern.matches(middleLevel, password)) {
            return PasswordSecurityLevel.MIDDLE;
        }
        return PasswordSecurityLevel.LOW;
    }

    fun buildAuthorizeUrl(): String {
        if (this.appId == null)
            throw Exception("The parsed appId when initializing AuthenticationClient")

        if (this.protocol != ProtocolEnum.SAML)
            throw Exception("The parsed protocol when initializing AuthenticationClient should be ProtocolEnum.SAML, not $protocol")

        return "$host/api/v2/saml-idp/$appId"
    }

    fun buildAuthorizeUrl(param: IOidcParams): String {
        if (this.appId == null)
            throw Exception("The parsed appId when initializing AuthenticationClient")

        if (this.protocol != ProtocolEnum.OIDC)
            throw Exception("The parsed protocol when initializing AuthenticationClient should be ProtocolEnum.SAML, not $protocol")

        if (this.redirectUri == "" && param.redirectUri == null)
            throw Exception("The redirectUri should not be empty. Solution：parse redirectUro when initializing AuthenticationClient, or parse redirectUri when calling buildAuthorizeUrl")

        val map = mutableMapOf<String, String?>(
            "client_id" to
                    if (param.appId != null) param.appId
                    else this.appId,
            "scope" to
                    if (param.scope != null) param.scope
                    else "openid profile email phone address",
            "state" to
                    if (param.state != null) param.state
                    else Utils().randomString(12),
            "nonce" to
                    if (param.nonce != null) param.nonce
                    else Utils().randomString(12),
            "response_mode" to
                    if (param.responseMode != null) param.responseMode.toString()
                    else null,
            "response_type" to
                    if (param.responseType != null) param.responseType
                    else "code",
            "redirect_uri" to
                    if (param.redirectUri != null) param.redirectUri
                    else this.redirectUri,
            "prompt" to
                    if (param.scope?.contains("offline_access") == true) "consent"
                    else null
        )

        val params = Utils().getQueryUrl(map.filter { (_, value) -> value != null })

        return "$host/oidc/auth$params"
    }

    fun buildAuthorizeUrl(param: IOauthParams): String {
        if (this.appId == null)
            throw Exception("The parsed appId when initializing AuthenticationClient")

        if (this.protocol != ProtocolEnum.OAUTH)
            throw Exception("The parsed protocol when initializing AuthenticationClient should be ProtocolEnum.OAUTH, not $protocol")


        if (this.redirectUri == "" && param.redirectUri == null)
            throw Exception("The redirectUri should not be empty. Solution：parse redirectUro when initializing AuthenticationClient, or parse redirectUri when calling buildAuthorizeUrl")


        val paramsMap = mutableMapOf<String, String?>(
            "client_id" to
                    if (param.appId != null) param.appId
                    else this.appId,
            "scope" to
                    if (param.scope != null) param.scope
                    else "user",
            "state" to
                    if (param.state != null) param.state
                    else Utils().randomString(12),
            "response_type" to
                    if (param.responseType != null) param.responseType
                    else "code",
            "redirect_uri" to
                    if (param.redirectUri != null) param.redirectUri
                    else this.redirectUri
        )

        val params = Utils().getQueryUrl(paramsMap.filter { (_, value) -> value != null })

        return "$host/oauth/auth$params"
    }

    fun buildAuthorizeUrl(param: ICasParams): String {
        if (this.appId == null)
            throw Exception("The parsed appId when initializing AuthenticationClient")

        if (this.protocol != ProtocolEnum.CAS)
            throw Exception("The parsed protocol when initializing AuthenticationClient should be ProtocolEnum.CAS, not $protocol")

        if (this.appId == null) {
            throw Exception("The parsed appId when initializing AuthenticationClient")
        }

        return if (param.service != null)
            "$host/cas-idp/$appId?service=${param.service}"
        else
            "$host/cas-idp/$appId"
    }

    fun getNewAccessTokenByRefreshToken(refreshToken: String): HttpCall<Any, Any> {
        if (listOf<ProtocolEnum>(ProtocolEnum.OAUTH, ProtocolEnum.OIDC).contains(this.protocol)) {
            throw Exception("The parsed protocol when initializing AuthenticationClient should be ProtocolEnum.OAUTH, not $protocol")
        }

        if (this.secret.isNullOrBlank()
            && this.tokenEndPointAuthMethod != AuthMethodEnum.NONE
        ) {
            throw Exception("Please parse appId and secret when initializing AuthenticationClient")
        }

        return when (this.tokenEndPointAuthMethod) {
            AuthMethodEnum.CLIENT_SECRET_POST -> {
                getNewAccessTokenByRefreshTokenWithClientSecretPost(refreshToken)
            }
            AuthMethodEnum.CLIENT_SECRET_BASIC -> {
                getNewAccessTokenByRefreshTokenWithClientSecretBasic(refreshToken)
            }
            else -> {
                getNewAccessTokenByRefreshTokenWithNone(refreshToken)
            }
        }
    }

    private fun getNewAccessTokenByRefreshTokenWithClientSecretPost(refreshToken: String): HttpCall<Any, Any> {
        return HttpCall(
            okHttpClient.newCall(
                Request.Builder()
                    .url(
                        if (this.protocol == ProtocolEnum.OIDC) "$host/oidc/token"
                        else "$host/oauth/token"
                    )
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(
                        FormBody.Builder()
                            .add("client_id", this.appId!!)
                            .add("client_secret", this.secret!!)
                            .add("grant_type", "refresh_token")
                            .add("refresh_token", refreshToken)
                            .build()
                    )
                    .build()
            ), json.getAdapter(object : TypeToken<Any>() {})
        ) {
            it
        }
    }

    private fun getNewAccessTokenByRefreshTokenWithClientSecretBasic(refreshToken: String): HttpCall<Any, Any> {
        val basic64Str =
            "Basic " + Base64.getEncoder().encodeToString(("${this.appId}:${this.secret}").toByteArray())
        return HttpCall(
            okHttpClient.newCall(
                Request.Builder()
                    .url(
                        if (this.protocol == ProtocolEnum.OIDC) "$host/oidc/token"
                        else "$host/oauth/token"
                    )
                    .addHeader("Authorization", basic64Str)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(
                        FormBody.Builder()
                            .add("grant_type", "refresh_token")
                            .add("refresh_token", refreshToken)
                            .build()
                    )
                    .build()
            ), json.getAdapter(object : TypeToken<Any>() {})
        ) {
            it
        }
    }

    private fun getNewAccessTokenByRefreshTokenWithNone(refreshToken: String): HttpCall<Any, Any> {
        return HttpCall(
            okHttpClient.newCall(
                Request.Builder()
                    .url(
                        if (this.protocol == ProtocolEnum.OIDC) "$host/oidc/token"
                        else "$host/oauth/token"
                    )
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(
                        FormBody.Builder()
                            .add("client_id", this.appId!!)
                            .add("grant_type", "refresh_token")
                            .add("refresh_token", refreshToken)
                            .build()
                    )
                    .build()
            ), json.getAdapter(object : TypeToken<Any>() {})
        ) {
            it
        }
    }

    fun introspectToken(token: String): HttpCall<Any, Any> {
        if (listOf<ProtocolEnum>(ProtocolEnum.OAUTH, ProtocolEnum.OIDC).contains(this.protocol)) {
            throw Exception("The parsed protocol when initializing AuthenticationClient should be ProtocolEnum.OAUTH or ProtocolEnum.OIDC, not $protocol")
        }

        if (this.secret.isNullOrBlank()
            && this.tokenEndPointAuthMethod != AuthMethodEnum.NONE
        ) {
            throw Exception("Please parse appId and secret when initializing AuthenticationClient")
        }

        return when (this.introspectionEndPointAuthMethod) {
            AuthMethodEnum.CLIENT_SECRET_POST -> introspectTokenWithClientSecretPost(token)
            AuthMethodEnum.CLIENT_SECRET_BASIC -> introspectTokenWithClientSecretBasic(token)
            else -> introspectTokenWithNone(token)

        }
    }

    private fun introspectTokenWithClientSecretPost(token: String): HttpCall<Any, Any> {
        return HttpCall(
            okHttpClient.newCall(
                Request.Builder()
                    .url(
                        if (this.protocol == ProtocolEnum.OIDC) "$host/oidc/token/introspection"
                        else "$host/oauth/token/introspection"
                    )
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(
                        FormBody.Builder()
                            .add("client_id", this.appId!!)
                            .add("client_secret", this.secret!!)
                            .add("token", token)
                            .build()
                    )
                    .build()
            ), json.getAdapter(object : TypeToken<Any>() {})
        ) {
            it
        }
    }

    private fun introspectTokenWithClientSecretBasic(token: String): HttpCall<Any, Any> {
        val basic64Str =
            "Basic " + Base64.getEncoder().encodeToString(("${this.appId}:${this.secret}").toByteArray())
        return HttpCall(
            okHttpClient.newCall(
                Request.Builder()
                    .url(
                        if (this.protocol == ProtocolEnum.OIDC) "$host/oidc/token/introspection"
                        else "$host/oauth/token/introspection"
                    )
                    .addHeader("Authorization", basic64Str)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(
                        FormBody.Builder()
                            .add("token", token)
                            .build()
                    )
                    .build()
            ), json.getAdapter(object : TypeToken<Any>() {})
        ) {
            it
        }
    }

    private fun introspectTokenWithNone(token: String): HttpCall<Any, Any> {
        return HttpCall(
            okHttpClient.newCall(
                Request.Builder()
                    .url(
                        if (this.protocol == ProtocolEnum.OIDC) "$host/oidc/token/introspection"
                        else "$host/oauth/token/introspection"
                    )
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(
                        FormBody.Builder()
                            .add("client_id", this.appId!!)
                            .add("token", token)
                            .build()
                    )
                    .build()
            ), json.getAdapter(object : TypeToken<Any>() {})
        ) {
            it
        }
    }

    fun validateToken(param: ValidateTokenParams): HttpCall<Any, Any> {
        val (accessToken, idToken) = param
        if (accessToken == null && idToken == null)
            throw Exception("Please include accessToken and idToken in parsed parameter")
        if (accessToken != null && idToken != null)
            throw Exception("You can either parse accessToken or idToken, not both")

        var url = "$host/api/v2/oidc/validate_token"
        url += if (accessToken != null) "?access_token=$accessToken" else "?id_token=$idToken"

        return HttpCall(
            okHttpClient.newCall(
                Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .get()
                    .build()
            ), json.getAdapter(object : TypeToken<Any>() {})
        ) {
            it
        }
    }


    fun revokeToken(token: String): HttpCall<Any, Boolean> {
        if (listOf<ProtocolEnum>(ProtocolEnum.OAUTH, ProtocolEnum.OIDC).contains(this.protocol)) {
            throw Exception("The parsed protocol when initializing AuthenticationClient should be ProtocolEnum.OAUTH or ProtocolEnum.OIDC, not $protocol")
        }

        if (this.secret.isNullOrBlank()
            && this.tokenEndPointAuthMethod != AuthMethodEnum.NONE
        ) {
            throw Exception("Please parse appId and secret when initializing AuthenticationClient")
        }

        return when (this.introspectionEndPointAuthMethod) {
            AuthMethodEnum.CLIENT_SECRET_POST -> revokeTokenWithClientSecretPost(token)
            AuthMethodEnum.CLIENT_SECRET_BASIC -> revokeTokenWithClientSecretBasic(token)
            else -> revokeTokenWithNone(token)

        }
    }

    private fun revokeTokenWithClientSecretPost(token: String): HttpCall<Any, Boolean> {
        return HttpCall(
            okHttpClient.newCall(
                Request.Builder()
                    .url(
                        if (this.protocol == ProtocolEnum.OIDC) "$host/oidc/token/revocation"
                        else "$host/oauth/token/revocation"
                    )
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(
                        FormBody.Builder()
                            .add("token", token)
                            .add("client_id", this.appId!!)
                            .add("client_secret", this.secret!!)
                            .build()
                    )
                    .build()
            ), json.getAdapter(object : TypeToken<Any>() {})
        ) {
            it != null
        }
    }

    private fun revokeTokenWithClientSecretBasic(token: String): HttpCall<Any, Boolean> {
        if (this.protocol == ProtocolEnum.OAUTH)
            throw Exception("Temporarily OAuth 2.0 does not support revoking Token in client_secret_basic mode")

        val basic64Str =
            "Basic " + Base64.getEncoder().encodeToString(("${this.appId}:${this.secret}").toByteArray())
        return HttpCall(
            okHttpClient.newCall(
                Request.Builder()
                    .url("$host/oidc/token/revocation")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Authorization", basic64Str)
                    .post(
                        FormBody.Builder()
                            .add("token", token)
                            .build()
                    )
                    .build()
            ), json.getAdapter(object : TypeToken<Any>() {})
        ) {
            it != null
        }
    }

    private fun revokeTokenWithNone(token: String): HttpCall<Any, Boolean> {
        return HttpCall(
            okHttpClient.newCall(
                Request.Builder()
                    .url(
                        if (this.protocol == ProtocolEnum.OIDC) "$host/oidc/token/revocation"
                        else "$host/oauth/token/revocation"
                    )
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(
                        FormBody.Builder()
                            .add("token", token)
                            .add("client_id", this.appId!!)
                            .build()
                    )
                    .build()
            ), json.getAdapter(object : TypeToken<Any>() {})
        ) {
            it != null
        }
    }

    fun buildLogoutUrl(options: ILogoutParams): String {
        if (this.protocol == ProtocolEnum.OAUTH)
            return this.buildCasLogoutUrl(options)

        if (this.protocol == ProtocolEnum.OIDC && options.expert != null)
            return this.buildOidcLogoutUrl(options)

        return buildEasyLogoutUrl(options)
    }

    private fun buildCasLogoutUrl(options: ILogoutParams): String {
        return if (options.redirectUri != null)
            "$host/cas-idp/logout?url=${options.redirectUri}"
        else
            "$host/cas-idp/logout"
    }

    private fun buildOidcLogoutUrl(options: ILogoutParams): String {
        if ((options.redirectUri != null && options.idToken != null) || (options.redirectUri == null && options.idToken == null))
            throw Exception("You must parse redirectUri and idToken at the same time, or neither of them, not both")

        return if (options.redirectUri != null)
            "${host}/oidc/session/end?id_token_hint=${options.idToken}&post_logout_redirect_uri=${options.redirectUri}"
        else
            "${host}/oidc/session/end"
    }

    private fun buildEasyLogoutUrl(options: ILogoutParams): String {
        return if (options.redirectUri != null)
            "${host}/login/profile/logout?redirect_uri=${options.redirectUri}"
        else
            "${host}/login/profile/logout"
    }
}