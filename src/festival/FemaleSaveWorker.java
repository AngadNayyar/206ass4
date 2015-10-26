package festival;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.SwingWorker;

class FemaleSaveWorker extends SwingWorker<Void,Void>{
	
	private String text;
	private File name;
	
	public FemaleSaveWorker(String s, File cmd){
		this.text = s;
		this.name = cmd;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		createscm();
		ProcessBuilder wave = new ProcessBuilder("/bin/bash", "-c", "echo '" + text + "'| text2wave -o " + name.getName() + ".wav -eval female.scm");
		ProcessBuilder mp3create = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -y -i " + name.getName() + ".wav -f mp3 " + name.getAbsolutePath() + ".mp3");
		
		try {
			Process process = wave.start();
			process.waitFor();
			Process process1 = mp3create.start();
			process1.waitFor();
		} catch (IOException | InterruptedException e) {}
		return null;
	}
	
	protected void createscm() throws IOException{
        BufferedWriter output = null;
        try {
            File file = new File("female.scm");
            output = new BufferedWriter(new FileWriter(file));
            output.write("(set! duffint_params '((start 260) (end 230)))\n");
            output.write("(Parameter.set 'Int_Method 'DuffInt)\n");
            output.write("(Parameter.set 'Int_Target_Method Int_Targets_Default)\n");
            output.write("(Parameter.set 'Duration_Stretch 1.02)");
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            if ( output != null ) output.close();
        }
	}
	
	@Override
	protected void done(){
		try {
			File remove = new File(name.getName() + ".wav");
			File remove1 = new File("female.scm");
			remove.delete();
			remove1.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}