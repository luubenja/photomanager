package photo_renamer;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RevertNameActionTest {
	
	private Manager manager;
	private RevertNameAction revertNameActionTest;
	private Photo testPhoto;

	@Before
	public void setUp() throws Exception {
		manager = new Manager();
		revertNameActionTest = new RevertNameAction(manager);
		manager.addObserver(revertNameActionTest);
		
	}

	@After
	public void tearDown() throws Exception {
		manager = null;
		revertNameActionTest = null;
		testPhoto = null;
	}

	/**
	 * Tests if reset removes all selected options and
	 * updates the working photo
	 * @throws IndexOutOfBoundsException
	 */
	@Test
	public void testReset() throws IndexOutOfBoundsException {
		testPhoto = manager.getPhotoInstance("test1.jpg", ".\\"); 
		
		String[] tags1 = {"Apple", "Banana", "Orange"};
		
		revertNameActionTest.reset(null);
		assertTrue(revertNameActionTest.getSelectedOptions().isEmpty());
		
		String oldName = testPhoto.getName();
		
		manager.setPhotoState(testPhoto.getName(), testPhoto.getDir(), tags1);
		
		revertNameActionTest.reset(testPhoto);
		
		String[] options = revertNameActionTest.getOptions();
		
		assertTrue(options.length == 1);
		assertEquals(options[0],oldName);
	}
	
	/**
	 * Tests whether options retrieved from RevertNameAction are correct
	 * @throws IndexOutOfBoundsException
	 */
	@Test
	public void testGetOptions() throws IndexOutOfBoundsException {
		testPhoto = manager.getPhotoInstance("test2.jpg", ".\\");
		
		String[] tags1 = {"Apple", "Banana", "Orange"};
		String[] tags2 = {"Pineapple", "Kumquat"};
		
		assertEquals(0,revertNameActionTest.getOptions().length);
		revertNameActionTest.reset(testPhoto);
		String[] options;
		
		String oldName1 = testPhoto.getName();
		manager.setPhotoState(testPhoto.getName(), testPhoto.getDir(), tags1);
		options = revertNameActionTest.getOptions();
		assertTrue(options.length == 1);
		assertTrue(options[0].equals(oldName1));
		String oldName2 = testPhoto.getName();
		manager.setPhotoState(testPhoto.getName(), testPhoto.getDir(), tags2);
		options = revertNameActionTest.getOptions();  //if photo name is changed Observable should automatically
														//notify the action to update it's options
		String[] oldNames = {oldName1, oldName2};
		
		assertTrue(options.length == 2);
		assertArrayEquals(options, oldNames);
	}

	/**
	 * Tests whether selected Options are retrieved correctly
	 * @throws IndexOutOfBoundsException
	 */
	@Test
	public void testGetSelectedOptions() throws IndexOutOfBoundsException{
		testPhoto = manager.getPhotoInstance("test3.jpg", ".\\");
		
		String[] tags1 = {"Apple", "Banana", "Orange"};
		
		revertNameActionTest.reset(testPhoto);
		
		assertTrue(revertNameActionTest.getSelectedOptions().isEmpty());
		
		String oldName1 = testPhoto.getName();
		manager.setPhotoState(testPhoto.getName(), testPhoto.getDir(), tags1);
		revertNameActionTest.update(0);
		assertTrue(revertNameActionTest.getSelectedOptions().contains(oldName1));
	}

	/**
	 * Tests whether selected options are correctly updated when user makes a selection
	 */
	@Test
	public void testUpdateInt() {
		testPhoto = manager.getPhotoInstance("test4.jpg", ".\\");
		
		String[] tags1 = {"Apple", "Banana", "Orange"};
		
		revertNameActionTest.reset(testPhoto);
		
		assertTrue(revertNameActionTest.getSelectedOptions().isEmpty());
		
		String oldName1 = testPhoto.getName();
		manager.setPhotoState(testPhoto.getName(), testPhoto.getDir(), tags1);
		revertNameActionTest.update(0);
		assertTrue(revertNameActionTest.getSelectedOptions().contains(oldName1));
		revertNameActionTest.update(0);
		assertTrue(revertNameActionTest.getSelectedOptions().isEmpty());

	}
	
	/**
	 * Tests if the action is correctly updated when notified by Manager
	 * @throws IndexOutOfBoundsException
	 */
	@Test
	public void testUpdateObservableObject() throws IndexOutOfBoundsException{
		testPhoto = manager.getPhotoInstance("test5.jpg", ".\\");
		
		String photoDir = testPhoto.getDir();
		
		String[] tags1 = {"Apple", "Banana", "Watermelon"};
		String[] tags2 = {"Orange", "Cherry"};
		
		String[] expectedNames = {"test5.jpg", "test5@Apple@Banana@Watermelon.jpg",
				                  "test5@Orange@Cherry.jpg", "test5@Cherry.jpg"};

		
		revertNameActionTest.reset(testPhoto); 
		
		assertTrue(revertNameActionTest.getSelectedOptions().isEmpty()); 
		
		manager.setPhotoState(testPhoto.getName(), photoDir, tags1);
		manager.setPhotoState(testPhoto.getName(), photoDir, tags2);
		
		String[] options = revertNameActionTest.getOptions(); //changing the name twice should automatically add
																//2 names into previous names
	
		assertEquals(options[0],expectedNames[0]);
		assertEquals(options[1],expectedNames[1]);
		
		revertNameActionTest.update(0);
		
		assertTrue(revertNameActionTest.getSelectedOptions().contains(expectedNames[0]));
		
		manager.addTag("Apricot");
		
		assertTrue(revertNameActionTest.getSelectedOptions().isEmpty());
		
		manager.deleteTag("Orange");
		manager.deleteTag("Cherry");   //deleting a tag from the library automatically changes the names of files
										//which may result in additional name changes being created
		
		options = revertNameActionTest.getOptions();
		for (int i =0 ; i < options.length; i++){
			assertEquals(expectedNames[i+1],options[i]);
		}
		
	}
	
	/**
	 * Tests if action correctly names the file once the user commits to the action
	 * @throws IndexOutOfBoundsException
	 * @throws IOException
	 */
	@Test
	public void testDoAction() throws IndexOutOfBoundsException, IOException {
		testPhoto = manager.getPhotoInstance("test7.jpg", ".\\");
		

		File fakeFile = new File("fake.png");
		fakeFile.createNewFile();
		
		
		String[] tags1 = {"Grape", "Strawberry", "Mango"};
		
		
		assertNull(revertNameActionTest.doAction(fakeFile, manager));
		
		revertNameActionTest.reset(testPhoto);
		
		assertNull(revertNameActionTest.doAction(fakeFile, manager));
		
		manager.setPhotoState(testPhoto.getName(), testPhoto.getDir(), tags1);
		
		File testFile = new File(testPhoto.getDir()+testPhoto.getName());
		testFile.createNewFile();
	
		revertNameActionTest.update(0);
		assertNull(revertNameActionTest.doAction(fakeFile, manager));
		File newFile = revertNameActionTest.doAction(testFile, manager);
	
		assertNotNull(newFile);
		String expectedName = "test7.jpg";
		
		assertEquals(expectedName,newFile.getName());
		assertEquals(testPhoto.getDir(),newFile.getParent()+"\\");

		newFile.delete();
		fakeFile.delete();
	}

	/**
	 * Tests for the correct unique name
	 */
	@Test
	public void testGetActionName() {
		assertEquals( "Reverting",revertNameActionTest.getActionName());
	}

}
