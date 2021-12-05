package org.uispec4j.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtilsTest extends UnitTestCase {

  private final Date date = getDate(1974, Calendar.NOVEMBER, 23, 19, 55, 0);

  @Test
  public void testDate() throws Exception {
    Assertions.assertEquals(date.toString(), DateUtils.getDate("1974.11.23 19:55").toString());
  }

  @Test
  public void testStandard() throws Exception {
    Assertions.assertEquals("1974.11.23 19:55", DateUtils.getStandardDate(date));
  }

  @Test
  public void testFormatted() throws Exception {
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(new Locale("en", "us"));
    Assertions.assertEquals("November 23, 1974, 7:55 PM", getFormattedDate(date));
    Locale.setDefault(defaultLocale);
  }

  @Test
  public void testSetDateFormat() throws Exception {
    Date date = getDate(2003, Calendar.DECEMBER, 25, 12, 0, 0);

    DateUtils.setDateFormat(new SimpleDateFormat("yyyy.MM.dd"));
    Assertions.assertEquals("2003.12.25", DateUtils.getStandardDate(date));

    DateUtils.setDateFormat(DateUtils.DEFAULT_DATE_FORMAT);
    Assertions.assertEquals("2003.12.25 12:00", DateUtils.getStandardDate(date));
  }

  private static String getFormattedDate(Date date) {
    return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(date);
  }

  private static Date getDate(int year, int month, int day, int hour, int minute, int second) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month, day, hour, minute, second);
    return calendar.getTime();
  }
}
