package photo_renamer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Create the graphical user interface for the entire program so that a user
 * can run the program and get an interface from which to change their photos
 * by adding tags, deleting tags, changing back to an old name, as well
 * as reviewing all of their past tagging history. 
 * 
 * @author Ben,Sara
 *
 */
public class PhotoRenamer {
	
	private ImageMode imageMode = ImageMode.getInstance();

	private JFrame photoRenamerWindow = new JFrame("Photo Renamer");
	private JFrame deleteConfirmationWindow = new JFrame("Confirm Deletion");

	private JPanel deletePanel = new JPanel();
	private JPanel deleteButtonPanel = new JPanel();
	private JLabel deleteMessageLabel = new JLabel();
	private JButton acceptDeleteButton;
	private JButton declineDeleteButton;

	private JButton addTagButton;
	private JButton deleteTagButton;
	private JPanel tagInputArea;
	private JButton saveChangesButton;
	
	private JPanel optionViewerPanel = new JPanel();
	private JTextPane selectedOptionsText = new JTextPane();
	private ActionCheckBoxPanel optionPanel = new ActionCheckBoxPanel(imageMode, selectedOptionsText);
	
	private JPanel actionBorder = new JPanel();
	private JPanel actionButtons = new JPanel();
	private JPanel actionPane = new JPanel();
	private JPanel actionSelectionPanel = new JPanel();
	
	private JButton tagModeButton;
	private JButton revertNameModeButton;
	
	private JPanel masterLogPanel;
	private JScrollPane logScroll;
	private JList<Object> logList;

	private JTabbedPane westPane;
	private JScrollPane optionsScroll;

	private JButton getDirectoryButton;
	private JScrollPane photoView = new JScrollPane();
	private static JPanel photoViewer = new JPanel();
	

	/***
	 * Builds the pop up window which asks the user if they are sure they want to
	 * permanently delete a tag from their library and all the photos which it is 
	 * contained in. 
	 */
	private void buildDeleteConfirmationWindow() {
		deletePanel.setLayout(new BorderLayout());
		deleteMessageLabel.setText("Are you sure you want to delete the selected tags from all photos and the library?");
		deleteMessageLabel.setHorizontalAlignment(JLabel.CENTER);
		acceptDeleteButton = new JButton("Yes");
		acceptDeleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteTag();
				updateLogPanel();
				deleteConfirmationWindow.setVisible(false);
			}
		});
		//create decline button and its action listener
		declineDeleteButton = new JButton("No");
		declineDeleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteConfirmationWindow.setVisible(false);
			}
		});
		//set up panel
		deleteButtonPanel.setLayout(new BorderLayout());
		deleteButtonPanel.add(acceptDeleteButton, BorderLayout.WEST);
		deleteButtonPanel.add(declineDeleteButton, BorderLayout.EAST);
		deletePanel.add(deleteButtonPanel, BorderLayout.SOUTH);
		deletePanel.add(deleteMessageLabel, BorderLayout.CENTER);

	}

	/**
	 * Deletes selected tags from the library once the user has confirmed their decision
	 * Also updates the buttons and options once the tags are removed.
	 */
	private void deleteTag() {
		
		//gets the selected tags from optionPanel, and calls delete on them which
		//removes them from the library of tags and all the photos they are contained in
		String[] tagsToDelete = optionPanel.getSelected();

		for (String tag : tagsToDelete) {
			imageMode.deleteTag(tag);
		}
		//update the photo buttons to makes sure their names are correct and 
		//update the list of possible tags to select
		updatePhotoButtons();
		optionPanel.clear();
		optionPanel.reset();

	}

	/**
	 * Creates a tabbed panel that has the action options, called DO STUFF!!, and 
	 * the view the master log. 
	 * 
	 */
	private void buildTabbedPane() {
		
		// create tab pane layout, create the buttons, and add the buttons to the panel
		masterLogPanel = new JPanel();
		buildSaveChangesButton();
		viewMasterLogPanel();
		buildActionPanel();
		// create the tabbed panel and the tabs

		westPane = new JTabbedPane();
		
		westPane.add("DO STUFF!", actionBorder);
		westPane.addTab("View Complete Log", masterLogPanel);
		
	}

	/**
	 * Creates the panel that shows the master log by reading the log file
	 * that is added too every time a photos name is changed. 
	 */
	private void viewMasterLogPanel(){
		
		//get the log file name and read it into ViewLog
		String workingDir = System.getProperty("user.dir");
		String fileName = workingDir + "Manager.log";
		java.util.Queue<String> fullLog = ViewLog.getLog(fileName);
		
		//add the log to the scroll pane and display
		logScroll = new JScrollPane();
		if (!fullLog.isEmpty()){
			logList = new JList<Object>(fullLog.toArray());
			logScroll.setViewportView(logList);
			Dimension preferredSize = new Dimension(500,650);
			logScroll.setPreferredSize(preferredSize);
			masterLogPanel.add(logScroll);	}
		else{
			System.out.println("No log file, tag some photos to get one!");
		}
	}
	/**
	 * Updates the master log panel, called when save button and confirm delete 
	 * buttons are pressed so that the changes in photos names are printed 
	 * in the view the total log.
	 */
	private void updateLogPanel(){
		masterLogPanel.removeAll();
		viewMasterLogPanel();
	}


	/**
	 * Add all the components to the action panel on the west side of the GUI
	 * so that any action (add tag, delete tag, save selection, write tag input
	 * switch from revert/tagging) can be performed
	 */
	private void buildActionPanel() {
		
		//set the layout
		actionBorder.setLayout(new BorderLayout());
		actionSelectionPanel.setLayout(new GridLayout(1,2));
		actionPane.setLayout(new GridLayout(1, 2));

		//create buttons and their listeners
		actionButtons.setLayout(new GridLayout(4, 1));
		buildDeleteTagButton();
		buildAddTagButton();
		buildActionSwitchButtons();
		
		//create tagging options and add them to the panel
		optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
		optionsScroll = new JScrollPane(optionPanel);

		optionViewerPanel.setLayout(new GridLayout(2, 1));
		optionViewerPanel.add(optionsScroll);
		optionViewerPanel.add(selectedOptionsText);

		//add all the action buttons
		actionButtons.add(tagInputArea);
		actionButtons.add(addTagButton);
		actionButtons.add(saveChangesButton);
		actionButtons.add(deleteTagButton);

		actionPane.add(actionButtons);
		actionPane.add(optionViewerPanel);
		
		//add the change function buttons 
		actionSelectionPanel.add(tagModeButton);
		actionSelectionPanel.add(revertNameModeButton);
		
		actionBorder.add(actionSelectionPanel, BorderLayout.NORTH);
		actionBorder.add(actionPane, BorderLayout.CENTER);
		
	}
	/**
	 * Produces the GUI views so that a user can switch back and forth between
	 * reverting a photo to an old name, and the tagging function, which will
	 * add/delete tags from a photo regardless of its previous name. 
	 */
	private void buildActionSwitchButtons(){
		//if the revert to an old name is selected call this action
		revertNameModeButton = new JButton("Change to an old Name");
		revertNameModeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				imageMode.actionSwitch("Reverting");
				optionPanel.clear();
				optionPanel.reset();
				tagInputArea.setVisible(false);
				addTagButton.setVisible(false);
				deleteTagButton.setVisible(false);
			}
		});
		//if the tagging mode button is selected call this action 
		tagModeButton = new JButton("Tag Photos");
		tagModeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				imageMode.actionSwitch("Tagging");
				optionPanel.clear();
				optionPanel.reset();
				tagInputArea.setVisible(true);
				addTagButton.setVisible(true);
				deleteTagButton.setVisible(true);
			}
		});
	}
	

	/**
	 * Creates a panel that contains a text field and a button, so that when the
	 * user adds a new tag to the text field, and clicks the button, the new tag
	 * is added to the tag library. Also makes a small panel to tell the user
	 * what the empty text box is for. 
	 * 
	 */
	private void buildAddTagButton() {
		tagInputArea= new JPanel();
		addTagButton = new JButton("Add New Tag");
		JLabel tellUserToTag = new JLabel("Enter a new tag: ");
		JTextArea enterNewTag = new JTextArea(5, 5);
		enterNewTag.setText(null);
		enterNewTag.setLineWrap(true);
		tagInputArea.setLayout(new BorderLayout());
		tagInputArea.add(tellUserToTag, BorderLayout.NORTH);
		tagInputArea.add(enterNewTag, BorderLayout.CENTER);
		addTagButton.addActionListener(new AddTagButtonListener(enterNewTag, imageMode, optionPanel));
	}

	/**
	 * Creates the delete tag button, and adds a listener so that when it is selected
	 * the confirmation JFrame will appear as  pop up window. 
	 */
	private void buildDeleteTagButton() {
		deleteTagButton = new JButton("Delete selected tags");
		deleteTagButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteConfirmationWindow.setVisible(true);
				
			}
		});
	}
	/**
	 * Updates the buttons that are the photos in the working directory so that 
	 * the new file name, after a photo has been tagged, is associated with that button
	 * 
	 */
	public void updatePhotoButtons() {
		ArrayList<File> viewingImages = imageMode.getViewingImages();
		Component[] photos = photoViewer.getComponents();

		for (int i = 0; i < photos.length; i++) {
			try {
				ActionListener listener = ((JButton) photos[i]).getActionListeners()[0];
				File listenerFile = ((ImageChooserButtonListener) listener).getImageFile();
				if (!viewingImages.get(i).equals(listenerFile)) {
					((ImageChooserButtonListener) listener).setImageFile(viewingImages.get(i));
				}
			} catch (IndexOutOfBoundsException ex) {
				System.out.println("This JButton does nothing!");
			}
		}
	}

	/**
	 * Creates the save a tag selection button and adds a listener to tag the photo when 
	 * pressed 
	 */
	public void buildSaveChangesButton() {
		saveChangesButton = new JButton("Save Changes");
		saveChangesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				imageMode.doAction();
				updatePhotoButtons();
				optionPanel.clear();
				optionPanel.reset();
				
				//when a photo is being renamed need to update the view
				//of the full log
				updateLogPanel();
			}
		});
	}

	/**
	 * Creates the button to select a working directory. Adds an action listener
	 * so that when the button is clicked a directory can be selected
	 */
	private void buildChooseDirectoryButton() {
		getDirectoryButton = new JButton("Choose Directory");
		photoRenamerWindow.add(getDirectoryButton, BorderLayout.NORTH);
		getDirectoryButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectDirectory();
			}
		});
	}

	/**
	 * selected directory button is pressed in the main GUI frame this 
	 * called which gets the images from the selected directory and reads them into
	 * the function to produce buttons from these images
	 * 
	 */
	private void selectDirectory() {
		//make pop up window that can only select directories 
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = chooser.showOpenDialog(photoRenamerWindow);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			photoViewer.removeAll();
			ArrayList<File> photosInDir = (ImageMode.imageSelectFromDir(chooser.getSelectedFile()));
			imageMode.setViewingImages(photosInDir);
			buildPhotoButtons();
			
		}
		photoViewer.setLayout(new GridLayout(5, 5));
		photoRenamerWindow.repaint();
	}
	
	
	/**
	 * Creates buttons from the images in the selected directory and 
	 * and sub directories that it contains. Makes these buttons listen for 
	 * clicks on them, and if they are clicked call ImageChoserButtonListener.
	 */
	private void buildPhotoButtons() {
		ArrayList<File> photosInDir = imageMode.getViewingImages();

		for (int i = 0; i < photosInDir.size(); i++) {
			try {
				//If there are files read them in and create an icon from them, which
				//is added to a button and placed in the window
				File imageFile = photosInDir.get(i);
				BufferedImage photo = ImageIO.read(imageFile);
				ImageIcon photoIcon = new ImageIcon(photo.getScaledInstance(150, 150, 100));
				JButton photoButton = new JButton(photoIcon);

				photoButton.setSize(new Dimension(200, 200));
				photoButton.setBorderPainted(true);
				
				//create the buttons listener
				photoButton.addActionListener(new ImageChooserButtonListener(imageFile, imageMode, optionPanel));
				photoViewer.add(photoButton);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//make it so the pane attached to the main GUI is a scroll pane
		photoViewer.setLayout(new GridLayout(2, 5));
		photoRenamerWindow.repaint();
		photoView.setViewportView(photoViewer);
	}

	/**
	 * Create the graphical user interface. Runs methods that create all
	 * working buttons, sets up the layout and instructs the program to 
	 * serialize files upon closing the program
	 */
	private void createAndShowGui() {
		
		// run all the methods that create the different parts of the GUI
		buildChooseDirectoryButton();
		buildTabbedPane();
		buildDeleteConfirmationWindow();
		
		//add panes to the main window
		photoRenamerWindow.setLayout(new BorderLayout());
		photoRenamerWindow.add(photoView, BorderLayout.CENTER);
		photoRenamerWindow.add(westPane, BorderLayout.WEST);
		photoRenamerWindow.add(getDirectoryButton, BorderLayout.NORTH);
		photoRenamerWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		photoRenamerWindow.addWindowListener(new WindowListener() {
		
		//add a window listener so that the tag and photo libraries can be
		//serialized upon closing the program
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void windowClosing(WindowEvent e) {
				imageMode.serialize();
				photoRenamerWindow.dispose();
				System.exit(0);
			}
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
			}
		});
		
		//show the main window
		photoRenamerWindow.pack();
		photoRenamerWindow.setVisible(true);
		
		//make the delete tag pop up window not visible until called
		deleteConfirmationWindow.add(deletePanel);
		deleteConfirmationWindow.pack();
		deleteConfirmationWindow.setVisible(false);
	}

	/**
	 * Create the main program GUI by creating an instance of photoRenamer and 
	 * make the GUI by creating and showing the GUI
	 * 
	 * @param args <String[]>
	 */
	public static void main(String[]args){
		

		PhotoRenamer photoRenamer = new PhotoRenamer();
		photoRenamer.createAndShowGui();

	}

}
