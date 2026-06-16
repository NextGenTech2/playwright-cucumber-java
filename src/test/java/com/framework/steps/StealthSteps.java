package com.framework.steps;

import com.framework.context.TestContext;
import com.microsoft.playwright.Page;
import io.cucumber.java.en.Then;
import static org.assertj.core.api.Assertions.assertThat;

public class StealthSteps {
    private final Page page;

    public StealthSteps(TestContext testContext) {
        this.page = testContext.getPage();
    }

    @Then("verify WebDriver check shows as {string} or {string}")
    public void verifyWebDriverCheckShowsAs(String expected1, String expected2) {
        // Locate the table cell next to the "WebDriver" or "WebDriver (New)" header
        com.microsoft.playwright.Locator cell = page.locator("td:has-text('WebDriver') + td").first();
        cell.waitFor();
        String actualStatus = cell.textContent().trim();
        
        assertThat(actualStatus)
                .as("WebDriver check did not match expected status")
                .satisfies(status -> assertThat(status.toLowerCase())
                        .containsAnyOf(expected1.toLowerCase(), expected2.toLowerCase()));
    }

    @Then("verify navigator.webdriver JavaScript property is undefined")
    public void verifyNavigatorWebdriverIsUndefined() {
        Object result = page.evaluate("navigator.webdriver");
        assertThat(result)
                .as("navigator.webdriver property should be undefined (null in Playwright)")
                .isNull();
    }
}
