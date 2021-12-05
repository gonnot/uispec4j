package org.uispec4j.interception;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uispec4j.Trigger;
import org.uispec4j.UISpec4J;
import org.uispec4j.Window;
import org.uispec4j.interception.handlers.ShownInterceptionDetectionHandler;
import org.uispec4j.interception.toolkit.UISpecDisplay;
import org.uispec4j.utils.ComponentUtils;
import org.uispec4j.utils.Utils;

import javax.swing.*;
import java.awt.*;

public class WindowInterceptorForDialogSequenceTest extends WindowInterceptorTestCase {

  private Thread thread;

  @AfterEach
  final protected void tearDown() throws Exception {
    if (thread != null) {
      thread.join();
      thread = null;
    }
  }

  @Test
  public void testStandardSequence() throws Exception {
    WindowInterceptor
      .init(getShowFirstDialogTrigger())
      .process(new WindowHandler() {
        public Trigger process(Window window) throws Exception {
          logger.log("firstDialogShown");
          return window.getButton("OK").triggerClick();
        }
      })
      .process(new WindowHandler() {
        public Trigger process(Window window) throws Exception {
          logger.log("secondDialogShown");
          return window.getButton("OK").triggerClick();
        }
      })
      .process(new WindowHandler() {
        public Trigger process(Window window) throws Exception {
          logger.log("thirdDialogShown");
          return window.getButton("Dispose").triggerClick();
        }
      })
      .run();

    logger.assertEquals("<log>" +
                        "  <trigger/>" +
                        "  <firstDialogShown/>" +
                        "  <click button='OK'/>" +
                        "  <secondDialogShown/>" +
                        "  <click button='OK'/>" +
                        "  <thirdDialogShown/>" +
                        "  <click button='Dispose'/>" +
                        "</log>");
  }

  @Test
  public void testInterceptingAModalDialogWithAReturnedValue() throws Exception {
    WindowInterceptor
      .init(new Trigger() {
        public void run() {
          logger.log("triggerRun");
          JTextField textField = new JTextField();
          JDialog dialog = createModalDialog("aDialog");
          dialog.getContentPane().add(textField);
          dialog.setVisible(true);
          Assertions.assertEquals("result", textField.getText());
          logger.log("done");
        }
      })
      .process(new WindowHandler() {
        public Trigger process(final Window window) throws Exception {
          window.getTextBox().setText("result");
          logger.log("windowProcessed");
          return new Trigger() {
            public void run() throws Exception {
              ComponentUtils.close(window);
            }
          };
        }
      })
      .run();
    logger.assertEquals("<log>" +
                        "  <triggerRun/>" +
                        "  <windowProcessed/>" +
                        "  <done/>" +
                        "</log>");
  }

  @Test
  public void testModalInterceptionWithATriggerThatDisplaysNothing() throws Exception {
    checkAssertionError(WindowInterceptor
                          .init(Trigger.DO_NOTHING)
                          .process(new WindowHandler() {
                            public Trigger process(Window window) {
                              throw new AssertionError("this should not be called");
                            }
                          }),
                        ShownInterceptionDetectionHandler.NO_WINDOW_WAS_SHOWN_ERROR_MESSAGE);
    Assertions.assertEquals(0, UISpecDisplay.instance().getHandlerCount());
  }

  @Test
  public void testInterceptingAModalDialogWithoutClosingItInTheHandler() throws Exception {
    checkAssertionError(WindowInterceptor
                          .init(new Trigger() {
                            public void run() {
                              logger.log("show");
                              createAndShowModalDialog("aDialog");
                              logger.log("closedByUISpec");
                            }
                          })
                          .process(new WindowHandler() {
                            public Trigger process(Window window) {
                              logger.log("windowProcessed");
                              return Trigger.DO_NOTHING;
                            }
                          }),
                        "Modal window 'aDialog' was not closed - make sure that " +
                        "setVisible(false) gets called by the production code");
    logger.assertEquals("<log>" +
                        "  <show/>" +
                        "  <windowProcessed/>" +
                        "  <closedByUISpec/>" +
                        "</log>");
  }

  @Test
  public void testInterceptingAModalDialogShownFromAnotherThread() throws Exception {
    showModalDialogInThread(200, 100);
    logger.assertEquals("<log>" +
                        "  <triggerRun/>" +
                        "  <windowProcessed/>" +
                        "  <click button='Dispose'/>" +
                        "</log>");
  }

  @Test
  public void testUsingDisposeInShowDialog() throws Exception {
    showDialogAndDispose();
    logger.assertEquals("<log>" +
                        "  <show/>" +
                        "  <click button='Dispose'/>" +
                        "  <closed/>" +
                        "</log>");
  }

  @Test
  public void testInterceptionWorksEvenWhenInterceptionIsRunFromTheSwingThread() throws Exception {
    SwingUtilities.invokeAndWait(new Runnable() {
      public void run() {
        showDialogAndDispose();
      }
    });

    logger.assertEquals("<log>" +
                        "  <show/>" +
                        "  <click button='Dispose'/>" +
                        "  <closed/>" +
                        "</log>");
  }

  @Test
  public void testInterceptingAModalDialogWithoutClosingItInTheWindowHandlerWhenRunFromTheSwingThread() throws Exception {
    SwingUtilities.invokeAndWait(new Runnable() {
      public void run() {
        checkAssertionError(WindowInterceptor
                              .init(new Trigger() {
                                public void run() {
                                  logger.log("show");
                                  createAndShowModalDialog("aDialog");
                                  logger.log("closedByUISpec");
                                }
                              })
                              .process(new WindowHandler() {
                                public Trigger process(Window window) {
                                  logger.log("windowProcessed");
                                  return Trigger.DO_NOTHING;
                                }
                              }),
                            "Modal window 'aDialog' was not closed - make sure that setVisible(false) gets " +
                            "called by the production code");
      }
    });
    logger.assertEquals("<log>" +
                        "  <show/>" +
                        "  <windowProcessed/>" +
                        "  <closedByUISpec/>" +
                        "</log>");
  }

  @Test
  public void testImbricationOfInterceptions() throws Exception {
    final JFrame frame = new JFrame("frame");

    JDialog firstDialog = new JDialog(frame, "first", true);
    addHideButton(firstDialog, "Dispose");
    addShowDialogButton(frame, "Show", firstDialog);

    JDialog secondDialog = new JDialog(frame, "second", true);
    addHideButton(secondDialog, "Dispose");
    addShowDialogButton(firstDialog, "Show", secondDialog);

    WindowInterceptor
      .init(new Trigger() {
        public void run() throws Exception {
          frame.setVisible(true);
        }
      })
      .process(new WindowHandler() {
        public Trigger process(Window frame) throws Exception {
          WindowInterceptor
            .init(new ClickButtonTrigger(frame, "Show"))
            .process(new WindowHandler() {
              public Trigger process(Window firstWindow) throws Exception {
                WindowInterceptor
                  .init(new ClickButtonTrigger(firstWindow, "Show"))
                  .process(new WindowHandler() {
                    public Trigger process(Window secondWindow) throws Exception {
                      return secondWindow.getButton("Dispose").triggerClick();
                    }
                  })
                  .run();
                return firstWindow.getButton("Dispose").triggerClick();
              }
            })
            .run();
          return Trigger.DO_NOTHING;
        }
      })
      .run();
    logger.assertEquals("<log>" +
                        "  <click button='Show'/>" +
                        "  <click button='Show'/>" +
                        "  <click button='Dispose'/>" +
                        "  <click button='Dispose'/>" +
                        "</log>");
  }

  @Test
  public void testShowingTheSameDialogTwice() throws Exception {
    WindowInterceptor
      .init(new Trigger() {
        public void run() throws Exception {
          JDialog dialog = createModalDialog("aDialog");
          addHideButton(dialog, "Hide");
          dialog.setVisible(true);
          logger.log("step1");
          dialog.setVisible(true);
          logger.log("step2");
        }
      })
      .processWithButtonClick("Hide")
      .processWithButtonClick("Hide")
      .run();
    logger.assertEquals("<log>" +
                        "  <click button='Hide'/>" +
                        "  <step1/>" +
                        "  <click button='Hide'/>" +
                        "  <step2/>" +
                        "</log>");
  }

  @Test
  public void testShowIsBlocked() throws Exception {
    WindowInterceptor
      .init(new Trigger() {
        public void run() throws Exception {
          JDialog dialog = createModalDialog("aDialog");
          addHideButton(dialog, "Hide");
          logger.log("beforeShow");
          dialog.setVisible(true);
          logger.log("afterShow");
        }
      })
      .process(new WindowHandler() {
        public Trigger process(Window window) throws Exception {
          Utils.sleep(100);
          logger.log("sleepCompleted");
          return window.getButton("Hide").triggerClick();
        }
      })
      .run();
    logger.assertEquals("<log>" +
                        "  <beforeShow/>" +
                        "  <sleepCompleted/>" +
                        "  <click button='Hide'/>" +
                        "  <afterShow/>" +
                        "</log>");
  }

  @Test
  public void testAwtDialogsAreNotSupported() throws Exception {
    java.awt.Window window = new Dialog(new Frame());
    try {
      window.setVisible(true);
    }
    catch (Throwable e) {
      Assertions.assertEquals("Dialogs of type '" + window.getClass().getName() + "' are not supported.",
                              e.getMessage());
    }
  }

  @Test
  public void testErrorWhenTheInitialTriggerDisplaysNoWindow() throws Exception {
    checkAssertionError(WindowInterceptor
                          .init(Trigger.DO_NOTHING)
                          .process(new WindowHandler() {
                            public Trigger process(Window window) {
                              return Trigger.DO_NOTHING;
                            }
                          })
                          .processWithButtonClick("OK"),
                        "Error in first handler: " +
                        ShownInterceptionDetectionHandler.NO_WINDOW_WAS_SHOWN_ERROR_MESSAGE);
  }

  @Test
  public void testErrorWhenTheFirstHandlerDisplaysNoWindow() throws Exception {
    checkAssertionError(WindowInterceptor
                          .init(getShowFirstDialogTrigger())
                          .process(new WindowHandler("first") {
                            public Trigger process(final Window window) throws Exception {
                              return window.getButton("Dispose").triggerClick();
                            }
                          })
                          .process(new WindowHandler() {
                            public Trigger process(Window window) {
                              Assertions.fail("This one should not be called");
                              return Trigger.DO_NOTHING;
                            }
                          }), "Error in handler 'first': " +
                              ShownInterceptionDetectionHandler.NO_WINDOW_WAS_SHOWN_ERROR_MESSAGE);
  }

  @Test
  public void testErrorWhenTheFirstHandlerThrowsAnError() throws Exception {
    checkAssertionError(WindowInterceptor
                          .init(getShowFirstDialogTrigger())
                          .process(new WindowHandler("first") {
                            public Trigger process(Window window) {
                              throw new AssertionError("error");
                            }
                          })
                          .processWithButtonClick("ok"), "Error in handler 'first': error");
  }

  @Test
  public void testErrorWhenTheSecondHandlerThrowsAnError() throws Exception {
    checkAssertionError(WindowInterceptor
                          .init(getShowFirstDialogTrigger())
                          .processWithButtonClick("OK")
                          .process(new WindowHandler("second") {
                            public Trigger process(Window window) {
                              throw new AssertionError("error");
                            }
                          }), "Error in handler 'second': error");
  }

  @Test
  public void testErrorWhenTheFirstHandlerThrowsAnException() throws Exception {
    checkAssertionError(WindowInterceptor
                          .init(getShowFirstDialogTrigger())
                          .process(new WindowHandler("first") {
                            public Trigger process(Window window) {
                              throw new RuntimeException("exception");
                            }
                          })
                          .processWithButtonClick("ok"), "Error in handler 'first': exception");
  }

  @Test
  public void testErrorWhenTheSecondHandlerThrowsAnException() throws Exception {
    checkAssertionError(WindowInterceptor
                          .init(getShowFirstDialogTrigger())
                          .processWithButtonClick("OK")
                          .process(new WindowHandler("second") {
                            public Trigger process(Window window) {
                              throw new RuntimeException("exception");
                            }
                          })
                          .processWithButtonClick("ok"), "Error in handler 'second': exception");
  }

  @Test
  public void testErrorWhenAModalDialogIsNotClosedInTheOnlyWindowWithOnlyOneHandler() throws Exception {
    checkAssertionError(WindowInterceptor
                          .init(getShowFirstDialogTrigger())
                          .process(new WindowHandler("first") {
                            public Trigger process(Window window) {
                              return Trigger.DO_NOTHING;
                            }
                          }), "Modal window 'first dialog' was not closed - " +
                              "make sure that setVisible(false) gets called by the production code");
  }

  @Test
  public void testFirstWindowNotClosed() throws Exception {
    checkAssertionError(WindowInterceptor
                          .init(new Trigger() {
                            public void run() throws Exception {
                              JDialog firstDialog = new JDialog(new JFrame(), "first", true);
                              firstDialog.setTitle("first");
                              JDialog secondDialog = new JDialog(firstDialog, "second", true);
                              addShowDialogButton(firstDialog, "show", secondDialog);
                              addHideButton(secondDialog, "close");
                              firstDialog.setVisible(true);
                            }
                          })
                          .processWithButtonClick("show")
                          .processWithButtonClick("close"), "Error in first handler: " +
                                                            "Modal window 'first' was not closed - make sure that setVisible(false) gets " +
                                                            "called by the production code");
  }

  @Test
  public void testErrorWhenTheFirstWindowOfASequenceIsNotClosed() throws Exception {
    checkAssertionError(WindowInterceptor
                          .init(new Trigger() {
                            public void run() throws Exception {
                              JDialog firstDialog = new JDialog(new JFrame(), "first", true);
                              firstDialog.setTitle("first");
                              JDialog secondDialog = new JDialog(firstDialog, "second", true);
                              addShowDialogButton(firstDialog, "show", secondDialog);
                              addHideButton(secondDialog, "close");
                              firstDialog.setVisible(true);
                            }
                          })
                          .processWithButtonClick("show")
                          .processWithButtonClick("close"), "Error in first handler: " +
                                                            "Modal window 'first' was not closed - make sure that setVisible(false) gets " +
                                                            "called by the production code");
  }

  @Test
  public void testErrorWhenAModalDialogIsNotClosedInTheSecondAndLastWindow() throws Exception {
    checkAssertionError(WindowInterceptor
                          .init(getShowFirstDialogTrigger())
                          .process(new WindowHandler("first") {
                            public Trigger process(Window window) throws Exception {
                              return window.getButton("ok").triggerClick();
                            }
                          })
                          .process(new WindowHandler("second") {
                            public Trigger process(Window window) {
                              return Trigger.DO_NOTHING;
                            }
                          }), "Error in handler 'second': Modal window 'second dialog' was not closed - " +
                              "make sure that setVisible(false) gets called by the production code");
  }

  @Test
  public void testErrorWhenAModalDialogIsNotClosedInTheSecondWindow() throws Exception {
    checkAssertionError(WindowInterceptor
                          .init(getShowFirstDialogTrigger())
                          .processWithButtonClick("ok")
                          .process(new WindowHandler("second") {
                            public Trigger process(Window window) {
                              return Trigger.DO_NOTHING;
                            }
                          })
                          .process(new WindowHandler("third") {
                            public Trigger process(Window window) {
                              return Trigger.DO_NOTHING;
                            }
                          }), "Error in handler 'second': " +
                              ShownInterceptionDetectionHandler.NO_WINDOW_WAS_SHOWN_ERROR_MESSAGE);
  }

  @Test
  public void testNoHandlerAdded() throws Exception {
    checkAssertionError(WindowInterceptor.init(Trigger.DO_NOTHING), "You must add at least one handler");
  }

  @Test
  public void testHandlerNameIsNotGivenInTheMessageIfThereIsOnlyOneHandler() throws Exception {
    checkAssertionError(WindowInterceptor
                          .init(getShowFirstDialogTrigger())
                          .process(new WindowHandler() {
                            public Trigger process(Window window) {
                              throw new AssertionError("error");
                            }
                          }), "error");
  }

  @Test
  public void testHandlersAreGivenANumberIfNoNameIsSet() throws Exception {
    checkAssertionError(WindowInterceptor
                          .init(getShowFirstDialogTrigger())
                          .process(new WindowHandler() {
                            public Trigger process(Window window) {
                              throw new AssertionError("error");
                            }
                          })
                          .processWithButtonClick(""), "Error in handler '1': error");
    checkAssertionError(WindowInterceptor
                          .init(getShowFirstDialogTrigger())
                          .processWithButtonClick("OK")
                          .process(new WindowHandler() {
                            public Trigger process(Window window) {
                              throw new AssertionError("error");
                            }
                          })
                          .processWithButtonClick(""), "Error in handler '2': error");
  }

  @Test
  public void testModalDialogsShownInSequenceByTheInitialTrigger() throws Exception {
    WindowInterceptor
      .init(createTriggerWithThreeModalDialogsSequence())
      .processWithButtonClick("dispose")
      .processWithButtonClick("dispose")
      .processWithButtonClick("dispose")
      .run();
  }

  @Test
  public void testErrorForModalDialogsShownInSequenceByTheInitialTrigger() throws Exception {
    checkAssertionError(WindowInterceptor
                          .init(createTriggerWithThreeModalDialogsSequence())
                          .processWithButtonClick("dispose")
                          .process(new WindowHandler() {
                            public Trigger process(Window window) {
                              throw new AssertionError("error");
                            }
                          }), "Error in handler '2': error");
  }

  @Test
  public void testNotClosedErrorForModalDialogsShownInSequenceByTheInitialTrigger() throws Exception {
    checkAssertionError(WindowInterceptor
                          .init(createTriggerWithThreeModalDialogsSequence())
                          .processWithButtonClick("dispose")
                          .process(new WindowHandler("second") {
                            public Trigger process(Window window) {
                              window.titleEquals("dialog 2");
                              return Trigger.DO_NOTHING;
                            }
                          }), "Error in handler 'second': Modal window 'dialog 2' was not closed - " +
                              "make sure that setVisible(false) gets called by the production code");
  }

  private Trigger createTriggerWithThreeModalDialogsSequence() {
    final JFrame frame = new JFrame();
    return new Trigger() {
      public void run() throws Exception {
        for (int i = 1; i < 4; i++) {
          final JDialog dialog = new JDialog(frame, "dialog " + i, true);
          addHideButton(dialog, "Dispose");
          dialog.setVisible(true);
        }
      }
    };
  }

  private void showModalDialogInThread(int waitWindowTimeLimit, final int waitTimeInThread) {
    final JDialog dialog = createModalDialog("aDialog");
    addHideButton(dialog, "Dispose");

    UISpec4J.setWindowInterceptionTimeLimit(waitWindowTimeLimit);
    WindowInterceptor
      .init(new Trigger() {
        public void run() {
          logger.log("triggerRun");
          Runnable runnable = new Runnable() {
            public void run() {
              try {
                Utils.sleep(waitTimeInThread);
                SwingUtilities.invokeAndWait(new Runnable() {
                  public void run() {
                    dialog.setVisible(true);
                  }
                });
              }
              catch (Exception e) {
                throw new RuntimeException(e);
              }
            }
          };
          thread = new Thread(runnable);
          thread.start();
        }
      })
      .process(new WindowHandler() {
        public Trigger process(Window window) throws Exception {
          logger.log("windowProcessed");
          return window.getButton("Dispose").triggerClick();
        }
      })
      .run();
  }

  private void showDialogAndDispose() {
    WindowInterceptor
      .init(new Trigger() {
        public void run() throws Exception {
          final JDialog dialog = createModalDialog("aDialog");
          addHideButton(dialog, "Dispose");
          logger.log("show");
          dialog.setVisible(true);
          logger.log("closed");
        }
      })
      .process(new ButtonClickHandler("Dispose"))
      .run();
  }

  private static class ClickButtonTrigger implements Trigger {
    private final Window window;
    private final String buttonName;

    public ClickButtonTrigger(Window window, String buttonName) {
      this.window = window;
      this.buttonName = buttonName;
    }

    public void run() throws Exception {
      window.getButton(buttonName).click();
    }
  }

  private static class ButtonClickHandler extends WindowHandler {
    private final String buttonLabel;

    public ButtonClickHandler(String buttonLabel) {
      this.buttonLabel = buttonLabel;
    }

    public Trigger process(final Window window) throws Exception {
      return window.getButton(buttonLabel).triggerClick();
    }
  }
}
