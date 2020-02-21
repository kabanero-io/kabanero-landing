package io.kabanero.selenium;

import static org.junit.Assert.assertEquals;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.bonigarcia.wdm.WebDriverManager;

public class InstanceIT {
    private static WebDriver driver;
    private String baseUrl = "https://localhost:9443/instance/";
    private JavascriptExecutor js;
    private String fakeInstancesEnpointData = "{\"apiVersion\":\"kabanero.io/v1alpha2\",\"items\":[{\"apiVersion\":\"kabanero.io/v1alpha2\",\"kind\":\"Kabanero\",\"metadata\":{\"annotations\":{\"kubectl.kubernetes.io/last-applied-configuration\":\"{\\\"apiVersion\\\":\\\"kabanero.io/v1alpha2\\\",\\\"kind\\\":\\\"Kabanero\\\",\\\"metadata\\\":{\\\"annotations\\\":{},\\\"name\\\":\\\"kabanero\\\",\\\"namespace\\\":\\\"kabanero\\\"},\\\"spec\\\":{\\\"github\\\":{\\\"apiUrl\\\":\\\"https://api.github.com\\\",\\\"organization\\\":\\\"alohr51-kabanero\\\",\\\"teams\\\":[\\\"admin\\\"]},\\\"stacks\\\":{\\\"pipelines\\\":[{\\\"https\\\":{\\\"skipCertVerification\\\":false,\\\"url\\\":\\\"https://github.com/kabanero-io/collections/releases/download/0.5.0-rc.4/incubator.common.pipeline.default.tar.gz\\\"},\\\"id\\\":\\\"default\\\",\\\"sha256\\\":\\\"6537a5a25f845266d6b81c47d97a97c711a98306a78339d039d5af8ed700bff5\\\"}],\\\"repositories\\\":[{\\\"https\\\":{\\\"url\\\":\\\"https://github.com/kabanero-io/collections/releases/download/0.5.0/kabanero-index.yaml\\\"},\\\"name\\\":\\\"central\\\"}]},\\\"version\\\":\\\"0.6.0\\\"}}\\n\"},\"clusterName\":null,\"creationTimestamp\":\"2020-02-12T16:37:29.000-05:00\",\"deletionGracePeriodSeconds\":null,\"deletionTimestamp\":null,\"finalizers\":[\"kabanero.io.kabanero-operator\"],\"generateName\":null,\"generation\":2,\"initializers\":null,\"labels\":null,\"name\":\"kabanero\",\"namespace\":\"kabanero\",\"ownerReferences\":null,\"resourceVersion\":\"48870223\",\"selfLink\":\"/apis/kabanero.io/v1alpha2/namespaces/kabanero/kabaneros/kabanero\",\"uid\":\"de843463-4ddf-11ea-832b-00000a101618\"},\"spec\":{\"admissionControllerWebhook\":{\"image\":null,\"repository\":null,\"tag\":null,\"version\":null},\"cliServices\":{\"image\":null,\"repository\":null,\"sessionExpirationSeconds\":null,\"tag\":null,\"version\":null},\"codeReadyWorkspaces\":{\"enable\":false,\"operator\":{\"customResourceInstance\":{\"cheWorkspaceClusterRole\":null,\"devFileRegistryImage\":{\"image\":null,\"repository\":null,\"tag\":null,\"version\":null},\"openShiftOAuth\":null,\"selfSignedCert\":null,\"tlsSupport\":null}}},\"collectionController\":{\"image\":null,\"repository\":null,\"tag\":null,\"version\":null},\"events\":{\"enable\":null,\"image\":null,\"repository\":null,\"tag\":null,\"version\":null},\"github\":{\"apiUrl\":\"https://api.github.com\",\"organization\":\"alohr51-kabanero\",\"teams\":[\"admin\"]},\"landing\":{\"enable\":null,\"version\":null},\"sso\":{\"adminSecretName\":null,\"enable\":null,\"provider\":null},\"stackController\":{\"image\":null,\"repository\":null,\"tag\":null,\"version\":null},\"stacks\":{\"pipelines\":[{\"https\":{\"skipCertVerification\":null,\"url\":\"https://github.com/kabanero-io/collections/releases/download/0.5.0-rc.4/incubator.common.pipeline.default.tar.gz\"},\"id\":\"default\",\"sha256\":\"6537a5a25f845266d6b81c47d97a97c711a98306a78339d039d5af8ed700bff5\"}],\"repositories\":[{\"https\":{\"skipCertVerification\":null,\"url\":\"https://github.com/kabanero-io/collections/releases/download/0.5.0/kabanero-index.yaml\"},\"name\":\"central\",\"pipelines\":null}]},\"targetNamespaces\":null,\"triggers\":null,\"version\":\"0.6.0\"},\"status\":{\"admissionControllerWebhook\":{\"message\":null,\"ready\":\"True\"},\"appsody\":{\"message\":null,\"ready\":\"True\",\"version\":\"0.3.0\"},\"cli\":{\"hostnames\":[\"kabanero-cli-kabanero.apps.alohr.os.fyre.ibm.com\"],\"message\":null,\"ready\":\"True\"},\"codereadyWorkspaces\":null,\"collectionController\":{\"message\":null,\"ready\":\"True\",\"version\":\"0.6.0-rc.1\"},\"events\":null,\"kabaneroInstance\":{\"message\":null,\"ready\":\"True\",\"version\":\"0.6.0\"},\"kappnav\":null,\"landing\":{\"message\":null,\"ready\":\"True\",\"version\":\"0.5.0\"},\"serverless\":{\"knativeServing\":{\"message\":null,\"ready\":\"True\",\"version\":\"0.10.0\"},\"message\":null,\"ready\":\"True\",\"version\":\"1.3.0\"},\"sso\":{\"configured\":\"False\",\"message\":null,\"ready\":\"False\"},\"stackController\":{\"message\":null,\"ready\":\"True\",\"version\":\"0.6.0-rc.1\"},\"tekton\":{\"message\":null,\"ready\":\"True\",\"version\":\"v0.10.1\"}}}],\"kind\":\"KabaneroList\",\"metadata\":{\"continue\":\"\",\"remainingItemCount\":null,\"resourceVersion\":\"50004897\",\"selfLink\":\"/apis/kabanero.io/v1alpha2/namespaces/kabanero/kabaneros\"}}";
    private String fakeKabaneroInstanceData = "{\"apiVersion\":\"kabanero.io/v1alpha2\",\"kind\":\"Kabanero\",\"metadata\":{\"annotations\":{\"kubectl.kubernetes.io/last-applied-configuration\":\"{\\\"apiVersion\\\":\\\"kabanero.io/v1alpha2\\\",\\\"kind\\\":\\\"Kabanero\\\",\\\"metadata\\\":{\\\"annotations\\\":{},\\\"name\\\":\\\"kabanero\\\",\\\"namespace\\\":\\\"kabanero\\\"},\\\"spec\\\":{\\\"github\\\":{\\\"apiUrl\\\":\\\"https://api.github.com\\\",\\\"organization\\\":\\\"alohr51-kabanero\\\",\\\"teams\\\":[\\\"admin\\\"]},\\\"stacks\\\":{\\\"pipelines\\\":[{\\\"https\\\":{\\\"skipCertVerification\\\":false,\\\"url\\\":\\\"https://github.com/kabanero-io/collections/releases/download/0.5.0-rc.4/incubator.common.pipeline.default.tar.gz\\\"},\\\"id\\\":\\\"default\\\",\\\"sha256\\\":\\\"6537a5a25f845266d6b81c47d97a97c711a98306a78339d039d5af8ed700bff5\\\"}],\\\"repositories\\\":[{\\\"https\\\":{\\\"url\\\":\\\"https://github.com/kabanero-io/collections/releases/download/0.5.0/kabanero-index.yaml\\\"},\\\"name\\\":\\\"central\\\"}]},\\\"version\\\":\\\"0.6.0\\\"}}\\n\"},\"clusterName\":null,\"creationTimestamp\":\"2020-02-12T16:37:29.000-05:00\",\"deletionGracePeriodSeconds\":null,\"deletionTimestamp\":null,\"finalizers\":[\"kabanero.io.kabanero-operator\"],\"generateName\":null,\"generation\":2,\"initializers\":null,\"labels\":null,\"name\":\"kabanero\",\"namespace\":\"kabanero\",\"ownerReferences\":null,\"resourceVersion\":\"48870223\",\"selfLink\":\"/apis/kabanero.io/v1alpha2/namespaces/kabanero/kabaneros/kabanero\",\"uid\":\"de843463-4ddf-11ea-832b-00000a101618\"},\"spec\":{\"admissionControllerWebhook\":{\"image\":null,\"repository\":null,\"tag\":null,\"version\":null},\"cliServices\":{\"image\":null,\"repository\":null,\"sessionExpirationSeconds\":null,\"tag\":null,\"version\":null},\"codeReadyWorkspaces\":{\"enable\":false,\"operator\":{\"customResourceInstance\":{\"cheWorkspaceClusterRole\":null,\"devFileRegistryImage\":{\"image\":null,\"repository\":null,\"tag\":null,\"version\":null},\"openShiftOAuth\":null,\"selfSignedCert\":null,\"tlsSupport\":null}}},\"collectionController\":{\"image\":null,\"repository\":null,\"tag\":null,\"version\":null},\"events\":{\"enable\":null,\"image\":null,\"repository\":null,\"tag\":null,\"version\":null},\"github\":{\"apiUrl\":\"https://api.github.com\",\"organization\":\"alohr51-kabanero\",\"teams\":[\"admin\"]},\"landing\":{\"enable\":null,\"version\":null},\"sso\":{\"adminSecretName\":null,\"enable\":null,\"provider\":null},\"stackController\":{\"image\":null,\"repository\":null,\"tag\":null,\"version\":null},\"stacks\":{\"pipelines\":[{\"https\":{\"skipCertVerification\":null,\"url\":\"https://github.com/kabanero-io/collections/releases/download/0.5.0-rc.4/incubator.common.pipeline.default.tar.gz\"},\"id\":\"default\",\"sha256\":\"6537a5a25f845266d6b81c47d97a97c711a98306a78339d039d5af8ed700bff5\"}],\"repositories\":[{\"https\":{\"skipCertVerification\":null,\"url\":\"https://github.com/kabanero-io/collections/releases/download/0.5.0/kabanero-index.yaml\"},\"name\":\"central\",\"pipelines\":null}]},\"targetNamespaces\":null,\"triggers\":null,\"version\":\"0.6.0\"},\"status\":{\"admissionControllerWebhook\":{\"message\":null,\"ready\":\"True\"},\"appsody\":{\"message\":null,\"ready\":\"True\",\"version\":\"0.3.0\"},\"cli\":{\"hostnames\":[\"kabanero-cli-kabanero.apps.alohr.os.fyre.ibm.com\"],\"message\":null,\"ready\":\"True\"},\"codereadyWorkspaces\":null,\"collectionController\":{\"message\":null,\"ready\":\"True\",\"version\":\"0.6.0-rc.1\"},\"events\":null,\"kabaneroInstance\":{\"message\":null,\"ready\":\"True\",\"version\":\"0.6.0\"},\"kappnav\":null,\"landing\":{\"message\":null,\"ready\":\"True\",\"version\":\"0.5.0\"},\"serverless\":{\"knativeServing\":{\"message\":null,\"ready\":\"True\",\"version\":\"0.10.0\"},\"message\":null,\"ready\":\"True\",\"version\":\"1.3.0\"},\"sso\":{\"configured\":\"False\",\"message\":null,\"ready\":\"False\"},\"stackController\":{\"message\":null,\"ready\":\"True\",\"version\":\"0.6.0-rc.1\"},\"tekton\":{\"message\":null,\"ready\":\"True\",\"version\":\"v0.10.1\"}}}";


    private final static Logger LOGGER = Logger.getLogger(InstanceIT.class.getName());


    @BeforeClass
    public static void setupClass() throws IOException {
        // Manages the browser driver binary for us
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--ignore-certificate-errors", "--no-sandbox", "--disable-dev-shm-usage");
        options.setHeadless(true);
        driver = new ChromeDriver(options);
    }

    @Before
    public void beforeTest() {
        driver.get(baseUrl);
        js = (JavascriptExecutor) driver;
        // wait for page to finish loading before calling javascript function and setting mock data
        driver.manage().timeouts().setScriptTimeout(5, TimeUnit.SECONDS);

        // execute javascript function setInstanceSelections() and pass it mock data totest against
        js.executeScript("setInstanceSelections(" + fakeInstancesEnpointData + ")");
        // execute javascript function loadAllInfo() and pass it mock data to test against
        js.executeScript("setInstanceCard(" + fakeKabaneroInstanceData + ")");
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



        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        boolean headless_check = GraphicsEnvironment.isHeadless();
        LOGGER.log(Level.WARNING, "ksnkfnekfn" + headless_check);

    }

    @Test
    public void copiesToClipBoardIT() throws InterruptedException, UnsupportedFlavorException, IOException {
        String expectedAppsodyURL = "https://github.com/kabanero-io/collections/releases/download/0.5.0/kabanero-index.yaml";
        String actualAppsodyURL = getClipboardValue(By.className("appsody-url"));
        assertEquals("instance name equals " + expectedAppsodyURL, expectedAppsodyURL, actualAppsodyURL);

        String expectedCodewindURL = "https://github.com/kabanero-io/collections/releases/download/0.5.0/kabanero-index.json";
        String actualCodewindURL = getClipboardValue(By.className("codewind-url"));
        assertEquals("instance name equals " + expectedCodewindURL, expectedCodewindURL, actualCodewindURL);

        String expectedCliURL = "kabanero-cli-kabanero.apps.alohr.os.fyre.ibm.com";
        String actualCliURL = getClipboardValue(By.id("management-cli"));
        assertEquals("instance name equals " + expectedCliURL, expectedCliURL, actualCliURL);
    }

    public String getClipboardValue(By selector) throws UnsupportedFlavorException, IOException {
        WebElement urlBox = driver.findElement(selector);
        urlBox.click();

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        String clipboardContent = (String) clipboard.getData(DataFlavor.stringFlavor);

        return clipboardContent;
    }
}