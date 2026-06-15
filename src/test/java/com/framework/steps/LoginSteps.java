package com.framework.steps;

import com.framework.config.ConfigManager;
import com.framework.context.TestContext;
import com.framework.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

// Native AssertJ Assertion
import static org.assertj.core.api.Assertions.assertThat;

public class LoginSteps {

    private final LoginPage loginPage;

    // TestContext injected by PicoContainer
    public LoginSteps(TestContext testContext) {
        this.loginPage = testContext.getLoginPage();
    }

    @Given("the user is on the login page")
    public void theUserIsOnTheLoginPage() {
        loginPage.navigateTo(ConfigManager.getProperty("base.url") + "/login");
    }

    @When("the user enters username {string} and password {string}")
    public void theUserEntersUsernameAndPassword(String username, String password) {
        loginPage.enterCredentials(username, password);
    }

    @When("clicks the login button")
    public void clicksTheLoginButton() {
        loginPage.clickLogin();
    }

    @Then("the user should see an error message {string}")
    public void theUserShouldSeeAnErrorMessage(String expectedMessage) {
        String actualMessage = loginPage.getErrorMessage();
        
        // AssertJ style assertion providing very readable logs
        assertThat(actualMessage)
                .as("Validate that the login error message matches the expected value")
                .contains(expectedMessage);
    }
}
