package samples.utils;

import org.junit.jupiter.api.Assertions;

import java.util.*;

public class ArrayUtils {

  public static String toString(Object[] objects) {
    StringBuffer buffer = new StringBuffer();
    appendLine(buffer, objects, ",");
    return buffer.toString();
  }

  public static String toVerticalString(Object[] array) {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < array.length; i++) {
      buffer.append(array[i]);
      if (i < array.length - 1) {
        buffer.append(Utils.LINE_SEPARATOR);
      }
    }
    return buffer.toString();
  }

  private static Integer[] toArray(int[] actual) {
    Integer[] result = new Integer[actual.length];
    for (int i = 0; i < actual.length; i++) {
      result[i] = actual[i];
    }
    return result;
  }

  public static String toString(Object[][] objects) {
    StringBuffer buffer = new StringBuffer();
    buffer.append('[');
    for (int i = 0; i < objects.length; i++) {
      if (i > 0) {
        buffer.append('\n');
        buffer.append(' ');
      }
      appendLine(buffer, objects[i], ",\t");
    }
    buffer.append(']');
    return buffer.toString();
  }

  private static void appendLine(StringBuffer buffer, Object[] objects, String separator) {
    buffer.append('[');
    for (int i = 0; i < objects.length; i++) {
      if (objects[i] == null) {
        buffer.append("null");
      }
      else if (objects[i].getClass().isArray()) {
        buffer.append(toString((Object[])objects[i]));
      }
      else {
        buffer.append(objects[i]);
      }
      if (i < (objects.length - 1)) {
        buffer.append(separator);
      }
    }
    buffer.append(']');
  }

  public static String toString(int[] ints) {
    StringBuffer buffer = new StringBuffer();
    buffer.append('[');
    for (int i = 0; i < ints.length; i++) {
      buffer.append(ints[i]);
      if (i < (ints.length - 1)) {
        buffer.append(',');
      }
    }
    buffer.append(']');
    return buffer.toString();
  }

  public static String toString(List list) {
    StringBuffer buffer = new StringBuffer();
    buffer.append('[');
    for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
      buffer.append(iterator.next());
      if (iterator.hasNext()) {
        buffer.append(',');
      }
    }
    buffer.append(']');
    return buffer.toString();
  }

  public static Boolean[][] toBooleanObjects(boolean[][] source) {
    Boolean[][] result = new Boolean[source.length][];
    for (int i = 0; i < result.length; i++) {
      result[i] = new Boolean[source[i].length];
      for (int j = 0; j < result[i].length; j++) {
        result[i][j] = Boolean.valueOf(source[i][j]);
      }
    }
    return result;
  }

  public static void assertEquals(String message, Object[] expected, Object[] actual) {
    if (!Arrays.equals(expected, actual)) {
      fail(message, expected, actual);
    }
  }

  private static void fail(String message, Object[] expected, Object[] actual) {
    boolean verticalDisplay = expected.length > 5 || actual.length > 5;
    if (verticalDisplay) {
      Assertions.assertEquals(message, toVerticalString(expected), toVerticalString(actual));
    }
    else {
      String prefix = message != null ? message + "\n" : "";
      Assertions.fail(prefix +
                      "Expected: " + toString(expected) +
                      "\nActual:   " + toString(actual));
    }
  }

  public static void assertEquals(Object[] expected, Object[] actual) {
    if (actual == null) {
      Assertions.assertNull(expected, "Actual array is not null");
    }
    if (!Arrays.equals(expected, actual)) {
      fail(null, expected, actual);
    }
  }

  public static void assertEquals(Object[][] expected, Object[][] actual) {
    Assertions.assertEquals(expected.length, actual.length);
    for (int i = 0; i < expected.length; i++) {
      assertEquals("Error at row " + i + ":", expected[i], actual[i]);
    }
  }

  public static void assertEquals(int[] expected, int[] actual) {
    if (!Arrays.equals(expected, actual)) {
      fail(null, toArray(expected), toArray(actual));
    }
  }

  public static void assertEquals(Object[] expectedArray, List list) {
    assertEquals(expectedArray, list.iterator());
  }

  public static void assertEquals(Object[] expectedArray, Iterator actualIterator) {
    int index = 0;
    List actualList = new ArrayList();
    while (actualIterator.hasNext()) {
      if (index >= expectedArray.length) {
        for (Iterator iterator = actualIterator; iterator.hasNext(); ) {
          actualList.add(iterator.next());
        }
        Assertions.fail("The iterator contains too many elements: expected: " +
                        toString(expectedArray) + " but was: " + actualList);
      }
      Object obj = actualIterator.next();
      actualList.add(obj);
      if (!obj.equals(expectedArray[index])) {
        Assertions.fail("Mismatch at index " + index + ". expected: " + expectedArray[index] +
                        " but was: " + obj);
      }
      index++;
    }
    if (index < expectedArray.length) {
      fail("Several elements are missing from the iterator", expectedArray, actualList.toArray());
    }
  }

  public static void orderedCompare(Object[][] expectedData, Object[][] actualData) {
    compareCollection(actualData, expectedData);
  }

  private static void compareCollection(Object[][] actualData, Object[][] expectedData) {
    Collection<String> actual = new ArrayList<>();
    for (Object[] actualDatum : actualData) {
      actual.add(toString(actualDatum));
    }
    Collection<String> expected = new ArrayList<>();
    for (Object[] expectedDatum : expectedData) {
      expected.add(toString(expectedDatum));
    }
    Assertions.assertEquals(expected, actual);
  }

  public static void assertEmpty(Object[] array) {
    if ((array != null) && (array.length > 0)) {
      Assertions.fail("Array should be empty but is " + toString(array));
    }
  }

  public static void assertEmpty(List list) {
    if ((list != null) && (!list.isEmpty())) {
      Assertions.fail("List should be empty but is " + toString(list));
    }
  }
}

