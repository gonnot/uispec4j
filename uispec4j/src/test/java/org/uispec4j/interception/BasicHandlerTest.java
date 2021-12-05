package org.uispec4j.interception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uispec4j.Trigger;
import org.uispec4j.Window;

import javax.swing.*;

public class BasicHandlerTest extends InterceptionTestCase {

  @Test
  public void testStandardUsage() {
    WindowInterceptor
      .init(triggerShowDialog())
      .process(BasicHandler.init()
                 .assertTitleEquals("Dialog title")
                 .assertTitleContains("title")
                 .assertContainsText("some text")
                 .clickButton("OK")
                 .triggerButtonClick("Hide"))
      .run();
    logger.assertEquals("<log>" +
                        "  <click button='OK'/>" +
                        "  <click button='Hide'/>" +
                        "</log>");
  }

  @Test
  public void testTitleEqualsError() {
    checkAssertionError(
      WindowInterceptor
        .init(triggerShowDialog())
        .process(BasicHandler
                   .init()
                   .assertTitleEquals("Error")
                   .triggerButtonClick("Hide")),
      "Unexpected title - ==> expected: <Error> but was: <Dialog title>");
  }

  @Test
  public void testTitleContainsError() {
    checkAssertionError(
      WindowInterceptor
        .init(triggerShowDialog())
        .process(BasicHandler
                   .init()
                   .assertTitleContains("Error")
                   .triggerButtonClick("Hide")),
      "expected to contain:<Error> but was: <Dialog title> ==> expected: <true> but was: <false>");
  }

  @Test
  public void testAssertContainsTextError() {
    checkAssertionError(WindowInterceptor
                          .init(triggerShowDialog())
                          .process(BasicHandler
                                     .init()
                                     .assertContainsText("Error")
                                     .triggerButtonClick("Hide")),
                        "Text not found: Error");
  }

  @Test
  public void testClickButtonError() {
    checkAssertionError(WindowInterceptor
                          .init(triggerShowDialog())
                          .process(BasicHandler
                                     .init()
                                     .clickButton("Unknown")
                                     .triggerButtonClick("Hide")),
                        "Component 'Unknown' of type 'button' not found - available names: [Hide,OK]");
  }

  @Test
  public void testJOptionPaneConfirmationReplies() {
    checkSelectedValue(JOptionPane.YES_OPTION, JOptionPane.YES_NO_OPTION, getLocalLabel("OptionPane.yesButtonText"));
    checkSelectedValue(JOptionPane.NO_OPTION, JOptionPane.YES_NO_OPTION, getLocalLabel("OptionPane.noButtonText"));
    checkSelectedValue(JOptionPane.OK_OPTION, JOptionPane.OK_CANCEL_OPTION, getLocalLabel("OptionPane.okButtonText"));
    checkSelectedValue(JOptionPane.CANCEL_OPTION, JOptionPane.OK_CANCEL_OPTION, getLocalLabel("OptionPane.cancelButtonText"));
  }

  @Test
  public void testSetInputInJOptionPane() {
    WindowInterceptor
      .init(() -> Assertions.assertEquals("result", JOptionPane.showInputDialog("Message")))
      .process(BasicHandler.init()
                 .setText("result")
                 .triggerButtonClick("OK"))
      .run();
  }

  @Test
  public void testSetInputWithNullValueInJOptionPane() {
    WindowInterceptor
      .init(() -> Assertions.assertEquals("", JOptionPane.showInputDialog("Message")))
      .process(BasicHandler.init()
                 .setText(null)
                 .triggerButtonClick("OK"))
      .run();
  }

  /* This is not a feature, but a known limitation */
  @Test
  public void testSetInputFollowedByACancelInJOptionPaneReturnsTheInputValue() {
    WindowInterceptor
      .init(() -> Assertions.assertEquals("Result", JOptionPane.showInputDialog("Message")))
      .process(BasicHandler.init()
                 .setText("Result")
                 .triggerButtonClick(getLocalLabel("OptionPane.cancelButtonText")))
      .run();
  }

  @Test
  public void testInterceptingAJOptionPaneFromInsideATrigger() {
    final JFrame frame = new JFrame();
    WindowInterceptor
      .init(() -> WindowInterceptor
        .init(() -> {
          int result = JOptionPane.showConfirmDialog(frame, "OK?",
                                                     "Title",
                                                     JOptionPane.OK_CANCEL_OPTION);
          if (result == JOptionPane.OK_OPTION) {
            logger.log("showDialog");
            JDialog dialog = new JDialog(frame, "Dialog", true);
            dialog.setVisible(true);
          }
          else {
            throw new Error("unexpected result " + result);
          }
        })
        .process(BasicHandler.init().triggerButtonClick("OK"))
        .run())
      .process(new WindowHandler() {
        public Trigger process(final Window window) {
          logger.log("dialogShown").add("title", window.getTitle());
          return () -> window.getAwtContainer().setVisible(false);
        }
      })
      .run();
    logger.assertEquals("<log>" +
                        "  <showDialog/>" +
                        "  <dialogShown title='Dialog'/>" +
                        "</log>");
  }

  @Test
  public void testJOptionPaneInterceptionInAWindowSequence() {
    final JFrame frame = new JFrame();
    WindowInterceptor
      .init(() -> {
        int result = JOptionPane.showConfirmDialog(frame, "Confirm?", "Title", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
          logger.log("start");
          JDialog dialog = new JDialog(frame, "dialog", true);
          addHideButton(dialog, "Close");
          dialog.setVisible(true);
          logger.log("end");
        }
        else {
          throw new Error("Unexpected result " + result);
        }
      })
      .process(BasicHandler.init()
                 .assertContainsText("Confirm?")
                 .triggerButtonClick(getLocalLabel("OptionPane.yesButtonText")))
      .processWithButtonClick("Close")
      .run();
    logger.assertEquals("<log>" +
                        "  <start/>" +
                        "  <click button='Close'/>" +
                        "  <end/>" +
                        "</log>");
  }

  private void checkSelectedValue(final int value, final int optionType, String button) {
    WindowInterceptor
      .init(() -> Assertions.assertEquals(value,
                                          JOptionPane.showConfirmDialog(new JFrame(), "msg", "title",
                                                                        optionType,
                                                                        JOptionPane.WARNING_MESSAGE)))
      .process(BasicHandler.init().triggerButtonClick(button))
      .run();
  }

  private Trigger triggerShowDialog() {
    return () -> {
      JDialog dialog = createModalDialog("aDialog");
      dialog.setTitle("Dialog title");
      dialog.getContentPane().add(new JTextArea("some text"));
      addHideButton(dialog, "Hide");
      addLoggerButton(dialog, "OK");
      dialog.setVisible(true);
    };
  }
}
