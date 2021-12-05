package org.uispec4j.interception;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uispec4j.Button;
import org.uispec4j.Trigger;
import org.uispec4j.UISpec4J;
import org.uispec4j.Window;
import org.uispec4j.interception.handlers.ShownInterceptionDetectionHandler;
import org.uispec4j.utils.AssertionFailureNotDetectedError;
import org.uispec4j.utils.Utils;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class WindowInterceptorForNonModalWindowsTest extends WindowInterceptorTestCase {
  private Thread thread;

  @AfterEach
  final protected void tearDown() throws Exception {
    if (thread != null) {
      thread.join();
      thread = null;
    }
  }

  @Test
  public void testInterceptingAFrame() throws Exception {
    Window window = WindowInterceptor.run(new Trigger() {
      public void run() {
        logger.log("triggerRun");
        final JFrame frame = new JFrame(WindowInterceptorForNonModalWindowsTest.this.getClass().getSimpleName());
        addLoggerButton(frame, "OK");
        frame.setVisible(true);
      }
    });
    assertNotNull(window);
    window.getButton("OK").click();
    logger.assertEquals("<log>" +
                        "  <triggerRun/>" +
                        "  <click button='OK'/>" +
                        "</log>");
  }

  @Test
  public void testInterceptingANonModalJDialog() throws Exception {
    Window window = WindowInterceptor.run(new Trigger() {
      public void run() {
        logger.log("triggerRun");
        JDialog dialog = new JDialog();
        dialog.setTitle(WindowInterceptorForNonModalWindowsTest.this.getClass().getSimpleName());
        addLoggerButton(dialog, "OK");
        dialog.setVisible(true);
      }
    });
    assertNotNull(window);
    window.getButton("OK").click();
    logger.assertEquals("<log>" +
                        "  <triggerRun/>" +
                        "  <click button='OK'/>" +
                        "</log>");
  }

  @Test
  public void testNonModalJDialogAfterATransientDialog() throws Exception {
    final Trigger trigger = new Trigger() {
      public void run() {
        logger.log("triggerRun");
        JDialog transientDialog = new JDialog();
        transientDialog.setVisible(true);
        Utils.sleep(20);
        transientDialog.setVisible(false);

        JDialog dialog = new JDialog();
        dialog.setTitle("mon dialogue");
        addLoggerButton(dialog, "Log");
        addHideButton(dialog, "Close");
        dialog.setVisible(true);
      }
    };

    Window window = WindowInterceptor.run(new Trigger() {
      public void run() throws Exception {
        WindowInterceptor.init(trigger)
          .processTransientWindow().run();
      }
    });
    assertTrue(window.titleEquals("mon dialogue"));
    window.getButton("Log").click();
    logger.assertEquals("<log>" +
                        "  <triggerRun/>" +
                        "  <click button='Log'/>" +
                        "</log>");
  }

  @Test
  public void testInterceptionWithATriggerThatDisplaysNothing() throws Exception {
    try {
      WindowInterceptor.run(Trigger.DO_NOTHING);
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
      WindowInterceptor.run(new Trigger() {
        public void run() throws Exception {
          throw exception;
        }
      });
      throw new AssertionFailureNotDetectedError();
    }
    catch (RuntimeException e) {
      assertSame(exception, e.getCause());
    }

    try {
      WindowInterceptor
        .init(new Trigger() {
          public void run() throws Exception {
            throw exception;
          }
        })
        .process(new WindowHandler() {
          public Trigger process(Window window) {
            return null;
          }
        })
        .run();
      throw new AssertionFailureNotDetectedError();
    }
    catch (InterceptionError e) {
      Assertions.assertEquals("java.lang.IllegalAccessException: error", e.getMessage());
    }
  }

  @Test
  public void testInterceptingUsingAButtonTrigger() throws Exception {
    Button button = new Button(new JButton(new ShowDialogAction(false)));
    Window window = WindowInterceptor.run(button.triggerClick());
    window.titleEquals("MyDialog");
  }

  @Test
  public void testInterceptingAModalDialogMustUseAHandler() throws Exception {
    try {
      WindowInterceptor.run(new Trigger() {
        public void run() {
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              createAndShowModalDialog("aDialog");
            }
          });
        }
      });
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Window 'aDialog' is modal, it must be intercepted with a WindowHandler",
                              e.getMessage());
    }
  }

  @Test
  public void testInterceptingAJFrameShownFromAnotherThread() throws Exception {
    Window window = WindowInterceptor.run(new Trigger() {
      public void run() throws Exception {
        thread = new Thread() {
          public void run() {
            JFrame frame = new JFrame("expected title");
            frame.setVisible(true);
          }
        };
        thread.start();
      }
    });
    window.titleEquals("expected title");
  }

  @Test
  public void testInterceptingANonModalDialogShownFromAnotherThread() throws Exception {
    showNonModalDialogInThread(200, 100);
    logger.assertEquals("<log>" +
                        "  <triggerRun/>" +
                        "</log>");
  }

  @Test
  public void testNonModalWindowsDoNotNeedToBeClosed() throws Exception {
    final JFrame frame = new JFrame();
    WindowInterceptor
      .init(new Trigger() {
        public void run() throws Exception {
          frame.setVisible(true);
        }
      })
      .process(new WindowHandler() {
        public Trigger process(Window window) {
          Utils.sleep(UISpec4J.getWindowInterceptionTimeLimit() + 10);
          return Trigger.DO_NOTHING;
        }
      })
      .run();
    Assertions.assertTrue(frame.isVisible());
  }

  private void showNonModalDialogInThread(int waitWindowTimeLimit, final int waitTimeInThread) {
    final JDialog dialog = new JDialog(new JFrame(), "dialogShownInThread", false);
    UISpec4J.setWindowInterceptionTimeLimit(waitWindowTimeLimit);
    assertNotNull(WindowInterceptor.run(new Trigger() {
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
