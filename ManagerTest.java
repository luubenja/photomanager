package photo_renamer;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ManagerTest {

	Manager manager;
	Photo testPhoto;
	String photoDir;

	@Before
	public void setUp() throws Exception {
		photoDir = ".\\";
		manager = new Manager();
		testPhoto = manager.getPhotoInstance("test1.png", photoDir);
	}

	@After
	public void tearDown() throws Exception {
		HashSet<String> tagsInLibrary = new HashSet<>();

		tagsInLibrary.addAll(manager.getTags()); // deletes all tags from
													// library afterwards
													// *done to prevent
													// serializing from altering
													// other tests
		for (String key : tagsInLibrary) {
			manager.deleteTag(key);
		}

		manager = null;
		testPhoto = null;

	}

	/**
	 * Tests whether manager returns all of this tags in library correctly
	 */
	@Test
	public void testGetTags() {

		Set<String> tagSet = manager.getTags();
		assertNotNull(tagSet);
		assertTrue(tagSet.isEmpty());

		String[] testTags = { "Rain", "Snow", "Sun" };

		manager.addTag(testTags[0]);
		tagSet = manager.getTags();
		assertTrue(tagSet.contains(testTags[0]));
	}

	/**
	 * Tests whether manager returns correct tag objects from library
	 * 
	 * @throws IndexOutOfBoundsException
	 */
	@Test
	public void testGetTagInstances() throws IndexOutOfBoundsException {
		Collection<Tag> tagObjects = manager.getTagInstances();
		assertNotNull(tagObjects);
		assertTrue(tagObjects.isEmpty());

		String[] testTags = { "Rain", "Snow", "Sun" };

		for (String testTag : testTags) {
			manager.addTag(testTag);
		}

		tagObjects = manager.getTagInstances();
		Tag[] tagObjectArray = tagObjects.toArray(new Tag[tagObjects.size()]);

		for (int i = 0; i < testTags.length; i++) {
			assertEquals(testTags[i], tagObjectArray[i].getName());
		}
	}

	/**
	 * Tests whether manager returns correct photo objects from library
	 */
	@Test
	public void testGetPhotoInstances() {
		Collection<Photo> photoObjects = manager.getPhotoInstances(); // manager
																		// may
																		// have
																		// old
																		// photo
																		// Objects
																		// that
																		// were
																		// deserialized
																		// upon
																		// construction

		photoObjects = manager.getPhotoInstances();

		assertTrue(photoObjects.contains(testPhoto));

	}

	/**
	 * Tests whether manager can retrieve that tags that a photo in the library
	 * has
	 */
	@Test
	public void testGetTagsfromPhoto() {
		String[] testTags = { "Dolphin", "Panda", "Gecko" };

		Set<String> tagsFromPhoto = manager.getTagsfromPhoto(testPhoto.getName(), photoDir); // no
																								// tag
																								// case

		assertTrue(tagsFromPhoto.isEmpty());

		manager.setPhotoState(testPhoto.getName(), photoDir, testTags);
		tagsFromPhoto = manager.getTagsfromPhoto(testPhoto.getName(), photoDir);

		for (int i = 0; i < testTags.length; i++) {
			assertTrue(tagsFromPhoto.contains(testTags[i]));
		}
	}

	/**
	 * Tests whether Manager correctly changes the name of a file when given an
	 * array of tags
	 */
	@Test
	public void testSetPhotoState() {

		String[] testTags1 = { "Koala" };
		String[] testTags2 = { "Bear", "Puffin", "Platypus" };

		manager.setPhotoState(testPhoto.getName(), photoDir, testTags1);

		String expectedName = "test1@Koala.png"; // adding one tag
		assertEquals(expectedName, testPhoto.getName());
		assertTrue(testPhoto.getTags().contains(testTags1[0]));
		assertTrue(manager.getTags().contains(testTags1[0]));

		manager.setPhotoState(testPhoto.getName(), photoDir, testTags2); // adding
																			// multiple
																			// tags
		expectedName = "test1@Bear@Puffin@Platypus.png";
		assertEquals(expectedName, testPhoto.getName());
		for (int i = 0; i < testTags2.length; i++) { // ensures preservation of
														// order
			assertTrue(testPhoto.getTags().contains(testTags2[i]));
			assertTrue(manager.getTags().contains(testTags2[i]));
		}

		manager.setPhotoState(testPhoto.getName(), photoDir, new String[0]); // no
																				// tag
																				// case
																				// should
																				// return
																				// the
																				// original
																				// name

		expectedName = "test1.png";
		assertEquals(expectedName, testPhoto.getName());
		assertTrue(testPhoto.getTags().isEmpty());
	}

	/**
	 * Tests whether tags are properly added to the library
	 */
	@Test
	public void testAddTag() {

		String[] testTags = { "Beaver", "Seal" };
		manager.addTag(testTags[0]);
		assertEquals(1, manager.getTags().size());
		assertTrue(manager.getTags().contains(testTags[0]));

		manager.addTag("Beaver");
		assertEquals("Tag was added to library twice!", 1, manager.getTags().size()); // tests
																						// to
																						// see
																						// did
																						// not
																						// add
																						// Beaver
																						// twice

		manager.addTag("Seal");
		assertTrue(manager.getTags().contains(testTags[1]));
	}

	/**
	 * Tests whether tags are deleted from the library properly also checks that
	 * the corresponding photos that should have the deleted tag removed are
	 * properly updated
	 * 
	 * @throws IndexOutOfBoundsException
	 */
	@Test
	public void testDeleteTag() throws IndexOutOfBoundsException {
		String[] testTags = { "Armadillo", "Donkey", "Llama" };
		String testTag = "Coyote";

		manager.addTag(testTag);
		assertTrue(manager.getTags().contains(testTag)); // tests changes to
															// library solely
		manager.deleteTag(testTag);
		assertFalse(manager.getTags().contains(testTag));

		Photo testPhoto2 = manager.getPhotoInstance("test2.png", photoDir);

		manager.setPhotoState(testPhoto.getName(), photoDir, testTags);
		manager.setPhotoState(testPhoto2.getName(), photoDir, testTags);

		HashMap<String, Photo> filesToChange = manager.deleteTag("Donkey");
		Set<String> oldNames = filesToChange.keySet();

		String[] expectedNames = { "test1@Armadillo@Llama.png", "test2@Armadillo@Llama.png" };
		String[] expectedOldNames = { "test1@Armadillo@Donkey@Llama.png", "test2@Armadillo@Donkey@Llama.png" };
		String[] oldNamesArray = oldNames.toArray(new String[oldNames.size()]);

		assertArrayEquals(expectedOldNames, oldNamesArray);

		for (int i = 0; i < oldNamesArray.length; i++) { // tests that photos
															// with a deleted
															// tag have tag
															// removed
			Photo changedPhoto = filesToChange.get(oldNamesArray[i]);
			assertEquals(changedPhoto.getName(), expectedNames[i]);
		}
	}

	/**
	 * Tests if correctly retrieves photo object give its path key, also checks
	 * if new photos that are not found are added automatically
	 */
	@Test
	public void testGetPhotoInstance() {
		Photo testPhotoInstance = manager.getPhotoInstance(testPhoto.getName(), testPhoto.getDir());
		assertEquals(testPhotoInstance, testPhoto);
		Manager newManager = new Manager();

		assertTrue(newManager.getPhotoInstances().isEmpty());

		testPhotoInstance = newManager.getPhotoInstance(testPhoto.getName(), testPhoto.getDir());
		assertTrue(newManager.getPhotoInstances().contains(testPhotoInstance));
	}

	/**
	 * Tests if tag Objects are correctly retrieved based on their name keys
	 */
	@Test
	public void testGetTagInstance() {
		assertTrue(manager.getTags().isEmpty());
		String testTag = "Ox";

		manager.getTagInstance(testTag);
		assertTrue(manager.getTags().contains(testTag));
	}

	/**
	 * Tests if files are correctly serialized and that libraries are maintained
	 * upon re-instancing new Managers
	 */
	@Test
	public void testSerializeLibs() {
		manager.addTag("Whale");
		manager.serializeLibs();
		manager.getPhotoInstances();

		Manager newManager = new Manager();

		assertFalse(newManager.getPhotoInstances().isEmpty());
		assertTrue(newManager.getTags().contains("Whale"));

		File serialFile1 = new File("photoLibrary.ser");
		File serialFile2 = new File("tagLibrary.ser");

		serialFile1.delete(); // deletes the serializing files as to not affect
								// other tests
		serialFile2.delete();

	}
}
