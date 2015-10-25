package main;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class VideoControls {
    protected static boolean allowRewind = true;
	
	protected static void fastForward(EmbeddedMediaPlayer video){
		allowRewind = false;
		if (!video.isMute()){
			video.mute();
		}
		if(!video.isPlaying()){
			video.pause();
		}
		video.setRate(4);
	}
	
	protected static void play(EmbeddedMediaPlayer video){
		allowRewind = false;
		if (video.isMute()){
			video.mute();
		}
		if(video.getRate() > 1){
			video.setRate(1);
		} else {
		video.pause();
		}
	}
	
	protected static void rewind(EmbeddedMediaPlayer video){
		allowRewind = true;
		video.setRate(1);
		if( video.isPlaying()){
			video.pause();
		}
		if (!video.isMute()){
			video.mute();
		}
		VideoWorker v = new VideoWorker(video);
		v.execute();
	}
}
