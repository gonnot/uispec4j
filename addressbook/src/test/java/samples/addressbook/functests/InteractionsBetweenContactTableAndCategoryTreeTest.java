package samples.addressbook.functests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InteractionsBetweenContactTableAndCategoryTreeTest extends AddressBookTestCase {
  @BeforeEach
  final protected void setUp() throws Exception {

    createCategory("", "friends");
    createCategory("", "work");
    createCategory("work", "team1");
    createCategory("work", "team2");
  }

  @Test
  public void testCreateContactWithoutCategorySelection() throws Exception {
    categoryTree.clearSelection();
    createContactsAndCheckTableContent();
  }

  @Test
  public void testCreateContactWithRootCategorySelected() throws Exception {
    categoryTree.selectRoot();
    createContactsAndCheckTableContent();
  }

  @Test
  public void testCreateContactWithASpecificCategorySelected() throws Exception {
    categoryTree.select("friends");

    createContact("Homer", "Simpson");
    createContact("Marge", "Simpson");

    assertThat(contactTable.contentEquals(new String[][]{
      {"Homer", "Simpson", "", "", ""},
      {"Marge", "Simpson", "", "", ""},
      }));

    checkTableContentForGivenCategory("",
                                      new String[][]{
                                        {"Homer", "Simpson", "", "", ""},
                                        {"Marge", "Simpson", "", "", ""},
                                        });

    checkTableContentForGivenCategory("friends",
                                      new String[][]{
                                        {"Homer", "Simpson", "", "", ""},
                                        {"Marge", "Simpson", "", "", ""},
                                        });

    checkTableContentForGivenCategory("work", new String[0][0]);

    checkTableContentForGivenCategory("friends",
                                      new String[][]{
                                        {"Homer", "Simpson", "", "", ""},
                                        {"Marge", "Simpson", "", "", ""},
                                        });

    categoryTree.clearSelection();
    assertThat(contactTable.contentEquals(new String[][]{
      {"Homer", "Simpson", "", "", ""},
      {"Marge", "Simpson", "", "", ""},
      }));
  }

  @Test
  public void testCreateContactsInAHierarchyOfCategories() throws Exception {
    categoryTree.select("work/team1");
    createContact("Luke", "Skywalker");
    createContact("Han", "Solo");

    categoryTree.select("work/team2");
    createContact("Boba", "Fett");

    categoryTree.select("friends");
    createContact("Mickey", "Mouse");

    checkTableContentForGivenCategory("",
                                      new String[][]{
                                        {"Luke", "Skywalker", "", "", ""},
                                        {"Han", "Solo", "", "", ""},
                                        {"Boba", "Fett", "", "", ""},
                                        {"Mickey", "Mouse", "", "", ""},
                                        });

    checkTableContentForGivenCategory("work",
                                      new String[][]{
                                        {"Luke", "Skywalker", "", "", ""},
                                        {"Han", "Solo", "", "", ""},
                                        {"Boba", "Fett", "", "", ""},
                                        });

    checkTableContentForGivenCategory("friends",
                                      new String[][]{
                                        {"Mickey", "Mouse", "", "", ""},
                                        });

    checkTableContentForGivenCategory("work/team1",
                                      new String[][]{
                                        {"Luke", "Skywalker", "", "", ""},
                                        {"Han", "Solo", "", "", ""},
                                        });

    checkTableContentForGivenCategory("work/team2",
                                      new String[][]{
                                        {"Boba", "Fett", "", "", ""},
                                        });

    categoryTree.clearSelection();
    assertThat(contactTable.contentEquals(new String[][]{
      {"Luke", "Skywalker", "", "", ""},
      {"Han", "Solo", "", "", ""},
      {"Boba", "Fett", "", "", ""},
      {"Mickey", "Mouse", "", "", ""},
      }));
  }

  private void createContactsAndCheckTableContent() {
    createContact("Homer", "Simpson");
    createContact("Marge", "Simpson");

    assertThat(contactTable.contentEquals(new String[][]{
      {"Homer", "Simpson", "", "", ""},
      {"Marge", "Simpson", "", "", ""},
      }));

    checkTableContentForGivenCategory("",
                                      new String[][]{
                                        {"Homer", "Simpson", "", "", ""},
                                        {"Marge", "Simpson", "", "", ""},
                                        });

    checkTableContentForGivenCategory("friends", new String[0][0]);
    checkTableContentForGivenCategory("work", new String[0][0]);
    checkTableContentForGivenCategory("work/team1", new String[0][0]);
    checkTableContentForGivenCategory("work/team2", new String[0][0]);
  }

  private void checkTableContentForGivenCategory(String categoryPath, String[][] expectedTable) {
    categoryTree.select(categoryPath);
    assertThat(contactTable.contentEquals(expectedTable));
  }
}
