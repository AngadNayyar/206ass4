package festival;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.SwingWorker;

/**
 * This class creates an mp3 file using festival and swingworker, this is saved to a desired location.
 * This, however, creates an scm file to use a [more] female voice in festival
 */
class FemaleSaveWorker extends SwingWorker<Void,Void>{
	
	private String text;
	private File name;
	
	public FemaleSaveWorker(String s, File cmd){
		this.text = s;
		this.name = cmd;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		createscm(); //Create scm file with female voice settings
		
		//Create wave file with festival. However, use scm file to use a female voice
		ProcessBuilder wave = new ProcessBuilder("/bin/bash", "-c", "echo '" + text + "'| text2wave -o " + name.getName() + ".wav -eval female.scm");
		//Create an mp3 file from the wave file
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
	
	@Override
	protected void done(){
		try { //Remove the temporary files created
			File remove = new File(name.getName() + ".wav");
			File remove1 = new File("female.scm");
			remove.delete();
			remove1.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}