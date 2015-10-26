package main;

import javax.swing.SwingWorker;
import java.io.File;

/** 
 * This class takes the video and audio file to be merged, merges them and saves a preview file.
 */
public class MergeMediaWorker extends SwingWorker<Void, Void>{

	private File fileVideo;
	private File fileAudio;
	private int cTime;
	private boolean overwrite;
	
	public MergeMediaWorker(File fileV, File fileA, int currentTime, boolean overW){
		this.fileVideo = fileV;
		this.fileAudio = fileA;
		this.cTime = currentTime;
		this.overwrite = overW;		
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		String overwriteString = "combined";
		if (overwrite){
			overwriteString = "newAudio"; //if overwrite is chosen, use desired audio without combining audio from video
		}
		
		//Create silent file with cTime length
		ProcessBuilder offset = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -f lavfi -i anullsrc=r=48000:cl=mono -t " + cTime + " -acodec libmp3lame offset.mp3");
		//Add audio file to this silent file
		ProcessBuilder newAudio = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -i \"concat:offset.mp3|" + fileAudio.getAbsolutePath() + "\" -c copy newAudio.mp3");
		//Combine these two files, maintaining both audio streams
		ProcessBuilder combinedAudio = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -i " + fileVideo.getAbsolutePath() + " -i newAudio.mp3 -filter_complex amix=inputs=2:duration=first combined.mp3");
		//Add audio to video, and save it as a preview file
		ProcessBuilder newVideo = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -i " + fileVideo.getAbsolutePath()+ " -i " + overwriteString + ".mp3 -map 0:v -map 1:a " + fileVideo.getParent() + "/preview.avi");

		Process p1 = offset.start();
		p1.waitFor();
		Process p2 = newAudio.start();
		p2.waitFor();
		Process p3 = combinedAudio.start();
		p3.waitFor();
		Process p4 = newVideo.start();
		p4.waitFor();
		
		return null;
	}
	
	@Override
	protected void done(){
		MainApp.previewnow = true; //Allow previewing to now occur
		try { //Delete the temporary files that were created
			File del1 = new File("offset.mp3");
			del1.delete();
			File del2 = new File("newAudio.mp3");
			del2.delete();
			File del3 = new File("combined.mp3");
			del3.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
