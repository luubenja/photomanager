package photo_renamer;

import java.io.Serializable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Observable;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * A manager to manage all the tags and photos that exist in the program. Creates
 * a log of all changes done to all of the photos names, serialized the tag and photo
 * libraries so that when the program is closed all of the information will still be
 * available, and is observed by the two possible ways of changing a photos name; tagging
 * and revert name, so that actions can occur. 
 * 
 * @author Ben,Sara
 *
 */
public class Manager extends Observable implements Serializable {
	// the library of all Photos (keys: path + file name, values: Photo objects)
	private LinkedHashMap<String, Photo> photoLibrary = new LinkedHashMap<String, Photo>();
	// the library of all used tTag
	private LinkedHashMap<String, Tag> tagLibrary = new LinkedHashMap<String, Tag>();
	// the logger of all changes made
	private final Logger logger = Logger.getLogger(Manager.class.getName());
	// the filehandler for the logger
	private FileHandler handler = null;

	/**
	 * Creates an instance of manager, deserializes the tag and photo libraries,
	 * if the serializable files exist and intializes the logger handler so  that
	 * all logging of changes can occur.
	 */
	public Manager(){
		//if the serializeable files exist deserialize them
		if (new File("photoLibrary.ser").exists() && new File("tagLibrary.ser").exists() ){
			deserializeLibs();
		}
		//start the logger handler
		initHandler();
	}
	/**
	 * Intialize the handler for the logger
	 */
	private void initHandler() {
		try {
			//create the logger file and set the handler
			String workingDir = System.getProperty("user.dir");
			String fileName = workingDir + "Manager.log";
			handler = new FileHandler(fileName, true);

		} catch (IOException e) {
			e.printStackTrace();
		}
		//format the handler so it saves everything written to it in a simple format
		handler.setFormatter(new SimpleFormatter());
		logger.addHandler(handler);
		logger.setLevel(Level.ALL);
	}
	
	/**
	 * Update the log of photo name changes by adding the old name of the photo
	 * and the newly update name of a photo
	 * 
	 * @param <String>
	 * 			oldName
	 * @param newName
	 */
	private void updateLog(String oldName, String newName){
		//create the time at which the change occurs (year/month/day hour:min.second
		String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm.ss").format(new java.util.Date());
		//add the previous name, new name and timestamp
		logger.log(Level.SEVERE, "Previous name:{0}, New Name: {1}, Date: {2}",
				new Object[] { oldName, newName, timeStamp });
	}

	/**
	 * Return the set off all the tag names, from the tags that are contained
	 * within the tag library. 
	 * 
	 * @return <Set> of <String> tag names
	 */
	public Set<String> getTags() {
		return tagLibrary.keySet();
	}

	/**
	 *Get all the tags that are contained in the tag library
	 * 
	 * @return <Collection> of <Tag> objects in the library
	 */
	public Collection<Tag> getTagInstances() {
		return tagLibrary.values();
	}

	/**
	 * Get all the photos that are contained in the photo library
	 * 
	 * @return <Collection> of all <Photo> objects in the library
	 */
	public Collection<Photo> getPhotoInstances() {
		return photoLibrary.values();
	}

	/**
	 * Get the set off all tags that a photo is tagged with. 
	 * 
	 * @param <String>
	 *            photoName : photo name
	 * @param <String>
	 *            dir : directory photo is located in
	 * @return <Set> of <String> tag names that the photo has been tagged with
	 */
	public Set<String> getTagsfromPhoto(String photoName, String dir) {
		Photo p = getPhotoInstance(photoName, dir);
		return p.getTags();

	}

	/**
	 * Changes the name of the photo referenced by photoName and dir. If the photo is
	 * not in the library, adds a new photo object. The new photo name contains all the 
	 * tags indicated in the <Array> tags and will remove all tags it has prior to the
	 * name change. Any tags passed in that do not exist in the library will be added 
	 * automatically.
	 * 
	 * @param <String>
	 *            photoName : photo name
	 * @param <String>
	 *            dir : directory photo is located in
	 * @param <Array>
	 *            tags : array of tags that the photo is being updated with
	 */
	public void setPhotoState(String photoName, String dir, String[] tags) {
		Photo changingPhoto = getPhotoInstance(photoName, dir);
		changingPhoto.deleteAllTags(false); // deletes all references to tags

		Tag[] tagObjects = new Tag[tags.length];

		for (int i = 0; i < tags.length; i++) {
			tagObjects[i] = getTagInstance(tags[i]); // adds new tags
		}

		changingPhoto.addTags(tagObjects);

		updatePhotoInstance(photoName, dir); // updates photoLibrary with new
												// name
		//update tag action nd revert name action observers
		setChanged();
		notifyObservers();
		clearChanged();
		
		//update the log with the photos names change
		updateLog(photoName, changingPhoto.getName());
	}

	/**
	 * Add a tag to the tag library if it is not already contained within the 
	 * library. 
	 * 
	 * @param <String>
	 *            tagName : name of tag
	 * @return <boolean> true if addition is successful
	 */
	public boolean addTag(String tagName) {
		if (!tagLibrary.containsKey(tagName)) {  
			Tag newTag = new Tag(tagName); // if it doesn't already exsist create 
											// instance of tag
			tagLibrary.put(tagName, newTag);
			setChanged();  //notify action observers
			notifyObservers(tagName);
			clearChanged();
			return true;
		}
		return false;
	}

	/**
	 * Delete a tag from the tag library and rename all the photos which
	 * the tag had previously been contained in 
	 * 
	 * @param <String>
	 *            tagName : tag to delete
	 * @return <HashMap> of all the photos it had previously been
	 *         contained in
	 */
	public HashMap<String, Photo> deleteTag(String tagName) { 

		HashMap<String, Photo> photosToRename = new LinkedHashMap<String, Photo>();
		//find the tag in the tag library and remove it
		if (tagLibrary.containsKey(tagName)) {
			Tag tagToDelete = tagLibrary.get(tagName);  //get the tag instance of the 
														//tag to delete
			
			//get all the photos that contain this tag
			HashSet<Photo> photosWithTag = new LinkedHashSet<Photo>();
			photosWithTag.addAll(tagToDelete.getPhotosWithTag());
			
			//remove the tag from the photos which it was contained in, update log
			for (Photo photo : photosWithTag) {
				String oldName = photo.getName();
				photo.deleteTag(tagToDelete);
				
				updatePhotoInstance(oldName, photo.getDir()); 
				updateLog(oldName, photo.getName());
				
				photosToRename.put(oldName, photo);
			}
			//remove tag from tag library
			tagLibrary.remove(tagName);
			
			//tell actions that this tag is no longer an option
			setChanged();
			notifyObservers(tagName);
			clearChanged();
		}
		return photosToRename;
	}

	/**
	 * get the photo instance from the photo name
	 * 
	 * @param <String>
	 *            photoName : photo name
	 * @param <String>
	 *            dir : directory photo is located in
	 * @return <Photo> the <Photo> mapped to photoName
	 */
	protected Photo getPhotoInstance(String photoName, String dir) {
		Photo p;
		if (!photoLibrary.containsKey(dir + photoName)) { //if not in photo library
														  // create new photo instance
			p = new Photo(photoName, dir);
			photoLibrary.put(dir + photoName, p);
		} else { //otherwise get it from the library
			p = photoLibrary.get(dir + photoName);
		}
		return p;
	}
	
	/**
	 * Get the tag instance from the tag library 
	 * 
	 * @param <String>
	 * 			tagName the name of the tag to get
	 * @return <Tag>
	 * 			the tag instance of the tag
	 */
	protected Tag getTagInstance(String tagName) {
		Tag t;
		if (!tagLibrary.containsKey(tagName)) {
			// if not in tag library than create tag instance of the tag
			addTag(tagName);
		}
		t = tagLibrary.get(tagName); //get the tag instance
		return t;
	}

	/**
	 * Update the photo instance of a photo after a tag has been added or
	 * removed
	 * 
	 * @param <String>
	 *            photoName : photo name
	 * @param <String>
	 *            dir : directory photo is located in
	 */
	private void updatePhotoInstance(String photoName, String dir) {
		Photo changedPhoto = photoLibrary.remove(dir + photoName); //remove old
		photoLibrary.put(dir + changedPhoto.getName(), changedPhoto);

	}

	/**
	 * Deserialize the photo and tag library files so that the stored information
	 * can be accessed upon opening the program.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	
	private void deserializeLibs() {

		String[] serializedFileNames = { "photoLibrary.ser", "tagLibrary.ser" };
		try {
			for (int i = 0; i < serializedFileNames.length; i++) {
				
				// read in the serialized file and create the libraries
				FileInputStream libraryFile = new FileInputStream(serializedFileNames[i]);
				ObjectInputStream libraryIn = new ObjectInputStream(libraryFile);
				
				//if reading the photo library add information to the photos library
				if (i == 0){
					photoLibrary = (LinkedHashMap<String, Photo>) libraryIn.readObject();
				}
				//if reading the tag library file add information to the tag library
				else if (i == 1){
					tagLibrary = (LinkedHashMap<String, Tag>) libraryIn.readObject();
				}
				
				libraryIn.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Serialize the photo and tag libraries so that the information can be 
	 * saved when the program is closed
	 * 
	 */
	public void serializeLibs() {
	
		HashMap<String, LinkedHashMap> serializedFileMap = new HashMap<>();
		serializedFileMap.put("photoLibrary.ser", photoLibrary);
		serializedFileMap.put("tagLibrary.ser", tagLibrary);
		
		for (String fileName : serializedFileMap.keySet()){
			try {
				// create the serializing file and write the info from the library
				// to it
				FileOutputStream libraryFile = new FileOutputStream(fileName);
				ObjectOutputStream libraryOut = new ObjectOutputStream(libraryFile);
				libraryOut.writeObject(serializedFileMap.get(fileName));
				libraryOut.close();
				libraryFile.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		}
	}
}
