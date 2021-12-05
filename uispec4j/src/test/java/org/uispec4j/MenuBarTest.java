package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uispec4j.utils.AssertionFailureNotDetectedError;
import org.uispec4j.utils.UIComponentFactory;
import org.uispec4j.xml.XmlAssert;

import javax.swing.*;

public class MenuBarTest extends UIComponentTestCase {
  private MenuBar menuBar;
  private JMenuBar jMenuBar;
  private JMenu jFileMenu;
  private JMenu jEditMenu;

  @BeforeEach
  final protected void setUp() throws Exception {

    jMenuBar = new JMenuBar();
    jFileMenu = new JMenu("File");
    jFileMenu.setName("fileMenu");
    jMenuBar.add(jFileMenu);
    jEditMenu = new JMenu("Edit");
    jEditMenu.setName("editMenu");
    jMenuBar.add(jEditMenu);
    jMenuBar.setName("myMenuBar");
    menuBar = (MenuBar)UIComponentFactory.createUIComponent(jMenuBar);
  }

  @Test
  public void testGetComponentTypeName() throws Exception {
    Assertions.assertEquals("menuBar", menuBar.getDescriptionTypeName());
  }

  @Test
  public void testGetDescription() throws Exception {
    XmlAssert.assertEquivalent("<menuBar name='myMenuBar'>" +
                               "  <menu name='fileMenu'/>" +
                               "  <menu name='editMenu'/>" +
                               "</menuBar>", menuBar.getDescription());
  }

  @Test
  public void testFactory() throws Exception {
    checkFactory(new JMenuBar(), MenuBar.class);
  }

  protected UIComponent createComponent() {
    return menuBar;
  }

  @Test
  public void testGetContents() throws Exception {
    assertTrue(menuBar.contentEquals("File", "Edit"));
  }

  @Test
  public void testGetContentsError() throws Exception {
    try {
      assertTrue(menuBar.contentEquals("File", "Other"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
    }
  }

  @Test
  public void testGetMenu() throws Exception {
    Assertions.assertSame(jFileMenu, menuBar.getMenu("File").getAwtComponent());
    Assertions.assertSame(jEditMenu, menuBar.getMenu("Edit").getAwtComponent());
  }
}