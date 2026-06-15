# Technical Context & Design Decisions

This document details the architectural decisions, design patterns, and context configurations of the automation framework.

---

## 1. Context & Dependency Injection Pattern

To maintain clean and decoupled code in Cucumber step definitions, we avoid global/static variables or manual driver instances. Instead, we use **PicoContainer Dependency Injection** to manage state sharing.

### 🧬 TestContext
The `TestContext` class holds:
*   The Playwright `BrowserContext` and `Page` objects.
*   Singletons of Page Object classes (e.g., `LoginPage`, `GoogleSearchPage`, `ApexToolHubPage`).

```java
public class TestContext {
    private final BrowserContext browserContext;
    private final Page page;
    private final LoginPage loginPage;
    private final GoogleSearchPage googleSearchPage;
    private final ApexToolHubPage apexToolHubPage;

    public TestContext() {
        this.browserContext = DriverFactory.getBrowser().newContext();
        this.page = this.browserContext.newPage();
        this.loginPage = new LoginPage(this.page);
        this.googleSearchPage = new GoogleSearchPage(this.page);
        this.apexToolHubPage = new ApexToolHubPage(this.page);
    }
}
```

Cucumber automatically injects `TestContext` into the constructor of all step definition classes:
```java
public class ApexToolHubSteps {
    public ApexToolHubSteps(TestContext testContext) {
        this.googleSearchPage = testContext.getGoogleSearchPage();
        this.apexToolHubPage = testContext.getApexToolHubPage();
    }
}
```
This ensures a fresh browser page is used per scenario and Page objects are automatically wired with the correct active page context.

---

## 2. Zero-Hardcoding Data Driven Design

Feature files must not contain hardcoded JSON payloads or expected CSV blocks to keep tests maintainable and easy to edit.

- **Payloads file**: `src/test/resources/testdata/payloads.json` contains raw request inputs.
- **Expected Outputs file**: `src/test/resources/testdata/expected_outputs.json` contains the verified outputs.
- **File & Key Steps**: Steps dynamically load the string using a helper utility:
  ```gherkin
  And enters the JSON payload from file "testdata/payloads.json" under key "employees_json"
  Then output text data-testid="json-output" should contain the JSON from file "testdata/expected_outputs.json" under key "employees_converted_json"
  ```
- **Parsing**: `JsonUtils.getValueFromFile` loads and parses the target JSON key dynamically.

---

## 3. Timezone-Portable Validation for Date/Time

When converting UNIX Epoch timestamps to human-readable strings, JavaScript displays the date in the browser context's local timezone.

```
Expected local date output (India): Wed Jun 02 1982 14:12:00 GMT+0530 (India Standard Time)
Expected local date output (UTC CI): Wed Jun 02 1982 08:42:00 GMT+0000 (Coordinated Universal Time)
```

To prevent tests from failing on remote cloud systems or Jenkins pipelines running in different timezones:
1. We intercept the epoch input in `ApexToolHubSteps`.
2. We execute an in-browser JavaScript evaluation (`new Date(epoch * 1000).toString()`) to generate the *exact* timezone string the browser uses to format dates.
3. We dynamically compare the browser UI element content with this dynamically evaluated date string.

This makes the test environment-agnostic while still validating that the UI correctly converts and formats timestamps.

---

## 4. Bot Detection Bypass (Google Search)

Automated browsers can be blocked by search engines when typing queries. We mitigate this in two ways:
*   Passing the argument `--disable-blink-features=AutomationControlled` to the Playwright launch options.
*   Allowing headed browser execution (`channel = chrome`) for interactive search steps.
