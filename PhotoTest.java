package photo_renamer;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the implementation of the class Photo
 * @author Sara
 *
 */
public class PhotoTest {

	String workingDir = System.getProperty("user.dir");
	Photo testPhoto1;
	Photo testPhoto2;

	Tag testTag1;
	Tag testTag2;

	Tag[] testTagArray1 = new Tag[1];
	Tag[] testTagArray2 = new Tag[2];

	/**
	 * Sets up the tests before hand by creating photo instances and tag 
	 * instances to be used in the tests
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {

		testPhoto1 = new Photo("img.jpg", workingDir);
		testPhoto2 = new Photo("pic.jpg", workingDir);

		testTag1 = new Tag("Apple");
		testTag2 = new Tag("Banana");

		testTagArray1[0] = testTag1;

		testTagArray2[0] = testTag1;
		testTagArray2[1] = testTag2;
	}
	
	/**
	 * Clears up after the tests have finished by setting variables to null
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		testPhoto1 = null;
		testPhoto2 = null;

		testTag1 = null;
		testTag2 = null;

		testTagArray1 = null;
		testTagArray2 = null;
	}

	/**
	 * Tests adding a tag to the photo instance, checks if it is contained in the
	 * photos tag library, and if the name has been updated to include that tag
	 */
	@Test
	public void testAddTag() {
		
		testPhoto1.addTags(testTagArray1);
		assertTrue(testPhoto1.getTags().contains(testTag1.getName()));
		assertEquals(testPhoto1.getName(), "img@Apple.jpg");
		testPhoto1.addTags(testTagArray2);
		assertTrue(testPhoto1.getTags().contains(testTag2.getName()));
		assertTrue(testPhoto1.getTags().contains(testTag1.getName()));
		assertEquals(testPhoto1.getName(), "img@Apple@Banana.jpg");
	}

	/**
	 * Tests if deleting a tag from a photo deleted the tag name from the photos
	 * name, and deletes the tag from the photos tag library
	 */
	@Test
	public void testDeleteTag() {
		testPhoto1.addTags(testTagArray1);
		testPhoto1.addTags(testTagArray2);

		String expectedName = "img@Apple@Banana.jpg";

		assertEquals(expectedName, testPhoto1.getName());
		testPhoto1.deleteTag(testTag1);
		assertFalse(testPhoto1.getTags().contains(testTag1.getName()));

		expectedName = "img@Banana.jpg";
		assertEquals(expectedName, testPhoto1.getName());
	}

	/**
	 * Tests getting the name of a photo
	 */
	@Test
	public void testGetName() {
		String expectedName = "img.jpg";
		assertEquals(expectedName, testPhoto1.getName());
	}

	/**
	 * Tests getting a photos directory
	 */
	@Test
	public void testGetDir() {
		String expectedDir = System.getProperty("user.dir");
		String actualDir = testPhoto1.getDir();
		assertEquals(expectedDir, actualDir);

	}

	/**
	 * Tests getting the photos unique ID, and that the sequential numbering
	 * works
	 */
	@Test
	public void testGetId() {
		assertEquals(testPhoto1.getId(), 0);
		assertEquals(testPhoto2.getId(), 1);
	}


	/**
	 * Tests that after adding a tag to a photo and the previous photos name to
	 * the set of previous names, that you can get that information
	 */
	@Test
	public void testGetPrevNames() {

		String[] expectedNames = { "img.jpg", "img@Apple.jpg", "img@Apple@Banana.jpg" };
		assertFalse(testPhoto1.getPrevNames().contains(testPhoto1.getName()));
		testPhoto1.addTags(testTagArray1);
		testPhoto1.addTags(testTagArray2);
		assertTrue(testPhoto1.getPrevNames().contains(expectedNames[0]));
		assertTrue(testPhoto1.getPrevNames().contains(expectedNames[1]));
		assertFalse(testPhoto1.getPrevNames().contains(expectedNames[2]));
		

	}

	/**
	 * Tests getting the tags from a photos Tag library, and that it contains all
	 * the tags it should
	 */
	@Test
	public void testGetTags() {
		testPhoto1.addTags(testTagArray1);
		testPhoto1.addTags(testTagArray2);
		assertTrue(testPhoto1.getTags().contains(testTag1.getName()));
		assertTrue(testPhoto1.getTags().contains(testTag2.getName()));
	}

	/**
	 * Tests deleting all the tags will reset the name of the photo to its
	 * original name
	 */
	@Test
	public void testDeleteAllTags() {
		String[] expectedNames = {"img@Apple@Banana.jpg", "img.jpg"};
		testPhoto1.addTags(testTagArray1);
		testPhoto1.addTags(testTagArray2);
		assertEquals(expectedNames[0], testPhoto1.getName());
		testPhoto1.deleteAllTags();
		assertEquals(expectedNames[1], testPhoto1.getName());
	}

}
