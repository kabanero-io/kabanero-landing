package io.kabanero.selenium;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
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
import io.kabanero.selenium.resources.MockDataConstants;

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
        // execute javascript function setInstanceSelections() and pass it mock data to test against
        js.executeScript("setInstanceSelections(" + MockDataConstants.MOCK_INSTANCES_ENDPOINT_DATA + ")");
        // execute javascript function setInstanceCard() and pass it mock data to test against
        js.executeScript("setInstanceCard(" + MockDataConstants.MOCK_KABANERO_INSTANCE_DATA + ")");
        // execute javascript function setStackCard() and pass it mock data to test against
        js.executeScript("setStackCard(" + MockDataConstants.MOCK_STAKCS_ENDPOINT_DATA + ")");
        // execute javascript function setToolData() and pass it mock data to test against
        js.executeScript("setToolData(" + MockDataConstants.MOCK_TOOLS_ENDPOINT_DATA + ")");
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

        assertEquals("title equals " + expectedTitle, expectedTitle, actualTitle);
    }

    @Test
    public void displaysCorrectInstanceNameIT() throws InterruptedException {
        String expectedInstanceName = "kabanero";
        String actualInstanceName = driver.findElement(By.id("instance-accordion")).findElement(By.className("bx--accordion__title")).getText();

        assertEquals("instance name equals " + expectedInstanceName, expectedInstanceName, actualInstanceName);
    }

    @Test
    public void hasCorrectStackHubNameIT(){
        String expectedStackHubName = "central";
        String actualStackHubName = driver.findElement(By.className("stack-hub-name")).getText();
 
        assertEquals("Stack Hub name equals " + expectedStackHubName, expectedStackHubName, actualStackHubName);
    }

    @Test
    public void hasCorrectAppsodyUrlIT() {
        String expectedAppsodyURL = "https://github.com/kabanero-io/collections/releases/download/0.5.0/kabanero-index.yaml";
        String actualAppsodyURL = driver.findElement(By.id("instance-details")).findElement(By.className("appsody-url")).getAttribute("value");

        assertEquals("Appsody URL equals " + expectedAppsodyURL, expectedAppsodyURL, actualAppsodyURL);
    }

    @Test
    public void hasCorrectCodewindUrlIT() {
        String expectedCodewindURL = "https://github.com/kabanero-io/collections/releases/download/0.5.0/kabanero-index.json";
        String actualCodewindURL = driver.findElement(By.id("instance-details")).findElement(By.className("codewind-url")).getAttribute("value");

        assertEquals("Codewind URL equals " + expectedCodewindURL, expectedCodewindURL, actualCodewindURL);
    }
    
    @Test
    public void hasCorrectManagementCliUrlIT() {
        String expectedManagementCliURL = "kabanero-cli-kabanero.apps.alohr.os.fyre.ibm.com";
        String actualManagementCliURL = driver.findElement(By.id("management-cli")).getAttribute("value");

        assertEquals("Management CLI URL equals " + expectedManagementCliURL, expectedManagementCliURL, actualManagementCliURL);
    }

    @Test
    public void numberOfStacksMatchesStacksShownIT(){
       int expectedNumberOfStacks  = 5;
       int actualNumberOFStacks = Integer.valueOf(driver.findElement(By.id("num-stacks")).getText());
       List<WebElement> listOfStacksShown = driver.findElements(By.cssSelector("div#stack-list li"));
       int numberOfStacksShown = listOfStacksShown.size();

       assertEquals("Number of stakcs equals " + expectedNumberOfStacks, actualNumberOFStacks, actualNumberOFStacks);
       assertEquals("Number of stakcs in the list equals " + expectedNumberOfStacks, numberOfStacksShown, numberOfStacksShown);
    }

    @Test 
    public void pipelinesButtonIsActiveIt(){
        String expectedPipeLineButtonText = "Manage Pipelines";
        String actualPipeLineButtonText = driver.findElement(By.id("pipeline-button-text")).getText();

        assertEquals("Manage Pipelines button text equals " + expectedPipeLineButtonText, actualPipeLineButtonText, actualPipeLineButtonText);
    }

    @Test 
    public void pipelinesButtonHasCorrectLinkIT(){
        String expectedPipeLineButtonHref = "https://tekton-dashboard-tekton-pipelines.apps.alohr.os.fyre.ibm.com";
        String actualPipeLineButtonHref  = driver.findElement(By.id("pipeline-link")).getAttribute("href");

        assertEquals("Management CLI URL equals " + expectedPipeLineButtonHref, actualPipeLineButtonHref, actualPipeLineButtonHref);
    }

    @Test 
    public void applicationsButtonIsActiveIt(){
        String expectedApplicationsButtonText = "Manage Applications";
        String actualApplicationsButtonText = driver.findElement(By.id("manage-apps-button-text")).getText();

        assertEquals("Manage Pipelines button text equals " + expectedApplicationsButtonText, actualApplicationsButtonText, actualApplicationsButtonText);
    }

    @Test 
    public void appliecationsButtonHasCorrectLinkIT(){
        String expectedApplicationsButtonHref = "https://tekton-dashboard-tekton-pipelines.apps.alohr.os.fyre.ibm.com";
        String actualApplicationsButtonHref  = driver.findElement(By.id("appnav-link")).getAttribute("href");

        assertEquals("Management CLI URL equals " + expectedApplicationsButtonHref, actualApplicationsButtonHref, actualApplicationsButtonHref);
    }

    
}