package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uispec4j.utils.UIComponentFactory;
import org.uispec4j.xml.XmlAssert;

import javax.swing.*;

public class TableComponentTest extends UIComponentTestCase {
  private Table table;
  private JTable jTable;

  @BeforeEach
  final protected void setUp() throws Exception {
    init(new JTable(new String[][]{}, new String[]{}));
  }

  private void init(JTable table) {
    jTable = table;
    jTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    jTable.setName("myTable");
    jTable.setDefaultEditor(Integer.class, new DefaultCellEditor(new JComboBox(new Object[]{3, 4, 5})));
    this.table = (Table)UIComponentFactory.createUIComponent(jTable);
  }

  @Test
  public void testGetComponentTypeName() throws Exception {
    Assertions.assertEquals("table", table.getDescriptionTypeName());
  }

  @Test
  public void testGetDescription() throws Exception {
    XmlAssert.assertEquivalent("<table name='myTable'/>", table.getDescription());
  }

  @Test
  public void testFactory() throws Exception {
    checkFactory(new JTable(), Table.class);
  }

  protected UIComponent createComponent() {
    return table;
  }
}
