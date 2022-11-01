package com.s4m1d.glw.authorization.service.black.box.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.s4m1d.glw.authorization.service.black.box.tests.datamodel.AccountCreationRequestBody;
import com.s4m1d.glw.authorization.service.black.box.tests.datamodel.AccountCreationResponseBody;
import com.s4m1d.glw.authorization.service.black.box.tests.datamodel.AccountRemovalRequestBody;
import com.s4m1d.glw.authorization.service.black.box.tests.util.ObjectToStringConverter;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.s4m1d.glw.authorization.service.black.box.tests.constant.AuthorizationServiceConstants.*;

public class AccountTest {
    private static final String USER_NAME = "JohnDoe";
    private static final String PASSWORD = "iHateSins_101";
    private static final String TOKEN = "asdf-wert-yuio-zxcv";

    @BeforeMethod
    public void setUp(){
        RestAssured.port = PORT;
        RestAssured.baseURI = HOST;
    }

    @Test
    public void account_creation_test() throws JsonProcessingException {
        //prepare data
        AccountCreationRequestBody requestBody = AccountCreationRequestBody.builder()
                .userName(USER_NAME)
                .pwd(PASSWORD)
                .build();

        //request service
        String strRequestBody = ObjectToStringConverter.convert(requestBody);
        System.out.printf("sending request to %s with body %s%n", ACCOUNT_CREATE_PATH, strRequestBody);
        Response response = RestAssured.given().header("content-type", "application/json").body(strRequestBody).post(ACCOUNT_CREATE_PATH).then().statusCode(200).extract().response();
        System.out.println("Response:");
        response.prettyPrint();

        //assertions
        AccountCreationResponseBody responseBody = response.body().as(AccountCreationResponseBody.class);
        Assert.assertTrue(responseBody.isSuccess());

        //request service
        System.out.printf("sending request to %s with body %s%n", ACCOUNT_CREATE_PATH, strRequestBody);
        response = RestAssured.given().header("content-type", "application/json").body(strRequestBody).post(ACCOUNT_CREATE_PATH).then().statusCode(200).extract().response();
        System.out.println("Response:");
        response.prettyPrint();

        //assertions
        responseBody = response.body().as(AccountCreationResponseBody.class);
        Assert.assertFalse(responseBody.isSuccess());
        Assert.assertEquals(responseBody.getErrorCode(), "AC_01");
        Assert.assertEquals(responseBody.getMessage(), "Account with such name already exists");
    }

    @Test(dependsOnMethods = {"account_creation_test"})
    public void account_removal_test() throws JsonProcessingException {
        //prepare data
        AccountRemovalRequestBody requestBody = AccountRemovalRequestBody.builder()
                .token(TOKEN)
                .build();

        //request service
        String strRequestBody = ObjectToStringConverter.convert(requestBody);
        System.out.printf("sending request to %s with body %s%n", ACCOUNT_REMOVE_PATH, strRequestBody);
        Response response = RestAssured.given().header("content-type", "application/json").body(strRequestBody).post(ACCOUNT_REMOVE_PATH).then().statusCode(200).extract().response();
        System.out.println("Response:");
        response.prettyPrint();

        //assertions
        AccountCreationResponseBody responseBody = response.body().as(AccountCreationResponseBody.class);
        Assert.assertTrue(responseBody.isSuccess());

        //request service
        System.out.printf("sending request to %s with body %s%n", ACCOUNT_REMOVE_PATH, strRequestBody);
        response = RestAssured.given().header("content-type", "application/json").body(strRequestBody).post(ACCOUNT_REMOVE_PATH).then().statusCode(200).extract().response();
        System.out.println("Response:");
        response.prettyPrint();

        //assertions
        responseBody = response.body().as(AccountCreationResponseBody.class);
        Assert.assertFalse(responseBody.isSuccess());
        Assert.assertEquals(responseBody.getErrorCode(), "AR_01");
        Assert.assertEquals(responseBody.getMessage(), "No account with such user name exists");
    }
}
