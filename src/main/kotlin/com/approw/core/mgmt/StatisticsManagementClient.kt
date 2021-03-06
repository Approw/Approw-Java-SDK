package com.approw.core.mgmt

import com.approw.core.http.HttpCall
import com.approw.core.types.*
import com.google.gson.reflect.TypeToken

/**
 *  Log statistics Management
 */
class StatisticsManagementClient(private val client: ManagementClient) {

    /**
     * View user operation log
     */
    fun listUserActions(options: LogsPageParam?): HttpCall<RestfulResponse<PaginatedLogs>, PaginatedUserActionLog> {
        var url = "${client.host}/api/v2/analysis/user-action?";
        url += if (options?.clientIp != null) "&clientip=${options.clientIp}" else ""
        url += if (options?.page != null) "&page=${options.page}" else ""
        url += if (options?.limit != null) "&limit=${options.limit}" else ""

        if (options!=null && options.operationNames != null && options.operationNames!!.isNotEmpty()){
            options.operationNames!!.forEach {
                url += "&operation_name=$it"
            }
        }
        if (options!=null && options.userIds != null && options.userIds!!.isNotEmpty()){
            options.userIds!!.forEach {
                url += "&operator_arn=arn:cn:approw:${this.client.userPoolId}:user:${it}"
            }
        }

        return client.createHttpGetCall(url ,object:TypeToken<RestfulResponse<PaginatedLogs>>() {}){
            val list: MutableList<UserActionLogResponse> = mutableListOf()
            it.data.list.forEach {
                list.add(UserActionLogResponse(it.userpool_id, it.user?.id, it.user?.displayName,it.geoip?.city_name,
                    it.geoip?.region_name,it.geoip?.ip,it.operation_desc,
                    it.operation_name,it.timestamp,it.app_id,it.app?.name))
            }
            PaginatedUserActionLog(it.data.totalCount,list)
        };
    }

    /**
     * View audit log
     */
    fun listAuditLogs(options: AuditLogPageParam?): HttpCall<RestfulResponse<PaginatedAuditLogs>, PaginatedAuditLog> {
        var url = "${client.host}/api/v2/analysis/audit?"
        url += if (options?.clientIp != null) "&clientip=${options.clientIp}" else ""
        url += if (options?.page != null) "&page=${options.page}" else ""
        url += if (options?.limit != null) "&limit=${options.limit}" else ""

        if (options!=null && options.operationNames != null && options.operationNames!!.isNotEmpty()){
            options.operationNames!!.forEach {
                url += "&operation_name=$it"
            }
        }
        if (options!=null && options.operatorArns != null && options.operatorArns!!.isNotEmpty()){
            options.operatorArns!!.forEach {
                url += "&operator_arn=$it"
            }
        }

        return client.createHttpGetCall(url,object:TypeToken<RestfulResponse<PaginatedAuditLogs>>() {}){
            val list: MutableList<AuditLogResponse> = mutableListOf()
            it.data.list.forEach {
                AuditLogResponse("","","",""
                    ,"","","",""
                    ,"","","","")
                list.add(AuditLogResponse(it.userpool_id, it.operator_type, it.operator_detail?.id,it.operator_detail?.displayName,
                    it.operation_name,it.geoip?.city_name,it.geoip?.region_name,it.geoip?.ip,
                    it.resource_type,it.resource_desc,it.resource_arn,it.timestamp))
            }
            PaginatedAuditLog(it.data.totalCount,list)
        };
    }

}
