package org.uispec4j.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.uispec4j.UISpec4J;
import org.uispec4j.assertion.Assertion;
import org.uispec4j.assertion.UISpecAssert;
import org.uispec4j.interception.InterceptionError;

public abstract class UnitTestCase {
  static {
    UISpec4J.init();
  }

  @BeforeEach
  final public void unitTestCaseSetUp() {
    UISpec4J.setWindowInterceptionTimeLimit(100);
    UISpec4J.setAssertionTimeLimit(30);
  }

  public void assertTrue(Assertion assertion) {
    UISpecAssert.assertTrue(assertion);
  }

  public void waitUntil(Assertion assertion, int timeLimit) {
    UISpecAssert.waitUntil(assertion, timeLimit);
  }

  public void assertFalse(Assertion assertion) {
    UISpecAssert.assertFalse(assertion);
  }

  public Assertion not(Assertion assertion) {
    return UISpecAssert.not(assertion);
  }

  public void assertEquals(boolean expected, Assertion assertion) {
    UISpecAssert.assertEquals(expected, assertion);
  }

  protected void checkAssertionFails(Assertion assertion, String expectedMessage) throws Exception {
    try {
      assertTrue(assertion);
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals(expectedMessage, e.getMessage());
    }
  }

  protected void checkException(Functor functor, String expectedMessage) throws Exception {
    try {
      functor.run();
      throw new AssertionFailureNotDetectedError();
    }
    catch (Exception e) {
      Assertions.assertEquals(expectedMessage, e.getMessage());
    }
  }

  protected void checkInterceptionError(Functor functor, String expectedMessage) throws Exception {
    try {
      functor.run();
      throw new AssertionFailureNotDetectedError();
    }
    catch (InterceptionError e) {
      Assertions.assertEquals(expectedMessage, e.getMessage());
    }
  }

  protected void checkAssertionError(Functor functor, String expectedMessage) throws Exception {
    try {
      functor.run();
      throw new AssertionFailureNotDetectedError();
    }
    catch (Throwable e) {
      Assertions.assertEquals(expectedMessage, e.getMessage());
    }
  }

  protected void checkAssertionError(Functor functor) throws Exception {
    try {
      functor.run();
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError | InterceptionError ignored) {
    }
  }
}
