package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uispec4j.utils.ArrayUtils;
import org.uispec4j.utils.Functor;
import org.uispec4j.utils.Utils;
import org.uispec4j.xml.XmlAssert;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class DesktopTest extends UIComponentTestCase {

  private final JDesktopPane jDesktopPane = new JDesktopPane();
  private Desktop desktop;

  @BeforeEach
  final protected void setUp() throws Exception {

    jDesktopPane.setName("myDesktop");
    desktop = new Desktop(jDesktopPane);
  }

  @Test
  public void testGetComponentTypeName() throws Exception {
    Assertions.assertEquals("desktop", desktop.getDescriptionTypeName());
  }

  @Test
  public void testGetDescription() throws Exception {
    XmlAssert.assertEquivalent("<desktop name='myDesktop'/>", desktop.getDescription());
  }

  @Test
  public void testFactory() throws Exception {
    checkFactory(new JProgressBar(), ProgressBar.class);
  }

  protected UIComponent createComponent() {
    return new Desktop(jDesktopPane);
  }

  @Test
  public void testGetWindows() throws Exception {
    Assertions.assertEquals(0, desktop.getWindows().length);

    jDesktopPane.add(new JInternalFrame("frame1"));
    jDesktopPane.add(new JInternalFrame("frame2"));
    Window[] windows = desktop.getWindows();
    List titles = new ArrayList();
    for (int i = 0; i < windows.length; i++) {
      titles.add(windows[i].getTitle());
    }
    ArrayUtils.assertEquals(new String[]{"frame1", "frame2"}, titles);
  }

  @Test
  public void testAssertContainsWindow() throws Exception {
    jDesktopPane.add(new JInternalFrame("frame1"));
    assertTrue(desktop.containsWindow("frame1"));

    checkAssertionFails(desktop.containsWindow("unknown"),
                        "No window with title 'unknown' found");
  }

  @Test
  public void testGetWindow() throws Exception {
    JInternalFrame internalFrame = new JInternalFrame("frame1");
    jDesktopPane.add(internalFrame);
    Window window = desktop.getWindow("frame1");
    Assertions.assertEquals("frame1", window.getTitle());
    Assertions.assertSame(internalFrame, window.getAwtComponent());
  }

  @Test
  public void testGetWindowError() throws Exception {
    checkAssertionError(new Functor() {
      public void run() throws Exception {
        desktop.getWindow("unknown");
      }
    }, "Window 'unknown' not found");
  }

  @Test
  public void testGetWindowWaitsForTheWindowToAppear() throws Exception {
    UISpec4J.setWindowInterceptionTimeLimit(100);
    final JInternalFrame internalFrame = new JInternalFrame("frame1");
    Thread thread = new Thread() {
      public void run() {
        Utils.sleep(30);
        jDesktopPane.add(internalFrame);
      }
    };
    thread.start();
    Window window = desktop.getWindow("frame1");
    Assertions.assertSame(internalFrame, window.getAwtComponent());
  }

  @Test
  public void testGetWindowWithTitleAmbiguityError() throws Exception {
    jDesktopPane.add(new JInternalFrame("frame"));
    jDesktopPane.add(new JInternalFrame("frame"));
    checkAssertionError(new Functor() {
      public void run() throws Exception {
        desktop.getWindow("frame");
      }
    }, "There are several windows with title 'frame'");

  }
}
