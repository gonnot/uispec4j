package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uispec4j.utils.UIComponentFactory;
import org.uispec4j.xml.XmlAssert;

import javax.swing.*;

public class TreeComponentTest extends UIComponentTestCase {
  @Test
  public void testGetComponentTypeName() throws Exception {
    Assertions.assertEquals("tree", UIComponentFactory.createUIComponent(new JTree()).getDescriptionTypeName());
  }

  @Test
  public void testGetDescription() throws Exception {
    JTree jTree = new JTree();
    jTree.setName("myTree");
    Tree tree = new Tree(jTree);
    XmlAssert.assertEquivalent("<tree name='myTree'/>", tree.getDescription());
  }

  @Test
  public void testFactory() throws Exception {
    checkFactory(new JButton(), Button.class);
  }

  protected UIComponent createComponent() {
    return new Tree(new JTree());
  }

}
