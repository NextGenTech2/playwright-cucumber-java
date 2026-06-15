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
        // Click the first search result header (Google uses h3 for result titles)
        page.locator("h3").first().waitFor();
        page.locator("h3").first().click();
    }
}
