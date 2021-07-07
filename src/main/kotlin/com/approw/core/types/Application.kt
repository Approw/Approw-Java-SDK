package com.approw.core.types

/**
 * APP information
 */
data class Application(

    /**
     * APP ID
     */
    var id: String,

    /**
     * userpool ID of the APP
     */
    var userPoolId: String,

    /**
     *  protocol type of the APP
     */
    var protocol: String,

    /**
     * Whether it is an official APP.
     */
    var isOfficial: Boolean,

    /**
     * Whether it is deleted.
     */
    var isDeleted: Boolean,

    /**
     * Whether it is a default APP of userpool.
     */
    var isDefault: Boolean,

    /**
     * APP name
     */
    var name: String,

    /**
     * APP description information.
     */
    var description: String,

    /**
     * secret key
     */
    var secret: String,

    /**
     * The unique identifier tag pf the APP. Now it is used as second-level domain name.
     */
    var identifier: String,

    /**
     * jwt used to sign the id_token
     */
    var jwks: Any,

    /**
     * page customization settings
     */
    var ssoPageCustomizationSettings: ISsoPageCustomizationSettings,

    /**
     * Logo
     */
    var logo: String,

    /**
     * callback link (redirect link)
     */
    var redirectUris: List<String>,

    /**
     * logout redirect link
     */
    var logoutRedirectUris: List<String>,

    /**
     * Whether OIDC protocol is enabled.
     */
    var oidcProviderEnabled: Boolean,

    /**
     * Whether oauth protocol is enabled.
     */
    var oauthProviderEnabled: Boolean,

    /**
     * Whether SAML protocol is enabled.
     */
    var samlProviderEnabled: Boolean,

    /**
     * Whether registration is disabled.
     */
    var registerDisabled: Boolean,

    /**
     * login Tab list
     */
    var loginTabs: List<String>,

    /**
     * Default login Tab
     */
    var defaultLoginTab: String,

    /**
     * Register Tab list
     */
    var registerTabs: List<String>,

    /**
     * Default register Tab
     */
    var defaultRegisterTab: String,

    /**
     * Enabled ldap connection id
     */
    var ldapConnections: List<String>,

    /**
     * Enabled Active Directory connection ID
     */
    var adConnections: List<String>,

    /**
     * Disabled social login IDP, such as google
     */
    var disabledSocialConnections: List<String>,

    /**
     * Disabled OIDC connection ID
     */
    var disabledOidcConnections: List<String>,

    /**
     * Disabled SAML connection ID
     */
    var disabledSamlConnections: List<String>,

    /**
     * Disabled OAuth2.0 connection ID
     */
    var disabledOauth2Connections: List<String>,

    /**
     * Disabled CAS connection ID
     */
    var disabledCasConnections: List<String>,

    /**
     * Disabled Azure AD connection ID
     */
    var disabledAzureAdConnections: List<String>,

    var extendsFieldsEnabled: Boolean,

    /**
     * Register information fullfill setting.=
     */
    var extendsFields: List<ExtendsField>,

    var ext: Any,

    /**
     * Customized CSS
     */
    var css: String,

    var qrcodeScanning: ApplicationQRCodeScanning,

    /**
     * OIDC configuration
     */
    var oidcConfig: Any,

    var samlConfig: Any,

    var oauthConfig: Any,

    var showAuthorizationPage: Boolean,

    var permissionStrategy: ApplicationPermissionStrategyConfig,

    /**
     * Whether sub account is enabled.
     */
    var enableSubAccount: Boolean

)

data class ISsoPageCustomizationSettings(
    var hideIdp: Boolean,
    var hideForgetPassword: Boolean,
    var autoRegisterThenLogin: Boolean,
    var hideSocialLogin: Boolean,
    var hideLoginByPhoneCode: Boolean,
    var hideRegister: Boolean,
    var hideUserPasswordLogin: Boolean,
    var hideWxMpScanLogin: Boolean,
    var hideRegisterByPhone: Boolean,
    var hideRegisterByEmail: Boolean
)

data class ExtendsField(
    var type: String,
    var id: String,
    var name: String,
    var label: String,
    var inputType: String
)

data class ApplicationQRCodeScanning(
    var redirect: Boolean,
    var intervar: Int
)

data class ApplicationPermissionStrategyConfig(
    /**
     * Whether application authorize function is enabled.
     */
    var enabled: Boolean,

    /**
     * The default APP authorization policy. "ALLOW_ALL" or "DENY_ALL"
     */
    var defaultStrategy: String,

    /**
     * the ID of "allow login" policy
     */
    var allowPolicyId: String,

    /**
     * the ID of "deny login" policy
     */
    var denyPolicyId: String
)

data class ListApplicationResponse(
    var list: List<Application>,
    var totalCount: Int
)

data class ApplicationPublicDetail(
    var id: String,
    var createdAt: String,
    var updatedAt: String,
    var name: String,
    var logo: String,
    var domain: String,
    var description: String,
    var protocol: String
)

class IActiveUsersParam
@JvmOverloads
constructor(
    var appId: String,
    var page: Int? = 1,
    var limit: Int? = 10
)

data class IThirdPartyIdentity(
    var provider: String,
    var refreshToken: String,
    var accessToken: String,
    var scope: String,
    var expiresIn: String,
    var updatedAt: String
)

class ActiveUser
@JvmOverloads
constructor(
    var id: String,
    var createdAt: String,
    var updatedAt: String,
    var userPoolId: String,
    var isRoot: Boolean,
    var status: String,
    var oauth: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var username: String,
    var unionid: String? = null,
    var openid: String? = null,
    var nickname: String? = null,
    var company: String? = null,
    var photo: String,
    var browser: String? = null,
    var device: String? = null,
    var password: String,
    var salt: String? = null,
    var token: String,
    var tokenExpiredAt: String,
    var loginsCount: Int,
    var lastIp: String,
    var name: String? = null,
    var givenName: String? = null,
    var familyName: String? = null,
    var middleName: String? = null,
    var profile: String? = null,
    var preferredUsername: String? = null,
    var website: String? = null,
    var gender: String,
    var birthdate: String? = null,
    var zoneinfo: String? = null,
    var locale: String? = null,
    var address: String? = null,
    var formatted: Boolean?,
    var streetAddress: String? = null,
    var locality: String? = null,
    var region: String? = null,
    var postalCode: String? = null,
    var city: String? = null,
    var province: String? = null,
    var country: String? = null,
    var registerSource: List<String>,
    var secretInfo: String? = null,
    var emailVerified: Boolean,
    var phoneVerified: Boolean,
    var lastLogin: String,
    var blocked: Boolean,
    var isDeleted: Boolean,
    var sendSmsCount: Int,
    var sendSmsLimitCount: Int,
    var signedUp: String,
    var externalId: String? = null,
    var mainDepartmentId: String? = null,
    var mainDepartmentCode: String? = null,
    var lastMfaTime: String? = null,
    var passwordSecurityLevel: Int,
    var thirdPartyIdentity: IThirdPartyIdentity
)


data class CreateResourceParams
@JvmOverloads
constructor(
    var code: String,
    var type: ResourceType,
    var actions: List<IAction>,
    var description: String? = null
)

data class UpdateResourceParams
@JvmOverloads
constructor(
    var code: String,
    var type: ResourceType,
    var actions: List<IAction>,
    var description: String? = null
)

data class ResourceOptionsParams
@JvmOverloads
constructor(
    var code: String,
    var type: ResourceType,
    var actions: List<IAction>,
    var description: String? = null
)

data class IAccessPolicyParams
@JvmOverloads
constructor(
    var targetType: TargetTypeEnum,
    var targetIdentifiers: List<String>,
    var inheritByChildren: String? = null
)

data class CreateRoleParams
@JvmOverloads
constructor(
    var code: String,
    var description: String? = null
)

data class UpdateRoleParams @JvmOverloads constructor(
    var code: String,
    var description: String? = null,
    var newCode: String? = null
)

data class DenyAccessParams
@JvmOverloads
constructor(
    var targetType: TargetTypeEnum,
    var targetIdentifiers: List<String>,
    var inheritByChildren: String? = null
)

data class CreateAppParams
@JvmOverloads
constructor(
    var name: String,
    var identifier: String,
    var redirectUris: List<String>,
    var logo: String? = null

)

data class ListResourcesParams
@JvmOverloads
constructor(
    var appId: String,
    var type: ResourceType? = null,
    var limit: Int = 10,
    var page: Int = 1
)

data class AgreementParams
@JvmOverloads
constructor(
    var title: String,
    var required: Boolean? = true,
    var lang: String? = "zh-CN"
)

data class AgreementDetail
constructor(
    var title: String,
    var required: Boolean,
    var lang: String,
    var appId: String,
    var order: String,
    var userPoolId: String,
    var id: String
)
