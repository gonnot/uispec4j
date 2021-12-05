package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

public class ListSpinnerTest extends SpinnerTestCase {
  private ListSpinner listSpinner;

  @BeforeEach
  final protected void setUp() throws Exception {

    listSpinner = (ListSpinner)spinner;
  }

  public String getText() {
    return "1";
  }

  protected SpinnerModel createSpinnerModel() throws Exception {
    return DummySpinner.listModel("1", "2", "3");
  }

  protected Spinner createSpinner(JSpinner jSpinner) {
    return new ListSpinner(jSpinner);
  }

  @Test
  public void testContentsEquals() throws Exception {
    assertTrue(listSpinner.contentEquals("1", "2", "3"));
    assertFalse(listSpinner.contentEquals("2", "3", "1"));
  }

  @Test
  public void testUsingListSpinnerWithOtherModelThanSpinnerListModelThrowsAnException() throws Exception {
    try {
      new ListSpinner(new JSpinner());
      Assertions.fail();
    }
    catch (ItemNotFoundException e) {
      Assertions.assertEquals("Expected JSpinner using a SpinnerListModel", e.getMessage());
    }
  }
}
