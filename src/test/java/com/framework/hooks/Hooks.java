package com.framework.hooks;

import com.framework.context.TestContext;
import com.framework.utils.DriverFactory;
import com.microsoft.playwright.Tracing;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;
import java.nio.file.Paths;

public class Hooks {

    private final TestContext testContext;

    // PicoContainer injects TestContext automatically
    public Hooks(TestContext testContext) {
        this.testContext = testContext;
    }

    @Before
    public void setup(Scenario scenario) {
        // Start tracing before each scenario
        testContext.getBrowserContext().tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
    }

    @After
    public void teardown(Scenario scenario) {
        if (scenario.isFailed()) {
            // Attach failure screenshot to Allure
            byte[] screenshot = testContext.getPage().screenshot(new com.microsoft.playwright.Page.ScreenshotOptions()
                    .setFullPage(true));
            Allure.addAttachment("Failed Screenshot - " + scenario.getName(), new ByteArrayInputStream(screenshot));
            
            // Save and Attach Trace
            String traceName = scenario.getName().replaceAll("[^a-zA-Z0-9.-]", "_") + ".zip";
            String tracePath = "target/traces/" + traceName;
            
            testContext.getBrowserContext().tracing().stop(new Tracing.StopOptions().setPath(Paths.get(tracePath)));
            
            try {
                Allure.addAttachment("Playwright Trace", "application/zip", 
                        new java.io.FileInputStream(tracePath), ".zip");
            } catch (java.io.FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            testContext.getBrowserContext().tracing().stop();
        }

        // Close page and context to ensure clean state for next scenario
        testContext.getPage().close();
        testContext.getBrowserContext().close();
    }

    @AfterAll
    public static void globalTeardown() {
        // Quit browser instances thread-safely
        DriverFactory.quitBrowser();
    }
}
