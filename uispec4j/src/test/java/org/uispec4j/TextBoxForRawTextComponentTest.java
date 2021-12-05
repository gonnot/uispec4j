package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uispec4j.utils.AssertionFailureNotDetectedError;
import org.uispec4j.utils.DummyActionListener;
import org.uispec4j.utils.UIComponentFactory;
import org.uispec4j.xml.XmlAssert;

import javax.swing.*;
import javax.swing.text.*;

public class TextBoxForRawTextComponentTest extends TextBoxComponentTestCase {
  private JTextComponent jTextComponent;

  @BeforeEach
  final protected void setUp() {

    init(new JTextArea());
  }

  private void init(JTextComponent swingComponent) {
    jTextComponent = swingComponent;
    jTextComponent.setName("myText");
    createTextBox("");
  }

  protected void createTextBox(String text) {
    textBox = new TextBox(jTextComponent);
    textBox.setText(text);
  }

  @Test
  public void testGetComponentTypeName() {
    Assertions.assertEquals("textBox", UIComponentFactory.createUIComponent(new JTextField()).getDescriptionTypeName());
  }

  @Test
  public void testGetDescription() {
    XmlAssert.assertEquivalent("<textBox name='myText'/>", textBox.getDescription());
  }

  @Test
  public void testFactory() {
    checkFactory(new JTextArea(), TextBox.class);
    checkFactory(new JTextPane(), TextBox.class);
    checkFactory(new JEditorPane(), TextBox.class);
    checkFactory(new JTextField(), TextBox.class);
  }

  @Test
  public void testAssertTextEquals() {
    assertTrue(textBox.textEquals(""));
    jTextComponent.setText("some text");
    assertTrue(textBox.textEquals("some text"));
    assertFalse(textBox.textEquals(""));
    try {
      assertTrue(textBox.textEquals("error"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("expected: <error> but was: <some text>", e.getMessage());
    }
  }

  @Test
  public void testAssertTextContains() {
    jTextComponent.setText("some text");
    assertTrue(textBox.textContains("some"));
    try {
      assertTrue(textBox.textContains("error"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("The component text does not contain 'error' - actual content is:some text ==> expected: <true> but was: <false>",
                              e.getMessage());
    }
  }

  @Test
  public void testAssertTextDoesNotContain() {
    jTextComponent.setText("some text");
    assertTrue(textBox.textDoesNotContain("xxx"));
    try {
      assertTrue(textBox.textDoesNotContain("some"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("The component text should not contain 'some' - actual content is:some text ==> expected: <true> but was: <false>",
                              e.getMessage());
    }
  }

  @Test
  public void testAssertTextIsEditable() {
    jTextComponent.setEditable(true);
    assertTrue(textBox.isEditable());
    jTextComponent.setEditable(false);
    assertFalse(textBox.isEditable());
  }

  @Test
  public void testAssertEmptyWithPlainText() {
    jTextComponent.setText("");
    assertTrue(textBox.textIsEmpty());
    jTextComponent.setText("a");
    try {
      assertTrue(textBox.textIsEmpty());
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Text should be empty but contains: a ==> expected: <true> but was: <false>", e.getMessage());
    }
  }

  @Test
  public void testSetText() {
    textBox.setText("new text");
    Assertions.assertEquals("new text", jTextComponent.getText());
  }

  @Test
  public void testSetTextChecksThatTheComponentIsEditable() {
    textBox.setText("text");
    jTextComponent.setEditable(false);
    try {
      textBox.setText("new text");
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("The text box is not editable ==> expected: <true> but was: <false>", e.getMessage());
    }
    Assertions.assertEquals("text", jTextComponent.getText());
  }

  @Test
  public void testInsertText() {
    jTextComponent.setEditable(true);
    textBox.insertText("text", 0);
    Assertions.assertEquals("text", textBox.getText());
    textBox.insertText("this is some ", 0);
    Assertions.assertEquals("this is some text", textBox.getText());
    textBox.insertText("interesting ", 13);
    Assertions.assertEquals("this is some interesting text", textBox.getText());
    textBox.insertText(", isn't it?", textBox.getText().length());
    Assertions.assertEquals("this is some interesting text, isn't it?", textBox.getText());
  }

  @Test
  public void testInsertTextAtABadPosition() {
    textBox.setText("text");
    try {
      textBox.insertText("a", 10);
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Position should be between 0 and 4", e.getMessage());
    }
  }

  @Test
  public void testAppendAndClear() {
    jTextComponent.setEditable(true);
    textBox.clear();
    Assertions.assertEquals("", textBox.getText());
    textBox.clear();
    Assertions.assertEquals("", textBox.getText());
    textBox.appendText("blah");
    Assertions.assertEquals("blah", textBox.getText());
    textBox.appendText(" ");
    textBox.appendText("yadda");
    Assertions.assertEquals("blah yadda", textBox.getText());
    textBox.clear();
    Assertions.assertEquals("", textBox.getText());
  }

  @Test
  public void testInsertAppendAndClearDoNotNotifyActionListeners() {
    DummyActionListener actionListener = initWithTextFieldAndActionListener();
    textBox.insertText("text", 0);
    textBox.clear();
    textBox.appendText("text");
    Assertions.assertEquals(0, actionListener.getCallCount());
  }

  @Test
  public void testInsertAppendAndClearChecksThatTheComponentIsEditable() {
    textBox.setText("text");
    jTextComponent.setEditable(false);
    try {
      textBox.insertText("new text", 0);
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("The text box is not editable ==> expected: <true> but was: <false>", e.getMessage());
    }
    try {
      textBox.clear();
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("The text box is not editable ==> expected: <true> but was: <false>", e.getMessage());
    }
    try {
      textBox.appendText("new text");
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("The text box is not editable ==> expected: <true> but was: <false>", e.getMessage());
    }
    Assertions.assertEquals("text", jTextComponent.getText());
  }

  @Test
  public void testTextCannotBeEnteredWhenTheComponentIsDisabled() {
    jTextComponent.setEnabled(false);

    String message = "The text component is not enabled - text cannot be entered";

    try {
      textBox.setText("aa");
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals(message, e.getMessage());
    }

    try {
      textBox.insertText("aa", 0);
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals(message, e.getMessage());
    }

    try {
      textBox.appendText("aa");
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals(message, e.getMessage());
    }

    try {
      textBox.clear();
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals(message, e.getMessage());
    }
  }

  @Test
  public void testGetText() {
    jTextComponent.setText("some text");
    Assertions.assertEquals("some text", textBox.getText());
    textBox.setText("new text");
    Assertions.assertEquals("new text", textBox.getText());
    textBox.setText("new <b>text</b>");
    Assertions.assertEquals("new <b>text</b>", textBox.getText());
  }

  @Test
  public void testSetTextNotifiesActionListenersForJTextField() {
    DummyActionListener actionListener = initWithTextFieldAndActionListener();

    textBox.setText("text");
    Assertions.assertEquals(1, actionListener.getCallCount());
  }

  @Test
  public void testClickOnHyperlinkIsNotSupported() throws Exception {
    checkAssertionError(() -> textBox.clickOnHyperlink("text"),
                        "This component does not support hyperlinks.");
    checkAssertionError(() -> textBox.triggerClickOnHyperlink("text").run(),
                        "This component does not support hyperlinks.");
  }

  @Test
  public void testPressingPrintableKeyAddsItToText() {
    JTextField textField = new JTextField();
    TextBox textBox = new TextBox(textField);
    textBox.pressKey(Key.A);
    assertTrue(textBox.textEquals("a"));
    textBox.pressKey(Key.shift(Key.B));
    assertTrue(textBox.textEquals("aB"));
    textBox.pressKey(Key.C);
    assertTrue(textBox.textEquals("aBc"));
  }

  @Test
  public void testPressingPrintableKeyRejectedByTextField() {
    JTextField textField = new JTextField();
    ((AbstractDocument)textField.getDocument()).setDocumentFilter(new DocumentFilter() {
      public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (!text.equals("a")) {
          fb.replace(offset, length, text, attrs);
        }
      }
    });
    TextBox textBox = new TextBox(textField);
    textBox.pressKey(Key.A);
    assertTrue(textBox.textEquals(""));
    textBox.pressKey(Key.B);
    assertTrue(textBox.textEquals("b"));
    textBox.pressKey(Key.A);
    assertTrue(textBox.textEquals("b"));
    textBox.pressKey(Key.shift(Key.A));
    assertTrue(textBox.textEquals("bA"));
  }

  @Test
  public void testPressingPrintableKeyInANonEmptyTextBoxStartsAtPosition0() {
    JTextField textField = new JTextField("text");
    TextBox textBox = new TextBox(textField);
    textBox.pressKey(Key.A);
    assertTrue(textBox.textEquals("atext"));
  }
}
