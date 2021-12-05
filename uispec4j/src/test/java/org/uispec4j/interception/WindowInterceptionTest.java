package org.uispec4j.interception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uispec4j.utils.AssertionFailureNotDetectedError;
import org.uispec4j.utils.Utils;

import javax.swing.*;

public class WindowInterceptionTest extends InterceptionTestCase {

  @Test
  public void testShowingAnUnexpectedWindow() throws Exception {
    JFrame frame = new JFrame();
    String frameTitle = "frame(" + getClass().getSimpleName() + ")";
    frame.setTitle(frameTitle);
    frame.getContentPane().add(new JButton("OK"));
    try {
      frame.setVisible(true);
      throw new AssertionFailureNotDetectedError();
    }
    catch (Error e) {
      Assertions.assertEquals("Unexpected window shown - this window should be handled with WindowInterceptor. " +
                              "Window contents:" +
                              "<window title=\"" + frameTitle + "\">" +
                              "<button label=\"OK\"/></window>",
                              e.getMessage().replaceAll(Utils.LINE_SEPARATOR, ""));
    }
  }
}
