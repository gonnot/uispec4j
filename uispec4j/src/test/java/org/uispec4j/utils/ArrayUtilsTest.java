package org.uispec4j.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArrayUtilsTest extends UnitTestCase {
  @Test
  public void testToStringWithObjects() throws Exception {
    Assertions.assertEquals("[3,true,Hello]",
                            ArrayUtils.toString(new Object[]{3, Boolean.TRUE, "Hello"}));
  }

  @Test
  public void testToStringForArrays() throws Exception {
    Assertions.assertEquals("[]", ArrayUtils.toString(new String[0]));
    Assertions.assertEquals("[a]", ArrayUtils.toString(new String[]{"a"}));
    Assertions.assertEquals("[a,b]", ArrayUtils.toString(new String[]{"a", "b"}));
    Assertions.assertEquals("[a,b,c]", ArrayUtils.toString(new String[]{"a", "b", "c"}));

    Assertions.assertEquals("[a,b,[null,d],[e,[f,g]],h]", ArrayUtils.toString(new Object[]{
      "a",
      "b",
      new String[]{null, "d"},
      new Object[]{"e", new String[]{"f", "g"}},
      "h"
    }));
  }

  @Test
  public void testToStringForLists() throws Exception {
    List list = new ArrayList();
    Assertions.assertEquals("[]", ArrayUtils.toString(list));
    list.add("a");
    Assertions.assertEquals("[a]", ArrayUtils.toString(new String[]{"a"}));
    list.add("b");
    Assertions.assertEquals("[a,b]", ArrayUtils.toString(new String[]{"a", "b"}));
    list.add("c");
    Assertions.assertEquals("[a,b,c]", ArrayUtils.toString(new String[]{"a", "b", "c"}));
  }

  @Test
  public void testToStringWithIntegers() throws Exception {
    Assertions.assertEquals("[4,6,9]",
                            ArrayUtils.toString(new int[]{4, 6, 9}));
  }

  @Test
  public void testToStringForTwoDimensionalArrays() throws Exception {
    Assertions.assertEquals("[]", ArrayUtils.toString(new String[][]{}));
    Assertions.assertEquals("[[a]]", ArrayUtils.toString(new String[][]{{"a"}}));
    Assertions.assertEquals("[[a,\tb]\n [c,\td]]", ArrayUtils.toString(new String[][]{{"a", "b"}, {"c", "d"}}));
  }

  @Test
  public void testAssertEmptyForAnArray() throws Exception {
    ArrayUtils.assertEmpty((String[])null);
    ArrayUtils.assertEmpty(new Object[0]);
    try {
      ArrayUtils.assertEmpty(new String[]{"a"});
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Array should be empty but is [a]", e.getMessage());
    }
  }

  @Test
  public void testAssertEmpty() throws Exception {
    ArrayUtils.assertEmpty((List[])null);
    ArrayUtils.assertEmpty(Collections.EMPTY_LIST);
    try {
      List list = new ArrayList();
      list.add("a");
      ArrayUtils.assertEmpty(list);
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("List should be empty but is [a]", e.getMessage());
    }
  }
}
