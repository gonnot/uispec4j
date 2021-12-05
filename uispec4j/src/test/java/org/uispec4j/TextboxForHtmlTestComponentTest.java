package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uispec4j.utils.AssertionFailureNotDetectedError;
import org.uispec4j.utils.FileTestUtils;
import org.uispec4j.utils.UIComponentFactory;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.JTextComponent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class TextboxForHtmlTestComponentTest extends TextBoxComponentTestCase {
  private JTextComponent jTextComponent;

  @BeforeEach
  final protected void setUp() {

    initWithHtmlTextPane();
  }

  protected void createTextBox(String text) {
    textBox = new TextBox(jTextComponent);
    textBox.setText(text);
  }

  @Test
  public void testGetComponentTypeName() {
    Assertions.assertEquals("textBox", UIComponentFactory.createUIComponent(new JTextPane()).getDescriptionTypeName());
  }

  @Test
  public void testGetDescription() {
    Assertions.assertTrue(textBox.getDescription().startsWith("<textBox name='myText' text='&lt;html".replaceAll("'", "\"")));
  }

  @Test
  public void testFactory() {
    checkFactory(new JTextArea(), TextBox.class);
    checkFactory(new JTextPane(), TextBox.class);
    checkFactory(new JEditorPane(), TextBox.class);
    checkFactory(new JTextField(), TextBox.class);
  }

  @Test
  public void testAssertTextEqualsWithHtml() {
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
    catch (AssertionError ignored) {
    }
  }

  @Test
  public void testAssertHtmlEqualsWithMetaInHeader() {
    initWithHtmlTextPane();
    String text = "<html>" +
                  "  <head>" +
                  "    <meta name=\"UISpec4J\" land=\"en\"/>" +
                  "    <title name=\"Universal rules\"/>" +
                  "  </head>" +
                  "  <body>" +
                  "    Universal <b>rules</b>:" +
                  "    <ul>" +
                  "      <li>a &lt; b</li>" +
                  "      <li>2 &gt; 1</li>" +
                  "    </ul>" +
                  "  </body>" +
                  "</html>";
    textBox.setText(text);
    assertTrue(textBox.htmlEquals(text));
    try {
      assertTrue(textBox.htmlEquals("Universal <b>rules</b>:" +
                                    "<ul>" +
                                    "<li>a &lt; b</li>" +
                                    "</ul>"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError ignored) {
    }
  }

  @Test
  public void testAssertHtmlEquals() {
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
    catch (AssertionError ignored) {
    }
  }

  @Test
  public void testAssertTextContainsWithHtml() {
    initWithHtmlTextPane();
    String text = "My name is <b>Bond</b>";
    textBox.setText(text);
    assertTrue(textBox.textContains("Bond"));
    try {
      assertTrue(textBox.textContains("error"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("""
                              The component text does not contain 'error' - actual content is:<html>
                                <head>
                                </head>
                                <body>My name is <b>Bond</b></body>
                              </html>
                               ==> expected: <true> but was: <false>""",
                              e.getMessage());
    }
  }

  @Test
  public void testAssertTextEqualsWithEmptyStringIsTheSameAsAssertTextIsEmpty() {
    initWithHtmlTextPane();
    assertTrue(textBox.textEquals(""));
    jTextComponent.setText("blah");
    jTextComponent.setText("");
    assertTrue(textBox.textEquals(""));
  }

  @Test
  public void testAssertTextContainsHandlesHtmlLineBreaksAndFormatting() {
    initWithHtmlTextPane();
    String text = "blah ".repeat(20);
    textBox.setText(text);
    assertTrue(textBox.textContains(text));
  }

  @Test
  public void testAssertEmptyWithHtml() {
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
      Assertions.assertEquals("""
                              Text should be empty but contains: <html>
                                <head>
                                 \s
                                </head>
                                <body>
                                  a
                                </body>
                              </html>
                              """,
                              e.getMessage());
    }

    jTextComponent.setText("""
                           <html>
                             <head>

                             </head>
                             <body>
                               <p>
                                \s
                               </p>
                             </body>
                           </html>

                           """);
    assertTrue(textBox.textIsEmpty());

    jTextComponent.setText("<html><p></html>");
    jTextComponent.setText("");
    assertTrue(textBox.textIsEmpty());
  }

  @Test
  public void testAssertEmptyAfterReset() {
    initWithHtmlTextPane();
    assertTrue(textBox.textIsEmpty());
    jTextComponent.setText("blah");
    jTextComponent.setText("");
    assertTrue(textBox.textIsEmpty());
  }

  private JTextPane createHtmlTextPane(String html) {
    JTextPane textPane = new JTextPane();
    textPane.setContentType("text/html");
    textPane.setText(html);
    return textPane;
  }

  @Test
  public void testClickOnHyperlink() throws Exception {
    checkClickOnHyperlink("<html>blah blah<a href=\"https://www.junit.org\">link text</a>reblah</html>",
                          "link text",
                          "https://www.junit.org");
  }

  @Test
  public void testClickOnHyperlinkAcceptsSubstrings() throws Exception {
    checkClickOnHyperlink("<html>blah blah<a href=\"https://www.junit.org\">link text</a>reblah</html>",
                          "link",
                          "https://www.junit.org");
  }

  @Test
  public void testClickOnHyperLinkAcceptsLineSeparators() throws Exception {
    String link = "link text is very long so it will be on two lines";
    checkClickOnHyperlink("<html>blah blah<a href=\"https://www.junit.org\">" + link + "</a>reblah</html>",
                          link,
                          "https://www.junit.org");
  }

  @Test
  public void testClickOnHyperlinkIsCaseInsensitive() throws Exception {
    checkClickOnHyperlink("<html>blah blah<a href=\"https://www.junit.org\">link text</a>reblah</html>",
                          "liNk tEXt",
                          "https://www.junit.org");
  }

  @Test
  public void testClickOnHyperLinkWaitsForTheCompletePageLoad() throws Exception {
    String content1 = """
                      <html>
                        <head>
                          <title>Subject 1</title>
                        </head>
                        <body>
                          <p>blabla</p>    <a href="file2.html">Subject 2</a>
                        </body>
                      </html>""";
    String content2 = """
                      <html>
                        <head>
                          <title>Subject 2</title>
                        </head>
                        <body>
                          <p>blabla</p>    <a href="file1.html">Subject 1</a>
                        </body>
                      </html>""";

    File file1 = FileTestUtils.dumpStringToFile("file1.html", content1);
    File file2 = FileTestUtils.dumpStringToFile("file2.html", content2);

    final JEditorPane textComponent = createTextPaneFromUrl(file1.toURI().toURL());
    checkSwitchBetweenPages(textComponent, content1, content2);

    File archiveFile = FileTestUtils.createZipArchive(FileTestUtils.getFile("archive.zip"), new File[]{file1, file2});
    textComponent.setPage(new URL("jar:" + archiveFile.toURI().toURL() + "!/file1.html"));
    checkSwitchBetweenPages(textComponent, content1, content2);
  }

  @Test
  public void testClickOnHyperlinkGivesPriorityToExactMatches() throws Exception {
    checkClickOnHyperlink("<html>blah blah<a href=\"https://www.junit.org\">a link text</a>reblah" +
                          "blah blah<a href=\"https://www.apache.org\">link text</a>reblah</html>",
                          "link text",
                          "https://www.apache.org");
  }

  @Test
  public void testClickOnUnknownHyperlink() throws Exception {
    checkClickOnHyperlinkError("<html>blah blah<a href=\"https://www.junit.org\">a link text</a>reblah" +
                               "blah blah<a href=\"https://www.apache.org\">link text</a>reblah</html>",
                               "unknown",
                               "Hyperlink 'unknown' not found");
  }

  @Test
  public void testClickOnHyperlinkWithAmbiguity() throws Exception {
    checkClickOnHyperlinkError("<html>blah blah<a href=\"https://www.junit.org\">a link text</a>reblah" +
                               "blah blah<a href=\"https://www.apache.org\">another link text</a>reblah</html>",
                               "link text",
                               "Ambiguous command - found several hyperlinks matching 'link text'");
  }

  @Test
  public void testClickOnHyperlinkAcceptsAttributesOnATag() throws Exception {
    checkClickOnHyperlink("<html>blah blah<a title=\"JUNIT \" " +
                          "                  href=\"https://www.junit.org\" " +
                          "                  name = \"junit hyperlink\">link text</a>reblah</html>",
                          "link text",
                          "https://www.junit.org");
  }

  @Test
  public void testClickOnHyperlinkWithBigHtml() throws Exception {
    StringBuilder htmlText = new StringBuilder("<html>" +
                                               "  <head>" +
                                               "    <meta name=\"UISpec4J\" lang=\"en\" content=\"text/HTML; charset=utf-8\"/>" +
                                               "    <title name=\"Universal rules\"/>" +
                                               "  </head>" +
                                               "  <body>" +
                                               "    Universal <b>rules</b>:" +
                                               "    <a href=\"https://www.junit.org\">link text</a>");
    for (int i = 0; i < 5000; i++) {
      htmlText.append("<a href=\"https://www.otherlink.org\">other link text")
        .append(i)
        .append("</a>");
    }
    htmlText.append("</body></html>");
    checkClickOnHyperlink(htmlText.toString(),
                          "link text",
                          "https://www.junit.org");
  }

  private void checkSwitchBetweenPages(JTextComponent textComponent, String content1, String content2) {
    TextBox textBox = new TextBox(textComponent);
    assertTrue(textBox.htmlEquals(content1));

    textBox.clickOnHyperlink("Subject 2");
    textBox.clickOnHyperlink("Subject 1");
    waitUntil(textBox.htmlEquals(content1), 1000);

    textBox.clickOnHyperlink("Subject 2");
    assertTrue(textBox.htmlEquals(content2));
  }

  private JTextPane createTextPaneFromUrl(URL url) throws IOException {
    final JTextPane jTextPane = new JTextPane();
    jTextPane.setContentType("text/html");
    jTextPane.setPage(url);

    jTextPane.addHyperlinkListener(event -> {
      try {
        jTextPane.setPage(event.getURL());
      }
      catch (IOException ignored) {
      }
    });
    return jTextPane;
  }

  private void checkClickOnHyperlink(String html, String link, String expectedTarget) throws Exception {
    JTextPane textPane = createHtmlTextPane(html);
    clickOnHyperLink(textPane, link, expectedTarget);
  }

  private void clickOnHyperLink(JEditorPane editorPane, String link, String expectedTarget) throws Exception {
    DummyHyperlinkListener listener = new DummyHyperlinkListener();
    editorPane.addHyperlinkListener(listener);
    TextBox textComponent = new TextBox(editorPane);
    textComponent.clickOnHyperlink(link);
    Assertions.assertEquals(1, listener.getCallCount());
    Assertions.assertEquals(expectedTarget, listener.getLastEvent().getDescription());

    listener.reset();
    textComponent.triggerClickOnHyperlink(link).run();
    Assertions.assertEquals(1, listener.getCallCount());
    Assertions.assertEquals(expectedTarget, listener.getLastEvent().getDescription());
  }

  private void checkClickOnHyperlinkError(String html, final String link, String errorMessage) throws Exception {
    final TextBox textComponent = new TextBox(createHtmlTextPane(html));
    checkAssertionError(() -> textComponent.clickOnHyperlink(link), errorMessage);

    checkAssertionError(() -> textComponent.triggerClickOnHyperlink(link).run(), errorMessage);
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

  private void initWithHtmlTextPane() {
    JTextPane pane = new JTextPane();
    pane.setContentType("text/html; charset=EUC-JP");
    init(pane);
  }

  private void init(JTextComponent swingComponent) {
    jTextComponent = swingComponent;
    jTextComponent.setName("myText");
    createTextBox("");
  }
}
