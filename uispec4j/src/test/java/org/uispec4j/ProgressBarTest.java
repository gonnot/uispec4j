package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uispec4j.assertion.UISpecAssert;
import org.uispec4j.utils.AssertionFailureNotDetectedError;
import org.uispec4j.utils.UIComponentFactory;
import org.uispec4j.utils.Utils;
import org.uispec4j.xml.XmlAssert;

import javax.swing.*;

public class ProgressBarTest extends UIComponentTestCase {
  private ProgressBar progressBar;
  private JProgressBar jProgressBar;

  @BeforeEach
  final protected void setUp() {
    jProgressBar = new JProgressBar();
    jProgressBar.setName("myProgressBar");
    progressBar = (ProgressBar)UIComponentFactory.createUIComponent(jProgressBar);
  }

  @Test
  public void testGetComponentTypeName() {
    Assertions.assertEquals("progressBar", progressBar.getDescriptionTypeName());
  }

  @Test
  public void testGetDescription() {
    XmlAssert.assertEquivalent("<progressBar name='myProgressBar'/>", progressBar.getDescription());
  }

  @Test
  public void testFactory() {
    checkFactory(new JProgressBar(), ProgressBar.class);
  }

  protected UIComponent createComponent() {
    return progressBar;
  }

  @Test
  public void testAssertValueEquals() {
    setProgressValues(5, 15, 10);
    assertTrue(progressBar.completionEquals(50));
    checkAssertCompletionError(10, 50);
  }

  @Test
  public void testCompleted() {
    setProgressValues(5, 15, 15);
    assertTrue(progressBar.completionEquals(100));
    assertTrue(progressBar.isCompleted());

    setProgressValues(0, 10, 5);
    try {
      assertTrue(progressBar.isCompleted());
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Unexpected completion rate - ==> expected: <100> but was: <50>", e.getMessage());
    }

    jProgressBar.setIndeterminate(true);
    try {
      assertTrue(progressBar.isCompleted());
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Unexpected completion rate - ==> expected: <100> but was: <-1>", e.getMessage());
    }
  }

  @Test
  public void testAssertCompletionEqualsAcceptsValuesBetween0And100() {
    checkAssertCompletionError(-2, "Expected value should be in range [0,100]");
    checkAssertCompletionError(101, "Expected value should be in range [0,100]");
  }

  @Test
  public void testExpectedValueIsMinusOneWhenTheProgressBarIsIndeterminate() {
    jProgressBar.setIndeterminate(false);
    try {
      assertTrue(progressBar.completionEquals(-1));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("The progress bar status is not indeterminate ==> expected: <true> but was: <false>", e.getMessage());
    }
  }

  @Test
  public void testAssertCompletionEqualsChecksTheValidityOfTheMinMaxRange() {
    setProgressValues(10, -5, 8);
    checkAssertCompletionError(8, "Invalid range [-5,-5]");
    setProgressValues(10, 10, 10);
    checkAssertCompletionError(10, "Invalid range [10,10]");
  }

  @Test
  public void testAssertValueWhenProgressBarIsInIndeterminateMode() {
    jProgressBar.setIndeterminate(true);
    assertTrue(progressBar.completionEquals(-1));
  }

  @Test
  public void testUsingAPrecision() {
    setProgressValues(0, 100, 23);
    assertTrue(progressBar.completionEquals(22));
    assertTrue(progressBar.completionEquals(24));

    progressBar.setPrecision(5);
    assertTrue(progressBar.completionEquals(28));
    checkAssertCompletionError(29, 23);
    assertTrue(progressBar.completionEquals(18));
    checkAssertCompletionError(17, 23);
  }

  @Test
  public void testWaitForCompletion() throws InterruptedException {
    checkWaitForCompletion();
  }

  @Test
  public void testWaitForCompletionWithIndeterminateMode() throws InterruptedException {
    jProgressBar.setIndeterminate(true);
    checkWaitForCompletion();
  }

  @Test
  public void testAssertDisplayedValueEquals() {
    assertTrue(progressBar.displayedValueEquals(jProgressBar.getString()));
    jProgressBar.setString("done");
    assertTrue(progressBar.displayedValueEquals("done"));
    try {
      assertTrue(progressBar.displayedValueEquals("unexpected"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("expected: <unexpected> but was: <done>", e.getMessage());
    }
  }

  private void setProgressValues(int min, int max, int value) {
    jProgressBar.setMinimum(min);
    jProgressBar.setMaximum(max);
    jProgressBar.setValue(value);
  }

  private void checkAssertCompletionError(int expectedValue, String errorMessage) {
    try {
      assertTrue(progressBar.completionEquals(expectedValue));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals(errorMessage, e.getMessage());
    }
  }

  private void checkAssertCompletionError(int expectedValue, int actualValue) {
    checkAssertCompletionError(expectedValue,
                               "Unexpected completion rate - ==> expected: <" + expectedValue + "> but was: <" + actualValue + ">");
  }

  private void checkWaitForCompletion() throws InterruptedException {
    setProgressValues(0, 100, 0);
    Thread thread = new Thread(() -> {
      Utils.sleep(50);
      jProgressBar.setValue(100);
      jProgressBar.setIndeterminate(false);
      jProgressBar.setValue(100);
    });
    thread.start();
    UISpecAssert.waitUntil(progressBar.isCompleted(), 200);
    progressBar.completionEquals(100);
    thread.join(1000);
  }
}