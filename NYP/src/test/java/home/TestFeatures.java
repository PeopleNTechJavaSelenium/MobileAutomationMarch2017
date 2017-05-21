package home;

import ExtentReport.TestLogger;
import base.MobileAPI;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import pages.MainPage;

import static base.MobileAPI.ad;

/**
 * Created by mrahman on 5/20/17.
 */
public class TestFeatures extends MobileAPI {

    @Test
    public void home(){
        TestLogger.log("app is launched");
        MainPage mainPage = PageFactory.initElements(ad, MainPage.class);
        mainPage.goToArticles();

    }

}
