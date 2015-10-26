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

/**
 * This class creates an mp3 file using festival and swingworker, this is then played back
 * This, however, creates an scm file to use a [more] female voice in festival
 */
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
		createscm();//Create scm file with female voice settings
		
		//Create wave file with festival. However, use scm file to use a female voice
		ProcessBuilder wave = new ProcessBuilder("/bin/bash", "-c", "echo '" + text + "'| text2wave -o female.wav -eval female.scm");		
		
		try {
			Process process = wave.start();
			process.waitFor();
		} catch (IOException | InterruptedException e) {}
		
		listenVoice(); //Call method to playback the audio created.
		return null;
	}
	
	protected void createscm() throws IOException{
        BufferedWriter output = null;
        try {
            File file = new File("female.scm");
            output = new BufferedWriter(new FileWriter(file));
            //This buffered writter allows the creation of a scm file, which includes the settings for a 
            //female voice in festival
            output.write("(set! duffint_params '((start 260) (end 230)))\n"); //set the pitch higher (more feminine)
            output.write("(Parameter.set 'Int_Method 'DuffInt)\n");
            output.write("(Parameter.set 'Int_Target_Method Int_Targets_Default)\n");
            output.write("(Parameter.set 'Duration_Stretch 1.02)"); //Speak a little bit slower for better comprehensibility
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            if ( output != null ) output.close();
        }
	}
	
	//Playback the audio file created, so the user can listen to it.
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
		try { //Remove the temporary files created.
			File remove = new File("female.wav");
			File remove1 = new File("female.scm");
			remove.delete();
			remove1.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}