# MorrisonSoundscape
Wav file processing


This program was developed to quantify a sound ordinance placed in Morrison Colorado. Data was collected around Morrison using audio moths in an effort to model the sound issues.
The main class in this code is the Wav.java class. This class contains methods to find the min and max and the frame and time of both the min and max. This program has the capability to find this data for multiple wav files contained in multiple folders. 
A folder reader is also included that is utilized in the Wav class. This class has one simple method that returns the files and directories within a directory.
The whole program is built off of the WavFile and WaveFileException classes found here: http://www.labbookpages.co.uk/audio/javaWavFiles.html#methods .
