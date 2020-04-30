package instance;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

@FixMethodOrder(MethodSorters.JVM)
public class adminViewIT {
    
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

        // execute javascript functions to set mock data
        js.executeScript("setInstancesSelections(JSON.parse(arguments[0]), '');", multiInstanceJSON);
    }

    @AfterClass
    public static void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void doesNotShowAdminList(){
        js.executeScript("fetchInstanceAdmins({'isAdmin':false});");
        WebElement hiddenAdminList = driver.findElement(By.id("instance-accordion-admin-view"));

        assertEquals("hides accordion admin list from non-admin", false, hiddenAdminList.isDisplayed());
    }

    @Test
    public void doesShowAdminList() throws IOException {
        String adminMembersJSON = new String(Files.readAllBytes(Paths.get("src", "test", "resources", "adminMembers.json")), StandardCharsets.UTF_8);
        js.executeScript("fetchInstanceAdmins({isAdmin:true});");
        js.executeScript("updateInstanceAdminView(JSON.parse(arguments[0]));", adminMembersJSON);

        WebElement instaceAccordion = driver.findElement(By.id("instance-accordion"));
        List<WebElement> instances  = instaceAccordion.findElements(By.className("accordion-title"));
        instances.get(0).click();

        WebElement accordionAdminList = driver.findElement(By.id("instance-accordion-admin-view"));
        
        assertEquals("shows accordion admin list of admins", true, accordionAdminList.isDisplayed());
    }

    @Test
    public void hasCorrectNumAdminIT() throws IOException {
        List<WebElement> adminList = driver.findElements(By.className("instance-admin-names"));
        int expectedNumAdmins = 3;
        int actualNumAdmins = adminList.size();

        assertEquals("has correct Number of admins", expectedNumAdmins, actualNumAdmins);
    }

    @Test 
    public void hasCorrectAdminListIT() throws IOException {
        List<WebElement> adminList = driver.findElements(By.className("instance-admin-names"));
        String[] expectedAdminsArray = {"alohr51", "kSee04", "kidus60"};
        String[] actualAdminsArray = new String[adminList.size()];

        for(WebElement admin: adminList){
            actualAdminsArray[adminList.indexOf(admin)] = admin.getText();
        }

        assertArrayEquals("has first correct admin login", expectedAdminsArray, actualAdminsArray);
    }

    @Test
    public void manageAdminsBtnExists() throws IOException {
        WebElement manageAdminsBtn = driver.findElement(By.id("manage-admins-link"));

        assertEquals("shows accordion list of admins ", true, manageAdminsBtn.isDisplayed());
    }

    @Test
    public void adminModalAppearsOnClick() throws IOException {
        driver.findElement(By.id("manage-admins-link")).click();
        WebElement adminModal = driver.findElement(By.id("manage-admins-modal"));

        assertEquals("shows accordion admin list of admins", true, adminModal.isDisplayed());
    }

    @Test
    public void hasCorrectNumTeamIT() throws IOException {
        WebElement adminsModal = driver.findElement(By.id("admin-modal-list"));
        List<WebElement> adminList = adminsModal.findElements(By.tagName("li"));
        int expectedNumAdmins = 1;
        int actualNumAdmins = adminList.size();

        assertEquals("has correct Number of teams in admin modal", expectedNumAdmins, actualNumAdmins);
    }

    @Test
    public void hasCorrectNumModalHeaders() throws IOException {
        WebElement adminsModal = driver.findElement(By.id("admin-modal-list"));
        List<WebElement> adminList = adminsModal.findElements(By.tagName("li"));
        List<WebElement> headers =  adminList.get(0).findElements(By.className("modal-content-user-info"));

        int expectedNumHeaders = 3;
        int actualNumOfHeaders = headers.size();

        assertEquals("has correct Number of teams in admin modal", expectedNumHeaders, actualNumOfHeaders);
    }


    @Test
    public void hasCorrectModalHeaders() throws IOException {
        WebElement adminsModal = driver.findElement(By.id("admin-modal-list"));
        List<WebElement> adminList = adminsModal.findElements(By.tagName("li"));
        adminList.get(0).click();

        List<WebElement> headers = adminList.get(0).findElements(By.className("modal-content-user-info"));

        String expectedFirstHeader = "User";
        String expectedSecondHeader = "Full Name";
        String expectedThirdHeader = "Email";

        String actualFirstHeader = headers.get(0).getText();
        String actualSecondHeader = headers.get(1).getText();
        String actualThirdHeader = headers.get(2).getText();

        assertEquals("has first correct admin login", expectedFirstHeader, actualFirstHeader);
        assertEquals("has second correct admin login", expectedSecondHeader, actualSecondHeader);
        assertEquals("has third correct admin login", expectedThirdHeader, actualThirdHeader);
    }

}