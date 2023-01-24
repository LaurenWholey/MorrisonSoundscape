
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList; 
import java.util.HashMap ; 
public class Wav {
	//LAUREN WHOLEY 
	//University of Denver 
	//This class holds all the methods to extract information from a wav 
	//file like mins, maxs, and the frames and times where those occur 
	
	
	
	
	//stays the same between all methods 
	private static double timeChange; 
	//gets reset to -1 everytime its used - shouldnt cause problems 
	private static int totalFrames;
	private static FolderReader fr; 
	//array of doubles holding time and max for the value 
	private static HashMap<String, double[]> maxes;
	//private static  String[] args; 

	public static void main(String[] arg) throws IOException, WavFileException {
		//write out to a text file for each folder
		//really strong comments 
		//9 locations - 7 days each - file for each day/hour
		//instruction manual explaining code 
		
		String s = "132";
		double d = .45; 
		String result = s + d; 
		System.out.println("result:" + result);
		
		//wav file to get sample rate (take from testFolder)
		WavFile testWavFile = WavFile.openWavFile(new File("/Users/laurenwholey/Downloads/WavIS/am02/20200807_000500.WAV"));
		//testWavFile.display();
		File folder = new File("/Users/laurenwholey/Downloads/WavIS");
		
		timeChange = 1.0/testWavFile.getSampleRate();
		
		
		
		//hash map to fill with maxes and their respective time stamps 
		 maxes = new HashMap<>();
		 fr = new FolderReader();
		 
		 ArrayList<String> files = new ArrayList<String>();
			//array list with just the names 
			ArrayList<String> names = new ArrayList<String>();
			//fill array list with path names 
			

		testWavFile.close();
	
		//double averageMax = maxesOfFolder(folder);
		
		extractCSV(folder); 
		
	}
		
	//length of windows is how long your individual files should be 
	static void totalMaxes(File folder, int lengthOfWindows, String oFName, String iFName) {
		
		//need the length of the folder 
		
		ArrayList<String> files = new ArrayList<String>();
		//array list with just the names 
		ArrayList<String> names = new ArrayList<String>();
		//fill array list with path names 
		FolderReader.listOfFiles(folder, files, names);
		int numFiles = files.size();
		System.out.println("numFiles: " + numFiles);
		//length of result wavFile in frames
		int length = numFiles*lengthOfWindows; 
		WavFile result = null;
		
		//System.out.println("length: " + length);
		try {
			result = WavFile.newWavFile(new File(oFName), 1, length, 16, 48000);
			double[] buffer = new double[lengthOfWindows];		 
			//for each absolute path 
			for(String source : files) {
				//System.out.println(source);
				//temp file now has the max - makes a wav file of length length of Windows 
				 maxWindow(source,lengthOfWindows, iFName);
				WavFile max = WavFile.openWavFile(new File(iFName));
				
				//want to get all the frames from this max wav file 
				int framesRead =  max.readFrames(buffer, lengthOfWindows);
				//close source 
				max.close();
				//write for every file in the folder 
				result.writeFrames(buffer, framesRead);
			}
			
			result.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WavFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	//length is how many frames you want the wav file to be 
	static void maxWindow(String ifName, int length, String oFName) throws IOException, WavFileException {
		
		WavFile result = null;
	int framesRead=0;
			//create new wav file to fill 
			try {
				result = WavFile.newWavFile(new File(oFName), 1, length, 16, 48000);
				//read sample 1000 times and then you call read frames in a chunk of length size 
				//this advances the pointer to the place you want it to be 
				
				//index is where max is 
				int index = maxFrame(ifName);
				WavFile source = WavFile.openWavFile(new File(ifName));
				int numChannels = source.getNumChannels();
				double[] buffer = new double[length * numChannels];
				do {
				//if the max is in the beginning 
				if(index<length) {
					//here you dont have to skip anything bc you start at the beg 
				
					 framesRead = source.readFrames(buffer, length);
					
				}else if(index>source.getNumFrames()-length) {
					//this handles when the max is at the end 
					//offset is where you need to skip to 
					int offset = (int)source.getNumFrames()-length;
				
					//skips to offset 
					for(int i=0; i<offset; i++) {
						source.readFrames(buffer, 1);
					}
				
					
					 framesRead = source.readFrames(buffer, length);
				}else {
					//handles if the max is in the middle 
						int offset = index-((length)/2); 
					//	System.out.println("max is in the middle");
						//skips to offset 
						for(int i=0; i<offset; i++) {
							source.readFrames(buffer, 1);
						}
						framesRead = source.readFrames(buffer, length);
	
				}
			
				//write what was read in the result wavFile
					result.writeFrames(buffer, framesRead);
				
				}while(framesRead!=0);
				//close both files to save changes 
				source.close();
				result.close();
			} catch (IOException | WavFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}
	
	
	//for each folder in a overarching folder call maxes of folder
	//and extract all the CSV for each folder
	static void extractCSV(final File folder) throws IOException, WavFileException {
		// create a hash map for folder name and max of that folder
		HashMap<String, Double> folderAndMax = new HashMap<>();
		
		for(final File fileEntry: folder.listFiles(File::isDirectory)) {
			//for each folder make a new csv file with the name being the folder name
		
				double av = maxesOfFolder(fileEntry);
				folderAndMax.put(fileEntry.getName(), av);
				//System.out.println(fileEntry.getName());
		
			
			
		}
	}
	

	
	//returns the average of the folder 
	static double maxesOfFolder(File folder) throws IOException, WavFileException {	
		//to store the files
		ArrayList<String> files = new ArrayList<String>();
		//array list with just the names 
		ArrayList<String> names = new ArrayList<String>();
		//fill array list with path names 
		FolderReader.listOfFiles(folder, files, names);
	
		int count = 0; 
		double total=0;
		
		String CSVname = folder.getName() + ".csv";
		
		FileOutputStream fos = new FileOutputStream(CSVname, true);
		PrintWriter pw = new PrintWriter(fos);
		
		pw.println("File Name, Time, Max");
		
		//create wave files and get their max 
		for(int i=0; i<files.size(); i++) {
			//for max and respective time stamp
			
			double[] value; 
			String name = names.get(i);
			
			value = findMax(files.get(i));
			
			//manipulate key into sateTime adjusted by timeAdded 
			
			int timeAdded = (int)value[0];
			String dateTime;
			if(timeAdded<10) {
				dateTime = name.substring(0,13) + "0" + timeAdded;
			}else {
				dateTime = name.substring(0,13) + timeAdded;
			}	
		
			pw.println(dateTime + ", " + value[0] + ", " + value[1]);
			//path is files.get(i)
		
			maxes.put(dateTime,value);
			total = total + value[1];
			count++;
		}
		pw.close();
		System.out.println(".csv with maxes has been created");
		
		return total/count; 
	}
	
	
	static double[] findMax(String fName) throws IOException, WavFileException {
		WavFile wav = WavFile.openWavFile(new File(fName));
		try {
		int numChannels = wav.getNumChannels();
		// Create a buffer of 100 frames
		double[] buffer = new double[100 * numChannels];
		double max = Double.MIN_VALUE;
		//starts at -1 because 0 mod anything is 0
		totalFrames = -1;
		// time of max 
		double maxTime = 0;
		int framesRead=0;
		do {
		
			// Read frames into buffer
			framesRead = wav.readFrames(buffer, 100);
			
			// Loop through frames and look for minimum and maximum value
			for (int s = 0; s < framesRead * numChannels; s++) {
				//System.out.println("in for");
				if(s%numChannels==0){
				totalFrames++;
				}
				if (buffer[s] > max) {
					//System.out.println("in if");
					max = buffer[s];
					maxTime = totalFrames * timeChange;
					//System.out.println("totalFrames:" + totalFrames);
					//System.out.println("ttimechange:" + timeChange);
					//System.out.println(maxTime);
				}
			}
		} while (framesRead != 0);

		double[] info = new double[2];
		info[0]=maxTime; 
		info[1]=max;
		return info; 
		}finally {
			wav.close();
		}
	}
	
	
	
	//returns the frame where the max is
	static int maxFrame(String fName) throws IOException, WavFileException {
		//try and finally - no matter what happens inside it will always call the finally 
		
		WavFile wav = WavFile.openWavFile(new File(fName));
		try {
	
		int framesRead = 0; 
		int numChannels = wav.getNumChannels();
		// Create a buffer of 100 frames
		double[] buffer = new double[100 * numChannels];
		double max = Double.MIN_VALUE;
		int maxFrame =0; 
		//starts at -1 because 0 mod anything is 0
		totalFrames = -1;
		do {
			// Read frames into buffer
			 framesRead = wav.readFrames(buffer, 100);
	
			// Loop through frames and look for minimum and maximum value
			for (int s = 0; s < framesRead * numChannels; s++) {
				if(s%numChannels==0){
				totalFrames++;
				}
				if (buffer[s] > max) {
					max = buffer[s];
					maxFrame = totalFrames;
					
				}
			}
		} while (framesRead != 0);

		// Close the wavFile
	
		
		return maxFrame;
		}finally {
			wav.close();
		}
	}

	//this prints out the time at each frame
	static void printTimeStamps(WavFile wav) throws IOException, WavFileException {
		int framesRead = 0; 
		//bufferedwriter - writes in chunks so you dont go too fast 
		BufferedWriter fw = new BufferedWriter(new FileWriter("ClearMaxData.csv"));
		
		int numChannels = wav.getNumChannels();
		double[] buffer = new double[100 * numChannels];
		double time = 0;
		totalFrames = -1;
		int count=0;
		do {
			framesRead = wav.readFrames(buffer, 100);
		
			for (int s = 0; s < framesRead * numChannels; s++) {
				double value = buffer[s];

				if(s%numChannels==0){
					totalFrames++;
				
					}
				time = totalFrames * timeChange ;
			
				fw.write(time + "," + value + "\n");
				count++; 
				

			}
		} while (framesRead != 0);
	
		fw.close();
		System.out.println(count);

	}

	//This prints out the amplitude at every frame
	static void printData(WavFile wav) throws IOException, WavFileException {
		int framesRead = 0; 
		//file for the data to go to 
		FileWriter fw = new FileWriter("WavTestData.txt");
		int count =0; 
		int numChannels = wav.getNumChannels();
		// Create a buffer of 100 frames
		double[] buffer = new double[100 * numChannels];

		do {

			framesRead = wav.readFrames(buffer, 100);

			for (int s = 0; s < framesRead * numChannels; s++) {
				double value = buffer[s];
				fw.write(String.valueOf(value)+"\n");
				count++;
				System.out.printf("%f\n", buffer[s]);
			}
		} while (framesRead != 0);
		fw.close();

		System.out.println(count);
		
	}

	

}
