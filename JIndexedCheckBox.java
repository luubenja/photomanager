package photo_renamer;

import javax.swing.JCheckBox;

/**
 * Creates a JCheckBox which contains the index from which it was selected
 * so that each Tag in the tag options area can have a check box, and when one
 * is selected, the index of the tag is know, so it can be referenced. 
 * @author Ben,Sara
 *
 */
public class JIndexedCheckBox extends JCheckBox {

	private static final long serialVersionUID = 1L;
	private int index;
	/**
	 * Create the indexed check box
	 * @param text <String> the name of the tag
	 * @param num <int> the index of the tag
	 */
	public JIndexedCheckBox(String text, int num){
		super(text);
		index = num;
	}
	/**
	 * Gets the index of a JIndexedCheckBox
	 * @return <int> the index where the check box is selected.
	 */
	public Integer getIndex(){
		return index;
	}

}
