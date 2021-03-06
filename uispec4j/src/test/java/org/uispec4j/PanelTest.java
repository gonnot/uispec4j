package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uispec4j.finder.ComponentMatcher;
import org.uispec4j.utils.UIComponentFactory;
import org.uispec4j.xml.XmlAssert;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.uispec4j.DummySpinner.*;

public class PanelTest extends UIComponentTestCase {

  @Test
  public void testGetComponentTypeName() throws Exception {
    Assertions.assertEquals("panel", UIComponentFactory.createUIComponent(new JPanel()).getDescriptionTypeName());
  }

  @Test
  public void testGetDescription() throws Exception {
    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.setName("myTabbedPane");

    JPanel jPanel = new JPanel();
    jPanel.setName("myPanel");
    jPanel.add(tabbedPane);

    JTextField textField = new JTextField();
    textField.setName("myText");
    tabbedPane.addTab("1", textField);
    tabbedPane.addTab("2", new JButton("myButton"));

    Panel panel = new Panel(jPanel);
    XmlAssert.assertEquivalent("<panel name='myPanel'>" +
                               "  <tabGroup name='myTabbedPane'>" +
                               "     <textBox name='myText'/>" +
                               "  </tabGroup>" +
                               "</panel>", panel.getDescription());
  }

  @Test
  public void testFactory() throws Exception {
    checkFactory(new JPanel(), Panel.class);
  }

  protected UIComponent createComponent() {
    return new Panel(new JPanel());
  }

  @Test
  public void testContainsLabel() throws Exception {
    JPanel jPanel = new JPanel();
    jPanel.add(new JLabel("Some text"));
    Panel panel = new Panel(jPanel);
    assertTrue(panel.containsLabel("Some text"));
    assertTrue(panel.containsLabel("text"));
    assertFalse(panel.containsLabel("unknown"));
  }

  @Test
  public void testGetSpinnerThroughModel() throws Exception {
    checkGetSpinnerByModel(dateModel(), new Getter() {
      public UIComponent get(Panel panel) {
        return panel.getDateSpinner();
      }
    });
    checkGetSpinnerByModel(listModel(), new Getter() {
      public UIComponent get(Panel panel) {
        return panel.getListSpinner();
      }
    });
    checkGetSpinnerByModel(numberModel(), new Getter() {
      public UIComponent get(Panel panel) {
        return panel.getNumberSpinner();
      }
    });
  }

  @Test
  public void testGetSpinnerThroughModelAndComponentName() throws Exception {
    checkGetSpinnerByModel(dateModel(), new Getter() {
      public UIComponent get(Panel panel) {
        return panel.getDateSpinner("spinner1");
      }
    });
    checkGetSpinnerByModel(listModel(), new Getter() {
      public UIComponent get(Panel panel) {
        return panel.getListSpinner("spinner1");
      }
    });
    checkGetSpinnerByModel(numberModel(), new Getter() {
      public UIComponent get(Panel panel) {
        return panel.getNumberSpinner("spinner1");
      }
    });
  }

  @Test
  public void testGetSpinnerThroughModelAndMatcher() throws Exception {
    checkGetSpinnerByModel(dateModel(), new Getter() {
      public UIComponent get(Panel panel) {
        return panel.getDateSpinner(ComponentMatcher.ALL);
      }
    });
    checkGetSpinnerByModel(listModel(), new Getter() {
      public UIComponent get(Panel panel) {
        return panel.getListSpinner(ComponentMatcher.ALL);
      }
    });
    checkGetSpinnerByModel(numberModel(), new Getter() {
      public UIComponent get(Panel panel) {
        return panel.getNumberSpinner(ComponentMatcher.ALL);
      }
    });
  }

  private void checkGetSpinnerByModel(SpinnerModel model, Getter getter) {
    JPanel jPanel = new JPanel();
    jPanel.add(new JSpinner(new MySpinnerModel()));

    Panel panel = new Panel(jPanel);
    try {
      getter.get(panel);
      Assertions.fail();
    }
    catch (ItemNotFoundException e) {
      Assertions.assertEquals("No component found", e.getMessage());
    }

    JSpinner jSpinner = new JSpinner(model);
    jSpinner.setName("spinner1");
    jPanel.add(jSpinner);
    assertSame(jSpinner, getter.get(panel).getAwtComponent());
  }

  private interface Getter {
    UIComponent get(Panel panel);
  }

  private static class MySpinnerModel extends AbstractSpinnerModel {
    public Object getNextValue() {
      return null;
    }

    public Object getPreviousValue() {
      return null;
    }

    public Object getValue() {
      return null;
    }

    public void setValue(Object value) {
    }
  }
}
