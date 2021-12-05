package org.uispec4j.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtilsTest extends UnitTestCase {
  @Test
  public void test() throws Exception {
    checkNormalize("text", 4, "text");
    checkNormalize("text  ", 6, "text");
    checkNormalize("text    ", 8, "text");
    checkNormalize("te", 3, "text");
    checkNormalize("", 0, "text");
    checkNormalize("", -1, "text");
  }

  @Test
  public void testAssertSetEquals() throws Exception {
    final Item bag = new Item("bag");
    final Item bike = new Item("bike");
    final Item motorcycle = new Item("motorcycle");
    final Object[] collection = new Object[]{bike, motorcycle, bag};

    checkAssertionError(() -> Utils.assertSetEquals(new Object[]{bag, motorcycle}, collection, new ItemStringifier()),
                        """
                        2 elements instead of 3
                        Expected: [bag,motorcycle],
                        but was: [bike,motorcycle,bag] ==> expected: <true> but was: <false>""");
    checkAssertionError(() -> Utils.assertSetEquals(new Object[]{bag, motorcycle, bag}, collection, new ItemStringifier()),
                        """
                        Unexpected element 'bike'
                        Expected: [bag,motorcycle,bag],
                        but was: [bike,motorcycle,bag] ==> expected: <true> but was: <false>""");
    Utils.assertSetEquals(new Object[]{bag, bike, motorcycle}, collection, new ItemStringifier());
    Utils.assertSetEquals(new Object[]{bike, motorcycle, bag}, collection, new ItemStringifier());
  }

  @Test
  public void testAssertEquals() throws Exception {
    final Item bag = new Item("bag");
    final Item bike = new Item("bike");
    final Item motorcycle = new Item("motorcycle");
    final Object[] collection = new Object[]{bike, motorcycle, bag};

    checkAssertionError(() -> Utils.assertEquals(new Object[]{bag, motorcycle}, collection, new ItemStringifier()),
                        """
                        2 elements instead of 3
                        Expected: [bag,motorcycle],
                        but was: [bike,motorcycle,bag] ==> expected: <true> but was: <false>""");
    checkAssertionError(() -> Utils.assertEquals(new Object[]{bag, motorcycle, bag}, collection, new ItemStringifier()),
                        """
                        Unexpected element 'bike'
                        Expected: [bag,motorcycle,bag],
                        but was: [bike,motorcycle,bag] ==> expected: <true> but was: <false>""");
    checkAssertionError(() -> Utils.assertEquals(new Object[]{bag, bike, motorcycle}, collection, new ItemStringifier()),
                        """
                        Unexpected order in the collection
                        Expected: [bag,bike,motorcycle],
                        but was: [bike,motorcycle,bag] ==> expected: <true> but was: <false>""");
    Utils.assertEquals(new Object[]{bike, motorcycle, bag}, collection, new ItemStringifier());
  }

  private void checkNormalize(String result, int size, String input) {
    Assertions.assertEquals(result, Utils.normalize(input, size));
  }

  private static class Item {
    private final String description;

    public Item(String description) {
      this.description = description;
    }

    public String getDescription() {
      return description;
    }
  }

  private static class ItemStringifier implements Stringifier {
    public String toString(Object obj) {
      return ((Item)obj).getDescription();
    }
  }
}
