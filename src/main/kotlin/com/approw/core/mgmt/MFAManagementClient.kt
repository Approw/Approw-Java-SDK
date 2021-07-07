package com.approw.core.mgmt

import com.approw.core.http.HttpCall
import com.approw.core.types.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MFAManagementClient(private val client: ManagementClient) {

    fun getStatus(
        userId: String
    ): HttpCall<RestfulResponse<UserMfaStatus>, UserMfaStatus> {
        val url = "${client.host}/api/v2/users/${userId}/mfa-bound"

        return client.createHttpGetCall(
            url,
            object : TypeToken<RestfulResponse<UserMfaStatus>>() {}
        ) { it.data }
    }

    fun unAssociateMfa(
        userId: String,
        type: UserMfaType
    ): HttpCall<RestfulResponse<Boolean>, Boolean> {

        val url = "${client.host}/api/v2/users/${userId}/mfa-bound?type=${type}"

        return client.createHttpDeleteCall(
            url,
            object : TypeToken<RestfulResponse<Boolean>>() {}
        ) { it.code == 200 }
    }

    /**
     * @name importTotp
     * @name_zh import the secret and recovery code of existing TOTP to Approw and enable TOTP MFA for the user
     * @description set secret and recovery code for user TOTP, and enable MFA automatically
     *
     * @param {ImportTotpParams} options
     * @param options - {String} userId 
     * @param options - {String} secret TOTP 
     * @param options - {String} recoveryCode 
     */
    fun importTotp(
        options: ImportTotpParams
    ): HttpCall<RestfulResponse<ISetTotpResp>, ISetTotpResp> {
        val url = "${client.host}/api/v2/mfa/totp/import"

        return client.createHttpPostCall(
            url,
            Gson().toJson(options),
            object : TypeToken<RestfulResponse<ISetTotpResp>>() {}
        ) { it.data }
    }
}