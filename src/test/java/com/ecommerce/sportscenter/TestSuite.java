package com.ecommerce.sportscenter;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Test Suite to run all tests
 * This can be used to execute all tests in the application at once
 */
@Suite
@SuiteDisplayName("E-Commerce Sports Center Test Suite")
@SelectPackages({
    "com.ecommerce.sportscenter.service",
    "com.ecommerce.sportscenter.controller",
    "com.ecommerce.sportscenter.repository",
    "com.ecommerce.sportscenter.exceptions",
    "com.ecommerce.sportscenter.integration"
})
public class TestSuite {
    // This class remains empty, it is used only as a holder for the above annotations
}
