package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uispec4j.utils.UIComponentFactory;
import org.uispec4j.utils.UnitTestCase;

import javax.swing.*;
import java.awt.*;

public abstract class UIComponentTestCase extends UnitTestCase {

  @Test
  public abstract void testGetComponentTypeName() throws Exception;

  @Test
  public abstract void testGetDescription() throws Exception;

  @Test
  public abstract void testFactory() throws Exception;

  @Test
  public void testGetName() throws Exception {
    UIComponent component = createComponent();
    component.getAwtComponent().setName(null);
    Assertions.assertEquals(null, component.getName());
    component.getAwtComponent().setName("name");
    Assertions.assertEquals("name", component.getName());
  }

  @Test
  public void testPressingAndReleasingNonPrintableKey() throws Exception {
    UIComponent component = createComponent();
    Component awtComponent = component.getAwtComponent();
    DummyKeyListener keyListener = new DummyKeyListener();
    awtComponent.addKeyListener(keyListener);
    component.pressKey(Key.RIGHT);
    keyListener.checkEvents("keyPressed");
    component.releaseKey(Key.RIGHT);
    keyListener.checkEvents("keyReleased");
    component.typeKey(Key.RIGHT);
    keyListener.checkEvents("keyPressed", "keyReleased");
  }

  @Test
  public void testPressingAndReleasingPrintableKey() throws Exception {
    UIComponent component = createComponent();
    Component awtComponent = component.getAwtComponent();
    DummyKeyListener keyListener = new DummyKeyListener();
    awtComponent.addKeyListener(keyListener);
    component.pressKey(Key.A);
    keyListener.checkEvents("keyPressed", "keyTyped");
    component.releaseKey(Key.A);
    keyListener.checkEvents("keyReleased");
    component.typeKey(Key.A);
    keyListener.checkEvents("keyPressed", "keyTyped", "keyReleased");
  }

  protected abstract UIComponent createComponent();

  public static void checkFactory(JComponent jComponent, Class expectedGuiComponent) {
    UIComponent uiComponent = UIComponentFactory.createUIComponent(jComponent);
    Assertions.assertNotNull(uiComponent);
    Assertions.assertTrue(expectedGuiComponent.isInstance(uiComponent));
  }
}
