package photo_renamer;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the class tag Action
 * 
 * @author Ben
 *
 */
public class TagActionTest {

	private Manager manager;
	private TagAction tagActionTest;
	private Photo testPhoto;

	/**
	 * Sets up for the tests by creating the needed manager and tag action
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		manager = new Manager();
		tagActionTest = new TagAction(manager);
		manager.addObserver(tagActionTest);

		testPhoto = manager.getPhotoInstance("test1.jpg", ".\\");
	}

	/**
	 * Cleans up after the tests by deleting all the tags added and setting the
	 * manager and tagAction to null
	 * 
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {

		HashSet<String> tagsInLibrary = new HashSet<>();

		tagsInLibrary.addAll(manager.getTags());

		for (String key : tagsInLibrary) { //removes all the tags from library
			manager.deleteTag(key);
		}
		manager = null;
		tagActionTest = null;
	}
	
	/**
	 * Tests that the constructor stores all correct parameters
	 * @throws IndexOutOfBoundsException
	 */
	@Test
	public void testTagAction() throws IndexOutOfBoundsException {
		manager.addTag("Pluto");
		tagActionTest = new TagAction(manager);
		Set<String> managerTags = manager.getTags();
		String[] actionTags = tagActionTest.getOptions();
		assertTrue("TagAction does not have all tags that manager has", managerTags.contains(actionTags[0]));
	}
	/**
	 * Tests that when reset, selected options are erased and that changing the 
	 * working photo sets the selected options to whatever tags are already
	 * Existent in the name.
	 */
	@Test
	public void testReset() {

		String[] tags = { "Mercury", "Venus", "Mars" };

		for (int i = 0; i < tags.length; i++) {
			manager.addTag(tags[i]);
		}

		tagActionTest.reset(null);
		assertTrue(tagActionTest.getSelectedOptions().isEmpty());

		manager.setPhotoState(testPhoto.getName(), testPhoto.getDir(), tags);
		tagActionTest.reset(testPhoto);

		HashSet<String> actionTags = tagActionTest.getSelectedOptions();

		for (int i = 0; i < tags.length; i++) {			//resetting to a photo with tags should
			assertTrue(actionTags.contains(tags[i]));	//set selected options to those tags
		}

	}

	/**
	 * Tests if the action properly returns the correct options
	 */
	@Test
	public void testGetOptions() {

		assertTrue(tagActionTest.getOptions().length == 0);

		String[] tags = { "Neptune", "Jupiter", "Mars", "Venus" };

		for (int i = 0; i < tags.length; i++) {
			manager.addTag(tags[i]);
		}

		String[] actionTags = tagActionTest.getOptions();

		assertArrayEquals(tags, actionTags);

	}
	/**
	 * Tests whether the action gets the correct selected options
	 */
	@Test
	public void testGetSelectedOptions() {

		String[] tags = { "Neptune", "Jupiter" };

		manager.addTag(tags[0]);
		manager.addTag(tags[1]);

		manager.setPhotoState(testPhoto.getName(), testPhoto.getDir(), tags);

		tagActionTest.reset(testPhoto);
		HashSet<String> selectedTags = tagActionTest.getSelectedOptions();

		for (int i = 0; i < tags.length; i++) {
			assertTrue(selectedTags.contains(tags[i]));
		}

	}

	/**
	 * Tests if the action is correctly updated when the user makes a decision
	 * in regards choosing from options
	 * @throws IndexOutOfBoundsException
	 */
	@Test
	public void testUpdateInt() throws IndexOutOfBoundsException {
		String[] tags = { "Neptune", "Jupiter" };

		manager.addTag(tags[0]);
		manager.addTag(tags[1]);

		manager.setPhotoState(testPhoto.getName(), testPhoto.getDir(), tags);

		assertTrue("Was able to select tags before selecting a working photo!",
				tagActionTest.getSelectedOptions().isEmpty());

		HashSet<String> selectedTags;

		tagActionTest.reset(testPhoto);
		tagActionTest.update(0); //if user selects the first option

		selectedTags = tagActionTest.getSelectedOptions();

		assertFalse(selectedTags.contains(tags[0]));

		tagActionTest.update(0);
		tagActionTest.update(1);
		tagActionTest.update(1);

		selectedTags = tagActionTest.getSelectedOptions();

		assertTrue(selectedTags.contains(tags[0]));
		assertTrue(selectedTags.contains(tags[1]));
	}
	
	/**
	 * Tests if correctly updated options and working photo information
	 * when notified by Manager
	 */
	@Test
	public void testUpdateObservableObject() {
		String[] tags = { "Neptune", "Jupiter" };

		manager.addTag(tags[0]); //tags that are added should automatically be added to options
		manager.addTag(tags[1]);

		assertArrayEquals("TagAction was not updated when adding tags to library!", tagActionTest.getOptions(), tags);

		manager.deleteTag("Neptune");	//tags that are deleted should be removed from options
		manager.deleteTag("Jupiter");

		assertTrue(tagActionTest.getOptions().length == 0);

		tagActionTest.reset(testPhoto);

		assertTrue(tagActionTest.getSelectedOptions().isEmpty());

		String[] photoTags = { "Saturn", "Mars", "Venus" };
		manager.setPhotoState(testPhoto.getName(), testPhoto.getDir(), photoTags);

		assertArrayEquals("TagAction was not updated when adding tags to library!", tagActionTest.getOptions(),
				photoTags);

		HashSet<String> selectedTags = tagActionTest.getSelectedOptions();

		for (int i = 0; i < photoTags.length; i++) {
			assertTrue(selectedTags.contains(photoTags[i]));
		}

	}

	/**
	 * Tests if photos and corresponding files are correctly named when an action
	 * is committed by the user
	 * @throws IOException
	 */
	@Test
	public void testDoAction() throws IOException {
		File testFile = new File(testPhoto.getDir() + testPhoto.getName());
		File fakeFile = new File("fake.png");

		testFile.createNewFile();
		fakeFile.createNewFile();

		manager.addTag("Mercury");
		manager.addTag("Venus");
		manager.addTag("Earth");

		tagActionTest.reset(testPhoto);
		tagActionTest.update(0);
		tagActionTest.update(1);

		assertNull(tagActionTest.doAction(fakeFile, manager));

		File newFile = tagActionTest.doAction(testFile, manager);

		assertNotNull(newFile);
		assertEquals(newFile.getParent(), testFile.getParent());
		assertEquals(newFile.getName(), "test1@Mercury@Venus.jpg");

		newFile.delete();
		fakeFile.delete();

	}
	/**
	 * Tests if action returns correct String ID
	 */
	@Test
	public void testGetActionName() {
		assertEquals(tagActionTest.getActionName(), "Tagging");
	}

}
