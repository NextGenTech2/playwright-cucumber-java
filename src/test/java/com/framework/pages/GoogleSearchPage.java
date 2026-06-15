package com.framework.pages;

import com.microsoft.playwright.Page;

public class GoogleSearchPage {
    private final Page page;

    // Locators
    private final String searchInput = "[name='q']";
    private final String consentButton = "button:has-text('Accept all')"; // for Europe etc.

    public GoogleSearchPage(Page page) {
        this.page = page;
    }

    public void searchFor(String text) {
        // Handle Google's cookie consent dialog if it appears
        if (page.locator(consentButton).isVisible()) {
            page.locator(consentButton).click();
        }
        page.fill(searchInput, text);
        page.press(searchInput, "Enter");
    }

    public void clickSearchResultByUrl(String urlFragment) {
        // Find search result links that contain the target urlFragment (e.g., "apextoolhub.com")
        com.microsoft.playwright.Locator link = page.locator("a[href*='" + urlFragment + "']");
        
        // Wait for the target search result link to become available
        link.first().waitFor();
        
        // Google uses h3 inside the link for the result title.
        // We click the h3 title or fall back to clicking the link directly.
        com.microsoft.playwright.Locator title = link.locator("h3");
        if (title.count() > 0) {
            title.first().click();
        } else {
            link.first().click();
        }
    }
}
