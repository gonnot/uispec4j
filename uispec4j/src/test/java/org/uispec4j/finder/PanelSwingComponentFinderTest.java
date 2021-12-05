package org.uispec4j.finder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uispec4j.ComponentAmbiguityException;
import org.uispec4j.ItemNotFoundException;
import org.uispec4j.TestUtils;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertSame;

public class PanelSwingComponentFinderTest extends PanelComponentFinderTestCase {
  private JButton button1;
  private JButton button2;

  @BeforeEach
  final protected void setUp() throws Exception {

    button1 = addComponent(JButton.class, "button1");
    button2 = addComponent(JButton.class, "button2");
    button2.setEnabled(false);
  }

  @Test
  public void testGetSwingComponentWithCustomComponentMatcher() throws Exception {
    assertSame(button1, panel.findSwingComponent(new ComponentMatcher() {
      public boolean matches(Component component) {
        return component.isEnabled();
      }
    }));

    try {
      panel.findSwingComponent(new ComponentMatcher() {
        public boolean matches(Component component) {
          return component instanceof JTextField;
        }
      });
    }
    catch (ItemNotFoundException e) {
      Assertions.assertEquals("No component found", e.getMessage());
    }

    try {
      panel.findSwingComponent(new ComponentMatcher() {
        public boolean matches(Component component) {
          return component instanceof JButton;
        }
      });
    }
    catch (ComponentAmbiguityException e) {
      Assertions.assertEquals(Messages.computeAmbiguityMessage(new Component[]{button1, button2},
                                                               null, null),
                              e.getMessage());
    }
  }

  @Test
  public void testFindComponentsWithCustomComponentMatcher() throws Exception {
    TestUtils.assertSwingComponentsEquals(new JButton[]{button1},
                                          panel.getSwingComponents(new ComponentMatcher() {
                                            public boolean matches(Component component) {
                                              return component.isEnabled();
                                            }
                                          }));

    TestUtils.assertSwingComponentsEquals(new Component[0],
                                          panel.getSwingComponents(new ComponentMatcher() {
                                            public boolean matches(Component component) {
                                              return component instanceof JTextField;
                                            }
                                          }));

    TestUtils.assertSwingComponentsEquals(new JButton[]{button1, button2},
                                          panel.getSwingComponents(new ComponentMatcher() {
                                            public boolean matches(Component component) {
                                              return component instanceof JButton;
                                            }
                                          }));
  }
}