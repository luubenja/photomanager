package photo_renamer;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;

/**
 * Tests ImageTypeChecker
 * 
 * @author Ben,Sara
 */
public class ImageTypeCheckerTest {

	String imageFileName;
	File imageFile;
	String txtFileName;
	File txtFile;
	
	/**
	 * Sets up the tests by creating an files 
	 */
	@Before
	public void setUp()  {
		imageFileName = "img.jpg";
		imageFile = new File(imageFileName);
		txtFileName = "tired.txt";
		txtFile = new File(txtFileName);
	}
	
	/**
	 * Clears up after the tests have run by setting variables to null
	 */
	@After
	public void tearDown() {
		imageFileName = null;
		imageFile = null;
		txtFileName = null;
		txtFile = null;
	}
	
	/**
	 * Tests if an file with an image extension is accepted and if one with a 
	 * non image extension is rejected
	 */
	@Test
	public void testIsImage() {
		assertTrue(ImageTypeChecker.isImage(imageFile));
		assertFalse(ImageTypeChecker.isImage(txtFile));
	}

	/**
	 * Tests getting an extension from a filename
	 */
	@Test
	public void testGetExtension() {
		assertEquals(ImageTypeChecker.getExtension(imageFileName), "jpg" );
	}

	/**
	 * Tests removing the the extension from a file name 
	 */
	@Test
	public void testRemoveExtension() {
		assertEquals("img",ImageTypeChecker.removeExtension(imageFileName) );
	}

}
