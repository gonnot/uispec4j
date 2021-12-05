package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uispec4j.assertion.Assertion;
import org.uispec4j.assertion.UISpecAssert;
import org.uispec4j.interception.toolkit.Empty;
import org.uispec4j.utils.AssertionFailureNotDetectedError;
import org.uispec4j.utils.Chrono;
import org.uispec4j.utils.DummyActionListener;
import org.uispec4j.utils.Utils;

import javax.swing.*;

public abstract class ButtonTestCase extends UIComponentTestCase {

  protected abstract AbstractButton getButton();

  protected abstract javax.swing.AbstractButton getSwingButton();

  protected UIComponent createComponent() {
    return getButton();
  }

  @Test
  public void testEnableDisable() throws Exception {
    checkEnabled(true);
    checkEnabled(false);
  }

  private void checkEnabled(boolean enabled) {
    getSwingButton().setEnabled(enabled);
    Assertion enabledAssertion = getButton().isEnabled();
    assertTrue((enabled) ? enabledAssertion : not(enabledAssertion));
  }

  @Test
  public void testCheckText() throws Exception {
    getSwingButton().setText("text");
    assertTrue(getButton().textEquals("text"));
    try {
      assertTrue(getButton().textEquals("error"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
    }
  }

  @Test
  public void testCheckTextAcceptsNullText() throws Exception {
    getSwingButton().setText(null);
    assertTrue(getButton().textEquals(null));
  }

  @Test
  public void testCheckTextTrimsTheActualButtonText() throws Exception {
    getSwingButton().setText(" text  ");
    assertTrue(getButton().textEquals("text"));
  }

  @Test
  public void testIcons() throws Exception {
    Icon icon = new Empty.DummyIcon();
    checkAssertionFails(getButton().iconEquals(icon), "The component contains no icon. ==> expected: not <null>");

    getSwingButton().setIcon(icon);
    assertTrue(getButton().iconEquals(icon));

    assertFalse(getButton().iconEquals(new Empty.DummyIcon()));
  }

  @Test
  public void testActivateIsRejectedIfTheButtonIsDisabled() throws Exception {
    getSwingButton().setEnabled(false);
    try {
      getButton().click();
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("The button is not enabled, it cannot be activated ==> expected: <true> but was: <false>", e.getMessage());
    }
  }

  @Test
  public void testClick() throws Exception {
    DummyActionListener listener = new DummyActionListener();
    getSwingButton().addActionListener(listener);
    getButton().click();
    Assertions.assertEquals(1, listener.getCallCount());
  }

  @Test
  public void testTriggerClick() throws Exception {
    DummyActionListener listener = new DummyActionListener();
    getSwingButton().addActionListener(listener);
    getButton().triggerClick().run();
    Assertions.assertEquals(1, listener.getCallCount());
  }

  @Test
  public void testClickTakesLessTimeThanWithDefaultSwingCalls() throws Exception {
    Chrono chrono = Chrono.start();
    getButton().click();
    chrono.assertElapsedTimeLessThan(30);
  }

  @Test
  public void testWaitForEnabledState() throws Exception {
    AbstractButton button = getButton();
    final javax.swing.AbstractButton swingButton = (javax.swing.AbstractButton)button.getAwtComponent();
    swingButton.setEnabled(false);
    Thread thread = new Thread(new Runnable() {
      public void run() {
        Utils.sleep(10);
        swingButton.setEnabled(true);
      }
    });
    thread.start();
    assertFalse(button.isEnabled());
    UISpecAssert.waitUntil(button.isEnabled(), 30);
    assertTrue(button.isEnabled());
  }

  @Test
  public void testCheckButtonIsVisible() throws Exception {
    DummyActionListener listener = new DummyActionListener();
    getSwingButton().addActionListener(listener);
    getButton().getAwtComponent().setVisible(false);
    try {
      getButton().click();
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("The button is not visible, it cannot be activated ==> expected: <true> but was: <false>", e.getMessage());
    }
    Assertions.assertEquals(0, listener.getCallCount());
  }
}
