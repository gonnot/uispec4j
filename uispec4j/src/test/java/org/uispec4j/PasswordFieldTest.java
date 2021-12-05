package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

public class PasswordFieldTest extends UIComponentTestCase {

  private PasswordField passwordField;
  private JPasswordField jPasswordField;

  @BeforeEach
  final protected void setUp() throws Exception {
    jPasswordField = new JPasswordField();
    passwordField = new PasswordField(jPasswordField);
  }

  @Test
  public void testGetComponentTypeName() throws Exception {
    Assertions.assertEquals("passwordField", passwordField.getDescriptionTypeName());
  }

  @Test
  public void testGetDescription() throws Exception {
    Assertions.assertEquals("<passwordField/>", passwordField.getDescription());
  }

  @Test
  public void testFactory() throws Exception {
    checkFactory(new JPasswordField(), PasswordField.class);
  }

  protected UIComponent createComponent() {
    return passwordField;
  }

  @Test
  public void testPasswordEquals() throws Exception {
    jPasswordField.setText("pwd");
    assertTrue(passwordField.passwordEquals("pwd"));
    assertFalse(passwordField.passwordEquals("unknown"));
  }

  @Test
  public void testEnterPassword() throws Exception {
    passwordField.setPassword("pwd");
    Assertions.assertEquals("pwd", new String(jPasswordField.getPassword()));
  }
}
