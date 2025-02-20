package okhttp;

import dto.CarDtoApi;
import dto.ResponseMessageDto;
import dto.TokenDto;
import dto.UserDtoLombok;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utils.BaseApi;

import java.io.IOException;
import java.util.Random;

import static utils.PropertiesReader.getProperty;

public class AddNewCarTestOkHttp implements BaseApi {

    SoftAssert softAssert = new SoftAssert();
    TokenDto tokenDto;

    @BeforeClass(alwaysRun = true)
    public void login() {
        UserDtoLombok user = UserDtoLombok.builder()
                .username(getProperty("login.properties", "email"))
                .password(getProperty("login.properties", "password"))
                .build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(user), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + LOGIN)
                .post(requestBody)
                .build();
        try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                tokenDto = GSON.fromJson(response.body().string(), TokenDto.class);
                System.out.println(tokenDto.getAccessToken());
            } else {
                System.out.println("login wrong");
            }
        } catch (IOException e) {
            System.out.println("login wrong, created exception");
        }
    }

    @Test(invocationCount=2, groups = "smoke")
    public void addNewCarPositiveTest() {
        int i = new Random().nextInt(10000);
        CarDtoApi carDtoApi = CarDtoApi.builder()
                .serialNumber("number" + i)
                .manufacture("Ford")
                .model("Focus")
                .city("Haifa")
                .fuel("Electric")
                .about("about my car")
                .carClass("A")
                .seats(5)
                .year("2020")
                .pricePerDay(345.5)
                .build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(carDtoApi), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + ADD_NEW_CAR)
                .addHeader(AUTH, tokenDto.getAccessToken())
                .post(requestBody)
                .build();
        try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            System.out.println(response.isSuccessful() + " code " + response.code());
            if (response.isSuccessful()) {
                softAssert.assertEquals(response.code(), 200);
                System.out.println(response.toString());
                ResponseMessageDto responseMessageDto = GSON.fromJson(response.body().string(), ResponseMessageDto.class);
                softAssert.assertTrue(responseMessageDto.getMessage().equals("Car added successfully"));
                softAssert.assertAll();
            } else
                Assert.fail("response status code --> " + response.code());
        } catch (IOException e) {
            Assert.fail("created exception");
        }
    }
}
