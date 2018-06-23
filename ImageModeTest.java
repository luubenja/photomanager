package photo_renamer;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ImageModeTest {
	
	ImageMode testListener = ImageMode.getInstance();
	
	private final static String testPath = ".";

	private String testDir1 = testPath + "/" + "test_dir1";
	private String testDir2 = testPath + "/" + "test_dir2";

	private File testFile1;
	private File testFile2;
	
	private File testImageFile0;
	private File testImageFile1;
	private File testImageFile2;
	private File testImageFile3;
	private File testImageFile4;
	private File testImageFile5;
	
	private File testNonImageFile;
	

	@Before
	public void setUp() throws Exception {
		testFile1 = new File(testDir1);
		testFile1.mkdir(); // test_dir1
		testImageFile0 = new File(testDir1 + "/testImage1.jpg"); // test_dir1/f1
		testImageFile0.createNewFile();

		testFile2 = new File(testDir2);
		testFile2.mkdir(); // test_dir2
		
		new File(testDir2 + "/sub1/sub1_1").mkdirs(); // test_dir2/sub1/sub1_1
		new File(testDir2 + "/sub2").mkdirs(); // test_dir2/sub2
		
		testImageFile1 =  new File(testDir2 + "/testImage1.jpg"); // test_dir2/f1
		testImageFile2 = new File(testDir2 + "/testImage2.png"); // test_dir2/z1
		testImageFile3 = new File(testDir2 + "/sub1/testImage3.tif"); // test_dir2/sub1/f2
		testImageFile4 = new File(testDir2 + "/sub1/sub1_1/testImage4.bmp"); // test_dir2/sub1/sub1_1/z2
		testImageFile5 = new File(testDir2 + "/sub2/testimage5.gif");// test_dir2/sub2/z1
		
		/**
		 * Creates the following directory structure
		 * ----test_dir1
		 * -------f1
		 * ----test_dir2
		 * -------f1
		 * -------z1
		 * ----------sub1
		 * --------------f2
		 * --------------sub1_1
		 * ------------------z2
		 * ----------sub2
		 */
		
		testNonImageFile = new File(testDir2 + "/sub1/testFile.txt"); // test_dir2/sub1/f2
		testNonImageFile.createNewFile();
		
		File[] testImageFiles = {testImageFile1,testImageFile2,testImageFile3,testImageFile4,testImageFile5}; 
		
		for(int i = 0; i < testImageFiles.length; i++){
			testImageFiles[i].createNewFile();
		}
		
	}

	@After
	public void tearDown() throws IOException {
		delete(testFile1); //deletes all directories
		delete(testFile2);
		ImageMode.reset();
	}
	
	private static void delete(File f) {
		if (f.isDirectory()) { //deletes every thing in directories then the directory itself
			for (File c : f.listFiles())
				delete(c);
			f.delete();
		}
		else{
			f.delete();
		}
	}

	@Test
	/**
	 * Tests if viewing images can be set correctly
	 * files that are not image files are rejected
	 * @throws NullPointerException
	 */
	public void testSetViewingImages() throws NullPointerException {
		ArrayList<File> testFiles = new ArrayList<>();
		testListener.setViewingImages(null);

		testFiles.add(testImageFile1);
		testListener.setViewingImages(testFiles);
		assertTrue(testListener.getViewingImages().contains(testImageFile1));

		testFiles.add(testImageFile2);
		testFiles.add(testNonImageFile);
		testListener.setViewingImages(testFiles);
		assertTrue(testListener.getViewingImages().contains(testImageFile1));
		assertTrue(testListener.getViewingImages().contains(testImageFile2));
		assertFalse(testListener.getViewingImages().contains(testNonImageFile)); //non image files are not included

		testListener.setViewingImages(new ArrayList<File>());
		assertTrue(testListener.getViewingImages().isEmpty());

	}
	/**
	 * tests if viewing images can be retrieved correctly
	 */
	@Test
	public void testGetViewingImages() {
		ArrayList<File> testFiles = new ArrayList<>();
		assertTrue(testListener.getViewingImages().isEmpty());

		testFiles.add(testImageFile1);
		testFiles.add(testImageFile2);
		testListener.setViewingImages(testFiles);
		assertTrue(testListener.getViewingImages().contains(testImageFile1));
		assertTrue(testListener.getViewingImages().contains(testImageFile2));

		testListener.setViewingImages(new ArrayList<File>());
		assertTrue(testListener.getViewingImages().isEmpty());
	}
	/**
	 * tests if working file can be retrieved correctly
	 */
	@Test
	public void testGetWorkingFile() {
		assertNull(testListener.getWorkingFile());
		
		ArrayList<File> testFiles = new ArrayList<>();
		testFiles.add(testImageFile1);
		testFiles.add(testImageFile2);
		testListener.setViewingImages(testFiles);
		testListener.setWorkingFile(testImageFile2);
		assertEquals(testImageFile2,testListener.getWorkingFile());
	
	}
	/**
	 * tests if working file can be set correctly
	 * a working file MUST belong to the set of current viewing images
	 */
	@Test
	public void testSetWorkingFile() {
		ArrayList<File> testFiles = new ArrayList<>();
		testFiles.add(testImageFile1);
		testFiles.add(testImageFile2);
		testListener.setViewingImages(testFiles);
		
		testListener.setWorkingFile(null);
		assertNull(testListener.getWorkingFile()); 
		testListener.setWorkingFile(testImageFile3);
		assertNull(testListener.getWorkingFile()); // if not in viewing images cannot be set as working file
		
		testListener.setWorkingFile(testImageFile2);
		assertEquals(testImageFile2,testListener.getWorkingFile());
		
	}
	
	/**
	 * tests if action options can be retrieved correctly
	 * and that when switching between action types the options change
	 * accordingly
	 * @throws IndexOutOfBoundsException
	 */
	@Test
	public void testGetActionOptions() throws IndexOutOfBoundsException{
		
		String[] options;
		
		assertEquals(0,testListener.getActionOptions().length);
		
		ArrayList<File> testFiles = new ArrayList<>();
		testFiles.add(testImageFile1);
		testListener.setViewingImages(testFiles);
		testListener.setWorkingFile(testImageFile1); //when no tags or previous names exist yet
		assertEquals(0,testListener.getActionOptions().length);
		
		testListener.actionSwitch("Reverting");
		assertEquals(0,testListener.getActionOptions().length);
		
		testListener.actionSwitch("Tagging");
		String[] testTags = {"Red", "Blue", "Yellow"};
		
		for(int i = 0; i < testTags.length; i++){
			testListener.addTag(testTags[i]);
		}
		
		assertArrayEquals(testTags,testListener.getActionOptions()); //checks that tags now options
		
		testListener.updateActionPanel(0);
		testListener.doAction();
		
		testListener.actionSwitch("Reverting");
		
		options = testListener.getActionOptions(); //checks that previous name is now an option
		assertEquals(1,options.length);
		assertEquals(testImageFile1.getName(),options[0]);
		
	}
	/**
	 * Tests that selected options are retrieved correctly
	 * and that when changing between actions the selected options
	 * are reset 
	 */
	@Test
	public void testGetActionSelectedOptions() {
		String[] selectedOptions;
		ArrayList<File> testFiles = new ArrayList<>();
		testFiles.add(testImageFile1);
		testListener.setViewingImages(testFiles);
		testListener.setWorkingFile(testImageFile1);

		String[] testTags = {"Magenta", "Silver", "White"};
		
		for(int i = 0; i < testTags.length; i++){
			testListener.addTag(testTags[i]);
		}

		testListener.updateActionPanel(0);
		selectedOptions = testListener.getActionSelectedOptions();
		assertEquals(1,selectedOptions.length);  //after updating selected option has been added
		testListener.doAction(); //after action is committed selection options resets
		
		testListener.updateActionPanel(1);
		testListener.updateActionPanel(2);
		
		selectedOptions = testListener.getActionSelectedOptions();
		assertArrayEquals(testTags,selectedOptions);
		testListener.doAction();
		
		assertArrayEquals(testTags,selectedOptions);

		testListener.actionSwitch("Reverting");
		String[] expectedNames = {"testImage1.jpg", "testImage1@Magenta.jpg"};
		
		testListener.updateActionPanel(1);
		assertArrayEquals(expectedNames,testListener.getActionOptions());

		selectedOptions = testListener.getActionSelectedOptions();
		assertEquals(expectedNames[1],(selectedOptions[0]));
		
	}
	/**
	 * Tests that actions can be switched correctly
	 */
	@Test
	public void testActionSwitch() {
		String[] actionNames = {"Tagging","Reverting"};
		assertEquals(actionNames[0],testListener.getActionLabel());
		
		testListener.actionSwitch("Fake Action");
		assertEquals(actionNames[0],testListener.getActionLabel());
		
		testListener.actionSwitch("Reverting");
		assertEquals(actionNames[1],testListener.getActionLabel());
		
		testListener.actionSwitch("Tagging");
		assertEquals(actionNames[0],testListener.getActionLabel());
	}
	/**
	 * Tests that given user selection from options, the actions are correctly
	 * updated
	 */
	@Test
	public void testUpdateActionPanel() {
		String[] selectedOptions;
		ArrayList<File> testFiles = new ArrayList<>();
		testFiles.add(testImageFile1);
		testListener.setViewingImages(testFiles);
		testListener.setWorkingFile(testImageFile1);

		String[] testTags1 = {"Magenta", "White", "Black"};
		String[] testTags2 = {"White", "Black"};
		
		for(int i = 0; i < testTags1.length; i++){
			testListener.addTag(testTags1[i]);
		}

		testListener.updateActionPanel(0);
		selectedOptions = testListener.getActionSelectedOptions();
		assertEquals(1,selectedOptions.length);
		
		testListener.updateActionPanel(1);
		testListener.updateActionPanel(2);
		selectedOptions = testListener.getActionSelectedOptions();
		assertArrayEquals(testTags1,selectedOptions);
		
		testListener.updateActionPanel(0);
		selectedOptions = testListener.getActionSelectedOptions();
		assertArrayEquals(testTags2,selectedOptions);
	}

	@Test
	/**
	 * tests that when an action is performed the correct files are
	 * changed
	 */
	public void testDoAction() {
		ArrayList<File> testFiles = new ArrayList<>();
		testFiles.add(testImageFile1);
		testListener.setViewingImages(testFiles);
		testListener.setWorkingFile(testImageFile1);

		String[] testTags = {"Magenta", "Silver", "White"};
		String expectedName;
		
		for(int i = 0; i < testTags.length; i++){
			testListener.addTag(testTags[i]);
		}
		testListener.updateActionPanel(0);
		testListener.doAction();
		
		expectedName = "testImage1@Magenta.jpg";
		assertEquals(expectedName,testListener.getWorkingFile().getName());
		
		testListener.actionSwitch("Reverting");
		testListener.doAction();
		testListener.updateActionPanel(0);
		testListener.doAction();
		expectedName = "testImage1.jpg";
		assertEquals(expectedName,testListener.getWorkingFile().getName());
		
		testListener.updateActionPanel(0);
		testListener.doAction();
		expectedName = "testImage1@Magenta.jpg";
		assertEquals(expectedName,testListener.getWorkingFile().getName());
		
	}

	@Test
	/**
	 * Tests that tags can be added to the library
	 */
	public void testAddTag() {
		ArrayList<File> testFiles = new ArrayList<>();
		testFiles.add(testImageFile1);
		testListener.setViewingImages(testFiles);
		testListener.setWorkingFile(testImageFile1);

		String[] testTags = {"Beige", "Olive", "Mauve"};
		String[] expectedTags1 = {"Beige"};
		String[] expectedTags2 = {"Beige","Olive"};
		
		testListener.addTag(testTags[0]);
		assertArrayEquals(expectedTags1,testListener.getActionOptions());
		
		testListener.addTag(testTags[1]);
		assertArrayEquals(expectedTags2,testListener.getActionOptions());
		
		testListener.addTag(testTags[1]);
		assertArrayEquals(expectedTags2,testListener.getActionOptions());
		
	}

	@Test
	/*
	 * tests that deleting tags correctly removes the tag from the library and 
	 * also removes it from the correct files and that photos for which this happens
	 * have their previous names updated
	 */
	public void testDeleteTag() {
		ArrayList<File> testFiles = new ArrayList<>();
		testFiles.add(testImageFile1);
		testListener.setViewingImages(testFiles);
		testListener.setWorkingFile(testImageFile1);

		String[] testTags = {"Beige", "Olive", "Mauve", "Grey"};
		String[] expectedTags1 = {"Beige", "Mauve"};
		String[] expectedTags2 = {"Beige", "Mauve", "Olive"};
		
		for(int i = 0; i < testTags.length; i++){
			testListener.addTag(testTags[i]);
		}
		testListener.updateActionPanel(3);
		testListener.deleteTag(testTags[3]);
		
		assertEquals(0,testListener.getActionSelectedOptions().length);
		testListener.updateActionPanel(0);
		testListener.doAction();
		testListener.updateActionPanel(1);
		testListener.doAction();
		testListener.deleteTag(testTags[1]);
		assertArrayEquals(expectedTags1,testListener.getActionOptions());
		String expectedName = "testImage1@Beige.jpg";
		assertEquals(expectedName, testListener.getWorkingFile().getName());
		
		testListener.actionSwitch("Reverting");
		testListener.updateActionPanel(1);
		testListener.doAction();
		
		testListener.actionSwitch("Tagging");
		assertArrayEquals(expectedTags2,testListener.getActionOptions());
	}


	@Test
	/*8
	 * tests that image with most tags can be found
	 */
	public void testImageSelectFromMostTags() throws IndexOutOfBoundsException{
		ArrayList<File> testFiles = new ArrayList<>();
		testFiles.add(testImageFile1);
		testFiles.add(testImageFile2);
		testFiles.add(testImageFile3);
		testFiles.add(testImageFile4);
		testListener.setViewingImages(testFiles);
		
		String[] testTags = {"Violet", "Cerulean", "Gold", "Burgundy","Green"};
		for(int i = 0; i < testTags.length; i++){
			testListener.addTag(testTags[i]);
		}
		
		testListener.setWorkingFile(testImageFile1);
		testListener.updateActionPanel(0);
		testListener.doAction();
		
		testListener.setWorkingFile(testImageFile2);
		testListener.updateActionPanel(2);
		testListener.updateActionPanel(3);
		testListener.doAction();
		
		testListener.setWorkingFile(testImageFile3);
		testListener.updateActionPanel(4);
		testListener.updateActionPanel(1);
		testListener.updateActionPanel(3);
		testListener.doAction();
		
		ArrayList<File> filesWithMostTags = testListener.imageSelectFromMostTags();
		
		assertEquals(1,filesWithMostTags.size());

		File fileWithMostTags = filesWithMostTags.remove(0);
		
		assertEquals(testImageFile3.getParent(),fileWithMostTags.getParent());
		String expectedName = "testImage3@Green@Cerulean@Burgundy.tif";
		assertEquals(expectedName,fileWithMostTags.getName());
		
		testListener.setWorkingFile(testImageFile4);
		testListener.updateActionPanel(0);
		testListener.updateActionPanel(2);
		testListener.updateActionPanel(1);
		testListener.doAction();
		
		filesWithMostTags = testListener.imageSelectFromMostTags();
		
		assertEquals(2,filesWithMostTags.size());

		File fileWithMostTags1 = filesWithMostTags.get(0);
		File fileWithMostTags2 = filesWithMostTags.get(1);
		
		assertEquals(testImageFile3.getParent(),fileWithMostTags1.getParent());
		assertEquals(testImageFile4.getParent(),fileWithMostTags2.getParent());
		
		assertEquals(expectedName,fileWithMostTags1.getName());
		
		expectedName = "testImage4@Violet@Gold@Cerulean.bmp";
		assertEquals(expectedName,fileWithMostTags2.getName());
		
	}

	@Test
	/*
	 * Tests that all images in a specific directory can be obtained
	 */
	public void testImageSelectFromDir() {
		ArrayList<File> retrievedImageFiles = ImageMode.imageSelectFromDir(testFile1);
		HashSet<String> retrievedAbsolutePaths = new HashSet<String>();
		
		File[] imageFiles = {testImageFile0,testImageFile1,testImageFile2,testImageFile3,testImageFile4,testImageFile5};
		
		for (File retrievedFile: retrievedImageFiles){
			retrievedAbsolutePaths.add(retrievedFile.getAbsolutePath());
		}
		
		assertTrue(retrievedAbsolutePaths.contains(imageFiles[0].getAbsolutePath()));
		
		retrievedAbsolutePaths.clear();
		retrievedImageFiles = ImageMode.imageSelectFromDir(testFile2);
		assertEquals(imageFiles.length - 1, retrievedImageFiles.size());
		for (File retrievedFile: retrievedImageFiles){
			retrievedAbsolutePaths.add(retrievedFile.getAbsolutePath());
		}
		
		
		for(int i = 1; i < imageFiles.length; i++){
			assertTrue(retrievedAbsolutePaths.contains(imageFiles[i].getAbsolutePath()));
		}
		assertFalse(retrievedAbsolutePaths.contains(imageFiles[0].getAbsolutePath()));
		assertFalse(retrievedAbsolutePaths.contains(testNonImageFile.getAbsolutePath()));

	}

}
