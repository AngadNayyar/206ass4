package festival;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.SwingWorker;

class FemaleVoiceWorker extends SwingWorker<Void,Void>{
	
	private String text;
	private JButton btn;
	
	public FemaleVoiceWorker(String s, JButton btn){
		this.text = s;
		this.btn = btn;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		btn.setEnabled(false);
		createscm();
		ProcessBuilder wave = new ProcessBuilder("/bin/bash", "-c", "echo '" + text + "'| text2wave -o female.wav -eval female.scm");		
		
		try {
			Process process = wave.start();
			process.waitFor();
		} catch (IOException | InterruptedException e) {}
		
		listenVoice();
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
	
	protected void listenVoice() throws IOException, LineUnavailableException, UnsupportedAudioFileException{
		File test = new File("female.wav");
	    AudioInputStream audioIn = AudioSystem.getAudioInputStream(test);
	    Clip clip = AudioSystem.getClip();
	    clip.open(audioIn);
	    clip.start();
	}
	
	@Override
	protected void done(){
		btn.setEnabled(true);
		try {
			File remove = new File("female.wav");
			File remove1 = new File("female.scm");
			remove.delete();
			remove1.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}