package photo_renamer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Read in the location of the Manager log files, and produce a list
 * off all the lines in the file in order for it to be viewed by the user.
 * 
 * @author Sara
 *
 */
public class ViewLog {
	
	/**
	 * Reads through the logger and outputs the previous names and new names for viewing 
	 * by the user in the GUI.
	 * 
	 * @param <File>logFile - the log file to be viewed
	 * @return <Queue<String>> contains all the relevant logs
	 */
	public static Queue<String> getLog(String logFileName) {
		Queue<String> logToView = new LinkedList<String>();
		//get all the files in the folder, and if its a manager log file read its
		//information into the filereader and save the information to be viewed
		File logFile = new File(logFileName);
		FileReader logReader = null;
		try {
			logReader = new FileReader(logFile);
		} catch (FileNotFoundException e1) {
				System.out.println("Cannot find file");
		}
		BufferedReader br = new BufferedReader(logReader);
		
		String s;
		try {
			while ((s = br.readLine()) != null){
				if (s.contains("SEVERE")){
					logToView.add(s);
					}
				}
			} catch (IOException e) {
		} 
	return logToView;
	}
}
