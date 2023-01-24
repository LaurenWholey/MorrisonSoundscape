import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class FolderReader {
	public static ArrayList<String> files;
	public static ArrayList<String> names; 

	public static void main(String[] args) {
		
		files = new ArrayList<String>();
		names = new ArrayList<String>();
		
		File folder = new File("folder path");
		listOfFiles(folder, files, names);
		
		for(int i=0;i<files.size(); i++) {
			  System.out.println(files.get(i));
			  System.out.println(names.get(i));
		}
		
	}
	
	//give you an arraylist with abs path names and one array list with file names for key 
	public static void listOfFiles(final File folder, ArrayList<String> files, ArrayList<String> names) {
		//for each file entry list files 
		for(final File fileEntry: folder.listFiles()) {
				//if its another folder
			if(fileEntry.isDirectory()) {
				listOfFiles(fileEntry, files, names);
				//if its a wav file 
			} else if(fileEntry.isFile() && (fileEntry.getName().endsWith(".WAV")|| fileEntry.getName().endsWith(".wav") )) {
			files.add(fileEntry.getAbsolutePath());	
			names.add(fileEntry.getName());
			}
		}
		
		Collections.sort(files);
		Collections.sort(names);
		
	}

}
