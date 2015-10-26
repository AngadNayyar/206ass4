package main;

import javax.swing.SwingWorker;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
/**
 * This VideoWorker class allows rewinding to occur in the background by skipping a few frames continuously
 * until stopped
 */
public class VideoWorker extends SwingWorker<Void, Integer>{

	private EmbeddedMediaPlayer vid;

	public VideoWorker(EmbeddedMediaPlayer vid){
		this.vid = vid; //Allow video to be passed into this constructor
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		while(VideoControls.allowRewind){ //Keep rewinding until allowRewind bit is turned off
			vid.skip(-10); //Skip video frames backwards
			try {
				Thread.sleep(1); //Sleep the thread for 1millisecond to reduce overflow and control pace of rewinding
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}

	