package com.framework.utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

public class DriverFactory {
    private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();

    public static Browser getBrowser() {
        if (browserThreadLocal.get() == null) {
            Playwright playwright = Playwright.create();
            playwrightThreadLocal.set(playwright);

            // Read from ConfigManager first, fallback to System.getProperty, then default
            String browserType = com.framework.config.ConfigManager.getProperty("browser", "chromium");
            if (System.getProperty("browser") != null) browserType = System.getProperty("browser");

            String headlessStr = com.framework.config.ConfigManager.getProperty("headless", "true");
            if (System.getProperty("headless") != null) headlessStr = System.getProperty("headless");
            boolean isHeadless = Boolean.parseBoolean(headlessStr);

            String channel = com.framework.config.ConfigManager.getProperty("channel", "");

            // --disable-blink-features is Chromium-only; Firefox and WebKit reject it
            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(isHeadless);

            Browser browser = switch (browserType.toLowerCase()) {
                case "firefox" -> playwright.firefox().launch(options);
                case "webkit" -> playwright.webkit().launch(options);
                default -> {
                    // Chromium-specific stealth args
                    options.setArgs(java.util.Arrays.asList("--disable-blink-features=AutomationControlled"));
                    if (channel != null && !channel.isEmpty()) {
                        options.setChannel(channel);
                    }
                    yield playwright.chromium().launch(options);
                }
            };

            browserThreadLocal.set(browser);
        }
        return browserThreadLocal.get();
    }

    public static void quitBrowser() {
        if (browserThreadLocal.get() != null) {
            try {
                browserThreadLocal.get().close();
            } catch (Exception e) {
                // Ignore
            }
            browserThreadLocal.remove();
        }
        if (playwrightThreadLocal.get() != null) {
            try {
                playwrightThreadLocal.get().close();
            } catch (Exception e) {
                // Ignore
            }
            playwrightThreadLocal.remove();
        }
    }
}
