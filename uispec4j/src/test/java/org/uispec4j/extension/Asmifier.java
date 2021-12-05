package org.uispec4j.extension;

import org.objectweb.asm.util.ASMifier;
import org.uispec4j.Panel;

/**
 * Shortcut for dumping the ASM definition of a given class
 */
public class Asmifier {

  public static void main(String[] args) throws Exception {
    ASMifier.main(new String[]{Panel.class.getName()});
  }
}
