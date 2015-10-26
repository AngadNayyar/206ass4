package main;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 * This class contains the methods to control any video 
 */
public class VideoControls {
    protected static boolean allowRewind = true;
	
    //Fast forward the video by setting the rate 4x the normal speed
    //Mute while forwarding
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
	
	//Play and pause the video, unmute if muted
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
	
	//Rewind the video by using a VideoWorker class
	//This skips back frames until stopped
    //Mute while rewinding
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
