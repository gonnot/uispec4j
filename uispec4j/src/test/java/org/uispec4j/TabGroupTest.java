package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uispec4j.utils.AssertionFailureNotDetectedError;
import org.uispec4j.utils.ColorUtils;
import org.uispec4j.utils.UIComponentFactory;
import org.uispec4j.xml.XmlAssert;

import javax.swing.*;
import java.awt.*;

public class TabGroupTest extends UIComponentTestCase {
  private TabGroup tabGroup;
  private JTabbedPane jTabbedPane;

  @BeforeEach
  final protected void setUp() throws Exception {

    jTabbedPane = new JTabbedPane();
    jTabbedPane.setName("myTabbedPane");
    addTab("1", "tab1");
    addTab("2", "tab2");
    addTab("3", "tab3");
    tabGroup = new TabGroup(jTabbedPane);
  }

  @Test
  public void testGetComponentTypeName() throws Exception {
    Assertions.assertEquals("tabGroup", UIComponentFactory.createUIComponent(new JTabbedPane()).getDescriptionTypeName());
  }

  @Test
  public void testGetDescription() throws Exception {
    checkTabDescription("1");
    checkTabDescription("2");
    checkTabDescription("3");
  }

  protected UIComponent createComponent() {
    return tabGroup;
  }

  @Test
  public void testCheckCurrentTab() throws Exception {
    assertTrue(tabGroup.selectedTabEquals("1"));
    try {
      assertTrue(tabGroup.selectedTabEquals("2"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
    }
  }

  @Test
  public void testClickOnTabWithPartOfItsKey() throws Exception {
    addTab("GrosseTable", "table");
    tabGroup.selectTab("grosse");
    assertTrue(tabGroup.selectedTabEquals("GrosseTable"));
  }

  @Test
  public void testCheckTabs() throws Exception {
    assertTrue(tabGroup.tabNamesEquals(new String[]{"1", "2", "3"}));
    try {
      assertTrue(tabGroup.tabNamesEquals(new String[]{"this", "is", "wrong"}));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      // Expected
    }
  }

  @Test
  public void testSetCurrentTab() throws Exception {
    tabGroup.selectTab("2");
    Assertions.assertEquals("2", jTabbedPane.getTitleAt(jTabbedPane.getSelectedIndex()));
    tabGroup.selectTab("3");
    Assertions.assertEquals("3", jTabbedPane.getTitleAt(jTabbedPane.getSelectedIndex()));
  }

  @Test
  public void testSetCurrentTabError() throws Exception {
    try {
      tabGroup.selectTab("unknown");
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("There is no tab labelled 'unknown' - existing tab names: [1, 2, 3] ==> expected: <true> but was: <false>", e.getMessage());
    }
  }

  @Test
  public void testGetDescriptionWhenTheTabContainsAPanel() throws Exception {
    JButton button = new JButton("btn");
    JPanel panel = new JPanel();
    panel.add(button);
    jTabbedPane.addTab("4", panel);
    tabGroup.selectTab("4");
    XmlAssert.assertEquivalent("<tabGroup name='myTabbedPane'>" +
                               "  <button label='btn'/>" +
                               "</tabGroup>", tabGroup.getDescription());
  }

  @Test
  public void testFactory() throws Exception {
    checkFactory(new JTabbedPane(), TabGroup.class);
  }

  @Test
  public void testTabLabelColor() throws Exception {
    jTabbedPane.setForegroundAt(0, Color.RED);
    assertTrue(tabGroup.tabColorEquals(new String[]{"RED", "BLACK", "BLACK"}));
  }

  @Test
  public void testCheckColorErrors() throws Exception {
    try {
      assertTrue(tabGroup.tabColorEquals(new String[]{"BLACK", "GREEN"}));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("You specified 2 colors but there are 3 tabs - ==> expected: <2> but was: <3>",
                              e.getMessage());
    }

    try {
      jTabbedPane.setForegroundAt(1, Color.BLACK);
      assertTrue(tabGroup.tabColorEquals(new String[]{"BLACK", "BLUE", "GREEN"}));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Unexpected color for tab '2' (index 1) - " +
                              "expected " + ColorUtils.getColorDescription("BLUE") +
                              " but was " + ColorUtils.getColorDescription("000000"),
                              e.getMessage());
    }
  }

  @Test
  public void testSearchComponentsWhenVisibleTabIsAPanel() throws Exception {
    JButton jButton = new JButton("button");
    Component jPanel1WithButton = createPanelWithComponent(jButton);
    JTable jtable = new JTable();
    Component jPanel2WithTable = createPanelWithComponent(jtable);

    jTabbedPane = new JTabbedPane();
    jTabbedPane.addTab("panel1WithButton", jPanel1WithButton);
    jTabbedPane.addTab("panel2WithTable", jPanel2WithTable);
    tabGroup = new TabGroup(jTabbedPane);

    Assertions.assertSame(jPanel1WithButton, tabGroup.getSelectedTab().getAwtComponent());
    Assertions.assertSame(jButton, tabGroup.getSelectedTab().getButton("button").getAwtComponent());

    tabGroup.selectTab("panel2WithTable");
    Assertions.assertSame(jPanel2WithTable, tabGroup.getSelectedTab().getAwtComponent());
    Assertions.assertSame(jtable, tabGroup.getSelectedTab().getTable().getAwtComponent());
  }

  @Test
  public void testSearchComponentsFailsWhenVisibleTabIsNotAPanel() throws Exception {
    jTabbedPane = new JTabbedPane();
    jTabbedPane.addTab("tree", new JTree());
    tabGroup = new TabGroup(jTabbedPane);

    try {
      tabGroup.getSelectedTab();
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("tabGroup.getSelectedTab() only supports JPanel components inside a JTabbedPane", e.getMessage());
    }
  }

  private Component createPanelWithComponent(Component component) {
    JPanel panel = new JPanel();
    panel.add(component);
    return panel;
  }

  private void checkTabDescription(String tabLabel) throws Exception {
    tabGroup.selectTab(tabLabel);
    XmlAssert.assertEquivalent("<tabGroup name='myTabbedPane'>" +
                               "  <textBox name='tab" + tabLabel + "'/>" +
                               "</tabGroup>", tabGroup.getDescription());
  }

  private void addTab(String index, String tabName) {
    JLabel label = new JLabel("");
    label.setName(tabName);
    jTabbedPane.addTab(index, label);
  }
}
