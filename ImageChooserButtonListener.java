package photo_renamer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


/**
 * Create and implement an action listener for the image chooser button which
 * sets the working file to be the selected button. 
 * 
 * @author Ben,Sara
 *
 */
public class ImageChooserButtonListener implements ActionListener {

	File imageFile;
	ImageMode imageMode;
	ActionCheckBoxPanel optionsPanel;
	/**
	 * Create the action listener that is used when an image is selected from the 
	 * list of images in a chosen directory. Reads in the image file, the image mode
	 * and the currently selected options 
	 * 
	 * @param imageFile
	 * @param imageMode
	 * @param optionsPanel
	 */
	public ImageChooserButtonListener(File imageFile, ImageMode imageMode, ActionCheckBoxPanel optionsPanel) {
		this.imageFile = imageFile;
		this.imageMode = imageMode;
		this.optionsPanel = optionsPanel;
	}
	/**
	 * Gets the selected imageFile
	 * @return imageFile <File> - the selected image file
	 */
	public File getImageFile(){
		return imageFile;
	}
	/**
	 * Sets the selected image File
	 */
	public void setImageFile(File newFile){
		imageFile = newFile;
		
	}
	/**
	 * Implement the action to be performed upon clicking the selected image;
	 * set the selected image to be the current working file if it is not already
	 * and update the options so previous selections are clicked.
	 * 
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		File currWorkingFile = imageMode.getWorkingFile();
		
		//make sure the current working file is not the same as the image file,
		//if it is not then set the working file to be the image file
		if (currWorkingFile != null) {  //if current working file isn't null
			boolean condition1 = currWorkingFile.getName().equals(imageFile.getName());
			boolean condition2 = currWorkingFile.getParent().equals(imageFile.getParent());
			if (!condition1 || !condition2) {
				imageMode.setWorkingFile(imageFile);
				optionsPanel.clear();
				optionsPanel.reset();
			}
		} else {  //if current working file is null
			imageMode.setWorkingFile(imageFile);
			optionsPanel.clear();
			optionsPanel.reset();
		}
	}
}
