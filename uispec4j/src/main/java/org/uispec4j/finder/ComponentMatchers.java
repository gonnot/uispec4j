package org.uispec4j.finder;

import org.uispec4j.utils.ComponentUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * Standard searching policies, implemented as {@link ComponentMatcher} objects.
 */
public class ComponentMatchers {

  /**
   * Matches components whose displayed name is exactly the same as the reference.
   */
  public static ComponentMatcher displayedNameIdentity(String reference) {
    return new DisplayedNameComponentMatcher(StringMatcher.identity(reference));
  }

  /**
   * Matches components whose displayed name is a substring of the reference.
   */
  public static ComponentMatcher displayedNameSubstring(String reference) {
    return new DisplayedNameComponentMatcher(StringMatcher.substring(reference));
  }

  /**
   * Matches components whose displayed name matches with the regexp reference.
   */
  public static ComponentMatcher displayedNameRegexp(String reference) {
    return new DisplayedNameComponentMatcher(StringMatcher.regexp(reference));
  }

  /**
   * Matches components whose inner name is exactly the same as the reference.
   */
  public static ComponentMatcher innerNameIdentity(String reference) {
    return new InnerNameComponentMatcher(StringMatcher.identity(reference));
  }

  /**
   * Matches components whose inner name is a substring of the reference.
   */
  public static ComponentMatcher innerNameSubstring(String reference) {
    return new InnerNameComponentMatcher(StringMatcher.substring(reference));
  }

  /**
   * Matches components whose inner name matches with the regexp reference.
   */
  public static ComponentMatcher innerNameRegexp(String reference) {
    return new InnerNameComponentMatcher(StringMatcher.regexp(reference));
  }

  /**
   * Matches components that are instances of the class.
   */
  public static <T extends Component> ComponentMatcher fromClass(final Class<T> swingClass) {
    return swingClass::isInstance;
  }

  /**
   * Matches components based on labels (very useful when dealing with forms).
   *
   * @see JLabel#setLabelFor(java.awt.Component)
   */
  public static ComponentMatcher componentLabelFor(final String labelName) {
    return new ComponentMatcher() {
      public boolean matches(Component awtComp) {
        if (!(awtComp instanceof JComponent)) {
          return false;
        }

        JLabel label = ComponentUtils.findLabelFor(awtComp);
        return label != null && displayedNameSubstring(labelName).matches(label);
      }
    };
  }

  /**
   * Matches components that match all its sub-matchers.
   */
  public static ComponentMatcher and(final ComponentMatcher... matchers) {
    return intersection(matchers);
  }

  /**
   * Matches components that match all its sub-matchers.
   *
   * @deprecated See {@link #and(ComponentMatcher...)}
   */
  public static ComponentMatcher intersection(final ComponentMatcher... matchers) {
    return new ComponentMatcher() {
      public boolean matches(Component component) {
        for (ComponentMatcher matcher : matchers) {
          if (!matcher.matches(component)) {
            return false;
          }
        }
        return true;
      }
    };
  }

  /**
   * Matches components that match at least one of its sub-matchers.
   */
  public static ComponentMatcher or(final ComponentMatcher... matchers) {
    return union(matchers);
  }

  /**
   * Matches components that match at least one of its sub-matchers.
   *
   * @deprecated See {@link #or(ComponentMatcher...)}
   */
  public static ComponentMatcher union(final ComponentMatcher... matchers) {
    return new ComponentMatcher() {
      public boolean matches(Component component) {
        for (ComponentMatcher matcher : matchers) {
          if (matcher.matches(component)) {
            return true;
          }
        }
        return false;
      }
    };
  }

  /**
   * Matches components rejected by the inner matcher.
   */
  public static ComponentMatcher not(final ComponentMatcher matcher) {
    return component -> !matcher.matches(component);
  }

  public static ComponentMatcher toolTipEquals(final String text) {
    return component -> {
      if (component instanceof JComponent jComponent) {
        String tooltip = jComponent.getToolTipText();
        return Objects.equals(text, tooltip);
      }
      return false;
    };
  }

  private static class DisplayedNameComponentMatcher implements ComponentMatcher {
    private final StringMatcher stringMatcher;

    public DisplayedNameComponentMatcher(StringMatcher stringMatcher) {
      this.stringMatcher = stringMatcher;
    }

    public boolean matches(Component component) {
      if (!ComponentUtils.hasDisplayedName(component.getClass())) {
        return false;
      }
      String displayedName = ComponentUtils.getDisplayedName(component);
      return stringMatcher.matches(displayedName);
    }
  }

  private static class InnerNameComponentMatcher implements ComponentMatcher {
    private final StringMatcher stringMatcher;

    public InnerNameComponentMatcher(StringMatcher stringMatcher) {
      this.stringMatcher = stringMatcher;
    }

    public boolean matches(Component component) {
      return stringMatcher.matches(component.getName());
    }
  }
}
