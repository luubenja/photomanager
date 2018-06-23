package photo_renamer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Observable;
import java.util.Observer;

/**
 * Creates the action to revert a photo to a previous name. Inherits all methods
 * from Action interface, and observes Manager.  
 * 
 * @author Ben,Sara
 *
 */
public class RevertNameAction implements Observer, Action {

	private static String actionName = "Reverting";

	private String[] options = new String[0]; //array of previous names
	private String selectedName;              //user's choice from options

	private Photo workingPhoto;				  //photo to be renamed

	/**
	 * Creates an instance of revert name action 
	 * @param manager <Manger> the instance of manager
	 */
	public RevertNameAction(Manager manager) {

	}
	
	/**
	 * Resets the possible names to rename a file to when working <File> changes
	 * 
	 * @param <File>
	 *            workingFile : the photo being worked on
	 */
	public void reset(Photo newPhoto) {
		workingPhoto = newPhoto;
		selectedName = null;
		
		//get the working photos previous names and adds them to the possible options
		if (workingPhoto != null) {
			LinkedHashSet<String> prevNames = workingPhoto.getPrevNames();
			options = prevNames.toArray(new String[prevNames.size()]);

		}
	}

	/**
	 * Gets all the possible options that a photo can be renamed to. 
	 * 
	 * @return <String[]> all the names a photo can be renamed to, as strings
	 */
	public String[] getOptions() {
		return options;
	}

	/**
	 * Gets the old name that the user has selected to rename the file to
	 * 
	 * @return <String> selected old name
	 */
	public LinkedHashSet<String> getSelectedOptions() {
		LinkedHashSet<String> selectedOptions = new LinkedHashSet<String>();
		
		if (selectedName != null) { //make sure it exists
			selectedOptions.add(selectedName);
		}
		return selectedOptions;
	}

	/**
	 * Update the selected name to a different one the user has picked. If
	 * the name doesn't exist then an IndexOutOfBoundsException is thrown.
	 * 
	 * @param <String>
	 *            newNameChoice : the name of the <Photo> to rename to
	 */
	public void update(int index) throws IndexOutOfBoundsException {
		if (workingPhoto != null) { //if there is a working file
			if (index < options.length && index >= 0) {
				//get the index at which the selected name is
				String newSelectedName = options[index];
				if (newSelectedName.equals(selectedName)) {
					selectedName = null;
				} else {
					selectedName = options[index];
				}
			} else {
				throw new IndexOutOfBoundsException("No option at this index.");
			}
		}
	}

	/**
	 * Automatically updates when changes have been made to this
	 * list of available tags or a photo has changed names. This ensures
	 * that RevertNameAction will maintain the most current version
	 * it's working photo's names
	 * 
	 * @param
	 * 		<Manager> manager
	 * @param
	 * 		<Object> arg : passes in a tag to either add or remove, null if tag library
	 * 					   was not altered
	 * **/
	@Override
	public void update(Observable manager, Object arg) {
		this.reset(this.workingPhoto);
	}

	/**
	 * Reverts the file name to a previous name selected by user
	 * 
	 * @param <File>
	 *            file : the file to be renamed
	 * @return <File> the image file with changed name
	 * @throws IOException
	 */
	public File doAction(File file, Manager manager) {
		//if a working photo and selected name to revert to exist then proceed
		if (workingPhoto != null && selectedName != null) {

			String fileName = file.getName(); //the file which is being renamed name
			String dir = file.getParent() + "\\";

			String currFileName = workingPhoto.getName(); //current file name
			String currDir = workingPhoto.getDir();

			//renames the photo based on the tags in the old file name
			if (fileName.equals(currFileName) && (dir).equals(currDir)) { //makes sure current file is workingFile
				selectedName = ImageTypeChecker.removeExtension(selectedName);
				String[] nameSplit = selectedName.split("@"); // gets tags from
																// old file Name
				String[] tags = Arrays.copyOfRange(nameSplit, 1, nameSplit.length);

				manager.setPhotoState(fileName, dir, tags);
				File newFile = new File(currDir + workingPhoto.getName());

				if (file.renameTo(newFile)) {
					return newFile;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the <Action> name, name serves as a unique <String> Id
	 * 
	 * @return <String> action name
	 */
	public String getActionName() {
		return actionName;
	}
}