package com.approw.core.types

data class LoginByLdapParam @JvmOverloads constructor(
    /**
     * username
     */
    var username: String,

    /**
     * password
     */
    var password: String,

    var options: LoginByLdapParamOptions? = null
) {

    fun withOptions(options: LoginByLdapParamOptions): LoginByLdapParam {
        this.options = options
        return this
    }

    fun build(): LoginByLdapParam {
        return this
    }
}

data class LoginByLdapParamOptions(
    /**
     * Whether to register automatically or not.
     * If user doesn't exist, it will create an account automatically depends on the login account and secret.
     */
    var autoRegister: Boolean? = false,

    /**
     * CAPTACHA verification code
     */
    var captchaCode: String? = null,

    /**
     * The real IP of client.
     * If you call this API on server, please set this parameter to the real IP of the end user.
     */
    var clientIp: String? = null
) {
    fun withAutoRegister(autoRegister: Boolean): LoginByLdapParamOptions {
        this.autoRegister = autoRegister
        return this
    }

    fun withCaptchaCode(captchaCode: String): LoginByLdapParamOptions {
        this.captchaCode = captchaCode
        return this
    }

    fun withClientIp(clientIp: String): LoginByLdapParamOptions {
        this.clientIp = clientIp
        return this
    }

    fun build(): LoginByLdapParamOptions {
        return this
    }
}
