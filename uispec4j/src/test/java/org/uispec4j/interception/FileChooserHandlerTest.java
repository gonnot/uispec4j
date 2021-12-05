package org.uispec4j.interception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uispec4j.Trigger;
import org.uispec4j.utils.ArrayUtils;
import org.uispec4j.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FileChooserHandlerTest extends InterceptionTestCase {
  private final JFileChooser chooser = new JFileChooser();
  private int result = JFileChooser.ERROR_OPTION;
  private final Trigger SHOW_OPEN_DIALOG_TRIGGER = () -> {
    JFrame frame = new JFrame();
    result = chooser.showOpenDialog(frame);
  };
  private final Trigger SHOW_SAVE_DIALOG_TRIGGER = () -> {
    JFrame frame = new JFrame();
    result = chooser.showSaveDialog(frame);
  };
  private final Trigger SHOW_CUSTOM_DIALOG_TRIGGER = () -> {
    JFrame frame = new JFrame();
    result = chooser.showDialog(frame, "OK");
  };
  private final File javaHome = new File(System.getProperty("java.home"));
  private final File userHome = new File(System.getProperty("user.home"));

  @BeforeEach
  final protected void setUp() {
    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
  }

  @Test
  public void testSelectionOfASingleFile() {
    WindowInterceptor
      .init(SHOW_OPEN_DIALOG_TRIGGER)
      .process(FileChooserHandler.init().select(javaHome))
      .run();
    Assertions.assertEquals(javaHome, chooser.getSelectedFile());
    Assertions.assertEquals(JFileChooser.APPROVE_OPTION, result);
  }

  @Test
  public void testSelectionOfSeveralFiles() {
    File[] files = {javaHome, userHome};
    WindowInterceptor
      .init(SHOW_OPEN_DIALOG_TRIGGER)
      .process(FileChooserHandler.init().select(files))
      .run();
    ArrayUtils.assertEquals(files, chooser.getSelectedFiles());
    Assertions.assertEquals(JFileChooser.APPROVE_OPTION, result);
  }

  @Test
  public void testSelectionOfASingleStringifiedFile() {
    WindowInterceptor
      .init(SHOW_OPEN_DIALOG_TRIGGER)
      .process(FileChooserHandler.init().select(javaHome.getAbsolutePath()))
      .run();
    Assertions.assertEquals(javaHome, chooser.getSelectedFile());
    Assertions.assertEquals(JFileChooser.APPROVE_OPTION, result);
  }

  @Test
  public void testSelectionOfSeveralStringifiedFile() {
    String[] files = {javaHome.getAbsolutePath(), userHome.getAbsolutePath()};
    WindowInterceptor
      .init(SHOW_OPEN_DIALOG_TRIGGER)
      .process(FileChooserHandler.init().select(files))
      .run();
    ArrayUtils.assertEquals(new File[]{javaHome, userHome}, chooser.getSelectedFiles());
    Assertions.assertEquals(JFileChooser.APPROVE_OPTION, result);
  }

  @Test
  public void testCancelSelection() {
    WindowInterceptor
      .init(SHOW_OPEN_DIALOG_TRIGGER)
      .process(FileChooserHandler.init().cancelSelection())
      .run();
    Assertions.assertEquals(0, chooser.getSelectedFiles().length);
    Assertions.assertEquals(JFileChooser.CANCEL_OPTION, result);
  }

  @Test
  public void testAssertCurrentDirEquals() {
    chooser.setCurrentDirectory(javaHome);
    WindowInterceptor
      .init(SHOW_OPEN_DIALOG_TRIGGER)
      .process(FileChooserHandler.init()
                 .assertCurrentDirEquals(javaHome)
                 .select(javaHome))
      .run();
  }

  @Test
  public void testAssertCurrentDirEqualsError() {
    chooser.setCurrentDirectory(javaHome);
    checkError(SHOW_OPEN_DIALOG_TRIGGER, FileChooserHandler.init().assertCurrentDirEquals(userHome),
               javaHome, "Unexpected current directory - ==> expected: <"
                         + userHome + "> but was: <" + javaHome + ">");
  }

  @Test
  public void testAssertCurrentFileNameEquals() {
    chooser.setSelectedFile(new File(javaHome, "aFile.txt"));
    WindowInterceptor
      .init(SHOW_OPEN_DIALOG_TRIGGER)
      .process(FileChooserHandler.init()
                 .assertCurrentFileNameEquals("aFile.txt")
                 .select(javaHome))
      .run();
  }

  @Test
  public void testAssertCurrentFileNameEqualsError() {
    chooser.setSelectedFile(new File(javaHome, "aFile.txt"));
    checkError(SHOW_OPEN_DIALOG_TRIGGER,
               FileChooserHandler.init().assertCurrentFileNameEquals("toto.exe"),
               new File(javaHome, "aFile.txt"),
               "Unexpected file name - ==> expected: <toto.exe> but was: <aFile.txt>");
  }

  @Test
  public void testAssertCurrentFileNameEqualsWithNoSelection() {
    checkError(SHOW_OPEN_DIALOG_TRIGGER,
               FileChooserHandler.init().assertCurrentFileNameEquals("toto.dat"),
               new File(javaHome, "aFile.txt"),
               "Unexpected file name - ==> expected: <toto.dat> but was: <>");
  }

  @Test
  public void testAssertIsOpenSaveDialog() {
    checkOk(SHOW_OPEN_DIALOG_TRIGGER, FileChooserHandler.init().assertIsOpenDialog());
    checkError(SHOW_OPEN_DIALOG_TRIGGER, FileChooserHandler.init().assertIsSaveDialog(),
               javaHome, "Chooser is in 'open' mode");

    checkOk(SHOW_SAVE_DIALOG_TRIGGER, FileChooserHandler.init().assertIsSaveDialog());
    checkError(SHOW_SAVE_DIALOG_TRIGGER, FileChooserHandler.init().assertIsOpenDialog(),
               javaHome, "Chooser is in 'save' mode");

    checkError(SHOW_CUSTOM_DIALOG_TRIGGER, FileChooserHandler.init().assertIsSaveDialog(),
               javaHome, "Chooser is in 'custom' mode");
    checkError(SHOW_CUSTOM_DIALOG_TRIGGER, FileChooserHandler.init().assertIsOpenDialog(),
               javaHome, "Chooser is in 'custom' mode");
  }

  @Test
  public void testDefaultTitle() {
    checkOk(SHOW_OPEN_DIALOG_TRIGGER, FileChooserHandler.init().titleEquals(getLocalLabel("FileChooser.openDialogTitleText")));
    checkOk(SHOW_SAVE_DIALOG_TRIGGER, FileChooserHandler.init().titleEquals(getLocalLabel("FileChooser.saveDialogTitleText")));
  }

  @Test
  public void testCustomTitle() {
    chooser.setDialogTitle("title");
    checkOk(SHOW_OPEN_DIALOG_TRIGGER, FileChooserHandler.init().titleEquals("title"));
    checkError(SHOW_OPEN_DIALOG_TRIGGER, FileChooserHandler.init().titleEquals("error"),
               javaHome,
               "Unexpected title - ==> expected: <error> but was: <title>");
  }

  @Test
  public void testAssertApplyButtonTextEquals() {
    chooser.setApproveButtonText("text");
    checkOk(SHOW_OPEN_DIALOG_TRIGGER, FileChooserHandler.init().assertApplyButtonTextEquals("text"));
    checkError(SHOW_OPEN_DIALOG_TRIGGER, FileChooserHandler.init().assertApplyButtonTextEquals("other"),
               javaHome,
               "Unexpected apply button text - ==> expected: <other> but was: <text>");
  }

  @Test
  public void testAssertAcceptsFilesAndDirectories() {
    final int[] modes =
      {JFileChooser.FILES_ONLY,
       JFileChooser.FILES_AND_DIRECTORIES,
       JFileChooser.DIRECTORIES_ONLY};
    final String[] messages =
      {"The file chooser accepts files only.",
       "The file chooser accepts both files and directories.",
       "The file chooser accepts directories only."};
    for (int i = 0; i < modes.length; i++) {
      final FileChooserHandler[] interceptors =
        {FileChooserHandler.init().assertAcceptsFilesOnly(),
         FileChooserHandler.init().assertAcceptsFilesAndDirectories(),
         FileChooserHandler.init().assertAcceptsDirectoriesOnly()};
      chooser.setFileSelectionMode(modes[i]);
      for (int j = 0; j < modes.length; j++) {
        if (i == j) {
          checkOk(SHOW_OPEN_DIALOG_TRIGGER, interceptors[j]);
        }
        else {
          checkError(SHOW_OPEN_DIALOG_TRIGGER, interceptors[j], javaHome, messages[i]);
        }
      }
    }
  }

  @Test
  public void testAssertMultiSelectionEnabled() {
    checkMultiSelectionEnabled(true, "Multi selection is enabled.");
    checkMultiSelectionEnabled(false, "Multi selection is not enabled.");
  }

  @Test
  public void testShownDialogIsNotAFileChooserButAJFrame() {
    checkUnexpectedWindowShown(new JFrame("title"), "title");
  }

  @Test
  public void testShownDialogIsNotAFileChooserButAModalDialog() {
    checkUnexpectedWindowShown(createModalDialog("aDialog"), "aDialog");
  }

  private void checkUnexpectedWindowShown(final Window window, String title) {
    checkAssertionError(WindowInterceptor
                          .init(() -> window.setVisible(true))
                          .process(FileChooserHandler.init().select(javaHome)),
                        "The shown window is not a file chooser - window content:" + Utils.LINE_SEPARATOR +
                        "<window title=\"" + title + "\"/>");
  }

  private void checkOk(Trigger trigger, FileChooserHandler handler) {
    WindowInterceptor
      .init(trigger)
      .process(handler.select(javaHome))
      .run();
  }

  private void checkError(Trigger trigger,
                          FileChooserHandler handler,
                          File selectedFile,
                          String errorMessage) {
    checkAssertionError(WindowInterceptor
                          .init(trigger)
                          .process(handler.select(selectedFile)),
                        errorMessage);
  }

  private void checkMultiSelectionEnabled(boolean enabled, String message) {
    chooser.setMultiSelectionEnabled(enabled);
    checkOk(SHOW_OPEN_DIALOG_TRIGGER,
            FileChooserHandler.init().assertMultiSelectionEnabled(enabled));
    checkError(SHOW_OPEN_DIALOG_TRIGGER,
               FileChooserHandler.init().assertMultiSelectionEnabled(!enabled), javaHome, message);
  }
}
