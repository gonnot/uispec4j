package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uispec4j.assertion.UISpecAssert;
import org.uispec4j.utils.AssertionFailureNotDetectedError;
import org.uispec4j.utils.DummyActionListener;
import org.uispec4j.utils.Functor;
import org.uispec4j.utils.UIComponentFactory;
import org.uispec4j.xml.XmlAssert;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.JTextComponent;

public class TextBoxForTextComponentTest extends TextBoxComponentTestCase {
  private JTextComponent jTextComponent;

  @BeforeEach
  final protected void setUp() throws Exception {

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

  private void initWithHtmlTextPane() {
    JTextPane pane = new JTextPane();
    pane.setContentType("text/html; charset=EUC-JP");
    init(pane);
  }

  @Test
  public void testGetComponentTypeName() throws Exception {
    Assertions.assertEquals("textBox", UIComponentFactory.createUIComponent(new JTextField()).getDescriptionTypeName());
  }

  @Test
  public void testGetDescription() throws Exception {
    XmlAssert.assertEquivalent("<textBox name='myText'/>", textBox.getDescription());
  }

  @Test
  public void testFactory() throws Exception {
    checkFactory(new JTextArea(), TextBox.class);
    checkFactory(new JTextPane(), TextBox.class);
    checkFactory(new JEditorPane(), TextBox.class);
    checkFactory(new JTextField(), TextBox.class);
  }

  @Test
  public void testAssertTextEquals() throws Exception {
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
  public void testAssertTextEqualsWithHtml() throws Exception {
    initWithHtmlTextPane();
    String text = "Universal <b>rules</b>:" +
                  "<ul>" +
                  "<li style=\"margin-top: 0\" align=\"center\">a &lt; b</li>" +
                  "<li>2 &gt; 1</li>" +
                  "</ul>";
    textBox.setText(text);
    assertTrue(textBox.textEquals("Universal rules: a < b 2 > 1"));
    try {
      assertTrue(textBox.textEquals("Universal rules: a < b 2 > 1, seb is the best"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
    }
  }

  @Test
  public void testAssertHtmlEquals() throws Exception {
    initWithHtmlTextPane();
    String text = "Universal <b>rules</b>:" +
                  "<ul>" +
                  "<li>a &lt; b</li>" +
                  "<li>2 &gt; 1</li>" +
                  "</ul>";
    textBox.setText(text);
    assertTrue(textBox.htmlEquals(text));
    try {
      assertTrue(textBox.htmlEquals("Universal <b>rules</b>:" +
                                    "<ul>" +
                                    "<li>a &lt; b</li>" +
                                    "</ul>"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
    }
  }

  @Test
  public void testAssertTextContains() throws Exception {
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
  public void testAssertTextContainsWithHtml() throws Exception {
    initWithHtmlTextPane();
    String text = "My name is <b>Bond</b>";
    textBox.setText(text);
    assertTrue(textBox.textContains("Bond"));
    try {
      assertTrue(textBox.textContains("error"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("The component text does not contain 'error' - actual content is:<html>\n" +
                              "  <head>\n" +
                              "  </head>\n" +
                              "  <body>My name is <b>Bond</b></body>\n" +
                              "</html>\n ==> expected: <true> but was: <false>",
                              e.getMessage());
    }
  }

  @Test
  public void testAssertTextEqualsWithEmptyStringIsTheSameAsAssertTextIsEmpty() throws Exception {
    initWithHtmlTextPane();
    assertTrue(textBox.textEquals(""));
    jTextComponent.setText("blah");
    jTextComponent.setText("");
    assertTrue(textBox.textEquals(""));
  }

  @Test
  public void testAssertTextContainsHandlesHtmlLineBreaksAndFormatting() throws Exception {
    initWithHtmlTextPane();
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < 20; i++) {
      buffer.append("blah ");
    }
    String text = buffer.toString();
    textBox.setText(text);
    assertTrue(textBox.textContains(text));
  }

  @Test
  public void testAssertTextDoesNotContain() throws Exception {
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
  public void testAssertTextIsEditable() throws Exception {
    jTextComponent.setEditable(true);
    assertTrue(textBox.isEditable());
    jTextComponent.setEditable(false);
    assertFalse(textBox.isEditable());
  }

  @Test
  public void testAssertEmptyWithPlainText() throws Exception {
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
  public void testAssertEmptyWithHtml() throws Exception {
    initWithHtmlTextPane();
    assertTrue(textBox.textIsEmpty());
    jTextComponent.setText("");
    assertTrue(textBox.textIsEmpty());
    jTextComponent.setText("a");
    try {
      assertTrue(textBox.textIsEmpty());
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Text should be empty but contains: <html>\n" +
                              "  <head>\n" +
                              "    \n" +
                              "  </head>\n" +
                              "  <body>\n" +
                              "    a\n" +
                              "  </body>\n" +
                              "</html>\n",
                              e.getMessage());
    }

    jTextComponent.setText("<html>\n" +
                           "  <head>\n" +
                           "\n" +
                           "  </head>\n" +
                           "  <body>\n" +
                           "    <p>\n" +
                           "      \n" +
                           "    </p>\n" +
                           "  </body>\n" +
                           "</html>\n" +
                           "\n" +
                           "");
    assertTrue(textBox.textIsEmpty());

    jTextComponent.setText("<html><p></html>");
    jTextComponent.setText("");
    assertTrue(textBox.textIsEmpty());
  }

  @Test
  public void testAssertEmptyAfterReset() throws Exception {
    initWithHtmlTextPane();
    assertTrue(textBox.textIsEmpty());
    jTextComponent.setText("blah");
    jTextComponent.setText("");
    assertTrue(textBox.textIsEmpty());
  }

  @Test
  public void testSetText() throws Exception {
    textBox.setText("new text");
    Assertions.assertEquals("new text", jTextComponent.getText());
  }

  @Test
  public void testSetTextChecksThatTheComponentIsEditable() throws Exception {
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
  public void testInsertText() throws Exception {
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
  public void testInsertTextAtABadPosition() throws Exception {
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
  public void testInsertTextDoesNotNotifyActionListeners() throws Exception {
    DummyActionListener actionListener = initWithTextFieldAndActionListener();
    textBox.insertText("text", 0);
    Assertions.assertEquals(0, actionListener.getCallCount());
  }

  @Test
  public void testSetTextCanNotifyActionListeners() throws Exception {
    DummyActionListener actionListener = initWithTextFieldAndActionListener();
    textBox.setText("text", false);
    Assertions.assertEquals(0, actionListener.getCallCount());
    UISpecAssert.assertTrue(textBox.textEquals("text"));

    textBox.setText("another text", true);
    Assertions.assertEquals(1, actionListener.getCallCount());
    UISpecAssert.assertTrue(textBox.textEquals("another text"));
  }

  @Test
  public void testInsertTextChecksThatTheComponentIsEditable() throws Exception {
    textBox.setText("text");
    jTextComponent.setEditable(false);
    try {
      textBox.insertText("new text", 0);
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("The text box is not editable ==> expected: <true> but was: <false>", e.getMessage());
    }
    Assertions.assertEquals("text", jTextComponent.getText());
  }

  @Test
  public void testGetText() throws Exception {
    jTextComponent.setText("some text");
    Assertions.assertEquals("some text", textBox.getText());
    textBox.setText("new text");
    Assertions.assertEquals("new text", textBox.getText());
    textBox.setText("new <b>text</b>");
    Assertions.assertEquals("new <b>text</b>", textBox.getText());
  }

  @Test
  public void testSetTextNotifiesActionListenersForJTextField() throws Exception {
    DummyActionListener actionListener = initWithTextFieldAndActionListener();
    textBox.setText("text");
    Assertions.assertEquals(1, actionListener.getCallCount());
  }

  @Test
  public void testClickOnHyperlink() throws Exception {
    checkClickOnHyperlink("<html>blah blah<a href=\"http://www.junit.org\">link text</a>reblah</html>",
                          "link text",
                          "http://www.junit.org");
  }

  @Test
  public void testClickOnHyperlinkAcceptsSubstrings() throws Exception {
    checkClickOnHyperlink("<html>blah blah<a href=\"http://www.junit.org\">link text</a>reblah</html>",
                          "link",
                          "http://www.junit.org");
  }

  @Test
  public void testClickOnHyperLinkAcceptsLineSeparators() throws Exception {
    String link = "link text is very long so it will be on two lines";
    checkClickOnHyperlink("<html>blah blah<a href=\"http://www.junit.org\">" + link + "</a>reblah</html>",
                          link,
                          "http://www.junit.org");
  }

  @Test
  public void testClickOnHyperlinkIsCaseInsensitive() throws Exception {
    checkClickOnHyperlink("<html>blah blah<a href=\"http://www.junit.org\">link text</a>reblah</html>",
                          "liNk tEXt",
                          "http://www.junit.org");
  }

  @Test
  public void testClickOnHyperlinkGivesPriorityToExactMatches() throws Exception {
    checkClickOnHyperlink("<html>blah blah<a href=\"http://www.junit.org\">a link text</a>reblah" +
                          "blah blah<a href=\"http://www.apache.org\">link text</a>reblah</html>",
                          "link text",
                          "http://www.apache.org");
  }

  @Test
  public void testClickOnUnknownHyperlink() throws Exception {
    checkClickOnHyperlinkError("<html>blah blah<a href=\"http://www.junit.org\">a link text</a>reblah" +
                               "blah blah<a href=\"http://www.apache.org\">link text</a>reblah</html>",
                               "unknown",
                               "Hyperlink 'unknown' not found");
  }

  @Test
  public void testClickOnHyperlinkWithAmbiguity() throws Exception {
    checkClickOnHyperlinkError("<html>blah blah<a href=\"http://www.junit.org\">a link text</a>reblah" +
                               "blah blah<a href=\"http://www.apache.org\">another link text</a>reblah</html>",
                               "link text",
                               "Ambiguous command - found several hyperlinks matching 'link text'");
  }

  @Test
  public void testClickOnHyperLinkWithABadTextComponentFails() throws Exception {
    final TextBox textBox = new TextBox(new JTextArea());

    checkAssertionError(new Functor() {
      public void run() throws Exception {
        textBox.clickOnHyperlink("toto");
      }
    }, "This component does not support hyperlinks.");

    checkAssertionError(new Functor() {
      public void run() throws Exception {
        textBox.triggerClickOnHyperlink("toto").run();
      }
    }, "This component does not support hyperlinks.");
  }

  private JTextPane createTextPane(String html) {
    JTextPane textPane = new JTextPane();
    textPane.setContentType("text/html; charset=EUC-JP");
    textPane.setText(html);
    return textPane;
  }

  private void checkClickOnHyperlink(String html, String link, String expectedTarget) throws Exception {
    JTextPane textPane = createTextPane(html);
    DummyHyperlinkListener listener = new DummyHyperlinkListener();
    textPane.addHyperlinkListener(listener);
    TextBox textComponent = new TextBox(textPane);
    textComponent.clickOnHyperlink(link);
    Assertions.assertEquals(1, listener.getCallCount());
    Assertions.assertEquals(expectedTarget, listener.getLastEvent().getDescription());

    listener.reset();
    textComponent.triggerClickOnHyperlink(link).run();
    Assertions.assertEquals(1, listener.getCallCount());
    Assertions.assertEquals(expectedTarget, listener.getLastEvent().getDescription());
  }

  private void checkClickOnHyperlinkError(String html, final String link, String errorMessage) throws Exception {
    final TextBox textComponent = new TextBox(createTextPane(html));
    checkAssertionError(new Functor() {
      public void run() throws Exception {
        textComponent.clickOnHyperlink(link);
      }
    }, errorMessage);

    checkAssertionError(new Functor() {
      public void run() throws Exception {
        textComponent.triggerClickOnHyperlink(link).run();
      }
    }, errorMessage);
  }

  private static class DummyHyperlinkListener implements HyperlinkListener {
    int callCount;
    HyperlinkEvent lastEvent;

    public void hyperlinkUpdate(HyperlinkEvent event) {
      callCount++;
      this.lastEvent = event;
    }

    public int getCallCount() {
      return callCount;
    }

    public HyperlinkEvent getLastEvent() {
      return lastEvent;
    }

    public void reset() {
      callCount = 0;
      lastEvent = null;
    }
  }
}
