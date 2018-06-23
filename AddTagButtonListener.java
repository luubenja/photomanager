package photo_renamer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

import javax.swing.JTextArea;

/**
 * Create an action listener for the addTagButton that will get the users
 * input for name of the tag and add a tag to the set of tags if it is an 
 * alphanumeric expression, and if it isn't it will ask the user to add an
 * acceptable tag name. 
 * 
 * @author Ben, Sara
 *
 */

public class AddTagButtonListener implements ActionListener {
	private ImageMode imageMode;
	private JTextArea textArea;
	private ActionCheckBoxPanel optionsPanel;

	/**
	 * Create listener, reading in the textArea that the user has written a tag to,
	 * the instance of imageMode and the currently selected tags from the tag options
	 * available
	 * 
	 * @param textArea <JTextArea> the area in which the user write the 
	 * 							tag that they wish to add to their list of tags
	 * @param imageMode <ImageMode> the instance of imageMode
	 * @param optionsPanel <ActionCheckBoxPane> The tag options that are available 
	 * 								for the user to select from 
	 */
	public AddTagButtonListener(JTextArea textArea, ImageMode imageMode, ActionCheckBoxPanel optionsPanel) {
		this.imageMode = imageMode;
		this.textArea = textArea;
		this.optionsPanel = optionsPanel;
	}
	/**
	 * Makes the action listener event so that if the add tag button
	 * is pressed, it checks to see that the tag exists and only contains 
	 * alphanumeric characters, and if it does it adds the new tag to the tag options.
	 * If they tag name is not valid it prompts the user to enter a valid tag name.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String tagName = textArea.getText();
		if (tagName != null) {
			if ((Pattern.matches("[\\w&&[^_]]+", tagName))) {

				imageMode.addTag(tagName);
				textArea.setText(null);

				optionsPanel.clear();
			}
		}
		else {
			System.out.println("Please insert a correct tag name");
		}
	}
}
