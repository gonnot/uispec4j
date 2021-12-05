package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uispec4j.utils.UIComponentFactory;
import org.uispec4j.xml.XmlAssert;

import javax.swing.*;

public class TextBoxForLabelTest extends TextBoxComponentTestCase {
  private JLabel jLabel;

  @BeforeEach
  final protected void setUp() throws Exception {

    jLabel = new JLabel("some text");
    jLabel.setName("myLabel");
    textBox = new TextBox(jLabel);
  }

  protected void createTextBox(String text) {
    jLabel = new JLabel(text);
    textBox = new TextBox(jLabel);
  }

  @Test
  public void testGetComponentTypeName() throws Exception {
    Assertions.assertEquals("textBox", UIComponentFactory.createUIComponent(new JLabel()).getDescriptionTypeName());
  }

  @Test
  public void testGetDescription() throws Exception {
    XmlAssert.assertEquivalent("<textBox name='myLabel' text='some text'/>", textBox.getDescription());
  }

  @Test
  public void testFactory() throws Exception {
    checkFactory(new JLabel(), TextBox.class);
  }

  @Test
  public void testAssertTextEquals() throws Exception {
    assertTrue(textBox.textEquals("some text"));
    checkAssertionFails(textBox.textEquals("unknown"),
                        "expected: <unknown> but was: <some text>");
  }

  @Test
  public void testAssertTextEqualsWithHtml() {
    String text = "My name is <b>Bond</b>";
    jLabel.setText(text);
    assertTrue(textBox.textEquals(text));
    assertFalse(textBox.textEquals("My name is <b>Bond</b>, James Bond"));
  }

  @Test
  public void testAssertTextContains() throws Exception {
    jLabel.setText("some text");
    assertTrue(textBox.textContains("some"));
    checkAssertionFails(textBox.textContains("error"),
                        "The component text does not contain 'error' - actual content is: some text ==> expected: <true> but was: <false>");
  }

  @Test
  public void testAssertTextDoesNotContain() throws Exception {
    jLabel.setText("some text");
    assertTrue(textBox.textDoesNotContain("xxx"));
    checkAssertionFails(textBox.textDoesNotContain("some"),
                        "The component text should not contain 'some' - actual content is: some text ==> expected: <true> but was: <false>");
  }

  @Test
  public void testAssertTextIsEditable() {
    assertFalse(textBox.isEditable());
  }

  @Test
  public void testAssertEmpty() throws Exception {
    jLabel.setText("");
    assertTrue(textBox.textIsEmpty());
    jLabel.setText("a");
    checkAssertionFails(textBox.textIsEmpty(),
                        "Text should be empty but contains: a ==> expected: <true> but was: <false>");
  }

  @Test
  public void testSetTextIsNotSupported() throws Exception {
    checkAssertionError(() -> textBox.setText("text"),
                        "The text box is not editable");
    Assertions.assertEquals("some text", textBox.getText());
  }

  @Test
  public void testInsertTextIsNotSupported() throws Exception {
    checkAssertionError(() -> textBox.insertText("text", 0),
                        "The text box is not editable");
    Assertions.assertEquals("some text", textBox.getText());
  }

  @Test
  public void testAppendTextIsNotSupported() throws Exception {
    checkAssertionError(() -> textBox.appendText("text"),
                        "The text box is not editable");
    Assertions.assertEquals("some text", textBox.getText());
  }

  @Test
  public void testClearIsNotSupported() throws Exception {
    checkAssertionError(() -> textBox.clear(),
                        "The text box is not editable");
    Assertions.assertEquals("some text", textBox.getText());
  }

  @Test
  public void testGetText() {
    Assertions.assertEquals("some text", textBox.getText());
    jLabel.setText("new text");
    Assertions.assertEquals("new text", textBox.getText());
  }

  @Test
  public void testClickOnHyperlinkIsNotSupported() throws Exception {
    checkAssertionError(() -> textBox.clickOnHyperlink("text"),
                        "This component does not support hyperlinks.");
    checkAssertionError(() -> textBox.triggerClickOnHyperlink("text").run(),
                        "This component does not support hyperlinks.");
  }

  @Test
  public void testAssertIconEquals() throws Exception {
    ImageIcon icon1 = new ImageIcon();
    jLabel.setIcon(icon1);
    assertTrue(textBox.iconEquals(icon1));
    checkAssertionError(() -> {
      ImageIcon icon2 = new ImageIcon();
      assertTrue(textBox.iconEquals(icon2));
    });
  }
}
