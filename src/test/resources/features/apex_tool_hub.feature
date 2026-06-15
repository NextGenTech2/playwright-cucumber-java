Feature: JSON to CSV Conversion
  As a user
  I want to convert a JSON payload to CSV using ApexToolHub
  So that I can verify the conversion functionality

  Scenario: Convert JSON to CSV directly
    Given the user opens the "apex.url"
    When clicks on the "JSON to CSV" tool
    Then the user should see the page header "JSON to CSV Converter"
    When the user clears the text area
    And enters the JSON payload from file "testdata/payloads.json" under key "employees_json"
    And clicks the "Convert Data" button
    Then the output text area should contain the CSV from file "testdata/expected_outputs.json" under key "employees_csv"

  @googleSearchCsvToJson
  Scenario: Convert JSON to CSV via Google Search
    Given the user opens the "google.url"
    When the user searches for "JSON to CSV Converter site apextoolhub"
    And clicks on the search result link for "apextoolhub.com"
    And clicks on the "JSON to CSV" tool
    Then the user should see the page header "JSON to CSV Converter"
    When the user clears the text area
    And enters the JSON payload from file "testdata/payloads.json" under key "employees_json"
    And clicks the "Convert Data" button
    Then the output text area should contain the CSV from file "testdata/expected_outputs.json" under key "employees_csv"

  @csvToJsonUpload
  Scenario: Convert CSV to JSON via Google Search
    Given the user opens the "google.url"
    When the user searches for "CSV to JSON Converter site apextoolhub"
    And clicks on the search result link for "apextoolhub.com"
    And clicks on the "CSV/Excel to JSON" tool
    Then the user should see the page header "CSV/Excel to JSON Converter"
    When User uploads the file "testdata/employees.csv"
    And user clicks data-testid="convert-btn" button
    Then output text data-testid="json-output" should contain the JSON from file "testdata/expected_outputs.json" under key "employees_converted_json"
    When user clicks data-testid="clear-data-btn" button to clear the data
    Then verify both text area are blank.

  @epochConverter
  Scenario: Epoch converter via Google Search
    Given the user opens the "google.url"
    When the user searches for "Epoch Converter apextoolhub"
    And clicks on the search result link for "apextoolhub.com"
    And clicks on the "Epoch Converter" tool
    Then the user should see the page header "Epoch Timestamp Converter"
    Then verify current EPOC time is showing on data-testid="current-epoch"
    Then enter "391855320" in data-testid="timestamp-input" text box
    And user click on convert button element is data-testid="convert-to-date-btn"
    Then verify GMT date shows "Wed, 02 Jun 1982 08:42:00 GMT" on data-testid="gmt-date-output" element
    And "Wed Jun 02 1982 14:12:00 GMT+0530 (India Standard Time)" on data-testid="local-date-output" element
    When user click on pause button data-testid="pause-resume-btn" then time stops on element data-testid="current-epoch"
    And user click on resume button using data-testid="pause-resume-btn" then time starts changing on element data-testid="current-epoch"
