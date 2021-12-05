package org.uispec4j.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class FileTestUtilsTest {
  @Test
  public void testDumpStringToFile() throws Exception {
    String content = "hello world" + Utils.LINE_SEPARATOR + "this a new line!";
    String filename = "example.txt";
    File file = FileTestUtils.dumpStringToFile(filename, content);
    Assertions.assertTrue(file.exists());
    Assertions.assertEquals(filename, file.getName());
    Assertions.assertEquals(content, FileTestUtils.loadTextFileToString(file));
  }
}
