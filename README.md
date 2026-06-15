# Playwright BDD Automation Framework

A modern, highly optimized test automation framework built using **Java**, **Playwright**, **Cucumber (BDD)**, **PicoContainer (Dependency Injection)**, and **AssertJ**.

This repository focuses on testing and automating services under the ApexToolHub platform, such as JSON-to-CSV converters, CSV-to-JSON converters, and Epoch Timestamp converters.

---

## 🚀 Key Features

*   **BDD with Cucumber**: Expressive scenarios written in Gherkin syntax.
*   **Playwright Engine**: Ultra-fast execution, auto-waiting, and native headed/headless browser management.
*   **Zero-Hardcoding / Data-Driven**: Scenario payloads and expected output mappings are parameterized and stored as external files (`payloads.json`, `expected_outputs.json`).
*   **PicoContainer DI**: Step definition classes share browser state and page object instances cleanly through constructor Dependency Injection (DI).
*   **Timezone-Portable Epoch Testing**: Dynamically evaluates Epoch date/time values inside the browser instance so that assertions pass flawlessly in any timezone (local, UTC/Jenkins, etc.).
*   **Stealth Mode Browser**: Bypasses bot/automation detection algorithms on search engines.

---

## 🛠️ Tech Stack

*   **Core language**: Java 17
*   **Web Automation**: Playwright (v1.44.0)
*   **BDD Orchestrator**: Cucumber JVM (v7.14.0)
*   **Assertions**: AssertJ (v3.26.0)
*   **Dependency Injection**: Cucumber PicoContainer
*   **JSON Handling**: Jackson Databind
*   **Reporting**: Allure Reports & standard HTML Cucumber reports

---

## 📁 Project Structure

```
├── .gitignore
├── pom.xml
├── README.md
├── docs
│   └── context.md
└── src
    └── test
        ├── java
        │   └── com
        │       └── framework
        │           ├── context         # PicoContainer Context Injection
        │           ├── hooks           # Hooks for setup/teardown
        │           ├── pages           # Page Object Model (POM) classes
        │           ├── runners         # Test runners (Junit/Failsafe)
        │           ├── steps           # Cucumber step definitions
        │           └── utils           # Common helper utilities (JSON parser, Drivers)
        └── resources
            ├── config.properties      # Environmental configurations
            ├── features/               # Cucumber Feature Files (.feature)
            └── testdata/               # Test data (JSON, CSV inputs/outputs)
```

---

## ⚡ Quick Start

### 📋 Prerequisites
*   Java JDK 17 or higher
*   Apache Maven 3.8+

### 📦 Build the Project
Compile the source code and install required browser binaries:
```bash
mvn clean test-compile
```

### 🧪 Run Tests
Tests can be executed via Maven by specifying the tags configuration in the Test Runner or overriding via CLI:

To run all tests:
```bash
mvn test
```

To run a specific tag (e.g., Epoch converter):
```bash
mvn test -Dcucumber.filter.tags="@epochConverter"
```

To run the CSV-to-JSON upload test:
```bash
mvn test -Dcucumber.filter.tags="@csvToJsonUpload"
```

---

## 📊 Reports
After tests complete:
*   Standard HTML Cucumber reports are generated under `target/cucumber-reports.html`.
*   Allure test results are saved in `target/allure-results/`. Generate the Allure report:
    ```bash
    mvn allure:serve
    ```
