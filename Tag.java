package photo_renamer;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Creates tag instances so all tags the user wants to be adding to photos 
 * will have a name and know what photos they are currently tagged in. This class
 * observes photo so that its collection of photos that the tag is saved in is
 * automatically updated when the photos name is changed.
 * 
 * @author Ben,Sara
 *
 */
public class Tag implements Serializable, Observer {
	private static final long serialVersionUID = 1L;
	private String name;
	//map of all the photos that contain this tag with key as id of photo, 
	//and value as the photo object it corresponds to
	private HashMap<Integer, Photo> photosWithTag;
	
	/**
	 * Create a tag which has a name, and a set of all the photos that it is tagged
	 * in
	 * 
	 * @param <String> name : The name of the <Tag>
	 */
	public Tag(String name) {
		this.name = name;
		this.photosWithTag = new HashMap<Integer, Photo>();
	}
	
	/**
	 * Get the full <Map> of photos that contain this <Tag>
	 * 
	 * @return the <Map> of <Photo> objects mapped to this <Tag>
	 */
	public Collection<Photo> getPhotosWithTag() {
		return photosWithTag.values();
		}
		
	/**
	 * Get the name of this tag
	 * 
	 * @return the <String> name of this tag
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Receive any changes from the photo's that this tag is observing and update
	 * the tag instance to either remove the photo from its set of photos it is contained
	 * in, or add it, depending on if it has been deleted or added to a photo. 
	 */
	@Override
	public void update(Observable observedPhoto, Object arg) {
		Photo photo = (Photo) observedPhoto;
		//if the name of the tag is in the list of tags then it is being kept or added
		//so add it to the photos that contain this tag
		if (photo.getTags().contains(this.name)){
			photosWithTag.put(photo.getId(), photo);
		}
		//otherwise it has been deleted from this photo so you remove the photo instance
		//from the photos that contain this tag
		else{
			photosWithTag.remove(photo.getId());
		}
	}
	
}
	
