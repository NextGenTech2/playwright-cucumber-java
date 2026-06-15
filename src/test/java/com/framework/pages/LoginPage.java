package com.framework.pages;

import com.microsoft.playwright.Page;

public class LoginPage {
    private final Page page;

    // Element Locators
    private final String usernameInput = "#username";
    private final String passwordInput = "#password";
    private final String loginButton = "button[type='submit']";
    private final String errorMessage = ".flash.error";

    public LoginPage(Page page) {
        this.page = page;
    }

    public void navigateTo(String url) {
        page.navigate(url);
    }

    public void enterCredentials(String username, String password) {
        page.fill(usernameInput, username);
        page.fill(passwordInput, password);
    }

    public void clickLogin() {
        page.click(loginButton);
    }

    public String getErrorMessage() {
        return page.textContent(errorMessage).trim();
    }
}
