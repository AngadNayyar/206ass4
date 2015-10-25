package main;

import javax.swing.SwingWorker;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

//This VideoWorker class allows rewinding to occur in the background
public class VideoWorker extends SwingWorker<Void, Integer>{

	private EmbeddedMediaPlayer vid;

	public VideoWorker(EmbeddedMediaPlayer vid){
		this.vid = vid;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		while(VideoControls.allowRewind){
			vid.skip(-10);
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}

	