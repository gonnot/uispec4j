package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uispec4j.utils.AssertionFailureNotDetectedError;

import javax.swing.*;
import java.awt.*;

public class ComboBoxTest extends UIComponentTestCase {
  private ComboBox comboBox;
  private JComboBox<String> jComboBox;

  @BeforeEach
  final protected void setUp() {
    init(new JComboBox<>(new String[]{"one", "two", "three"}));
  }

  private void init(JComboBox<String> box) {
    jComboBox = box;
    jComboBox.setName("marcel");
    comboBox = new ComboBox(jComboBox);
  }

  @Test
  public void testGetComponentTypeName() {
    Assertions.assertEquals("comboBox", comboBox.getDescriptionTypeName());
  }

  @Test
  public void testGetDescription() {
    Assertions.assertEquals("<comboBox name=\"marcel\"/>", comboBox.getDescription());
  }

  @Test
  public void testFactory() {
    checkFactory(new JComboBox<>(), ComboBox.class);
  }

  protected UIComponent createComponent() {
    return comboBox;
  }

  @Test
  public void testCheckContent() {
    assertTrue(comboBox.contentEquals("one", "two", "three"));
  }

  @Test
  public void testCheckContentWithErrors() {
    try {
      assertTrue(comboBox.contentEquals("one", "two", "unknown", "three"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError ignored) {
    }
  }

  @Test
  public void testContains() {
    assertTrue(comboBox.contains("two"));
    assertTrue(comboBox.contains("two", "one"));
  }

  @Test
  public void test() {
    try {
      assertTrue(comboBox.contains("unknown"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Item 'unknown' not found - actual content:[one, two, three]", e.getMessage());
    }

    try {
      assertTrue(comboBox.contains("three", "unknown"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Item 'unknown' not found - actual content:[one, two, three]", e.getMessage());
    }
  }

  @Test
  public void testCheckContentWithSpecificJLabelRenderer() {
    jComboBox.setRenderer(new DummyRenderer());
    assertTrue(comboBox.contentEquals("(one)", "(two)", "(three)"));
  }

  @Test
  public void testCheckContentWithNoJLabelUsesTheModelValue() {
    jComboBox.setRenderer(new DefaultListCellRenderer() {
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return new JComboBox<>();
      }
    });
    assertTrue(comboBox.contentEquals("one", "two", "three"));
  }

  @Test
  public void testUsingACustomCellRenderer() {
    jComboBox.setRenderer(new DefaultListCellRenderer() {
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (index == -1) {
          return new JLabel(value.toString());
        }
        else {
          return new JTextField("level " + value);
        }
      }
    });
    comboBox.setCellValueConverter((index, renderedComponent, modelObject) -> {
      if (index == -1) {
        return ((JLabel)renderedComponent).getText();
      }
      else {
        return ((JTextField)renderedComponent).getText();
      }
    });

    assertTrue(comboBox.contentEquals("level one", "level two", "level three"));
    comboBox.select("level two");
    assertTrue(comboBox.selectionEquals("two"));
    try {
      comboBox.select("unknown");
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError ignored) {
    }
    try {
      assertTrue(comboBox.isEmpty("<no item>"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Unexpected content: [level one,level two,level three]", e.getMessage());
    }
  }

  @Test
  public void testAssertEmptyChecksTheDisplayedValue() {
    jComboBox.setRenderer(new DummyRenderer());
    jComboBox.removeAllItems();
    assertTrue(comboBox.isEmpty("<no item>"));
  }

  @Test
  public void testAssertEmptyFailures() {
    try {
      assertTrue(comboBox.isEmpty("<no item>"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Unexpected content: [one,two,three]", e.getMessage());
    }

    jComboBox.setRenderer(new DummyRenderer());
    jComboBox.removeAllItems();
    try {
      assertTrue(comboBox.isEmpty("error"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("expected: <error> but was: <<no item>>", e.getMessage());
    }
  }

  @Test
  public void testCheckSelectionUsesNullWhenNothingIsSelected() {
    jComboBox.setSelectedIndex(-1);
    assertTrue(comboBox.selectionEquals(null));
  }

  @Test
  public void testCheckSelectionUsesDisplayedNullValueWhenNothingIsSelected() {
    jComboBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> new JLabel("null text"));
    jComboBox.setSelectedIndex(-1);
    assertTrue(comboBox.selectionEquals("null text"));
  }

  @Test
  public void testClickSelectsTheFirstItem() {
    jComboBox.setSelectedIndex(1);
    comboBox.click();
    Assertions.assertEquals(0, jComboBox.getSelectedIndex());
  }

  @Test
  public void testClickDoesNothingIfTheComboIsEmpty() {
    init(new JComboBox<>());
    comboBox.click();
    Assertions.assertEquals(-1, jComboBox.getSelectedIndex());
  }

  @Test
  public void testBasicSelection() {
    comboBox.select("two");
    Assertions.assertEquals(1, jComboBox.getSelectedIndex());
    assertTrue(comboBox.selectionEquals("two"));
    comboBox.select("one");
    Assertions.assertEquals(0, jComboBox.getSelectedIndex());
    assertTrue(comboBox.selectionEquals("one"));
  }

  @Test
  public void testBasicSelectionWithCustomModel() {
    jComboBox.setModel(new VerySimpleComboBoxModel("one", "two", "three"));
    comboBox.select("two");
    Assertions.assertEquals(1, jComboBox.getSelectedIndex());
    assertTrue(comboBox.selectionEquals("two"));
    comboBox.select("one");
    Assertions.assertEquals(0, jComboBox.getSelectedIndex());
    assertTrue(comboBox.selectionEquals("one"));
  }

  @Test
  public void testSelectionIsNotCaseSensitive() {
    comboBox.select("TwO");
    Assertions.assertEquals(1, jComboBox.getSelectedIndex());
    assertTrue(comboBox.selectionEquals("two"));
    comboBox.select("oNe");
    Assertions.assertEquals(0, jComboBox.getSelectedIndex());
    assertTrue(comboBox.selectionEquals("one"));
  }

  @Test
  public void testSelectionWithSubstring() {
    comboBox.select("tw");
    Assertions.assertEquals(1, jComboBox.getSelectedIndex());
    assertTrue(comboBox.selectionEquals("two"));
    comboBox.select("ne");
    Assertions.assertEquals(0, jComboBox.getSelectedIndex());
    assertTrue(comboBox.selectionEquals("one"));
  }

  @Test
  public void testAmbiguityInSelection() {
    try {
      comboBox.select("o");
      throw new AssertionFailureNotDetectedError();
    }
    catch (ItemAmbiguityException e) {
      Assertions.assertEquals("2 items are matching the same pattern 'o': [one,two]", e.getMessage());
    }
  }

  @Test
  public void testSelectingAnUnknownValueThrowsAnException() {
    try {
      comboBox.select("unknown");
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError ignored) {
    }
  }

  @Test
  public void testCheckSelectedError() {
    try {
      assertTrue(comboBox.selectionEquals("unknown"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError ignored) {
    }
  }

  @Test
  public void testAssertEditable() throws Exception {
    jComboBox.setEditable(false);
    assertFalse(comboBox.isEditable());
    checkAssertionError(() -> assertTrue(comboBox.isEditable()));
    jComboBox.setEditable(true);
    assertTrue(comboBox.isEditable());
    checkAssertionError(() -> assertFalse(comboBox.isEditable()));
  }

  @Test
  public void testSetTextIsAvailableOnlyWhenComponentIsEditable() {
    comboBox.select("two");
    assertTrue(comboBox.selectionEquals("two"));
    try {
      comboBox.setText("notInList");
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("The combo box is not editable", e.getMessage());
    }
    assertTrue(comboBox.selectionEquals("two"));

    jComboBox.setEditable(true);
    comboBox.setText("notInList");
    Assertions.assertEquals("notInList", jComboBox.getSelectedItem());
    assertTrue(comboBox.selectionEquals("notInList"));
    assertTrue(comboBox.contentEquals("one", "two", "three"));
    comboBox.select("one");
    assertTrue(comboBox.selectionEquals("one"));
  }

  @Test
  public void testCheckSelectionUsesTheProperRendererIndex() {
    jComboBox.setRenderer(new DefaultListCellRenderer() {
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (index < 0) {
          super.setText("selected");
        }
        return this;
      }
    });
    assertTrue(comboBox.contentEquals("one", "two", "three"));
    comboBox.select("one");
    assertTrue(comboBox.selectionEquals("selected"));
    comboBox.select("two");
    assertTrue(comboBox.selectionEquals("selected"));
  }

  @Test
  public void testExceptionThrownByTheModel() throws Exception {
    jComboBox.setModel(new DefaultComboBoxModel<>() {
      public int getSize() {
        return 1;
      }

      public String getElementAt(int index) {
        throw new NullPointerException("boum");
      }
    });

    checkAssertionError(() -> assertTrue(comboBox.contentEquals(new String[1])),
                        "boum");
  }

  private static class DummyRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      String renderedValue = (index == -1) ? "<no item>" : "(" + value + ")";
      return super.getListCellRendererComponent(list, renderedValue, index, isSelected, cellHasFocus);
    }
  }

  private static class VerySimpleComboBoxModel extends AbstractListModel<String> implements ComboBoxModel<String> {
    private final String[] content;
    private Object selectedObject = null;

    public VerySimpleComboBoxModel(String... content) {
      this.content = content;
    }

    public int getSize() {
      return content.length;
    }

    public String getElementAt(int index) {
      return content[index];
    }

    public Object getSelectedItem() {
      return selectedObject;
    }

    public void setSelectedItem(Object anItem) {
      selectedObject = anItem;
    }
  }
}
