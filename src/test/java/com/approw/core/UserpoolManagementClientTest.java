package com.approw.core;

import com.approw.core.graphql.GraphQLException;
import com.approw.core.graphql.GraphQLResponse;
import com.approw.core.http.Callback;
import com.approw.core.mgmt.ManagementClient;
import com.approw.core.mgmt.UserpoolManagementClient;
import com.approw.core.types.CommonMessage;
import com.approw.core.types.Env;
import com.approw.core.types.UpdateUserpoolInput;
import com.approw.core.types.UserPool;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class UserpoolManagementClientTest {
    
    private UserpoolManagementClient userpoolManagementClient;

    @Before
    public void before() throws IOException, GraphQLException {
        ManagementClient managementClient = new ManagementClient("6006d6820d57817ed7a95f84", "4bdb08da88e47a978001d236a09e27f9");
        managementClient.setHost("https://core.authing.cn");
        this.userpoolManagementClient = managementClient.userpool();

        managementClient.requestToken().execute();
    }

    @Test
    public void detail() throws ExecutionException, InterruptedException {
        CompletableFuture<UserPool> future = new CompletableFuture<>();
        this.userpoolManagementClient.detail().enqueue(new Callback<UserPool>() {
            @Override
            public void onSuccess(UserPool result) {
                future.complete(result);
                System.out.println(result);
            }

            @Override
            public void onFailure(@Nullable GraphQLResponse.ErrorInfo error) {
                System.out.println(error);
            }
        });
        Assert.assertNotNull(future.get());
    }

    @Test
    public void update() throws IOException, GraphQLException {
        UserPool userPool = this.userpoolManagementClient.update(new UpdateUserpoolInput().withDescription("desc")).execute();
        Assert.assertNotNull(userPool);
    }

    @Test
    public void listEnv() throws IOException {
        List<Env> list = this.userpoolManagementClient.listEnv().execute();
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void addEnv() throws IOException {
        Env env = this.userpoolManagementClient.addEnv("key", "value").execute();
        Assert.assertEquals("key", env.getKey());
    }

    @Test
    public void removeEnv() throws IOException {
        CommonMessage message = this.userpoolManagementClient.removeEnv("key").execute();
        Assert.assertEquals(200, Objects.requireNonNull(message.getCode()).intValue());
    }
}
