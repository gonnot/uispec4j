package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uispec4j.utils.AssertionFailureNotDetectedError;
import org.uispec4j.utils.Counter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TreeClickingTest extends TreeTestCase {

  @Test
  public void testClickOnlyChangesTheSelectionOnce() throws Exception {
    checkClickOnlyChangesTheSelectionOnce(new DirectClicker());
    checkClickOnlyChangesTheSelectionOnce(new TriggerClicker());
  }

  private void checkClickOnlyChangesTheSelectionOnce(Clicker clicker) throws Exception {
    final Counter counter = new Counter();
    jTree.getSelectionModel().addTreeSelectionListener(e -> counter.increment());
    clicker.click("child1");
    Assertions.assertEquals(1, counter.getCount());
    clicker.click("child2");
    Assertions.assertEquals(2, counter.getCount());
    clicker.click("child1/child1_1");
    Assertions.assertEquals(3, counter.getCount());
  }

  @Test
  public void testClickFailsWhenAppliedOnNonExistingPath() throws Exception {
    checkClickFailsWhenAppliedOnNonExistingPath(new DirectClicker());
    checkClickFailsWhenAppliedOnNonExistingPath(new TriggerClicker());
  }

  private void checkClickFailsWhenAppliedOnNonExistingPath(Clicker clicker) throws Exception {
    final Counter counter = new Counter();
    jTree.getSelectionModel().addTreeSelectionListener(e -> counter.increment());
    try {
      clicker.click("child3");
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals(Tree.badTreePath("child3"), e.getMessage());
    }
    try {
      clicker.rightClick("child3");
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals(Tree.badTreePath("child3"), e.getMessage());
    }
    Assertions.assertEquals(0, counter.getCount());
  }

  @Test
  public void testRightClickBehaviour() throws Exception {
    checkRightClickBehaviour(new DirectClicker());
    checkRightClickBehaviour(new TriggerClicker());
  }

  private void checkRightClickBehaviour(Clicker clicker) throws Exception {
    final Counter counter = new Counter();
    jTree.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        counter.increment();
        int modifiers = e.getModifiersEx();
        Assertions.assertTrue((modifiers & MouseEvent.BUTTON3_DOWN_MASK) != 0);
      }
    });
    tree.select("child1");
    tree.addToSelection("child2");
    clicker.rightClick("child1");
    assertTrue(tree.selectionEquals(new String[]{"child1"}));
    Assertions.assertEquals(1, counter.getCount());

    tree.addToSelection("child2");
    clicker.rightClickInSelection();
    assertTrue(tree.selectionEquals(new String[]{"child1", "child2"}));
    Assertions.assertEquals(2, counter.getCount());

    clicker.rightClick("child1/child1_1");
    assertTrue(tree.selectionEquals("child1/child1_1"));
    Assertions.assertEquals(3, counter.getCount());

    clicker.rightClickInSelection();
    assertTrue(tree.selectionEquals("child1/child1_1"));
    Assertions.assertEquals(4, counter.getCount());
  }

  @Test
  public void testRightClickInSelectionNeedsASelection() throws Exception {
    checkRightClickInSelectionNeedsASelection(new DirectClicker());
    checkRightClickInSelectionNeedsASelection(new TriggerClicker());
  }

  private void checkRightClickInSelectionNeedsASelection(Clicker clicker) throws Exception {
    try {
      clicker.rightClickInSelection();
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("There is no current selection ==> expected: not <null>", e.getMessage());
    }
  }

  @Test
  public void testDoubleClickBehaviour_direct() throws Exception {
    checkDoubleClickBehaviour(new DirectClicker());
  }

  @Test
  public void testDoubleClickBehaviour_trigger() throws Exception {
    checkDoubleClickBehaviour(new TriggerClicker());
  }

  private void checkDoubleClickBehaviour(Clicker clicker) throws Exception {
    final Counter counter = new Counter();
    jTree.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        counter.increment();
        Assertions.assertEquals(2, e.getClickCount());
        int modifiers = e.getModifiers();
        Assertions.assertTrue((modifiers & MouseEvent.BUTTON1_MASK) != 0);
      }
    });
    tree.select("child1");
    tree.addToSelection("child2");
    clicker.doubleClick("child1");
    assertTrue(tree.selectionEquals(new String[]{"child1"}));
    Assertions.assertEquals(1, counter.getCount());

    clicker.doubleClick("child1/child1_1");
    assertTrue(tree.selectionEquals("child1/child1_1"));
    Assertions.assertEquals(2, counter.getCount());
  }

  private interface Clicker {
    void click(String path) throws Exception;

    void rightClick(String path) throws Exception;

    void rightClickInSelection() throws Exception;

    void doubleClick(String path) throws Exception;
  }

  private class DirectClicker implements Clicker {
    public void click(String path) {
      tree.click(path);
    }

    public void rightClick(String path) {
      tree.rightClick(path);
    }

    public void rightClickInSelection() {
      tree.rightClickInSelection();
    }

    public void doubleClick(String path) {
      tree.doubleClick(path);
    }
  }

  private class TriggerClicker implements Clicker {
    public void click(String path) throws Exception {
      tree.triggerClick(path).run();
    }

    public void rightClick(String path) throws Exception {
      tree.triggerRightClick(path).run();
    }

    public void rightClickInSelection() throws Exception {
      tree.triggerRightClickInSelection().run();
    }

    public void doubleClick(String path) throws Exception {
      tree.triggerDoubleClick(path).run();
    }
  }
}
