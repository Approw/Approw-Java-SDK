package com.approw.core;

import com.approw.core.graphql.GraphQLException;
import com.approw.core.mgmt.ApplicationManagementClient;
import com.approw.core.mgmt.ManagementClient;
import com.approw.core.types.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class ApplicationManagementClientTest {

    private ApplicationManagementClient applicationManagementClient;

    private ManagementClient managementClient;

//    private final String APP_ID = "605084fe415a744f79029f09";
    private final String APP_ID = "606ee72d3ce1b4694360edc5";

    @Before
    public void before() throws IOException, GraphQLException {

        String userPoolId = "5f45cad3ece50b62de2a02cd";
//        String userPoolId = "606d9b76fe845b4cde293413";
        String userPoolSecret = "624cb39b07ffd29b946112ea82f5b50e";
//        String userPoolSecret = "ca814ba7921c87500f301c12f78b29eb";

        ManagementClient managementClient = new ManagementClient(userPoolId, userPoolSecret);
//        managementClient.setHost("http://localhost:3000");
        managementClient.setHost("https://core.authing.cn");

        this.managementClient = managementClient;
        this.applicationManagementClient = managementClient.application();
    }

    @Test
    public void create() throws IOException {
        CreateAppParams params = new CreateAppParams(
                "APP_NAME",
                "Identiflistier",
                Arrays.asList("www.xxxxx.com")
        );

        Application app = managementClient.application().create(params).execute();

        Assert.assertNotNull(app);
    }

    public void delete() throws IOException {
        CreateAppParams params = new CreateAppParams(
                "APP_NAME",
                "Identifier",
                Arrays.asList("www.xxxxx.com")
        );

        Application app = managementClient.application().create(params).execute();

        Boolean deleted = managementClient.application().delete(app.getId()).execute();

        Assert.assertTrue(deleted);

    }

    @Test
    public void list() throws IOException {
        List<Application> result = this.applicationManagementClient.list(1, 2).execute();

        Assert.assertTrue(result.size() != 0);
    }

    @Test
    public void findById() throws IOException {
        Application application = this.applicationManagementClient.findById(APP_ID).execute();
        Assert.assertEquals(this.APP_ID, application.getId());
    }

    @Test
    public void activeUsers() throws IOException {
        IActiveUsersParam param = new IActiveUsersParam(APP_ID);

        Pagination<ActiveUser> pagination = managementClient.application().activeUsers(param).execute();

        Assert.assertNotNull(pagination.getList());
    }

    @Test
    public void refreshApplicationSecret() throws IOException {
        Application application = managementClient.application().refreshApplicationSecret(APP_ID).execute();

        Assert.assertNotNull(application);
    }

    @Test
    public void createResource() throws IOException {
        IResourceResponse res = managementClient.application().createResource(
                APP_ID,
                new ResourceOptionsParams(
                        TestUtils.createRandomString(6),
                        ResourceType.DATA,
                        Arrays.asList(new IAction("name1"), new IAction("name1"))
                )
        ).execute();

        Assert.assertNotNull(res);
    }

    private IResourceResponse createResponse(String code) throws IOException {

        return managementClient.application().createResource(
                APP_ID,
                new ResourceOptionsParams(
                        code,
                        ResourceType.DATA,
                        Arrays.asList(new IAction("name1"), new IAction("name1"))
                )
        ).execute();
    }

    private IResourceResponse createResponse() throws IOException {

        return managementClient.application().createResource(
                APP_ID,
                new ResourceOptionsParams(
                        TestUtils.createRandomString(6),
                        ResourceType.DATA,
                        Arrays.asList(new IAction("name1"), new IAction("name1"))
                )
        ).execute();
    }

    @Test
    public void listResources() throws IOException {
        ListResourcesParams params = new ListResourcesParams(APP_ID);
        params.setLimit(1);
        params.setPage(1);
        Pagination<IResourceResponse> res = managementClient.application().listResources(params).execute();

        Assert.assertNotNull(res);
    }

    @Test
    public void updateResource() throws IOException {
        IResourceResponse create = createResponse();

        String description = "description description description description";

        ResourceOptionsParams params = new ResourceOptionsParams(
                create.getCode(),
                ResourceType.API,
                create.getActions(),
                description
        );

        IResourceResponse updateRes = managementClient
                .application()
                .updateResource(APP_ID, params)
                .execute();

        Assert.assertEquals(updateRes.getDescription(), description);

        IResourceResponse res = managementClient.application().findResourceByCode(
                APP_ID,
                create.getCode()
        ).execute();

        Assert.assertEquals(res.getId(), updateRes.getId());
    }

    @Test
    public void deleteResource() throws IOException {
        IResourceResponse resource = createResponse();

        Boolean deleted = managementClient
                .application()
                .deleteResource(APP_ID, resource.getCode())
                .execute();

        Assert.assertTrue(deleted);

        IResourceResponse res = managementClient
                .application()
                .findResourceByCode(APP_ID, resource.getCode())
                .execute();

        Assert.assertNotNull(res.getId());

    }

    @Test
    public void allowAccess() throws IOException {
        IAccessPolicyParams params = new IAccessPolicyParams(
                TargetTypeEnum.GROUP,
                Arrays.asList("1", "2", "3")
        );
        Boolean res = managementClient.application().allowAccess(
                        APP_ID,
                        params
                )
                .execute();

        Assert.assertTrue(res);

        Boolean deleted = managementClient.application().deleteAccessPolicy(
                APP_ID,
                params
        ).execute();

        Assert.assertTrue(deleted);
    }

    @Test
    public void denyAccess() throws IOException {
        Boolean res = managementClient
                .application()
                .denyAccess(
                        APP_ID,
                        new DenyAccessParams(
                                TargetTypeEnum.GROUP,
                                Arrays.asList("1", "2", "3")
                        )
                )
                .execute();

        Assert.assertTrue(res);
    }

    private Role createRoleE() throws IOException, GraphQLException {
        return managementClient
                .application()
                .createRole(
                        APP_ID,
                        new CreateRoleParams(TestUtils.createRandomString(6))
                )
                .execute();
    }

    @Test
    public void createRole() throws IOException, GraphQLException {
        Role role = managementClient
                .application()
                .createRole(
                        APP_ID,
                        new CreateRoleParams(TestUtils.createRandomString(6))
                )
                .execute();

        Assert.assertNotNull(role.getCode());
    }

    @Test
    public void deleteRole() throws IOException, GraphQLException {
        Role role = this.createRoleE();

        CommonMessage execute = managementClient
                .application()
                .deleteRole(APP_ID, role.getCode())
                .execute();

        Assert.assertEquals(execute.getCode().intValue(), 200);

        Role findRole = managementClient.application().findRole(APP_ID, role.getCode()).execute();

        System.out.println(findRole);
        Assert.assertNull(findRole);
    }

    @Test
    public void deleteRoles() throws IOException, GraphQLException {
        int index = 0;

        ArrayList<String> codeList = new ArrayList<>();

        while (index < 5) {
            codeList.add(this.createRoleE().getCode());
            index ++;
        }

        CommonMessage execute = managementClient
                .application()
                .deleteRoles(APP_ID, codeList)
                .execute();

        Assert.assertEquals(execute.getCode().intValue(), 200);
    }

    @Test
    public void updateRole() throws IOException, GraphQLException {
        Role role = this.createRoleE();

        String description = "description description description description";

        Role updateRole = managementClient.application()
                .updateRole(
                        APP_ID,
                        new UpdateRoleParams(
                                role.getCode(),
                                description
                        )
                )
                .execute();

        Assert.assertEquals(updateRole.getDescription(), description);

        UpdateRoleParams updateRoleParams = new UpdateRoleParams(role.getCode());
        String newCode = TestUtils.createRandomString(6);

        updateRoleParams.setNewCode(newCode);
        Role role1 = managementClient
                .application()
                .updateRole(
                        APP_ID,
                        updateRoleParams
                )
                .execute();

        Assert.assertEquals(role1.getCode(), newCode);
    }

    @Test
    public void getRoles() throws IOException, GraphQLException {
        Role roleE = this.createRoleE();
        PaginatedRoles roleList = managementClient
                .application()
                .getRoles(APP_ID)
                .execute();

        // ??????????????????????????? ????????? ???????
        Assert.assertEquals(roleList.getList().get(0).getCode(), roleE.getCode());
        Assert.assertNotEquals(roleList.getTotalCount(), 0);

        PageOptions page = new PageOptions();

        page.setLimit(1);
        PaginatedRoles roleListPageTest = managementClient
                .application()
                .getRoles(
                        APP_ID,
                        page
                )
                .execute();

        Assert.assertEquals(roleListPageTest.getList().size(), 1);
    }

    @Test
    public void enableAccessPolicy() throws IOException {
        IAccessPolicyParams params = new IAccessPolicyParams(
                TargetTypeEnum.GROUP,
                Arrays.asList("xxxxx", "xxxxx")
        );
        Boolean enabled = managementClient.application().enableAccessPolicy(
                APP_ID,
                params
        ).execute();

        Boolean disabled = managementClient.application().disableAccessPolicy(
                APP_ID,
                params
        ).execute();

        Assert.assertTrue(enabled);
        Assert.assertTrue(disabled);
    }

    @Test
    public void updateDefaultAccessPolicy() throws IOException {
        Application app = managementClient.application().updateDefaultAccessPolicy(
                APP_ID,
                DefaultStrategy.DENY_ALL
        ).execute();

        Assert.assertNotNull(app);
    }

    @Test
    public void createAgreement() throws IOException {
        String title = " this title ";
        AgreementParams params = new AgreementParams(title);

        AgreementDetail agreement = managementClient.application().createAgreement(
                APP_ID,
                params
        ).execute();

        Assert.assertEquals(agreement.getTitle(), title);
    }

    private AgreementDetail createAgreementE() throws IOException {
        String title = " this title ";
        AgreementParams params = new AgreementParams(title);

        return managementClient.application().createAgreement(
                APP_ID,
                params
        ).execute();
    }

    @Test
    public void modifyAgreement() throws IOException {
        AgreementDetail agreement = this.createAgreementE();

        String title = "new agreement title";

        AgreementDetail res = managementClient.application().modifyAgreement(
                APP_ID,
                agreement.getId(),
                new AgreementParams(title)
        ).execute();

        Assert.assertEquals(res.getTitle(), title);
    }

    @Test
    public void listAgreement() throws IOException {
        AgreementDetail agreement = this.createAgreementE();

        Pagination<AgreementDetail> listAgreement = managementClient.application()
                .listAgreement(APP_ID).execute();

        Assert.assertNotEquals(listAgreement.getTotalCount(), 0);
    }

    @Test
    public void deleteAgreement() throws IOException {
        AgreementDetail agreement = this.createAgreementE();

        Boolean deleted = managementClient.application()
                .deleteAgreement(APP_ID, agreement.getId()).execute();

        Assert.assertTrue(deleted);
    }

    @Test
    public void sortAgreement() throws IOException {
        Pagination<AgreementDetail> listAgreement = managementClient.application()
                .listAgreement(APP_ID).execute();

        List<String> list = Arrays.asList("id_1", "id_2");

        Boolean res = managementClient.application().sortAgreement(
                APP_ID,
                list
        ).execute();
    }

    @Test
    public void testToken() throws IOException, GraphQLException {
        String userPoolId = "606d9b76fe845b4cde293413";
        String userPoolSecret = "ca814ba7921c87500f301c12f78b29eb";

        ManagementClient managementClient = new ManagementClient(userPoolId, userPoolSecret);
        managementClient.setHost("http://localhost:3000");

        managementClient.application()
                .listAgreement(APP_ID).execute();

        System.out.println(managementClient.getToken());
        System.out.println(managementClient.getAccessTokenExpiresAt());

        managementClient.application()
                .listAgreement(APP_ID).execute();
    }
}
