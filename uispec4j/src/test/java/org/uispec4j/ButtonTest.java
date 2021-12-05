package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uispec4j.utils.UIComponentFactory;
import org.uispec4j.xml.XmlAssert;

import javax.swing.*;

public class ButtonTest extends ButtonTestCase {

  private final JButton jButton = new JButton();
  private Button button;

  @BeforeEach
  final protected void setUp() throws Exception {

    button = (Button)UIComponentFactory.createUIComponent(jButton);
  }

  protected AbstractButton getButton() {
    return button;
  }

  protected javax.swing.AbstractButton getSwingButton() {
    return jButton;
  }

  @Test
  public void testGetComponentTypeName() throws Exception {
    Assertions.assertEquals("button", button.getDescriptionTypeName());
  }

  @Test
  public void testGetDescription() throws Exception {
    XmlAssert.assertEquivalent("<button/>", button.getDescription());
    jButton.setText("toto");
    XmlAssert.assertEquivalent("<button label='toto'/>", button.getDescription());
  }

  @Test
  public void testFactory() throws Exception {
    checkFactory(new JButton(), Button.class);
  }
}
