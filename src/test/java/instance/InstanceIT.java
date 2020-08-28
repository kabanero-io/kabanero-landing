package instance;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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

public class InstanceIT {
    private static WebDriver driver;
    private static String baseUrl = "https://localhost:9443/instance/";
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

        String multiInstanceJSON = new String(Files.readAllBytes(Paths.get("src", "test", "resources", "multipleInstances.json")), StandardCharsets.UTF_8);
        String singleInstanceJSON = new String(Files.readAllBytes(Paths.get("src", "test", "resources", "singleInstance.json")), StandardCharsets.UTF_8);
        String stacksJSON = new String(Files.readAllBytes(Paths.get("src", "test", "resources", "stacksCRD.json")), StandardCharsets.UTF_8);
        String toolsJSON = new String(Files.readAllBytes(Paths.get("src", "test", "resources", "tools.json")), StandardCharsets.UTF_8);

        // execute javascript functions to set mock data
        js.executeScript("setInstancesSelections(JSON.parse(arguments[0]), '');", multiInstanceJSON);
        js.executeScript("setInstanceCard(JSON.parse(arguments[0]));", singleInstanceJSON);
        js.executeScript("setStackCard(JSON.parse(arguments[0]));", stacksJSON);
        js.executeScript("setToolData(JSON.parse(arguments[0]));", toolsJSON);
    }

    @AfterClass
    public static void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void hasCorrectTitleIT() {
        String expectedTitle = "Kabanero";
        String actualTitle = driver.getTitle();
        assertEquals("has correct title", expectedTitle, actualTitle);
    }

    @Test
    public void displaysCorrectInstanceNameIT() throws InterruptedException {
        String expectedInstanceName = "kabanero";

        String actualInstanceName = driver.findElement(By.id("instance-accordion"))
                .findElement(By.className("bx--accordion__title")).getText();

        assertEquals("displays correct instance name", expectedInstanceName, actualInstanceName);
    }

    @Test
    public void hasCorrectStackHubNameIT() {
        String expectedStackHubName = "central";
        String actualStackHubName = driver.findElement(By.className("stack-hub-name")).getText();

        assertEquals("has correct Stack Hub name", expectedStackHubName, actualStackHubName);
    }

    @Test
    public void hasCorrectAppsodyUrlIT() {
        String expectedAppsodyURL = "https://github.com/kabanero-io/collections/releases/download/0.5.0/kabanero-index.yaml";
        String actualAppsodyURL = driver.findElement(By.id("instance-details")).findElement(By.className("appsody-url"))
                .getAttribute("value");

        assertEquals("has correct Appsody URL", expectedAppsodyURL, actualAppsodyURL);
    }

    @Test
    public void hasCorrectCodewindUrlIT() {
        String expectedCodewindURL = "https://github.com/kabanero-io/collections/releases/download/0.5.0/kabanero-index.json";
        String actualCodewindURL = driver.findElement(By.id("instance-details"))
                .findElement(By.className("codewind-url")).getAttribute("value");

        assertEquals("has correct Codewind URL", expectedCodewindURL, actualCodewindURL);
    }

    @Test
    public void hasCorrectManagementClIURLIT() {
        String expectedManagementCliURL = "kabanero-cli-kabanero.apps.alohr.os.fyre.ibm.com";
        String actualManagementCliURL = driver.findElement(By.id("management-cli")).getAttribute("value");

        assertEquals("has correct Management CLI URL", expectedManagementCliURL, actualManagementCliURL);
    }

    @Test
    public void numberOfStacksMatchesStacksShownIT() {
        int expectedNumberOfStacks = 5;
        int actualNumberOFStacks = Integer.valueOf(driver.findElement(By.id("num-stacks")).getText());
        List<WebElement> listOfStacksShown = driver.findElements(By.cssSelector("div#stack-list li"));
        int numberOfStacksShown = listOfStacksShown.size();

        assertEquals("has correct Number of stacks in card title", expectedNumberOfStacks, actualNumberOFStacks);
        assertEquals("has correct Number of stacks in the card list", expectedNumberOfStacks, numberOfStacksShown);
    }

    @Test
    public void hasCorrectManageApplicationsButtonText() {
        String expectedApplicationsButtonText = "Manage Applications";
        String actualApplicationsButtonText = driver.findElement(By.cssSelector("#application-navigator .button-text")).getText();

        assertEquals("has correct Manage Application button text", expectedApplicationsButtonText,
                actualApplicationsButtonText);
    }

    @Test
    public void hasCorrectManageApplicationsLink() {
        String expectedApplicationsButtonHref = "https://kappnav-ui-service-kappnav.apps.com/";
        String actualApplicationsButtonHref = driver.findElement(By.cssSelector("#application-navigator a")).getAttribute("href");

        assertEquals("has correct Manage Applications link", expectedApplicationsButtonHref,
                actualApplicationsButtonHref);
    }

}