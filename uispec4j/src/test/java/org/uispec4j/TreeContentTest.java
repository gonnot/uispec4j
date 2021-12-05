package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uispec4j.utils.AssertionFailureNotDetectedError;
import org.uispec4j.utils.DummyTreeCellRenderer;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class TreeContentTest extends TreeTestCase {
  @Test
  public void testContentCheck() {
    assertTrue(tree.contentEquals("""
                                  root
                                    child1
                                      child1_1
                                    child2"""));
  }

  @Test
  public void testContentCheckWithEmptyExpectedStringError() {
    checkContainmentError("  ",
                          "Expected tree description should not be empty ==> expected: <true> but was: <false>");
  }

  @Test
  public void testContentCheckWithErrors() {
    checkContainmentError("""
                          root
                            child1
                              error
                            child2""");
  }

  @Test
  public void testExpectedContentStringIsTrimmedInContainmentCheck() {
    assertTrue(tree.contentEquals("""
                                     root
                                    child1
                                      child1_1
                                    child2
                                  """));
    assertTrue(tree.contentEquals("""
                                    root
                                   child1
                                     child1_1
                                   child2
                                  \t\s""".indent(1)));
  }

  @Test
  public void testContentCheckWithPropertiesSpecification() {
    child1_1.setBold(true);
    child1_1.setColor(Color.RED);
    child2.setColor(Color.BLUE);
    assertTrue(tree.contentEquals("""
                                  root
                                    child1
                                      child1_1 #(bold,color=red)
                                    child2 #(color=blue)"""));
  }

  @Test
  public void testContentCheckWithMissingBoldnessError() {
    if (TestUtils.isMacOsX()) {
      //TODO: to be studied - on MacOS, the font of the renderer component does not accept to turn to bold (JDK issue?)
      return;
    }
    child1.setBold(true);
    checkContainmentError("""
                          root
                            child1
                              child1_1
                            child2""");
  }

  @Test
  public void testContentCheckWithBoldnessError() {
    child1.setBold(false);
    checkContainmentError("""
                          root
                            child1 #(bold)
                              child1_1
                            child2""");
  }

  @Test
  public void testContentCheckAcceptsBothNumericAndHumanReadableValues() {
    child2.setColor(Color.BLUE);
    assertTrue(tree.contentEquals("""
                                  root
                                    child1
                                      child1_1
                                    child2 #(color=blue)"""));
    assertTrue(tree.contentEquals("""
                                  root
                                    child1
                                      child1_1
                                    child2 #(color=0000ff)"""));
  }

  @Test
  public void testContentCheckWithMissingColorError() {
    child1.setColor(Color.BLUE);
    checkContainmentError("""
                          root
                            child1
                              child1_1
                            child2""");
  }

  @Test
  public void testCheckContentsWithColorNameError() throws Exception {
    child1_1.setColor(Color.BLUE);
    checkAssertionError(() -> assertTrue(tree.contentEquals("""
                                                            root
                                                              child1
                                                                child1_1 #(color=ERROR)
                                                              child2""")),
                        "'ERROR' does not seem to be a color");
  }

  @Test
  public void testAssertContains() {
    assertTrue(tree.contains("child1/child1_1"));
    try {
      assertTrue(tree.contains("child1/unknown"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("Could not find element 'child1/unknown'", e.getMessage());
    }
  }

  @Test
  public void testAssertContainsReallyChecksTheWholePath() {
    child1Node.add(new DefaultMutableTreeNode(new DummyTreeCellRenderer.UserObject("child1_2")));
    assertTrue(tree.contains("child1/child1_2"));
  }

  @Test
  public void testSeparatorCustomisation() {
    DefaultMutableTreeNode child3 =
      new DefaultMutableTreeNode(new DummyTreeCellRenderer.UserObject("child/3"));
    rootNode.add(child3);
    child3.add(new DefaultMutableTreeNode(new DummyTreeCellRenderer.UserObject("child/3 3")));
    tree.setSeparator(" | ");
    checkPath("child/3 | child/3 3");
    tree.setSeparator(" ## ");
    checkPath("child/3 ## child/3 3");
    tree.setSeparator("-");
    checkPath("child/3-child/3 3");
  }

  private void checkPath(String path) {
    tree.contains(path);
    tree.select(path);
    assertTrue(tree.selectionEquals(path));
    tree.clearSelection();
    tree.click(path);
    assertTrue(tree.selectionEquals(path));
  }

  @Test
  public void testSeparatorCanBeSpecifiedAtTreeCreationTime() {
    String previousSeparator = Tree.defaultSeparator;
    System.getProperties().remove(Tree.SEPARATOR_PROPERTY);
    Tree.setDefaultSeparator("-*-");

    Tree tree = new Tree(jTree);
    assertTrue(tree.contains("child1-*-child1_1"));
    System.setProperty(Tree.SEPARATOR_PROPERTY, "#");
    assertTrue(tree.contains("child1-*-child1_1"));
    tree = new Tree(jTree);
    assertTrue(tree.contains("child1#child1_1"));

    System.getProperties().remove(Tree.SEPARATOR_PROPERTY);
    Tree.setDefaultSeparator(previousSeparator);
  }

  @Test
  public void testSeparatorCannotBeEmpty() {
    try {
      tree.setSeparator("");
      throw new AssertionFailureNotDetectedError();
    }
    catch (IllegalArgumentException e) {
      Assertions.assertEquals("Separator must not be empty", e.getMessage());
    }
    try {
      Tree.setDefaultSeparator("");
      throw new AssertionFailureNotDetectedError();
    }
    catch (IllegalArgumentException e) {
      Assertions.assertEquals("Separator must not be empty", e.getMessage());
    }

    System.setProperty(Tree.SEPARATOR_PROPERTY, "");
    Tree tree = new Tree(jTree);
    Assertions.assertEquals("/", tree.getSeparator());
    System.getProperties().remove(Tree.SEPARATOR_PROPERTY);
  }

  @Test
  public void testSeparatorCannotBeNull() {
    try {
      tree.setSeparator(null);
      throw new AssertionFailureNotDetectedError();
    }
    catch (IllegalArgumentException e) {
      Assertions.assertEquals("Separator must not be null", e.getMessage());
    }
    try {
      Tree.setDefaultSeparator(null);
      throw new AssertionFailureNotDetectedError();
    }
    catch (IllegalArgumentException e) {
      Assertions.assertEquals("Separator must not be null", e.getMessage());
    }
  }

  @Test
  public void testPathDefinitionsGivePriorityToExactNames() {
    rootNode.add(new DefaultMutableTreeNode(new DummyTreeCellRenderer.UserObject("child1bis")));
    checkPath("child1/child1_1");
  }

  @Test
  public void testUsingASpecificConverter() {
    tree.setCellValueConverter(new DummyTreeCellValueConverter());
    assertTrue(tree.contentEquals("""
                                  _obj:root_
                                    _obj:child1_
                                      _obj:child1_1_
                                    _obj:child2_"""));
    assertTrue(tree.contains("_obj:child1_/_obj:child1_1_"));
  }

  @Test
  public void testUsingASpecificConverterForColor() {
    DummyTreeCellValueConverter converter = new DummyTreeCellValueConverter();
    converter.setRedFontPattern("child1");
    tree.setCellValueConverter(converter);
    assertTrue(tree.contentEquals("""
                                  _obj:root_
                                    _obj:child1_ #(color=FF0000)
                                      _obj:child1_1_ #(color=FF0000)
                                    _obj:child2_"""));
  }

  @Test
  public void testUsingASpecificConverterForTextStyle() {
    DummyTreeCellValueConverter converter = new DummyTreeCellValueConverter();
    converter.setBoldPattern("child");
    tree.setCellValueConverter(converter);
    assertTrue(tree.contentEquals("""
                                  _obj:root_
                                    _obj:child1_ #(bold)
                                      _obj:child1_1_ #(bold)
                                    _obj:child2_ #(bold)"""));
  }

  @Test
  public void testGetChildCount() {
    Assertions.assertEquals(2, tree.getChildCount(""));
    Assertions.assertEquals(1, tree.getChildCount("child1"));
  }

  @Test
  public void testCheckForegroundColor() {
    assertTrue(tree.foregroundEquals("", "black"));
    child1.setColor(new Color(250, 10, 10));
    assertTrue(tree.foregroundEquals("child1", "red"));
    try {
      assertTrue(tree.foregroundEquals("child1", "green"));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      Assertions.assertEquals("expected: <GREEN> but was: <FA0A0A>", e.getMessage());
    }
  }

  @Test
  public void testToString() {
    Assertions.assertEquals("""
                            root
                              child1
                                child1_1
                              child2
                            """,
                            tree.toString());
  }

  private void checkContainmentError(String expectedTree) {
    checkContainmentError(expectedTree, null);
  }

  private void checkContainmentError(String expectedTree, String message) {
    try {
      assertTrue(tree.contentEquals(expectedTree));
      throw new AssertionFailureNotDetectedError();
    }
    catch (AssertionError e) {
      if (message != null) {
        Assertions.assertEquals(message, e.getMessage());
      }
    }
  }
}
