package festival;

import java.io.IOException;

import javax.swing.JButton;
import javax.swing.SwingWorker;

/**
 * This class uses festival to play the string passed in to it.
 */
class ListenMp3Worker extends SwingWorker<Void,Void>{
	
	private String text;
	private JButton listenbutton;
	
	public ListenMp3Worker(String cmd, JButton listenbtn){
		this.listenbutton = listenbtn;
		this.text = cmd;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		listenbutton.setEnabled(false); //disable the playback button while playing this
		//Use festival to play string passed in.
		ProcessBuilder listen = new ProcessBuilder("/bin/bash", "-c", "echo '" + text + "'| festival --tts");
		try {
			Process process = listen.start();
			process.waitFor();
		} catch (IOException | InterruptedException e) {}
		return null;
	}
	
	@Override
	protected void done(){
		listenbutton.setEnabled(true); //enable the playback button again
	}
}