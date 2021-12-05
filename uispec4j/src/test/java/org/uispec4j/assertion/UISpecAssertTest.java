package org.uispec4j.assertion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uispec4j.UISpec4J;
import org.uispec4j.utils.Chrono;
import org.uispec4j.utils.UnitTestCase;
import org.uispec4j.utils.Utils;

public class UISpecAssertTest extends UnitTestCase {

  @BeforeEach
  final protected void setUp() throws Exception {
    UISpec4J.setAssertionTimeLimit(UISpec4J.DEFAULT_ASSERTION_TIME_LIMIT);
  }

  @Test
  public void testAssertTrue() throws Exception {
    UISpecAssert.assertTrue(DummyAssertion.TRUE);
    checkAssertionError(() -> UISpecAssert.assertTrue(DummyAssertion.FALSE));
  }

  @Test
  public void testAssertTrueRetriesUntilTheAssertionSucceeds() throws Exception {
    Chrono chrono = Chrono.start();
    runThreadAndCheckAssertion(40, true);
    chrono.assertElapsedTimeLessThan(150);
  }

  @Test
  public void testWaitForAssertionDoesNotTakeIntoAccountGlobalWaitTimeLimit() throws Exception {
    UISpec4J.setAssertionTimeLimit(0);
    Chrono chrono = Chrono.start();
    runThreadAndWaitForAssertion(50, 100);
    chrono.assertElapsedTimeLessThan(200);

    UISpec4J.setAssertionTimeLimit(500);
    checkAssertionError(() -> runThreadAndWaitForAssertion(300, 100), "error!");

    final DummyAssertion assertion = new DummyAssertion("custom message");

    checkAssertionError(() -> UISpecAssert.waitUntil(assertion, 0), "custom message");

    checkAssertionError(() -> UISpecAssert.waitUntil("other message", assertion, 0), "other message");
  }

  @Test
  public void testAssertTrueRetriesUpToATimeLimit() throws Exception {
    checkAssertionError(() -> runThreadAndCheckAssertion(900, true), "error!");
  }

  @Test
  public void testAssertTrueAssertionErrorMessage() throws Exception {
    UISpec4J.setAssertionTimeLimit(0);
    final DummyAssertion assertion = new DummyAssertion("custom message");

    checkAssertionFails(assertion, "custom message");

    checkAssertionError(() -> UISpecAssert.assertTrue("other message", assertion), "other message");

    checkAssertionError(() -> {
                          assertion.setError("exception message");
                          UISpecAssert.assertTrue("assertTrue message", assertion);
                        },
                        "assertTrue message");
  }

  @Test
  public void testAssertFalseAssertionErrorMessage() throws Exception {
    UISpec4J.setAssertionTimeLimit(0);
    final DummyAssertion assertion = new DummyAssertion("custom message");

    checkAssertionError(() -> UISpecAssert.assertFalse(not(assertion)), "");

    checkAssertionError(() -> UISpecAssert.assertFalse("other message", not(assertion)), "other message");
  }

  @Test
  public void testAssertFalse() throws Exception {
    UISpecAssert.assertFalse(DummyAssertion.FALSE);
    checkAssertionError(() -> UISpecAssert.assertFalse(DummyAssertion.TRUE));
  }

  @Test
  public void testAssertFalseRetriesUntilTheAssertionFails() throws Exception {
    Chrono chrono = Chrono.start();
    runThreadAndCheckAssertion(80, false);
    chrono.assertElapsedTimeLessThan(200);
  }

  @Test
  public void testAssertFalseRetriesUpToATimeLimit() throws Exception {
    checkAssertionError(() -> runThreadAndCheckAssertion(900, false));
  }

  @Test
  public void testAssertEquals() throws Exception {
    UISpecAssert.assertEquals(false, DummyAssertion.FALSE);
    UISpecAssert.assertEquals(true, DummyAssertion.TRUE);
    checkAssertionError(() -> UISpecAssert.assertEquals(true, DummyAssertion.FALSE), DummyAssertion.DEFAULT_ERROR_MSG);
    checkAssertionError(() -> UISpecAssert.assertEquals(false, DummyAssertion.TRUE));
  }

  @Test
  public void testAssertEqualsWithMessage() throws Exception {
    final String message = "my custom message";
    UISpecAssert.assertEquals(message, false, DummyAssertion.FALSE);
    UISpecAssert.assertEquals(message, true, DummyAssertion.TRUE);
    checkAssertionError(() -> UISpecAssert.assertEquals(message, true, DummyAssertion.FALSE), message);
    checkAssertionError(() -> UISpecAssert.assertEquals(message, false, DummyAssertion.TRUE), message);
  }

  @Test
  public void testAssertionNegationOperator() {
    UISpecAssert.assertTrue(DummyAssertion.TRUE);
    UISpecAssert.assertFalse(UISpecAssert.not(DummyAssertion.TRUE));
    UISpecAssert.assertTrue(UISpecAssert.not(UISpecAssert.not(DummyAssertion.TRUE)));
  }

  @Test
  public void testAssertionIntersectionOperator() {
    DummyAssertion assertion = new DummyAssertion(true);
    UISpecAssert.assertTrue(UISpecAssert.and(assertion, DummyAssertion.TRUE));
    UISpecAssert.assertFalse(UISpecAssert.and(assertion, DummyAssertion.FALSE));

    assertion.setError("");
    UISpecAssert.assertFalse(UISpecAssert.and(assertion, DummyAssertion.TRUE));
    UISpecAssert.assertFalse(UISpecAssert.and(assertion, DummyAssertion.FALSE));
  }

  @Test
  public void testAssertionUnionOperator() {
    DummyAssertion assertion = new DummyAssertion(true);
    UISpecAssert.assertTrue(UISpecAssert.or(assertion, DummyAssertion.TRUE));
    UISpecAssert.assertTrue(UISpecAssert.or(assertion, DummyAssertion.FALSE));

    assertion.setError("");
    UISpecAssert.assertTrue(UISpecAssert.or(assertion, DummyAssertion.TRUE));
    UISpecAssert.assertTrue(UISpecAssert.or(assertion, DummyAssertion.TRUE, DummyAssertion.FALSE));
    UISpecAssert.assertTrue(UISpecAssert.or(assertion, DummyAssertion.FALSE, DummyAssertion.TRUE));
    UISpecAssert.assertFalse(UISpecAssert.or(assertion, DummyAssertion.FALSE));
  }

  private void runThreadAndCheckAssertion(int threadSleepTime, final boolean useAssertTrue) throws Exception {
    final DummyThread thread = new DummyThread(threadSleepTime);
    thread.start();
    Assertion assertion = new Assertion() {
      public void check() {
        if ((useAssertTrue) ? !thread.timeoutExpired : thread.timeoutExpired) {
          throw new AssertionError("error!");
        }
      }
    };
    if (useAssertTrue) {
      UISpecAssert.assertTrue(assertion);
    }
    else {
      UISpecAssert.assertFalse(assertion);
    }
    thread.join();
  }

  private void runThreadAndWaitForAssertion(int threadSleepTime, long waitTimeLimit) throws Exception {
    final DummyThread thread = new DummyThread(threadSleepTime);
    thread.start();
    Assertion assertion = new Assertion() {
      public void check() {
        if (!thread.timeoutExpired) {
          throw new AssertionError("error!");
        }
      }
    };
    UISpecAssert.waitUntil(assertion, waitTimeLimit);
    thread.join();
  }

  private static class DummyThread extends Thread {
    boolean timeoutExpired;
    private final int sleepTime;

    public DummyThread(int sleepTime) {
      this.sleepTime = sleepTime;
    }

    public void run() {
      Utils.sleep(sleepTime);
      timeoutExpired = true;
    }
  }
}
