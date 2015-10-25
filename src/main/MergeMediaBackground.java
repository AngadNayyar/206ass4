package main;

import javax.swing.SwingWorker;
import java.io.File;
import java.io.IOException;

public class MergeMediaBackground extends SwingWorker<Void, Void>{

	private File fileVideo;
	private File fileAudio;
	private int time;
	private boolean overwrite;
	
	public MergeMediaBackground(File fileV, File fileA, int currentTime, boolean overW){
		fileVideo = fileV;
		fileAudio = fileA;
		time = currentTime;
		overwrite = overW;		
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		String overwriteString = "newAudio";
		if (overwrite){
			overwriteString = "combined";
		}
		
		ProcessBuilder offset = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -f lavfi -i anullsrc -i" 
		+ fileVideo.getAbsolutePath() + "-t " + time + "-c:a copy offset.mp3");
		ProcessBuilder newAudio = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -i \"concat:offset.mp3|"
				+ fileAudio.getAbsolutePath() + "\" -c copy newAudio.mp3");
		ProcessBuilder combinedAudio = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -i " + fileVideo.getAbsolutePath()
		+ "-i newAudio.mp3 amix=:duration=first combined.mp3");
		ProcessBuilder newVideo = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -i " + fileVideo.getAbsolutePath()
		+ " -i " + overwriteString + ".mp3 -map 0:v -map 1:a preview.avi");
		
		Process offsetter = offset.start();
		offsetter.waitFor();
		Process createAudio = newAudio.start();
		createAudio.waitFor();
		Process createVideo = newVideo.start();
		createVideo.waitFor();
		return null;
	}
	
	@Override
	protected void done(){
		try {
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
