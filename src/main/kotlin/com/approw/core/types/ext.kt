package com.approw.core.types

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.awt.Label
import java.util.*

//This file stores customized extension type.


data class SecurityLevel(
    // Whether email is bound
    var email: Boolean?,
    // Whether password is set
    var password: Boolean?,
    // Whether phone number is bound
    var phone: Boolean?,
    // The score of total security evaluation of the account.=
    var score: String?,
    // Password strength level. Null means the password is not tested yet.=
    var passwordSecurityLevel: PasswordSecurityLevel?
)


enum class PasswordSecurityLevel(val level: Int) {
    LOW(1),
    MIDDLE(2),
    HIGH(3);

    companion object {
        @JvmStatic
        fun valueOfLabel(level: Int): PasswordSecurityLevel? {
            return values().find { it.level == level }
        }
    }
}

fun convertUdvToKeyValuePair(data: List<UserDefinedData>): Map<String, Any> {
    val hashtable = Hashtable<String, Any>()
    val gson = Gson()
    for (item in data) {
        hashtable.put(item.key, item.value)
//        if (item.dataType === UdfDataType.NUMBER) {
//            hashtable.put(item.key,gson.fromJson(item.value, Map::class.java))
//        } else if (item.dataType === UdfDataType.BOOLEAN) {
//            hashtable.put(item.key,gson.fromJson(item.value, Map::class.java))
//        } else if (item.dataType === UdfDataType.DATETIME) {
//            hashtable.put(item.key,item.value)
//        } else if (item.dataType === UdfDataType.OBJECT) {
//            hashtable.put(item.key,gson.fromJson(item.value, Map::class.java))
//        }
    }
    return hashtable
}

data class SetUdfValueBatchInputItem(
    // user id
    var userId: String,
    // user-defined-data key-value pairs
    var data: Map<String, String>
)

data class S(
    // user id
    var userId: String,
    // user-defined-data key-value pairs
    var data: Map<String, String>
)

enum class ProtocolEnum(val value: String) {
    OAUTH("oauth"),
    OIDC("oidc"),
    CAS("cas"),
    SAML("saml");

    companion object {
        @JvmStatic
        fun valueOfLabel(value: String): ProtocolEnum? {
            return values().find { it.value == value }
        }
    }
}

enum class AuthMethodEnum(val value: String) {
    CLIENT_SECRET_POST("client_secret_post"),
    CLIENT_SECRET_BASIC("client_secret_basic"),
    NONE("none");

    companion object {
        @JvmStatic
        fun valueOfLabel(value: String): AuthMethodEnum? {
            return values().find { it.value == value }
        }
    }
}

enum class ProviderType {
    @SerializedName("wechat:pc")
    WECHAT_PC,
    @SerializedName("github")
    GITHUB,
    @SerializedName("google")
    GOOGLE,
    @SerializedName("qq")
    QQ,
    @SerializedName("apple")
    APPLE,
    @SerializedName("baidu")
    BAIDU,
    @SerializedName("alipay")
    ALIPAY,
    @SerializedName("lark:app-store")
    APP_STORE,
    @SerializedName("lark:custom-app")
    CUSTOM_APP,
    @SerializedName("weibo")
    WEIBO,
    @SerializedName("dingtalk")
    DINGTALK,
    @SerializedName("wechat:webpage-authorization")
    WEBPAGE_AUTHORIZATION,
    @SerializedName("wechat:miniprogram:default")
    MINIPROGRAM,
    @SerializedName("wechat:mobile")
    WECHAT_MOBILE,
    @SerializedName("wechatwork:service-provider:service")
    WECHATWORK_SERVICE_SERVICE,
    @SerializedName("wechatwork:service-provider:qrconnect")
    WECHATWORK_SERVICE_QRCONNECT,
    @SerializedName("wechatwork:corp:qrconnect")
    WECHATWORK_CORP_QRCONNECT,
    @SerializedName("wechat:miniprogram:app-launch")
    WECHAT_MINIPROGRAM_APP,
    @SerializedName("wechat:miniprogram:qrconnect")
    WECHAT_MINIPROGRAM_QRCONNECT;
}

data class ClientCredentialInput(
    // key,appid
    var accessKey: String,
    // secret
    var accessSecret: String
)
