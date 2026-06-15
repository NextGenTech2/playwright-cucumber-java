package com.framework.steps;

import com.framework.config.ConfigManager;
import com.framework.context.TestContext;
import com.microsoft.playwright.Page;
import io.cucumber.java.en.Given;

public class CommonSteps {
    private final Page page;

    public CommonSteps(TestContext testContext) {
        this.page = testContext.getPage();
    }

    @Given("the user opens the {string}")
    public void theUserOpensTheUrl(String configKey) {
        String url = ConfigManager.getProperty(configKey);
        if (url == null || url.isEmpty()) {
            url = configKey; // fallback if they provided a literal URL
        }
        page.navigate(url);
    }
}
