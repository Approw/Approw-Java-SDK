package com.approw.core

import com.approw.core.graphql.GraphQLCall
import com.approw.core.graphql.GraphQLRequest
import com.approw.core.graphql.GraphQLResponse
import com.approw.core.http.HttpCall
import com.approw.core.types.AuthMethodEnum
import com.approw.core.types.ProtocolEnum
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher


/**
 * Approw client class
 */
abstract class BaseClient {

    init {
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2")
    }

    // optional parameters
    var host: String = "https://core.approw.com"
    var publicKey: String = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4xKeUgQ+Aoz7TLfAfs9+paePb" +
            "5KIofVthEopwrXFkp8OCeocaTHt9ICjTT2QeJh6cZaDaArfZ873GPUn00eOIZ7Ae" +
            "+TiA2BKHbCvloW3w5Lnqm70iSsUi5Fmu9/2+68GZRH9L7Mlh8cFksCicW2Y2W2uM" +
            "GKl64GDcIq3au+aqJQIDAQAB"

    var userPoolId: String? = null
    /**
     * Each time you send a request, the token will be added in request header
     *
     * You must set this variable when accessing some APIs which need special privileges
     */
    var token: String? = null
    /**
     * token expire time
     */
    var accessTokenExpiresAt: Long? = null

    /**
     * app Id
     */
    var appId: String? = ""

    //APP secret
    var secret: String? = ""
    //APP identity protocol
    var protocol: ProtocolEnum? = ProtocolEnum.OIDC
    //get token end point authenticate method
    var tokenEndPointAuthMethod: AuthMethodEnum? = AuthMethodEnum.CLIENT_SECRET_POST
    //check token end point authenticate method
    var introspectionEndPointAuthMethod: AuthMethodEnum? = AuthMethodEnum.CLIENT_SECRET_POST
    //revoke token end point authenticate method
    var revocationEndPointAuthMethod: AuthMethodEnum? = AuthMethodEnum.CLIENT_SECRET_POST

    //APP callback address
    var redirectUri: String? = ""
    //Websocket host domain name
    var websocketHost: String? = ""

    // const
    protected val mediaTypeJson: MediaType? = "application/json".toMediaTypeOrNull()
    protected val mediaTypeUrlencoded: MediaType? = "application/x-www-form-urlencoded".toMediaTypeOrNull()
    protected val sdkType: String = "SDK"
    protected val sdkVersion: String = "java:4.3.9"

    // graphql endpoint
    private val endpoint: String
        get() {
            return "$host/graphql/v2"
        }

    protected val okHttpClient: OkHttpClient = OkHttpClient()
    protected val json = GsonBuilder().create()

    /**
     * password encrypt method
     */
    internal open fun encrypt(msg: String?): String {
        if (msg === null) {
            return ""
        }

        // get publicKey
        val keyBytes: ByteArray = Base64.getDecoder().decode(publicKey)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKey = keyFactory.generatePublic(keySpec)
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val cipherMsg = cipher.doFinal(msg.toByteArray())

        return String(Base64.getEncoder().encode(cipherMsg))
    }

    /**
     * create GraphQL request
     */
    internal open fun <TData, TResult> createGraphQLCall(
        request: GraphQLRequest,
        typeToken: TypeToken<GraphQLResponse<TData>>,
        resolver: (data: TData) -> TResult
    ): GraphQLCall<TData, TResult> {
        val adapter = json.getAdapter(typeToken)
        return GraphQLCall(
            okHttpClient.newCall(
                Request.Builder()
                    .url(endpoint)
                    .addHeader("Authorization", "Bearer " + this.token)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-approw-userpool-id", "" + userPoolId)
                    .addHeader("x-approw-request-from", sdkType)
                    .addHeader("x-approw-sdk-version", sdkVersion)
                    .addHeader("x-approw-app-id", "" + this.appId)
                    .post(json.toJson(request).toRequestBody(mediaTypeJson))
                    .build()
            ), adapter, resolver
        )
    }

    /**
     * create HTTP GET request
     */
    internal open fun <TData, TResult> createHttpGetCall(
        url: String,
        typeToken: TypeToken<TData>,
        resolver: (data: TData) -> TResult
    ): HttpCall<TData, TResult> {
        val adapter = json.getAdapter(typeToken)
        return HttpCall(
            okHttpClient.newCall(
                Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + this.token)
                    .addHeader("x-approw-userpool-id", "" + userPoolId)
                    .addHeader("x-approw-request-from", sdkType)
                    .addHeader("x-approw-sdk-version", sdkVersion)
                    .addHeader("x-approw-app-id", "" + this.appId)
                    .get()
                    .build()
            ), adapter, resolver
        )
    }


    /**
     * create HTTP POST request
     */
    internal open fun <TData, TResult> createHttpPostCall(
        url: String,
        body: String,
        typeToken: TypeToken<TData>,
        resolver: (data: TData) -> TResult
    ): HttpCall<TData, TResult> {
        val adapter = json.getAdapter(typeToken)
        return HttpCall(
            okHttpClient.newCall(
                Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + this.token)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-approw-userpool-id", "" + userPoolId)
                    .addHeader("x-approw-request-from", sdkType)
                    .addHeader("x-approw-sdk-version", sdkVersion)
                    .addHeader("x-approw-app-id", "" + this.appId)
                    .post(body.toRequestBody(mediaTypeJson))
                    .build()
            ), adapter, resolver
        )
    }

    /**
     * create HTTP DELETE request
     */
    internal open fun <TData, TResult> createHttpDeleteCall(
        url: String, typeToken: TypeToken<TData>,
        resolver: (data: TData) -> TResult
    ): HttpCall<TData, TResult> {
        val adapter = json.getAdapter(typeToken)
        return HttpCall(
            okHttpClient.newCall(
                Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + this.token)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-approw-userpool-id", "" + userPoolId)
                    .addHeader("x-approw-request-from", sdkType)
                    .addHeader("x-approw-sdk-version", sdkVersion)
                    .addHeader("x-approw-app-id", "" + this.appId)
                    .delete()
                    .build()
            ), adapter, resolver
        )
    }


    /**
     * create HTTP Patch request
     */
    internal open fun <TData, TResult> createHttpPatchCall(
        url: String,
        body: String,
        typeToken: TypeToken<TData>,
        resolver: (data: TData) -> TResult
    ): HttpCall<TData, TResult> {
        val adapter = json.getAdapter(typeToken)
        return HttpCall(
            okHttpClient.newCall(
                Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + this.token)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-approw-userpool-id", "" + userPoolId)
                    .addHeader("x-approw-request-from", sdkType)
                    .addHeader("x-approw-sdk-version", sdkVersion)
                    .addHeader("x-approw-app-id", "" + this.appId)
                    .patch(body.toRequestBody(mediaTypeJson))
                    .build()
            ), adapter, resolver
        )
    }

    /**
     * PUT
     */
    internal open fun <TData, TResult> createHttpPutCall(
        url: String,
        body: String,
        typeToken: TypeToken<TData>,
        resolver: (data: TData) -> TResult
    ): HttpCall<TData, TResult> {
        val adapter = json.getAdapter(typeToken)
        return HttpCall(
            okHttpClient.newCall(
                Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + this.token)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-approw-userpool-id", "" + userPoolId)
                    .addHeader("x-approw-request-from", sdkType)
                    .addHeader("x-approw-sdk-version", sdkVersion)
                    .addHeader("x-approw-app-id", "" + this.appId)
                    .put(body.toRequestBody(mediaTypeJson))
                    .build()
            ), adapter, resolver
        )
    }
}
