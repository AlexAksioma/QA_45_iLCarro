package okhttp;

import dto.ErrorMessageDto;
import dto.TokenDto;
import dto.UserDtoLombok;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utils.BaseApi;

import java.io.IOException;
import java.util.Random;

public class RegistrationTestsOkHttp implements BaseApi {

    SoftAssert softAssert = new SoftAssert();

    @Test
    public void registrationPositiveTest() {
        int i = new Random().nextInt(1000) + 1000;
        UserDtoLombok user = UserDtoLombok.builder()
                .firstName("Bob")
                .lastName("Doe")
                .username(i + "bob_mail@mail.com")
                .password("Pass123!")
                .build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(user), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + REGISTRATION)
                .post(requestBody)
                .build();
        Response response;
        try {
            response = OK_HTTP_CLIENT.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.isSuccessful());
        System.out.println(response.toString());
        System.out.println(response.code());
        try {
            System.out.println(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals(response.code(), 200);
    }

    @Test
    public void registrationPositiveTestValidateToken() {
        int i = new Random().nextInt(1000) + 1000;
        UserDtoLombok user = UserDtoLombok.builder()
                .firstName("Bob")
                .lastName("Doe")
                .username(i + "bob_mail@mail.com")
                .password("Pass123!")
                .build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(user), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + REGISTRATION)
                .post(requestBody)
                .build();
        //Response response;
        try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                TokenDto tokenDto = GSON.fromJson(response.body().string(), TokenDto.class);
                System.out.println(tokenDto.toString());
                Assert.assertFalse(tokenDto.getAccessToken().isBlank());
            } else {
                ErrorMessageDto errorMessageDto = GSON.fromJson(response.body().string(), ErrorMessageDto.class);
                System.out.println(errorMessageDto.toString());
                Assert.fail("Status code response --> " + response.code());
            }
        } catch (IOException e) {
            Assert.fail("Created exception");
            throw new RuntimeException(e);
        }
    }

    @Test
    public void registrationNegativeTest_wrongPassword_400() {
        int i = new Random().nextInt(1000) + 1000;
        UserDtoLombok user = UserDtoLombok.builder()
                .firstName("Bob")
                .lastName("Doe")
                .username(i + "bob_mail@mail.com")
                .password("Pass123qw")
                .build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(user), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + REGISTRATION)
                .post(requestBody)
                .build();
        try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                ErrorMessageDto errorMessageDto = GSON.fromJson(response.body().string(), ErrorMessageDto.class);
                System.out.println(errorMessageDto.toString());
                softAssert.assertEquals(response.code(), 400);
                softAssert.assertTrue(errorMessageDto.getError().equals("Bad Request"));
                softAssert.assertTrue(errorMessageDto.getMessage().toString().contains("At least 8 characters;"));
                softAssert.assertAll();
            } else {
                Assert.fail("Status code response --> " + response.code());
            }
        } catch (IOException e) {
            Assert.fail("Created exception");
            throw new RuntimeException(e);
        }
    }
}
