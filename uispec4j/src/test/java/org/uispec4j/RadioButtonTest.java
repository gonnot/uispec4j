package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uispec4j.utils.UIComponentFactory;
import org.uispec4j.xml.XmlAssert;

import javax.swing.*;

public class RadioButtonTest extends ButtonTestCase {
  private RadioButton radioButton;
  private JRadioButton jRadioButton;

  @BeforeEach
  final protected void setUp() throws Exception {
    jRadioButton = new JRadioButton("myRadioButton");
    radioButton = (RadioButton)UIComponentFactory.createUIComponent(jRadioButton);
  }

  protected AbstractButton getButton() {
    return radioButton;
  }

  protected javax.swing.AbstractButton getSwingButton() {
    return jRadioButton;
  }

  @Test
  public void testGetComponentTypeName() throws Exception {
    Assertions.assertEquals("radioButton", radioButton.getDescriptionTypeName());
  }

  @Test
  public void testGetDescription() throws Exception {
    XmlAssert.assertEquivalent("<radioButton label='myRadioButton'/>", radioButton.getDescription());
  }

  @Test
  public void testFactory() throws Exception {
    checkFactory(jRadioButton, RadioButton.class);
  }

  @Test
  public void testIsActivated() throws Exception {
    assertFalse(radioButton.isSelected());
    jRadioButton.doClick();
    assertTrue(radioButton.isSelected());
    jRadioButton.doClick();
    assertFalse(radioButton.isSelected());
  }

  @Test
  public void testActivate() throws Exception {
    assertFalse(radioButton.isSelected());
    radioButton.click();
    Assertions.assertTrue(jRadioButton.isSelected());
    assertTrue(radioButton.isSelected());
    radioButton.click();
    assertFalse(radioButton.isSelected());
    Assertions.assertFalse(jRadioButton.isSelected());
  }
}