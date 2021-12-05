package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uispec4j.utils.AssertionFailureNotDetectedError;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.fail;

public class TableContentTest extends TableTestCase {
  @Test
  public void testAssertContentEquals() {
    assertTrue(table.contentEquals(new Object[][]{
      {"a", Boolean.TRUE, "3"},
      {"c", Boolean.FALSE, "4"}
    }));
  }

  @Test
  public void testAssertContentEqualsFailure() {
    try {
      assertTrue(table.contentEquals(new Object[][]{
        {"a", Boolean.TRUE, "3"},
        {"c", Boolean.FALSE, "ERROR!"}
      }));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError ignored) {
    }
  }

  @Test
  public void testAssertEmptyFailure() {
    try {
      assertTrue(table.isEmpty());
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Expected: empty table but was:[[a,\ttrue,\t3]\n [c,\tfalse,\t4]]", e.getMessage());
    }
  }

  @Test
  public void testBlockEqual() {
    assertTrue(table.blockEquals(1, 1, 2, 1, new Object[][]{
      {Boolean.FALSE, "4"}
    }));
  }

  @Test
  public void testBlockEqualFailure() {
    try {
      assertTrue(table.blockEquals(1, 1, 2, 1, new Object[][]{
        {"c", Boolean.FALSE, "4"}
      }));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError ignored) {
    }
  }

  @Test
  public void testPartialRowEqual() {
    assertTrue(table.rowEquals(1, 1, 2, new Object[]{Boolean.FALSE, "4"}));
  }

  @Test
  public void testPartialRowEqualFailure() {
    try {
      assertTrue(table.rowEquals(1, 0, 2, new Object[]{Boolean.FALSE, "4"}));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError ignored) {
    }
  }

  @Test
  public void testContentEqualsUsesTheModelWhenAnUnknownRendererComponentIsUsed() {
    installJPanelRenderer();
    assertTrue(table.contentEquals(new Object[][]{
      {"a", "true", "3"},
      {"c", "false", "4"}
    }));
    try {
      assertTrue(table.contentEquals(new Object[][]{
        {"a", Boolean.TRUE, "3"},
        {"c", Boolean.FALSE, "4"}
      }));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError ignored) {
    }
  }

  @Test
  public void testContentEqualsWithUnknownRendererComponentAndNullModelValue() {
    installJPanelRenderer();
    jTable.getModel().setValueAt(null, 0, 1);
    assertTrue(table.contentEquals(new Object[][]{
      {"a", "", "3"},
      {"c", "false", "4"}
    }));
    try {
      assertTrue(table.contentEquals(new Object[][]{
        {"a", "", "3"},
        {"c", Boolean.FALSE, "4"}
      }));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError ignored) {
    }
  }

  @Test
  public void testContentEqualsWhenColumnMoved() {
    jTable.moveColumn(0, 1);
    assertTrue(table.contentEquals(new Object[][]{
      {Boolean.TRUE, "a", "3"},
      {Boolean.FALSE, "c", "4"}
    }));
    try {
      assertTrue(table.contentEquals(new Object[][]{
        {"a", Boolean.TRUE, "3"},
        {"c", Boolean.FALSE, "4"}
      }));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError ignored) {
    }
  }

  @Test
  public void testContentEqualsAfterSettingACustomCellValueConverterAsDefault() {
    table.setDefaultCellValueConverter((row, column, renderedComponent, modelObject) -> {
      if ((row == 1) && (column == 1)) {
        return "toto";
      }
      return modelObject.toString();
    });
    assertTrue(table.contentEquals(new Object[][]{
      {"a", "true", "3"},
      {"c", "toto", "4"}
    }));
    try {
      assertTrue(table.contentEquals(new Object[][]{
        {"a", Boolean.TRUE, "3"},
        {"c", Boolean.FALSE, "4"}
      }));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError ignored) {
    }
  }

  @Test
  public void testContentEqualsAfterSettingACustomCellValueConverterOnAColumn() {
    table.setCellValueConverter(0, (row, column, renderedComponent, modelObject) -> "custom " + modelObject);
    assertTrue(table.contentEquals(new Object[][]{
      {"custom a", Boolean.TRUE, "3"},
      {"custom c", Boolean.FALSE, "4"}
    }));
    try {
      assertTrue(table.contentEquals(new Object[][]{
        {"a", Boolean.TRUE, "3"},
        {"c", Boolean.FALSE, "4"}
      }));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError ignored) {
    }
  }

  @Test
  public void testContentEqualsAfterSettingACustomCellValueConverterOnAColumnThatHasBeenMoved() {
    jTable.moveColumn(0, 1);
    table.setCellValueConverter(0, (row, column, renderedComponent, modelObject) -> "custom " + modelObject);
    assertTrue(table.contentEquals(new Object[][]{
      {"custom true", "a", "3"},
      {"custom false", "c", "4"}
    }));
    try {
      assertTrue(table.contentEquals(new Object[][]{
        {Boolean.TRUE, "a", "3"},
        {Boolean.FALSE, "c", "4"}
      }));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError ignored) {
    }
  }

  @Test
  public void testContentEqualsAfterSettingACustomCellValueConverterThatHandlesSelection() {
    jTable.setDefaultRenderer(Boolean.class, new DefaultTableCellRenderer() {
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        value = isSelected ? "selected " + value : value;
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      }
    });
    assertTrue(table.contentEquals(new Object[][]{
      {"a", "true", "3"},
      {"c", "false", "4"}
    }));

    table.selectRow(0);
    assertTrue(table.contentEquals(new Object[][]{
      {"a", "selected true", "3"},
      {"c", "false", "4"}
    }));
  }

  @Test
  public void testCustomContentEquals() {
    assertTrue(table.contentEquals(
      new String[]{"0", "1", "2"},
      new Object[][]{
        {"a", Boolean.TRUE, "3"},
        {"c", Boolean.FALSE, "4"}
      }));

    assertTrue(table.contentEquals(
      new String[]{"0", "2", "1"},
      new Object[][]{
        {"a", "3", Boolean.TRUE},
        {"c", "4", Boolean.FALSE}
      }));

    assertTrue(table.contentEquals(
      new String[]{"2", "0"},
      new Object[][]{
        {"3", "a"},
        {"4", "c"}
      }));
  }

  @Test
  public void testCustomContentEqualsErrors() {
    try {
      assertTrue(table.contentEquals(
        new String[]{"0", "2"},
        new Object[][]{
          {"a", "3"},
          {"c", "ERROR!"}
        }));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertTrue(e.getMessage().startsWith("Error at (1, 1)"));
    }
  }

  @Test
  public void testCustomContentEqualsChecksTheNumberOfColumns() {
    try {
      assertTrue(table.contentEquals(
        new String[]{"0", "2", "1"},
        new Object[][]{
          {"a", "3"},
          {"a", "3"},
          }));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Expected array should have 3 elements for each row - invalid row 0: [a,3]", e.getMessage());
    }
  }

  @Test
  public void testCustomContentEqualsChecksTheColumnNames() {
    try {
      assertTrue(table.contentEquals(
        new String[]{"0", "2", "unknown"},
        new Object[][]{
          {"a", "3", "x"}
        }));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Column 'unknown' not found - actual names: [0, 1, 2]", e.getMessage());
    }
  }

  @Test
  public void testCustomContentEqualsChecksTheNumberOfRows() {
    try {
      assertTrue(table.contentEquals(
        new String[]{"2", "0"},
        new Object[][]{
          {"3", "a"},
          {"4", "c"},
          {"5", "e"}
        }));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertTrue(e.getMessage().startsWith("Expected 3 rows but found 2"));
    }
  }

  @Test
  public void testCellEquals() {
    assertTrue(table.cellEquals(0, 0, "a"));
    assertTrue(table.cellEquals(1, 1, Boolean.FALSE));
  }

  @Test
  public void testCellEqualsError() {
    try {
      assertTrue(table.cellEquals(0, 0, "invalidValue"));
      fail();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Error at (0,0) - ==> expected: <invalidValue> but was: <a>", e.getMessage());
    }
  }

  @Test
  public void testCellEqualsWithConverter() {
    assertTrue(table.cellEquals(0, 0, "-a-", new DummyTableCellValueConverter()));
    assertTrue(table.cellEquals(1, 1, "-false-", new DummyTableCellValueConverter()));
  }

  @Test
  public void testRowEquals() {
    assertTrue(table.rowEquals(0, new Object[]{"a", Boolean.TRUE, "3"}));
    assertTrue(table.rowEquals(1, new Object[]{"c", Boolean.FALSE, "4"}));
  }

  @Test
  public void testRowEqualsFailures() {
    try {
      assertTrue(table.rowEquals(1, new Object[]{"c", Boolean.TRUE, "4"}));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("expected: <[c,true,4]> but was: <c,false,4>", e.getMessage());
    }

    try {
      assertTrue(table.rowEquals(-1, new Object[]{"a", "b", "c"}));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Row index should be positive", e.getMessage());
    }

    try {
      assertTrue(table.rowEquals(2, new Object[]{"a", "b", "c"}));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Table contains only 2 rows, unable to access row 2", e.getMessage());
    }
  }

  @Test
  public void testCustomRowEquals() {
    assertTrue(table.rowEquals(0, new String[]{"0", "1", "2"}, new Object[]{"a", Boolean.TRUE, "3"}));
    assertTrue(table.rowEquals(1, new String[]{"0", "1", "2"}, new Object[]{"c", Boolean.FALSE, "4"}));

    assertTrue(table.rowEquals(0, new String[]{"0", "2", "1"}, new Object[]{"a", "3", Boolean.TRUE}));

    assertTrue(table.rowEquals(0, new String[]{"2", "0"}, new Object[]{"3", "a"}));
  }

  @Test
  public void testCustomRowEqualsErrors() {
    try {
      assertTrue(table.rowEquals(0, new String[]{"0", "2"}, new Object[]{"a", "xxxxxx"}));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("""
                              Unexpected content at row 0
                              Expected: [a,xxxxxx]
                              Actual:   [a,3]""",
                              e.getMessage());
    }
  }

  @Test
  public void testCustomRowEqualsChecksTheNumberOfColumns() {
    try {
      assertTrue(table.rowEquals(0, new String[]{"0", "2", "1"}, new Object[]{"a", "3"}));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Expected array should have 3 elements for each row - invalid row 0: [a,3]", e.getMessage());
    }
  }

  @Test
  public void testCustomRowEqualsChecksTheColumnNames() {
    try {
      assertTrue(table.rowEquals(0, new String[]{"0", "2", "unknown"}, new Object[]{"a", "3", "x"}));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Column 'unknown' not found - actual names: [0, 1, 2]", e.getMessage());
    }
  }

  @Test
  public void testCustomRowEqualsChecksTheNumberOfRows() {
    try {
      assertTrue(table.rowEquals(3, new String[]{"2", "0"}, new Object[]{"3", "a"}));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Table contains only 2 rows, unable to access row 3", e.getMessage());
    }
  }

  @Test
  public void testColumnEquals() {
    assertTrue(table.columnEquals(0, new Object[]{"a", "c"}));
    assertTrue(table.columnEquals(1, new Object[]{Boolean.TRUE, Boolean.FALSE}));
    assertTrue(table.columnEquals(2, new Object[]{"3", "4"}));
  }

  @Test
  public void testColumnEqualsFailures() {
    try {
      assertTrue(table.columnEquals(1, new Object[]{Boolean.TRUE, Boolean.TRUE}));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("expected: <[true,true]> but was: <true,false>", e.getMessage());
    }

    try {
      assertTrue(table.columnEquals(-1, new Object[]{"a", "c"}));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Column index should be positive", e.getMessage());
    }

    try {
      assertTrue(table.columnEquals(3, new Object[]{"3", "4"}));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Table contains only 3 columns, unable to access column 3", e.getMessage());
    }
  }

  @Test
  public void testGetRowAndColumnCount() {
    Assertions.assertEquals(2, table.getRowCount());
    assertTrue(table.rowCountEquals(2));
    Assertions.assertEquals(3, table.getColumnCount());
    assertTrue(table.columnCountEquals(3));
  }

  @Test
  public void testRowCountError() {
    try {
      assertTrue(table.rowCountEquals(9999));
      fail();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Unexpected number of rows - ==> expected: <9999> but was: <2>", e.getMessage());
    }
  }

  @Test
  public void testColumnCountError() {
    try {
      assertTrue(table.columnCountEquals(9999));
      fail();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Unexpected number of columns - ==> expected: <9999> but was: <3>", e.getMessage());
    }
  }

  @Test
  public void testGetContentAt() {
    Assertions.assertEquals("a", table.getContentAt(0, 0));
    Assertions.assertEquals(Boolean.TRUE, table.getContentAt(0, 1));
    Assertions.assertEquals("c", table.getContentAt(1, 0));
    Assertions.assertEquals(Boolean.FALSE, table.getContentAt(1, 1));

    jTable.getModel().setValueAt(null, 0, 0);
    Assertions.assertEquals("", table.getContentAt(0, 0));
  }

  @Test
  public void testGetContentAtWorksWhenTheRendererIsUnusable() {
    jTable.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
      public Component getTableCellRendererComponent(JTable table, Object value,
                                                     boolean isSelected,
                                                     boolean hasFocus,
                                                     int row, int column) {
        return new JPanel();
      }
    });
    Assertions.assertEquals("a", table.getContentAt(0, 0));
  }

  @Test
  public void testGetContentAtWithConverter() {
    Assertions.assertEquals("-a-", table.getContentAt(0, 0, new DummyTableCellValueConverter()));

    Assertions.assertEquals(3, table.getContentAt(0, 2, ModelTableCellValueConverter.INSTANCE));
  }

  @Test
  public void testGetEditorComponentAt() {
    Component firstCellComponent = table.getSwingEditorComponentAt(0, 0);
    Assertions.assertTrue(firstCellComponent instanceof JTextField);

    Component secondCellComponent = table.getSwingEditorComponentAt(0, 1);
    Assertions.assertTrue(secondCellComponent instanceof JCheckBox);

    Component thirdCellComponent = table.getSwingEditorComponentAt(0, 2);
    Assertions.assertTrue(thirdCellComponent instanceof JComboBox);
  }

  @Test
  public void testToString() {
    Assertions.assertEquals("[[a,\ttrue,\t3]\n" +
                            " [c,\tfalse,\t4]]",
                            table.toString());
  }

  @Test
  public void testCellForegroundAndBackground() {
    jTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (row == 0) {
          component.setForeground(Color.red);
          component.setBackground(Color.pink);
        }
        else {
          component.setForeground(Color.blue);
          component.setBackground(Color.green);
        }
        return component;
      }
    });

    assertTrue(table.foregroundEquals(new String[][]{
      {"black", "red", "black"},
      {"black", "blue", "black"}
    }));

    assertTrue(table.foregroundNear(0, 0, "black"));
    assertTrue(table.foregroundNear(0, 0, "010101"));

    assertTrue(table.backgroundNear(0, 0, "white"));
    assertTrue(table.backgroundNear(0, 0, "FEFEFE"));

    assertTrue(table.foregroundNear(0, 1, "red"));
    assertTrue(table.foregroundNear(0, 1, "DD1111"));

    assertTrue(table.backgroundNear(0, 1, "pink"));
    assertTrue(table.backgroundNear(0, 1, "FFAEAE"));

    assertFalse(table.foregroundNear(0, 1, "blue"));
    assertFalse(table.backgroundNear(0, 1, "black"));
  }

  @Test
  public void testDefaultForeground() {
    table.foregroundEquals("black");
  }

  @Test
  public void testBackgroundEqualsWithDefaultValue() {
    jTable.setBackground(Color.BLUE);
    assertTrue(table.backgroundEquals("blue"));
    try {
      assertTrue(table.backgroundEquals("green"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("expected: <GREEN> but was: <0000FF>", e.getMessage());
    }
  }

  @Test
  public void testBackgroundEquals() {
    jTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (row == 1) {
          component.setBackground(Color.red);
        }
        else {
          component.setBackground(Color.blue);
        }
        return component;
      }
    });
    assertTrue(table.backgroundEquals(new Object[][]{
      {"white", "blue", "white"},
      {Color.white, Color.red, Color.white}
    }));

    try {
      assertTrue(table.backgroundEquals(new Object[][]{
        {"white", "green", "white"},
        {Color.white, Color.red, Color.white}
      }));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Error at (0, 1) - expected: <GREEN> but was: <0000FF>", e.getMessage());
    }
  }

  @Test
  public void testBackgroundEqualsUsesTheSelectionBackgroundColor() {
    jTable.setCellSelectionEnabled(true);
    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (isSelected) {
          component.setBackground(Color.red);
        }
        else {
          component.setBackground(Color.black);
        }
        return component;
      }
    };
    jTable.setDefaultRenderer(Object.class, renderer);
    jTable.setDefaultRenderer(Boolean.class, renderer);
    jTable.setDefaultRenderer(Integer.class, renderer);
    table.selectCell(0, 1);
    table.backgroundEquals(new Object[][]{
      {Color.BLACK, "red", Color.BLACK},
      {Color.BLACK, Color.BLACK, Color.BLACK}
    });
  }

  @Test
  public void testStartsWith() {
    assertTrue(table.startsWith(new Object[][]{
      {"a", Boolean.TRUE, "3"}
    }));
  }

  @Test
  public void testStartsWithFailsBecauseOfWrongData() {
    assertFalse(table.startsWith(new Object[][]{
      {"Wrong", Boolean.TRUE, "3"}
    }));
  }

  @Test
  public void testStartsWithFailsBecauseTheExpectedFirstLineIsNotTheFirst() {
    assertFalse(table.startsWith(new Object[][]{
      {"c", Boolean.FALSE, "4"}
    }));
  }

  @Test
  public void testStartsWithFailsBecauseTheExpectedContentIsTooLarge() {
    assertFalse(table.startsWith(new Object[][]{
      {"a", Boolean.TRUE, "3", "5"}
    }));
  }

  @Test
  public void testStartsWithFailsBecauseTheExpectedContentIsTooLong() throws Exception {
    checkAssertionFails(table.startsWith(new Object[][]{
      {"a", Boolean.TRUE, "3"},
      {"c", Boolean.FALSE, "4"},
      {"d", Boolean.FALSE, "5"}
    }), "Table contains only 2 rows whereas 3 rows are expected. ==> expected: <true> but was: <false>");
  }

  @Test
  public void testEndsWith() {
    assertTrue(table.endsWith(new Object[][]{
      {"c", Boolean.FALSE, "4"}
    }));
  }

  @Test
  public void testEndsWithFailsBecauseOfWrongData() {
    assertFalse(table.endsWith(new Object[][]{
      {"Wrong", Boolean.FALSE, "4"}
    }));
  }

  @Test
  public void testEndsWithFailsBecauseTheExpectedFirstLineIsNotTheFirst() {
    assertFalse(table.endsWith(new Object[][]{
      {"a", Boolean.TRUE, "3"}
    }));
  }

  @Test
  public void testEndsWithFailsBecauseTheExpectedContentIsTooLong() throws Exception {
    checkAssertionFails(table.endsWith(new Object[][]{
      {"d", Boolean.FALSE, "5"},
      {"a", Boolean.TRUE, "3"},
      {"c", Boolean.FALSE, "4"}
    }), "Table contains only 2 rows whereas 3 rows are expected. ==> expected: <true> but was: <false>");
  }

  @Test
  public void testContainsRow() {
    assertTrue(table.containsRow(new Object[]{
      "a", Boolean.TRUE, "3"
    }));
    assertTrue(table.containsRow(new Object[]{
      "c", Boolean.FALSE, "4"
    }));
    assertFalse(table.containsRow(new Object[]{
      "Wrong", Boolean.TRUE, "3"
    }));
  }

  @Test
  public void testContainsRowWithCellContent() throws Exception {
    assertTrue(table.containsRow(0, "a"));
    assertTrue(table.containsRow(1, Boolean.FALSE));
    checkAssertionFails(table.containsRow(0, Boolean.TRUE),
                        "No row found with 'true' in column 0");
  }

  @Test
  public void testRowIndex() {
    Assertions.assertEquals(0, table.getRowIndex(0, "a"));
    Assertions.assertEquals(1, table.getRowIndex(0, "c"));
    Assertions.assertEquals(-1, table.getRowIndex(0, "d"));
  }

  @Test
  public void testRowIndexWithConverter() {
    table.setCellValueConverter(2, new DummyTableCellValueConverter());
    Assertions.assertEquals(0, table.getRowIndex(2, "-3-"));
    Assertions.assertEquals(1, table.getRowIndex(2, "-4-"));
    Assertions.assertEquals(-1, table.getRowIndex(2, "-5-"));
  }

  @Test
  public void testRowIndicesWithoutConverter() {
    int[] indices = table.getRowIndices(0, "a");
    Assertions.assertEquals(1, indices.length);
    Assertions.assertEquals(0, indices[0]);
  }

  @Test
  public void testRowIndicesWithConverter() {
    table.setCellValueConverter(2, (row, column, renderedComponent, modelObject) -> "same");
    int[] indices = table.getRowIndices(2, "same");
    Assertions.assertEquals(2, indices.length);
    Assertions.assertEquals(0, indices[0]);
    Assertions.assertEquals(1, indices[1]);
  }

  @Test
  public void testFindColumnIndex() {
    Assertions.assertEquals(1, table.getColumnIndex("1"));

    try {
      table.getColumnIndex("unknown");
      fail();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Column 'unknown' not found - actual names: [0, 1, 2]", e.getMessage());
    }
  }

  private static class DummyTableCellValueConverter implements TableCellValueConverter {
    public Object getValue(int row, int column, Component renderedComponent, Object modelObject) {
      return "-" + modelObject + "-";
    }
  }
}
