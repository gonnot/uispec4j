package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uispec4j.utils.AssertionFailureNotDetectedError;
import org.uispec4j.xml.XmlAssert;

import javax.swing.*;

public abstract class SpinnerTestCase extends UIComponentTestCase {
  protected JSpinner jSpinner;
  protected Spinner spinner;

  protected abstract SpinnerModel createSpinnerModel() throws Exception;

  protected abstract Spinner createSpinner(JSpinner jSpinner);

  @BeforeEach
  final protected void spinnerSetUp() throws Exception {
    init();
  }

  @Test
  public final void testGetComponentTypeName() throws Exception {
    Assertions.assertEquals("spinner", spinner.getDescriptionTypeName());
  }

  @Test
  public final void testGetDescription() throws Exception {
    XmlAssert.assertEquivalent("<spinner name='marcel'>" +
                               "  <button name='Spinner.nextButton'/>" +
                               "  <button name='Spinner.previousButton'/>" +
                               "  <textBox name='Spinner.formattedTextField' text='" + getText() + "'/>" +
                               "</spinner>", spinner.getDescription());
  }

  protected final UIComponent createComponent() {
    return spinner;
  }

  protected final void checkPreviousValueFails(String wrongPreviousValue) {
    try {
      assertTrue(spinner.previousValueEquals(wrongPreviousValue));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("No previous value from the start", e.getMessage());
    }
  }

  protected final void checkNextValueFails(String wrongNextValue) {
    try {
      assertTrue(spinner.nextValueEquals(wrongNextValue));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("No previous value from the end", e.getMessage());
    }
  }

  protected final void init() throws Exception {
    jSpinner = new JSpinner(createSpinnerModel());
    jSpinner.setName("marcel");
    spinner = createSpinner(jSpinner);
  }

  @Test
  public void testFactory() throws Exception {
    checkFactory(jSpinner, Spinner.class);
  }

  public abstract String getText();
}
