package org.uispec4j.finder;

import org.junit.jupiter.api.BeforeEach;
import org.uispec4j.Panel;
import org.uispec4j.utils.UnitTestCase;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public abstract class PanelComponentFinderTestCase extends UnitTestCase {
  protected JPanel jPanel;
  protected Panel panel;
  protected List components = new ArrayList();

  @BeforeEach
  final protected void finderSetUp() {
    jPanel = new JPanel();
    jPanel.setName("myPanel");
    panel = new Panel(jPanel);
  }

  protected <T extends Component> T addComponent(Class<T> awtComponentclass, String name) throws Exception {
    T component = createComponent(awtComponentclass, name);
    components.add(component);
    jPanel.add(component);
    return component;
  }

  private <T extends Component> T createComponent(Class<T> awtComponentclass, String name) throws Exception {
    Component component;
    try {
      Constructor<T> constructor = awtComponentclass.getConstructor(String.class);
      component = constructor.newInstance(name);
    }
    catch (NoSuchMethodException e) {
      component = awtComponentclass.getDeclaredConstructor().newInstance();
    }
    component.setName(name);
    return (T)component;
  }
}
