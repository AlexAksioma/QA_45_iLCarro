package tests;

import dto.CarDto;
import dto.UserDtoLombok;
import manager.ApplicationManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LetCarWorkPage;
import pages.LoginPage;
import pages.SearchPage;
import utils.Fuel;

import java.util.Random;

public class AddNewCarTests extends ApplicationManager {
    LoginPage loginPage;
    LetCarWorkPage letCarWorkPage;

    @BeforeMethod
    public void login() {
        UserDtoLombok user = UserDtoLombok.builder()
                .email("alexmed123@gmail.com")
                .password("Qwerty123!")
                .build();
        new SearchPage(getDriver()).clickBtnLogin();
        loginPage = new LoginPage(getDriver());
        loginPage.typeLoginForm(user);
        loginPage.clickBtnYalla();
        if (loginPage.isPopUpLoginMessagePresent("Logged in success")) {
            System.out.println("login success");
            loginPage.clickBtnOK();
            loginPage.clickBtnLetCarWork();
        } else
            System.out.println("Something went wrong");
    }

    @Test
    public void addNewCarPositiveTest() {
        CarDto car = CarDto.builder()
                .serialNumber(new Random().nextInt(1000) + "-055")
                .city("Haifa")
                .manufacture("Mazda")
                .model("CX-90")
                .year("2022")
                .fuel(Fuel.HYBRID.getLocator())
                .seats(4)
                .carClass("A")
                .pricePerDay(123.99)
                .about("About my car")
                .build();
        letCarWorkPage = new LetCarWorkPage(getDriver());
        letCarWorkPage.typeLetCarWorkForm(car);
        Assert.assertTrue(letCarWorkPage
                .isPopUpMessagePresent(car.getManufacture() + " " + car.getModel() + " " + "added successful"));

    }

}
