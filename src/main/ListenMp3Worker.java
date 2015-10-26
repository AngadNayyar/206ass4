package main;

import java.io.IOException;

import javax.swing.JButton;
import javax.swing.SwingWorker;

class ListenMp3Worker extends SwingWorker<Void,Void>{
	
	private String text;
	private JButton listenbutton;
	
	public ListenMp3Worker(String cmd, JButton listenbtn){
		this.listenbutton = listenbtn;
		this.text = cmd;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		listenbutton.setEnabled(false);
		ProcessBuilder listen = new ProcessBuilder("/bin/bash", "-c", "echo '" + text + "'| festival --tts");
		try {
			Process process = listen.start();
			process.waitFor();
		} catch (IOException | InterruptedException e) {}
		return null;
	}
	
	@Override
	protected void done(){
		listenbutton.setEnabled(true);
	}
}