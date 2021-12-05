package org.uispec4j.xml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uispec4j.utils.UnitTestCase;

public class XmlEscapeTest extends UnitTestCase {
  @Test
  public void testConvertToXmlEntity() throws Exception {
    Assertions.assertEquals("sdfsdf&amp;sdfsdf", XmlEscape.convertToXmlWithEntities("sdfsdf&sdfsdf"));
    Assertions.assertEquals("sdfsdf&lt;sdf&gt;sdf", XmlEscape.convertToXmlWithEntities("sdfsdf<sdf>sdf"));
    Assertions.assertEquals("sdf&quot;sdf&quot;sdfsdf", XmlEscape.convertToXmlWithEntities("sdf\"sdf\"sdfsdf"));
    Assertions.assertEquals("sdfsdf&apos;sdfsdf", XmlEscape.convertToXmlWithEntities("sdfsdf'sdfsdf"));
    Assertions.assertEquals("&amp;&lt;&gt;&apos;&quot;", XmlEscape.convertToXmlWithEntities("&<>'\""));
  }
}