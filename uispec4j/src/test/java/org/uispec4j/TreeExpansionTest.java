package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uispec4j.utils.AssertionFailureNotDetectedError;

import javax.swing.tree.TreePath;

public class TreeExpansionTest extends TreeTestCase {

  @Test
  public void testExpandAndCollapsePath() {
    TreePath path = new TreePath(child1Node.getPath());
    jTree.expandPath(path);
    checkExpanded("child1", true);
    jTree.collapsePath(path);
    checkExpanded("child1", false);
  }

  @Test
  public void testAssertPathExpanded() {
    tree.expand("child1", true);
    checkExpanded("child1", true);
    tree.expand("child1", false);
    checkExpanded("child1", false);
  }

  @Test
  public void testAssertPathExpandedNeedsAValidPath() {
    try {
      tree.expand("unknown", true);
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals(Tree.badTreePath("unknown"), e.getMessage());
    }
  }

  private void checkExpanded(String path, boolean expanded) {
    assertEquals(expanded, tree.pathIsExpanded(path));
    try {
      assertEquals(!expanded, tree.pathIsExpanded(path));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError ignored) {
    }
  }
}
