package com.s4m1d.glw.authorization.service.black.box.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.s4m1d.glw.authorization.service.black.box.tests.datamodel.AccountCredentialsRequestBody;
import com.s4m1d.glw.authorization.service.black.box.tests.datamodel.AccountCreationAndRemovalResponseBody;
import com.s4m1d.glw.authorization.service.black.box.tests.datamodel.AccountRemovalRequestBody;
import com.s4m1d.glw.authorization.service.black.box.tests.datamodel.SignInResponseBody;
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
    private static final String NOT_MATCHING_PASSWORD = "iLoveHumankind_123";
    private static final String RIGHT_TOKEN = "asdf-wert-yuio-zxcv";
    private static final String WRONG_TOKEN = "qwer-asdf-jklo-poiu";

    @BeforeMethod
    public void setUp(){
        RestAssured.port = PORT;
        RestAssured.baseURI = HOST;
    }

    @Test
    public void account_creation_test() throws JsonProcessingException {
        //prepare data
        AccountCredentialsRequestBody requestBody = AccountCredentialsRequestBody.builder()
                .userName(USER_NAME)
                .password(PASSWORD)
                .build();

        //request service
        String strRequestBody = ObjectToStringConverter.convert(requestBody);
        System.out.printf("sending request to %s with body %s%n", ACCOUNT_CREATE_PATH, strRequestBody);
        Response response = RestAssured.given().header("content-type", "application/json").body(strRequestBody).post(ACCOUNT_CREATE_PATH).then().statusCode(200).extract().response();
        System.out.println("Response:");
        response.prettyPrint();

        //assertions
        AccountCreationAndRemovalResponseBody responseBody = response.body().as(AccountCreationAndRemovalResponseBody.class);
        Assert.assertTrue(responseBody.isSuccess());

        //request service
        System.out.printf("sending request to %s with body %s%n", ACCOUNT_CREATE_PATH, strRequestBody);
        response = RestAssured.given().header("content-type", "application/json").body(strRequestBody).post(ACCOUNT_CREATE_PATH).then().statusCode(200).extract().response();
        System.out.println("Response:");
        response.prettyPrint();

        //assertions
        responseBody = response.body().as(AccountCreationAndRemovalResponseBody.class);
        Assert.assertFalse(responseBody.isSuccess());
        Assert.assertEquals(responseBody.getErrorCode(), "CRED_01");
        Assert.assertEquals(responseBody.getMessage(), "Account with such name already exists");
    }

    @Test(dependsOnMethods = {"account_creation_test"})
    public void sign_in_test() throws JsonProcessingException {
        //prepare data
        AccountCredentialsRequestBody requestBody = AccountCredentialsRequestBody.builder()
                .userName(USER_NAME)
                .password(PASSWORD)
                .build();

        //request service
        String strRequestBody = ObjectToStringConverter.convert(requestBody);
        System.out.printf("sending request to %s with body %s%n", SIGN_IN_PATH, strRequestBody);
        Response response = RestAssured.given().header("content-type", "application/json").body(strRequestBody).post(SIGN_IN_PATH).then().statusCode(200).extract().response();
        System.out.println("Response:");
        response.prettyPrint();

        //assertions
        SignInResponseBody responseBody = response.body().as(SignInResponseBody.class);
        Assert.assertTrue(responseBody.isSuccess());
        Assert.assertEquals(responseBody.getToken(), RIGHT_TOKEN);

        //prepare data
        requestBody = AccountCredentialsRequestBody.builder()
                .userName(USER_NAME)
                .password(NOT_MATCHING_PASSWORD)
                .build();

        //request service
        strRequestBody = ObjectToStringConverter.convert(requestBody);
        System.out.printf("sending request to %s with body %s%n", SIGN_IN_PATH, strRequestBody);
        response = RestAssured.given().header("content-type", "application/json").body(strRequestBody).post(SIGN_IN_PATH).then().statusCode(200).extract().response();
        System.out.println("Response:");
        response.prettyPrint();

        //assertions
        responseBody = response.body().as(SignInResponseBody.class);
        Assert.assertFalse(responseBody.isSuccess());
        Assert.assertEquals(responseBody.getErrorCode(), "CRED_03");
        Assert.assertEquals(responseBody.getMessage(), "Wrong password");
    }

    @Test(dependsOnMethods = {"account_creation_test", "sign_in_test"})
    public void account_removal_test() throws JsonProcessingException {
        //prepare data
        AccountRemovalRequestBody requestBody = AccountRemovalRequestBody.builder()
                .token(WRONG_TOKEN)
                .build();

        //request service
        String strRequestBody = ObjectToStringConverter.convert(requestBody);
        System.out.printf("sending request to %s with body %s%n", ACCOUNT_REMOVE_PATH, strRequestBody);
        Response response = RestAssured.given().header("content-type", "application/json").body(strRequestBody).post(ACCOUNT_REMOVE_PATH).then().statusCode(200).extract().response();
        System.out.println("Response:");
        response.prettyPrint();

        //assertions
        AccountCreationAndRemovalResponseBody responseBody = response.body().as(AccountCreationAndRemovalResponseBody.class);
        Assert.assertFalse(responseBody.isSuccess());
        Assert.assertEquals(responseBody.getErrorCode(), "AUTHENTICATION_01");
        Assert.assertEquals(responseBody.getMessage(), "No active session with this token");

        //prepare data
        requestBody = AccountRemovalRequestBody.builder()
                .token(RIGHT_TOKEN)
                .build();

        //request service
        strRequestBody = ObjectToStringConverter.convert(requestBody);
        System.out.printf("sending request to %s with body %s%n", ACCOUNT_REMOVE_PATH, strRequestBody);
        response = RestAssured.given().header("content-type", "application/json").body(strRequestBody).post(ACCOUNT_REMOVE_PATH).then().statusCode(200).extract().response();
        System.out.println("Response:");
        response.prettyPrint();

        //assertions
        responseBody = response.body().as(AccountCreationAndRemovalResponseBody.class);
        Assert.assertTrue(responseBody.isSuccess());

        //request service
        System.out.printf("sending request to %s with body %s%n", ACCOUNT_REMOVE_PATH, strRequestBody);
        response = RestAssured.given().header("content-type", "application/json").body(strRequestBody).post(ACCOUNT_REMOVE_PATH).then().statusCode(200).extract().response();
        System.out.println("Response:");
        response.prettyPrint();

        //assertions
        responseBody = response.body().as(AccountCreationAndRemovalResponseBody.class);
        Assert.assertFalse(responseBody.isSuccess());
        Assert.assertEquals(responseBody.getErrorCode(), "CRED_02");
        Assert.assertEquals(responseBody.getMessage(), "No account with such user name exists");
    }
}
