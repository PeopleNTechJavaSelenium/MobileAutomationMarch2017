package ExtentReport;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.relevantcodes.extentreports.model.Test;

/**
 * Created by mrahman on 05/13/17.
 */
public class ExtentTestUpdate extends ExtentTest {

    private LogStatus runStatus;
    private Test test;
    public ExtentReports extentReports = new ExtentReports("");

    public ExtentTestUpdate(String testName, String description) {
        super(testName,description);
    }

    public void startTest(String className, String testName, String description) {
        extentReports.startTest(testName);
    }

}
