package com.framework.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        tags = "@jsonSuite or @jsonToXml or @xmlToJson",
        glue = {"com.framework.steps", "com.framework.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports-runner3.html",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        monochrome = true
)
public class Runner3 {
}
