package festival;

import java.io.File;
import java.io.IOException;

import javax.swing.SwingWorker;

/**
 * This class creates an mp3 file using festival and swingworker, this is saved to a desired location.
 */
class CreateMp3Worker extends SwingWorker<Void,Void>{
	
	private String text;
	private File name;
	
	public CreateMp3Worker(String s, File cmd){
		this.text = s;
		this.name = cmd;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		//Create wave file using text2wave
		ProcessBuilder wave = new ProcessBuilder("/bin/bash", "-c", "echo '" + text + "'| text2wave -o " + name.getName() + ".wav");
		//Create mp3 file from wave file
		ProcessBuilder mp3create = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -y -i " + name.getName() + ".wav -f mp3 " + name.getAbsolutePath() + ".mp3");
		
		try {
			Process process = wave.start();
			process.waitFor();
			Process process1 = mp3create.start();
			process1.waitFor();
		} catch (IOException | InterruptedException e) {}
		return null;
	}
	
	@Override
	protected void done(){
		try { //Delete the wave file that was created
			File remove = new File(name.getName() + ".wav");
			remove.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}