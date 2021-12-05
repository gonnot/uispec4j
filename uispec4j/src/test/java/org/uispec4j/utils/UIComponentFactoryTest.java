package org.uispec4j.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uispec4j.AbstractButton;
import org.uispec4j.Key;
import org.uispec4j.Panel;
import org.uispec4j.UIComponent;
import org.uispec4j.assertion.Assertion;
import org.uispec4j.extension.JCountingButton;

import java.awt.*;

public class UIComponentFactoryTest extends UnitTestCase {

  @Test
  public void testInitWithDummyComponent() throws Exception {
    UIComponentFactory.register(ComponentWithSwingClassesField.class);
    UIComponent uiComponent = UIComponentFactory.createUIComponent(new JCountingButton(""));
    Assertions.assertEquals(ComponentWithSwingClassesField.class, uiComponent.getClass());
  }

  @Test
  public void testInitErrorForClassWithoutSwingClassesField() throws Exception {
    checkInitError(ComponentWithoutSwingClassesField.class,
                   "Class " + ComponentWithoutSwingClassesField.class +
                   " should have a field 'static Class[] SWING_CLASSES'");
  }

  @Test
  public void testInitErrorForClassWithUninitialisedSwingClassesField() throws Exception {
    checkInitError(ComponentWithUninitializedSwingClassesField.class,
                   "Field 'static Class[] SWING_CLASSES' in class " +
                   ComponentWithUninitializedSwingClassesField.class +
                   " should be initialized");
  }

  @Test
  public void testNonSwingClassInSwingClassesField() throws Exception {
    checkInitError(ComponentWithInvalidSwingClass.class,
                   "Class '" + String.class + "' in field 'SWING_CLASSES' of class '" +
                   ComponentWithInvalidSwingClass.class +
                   "' should extend '" + Container.class + "'");
  }

  @Test
  public void testInitErrorForClassWithoutTypeName() throws Exception {
    checkInitError(ComponentWithoutTypeName.class,
                   "Class " + ComponentWithoutTypeName.class +
                   " should have a field 'public static String TYPE_NAME'");
  }

  @Test
  public void testInitErrorForClassWithUninitialisedTypeName() throws Exception {
    checkInitError(ComponentWithUninitializedTypeNameField.class,
                   "Field 'static String TYPE_NAME' in class " +
                   ComponentWithUninitializedTypeNameField.class +
                   " should be initialized");
  }

  @Test
  public void testInitErrorForClassWithPrivateTypeName() throws Exception {
    checkInitError(ComponentWithPrivateTypeNameField.class,
                   "Field 'static String TYPE_NAME' in class " +
                   ComponentWithPrivateTypeNameField.class +
                   " should be public");
  }

  @Test
  public void testInitErrorForClassWithUnexpectedTypeNameClass() throws Exception {
    checkInitError(ComponentWithoutUnexpectedTypeNameClass.class,
                   "Static field 'TYPE_NAME' in class " +
                   ComponentWithoutUnexpectedTypeNameClass.class +
                   " should be of type String");
  }

  @Test
  public void testComponentClassesMustImplementUIComponent() throws Exception {
    checkInitError(String.class,
                   "Class '" + String.class + "' should implement " + UIComponent.class);
  }

  private void checkInitError(Class uiClass, String message) {
    try {
      UIComponentFactory.register(uiClass);
      Assertions.fail();
    }
    catch (RuntimeException e) {
      Assertions.assertEquals(message, e.getMessage());
    }
  }

  private static class ComponentWithSwingClassesField extends DummyUIComponent {
    public static Class[] SWING_CLASSES = {JCountingButton.class};
    public static String TYPE_NAME = "Type";

    public ComponentWithSwingClassesField(JCountingButton button) {
    }
  }

  private static class ComponentWithoutSwingClassesField extends DummyUIComponent {
    public static String TYPE_NAME = "Type";
  }

  private static class ComponentWithUninitializedSwingClassesField extends DummyUIComponent {
    static Class[] SWING_CLASSES;
    public static String TYPE_NAME = "Type";
  }

  private static class ComponentWithInvalidSwingClass extends DummyUIComponent {
    static Class[] SWING_CLASSES = {String.class};
    public static String TYPE_NAME = "Type";
  }

  private static class ComponentWithoutTypeName extends DummyUIComponent {
    public static Class[] SWING_CLASSES = {AbstractButton.class};
  }

  private static class ComponentWithUninitializedTypeNameField extends DummyUIComponent {
    public static Class[] SWING_CLASSES = {AbstractButton.class};
    public static String TYPE_NAME;
  }

  private static class ComponentWithPrivateTypeNameField extends DummyUIComponent {
    public static Class[] SWING_CLASSES = {AbstractButton.class};
    private static final String TYPE_NAME = "Type";
  }

  private static class ComponentWithoutUnexpectedTypeNameClass extends DummyUIComponent {
    public static Class[] SWING_CLASSES = {AbstractButton.class};
    public static Object TYPE_NAME = new Integer(3);
  }

  private static class DummyUIComponent implements UIComponent {

    public Component getAwtComponent() {
      return null;
    }

    public String getDescription() {
      return null;
    }

    public String getDescriptionTypeName() {
      return null;
    }

    public Assertion isEnabled() {
      return new Assertion() {
        public void check() {
        }
      };
    }

    public Assertion isVisible() {
      return new Assertion() {
        public void check() {
        }
      };
    }

    public String getName() {
      return null;
    }

    public Panel getContainer(String parentName) {
      return null;
    }

    public String getLabel() {
      return null;
    }

    public DummyUIComponent typeKey(Key key) {
      return this;
    }

    public DummyUIComponent pressKey(Key key) {
      return this;
    }

    public DummyUIComponent releaseKey(Key key) {
      return this;
    }
  }
}
