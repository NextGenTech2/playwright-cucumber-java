Feature: Verification of Stealth Mode
  As a test framework developer
  I want to verify that the browser stealth mode is active
  So that automation is not detected by search engines or anti-bot services

  @stealthCheck
  Scenario: Verify WebDriver is masked on bot detection page
    Given the user opens the "https://bot.sannysoft.com/"
    Then verify WebDriver check shows as "missing (passed)" or "passed"
    And verify navigator.webdriver JavaScript property is undefined
