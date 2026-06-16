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
    And user clicks the "Convert" button
    Then the output text area should contain the JSON from file "testdata/expected_outputs.json" under key "employees_converted_json"
    When user clicks the "Clear Data" button
    Then verify both text area are blank.

  @epochConverter
  Scenario: Epoch converter via Google Search
    Given the user opens the "google.url"
    When the user searches for "Epoch Converter apextoolhub"
    And clicks on the search result link for "apextoolhub.com"
    And clicks on the "Epoch Converter" tool
    Then verify the current Epoch time is displayed
    When user enters "391855320" in "Epoch Timestamp" text box
    And user clicks the "Epoch Convert" button
    Then verify GMT date shows "Wed, 02 Jun 1982 08:42:00 GMT"
    And verify local date shows "Wed Jun 02 1982 14:12:00 GMT+0530 (India Standard Time)"
    When user clicks the "Pause" button then current Epoch time stops changing
    And user clicks the "Resume" button then current Epoch time starts changing

  @unitConverter
  Scenario: Unit converter via Google Search
    Given the user opens the "google.url"
    When the user searches for "Unit Converter apextoolhub"
    And clicks on the search result link for "apextoolhub.com"
    And clicks on the "Unit Converter" tool
    Then the user should see the page header "Unit & Dimension Converter"
    When user enters "1" in "Terabyte" text box
    Then verify "Gigabyte" text box has "1024"
    And verify "Megabyte" text box has "1048576"
    And verify "Kilobyte" text box has "1073741824"
    And verify "Byte" text box has "1099511627776"
    When user enters "1" in "Kilometer" text box
    Then verify "Mile" text box has "0.6213711922"
    And verify "Meter" text box has "1000"
    And verify "Yard" text box has "1093.6132983377"
    And verify "Foot" text box has "3280.839895"
    And verify "Inch" text box has "39370.07874"
    When user enters "1" in "KG" text box
    Then verify "Gram" text box has "1000"
    And verify "Pound" text box has "2.2046226218"
    And verify "Ounce" text box has "35.2739619496"
    When user clicks the "Reset All" button
    Then verify all input text boxes on this page are reset to blank or zero

  @jsonSuite
  Scenario: JSON Suite formatting and validation via Google Search
    Given the user opens the "google.url"
    When the user searches for "JSON Formatter site apextoolhub"
    And clicks on the search result link for "apextoolhub.com"
    And clicks on the "JSON Suite" tool
    Then the user should see the page header "Data Format Workstation"
    When user enters "{\"name\": \"ApexToolHub\",\"features\":[\"format\",\"validate\"]}" in "raw text area" text box
    And user clicks the "Format JSON" button
    Then verify "JSON Output" text box has "{\n  \"name\": \"ApexToolHub\",\n  \"features\": [\n    \"format\",\n    \"validate\"\n  ]\n}"
    When user clicks the "Minify JSON" button
    Then verify "JSON Output" text box has "{\"name\":\"ApexToolHub\",\"features\":[\"format\",\"validate\"]}"
    When user clicks the "Validate Lint" button
    Then verify the validation message "The payload parsed successfully. Zero syntax lint errors detected" is displayed
    When user clicks the "Clear Input" button
    Then verify "raw text area" text box is blank
    When user enters "{\"name\" \"ApexToolHub\",\"features\":[\"format\",\"validate\"]}" in "raw text area" text box
    And user clicks the "Validate Lint" button
    Then verify the validation message "Expected ':' after property name in JSON (line 1 column 9)" is displayed

  @jsonToXml
  Scenario: JSON to XML formatting and validation via Google Search
    Given the user opens the "google.url"
    When the user searches for "JSON To XML site apextoolhub"
    And clicks on the search result link for "apextoolhub.com"
    And clicks on the "JSON Suite" tool
    Then the user should see the page header "Data Format Workstation"
    When user enters "{\"name\": \"ApexToolHub\",\"features\":[\"format\",\"validate\"]}" in "raw text area" text box
    And user clicks the "Convert to XML" button
    Then verify "XML Output" text box has "<root>\n  <name>ApexToolHub</name>\n  <features>format</features>\n  <features>validate</features>\n</root>"
    When user enters "{\"name\": \"ApexToolHub\",\"features\"[\"format\",\"validate\"]}" in "raw text area" text box
    And user clicks the "Convert to XML" button
    Then verify the validation message "Expected ':' after property name in JSON (line 1 column 34)" is displayed

  @xmlToJson
  Scenario: XML to JSON formatting and validation via Google Search
    Given the user opens the "google.url"
    When the user searches for "XML To JSON site apextoolhub"
    And clicks on the search result link for "apextoolhub.com"
    And clicks on the "JSON Suite" tool
    Then the user should see the page header "Data Format Workstation"
    When user enters "<root><name>ApexToolHub</name><features>format</features><features>validate</features></root>" in "raw text area" text box
    And user clicks the "Format & Validate XML" button
    Then verify "XML Output" text box has "<root>\n  <name>ApexToolHub</name>\n  <features>format</features>\n  <features>validate</features>\n</root>"
    When user clicks the "Minify XML" button
    Then verify "XML Output" text box has "<root><name>ApexToolHub</name><features>format</features><features>validate</features></root>"
    When user clicks the "Convert to JSON" button
    Then verify "JSON Output" text box has "{\n  \"root\": {\n    \"name\": \"ApexToolHub\",\n    \"features\": [\n      \"format\",\n      \"validate\"\n    ]\n  }\n}"
    When user clicks the "Convert to Excel (CSV)" button
    Then verify "CSV Output" text box has "format\nvalidate"
    When user enters "<root><name>ApexToolHub</name><features>format</features>" in "raw text area" text box
    And user clicks the "Format & Validate XML" button
    Then verify the validation message "Invalid XML Syntax: This page contains the following errors:error on line 1 at column 58: Premature end of data in tag root line 1 Below is a rendering of the page up to the first error." is displayed

