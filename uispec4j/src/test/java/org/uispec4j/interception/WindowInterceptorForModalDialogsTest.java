package org.uispec4j.interception;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uispec4j.Button;
import org.uispec4j.Trigger;
import org.uispec4j.UISpec4J;
import org.uispec4j.Window;
import org.uispec4j.interception.handlers.ShownInterceptionDetectionHandler;
import org.uispec4j.interception.toolkit.UISpecDisplay;
import org.uispec4j.utils.AssertionFailureNotDetectedError;
import org.uispec4j.utils.Utils;

import javax.swing.*;

public class WindowInterceptorForModalDialogsTest extends WindowInterceptorTestCase {
  private Thread thread;

  @AfterEach
  final protected void tearDown() throws Exception {
    if (thread != null) {
      thread.join();
      thread = null;
    }
  }

  @Test
  public void testInterceptingAModalDialog() throws Exception {
    Window window = WindowInterceptor.getModalDialog(new Trigger() {
      public void run() {
        logger.log("triggerRun");
        JDialog dialog = createModalDialog("aDialog");
        addHideButton(dialog, "OK");
        dialog.setVisible(true);
      }
    });
    logger.assertEquals("<log>" +
                        "  <triggerRun/>" +
                        "</log>");
    assertTrue(window.isVisible());
    window.getButton("OK").click();
    logger.assertEquals("<log>" +
                        "  <click button='OK'/>" +
                        "</log>");
    assertFalse(window.isVisible());
  }

  @Test
  public void testInterceptingAFrame() throws Exception {
    try {
      WindowInterceptor.getModalDialog(new Trigger() {
        public void run() {
          new JFrame("aFrame").setVisible(true);
        }
      });
      throw new AssertionFailureNotDetectedError();
    }
    catch (Exception e) {
      Assertions.assertEquals("Window 'aFrame' is non-modal, it must be intercepted with WindowInterceptor.run(Trigger)",
                              e.getCause().getMessage());
    }
  }

  @Test
  public void testInterceptingANonModalJDialog() throws Exception {
    try {
      WindowInterceptor.getModalDialog(new Trigger() {
        public void run() {
          JDialog dialog = new JDialog();
          dialog.setTitle("aDialog");
          dialog.setVisible(true);
        }
      });
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Window 'aDialog' is non-modal, it must be intercepted with WindowInterceptor.run(Trigger)",
                              e.getMessage());
    }
  }

  @Test
  public void testInterceptionWithATriggerThatDisplaysNothing() throws Exception {
    try {
      WindowInterceptor.getModalDialog(Trigger.DO_NOTHING);
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals(ShownInterceptionDetectionHandler.NO_WINDOW_WAS_SHOWN_ERROR_MESSAGE,
                              e.getMessage());
    }
  }

  @Test
  public void testTriggerExceptionsAreConvertedIntoInterceptionErrors() throws Exception {
    final Exception exception = new IllegalAccessException("error");
    try {
      WindowInterceptor.getModalDialog(new Trigger() {
        public void run() throws Exception {
          throw exception;
        }
      });
      throw new AssertionFailureNotDetectedError();
    }
    catch (RuntimeException e) {
      Assertions.assertSame(exception, e.getCause());
    }
  }

  @Test
  public void testTriggerExceptionsAreStoredAndRethrownWhenNotCaughtImmediately() throws Exception {
    final Exception exception = new RuntimeException("unexpected production code exception");
    Window window1 = WindowInterceptor.getModalDialog(new Trigger() {
      public void run() throws Exception {
        JDialog dialog = createModalDialog("dialog");
        addHideButton(dialog, "OK");
        dialog.setVisible(true);
        JDialog dialog2 = createModalDialog("dialog2");
        addHideButton(dialog2, "OK");
        dialog2.setVisible(true);
        throw exception;
      }
    });

    Window window2 = WindowInterceptor.getModalDialog(window1.getButton("OK").triggerClick());
    window2.titleEquals("dialog2");
    window2.getButton("OK").click();
    Utils.sleep(1);

    try {
      WindowInterceptor.run(new Trigger() {
        public void run() throws Exception {
          JDialog dialog3 = new JDialog();
          addHideButton(dialog3, "OK");
          dialog3.setVisible(true);
        }
      });
      Assertions.fail();
    }
    catch (Exception e) {
      Assertions.assertSame(exception, e.getCause());
    }
  }

  @Test
  public void testTriggerExceptionsAreStoredWhenNotCaughtImmediately2() throws Exception {
    final RuntimeException exception = new RuntimeException("unexpected production code exception");
    Window window = WindowInterceptor.getModalDialog(new Trigger() {
      public void run() throws Exception {
        JDialog dialog = createModalDialog("dialog");
        addHideButton(dialog, "OK");
        dialog.setVisible(true);
        throw exception;
      }
    });

    window.getButton("OK").click();
    Utils.sleep(1);
    try {
      UISpecDisplay.instance().rethrowIfNeeded();
      Assertions.fail();
    }
    catch (Exception e) {
      Assertions.assertSame(exception, e.getCause());
    }
  }

  @Test
  public void testInterceptingUsingAButtonTrigger() throws Exception {
    Button button = new Button(new JButton(new ShowDialogAction(true)));
    Window window = WindowInterceptor.getModalDialog(button.triggerClick());
    window.titleEquals("MyDialog");
    window.dispose();
  }

  @Test
  public void testInterceptingAJDialogShownFromAnotherThread() throws Exception {
    Window window = WindowInterceptor.getModalDialog(new Trigger() {
      public void run() throws Exception {
        thread = new Thread() {
          public void run() {
            JDialog dialog = createModalDialog("expected title");
            addHideButton(dialog, "OK");
            dialog.setVisible(true);
          }
        };
        thread.start();
      }
    });
    window.titleEquals("expected title");
    window.getButton("OK").click();
    assertFalse(window.isVisible());
    window.dispose();
  }

  public void disabled_testInterceptingAModalDialogShownFromAnotherThread() throws Exception {
    showModalDialogInThread(200, 100);
    logger.assertEquals("<log>" +
                        "  <triggerRun/>" +
                        "</log>");
  }

  private void showModalDialogInThread(int waitWindowTimeLimit, final int waitTimeInThread) {
    final JDialog dialog = new JDialog(new JFrame(), "dialogShownInThread", true);
    UISpec4J.setWindowInterceptionTimeLimit(waitWindowTimeLimit);
    Assertions.assertNotNull(WindowInterceptor.getModalDialog(new Trigger() {
      public void run() {
        logger.log("triggerRun");
        thread = new Thread(new Runnable() {
          public void run() {
            Utils.sleep(waitTimeInThread);
            dialog.setVisible(true);
          }
        });
        thread.setName(thread.getName() + "(" + getClass().getSimpleName() + ")");
        thread.start();
      }
    }));
  }
}
