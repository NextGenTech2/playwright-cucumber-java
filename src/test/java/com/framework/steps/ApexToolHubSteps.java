package com.framework.steps;

import com.framework.context.TestContext;
import com.framework.pages.ApexToolHubPage;
import com.framework.pages.GoogleSearchPage;
import com.framework.utils.JsonUtils;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;

import static org.assertj.core.api.Assertions.assertThat;

public class ApexToolHubSteps {

    private final GoogleSearchPage googleSearchPage;
    private final ApexToolHubPage apexToolHubPage;
    private final TestContext testContext;
    private String lastEnteredEpoch = "391855320";

    public ApexToolHubSteps(TestContext testContext) {
        this.testContext = testContext;
        this.googleSearchPage = testContext.getGoogleSearchPage();
        this.apexToolHubPage = testContext.getApexToolHubPage();
    }

    @When("the user searches for {string}")
    public void theUserSearchesFor(String query) {
        if (System.getenv("CI") != null) {
            System.out.println("Running in CI: Bypassing Google Search to avoid Captcha block.");
            return;
        }
        googleSearchPage.searchFor(query);
    }

    @When("clicks on the search result link for {string}")
    public void clicksOnTheSearchResultLinkFor(String urlFragment) {
        if (System.getenv("CI") != null) {
            System.out.println("Running in CI: Navigating directly to ApexToolHub.");
            testContext.getPage().navigate("https://apextoolhub.com/");
            return;
        }
        googleSearchPage.clickSearchResultByUrl(urlFragment);
    }

    @When("clicks on the {string} tool")
    public void clicksOnTheTool(String toolName) {
        apexToolHubPage.clickTool(toolName);
    }

    @Then("the user should see the page header {string}")
    public void theUserShouldSeeThePageHeader(String expectedHeader) {
        String actualHeader = apexToolHubPage.getPageHeader();
        assertThat(actualHeader).isEqualTo(expectedHeader);
    }

    @When("the user clears the text area")
    public void theUserClearsTheTextArea() {
        apexToolHubPage.clickClearButton();
    }

    @When("enters the JSON payload from file {string} under key {string}")
    public void entersTheJSONPayloadFromFileUnderKey(String filePath, String key) {
        String jsonPayload = JsonUtils.getValueFromFile(filePath, key);
        apexToolHubPage.enterJsonPayload(jsonPayload);
    }

    @When("clicks the {string} button")
    public void clicksTheButton(String buttonName) {
        if (buttonName.equals("Convert Data")) {
            apexToolHubPage.clickConvertData();
        }
    }

    @Then("the output text area should contain the CSV from file {string} under key {string}")
    public void theOutputTextAreaShouldContainTheCSVFromFileUnderKey(String filePath, String key) {
        String expectedCsv = JsonUtils.getValueFromFile(filePath, key);
        String actualCsv = apexToolHubPage.getOutputCsv();
        
        String normalizedExpected = expectedCsv.replace("\r\n", "\n").trim();
        String normalizedActual = actualCsv.replace("\r\n", "\n").trim();
        
        assertThat(normalizedActual)
                .as("Output CSV did not match expected")
                .isEqualTo(normalizedExpected);
    }

    @When("User uploads the file {string}")
    public void userUploadsFile(String relativePath) {
        String path = "src/test/resources/" + relativePath;
        apexToolHubPage.uploadFile(path);
    }

    @When("user clicks data-testid={string} button")
    @When("user clicks data-testid={string} button to clear the data")
    public void userClicksDataTestidButton(String testId) {
        apexToolHubPage.clickButtonByTestId(testId);
    }

    @Then("output text data-testid={string} should have content")
    public void outputTextDataTestidShouldHaveContent(String testId, String expectedContent) {
        String actualContent = apexToolHubPage.getTextByTestId(testId);
        // Normalize whitespace and newlines for robustness
        String normalizedExpected = expectedContent.replaceAll("\\s+", "").trim();
        String normalizedActual = actualContent.replaceAll("\\s+", "").trim();
        assertThat(normalizedActual).isEqualTo(normalizedExpected);
    }

    @Then("output text data-testid={string} should contain the JSON from file {string} under key {string}")
    public void outputTextDataTestidShouldContainJSONFromFileUnderKey(String testId, String filePath, String key) {
        String expectedJson = JsonUtils.getValueFromFile(filePath, key);
        String actualJson = apexToolHubPage.getTextByTestId(testId);
        
        // Normalize whitespaces for comparison
        String normalizedExpected = expectedJson.replaceAll("\\s+", "").trim();
        String normalizedActual = actualJson.replaceAll("\\s+", "").trim();
        
        assertThat(normalizedActual)
                .as("Output JSON under data-testid '%s' did not match expected", testId)
                .isEqualTo(normalizedExpected);
    }

    @Then("verify both text area are blank.")
    public void verifyBothTextAreaAreBlank() {
        assertThat(apexToolHubPage.isFileUploadBlank())
                .as("File upload input should be blank")
                .isTrue();
        assertThat(apexToolHubPage.isOutputBlankByTestId("json-output"))
                .as("Output JSON text area should be blank")
                .isTrue();
    }

    @Then("verify current EPOC time is showing on data-testid={string}")
    public void verifyCurrentEpochTimeIsShowing(String testId) {
        String text = apexToolHubPage.getTextByTestId(testId);
        assertThat(text).isNotEmpty();
        assertThat(text.matches("\\d+")).as("Expected current EPOC time to be a number: " + text).isTrue();
    }

    @Then("enter {string} in data-testid={string} text box")
    public void enterInTextBox(String value, String testId) {
        if (testId.equals("timestamp-input")) {
            lastEnteredEpoch = value;
        }
        apexToolHubPage.enterTextByTestId(testId, value);
    }

    @Then("user click on convert button element is data-testid={string}")
    public void userClickOnConvertButton(String testId) {
        apexToolHubPage.clickButtonByTestId(testId);
    }

    @Then("verify GMT date shows {string} on data-testid={string} element")
    public void verifyGmtDateShows(String expectedText, String testId) {
        String actualText = apexToolHubPage.getTextByTestId(testId);
        assertThat(actualText).isEqualTo(expectedText);
    }

    @Then("{string} on data-testid={string} element")
    public void verifyElementContent(String expectedText, String testId) {
        String actualText = apexToolHubPage.getTextByTestId(testId);
        if (testId.equals("local-date-output")) {
            String dynamicExpected = apexToolHubPage.evaluateJs("new Date(" + lastEnteredEpoch + " * 1000).toString()");
            assertThat(actualText).isEqualTo(dynamicExpected);
        } else {
            assertThat(actualText).isEqualTo(expectedText);
        }
    }

    @When("user click on pause button data-testid={string} then time stops on element data-testid={string}")
    public void userClicksPauseButtonAndTimeStops(String buttonTestId, String epochTestId) {
        apexToolHubPage.clickButtonByTestId(buttonTestId);
        
        String initialEpoch = apexToolHubPage.getTextByTestId(epochTestId);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String newEpoch = apexToolHubPage.getTextByTestId(epochTestId);
        
        assertThat(newEpoch)
                .as("Epoch time did not stop after pausing")
                .isEqualTo(initialEpoch);
    }

    @When("user click on resume button using data-testid={string} then time starts changing on element data-testid={string}")
    public void userClicksResumeButtonAndTimeStartsChanging(String buttonTestId, String epochTestId) {
        apexToolHubPage.clickButtonByTestId(buttonTestId);
        
        String initialEpoch = apexToolHubPage.getTextByTestId(epochTestId);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String newEpoch = apexToolHubPage.getTextByTestId(epochTestId);
        
        assertThat(newEpoch)
                .as("Epoch time did not start changing after resuming")
                .isNotEqualTo(initialEpoch);
    }
}
