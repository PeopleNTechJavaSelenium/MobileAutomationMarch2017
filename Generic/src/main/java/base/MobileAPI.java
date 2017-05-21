package base;

import ExtentReport.ExtentManager;
import ExtentReport.ExtentTestManager;
import ExtentReport.TestLogger;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.LogStatus;
import io.appium.java_client.*;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.*;
import org.testng.annotations.Optional;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.Key;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Created by mrahman on 5/19/17.
 */
public class MobileAPI {

    public static ExtentReports extent;

    @BeforeSuite
    public void extentSetup(ITestContext context) {
        ExtentManager.setOutputDirectory(context);
        extent = ExtentManager.getInstance();
    }

    @BeforeMethod
    public void startExtent(Method method) {
        String className = convertCamelCase(method.getDeclaringClass().getSimpleName());
        String methodName = convertCamelCase(method.getName()).toLowerCase();

        ExtentTestManager.startTest( methodName );
        ExtentTestManager.getTest().assignCategory(className);
    }

    protected String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    @AfterMethod
    public void afterEachTestMethod(ITestResult result) {

        ExtentTestManager.getTest().getTest().setStartedTime(getTime(result.getStartMillis()));
        ExtentTestManager.getTest().getTest().setEndedTime(getTime(result.getEndMillis()));
        for (String group : result.getMethod().getGroups()) {
            ExtentTestManager.getTest().assignCategory(group);
        }
        if (result.getStatus() == 1) {
            ExtentTestManager.getTest().log(LogStatus.PASS, "Test Passed");
        } else if (result.getStatus() == 2) {
            ExtentTestManager.getTest().log(LogStatus.FAIL, getStackTrace(result.getThrowable()));
        } else if (result.getStatus() == 3) {
            ExtentTestManager.getTest().log(LogStatus.SKIP, "Test Skipped");
        }
        ExtentTestManager.endTest();
        extent.flush();
        if (result.getStatus() == ITestResult.FAILURE) {
            captureScreenShot(result.getName(),ad);
        }
        ad.removeApp("nyp.apk");
        ad.quit();
    }

    @AfterSuite
    public void generateReport() {
        extent.close();
    }

    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }

    public static AppiumDriver ad = null;
    public File appDirectory = null;
    public File findApp = null;

    @Parameters({"OS","appType","deviceType", "deviceName","version", "moduleName","appName"})
    @BeforeMethod
    public void setUp(@Optional("android")String OS,@Optional("mobile") String appType,@Optional("real device") String deviceType,
                      @Optional("") String deviceName, @Optional("") String version,@Optional("") String moduleName,
                      @Optional("") String appName)throws IOException,InterruptedException{

        if(OS.equalsIgnoreCase("ios")){
            if(appType.contains("iPhone")){
                appDirectory = new File(moduleName+"src/app");
                findApp = new File(appDirectory,"UICatalog6.1.app.zip");
                if(deviceType.equalsIgnoreCase("RealDevice")){
                    ad = setUpiOsEnv(deviceName,version);
                }else if (deviceType.equalsIgnoreCase("Simulator")){
                    ad = setUpiOsEnv(deviceName,version);
                }

            }else if(appType.equalsIgnoreCase("iPad 2")){
                appDirectory = new File(moduleName+"src/app");
                findApp = new File(appDirectory,"UICatalog6.1.app.zip");
                if(deviceType.contains("RealDevice")){
                    ad = setUpiOsEnv(deviceName,version);
                }else if (deviceType.equalsIgnoreCase("Simulator")){
                    ad = setUpiOsEnv(deviceName,version);
                }
            }

        }else if(OS.equalsIgnoreCase("Android")){
            if(appType.contains("Phone")){
                appDirectory = new File("src/app");
                findApp = new File(appDirectory,appName);
                if(deviceType.equalsIgnoreCase("RealDevice")){
                    ad = setUpAndroidEnv(deviceName,version);
                }else if (deviceType.equalsIgnoreCase("Emulator")){

                    ad =setUpAndroidEnv(deviceName,version);
                }

            }else if(OS.equalsIgnoreCase("Tablets")){
                if(deviceType.equalsIgnoreCase("RealDevice")){
                    ad = setUpAndroidEnv(deviceName,version);
                }else if (deviceType.equalsIgnoreCase("Emulator")){
                    ad = setUpAndroidEnv(deviceName,version);
                }
            }
        }
    }

    public AppiumDriver setUpiOsEnv(String deviceName,String version)throws IOException,InterruptedException{
        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
        cap.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.IOS);
        cap.setCapability(MobileCapabilityType.PLATFORM_VERSION, version);
        cap.setCapability(MobileCapabilityType.APP, findApp.getAbsolutePath());
        ad = new IOSDriver(new URL("http://127.0.0.1:4723/wd/hub"), cap);
        ad.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        return ad;

    }
    public AppiumDriver setUpAndroidEnv(String deviceName,String version)throws IOException,InterruptedException{
        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
        cap.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.ANDROID);
        cap.setCapability(MobileCapabilityType.PLATFORM_VERSION, version);
        cap.setCapability(MobileCapabilityType.APP, findApp.getAbsolutePath());
        ad = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), cap);
        ad.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        return ad;

    }

    protected static void skip() throws SkipException {
        throw new SkipException("Skipping this test");
    }

    private String convertCamelCase(String words){
        return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(words), ' ');
    }

    private static final List<Function<String, By>> resolvers = Arrays.asList(By::id, By::className, By::xpath);

    public WebElement locateElement(AppiumDriver ad, String locator) {
        WebElement el = null;

        for (Function<String, By> resolver : resolvers) {
            try {
                el = ad.findElement(resolver.apply(locator));
                if (el != null) {
                    break;
                }
            } catch (Exception e) {
                if (locator == null) {
                    System.out.println("Ensure getResource() is fetching a valid resource in the locator resource file");
                }
                System.out.println(e + "Locator: " + locator);
            }
        }
        return el;
    }

    public void clickOnElement(AppiumDriver ad, String locator) {
        locateElement(ad, locator).click();

    }

    public void enterValueOnElement(AppiumDriver ad, String locator, String value) {
        locateElement(ad, locator).sendKeys(value);

    }

    public void swipeOnElement(AppiumDriver ad, String locator) {
        locateElement(ad, locator);

    }

    public void waitUntilPresence(AppiumDriver ad, String locator) {
        WebDriverWait wait = new WebDriverWait(ad, 45);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(locator)));
    }

    public boolean validateClickable(AppiumDriver ad, WebElement element) {
        try {
            WebDriverWait wait = new WebDriverWait(ad, 10);
            wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (Error e) {
            return false;
        }
        return true;
    }


    public void waitUntilClickable(AppiumDriver ad, String locator) {
        WebDriverWait wait = new WebDriverWait(ad, 45);
        //wait.until(ExpectedConditions.elementToBeClickable(By.id(locator)));
    }

    public void clickWhenClickable(AppiumDriver ad, String locator) {
        WebDriverWait wait = new WebDriverWait(ad, 45);
        wait.until(ExpectedConditions.elementToBeClickable(By.id(locator))).click();
    }

    public void clickOnElementWithText(List<WebElement> elementList, String targetText) {
        for (WebElement element : elementList) {
            if (element.getText().contains(targetText)) {
                try {
                    element.click();
                    break;
                } catch (Exception ignore) {
                }
            }
        }
    }

    public void clearField(AppiumDriver ad, String locator) {
        locateElement(ad, locator).clear();
    }

    public void clickByXpathOnIOS(AppiumDriver ad, String locator) {
        //new TouchAction((MobileDriver) ad).tap(By.xpath(locator)).perform();
        ad.tap(1, ad.findElement(MobileBy.xpath(locator)), 200);
    }

    public void sleep(int sec) {
        try {
            Thread.sleep(1000 * sec);
        } catch (InterruptedException e) {
        }
    }

    public void type(AppiumDriver ad, String locator, String value) {
        locateElement(ad, locator).sendKeys(value);
    }

    public List<String> getTexts(List<WebElement> elements) {
        List<String> text = new ArrayList<String>();
        for (WebElement element : elements) {
            text.add(element.getText());
        }
        return text;
    }

    public void touch(AppiumDriver ad, WebElement locator) {
        TouchAction touchAction = new TouchAction(ad);
        touchAction.moveTo(locator);
    }

    private Dimension getDemensions(AppiumDriver ad) {
        Dimension size = ad.manage().window().getSize();
        return size;
    }

    private HashMap<String, Integer> setXandY(AppiumDriver ad, Double startX, Double endX, Double startY, Double endY) {
        HashMap<String, Integer> dimensions = new HashMap<String, Integer>();
        Dimension size = getDemensions(ad);
        dimensions.put("startX", (int) (size.width * startX)); // 0.8);
        dimensions.put("startY", (int) (size.width * endX)); // 0.20
        dimensions.put("endX", (int) (size.height / startY)); // 2
        dimensions.put("endY", (int) (size.height / endY)); // 2
        return dimensions;
    }

    public void swipeRightToLeft(AppiumDriver ad, Double startX, Double endX, Double startY, Double endY) {
        HashMap<String, Integer> d = setXandY(ad, startX, endX, startY, endY);
        ad.swipe(d.get("startX"), d.get("startY"), d.get("endX"), d.get("startY"), 3000);
    }

    public void swipeLeftToRight(AppiumDriver ad, Double startX, Double endX, Double startY, Double endY) {
        HashMap<String, Integer> d = setXandY(ad, startX, endX, startY, endY);
        ad.swipe(d.get("endX"), d.get("startY"), d.get("startX"), d.get("startY"), 3000);
    }


    public void swipeUp(AppiumDriver ad, Double startX, Double endX, Double startY, Double endY) {
        HashMap<String, Integer> d = setXandY(ad, startX, endX, startY, endY);
        ad.swipe(d.get("startX"), d.get("startY"), d.get("endX"), d.get("endT"), 3000);
    }

    public void swipeDown(AppiumDriver ad, Double startX, Double endX, Double startY, Double endY) {
        //Get the size of screen.
        HashMap<String, Integer> d = setXandY(ad, startX, endX, startY, endY);
        ad.swipe(d.get("startX"), d.get("startY"), d.get("endX"), d.get("endT"), 3000);
    }

    public String getText(AppiumDriver ad, String locator) {
        return locateElement(ad, locator).getText();
    }

    public void sleepFor(int time) {
        try {
            Thread.sleep(1000 * time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tapOn(AppiumDriver ad, String element) {
        new TouchAction((MobileDriver) ad.findElement(By.xpath(element)));
    }

    public void captureScreenShot(String className, AppiumDriver ad) {
        String destDir = "";
        File scrFile = ((TakesScreenshot) ad).getScreenshotAs(OutputType.FILE);
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy__hh_mm_ssaa");
        destDir = "screenshots/BaseLine";
        new File(destDir).mkdirs();
        String destFile = className + ".png";
        try {
            FileUtils.copyFile(scrFile, new File(destDir + "/" + destFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void captureScreenShot(ITestResult result, String status) {
        String destDir = "";
        String passfailMethod = result.getMethod().getRealClass().getSimpleName() + "." + result.getMethod().getMethodName();
        File scrFile = ((TakesScreenshot) ad).getScreenshotAs(OutputType.FILE);
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy__hh_mm_ssaa");
        if (status.equalsIgnoreCase("fail")) {
            destDir = "screenshots/Failures";
        } else if (status.equalsIgnoreCase("pass")) {
            destDir = "screenshots/Success";
        }
        new File(destDir).mkdirs();
        String destFile = passfailMethod + " - " + dateFormat.format(new Date()) + ".png";

        try {
            FileUtils.copyFile(scrFile, new File(destDir + "/" + destFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void swipeUpNDown(AppiumDriver ad) {
        //Get the size of screen.
        Dimension size = ad.manage().window().getSize();
        //Find swipe start and end point from screen's with and height.
        //Find starty point which is at bottom side of screen.
        int startx = (int) (size.width / 2);
        //Find vertical point where you wants to swipe. It is in middle of screen height.
        int starty = (int) (size.height * 0.80);
        //Find endx point which is at left side of screen.
        int endx = (int) (size.width * 0.30);
        //Find endy point which is at top side of screen.
        int endy = (int) (size.height * 0.20);
        //ad.swipe(startx, endy, startx, starty, 3000);
        ad.swipe(startx, endy, startx, starty, 3000);
    }

    public void scrollAndClickByName(String locator){
        ad.scrollTo(locator).click();
    }

    public void clickByXpath(String locator){
        ad.findElement(By.xpath(locator)).click();
    }
    public void clickByXpathWebElement(WebElement locator){
        locator.click();
    }
    public void typeByXpath(String locator, String value, Key key){
        ad.findElement(By.xpath(locator)).sendKeys(value);
    }

    public void typeByXpath(String locator, String value){
        ad.findElementByXPath(locator).sendKeys(value);
    }

    public static void scrollKeys(AppiumDriver driver, String[] list, String parent) {
        System.out.println("Starting the process");
        for (int i = 0; i < list.length; i++) {
            MobileElement we = (MobileElement) driver.findElementByXPath(parent+"/UIAPickerWheel["+(i+1)+"]");
            we.sendKeys(list[i]);
        }
        System.out.println("Ending Process");
    }
    public void scrollToElement(AppiumDriver driver, String text){
        MobileElement we = (MobileElement) driver.findElementByXPath(text);
        driver.scrollTo(we.getText());
    }
}
