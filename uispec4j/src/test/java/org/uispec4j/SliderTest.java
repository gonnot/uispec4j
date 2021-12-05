package org.uispec4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uispec4j.assertion.Assertion;
import org.uispec4j.utils.Functor;
import org.uispec4j.xml.XmlAssert;

import javax.swing.*;
import java.util.Hashtable;

public class SliderTest extends UIComponentTestCase {
  private final JSlider jSlider = createTemperatureSlider();
  private final Slider slider = new Slider(jSlider);

  @Test
  public void testGetComponentTypeName() throws Exception {
    Assertions.assertEquals("slider", slider.getDescriptionTypeName());
  }

  @Test
  public void testGetDescription() throws Exception {
    XmlAssert.assertEquivalent("<slider name='my thermometer'/>", slider.getDescription());
  }

  @Test
  public void testFactory() throws Exception {
    checkFactory(createTemperatureSlider(), Slider.class);
  }

  protected UIComponent createComponent() {
    return slider;
  }

  @Test
  public void testLabels() throws Exception {
    checkAssertionFails(slider.labelsEqual("1", "2", "3", "4"),
                        "4 elements instead of 6\n" +
                        "Expected: [1,2,3,4],\n" +
                        "but was: [-10,0,10,20,30,40] ==> expected: <true> but was: <false>");

    assertTrue(slider.labelsEqual("-10", "0", "10", "20", "30", "40"));

    changeLabels();

    checkAssertionFails(slider.labelsEqual("1", "2", "3", "4", "5"),
                        "Unexpected element 'Very Cold'\n" +
                        "Expected: [1,2,3,4,5],\n" +
                        "but was: [Very Cold,Cold,Cool,Warm,Hot] ==> expected: <true> but was: <false>");
    checkAssertionFails(slider.labelsEqual("Very Cold", "Cold", "Cool", "Hot", "Warm"),
                        "Unexpected order in the collection\n" +
                        "Expected: [Very Cold,Cold,Cool,Hot,Warm],\n" +
                        "but was: [Very Cold,Cold,Cool,Warm,Hot] ==> expected: <true> but was: <false>");
    assertTrue(slider.labelsEqual("Very Cold", "Cold", "Cool", "Warm", "Hot"));

  }

  @Test
  public void testPositionCheckBasedOnLabels() throws Exception {
    changeLabels();

    jSlider.setValue(10);
    checkPosition(10, "Cool", "Warm");

    slider.setPosition("Warm");
    checkPosition(20, "Warm", "Cool");

    slider.setPosition("Cold");
    checkPosition(0, "Cold", "Hot");

    checkException(new Functor() {
      public void run() throws Exception {
        slider.setPosition("unexisting");
      }
    }, "No label 'unexisting' has been found");
    checkPosition(0, "Cold", "Hot");
  }

  @Test
  public void testRelativePosition() throws Exception {
    jSlider.setValue(16);
    Assertion positionIsMiddle = slider.relativePositionEquals(50);
    assertTrue(positionIsMiddle);

    jSlider.setValue(17);
    checkAssertionFails(positionIsMiddle, "Expected 50 but was 54 ==> expected: <true> but was: <false>");

    slider.setPrecision(5);
    assertTrue(positionIsMiddle);
  }

  @Test
  public void testSettingARelativePosition() throws Exception {
    slider.setRelativePosition(50);
    Assertions.assertEquals(15, jSlider.getValue());

    slider.setRelativePosition(0);
    Assertions.assertEquals(-10, jSlider.getValue());

    slider.setRelativePosition(100);
    Assertions.assertEquals(40, jSlider.getValue());

    checkAssertionError(
      new Functor() {
        public void run() throws Exception {
          slider.setRelativePosition(-1);
        }
      },
      "Value must be within [0..100]");

    checkAssertionError(
      new Functor() {
        public void run() throws Exception {
          slider.setRelativePosition(101);
        }
      },
      "Value must be within [0..100]");
  }

  private void checkPosition(int intValue, String correctLabel, String wrongLabel) throws Exception {
    Assertions.assertEquals(intValue, jSlider.getValue());
    assertTrue(slider.positionEquals(correctLabel));
    checkAssertionFails(slider.positionEquals(wrongLabel), "expected: <" + wrongLabel + "> but was: <" + correctLabel + ">");
  }

  private static JSlider createTemperatureSlider() {
    JSlider thermometer = new JSlider(JSlider.HORIZONTAL, -10, 40, 0);
    thermometer.setName("my thermometer");
    thermometer.setMajorTickSpacing(10);
    thermometer.setPaintLabels(true);
    thermometer.setPaintTicks(true);
    return thermometer;
  }

  private void changeLabels() {
    Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
    table.put(-10, new JLabel("Very Cold"));
    table.put(0, new JLabel("Cold"));
    table.put(10, new JLabel("Cool"));
    table.put(20, new JLabel("Warm"));
    table.put(40, new JLabel("Hot"));
    jSlider.setLabelTable(table);
  }
}