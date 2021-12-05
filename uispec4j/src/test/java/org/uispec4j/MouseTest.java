package org.uispec4j;



import org.junit.jupiter.api.Test;

import javax.swing.*;

public class MouseTest {
  TextBox textBox = new TextBox(new JLabel());
  final MouseLogger logger = new MouseLogger(textBox);

  @Test
  public void testSimpleClickOnComponent() throws Exception {
    Mouse.click(textBox);

    logger.assertEquals("<log>" +
                        "  <mousePressed button='1'/>" +
                        "  <mouseReleased button='1'/>" +
                        "  <mouseClicked button='1'/>" +
                        "</log>");
  }

  @Test
  public void testDoubleClickOnComponent() throws Exception {
    Mouse.doubleClick(textBox);

    logger.assertEquals("<log>" +
                        "  <mousePressed button='1' clickCount='2'/>" +
                        "  <mouseReleased button='1' clickCount='2'/>" +
                        "  <mouseClicked button='1' clickCount='2'/>" +
                        "</log>");
  }
}
