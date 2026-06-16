package com.framework.context;

import com.framework.pages.ApexToolHubPage;
import com.framework.pages.GoogleSearchPage;
import com.framework.pages.LoginPage;
import com.framework.utils.DriverFactory;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import lombok.Getter;

@Getter
public class TestContext {
    private final BrowserContext browserContext;
    private final Page page;
    
    // Page Objects initialized here for PicoContainer dependency injection
    private final LoginPage loginPage;
    private final GoogleSearchPage googleSearchPage;
    private final ApexToolHubPage apexToolHubPage;

    public TestContext() {
        // Initialize browser context and page
        this.browserContext = DriverFactory.getBrowser().newContext();
        this.browserContext.addInitScript("try { delete Navigator.prototype.webdriver; } catch (e) {}");
        this.page = this.browserContext.newPage();
        
        // Initialize Pages
        this.loginPage = new LoginPage(this.page);
        this.googleSearchPage = new GoogleSearchPage(this.page);
        this.apexToolHubPage = new ApexToolHubPage(this.page);
    }
}
