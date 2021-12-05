package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uispec4j.utils.ArrayUtils;
import org.uispec4j.utils.AssertionFailureNotDetectedError;
import org.uispec4j.utils.Functor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.regex.Pattern;

public class TableHeaderTest extends TableTestCase {
  @Test
  public void test() throws Exception {
    JTableHeader tableHeader = jTable.getTableHeader();
    tableHeader.setVisible(false);
    jTable.setTableHeader(null);
    Assertions.assertEquals(1, table.getHeader().findColumnIndex("1"));
  }

  @Test
  public void testContent() {
    assertTrue(table.getHeader().contentEquals("0", "1", "2"));
    try {
      assertTrue(table.getHeader().contentEquals());
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Pattern pattern = Pattern.compile("expected: <.*> but was: <.*0,1,2.*>");
      if (!pattern.matcher(e.getMessage()).matches()) {
        Assertions.fail("Unexpected message: " + e.getMessage());
      }
    }
  }

  @Test
  public void testPartialContent() {
    assertTrue(table.getHeader().contentEquals(2, "0", "1"));
    try {
      assertTrue(table.getHeader().contentEquals(2, "0", "2"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError ignored) {
    }
  }

  @Test
  public void testClickOnHeader() {
    MouseLogger mouseLogger = new MouseLogger(jTable.getTableHeader());
    table.getHeader().click(0);
    mouseLogger.assertEquals("<log>" +
                             "  <mousePressed button='1'/>" +
                             "  <mouseReleased button='1'/>" +
                             "  <mouseClicked button='1'/>" +
                             "</log>");
  }

  @Test
  public void testRightClickOnHeader() {
    MouseLogger dummyHeaderListener = new MouseLogger(jTable.getTableHeader());
    table.getHeader().rightClick(0);
    dummyHeaderListener.assertEquals("<log>" +
                                     "  <mousePressed button='3'/>" +
                                     "  <mouseReleased button='3'/>" +
                                     "  <mouseClicked button='3'/>" +
                                     "</log>");
  }

  @Test
  public void testHeader() {
    assertTrue(table.hasHeader());

    jTable.setTableHeader(null);
    assertFalse(table.hasHeader());
    try {
      assertTrue(table.hasHeader());
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("The table contains an header", e.getMessage());
    }
  }

  @Test
  public void testNoHeaderExceptions() throws Exception {
    jTable.setTableHeader(null);
    checkNoHeaderException(() -> assertTrue(table.getHeader().backgroundEquals(null)));
    checkNoHeaderException(() -> assertTrue(table.getHeader().contentEquals()));
    checkNoHeaderException(() -> table.getHeader().click(0));
    checkNoHeaderException(() -> table.getHeader().click(""));
    checkNoHeaderException(() -> table.getHeader().triggerClick(0).run());
    checkNoHeaderException(() -> table.getHeader().triggerClick("").run());
    checkNoHeaderException(() -> table.getHeader().getDefaultBackground());
    checkNoHeaderException(() -> table.getHeader().rightClick(0));
    checkNoHeaderException(() -> table.getHeader().rightClick(""));
    checkNoHeaderException(() -> table.getHeader().triggerRightClick(0).run());
    checkNoHeaderException(() -> table.getHeader().triggerRightClick("").run());
  }

  @Test
  public void testAssertHeaderBackgroundEquals() {
    jTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (column == 1) {
          component.setBackground(Color.red);
        }
        else {
          component.setBackground(Color.blue);
        }
        return component;
      }
    });

    assertTrue(table.getHeader().backgroundEquals(new Object[]{"blue", "red", "blue"}));

    try {
      assertTrue(table.getHeader().backgroundEquals(new Object[]{"blue", "black", "blue"}));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Unexpected color at column 1 - expected: <BLACK> but was: <FF0000>", e.getMessage());
    }
  }

  @Test
  public void testGetColumnNames() {
    ArrayUtils.assertEquals(new String[0], new Table(new JTable()).getHeader().getColumnNames());
    ArrayUtils.assertEquals(new String[]{"0", "1", "2"}, table.getHeader().getColumnNames());
  }

  @Test
  public void testFindColumnIndex() {
    Assertions.assertEquals(0, table.getHeader().findColumnIndex("0"));
    Assertions.assertEquals(1, table.getHeader().findColumnIndex("1"));
    Assertions.assertEquals(2, table.getHeader().findColumnIndex("2"));

    try {
      table.getHeader().findColumnIndex("unknown");
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Column 'unknown' not found - actual names: [0, 1, 2]", e.getMessage());
    }
  }

  private void checkNoHeaderException(Functor functor) throws Exception {
    try {
      functor.run();
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("The table contains no header ==> expected: not <null>", e.getMessage());
    }
  }
}
