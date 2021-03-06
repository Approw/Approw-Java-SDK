package com.approw.core;

import com.approw.core.graphql.GraphQLException;
import com.approw.core.mgmt.ManagementClient;
import com.approw.core.mgmt.StatisticsManagementClient;
import com.approw.core.types.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class StatisticsManagementClientTest {

    private StatisticsManagementClient statisticsManagementClient;

    @Before
    public void before() throws IOException, GraphQLException {
        ManagementClient managementClient = new ManagementClient("5f9d0cee4a8f5e150cf6470d", "ea4e02cd9dbff480a64813f7fe3b5cf0");
        managementClient.setHost("https://core.authing.cn");
        this.statisticsManagementClient = managementClient.statistics();

        managementClient.requestToken().execute();
    }

    @Test
    public void listUserActions() throws IOException {
        List<String> operationNames = Arrays.asList("login", "register");
        List<String> userIds = Arrays.asList("5f9d22ff9e171c6932cf5b0b", "22");

        LogsPageParam pageParam = new LogsPageParam(
                "123.117.179.159", operationNames, userIds, 1,10
        );

        PaginatedUserActionLog result = this.statisticsManagementClient.listUserActions(null).execute();
        Assert.assertTrue(result.getList().size() > 0);
    }

    @Test
    public void listAuditLogs() throws IOException {
        List<String> operationNames = Arrays.asList("userpool:UpdateConfig", "11");
        List<String> operatorArns = Arrays.asList("arn:cn:authing:59f86b4832eb28071bdd9214:user:5f8d27f5a5c763ceaa628844", "22");

        AuditLogPageParam pageParam = new AuditLogPageParam(
                null, operationNames, operatorArns, 1,20
        );

        PaginatedAuditLog result = this.statisticsManagementClient.listAuditLogs(pageParam).execute();
        Assert.assertTrue(result.getList().size() > 0);
    }


}
