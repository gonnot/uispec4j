module org.uispec4j {
  requires java.desktop;
  requires org.objectweb.asm;
  requires org.junit.jupiter.api;
  requires static org.testng;

  opens org.uispec4j;
  opens org.uispec4j.assertion;
  opens org.uispec4j.extension;
  opens org.uispec4j.finder;
  opens org.uispec4j.interception;
  opens org.uispec4j.interception.ui;
  opens org.uispec4j.utils;
  opens org.uispec4j.xml;

  exports org.uispec4j;
  exports org.uispec4j.assertion;
  exports org.uispec4j.extension;
  exports org.uispec4j.finder;
  exports org.uispec4j.interception;
  exports org.uispec4j.interception.ui;
  exports org.uispec4j.utils;
  exports org.uispec4j.xml;
}