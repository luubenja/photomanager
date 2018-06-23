package photo_renamer;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the implementation of class Tag
 * 
 * @author Ben,Sara
 *
 */
public class TagTest {

	String workingDir = System.getProperty("user.dir");
	Photo testPhoto;

	Tag testTag1;
	Tag testTag2;

	Tag[] testTagArray1 = new Tag[1];
	Tag[] testTagArray2 = new Tag[2];
	
	/**
	 * Sets up the tests
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {

		testPhoto = new Photo("img.jpg", workingDir);

		testTag1 = new Tag("Mango");
		testTag2 = new Tag("Ketchup");

		testTagArray1[0] = testTag1;
		testTagArray2[0] = testTag1;
		testTagArray2[1] = testTag2;
	}
	
	/**
	 * Clears up variables after the tests are finished
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {

		testTag1 = null;
		testTag2 = null;

		testTagArray1 = null;
		testTagArray2 = null;

	}

	/**
	 * Tests getting the photos with a tag from the photos with tag collection
	 */
	@Test
	public void testGetPhotosWithTag() {
		// checks if the observer works when adding a tag also
		testPhoto.addTags(testTagArray1);
		assertTrue(testTag1.getPhotosWithTag().contains(testPhoto));
		testPhoto.addTags(testTagArray2);
		assertTrue(testTag2.getPhotosWithTag().contains(testPhoto));

	}

	/**
	 * Test the ability to get a tags name from their instance
	 * 
	 */
	@Test
	public void testGetName() {

		String[] expectedNames = { "Mango", "Ketchup" };

		assertEquals(testTag1.getName(), expectedNames[0]);
		assertEquals(testTag2.getName(), expectedNames[1]);
	}
	/**
	 * Tests that the Observing is working between Tag and Photo
	 */
	@Test
	public void testUpdate() {

		testPhoto.addTags(testTagArray2);
		testPhoto.deleteTag(testTag1);
		
		// checks if the observer works while deleting one tag also
		assertFalse(testTag1.getPhotosWithTag().contains(testPhoto));
		assertTrue(testTag2.getPhotosWithTag().contains(testPhoto));

		testPhoto.addTags(testTagArray1);
		
		// Also checks that the observer works when deleting all tags
		testPhoto.deleteAllTags();
		assertFalse(testTag1.getPhotosWithTag().contains(testPhoto));
		assertFalse(testTag2.getPhotosWithTag().contains(testPhoto));

		assertTrue(testPhoto.getTags().isEmpty());
	}
}
