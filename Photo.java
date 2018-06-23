package photo_renamer;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Observable;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Create a photo object which can be tagged using a <Tag>, has its own name,
 * its original name, the set of all previous names, the file extension, the set
 * of all tags that it is tagged by, a logger, and a unique ID. Tag observes photo,
 * so every time the photos name is changed it sends an update to the tags that
 * are watching it, telling the tag its new file name. 
 * 
 * @author Ben,Sara
 *
 */
public class Photo extends Observable implements Serializable  {
	
	private String name;
	private String originalName; //filename without any tags, includes extension
	private String extension;
	private String dir;
	private LinkedHashMap<String, Tag> tags;
	private LinkedHashSet<String> prevNames;
	private int id;
	private static final Logger logger = Logger.getLogger( Photo.class.getName() );
	private static FileHandler handler = null;
	static int nextId;// int id for the next photo to be added to the Library
	
	/**
	 * Create instance of <Photo> which contains the photos name, directory,
	 * originial name, extension, a unique id number, the tags it contains, 
	 * the set of all previous names and the log file handler.
	 * 
	 * @param <String> name : name of photo <File>
	 * @param <String> dir : parent directory of photo <File>
	 */
	public Photo(String name, String dir){
		this.name = name;
		this.dir = dir;
		this.extension = ImageTypeChecker.getExtension(name);
		this.originalName = ImageTypeChecker.removeExtension(name);
		this.id = nextId;
		nextId += 1;
		tags = new LinkedHashMap<String, Tag>(); //key: tag name, value: Tag object
		prevNames = new LinkedHashSet<String>();
		
		//set handler for each instance of a photo
		try{
			String workingDir = System.getProperty("user.dir");
			String fileName = workingDir + this.originalName + ".log";
			handler = new FileHandler(fileName, true);
		}
		catch (IOException e) {  
			e.printStackTrace();
		}
			handler.setFormatter(new SimpleFormatter());
			logger.addHandler(handler);
			logger.setLevel(Level.ALL);
		}
		
	/**
	 * Get the <Logger> for the <Photo>
	 * @return the photo <Logger>
	 */
	public Logger getPhotoLogger(){
	return logger;
	}
	
	/**
	 * Get the name of the <Photo>
	 * @return name of photo <String>
	 */
	public String getName(){
		return name;
	}
	/**
	 * get the parent directory of <Photo> <File>
	 * @return the directory <String>
	 */
	public String getDir(){
		return dir;
	}
	/**
	 * Get the unique <int> ID for the <Photo>
	 * @return  <int> ID
	 */
	public int getId(){
		return id;
	}
	
	/**
	 * Returns a <Set> previous names of the photo
	 * @return  <Set> of previous <String> names
	 */
	public LinkedHashSet<String> getPrevNames(){
		return prevNames;
	}
	
	
	/**
	 * Adds a <String> name to the <Set> of previous names
	 * the <Photo> has had
	 * @param <String> : an old name
	 */
	private void addPrevNames(String name){
		if (!prevNames.contains(name)){
			prevNames.add(name);
		}
	}
	/**
	 * Removes a previous name from the set of previous names the photo has had,
	 * so that the previous names will not contain the current name, since it is
	 * not previous.
	 * 
	 * @param name <String> the file name to remove from the set of all previous names
	 */
	private void removePrevNames(String name){
		if (prevNames.contains(name)){
			prevNames.remove(name);
		}
	}

	/**
	 * Get the <Set> of <Tag> objects the <Photo> has
	 * @return <Set> of tags the <Photo> is tagged with
	 */

	public Set<String> getTags(){
		return tags.keySet();
	}
	
	/**
	 * Set the <String>name of the photo, constructs the name
	 * from the tags within the <Photo> instance
	 */
	private void setName(){
		String tagString = new String();
		for (String k : tags.keySet()){
			tagString += "@" + k;
		}
		String newName = originalName + tagString +"."+ extension;
		if (!newName.equals(name)){
			removePrevNames(newName); //if the new name is a previous name
			addPrevNames(name);
			updateLog(newName);
			name = newName;
			//notify the tag observer
			setChanged();
			notifyObservers();
			clearChanged();
		}
	}
	
	/**
	 * Add a <Tag> to the photo
	 * @param <Tag[]> newTags: all the tags to add to the photo
	 */
	public void addTags(Tag[] newTags){
		String tagName;
		for (int i = 0; i < newTags.length; i++){
			tagName = newTags[i].getName();
			if (!tags.containsKey(tagName)){ //checks if photo is already tagged with the tag
				tags.put(tagName, newTags[i]);
				addObserver(newTags[i]);
			}
		}
		
		setName();
	}

	/**
	 * Delete a <Tag> from the photo
	 * @param <Tag> tag : tag being deleted
	 */
	public void deleteTag(Tag tag){
		String tagName = tag.getName();
		if (tags.containsKey(tagName)){
			tags.remove(tagName);
			setName();
			deleteObserver(tag);
		}
	}
	/**
	 * 
	 */
	public void deleteAllTags(){
		deleteAllTags(true);
	}
	/**
	 * Delete all the tags from this photo
	 */
	public void deleteAllTags(boolean toResetName){
		tags.clear();
		setChanged();
		notifyObservers();
		clearChanged();
		deleteObservers();	
		
		if (toResetName){
			setName();
		}
	}
	/**
	 * Update the photos log with the new name of the photo, the previous name of the 
	 * photo and the time at which the name was changed
	 * 
	 * @param newName <String> the new name of the photo 
	 */
	public void updateLog(String newName){
		String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm.ss").format(new java.util.Date());
		logger.log(Level.SEVERE,"Previous name:{0}, New Name: {1}, Date: {2}", new Object[] {name, newName,timeStamp});
	}

	
}