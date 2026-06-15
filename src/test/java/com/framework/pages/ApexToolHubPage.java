package com.framework.pages;

import com.microsoft.playwright.Page;

import java.nio.file.Paths;

public class ApexToolHubPage {
    private final Page page;

    private final String pageHeader = "h1";
    private final String clearButton = "div.flex.flex-col:nth-of-type(1) > div.flex.items-center:nth-of-type(1) > div.flex.gap-2 > button.text-xs.flex:nth-of-type(3)";
    private final String inputTextArea = ".flex-1.relative textarea";
    private final String outputTextArea = "[data-testid='csv-output']";
    private final String convertDataButton = "button:has-text('Convert Data')";

    public ApexToolHubPage(Page page) {
        this.page = page;
    }

    public void clickTool(String toolName) {
        page.locator("h3:has-text('" + toolName + "'), div.flex.flex-col.text-left:has-text('" + toolName + "'), span:has-text('" + toolName + "')").first().click();
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
    }

    public void clickJsonToCsvTool() {
        clickTool("JSON to CSV");
    }

    public String getPageHeader() {
        page.locator("h1:has-text('Converter')").waitFor();
        return page.textContent(pageHeader).trim();
    }

    public void clickClearButton() {
        page.locator(clearButton).click();
    }

    public void enterJsonPayload(String json) {
        // Assuming the first textarea is the input
        page.locator(inputTextArea).first().fill(json);
    }

    public void clickConvertData() {
        page.locator(convertDataButton).click();
    }

    public String getOutputCsv() {
        page.locator(outputTextArea).waitFor();
        page.waitForCondition(() -> !page.locator(outputTextArea).innerText().trim().isEmpty());
        return page.locator(outputTextArea).innerText().trim();
    }

    public void uploadFile(String filePath) {
        page.locator("input[type='file']").setInputFiles(Paths.get(filePath));
    }

    public void clickButtonByTestId(String testId) {
        page.locator("[data-testid='" + testId + "']").click();
    }

    public void enterTextByTestId(String testId, String text) {
        com.microsoft.playwright.Locator loc = page.locator("[data-testid='" + testId + "']");
        loc.waitFor();
        loc.fill(text);
    }

    public String evaluateJs(String expression) {
        return (String) page.evaluate(expression);
    }

    public String getTextByTestId(String testId) {
        com.microsoft.playwright.Locator loc = page.locator("[data-testid='" + testId + "']");
        loc.waitFor();
        try {
            String val = loc.inputValue();
            if (val != null && !val.isEmpty()) {
                return val;
            }
        } catch (Exception e) {
            // Not a form control supporting inputValue
        }
        return loc.textContent().trim();
    }

    public boolean isFileUploadBlank() {
        try {
            String val = page.locator("input[type='file']").inputValue();
            return val == null || val.isEmpty();
        } catch (Exception e) {
            return true; // if not found or no value
        }
    }

    public boolean isOutputBlankByTestId(String testId) {
        com.microsoft.playwright.Locator locator = page.locator("[data-testid='" + testId + "']");
        if (locator.count() == 0) {
            return true;
        }
        try {
            String val = locator.inputValue();
            if (val != null && !val.isEmpty()) {
                return false;
            }
        } catch (Exception e) {
            // Not a form control
        }
        String text = locator.textContent();
        return text == null || text.trim().isEmpty();
    }
}
