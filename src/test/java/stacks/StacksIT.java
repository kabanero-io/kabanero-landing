package stacks;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.List;

public class StacksIT {
    private static WebDriver driver;
    private static String baseUrl = "https://localhost:9443/instance/stacks/";
    private static JavascriptExecutor js;

    @BeforeClass
    public static void setupClass() throws IOException {
        // Manages the browser driver binary for us
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--ignore-certificate-errors", "--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu");
        options.setHeadless(true);
        driver = new ChromeDriver(options);
        driver.get(baseUrl);
        
        js = (JavascriptExecutor) driver;
        String singleInstanceJSON = new String(Files.readAllBytes(Paths.get("src", "test", "resources", "singleInstance.json")), StandardCharsets.UTF_8);
        String stacksJSON = new String(Files.readAllBytes(Paths.get("src", "test", "resources", "stacks.json")), StandardCharsets.UTF_8);

        // execute javascript functions to set mock data
        js.executeScript("updateStackView(JSON.parse(arguments[0]), JSON.parse(arguments[1]));", singleInstanceJSON, stacksJSON);
        js.executeScript("displayDigest(JSON.parse(arguments[0]));", singleInstanceJSON);

    }

    @AfterClass
    public static void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void hasCorrectTitleIT() {
        String expectedTitle = "Stacks";
        String actualTitle = driver.getTitle();
        assertEquals("has correct title", expectedTitle, actualTitle);
    }

    @Test
    public void hasCorrectNumStackHeadersIT() {
        WebElement table = driver.findElement(By.id("stack-table"));
        List<WebElement> tableHeaders = table.findElements(By.tagName("th"));

        int expectedNumberOfStackTableHeaders = 4;
        int actualNumberOfStackTableHeaders = tableHeaders.size();

        assertEquals("has correct number of table headers", expectedNumberOfStackTableHeaders, actualNumberOfStackTableHeaders);
    }

    @Test
    public void hasCorrectStackHeadersIT() {
        WebElement table = driver.findElement(By.id("stack-table"));
        List<WebElement> tableHeaders = table.findElements(By.tagName("th"));

        String expectedFirstHeader = "Stack";
        String expectedSecondHeader = "Version";
        String expectedThirdHeader = "Status";

        String actualFirstHeader = tableHeaders.get(0).getText();
        String actualSecondHeader = tableHeaders.get(1).getText();
        String actualThirdHeader = tableHeaders.get(2).getText();

        assertEquals("has first correct stacks table header", expectedFirstHeader, actualFirstHeader);
        assertEquals("has second correct stacks table header", expectedSecondHeader, actualSecondHeader);
        assertEquals("has third correct stacks table header", expectedThirdHeader, actualThirdHeader);

    }

    @Test
    public void hasCorrectNumStackRowsIT() {
        int expectedNumberOfStacks = 5;
        int actualNumberOfStacks = driver.findElements(By.cssSelector("#stack-table-body tr:not(.digest-notification)")).size();

        assertEquals("stacks table has correct number of table rows", expectedNumberOfStacks, actualNumberOfStacks);
    }

    @Test
    public void hasCorrectNumCuratedStacksHeadersIT() {
        WebElement table = driver.findElement(By.id("curated-stack-table"));
        List<WebElement> tableHeaders = table.findElements(By.tagName("th"));

        int expectedNumberOfStackTableHeaders = 3;
        int actualNumberOfStackTableHeaders = tableHeaders.size();

        assertEquals("curated stacks table has correct number of table headers", expectedNumberOfStackTableHeaders, actualNumberOfStackTableHeaders);
    }

    @Test
    public void hasCorrectCuratedStackHeadersIT() {
        WebElement table = driver.findElement(By.id("curated-stack-table"));
        List<WebElement> tableHeaders = table.findElements(By.tagName("th"));

        String expectedFirstHeader = "Stack";
        String expectedSecondHeader = "Version";
        String expectedThirdHeader = "Images";

        String actualFirstHeader = tableHeaders.get(0).getText();
        String actualSecondHeader = tableHeaders.get(1).getText();
        String actualThirdHeader = tableHeaders.get(2).getText();

        assertEquals("has first correct curated stacks table header", expectedFirstHeader, actualFirstHeader);
        assertEquals("has second correct curated stacks table header", expectedSecondHeader, actualSecondHeader);
        assertEquals("has third correct curated stacks table header", expectedThirdHeader, actualThirdHeader);

    }

    @Test
    public void hasCorrectNumCuratedStacksIT() {
        int expectedNumberOfCuratedStacks = 5;
        int actualNumberOfCuratedStacks = driver.findElements(By.cssSelector("#curated-stack-table-body tr")).size();
        assertEquals("curated stacks table has correct table rows", expectedNumberOfCuratedStacks, actualNumberOfCuratedStacks);
    }

    @Test
    public void hasCorrectInitDigestnValue(){
        String expectedInitDropdownDigest = "Active Digest";
        String actualInitDropdownDigest = driver.findElement(By.id("stack-govern-value-text")).getText();
        
        assertEquals("digest dropdown has correct initial value", expectedInitDropdownDigest, actualInitDropdownDigest);
    }

    @Test
    public void correctNumDigestWarningIt() throws IOException {
        setDigestValue("activeDigest.json");
        int expectedNumberOfDigestWarnings = 5;
        int actualNumberOfDigestWarnings = driver.findElements(By.cssSelector("#stack-table-body tr.digest-notification")).size();

        assertEquals("stacks table has correct number of digest warning messages", expectedNumberOfDigestWarnings, actualNumberOfDigestWarnings);
    }

    @Test
    public void hasCorrectDigestWarningMsgIt() throws IOException {
        setDigestValue("activeDigest.json");
        List<WebElement> kabaneroStacks = driver.findElements(By.id("stack-table-body"));
        for (WebElement stack : kabaneroStacks) {
            String expectedWarningMsg = getExpectedDigestWarningMsg(stack);
            String actualWarningMsg = stack.findElement(By.className("digest-notification")).getText();

            assertEquals("stacks active digest warning message is correct ", expectedWarningMsg, actualWarningMsg);
        }
    }

    @Test
    public void correctNumDigestErrorIt() throws IOException {
        setDigestValue("strictDigest.json");
        int expectedNumberOfDigestErrors = 5;
        int actualNumberOfDigestErrors = driver.findElements(By.cssSelector("#stack-table-body tr.digest-notification")).size();

        assertEquals("stacks table has correct number digest error messages", expectedNumberOfDigestErrors, actualNumberOfDigestErrors);
    }

    @Test
    public void hasCorrectDigestErrorgMsgIt() throws IOException {
        setDigestValue("strictDigest.json");
        List<WebElement> kabaneroStacks = driver.findElements(By.id("stack-table-body"));
        for (WebElement stack : kabaneroStacks) {
            String expectedErrorMsg = getExpectedDigestErrorMsg(stack);
            String actualErrorMsg = stack.findElement(By.className("digest-notification")).getText();

            assertEquals("stacks strict digest error message is correct ", expectedErrorMsg, actualErrorMsg);
        }
    }

    public void setDigestValue(String jsonFile) throws IOException {
        String singleInstanceJSON = new String(Files.readAllBytes(Paths.get("src", "test", "resources", "digestJsons", jsonFile)), StandardCharsets.UTF_8);
        String stacksJSON = new String(Files.readAllBytes(Paths.get("src", "test", "resources", "stacks.json")), StandardCharsets.UTF_8);

        js.executeScript("$('#stack-table-body').empty()");
        js.executeScript("$('#curated-stack-table-body').empty()");
        js.executeScript("updateStackView(JSON.parse(arguments[0]), JSON.parse(arguments[1]));", singleInstanceJSON, stacksJSON);
    }

    private String getExpectedDigestWarningMsg(WebElement stack) {
        String stackName = stack.findElements(By.tagName("td")).get(0).getText();
        String stackVersion = stack.findElements(By.tagName("td")).get(1).getText();
        String msg = "Digest Warning: activeDigest policy enforces a Major.Minor semver digest match. The current "
                + stackName + " - " + stackVersion + " digest does not match the Kabanero " + stackName + " - "
                + stackVersion + " digest at time of activation, this may be a problem. More info X";

        return msg;
    }

    private String getExpectedDigestErrorMsg(WebElement stack) {
        String stackName = stack.findElements(By.tagName("td")).get(0).getText();
        String stackVersion = stack.findElements(By.tagName("td")).get(1).getText();
        String msg = "Digest Error: strictDigest policy enforces a strict digest match. The current " + stackName
                + " - " + stackVersion + " digest does not match the Kabanero " + stackName + " - " + stackVersion
                + " digest at time of activation. More info X";

        return msg;
    }
}
