package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uispec4j.utils.AssertionFailureNotDetectedError;

import javax.swing.*;
import java.awt.*;

public class WindowForFrameTest extends WindowTestCase {

  @Test
  public void testIsModal() throws Exception {
    Window window = new Window(new Frame());
    assertFalse(window.isModal());
    try {
      assertTrue(window.isModal());
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
    }
  }

  @Test
  public void testWindowManagesMenuBars() throws Exception {
    Window window = new Window(new Frame());
    try {
      window.getMenuBar();
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("This component has no menu bar", e.getMessage());
    }
  }

  protected boolean supportsMenuBars() {
    return false;
  }

  protected Window createWindowWithMenu(JMenuBar jMenuBar) {
    return null;
  }

  protected Window createWindowWithTitle(String title) {
    return new Window(new Frame(title));
  }

  protected void close(Window window) {
    Frame frame = (Frame)window.getAwtContainer();
    frame.dispose();
  }
}
