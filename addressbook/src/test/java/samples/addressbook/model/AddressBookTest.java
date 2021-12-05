package samples.addressbook.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import samples.addressbook.model.events.DummyBookListener;
import samples.addressbook.model.events.DummyCategoryListener;
import samples.addressbook.model.events.DummyContactListener;
import samples.addressbook.model.exceptions.NameAlreadyInUseException;
import samples.utils.ArrayUtils;
import samples.utils.UnitTestCase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;

public class AddressBookTest extends UnitTestCase {
  private final AddressBook book = new AddressBook();

  @Test
  public void testCreatingAndChangingAContactDispatchesEvents() throws Exception {
    DummyContactListener listener = DummyContactListener.register(book);

    Contact contact = book.createContact();
    listener.assertEquals("<log>" +
                          "  <create/>" +
                          "</log>");

    for (int i = 0; i < Contact.ALL_FIELDS.length; i++) {
      Contact.Field field = Contact.ALL_FIELDS[i];
      assertNull(contact.getValue(field));
      String value = "value" + i;
      book.changeContact(contact, field, value);
      listener.assertEquals("<log>" +
                            "  <change field='" + field + "' value='" + value + "'/>" +
                            "</log>");
      Assertions.assertEquals(value, contact.getValue(field));
    }
  }

  @Test
  public void testDeletingContactDispatchesAnEvent() throws Exception {
    Contact john = createContact("Smith", "John");
    Contact sandra = createContact("Bullock", "Sandra");

    DummyContactListener listener = DummyContactListener.register(book);
    book.removeContact(john);
    ArrayUtils.assertEquals(new Contact[]{sandra}, book.getContacts());
    listener.assertEquals("<log>" +
                          "  <delete name='Smith'/>" +
                          "</log>");
  }

  @Test
  public void testClearingTheAddressBookDispatchesAnEvent() throws Exception {
    Category rootCategory = book.getRootCategory();
    book.createCategory(rootCategory, "friends");
    createContact("Smith", "John");
    createContact("Bullock", "Sandra");

    DummyBookListener listener = DummyBookListener.register(book);
    book.reset();
    ArrayUtils.assertEquals(new Contact[0], book.getContacts());
    ArrayUtils.assertEquals(new Category[0], rootCategory.getChildren());

    listener.assertEquals("<log>" +
                          "  <bookReset/>" +
                          "</log>");
  }

  private Contact createContact(String lastName, String firstName) {
    Contact contact = book.createContact();
    book.changeContact(contact, Contact.Field.LAST_NAME, lastName);
    book.changeContact(contact, Contact.Field.FIRST_NAME, firstName);
    return contact;
  }

  @Test
  public void testCreatingCategories() throws Exception {
    Category root = book.getRootCategory();
    assertNull(root.getParent());
    Assertions.assertEquals("All", root.getName());
    Assertions.assertEquals("All", root.getPath());

    DummyCategoryListener listener = DummyCategoryListener.register(book);

    Category category1 = createAndCheckCategory(root, "1", "All/1", new String[]{"1"}, listener);
    createAndCheckCategory(category1, "1", "All/1/1", new String[]{"1"}, listener);
    Category category12 = createAndCheckCategory(category1, "2", "All/1/2", new String[]{"1", "2"}, listener);
    createAndCheckCategory(category12, "1", "All/1/2/1", new String[]{"1"}, listener);
  }

  @Test
  public void testCategoryPaths() throws Exception {
    Category root = book.getRootCategory();
    Category cat1 = book.createCategory(root, "Cat1");
    Category cat11 = book.createCategory(cat1, "Cat11");

    Assertions.assertEquals("All/Cat1", cat1.getPath());
    Assertions.assertEquals("All/Cat1/Cat11", cat11.getPath());
  }

  private Category createAndCheckCategory(Category parent,
                                          String name,
                                          String path,
                                          String[] newChildrenList,
                                          DummyCategoryListener listener) throws NameAlreadyInUseException {
    Category category = book.createCategory(parent, name);
    Assertions.assertSame(parent, category.getParent());
    Assertions.assertTrue(parent.isSameOrAncestorOf(category));
    Assertions.assertTrue(book.getRootCategory().isSameOrAncestorOf(category));
    Assertions.assertEquals(name, category.getName());
    ArrayUtils.assertEquals(newChildrenList, getChildrenCategories(parent));
    Assertions.assertEquals(path, category.getPath());
    listener.assertEquals("<log>" +
                          "  <create category='" + path + "'/>" +
                          "</log>");
    return category;
  }

  @Test
  public void testCreatingACategoryWithAnAlreadyUsedName() throws Exception {
    Category root = book.getRootCategory();
    book.createCategory(root, "1");
    try {
      book.createCategory(root, "1");
      Assertions.fail();
    }
    catch (NameAlreadyInUseException e) {
    }
  }

  @Test
  public void testCreatingContactUnderAGivenCategory() throws Exception {
    Category root = book.getRootCategory();
    Category cat1 = book.createCategory(root, "1");
    Category cat11 = book.createCategory(cat1, "1.1");

    DummyContactListener listener = DummyContactListener.register(book);

    Contact contact1 = book.createContact();
    ArrayUtils.assertEquals(new Category[0], contact1.getCategories());
    listener.assertEquals("<log>" +
                          "  <create/>" +
                          "</log>");

    Contact contact2 = book.createContact(root);
    ArrayUtils.assertEquals(new Category[0], contact2.getCategories());
    listener.assertEquals("<log>" +
                          "  <create/>" +
                          "</log>");
    ArrayUtils.assertEquals(new Contact[]{contact1, contact2}, book.getContacts(book.getRootCategory()));

    Contact contact3 = book.createContact(cat1);
    ArrayUtils.assertEquals(new Category[]{cat1}, contact3.getCategories());
    listener.assertEquals("<log>" +
                          "  <create/>" +
                          "  <categoriesChange firstName='null' categories='[1]'/>" +
                          "</log>");

    Contact contact4 = book.createContact(cat11);
    ArrayUtils.assertEquals(new Category[]{cat11}, contact4.getCategories());
    listener.assertEquals("<log>" +
                          "  <create/>" +
                          "  <categoriesChange firstName='null' categories='[1.1]'/>" +
                          "</log>");
  }

  @Test
  public void testAddingContactsToCategories() throws Exception {
    Category root = book.getRootCategory();
    Category cat1 = book.createCategory(root, "1");
    Category cat11 = book.createCategory(cat1, "1.1");
    Category cat2 = book.createCategory(root, "2");

    Contact contact = book.createContact();
    book.changeContact(contact, Contact.Field.FIRST_NAME, "sabrina");

    DummyContactListener listener = DummyContactListener.register(book);

    book.addCategory(contact, cat2);
    ArrayUtils.assertEquals(new Category[]{cat2}, contact.getCategories());
    listener.assertEquals("<log>" +
                          "  <categoriesChange firstName='sabrina' categories='[2]'/>" +
                          "</log>");

    book.addCategory(contact, cat11);
    ArrayUtils.assertEquals(new Category[]{cat2, cat11}, contact.getCategories());
    listener.assertEquals("<log>" +
                          "  <categoriesChange firstName='sabrina' categories='[2,1.1]'/>" +
                          "</log>");
  }

  @Test
  public void testAddingTheRootCategoryDoesNothing() throws Exception {
    Contact contact = book.createContact();
    book.addCategory(contact, book.getRootCategory());
    ArrayUtils.assertEmpty(contact.getCategories());
  }

  @Test
  public void testCategoriesAreOnlyStoredOnce() throws Exception {
    Contact contact = book.createContact();
    Category category = book.createCategory(book.getRootCategory(), "cat");
    book.addCategory(contact, category);
    book.addCategory(contact, category);
    ArrayUtils.assertEquals(new Category[]{category}, contact.getCategories());
  }

  @Test
  public void testAddingAContactRemovesAllSubcontacts() throws Exception {
    Category root = book.getRootCategory();
    Category cat1 = book.createCategory(root, "1");
    Category cat11 = book.createCategory(cat1, "1.1");
    Category cat2 = book.createCategory(root, "2");

    Contact contact = book.createContact();
    book.changeContact(contact, Contact.Field.FIRST_NAME, "cat");

    book.addCategory(contact, cat2);
    book.addCategory(contact, cat11);
    ArrayUtils.assertEquals(new Category[]{cat2, cat11}, contact.getCategories());

    DummyContactListener listener = DummyContactListener.register(book);

    book.addCategory(contact, cat1);
    ArrayUtils.assertEquals(new Category[]{cat2, cat1}, contact.getCategories());
    listener.assertEquals("<log>" +
                          "  <categoriesChange firstName='cat' categories='[2,1]'/>" +
                          "</log>");
  }

  @Test
  public void testFilteringContactsWithCategories() throws Exception {
    Category root = book.getRootCategory();
    Category cat1 = book.createCategory(root, "1");
    Category cat11 = book.createCategory(cat1, "1.1");
    Category cat2a = book.createCategory(root, "2a");
    Category cat2b = book.createCategory(root, "2b");

    Contact contact1 = book.createContact();
    book.addCategory(contact1, cat11);
    Contact contact2 = book.createContact();
    book.addCategory(contact2, cat2a);
    book.addCategory(contact2, cat2b);
    Contact contact2a = book.createContact();
    book.addCategory(contact2a, cat2a);

    ArrayUtils.assertEquals(new Contact[]{contact1, contact2, contact2a}, book.getContacts());
    ArrayUtils.assertEquals(new Contact[]{contact1, contact2, contact2a}, book.getContacts(root));
    ArrayUtils.assertEquals(new Contact[]{contact1}, book.getContacts(cat11));
    ArrayUtils.assertEquals(new Contact[]{contact1}, book.getContacts(cat1));
    ArrayUtils.assertEquals(new Contact[]{contact2, contact2a}, book.getContacts(cat2a));
    ArrayUtils.assertEquals(new Contact[]{contact2}, book.getContacts(cat2b));
  }

  private String[] getChildrenCategories(Category category) {
    List names = new ArrayList();
    List children = category.getChildren();
    for (Iterator iterator = children.iterator(); iterator.hasNext(); ) {
      Category child = (Category)iterator.next();
      names.add(child.getName());
    }
    return (String[])names.toArray(new String[names.size()]);
  }
}
