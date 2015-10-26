package festival;

import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.SwingWorker;

class CreateMp3Worker extends SwingWorker<Void,Void>{
	
	private String text;
	private File name;
	
	public CreateMp3Worker(String s, File cmd){
		this.text = s;
		this.name = cmd;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		ProcessBuilder wave = new ProcessBuilder("/bin/bash", "-c", "echo '" + text + "'| text2wave -o " + name.getName() + ".wav");
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
		try {
			File remove = new File(name.getName() + ".wav");
			remove.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}