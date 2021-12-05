package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.Calendar;

import static org.uispec4j.DummySpinner.*;

public class DateSpinnerTest extends SpinnerTestCase {
  private DateSpinner dateSpinner;
  private final SpinnerDateModel model;

  public DateSpinnerTest() throws Exception {
    model = dateModel();
  }

  @BeforeEach
  final protected void setUp() throws Exception {
    dateSpinner = (DateSpinner)spinner;
  }

  public String getText() {
    return ((JSpinner.DateEditor)dateSpinner.getAwtComponent().getEditor())
      .getFormat().format(model.getDate());
  }

  protected SpinnerModel createSpinnerModel() throws Exception {
    return model;
  }

  protected Spinner createSpinner(JSpinner jSpinner) {
    return new DateSpinner(jSpinner);
  }

  @Test
  public void testStartAndEndDate() throws Exception {
    assertTrue(dateSpinner.startDateEquals(START_DATE));
    assertTrue(dateSpinner.endDateEquals(END_DATE));

    assertFalse(dateSpinner.startDateEquals(OTHER_DATE));
    assertFalse(dateSpinner.endDateEquals(OTHER_DATE));
  }

  @Test
  public void testCalendarFielsEquals() throws Exception {
    assertTrue(dateSpinner.calendarFieldsEquals(Calendar.MONTH));
    assertFalse(dateSpinner.calendarFieldsEquals(Calendar.YEAR));
  }

  @Test
  public void testUsingDateSpinnerWithOtherModelThanSpinnerDateModelThrowsAnException() throws Exception {
    try {
      new DateSpinner(new JSpinner());
      Assertions.fail();
    }
    catch (ItemNotFoundException e) {
      Assertions.assertEquals("Expected JSpinner using a SpinnerDateModel", e.getMessage());
    }
  }
}
