# Playwright BDD Automation Framework

A modern, highly optimized test automation framework built using **Java**, **Playwright**, **Cucumber (BDD)**, **PicoContainer (Dependency Injection)**, and **AssertJ**.

This repository focuses on testing and automating services under the ApexToolHub platform, such as JSON-to-CSV converters, CSV-to-JSON converters, and Epoch Timestamp converters.

---

## 🚀 Key Features

*   **BDD with Cucumber**: Expressive scenarios written in Gherkin syntax.
*   **Page Object Model (POM)**: Clean separation of BDD steps and page interaction details (locators, actions).
*   **Playwright Auto-Waiting**: Zero manual `Thread.sleep()` or hardcoded timeouts — one global timeout in `config.properties` governs all operations automatically.
*   **Zero-Hardcoding / Data-Driven**: Scenario payloads and expected output mappings are parameterized and stored as external files (`payloads.json`, `expected_outputs.json`).
*   **PicoContainer DI**: Step definition classes share browser state and page object instances cleanly through constructor Dependency Injection (DI).
*   **Timezone-Portable Epoch Testing**: Dynamically evaluates Epoch date/time values inside the browser instance so that assertions pass flawlessly in any timezone (local, UTC/Jenkins, etc.).
*   **Stealth Mode Browser**: Bypasses bot/automation detection algorithms on search engines.

---

## 🏛️ Design Patterns

### 1. Page Object Model (POM)
The framework enforces the Page Object Model (POM) pattern to maintain structural separation:
*   **Page Classes** (`src/test/java/com/framework/pages/`): Define and encapsulate the page's locators (e.g., `data-testid`, CSS paths) and actions (`clickTool()`, `enterJsonPayload()`, `uploadFile()`). Step definitions invoke these public methods without direct access to Playwright locators.
*   **Maintainability**: Any changes to UI structure only require modifying the locator definitions in the Page Object class rather than editing BDD step definitions.

### 2. Dependency Injection (DI)
*   Page Object singletons and the Playwright browser context are managed and passed using **PicoContainer** constructor injection, ensuring isolation and clean state sharing.

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

> ⚠️ `mvn test` uses Surefire which is **disabled** in this framework. Use `mvn verify` instead (see Parallel & Serial Execution section below).

To run all tests in parallel:
```powershell
mvn verify
```

To run a specific runner with a tag:
```powershell
mvn verify "-Dit.test=Runner1" "-Dcucumber.filter.tags=@epochConverter"
```

---

## 📊 Reports
After tests complete:
*   Standard HTML Cucumber reports are generated under `target/cucumber-reports.html`.
*   Allure test results are saved in `target/allure-results/`. Generate the Allure report:
    ```bash
    mvn allure:serve
    ```

---

## ⏱️ Playwright Auto-Waiting — No Manual Timeouts

This is a core principle of the framework. **Playwright has built-in smart waiting** — it automatically waits for elements to be visible, stable, enabled, and not obscured before interacting.

### The Selenium vs Playwright Anti-Pattern

```
SELENIUM (anti-pattern)               PLAYWRIGHT (this framework)
─────────────────────────────         ──────────────────────────────────────
Thread.sleep(3000);          ❌        // nothing needed               ✅
WebDriverWait(driver, 15)    ❌        locator.click()  ← auto-waits   ✅
  .until(ExpectedConditions)
driver.findElement(...)
```

### How It Works — One Setting, Everywhere

The global timeout is set **once** in `config.properties` and applied to the entire framework via `BrowserContext.setDefaultTimeout()` in `TestContext.java`:

```
config.properties
  default.timeout=30000
        │
        ▼
TestContext.java
  browserContext.setDefaultTimeout(30000)   ← set ONCE
        │
        ▼  propagates automatically to:
  locator.click()            ← waits up to 30s for element to be ready
  locator.waitFor()          ← waits up to 30s
  page.waitForURL(...)       ← waits up to 30s
  page.waitForLoadState(...) ← waits up to 30s
  page.navigate(...)         ← waits up to 30s
```

### Adjusting the Timeout

Change `default.timeout` in `config.properties` — no code changes needed:

```properties
# config.properties
default.timeout=30000   # Local / fast machine
default.timeout=60000   # CI / slow network
```

Or override at runtime via CLI:
```powershell
mvn verify -Ddefault.timeout=60000
```

> **Rule:** Never add `setTimeout(...)`, `Thread.sleep()`, or hardcoded waits in page objects or step definitions. If a wait is needed, increase `default.timeout` in config.

---

## ⚡ Parallel & Serial Execution

> **Note:** There is no `playwright.config` file in this framework — that is a Node.js/TypeScript concept.
> In Java, parallel execution is controlled entirely through **Maven plugins + Cucumber runner classes**.

---

### How It Works — Architecture

```
TRUE PARALLEL (mvn verify)
──────────────────────────────────────────────────────────────────
 Maven Failsafe Plugin (forkCount=3)
   │
   ├── JVM Fork 1  →  Runner1  →  @epochConverter, @unitConverter
   │                              └── own Playwright + Chromium
   │
   ├── JVM Fork 2  →  Runner2  →  @csvToJsonUpload, @googleSearchCsvToJson
   │                              └── own Playwright + Chromium
   │
   └── JVM Fork 3  →  Runner3  →  @jsonSuite, @jsonToXml, @xmlToJson
                                  └── own Playwright + Chromium

  Total time ≈ slowest runner (not sum of all)
──────────────────────────────────────────────────────────────────

SEQUENTIAL (mvn test — skipped/disabled)
──────────────────────────────────────────────────────────────────
 Maven Surefire Plugin  →  SKIPPED (skip=true in pom.xml)
──────────────────────────────────────────────────────────────────
```

---

### Runner Split Strategy

Each runner covers a logical feature group and lives in `src/test/java/com/framework/runners/`:

| Runner | Tags Covered | Picked up by Failsafe? |
|---|---|---|
| `Runner1.java` | `@epochConverter`, `@unitConverter` | ✅ matches `**/Runner*.java` |
| `Runner2.java` | `@csvToJsonUpload`, `@googleSearchCsvToJson` | ✅ matches `**/Runner*.java` |
| `Runner3.java` | `@jsonSuite`, `@jsonToXml`, `@xmlToJson` | ✅ matches `**/Runner*.java` |
| `TestRunner.java` | all tags (sequential, for local IDE runs) | ❌ excluded — does not start with `Runner` |

> **Why `TestRunner` is excluded:** The Failsafe include pattern is `**/Runner*.java`.
> `TestRunner` starts with `Test`, not `Runner`, so Failsafe intentionally skips it.
> Use `TestRunner` only from the IDE (right-click → Run As JUnit Test).

---

### `pom.xml` — Final Working Configuration

```xml
<!-- Surefire: skipped so only Failsafe runs tests -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.5</version>
    <configuration>
        <skip>true</skip>
    </configuration>
</plugin>

<!-- Failsafe: true parallel execution via JVM forks -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>3.2.5</version>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
            <configuration>
                <!-- Only pick up Runner1, Runner2, Runner3 — NOT TestRunner -->
                <includes>
                    <include>**/Runner*.java</include>
                </includes>
                <!--
                  TRUE PARALLEL: forkCount=3 creates 3 separate JVM processes.
                  Each JVM handles one Runner class with its own Playwright + Chromium.
                  argLine caps heap per fork: 3 × 512MB = 1.5GB max total.
                -->
                <forkCount>3</forkCount>
                <reuseForks>true</reuseForks>
                <argLine>-Xmx512m -Xms128m</argLine>
            </configuration>
        </execution>
    </executions>
</plugin>
```

**Memory budget per fork:**

| Resource | Per Fork | × 3 Forks | Total |
|---|---|---|---|
| JVM heap (`-Xmx512m`) | 512 MB | × 3 | ~1.5 GB |
| Playwright Chromium | ~80–100 MB | × 3 | ~300 MB |
| **Grand total** | | | **~1.8 GB** |

---

### Thread Safety — How It Is Guaranteed

**`DriverFactory.java`** uses `ThreadLocal` — each JVM fork / thread maintains its own isolated Playwright + Browser:

```java
private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
private static final ThreadLocal<Browser>    browserThreadLocal     = new ThreadLocal<>();
```

**`TestContext.java`** is instantiated fresh per scenario by PicoContainer → `BrowserContext` and `Page` are never shared across runners.

**`Hooks.java` `@After`** calls `DriverFactory.quitBrowser()` → `ThreadLocal.remove()` ensures clean teardown after every scenario.

---

### `config.properties` — Browser Settings

```properties
browser=chromium    # Use Playwright's bundled Chromium (lightweight ~80MB)
headless=true       # Always true for parallel — avoids display/memory conflicts
                    # Do NOT set channel=chrome for parallel — full Chrome (~150MB)
                    # would cause Windows paging file exhaustion across 3 forks
```

---

### 🧪 All Run Commands

#### Run All Tests in Parallel

```powershell
# Run all 3 runners simultaneously (default parallel mode)
mvn verify

# With explicit headless flag
mvn verify -Dheadless=true

# With explicit browser (chromium / firefox / webkit)
mvn verify -Dbrowser=chromium -Dheadless=true
```

#### Run a Single Specific Runner

```powershell
# Run only Runner1 (epochConverter + unitConverter)
mvn verify "-Dit.test=Runner1"

# Run only Runner2 (csvToJson + googleSearchCsvToJson)
mvn verify "-Dit.test=Runner2"

# Run only Runner3 (jsonSuite + jsonToXml + xmlToJson)
mvn verify "-Dit.test=Runner3"

# Run two runners together
mvn verify "-Dit.test=Runner1,Runner2"
```

#### Run by Tag (Override Tags at Runtime)

```powershell
# Run only @unitConverter scenario via Runner1
mvn verify "-Dit.test=Runner1" "-Dcucumber.filter.tags=@unitConverter"

# Run only @epochConverter scenario
mvn verify "-Dit.test=Runner1" "-Dcucumber.filter.tags=@epochConverter"

# Run only @csvToJsonUpload scenario via Runner2
mvn verify "-Dit.test=Runner2" "-Dcucumber.filter.tags=@csvToJsonUpload"

# Run only @jsonSuite via Runner3
mvn verify "-Dit.test=Runner3" "-Dcucumber.filter.tags=@jsonSuite"
```

> ⚠️ **PowerShell Quoting Rule (Windows)**
>
> Always wrap `-Dit.test=...` and `-Dcucumber.filter.tags=...` in **double quotes** in PowerShell.
> Without quotes, PowerShell splits on `.` and `=`, causing Maven to see `.test=Runner1`
> as an unknown lifecycle phase:
>
> ```powershell
> # ❌ WRONG — PowerShell splits "-Dit.test=Runner1" incorrectly
> mvn verify -Dit.test=Runner1
>
> # ✅ CORRECT — quoted, passed as single argument
> mvn verify "-Dit.test=Runner1"
> ```

---

### Parallel vs Sequential — Quick Reference

| Command | Plugin | Mode | Time |
|---|---|---|---|
| `mvn test` | Surefire | **SKIPPED** (disabled) | — |
| `mvn verify` | Failsafe | ✅ All 3 runners in parallel | ~Slowest runner |
| `mvn verify "-Dit.test=Runner1"` | Failsafe | ✅ Runner1 only | ~Runner1 time |
| `mvn verify "-Dit.test=Runner1" "-Dcucumber.filter.tags=@unitConverter"` | Failsafe | ✅ Single tag | ~Scenario time |
| Right-click `TestRunner.java` → Run As JUnit | IDE | Sequential (all tags) | Sum of all |

---

### Common Mistakes & Fixes

| Mistake | Symptom | Fix |
|---|---|---|
| Using `channel=chrome` | Windows paging file OOM crash | Remove `channel=` — use bundled Chromium |
| `forkCount=1` with `parallel=classes` | Tests run sequentially in one JVM | Use `forkCount=3` for true parallel JVM forks |
| `-Dit.test=Runner1` (unquoted in PowerShell) | `Unknown lifecycle phase ".test=Runner1"` | Quote it: `"-Dit.test=Runner1"` |
| `**/*Runner.java` include pattern | Only `TestRunner` runs, not `Runner1/2/3` | Use `**/Runner*.java` pattern |
| Using `mvn test` instead of `mvn verify` | Surefire skipped, no tests run | Always use `mvn verify` for Failsafe |


