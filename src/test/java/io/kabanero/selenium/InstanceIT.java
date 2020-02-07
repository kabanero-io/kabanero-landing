package io.kabanero.selenium;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;



public class InstanceIT {
    private WebDriver driver;

    @BeforeClass
    public static void setupClass() {
        // Manages the browser driver binary for us
        WebDriverManager.chromedriver().setup();
    }

    @Before
    public void beforeTest() throws IOException {
        // selenium stuff
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--ignore-certificate-errors");
        options.setHeadless(true);
		driver = new ChromeDriver(options);
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void hasCorrectTitleIT(){
        String baseUrl = "https://localhost:9443/instance/";

        driver.get(baseUrl);
        String expectedTitle = "Kabanero";
        String actualTitle = driver.getTitle();
        assertEquals("title equals " + expectedTitle, expectedTitle, actualTitle);
    }
}
