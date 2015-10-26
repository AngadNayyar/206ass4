package main;

import javax.swing.SwingWorker;
import java.io.File;

public class SaveVideoWorker extends SwingWorker<Void, Void>{

	private File outVideo;
	private File fileVideo;
	private File fileAudio;
	private int cTime;
	private boolean overwrite;
	
	public SaveVideoWorker(File outname, File fileV, File fileA, int currentTime, boolean overW){
		this.outVideo = outname;
		this.fileVideo = fileV;
		this.fileAudio = fileA;
		this.cTime = currentTime;
		this.overwrite = overW;		
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		String overwriteString = "combined";
		if (overwrite){
			overwriteString = "newAudio";
		}
		
		ProcessBuilder offset = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -f lavfi -i anullsrc=r=48000:cl=mono -t " + cTime + " -acodec libmp3lame offset.mp3");
		ProcessBuilder newAudio = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -i \"concat:offset.mp3|" + fileAudio.getAbsolutePath() + "\" -c copy newAudio.mp3");
		ProcessBuilder combinedAudio = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -i " + fileVideo.getAbsolutePath() + " -i newAudio.mp3 -filter_complex amix=inputs=2:duration=first combined.mp3");
		ProcessBuilder newVideo = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -i " + fileVideo.getAbsolutePath()+ " -i " + overwriteString + ".mp3 -map 0:v -map 1:a " + outVideo.getAbsolutePath() + ".avi");
		
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
		System.out.println("Worked!");
	}

}
