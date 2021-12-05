package org.uispec4j.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test class for {@link ColorUtils}
 */
public class ColorUtilsTest extends UnitTestCase {

  @Test
  public void testAssertEqualsWithPrefix() throws Exception {
    ColorUtils.assertEquals("Message", "FF0000", Color.RED);
    ColorUtils.assertEquals("Message", "red", Color.RED);
    ColorUtils.assertEquals("Message", "red", new Color(0xDD1111));

    checkAssertEqualsError("Message", "112233", new Color(0x332211),
                           "Message - expected: <112233> but was: <332211>");
    checkAssertEqualsError("Message", ColorUtils.getColor("112233"), new Color(0x332211),
                           "Message - expected: <112233> but was: <332211>");
  }

  @Test
  public void testAssertEquals() throws Exception {
    ColorUtils.assertEquals("FF0000", Color.RED);
    ColorUtils.assertEquals("red", Color.RED);
    ColorUtils.assertEquals("red", new Color(0xDD1111));

    checkAssertEqualsError("112233", new Color(0x332211),
                           "expected: <112233> but was: <332211>");
  }

  @Test
  public void testInvalidArgumentsToAssertEquals() throws Exception {
    try {
      ColorUtils.assertEquals(2, Color.red);
      fail();
    }
    catch (IllegalArgumentException error) {
      Assertions.assertEquals(ColorUtils.UNEXPECTED_COLOR_CLASS, error.getMessage());
    }

    try {
      ColorUtils.assertEquals("Msg", 2, Color.red);
      fail();
    }
    catch (IllegalArgumentException error) {
      Assertions.assertEquals(ColorUtils.UNEXPECTED_COLOR_CLASS, error.getMessage());
    }
  }

  @Test
  public void testEqualsByHexaAndRGB() throws Exception {
    Assertions.assertTrue(ColorUtils.equals("FF0000", new Color(255, 0, 0)));
    Assertions.assertTrue(ColorUtils.equals("FF0000", new Color(0xFF0000)));
  }

  @Test
  public void testEqualsByName() throws Exception {
    Assertions.assertTrue(ColorUtils.equals("red", Color.RED));
    Assertions.assertTrue(ColorUtils.equals("RED", Color.RED));

    Assertions.assertFalse(ColorUtils.equals("blue", Color.RED));
    Assertions.assertTrue(ColorUtils.equals("darkGray", Color.darkGray));
  }

  @Test
  public void testEqualsWithAdditionalNamedColor() throws Exception {
    Assertions.assertTrue(ColorUtils.equals("darkGrey", ColorUtils.getColor("555555")));
    Assertions.assertTrue(ColorUtils.equals("darkRed", ColorUtils.getColor("550000")));
    Assertions.assertTrue(ColorUtils.equals("darkGreen", ColorUtils.getColor("005500")));
    Assertions.assertTrue(ColorUtils.equals("darkBlue", ColorUtils.getColor("000055")));

    Assertions.assertTrue(ColorUtils.equals("DARK_GREY", ColorUtils.getColor("555555")));
    Assertions.assertTrue(ColorUtils.equals("DARK_RED", ColorUtils.getColor("550000")));
    Assertions.assertTrue(ColorUtils.equals("DARK_GREEN", ColorUtils.getColor("005500")));
    Assertions.assertTrue(ColorUtils.equals("DARK_BLUE", ColorUtils.getColor("000055")));
  }

  @Test
  public void testEqualsByNameAndSimilarity() throws Exception {
    Assertions.assertFalse(ColorUtils.equals("blue", Color.red));
    Assertions.assertTrue(ColorUtils.equals("red", Color.red));
    Assertions.assertTrue(ColorUtils.equals("red", new Color(170, 5, 5)));
    Assertions.assertTrue(ColorUtils.equals("red", ColorUtils.getColor("ffc8c8")));
    Assertions.assertTrue(ColorUtils.equals("yellow", ColorUtils.getColor("ffffd0")));

    Assertions.assertTrue(ColorUtils.equals("lightGray", Color.gray));
    Assertions.assertFalse(ColorUtils.equals("white", Color.gray));
    Assertions.assertFalse(ColorUtils.equals("black", Color.gray));

    Assertions.assertFalse(ColorUtils.equals("red", ColorUtils.getColor("ffaaff")));
  }

  @Test
  public void testBadColorDescription() throws Exception {
    try {
      ColorUtils.equals("not a color", Color.red);
      fail("Should have failed because 'not a color' is not a color");
    }
    catch (IllegalArgumentException error) {
      Assertions.assertEquals("'not a color' does not seem to be a color", error.getMessage());
    }

    try {
      ColorUtils.equals(2, Color.red);
      fail();
    }
    catch (IllegalArgumentException error) {
      Assertions.assertEquals(ColorUtils.UNEXPECTED_COLOR_CLASS, error.getMessage());
    }
  }

  @Test
  public void testGetColor() throws Exception {
    Assertions.assertEquals(Color.red, ColorUtils.getColor("ff0000"));
    Assertions.assertEquals(Color.green, ColorUtils.getColor("00ff00"));
    Assertions.assertEquals(Color.blue, ColorUtils.getColor("0000ff"));
  }

  @Test
  public void testGetColorDescriptionByColor() throws Exception {
    Assertions.assertEquals("112233", ColorUtils.getColorDescription(new Color(0x112233)));

    Assertions.assertEquals("FF0000", ColorUtils.getColorDescription(Color.RED));
    Assertions.assertEquals("404040", ColorUtils.getColorDescription(Color.DARK_GRAY));
  }

  @Test
  public void testGetColorDescriptionByString() throws Exception {
    Assertions.assertEquals("112233", ColorUtils.getColorDescription("112233"));

    Assertions.assertEquals("RED", ColorUtils.getColorDescription("red"));
    Assertions.assertEquals("FF0000", ColorUtils.getColorDescription("FF0000"));
    Assertions.assertEquals("0000FF", ColorUtils.getColorDescription("0000ff"));
  }

  private void checkAssertEqualsError(String messagePrefix,
                                      Object expectedColor,
                                      Color actualColor,
                                      String errorMessage) {
    try {
      ColorUtils.assertEquals(messagePrefix, expectedColor, actualColor);
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals(errorMessage, e.getMessage());
    }
  }

  private void checkAssertEqualsError(String expected, Color actual, String errorMessage) {
    try {
      ColorUtils.assertEquals(expected, actual);
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals(errorMessage, e.getMessage());
    }
  }
}

