package photo_renamer;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;

/**
 * Determine if a file is of an image file type: image, png, tif,
 * Also contains a collection of utility methods for handling file names
 * jpg, jpeg, bmp, gif, JPG
 * 
 * @author Ben,Sara
 *
 */
public class ImageTypeChecker {
	private static MimetypesFileTypeMap typeChecker = new MimetypesFileTypeMap();
	
	/**
	 * Determines if a <File> is an image file or not based on the extension
	 * 
	 * @param <File> f : the file you are checking
	 * @return <boolean> : true if it possesses an image extension
	 */
	public static boolean isImage(File f){
		typeChecker.addMimeTypes("image png tif jpg jpeg bmp gif JPG");
		String type = typeChecker.getContentType(f);
		return type.equals("image");
		
	}
	/**
	 * Gets the <String> extension of a file name
	 * @param <String >fileName : the file name 
	 * @return <String> the extension
	 */
	public static String getExtension(String fileName){
		//find where the extension starts and get it
		String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
		return ext;
		
	}
	/**
	 * Removes the <String> extension of a <String> file name and return the file name without the extension
	 * @param <String> fileName : the file name
	 * @return <String> the file name with extension removed
	 */
	public static String removeExtension(String fileName){
		//finds where the extenstion starts and gets everything before it
		String newFileName = fileName.substring(0,fileName.lastIndexOf('.'));
		return newFileName;
	}
}
