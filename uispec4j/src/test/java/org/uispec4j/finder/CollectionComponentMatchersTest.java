package org.uispec4j.finder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uispec4j.TestUtils;

import javax.swing.*;
import java.awt.*;

import static org.uispec4j.finder.ComponentMatchers.*;

public class CollectionComponentMatchersTest extends PanelComponentFinderTestCase {
  private JButton component1;
  private JButton component2;
  private JButton component3;
  private JTextField component4;

  @BeforeEach
  final protected void setUp() throws Exception {

    component1 = addComponent(JButton.class, "some text");
    component2 = addComponent(JButton.class, "other text");
    component3 = addComponent(JButton.class, "nothing");
    component4 = addComponent(JTextField.class, "last text");
  }

  @Test
  public void testIntersectionMatcher() throws Exception {
    ComponentMatcher matcher = and(displayedNameSubstring("text"), fromClass(JButton.class));

    TestUtils.assertSwingComponentsEquals(new JButton[]{component1, component2},
                                          panel.getSwingComponents(matcher));
  }

  @Test
  public void testUnionComponentMatcher() throws Exception {
    ComponentMatcher matcher = or(displayedNameSubstring("text"), fromClass(JButton.class));

    TestUtils.assertSwingComponentsEquals(new Component[]{component1, component2, component3, component4},
                                          panel.getSwingComponents(matcher));
  }

  @Test
  public void testNegatedComponentMatcher() throws Exception {
    ComponentMatcher matcher = ComponentMatchers.not(displayedNameSubstring("text"));
    TestUtils.assertSwingComponentsEquals(new Component[]{component3},
                                          panel.getSwingComponents(matcher));
  }
}
