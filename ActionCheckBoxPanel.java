package photo_renamer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextPane;
/**
 * Creates the option check box panel to be added to the GUI and produces its
 * action listener so that imageMode knows what options are selected. 
 * @author Ben,Sara
 *
 */
public class ActionCheckBoxPanel extends JPanel implements ActionListener{
	List<JCheckBox> myBoxes = new ArrayList<>();
	ImageMode imageMode;
	JTextPane selectedOptionsLabel;
	
	/**
	 * Creates an actionCheckBoxPanel and reads in the selected options label
	 * so that when check boxes are selected the user can view which ones are selected
	 * in its own window 
	 * 
	 * @param imageMode <ImageMode> the current instance of ImageMode
	 * @param selectedOptionsLabel <JTextPane> the panel where selected tags are shown
	 */
	public ActionCheckBoxPanel(ImageMode imageMode, JTextPane selectedOptionsLabel){
		this.imageMode = imageMode;
		this.selectedOptionsLabel = selectedOptionsLabel;
		clear();
	}
	/* (non-Javadoc)
	 * @see java.awt.Container#add(java.awt.Component)
	 * 
	 * Creates way of adding a component to the CheckBoxPanel so that the boxes can
	 * have their own check box and listener to see if they are clicked.
	 */
	@Override
	public Component add(Component comp) {
		// TODO Auto-generated method stub
		Component superRes = super.add(comp);
		myBoxes.add((JCheckBox) comp);
		((JCheckBox) comp).addActionListener(this);
		return superRes;
	}
	/**
	 * Removes a check from a checkboxPanel
	 */
	public void clear(){

		super.removeAll();
		myBoxes.clear();
		
		String[] options = imageMode.getActionOptions();
		for (int i = 0; i < options.length; i++) {
			JCheckBox newBox = new JIndexedCheckBox(options[i],i);
			this.add(newBox);
		}
		this.updateUI();
	}
	/**
	 * Resets the selected options list 
	 */
	public void reset(){
		
		HashSet<String> selectedOptionsSet = new HashSet<>();
		for (String option: imageMode.getActionSelectedOptions()){
			selectedOptionsSet.add(option);
		}
		
		for (JCheckBox checkBox : myBoxes){ //all boxes
			if (selectedOptionsSet.contains(checkBox.getText())){
				checkBox.setSelected(true);
			}
			else{
				checkBox.setSelected(false);
			}
		}
		
		setSelectedLabel();
		this.updateUI();
	}
	/**
	 * Set the selected panel so that it contains the selected check boxes from
	 * the options panel
	 */
	public void setSelectedLabel() {
		String selectedOptionsString = new String();
		String[] selectedOptions = imageMode.getActionSelectedOptions();
		for (int i = 0; i < selectedOptions.length; i++){
			selectedOptionsString += selectedOptions[i] + "\n";
		}
		selectedOptionsLabel.setText(selectedOptionsString);

	}
	/**
	 * Gets all the names of the tags that are checked off. 
	 * 
	 * @return <String[]> Array of all tag names that are selected
	 */
	public String[] getSelected(){
		ArrayList<String> selectedBoxes = new ArrayList<>();
		for (JCheckBox checkBox :  myBoxes){
			if (checkBox.isSelected()){
				selectedBoxes.add(checkBox.getText());
			}
		}
		return selectedBoxes.toArray(new String[selectedBoxes.size()]);
	}
	/**
	 * Gets the check box that has been selected and updates the imageMode
	 * instance so it knows a selection has occurred, and what that selection is
	 */
    public void actionPerformed(ActionEvent e) {
    	JIndexedCheckBox comp = (JIndexedCheckBox )e.getSource();
    	imageMode.updateActionPanel( comp.getIndex() );
    	this.reset();
    }
	
}