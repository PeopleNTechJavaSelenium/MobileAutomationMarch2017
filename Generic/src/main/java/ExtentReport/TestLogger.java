package ExtentReport;

import com.relevantcodes.extentreports.LogStatus;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;

/**
 * Created by mrahman on 05/13/17.
 */

public class TestLogger {

    public static void log(final String message) {
        Reporter.log(message, true);
        ExtentReport.ExtentTestManager.getTest().log(LogStatus.INFO, message + "<br>");
    }

    public static void log(final StringUtils message) {
        Reporter.log(message + "<br>", true);
        ExtentReport.ExtentTestManager.getTest().log(LogStatus.INFO, message + "<br>");
    }

    public static void log(final String message, WebDriver driver) {
        Reporter.log(message + "<br>", true);
        ExtentReport.ExtentTestManager.getTest().log(LogStatus.INFO, message + "<br>");
    }


}
