package main;

import java.io.IOException;

import javax.swing.SwingWorker;

class ProcessBuilderWorker extends SwingWorker<Void,Void>{
	
	private String cmd;
	
	public ProcessBuilderWorker(String cmd){
		this.cmd = cmd;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = pb.start();
			process.waitFor();
		} catch (IOException | InterruptedException e) {}
		return null;
	}
}