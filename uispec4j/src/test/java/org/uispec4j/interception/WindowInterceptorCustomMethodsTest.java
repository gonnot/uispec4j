package org.uispec4j.interception;

import org.junit.jupiter.api.Test;
import org.uispec4j.Trigger;
import org.uispec4j.Window;
import org.uispec4j.utils.Utils;

import javax.swing.*;

public class WindowInterceptorCustomMethodsTest extends WindowInterceptorTestCase {

  @Test
  public void testProcessTransientWindow() {
    WindowInterceptor
      .init(new TransientWindowTrigger())
      .processTransientWindow("Actual")
      .run();
  }

  @Test
  public void testProcessTransientWindowWithNoTitle() {
    WindowInterceptor
      .init(new TransientWindowTrigger())
      .processTransientWindow()
      .run();
  }

  @Test
  public void testProcessTransientWindowError() throws Exception {
    checkInterceptionError(() -> WindowInterceptor
                             .init(new TransientWindowTrigger())
                             .processTransientWindow("Expected")
                             .run(),
                           "Invalid window title - ==> expected: <Expected> but was: <Actual>");
  }

  @Test
  public void testWindowTitleChecking() {
    WindowInterceptor
      .init(() -> {
        JDialog dialog = createModalDialog("dialog title");
        addHideButton(dialog, "Hide");
        dialog.setVisible(true);
      })
      .process("dialog title", new ButtonTriggerHandler("Hide"))
      .run();
    logger.assertEquals("<log>" +
                        "  <click button='Hide'/>" +
                        "</log>");
  }

  @Test
  public void testWindowTitleError() {
    checkAssertionError(
      WindowInterceptor
        .init(() -> {
          JDialog dialog = createModalDialog("dialog title");
          addHideButton(dialog, "Hide");
          dialog.setVisible(true);
        })
        .process("error", new ButtonTriggerHandler("Hide")),
      "Unexpected title - ==> expected: <error> but was: <dialog title>");
  }

  @Test
  public void testWindowTitleErrorInASequence() {
    checkAssertionError(
      WindowInterceptor
        .init(getShowFirstDialogTrigger())
        .processWithButtonClick("OK")
        .process("error", new ButtonTriggerHandler("OK")),
      "Error in handler 'error': Unexpected title - ==> expected: <error> but was: <second dialog>");
  }

  @Test
  public void testProcessWithButtonClick() {
    WindowInterceptor
      .init(getShowFirstDialogTrigger())
      .processWithButtonClick("OK")
      .processWithButtonClick("second dialog", "OK")
      .processWithButtonClick("Dispose")
      .run();
    logger.assertEquals("<log>" +
                        "  <trigger/>" +
                        "  <click button='OK'/>" +
                        "  <click button='OK'/>" +
                        "  <click button='Dispose'/>" +
                        "</log>");
  }

  @Test
  public void testProcessSeveralHandlers() {
    WindowInterceptor
      .init(getShowFirstDialogTrigger())
      .process(new WindowHandler[]{
        new ButtonTriggerHandler("OK"),
        new ButtonTriggerHandler("OK"),
        new ButtonTriggerHandler("Dispose"),
        })
      .run();
    logger.assertEquals("<log>" +
                        "  <trigger/>" +
                        "  <click button='OK'/>" +
                        "  <click button='OK'/>" +
                        "  <click button='Dispose'/>" +
                        "</log>");
  }

  private static class ButtonTriggerHandler extends WindowHandler {
    private final String buttonName;

    public ButtonTriggerHandler(String buttonName) {
      this.buttonName = buttonName;
    }

    public Trigger process(Window window) {
      return window.getButton(buttonName).triggerClick();
    }
  }

  @Test
  public void testProcessWithButtonClickWithAnUnknownButtonName() {
    checkAssertionError(WindowInterceptor
                          .init(getShowFirstDialogTrigger())
                          .processWithButtonClick("unknown"),
                        "Component 'unknown' of type 'button' not found - available names: [Dispose,OK]");
  }

  @Test
  public void testProcessWithButtonClickHandlesJOptionPaneDialogs() {
    final JFrame frame = new JFrame();
    WindowInterceptor.run(() -> frame.setVisible(true));
    WindowInterceptor
      .init(() -> {
        int result = JOptionPane.showConfirmDialog(frame, "msg");
        logger.log("confirm").add("result", result);
      })
      .processWithButtonClick(getLocalLabel("OptionPane.yesButtonText"))
      .run();
    logger.assertEquals("<log>" +
                        "  <confirm result='0'/>" +
                        "</log>");
  }

  @Test
  public void testProcessWithButtonClickWithAnInvalidTitle() throws Exception {
    checkInterceptionError(() ->
                             WindowInterceptor
                               .init(() -> {
                                 JDialog dialog = new JDialog(new JFrame(), "Actual");
                                 addHideButton(dialog, "ok");
                                 dialog.setVisible(true);
                               })
                               .processWithButtonClick("Expected", "OK")
                               .run(),
                           "Invalid window title - ==> expected: <Expected> but was: <Actual>");
  }

  private static class TransientWindowTrigger implements Trigger {
    public void run() throws InterruptedException {
      JDialog dialog = new JDialog(new JFrame(), "Actual");
      Thread thread = new Thread(() -> Utils.sleep(20));
      thread.start();
      dialog.setVisible(true);
      thread.join();
    }
  }
}
