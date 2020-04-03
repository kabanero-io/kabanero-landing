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
        int actualNumberOfStacks = getNumTableRow("stack-table-body");
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
        int actualNumberOfCuratedStacks = getNumTableRow("curated-stack-table-body");
        assertEquals("curated stacks table has correct table rows", expectedNumberOfCuratedStacks,
                actualNumberOfCuratedStacks);
    }

    public int getNumTableRow(String tableId) {
        WebElement tableBody = driver.findElement(By.id(tableId));
        List<WebElement> tableRows = tableBody.findElements(By.tagName("tr"));

        return tableRows.size();
    }

}
