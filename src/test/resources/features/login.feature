Feature: User Login
  As a user
  I want to login to the application
  So that I can access my dashboard

  Scenario Outline: Invalid login attempts
    Given the user is on the login page
    When the user enters username "<username>" and password "<password>"
    And clicks the login button
    Then the user should see an error message "<error_message>"

    Examples:
      | username | password       | error_message            |
      | invalid  | SuperSecret    | Your username is invalid |
      | tomsmith | invalid        | Your password is invalid |
