package org.uispec4j.finder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uispec4j.Button;
import org.uispec4j.TextBox;

import javax.swing.*;
import java.awt.*;

public class PanelContainsComponentTest extends PanelComponentFinderTestCase {
  private JButton button;

  private static final ComponentMatcher CUSTOM_MATCHER = new ComponentMatcher() {
    public boolean matches(Component component) {
      String name = component.getName();
      return (name != null) && name.startsWith("custom");
    }
  };

  @BeforeEach
  final protected void setUp() throws Exception {

    button = addComponent(JButton.class, "button1");
  }

  @Test
  public void testContainsComponentByClass() throws Exception {
    assertFalse(panel.containsUIComponent(TextBox.class));
    assertFalse(panel.containsSwingComponent(JTextField.class));

    assertTrue(panel.containsUIComponent(Button.class));
    assertTrue(panel.containsSwingComponent(JButton.class));

    addComponent(JButton.class, "button2");
    assertTrue(panel.containsUIComponent(Button.class));
    assertTrue(panel.containsSwingComponent(JButton.class));
  }

  @Test
  public void testContainsComponentByName() throws Exception {
    assertFalse(panel.containsUIComponent(Button.class, "unknown"));
    assertFalse(panel.containsSwingComponent(JTextField.class, "button"));

    assertTrue(panel.containsUIComponent(Button.class, "button1"));
    assertTrue(panel.containsUIComponent(Button.class, "button"));
    assertTrue(panel.containsSwingComponent(JButton.class, "button1"));
    assertTrue(panel.containsSwingComponent(JButton.class, "button"));

    addComponent(JButton.class, "button2");
    assertTrue(panel.containsUIComponent(Button.class, "button"));
    assertTrue(panel.containsSwingComponent(JButton.class, "button"));
  }

  @Test
  public void testContainsComponentWithCustomComponentMatcher() throws Exception {
    assertFalse(panel.containsComponent(CUSTOM_MATCHER));

    button.setName("custom button");
    assertTrue(panel.containsComponent(CUSTOM_MATCHER));

    Component textField = addComponent(JTextField.class, "text");
    assertTrue(panel.containsComponent(CUSTOM_MATCHER));

    textField.setName("custom text");
    assertTrue(panel.containsComponent(CUSTOM_MATCHER));
  }
}