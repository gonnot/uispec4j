package org.uispec4j.assertion.testlibrairies;

import org.junit.jupiter.api.Assertions;

class JUnitLibrary implements TestLibrary {
  public void fail(String message) {
    Assertions.fail(message);
  }

  public void assertTrue(boolean b) {
    Assertions.assertTrue(b);
  }

  public void assertTrue(String message, boolean b) {
    Assertions.assertTrue(b, message);
  }

  public void assertFalse(String description, boolean b) {
    Assertions.assertFalse(b, description);
  }

  public void assertEquals(String expected, String actual) {
    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(Object expected, Object actual) {
    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(String message, String expected, String actual) {
    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(String message, Object expected, Object actual) {
    Assertions.assertEquals(expected, actual, message);
  }

  public void assertSame(String message, Object expected, Object actual) {
    Assertions.assertSame(expected, actual, message);
  }

  public void assertNotNull(String message, Object o) {
    Assertions.assertNotNull(o, message);
  }

  public void assertNull(String message, Object o) {
    Assertions.assertNull(o, message);
  }
}
