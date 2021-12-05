package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uispec4j.xml.XmlAssert;

import javax.swing.AbstractButton;
import javax.swing.*;

public class ToggleButtonTest extends ButtonTestCase {

  private final JToggleButton jToggleButton = new JToggleButton();
  private ToggleButton toggle;

  @BeforeEach
  final protected void setUp() throws Exception {

    toggle = new ToggleButton(jToggleButton);
  }

  protected org.uispec4j.AbstractButton getButton() {
    return toggle;
  }

  protected AbstractButton getSwingButton() {
    return jToggleButton;
  }

  @Test
  public void testGetComponentTypeName() throws Exception {
    Assertions.assertEquals("toggleButton", toggle.getDescriptionTypeName());
  }

  @Test
  public void testGetDescription() throws Exception {
    XmlAssert.assertEquivalent("<toggleButton/>", toggle.getDescription());
    jToggleButton.setText("toto");
    XmlAssert.assertEquivalent("<toggleButton label='toto'/>", toggle.getDescription());
  }

  @Test
  public void testFactory() throws Exception {
    checkFactory(new JToggleButton(), ToggleButton.class);
  }

  @Test
  public void testSelectionThroughClick() throws Exception {
    jToggleButton.setSelected(false);
    toggle.click();
    assertTrue(toggle.isSelected());
    toggle.click();
    assertFalse(toggle.isSelected());
  }

  @Test
  public void testSelectAndUnselect() throws Exception {

    toggle.select();
    assertTrue(toggle.isSelected());

    toggle.select();
    assertTrue(toggle.isSelected());

    toggle.unselect();
    assertFalse(toggle.isSelected());

    toggle.unselect();
    assertFalse(toggle.isSelected());

    toggle.select();
    assertTrue(toggle.isSelected());
  }
}
