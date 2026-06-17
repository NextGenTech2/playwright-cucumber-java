package com.framework.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

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

        // Wait for Google's full page to paint — including AI Overview + organic results.
        // Google loads in phases: AI section first, then organic links repaint after.
        // "networkidle" waits until no network requests for 500ms — safe for full render.
        // Timeout is governed by the global default set in TestContext (config: default.timeout).
        try {
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
        } catch (Exception e) {
            // If networkidle times out (heavy page), fall through — results may still be usable
        }
    }

    public void clickSearchResultByUrl(String urlFragment) {
        // Target organic search results inside #search container.
        // This avoids matching AI Overview or ad links that appear earlier in the DOM.
        String organicSelector = "#search a[href*='" + urlFragment + "']";
        String fallbackSelector = "a[href*='" + urlFragment + "']";

        Locator link;

        // Try organic results first (inside #search div — painted after AI Overview)
        Locator organicLink = page.locator(organicSelector);
        try {
            // Global default timeout (config: default.timeout) governs this wait automatically
            organicLink.first().waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE));
            link = organicLink;
        } catch (Exception e) {
            // Fallback to any link with the URL fragment if #search container not found
            Locator fallbackLink = page.locator(fallbackSelector);
            try {
                fallbackLink.first().waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE));
                link = fallbackLink;
            } catch (Exception e2) {
                throw new RuntimeException("Search result link for '" + urlFragment + "' not found after waiting.", e2);
            }
        }

        // Scroll the link into view before clicking — avoids click interception by sticky headers
        link.first().scrollIntoViewIfNeeded();

        // Click via h3 title if available (more stable click target), else click the link directly
        Locator title = link.first().locator("h3");
        if (title.count() > 0) {
            title.first().click();
        } else {
            link.first().click();
        }
    }
}
