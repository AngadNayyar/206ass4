package main;



import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.media.Media;
import uk.co.caprica.vlcj.player.media.callback.nonseekable.FileInputStreamMedia;

public class MainApp1 {
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private final JFrame frame;
	JFileChooser fileChooser;
	String fileLoc = null;
	String vidLoc= "empty";
    private boolean allowRewind = true;
	
	public static void main(String[] args) {
		new NativeDiscovery().discover();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainApp();
            }
        });
	}
	
	
	private MainApp1(){
		//Sets Frame
		frame = new JFrame("VIDIVOX Trailer Editor");
		frame.setLocation(0, 0);
        frame.setSize(1050, 540);
        frame.setMinimumSize(new Dimension(320,180));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.getContentPane().setLayout(new CardLayout(0, 0));
        
        JPanel mainPanel = new JPanel();
		frame.getContentPane().add(mainPanel, "mainPanel");
		mainPanel.setLayout(new BorderLayout(0, 0));
        
        //Embedded media player
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		final EmbeddedMediaPlayer video = mediaPlayerComponent.getMediaPlayer();
		
        //Adds it to the frame and loads video
        mainPanel.add(mediaPlayerComponent, BorderLayout.CENTER);
        
        //Adds panels on the mainPanel for the buttons to be placed
        JPanel southPanel = new JPanel();
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		southPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
		JPanel northPanel = new JPanel();
		mainPanel.add(northPanel, BorderLayout.NORTH);
		northPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		
        //Components added to the north and south panels
		
		final JLabel labelW = new JLabel("Please select a video before adding sound");
		labelW.setForeground(Color.RED);
		northPanel.add(labelW);
		labelW.setVisible(false);
        
		//Calls the appropriate methods to open JFileChooser and start the video if a file is chosen
        JButton btnOpenVideo = new JButton("Open");
        btnOpenVideo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InitialiseFC(1);
				boolean t =ChooseFile();
				if (t==true){
					StartMedia();
					labelW.setVisible(false);
				}
				
			}
		});
        btnOpenVideo.setAlignmentX(Component.CENTER_ALIGNMENT);
        northPanel.add(btnOpenVideo);
		
        //Opens a JFileChooser to select audio to be placed over the video
        //checks to see if there is a video loaded and tells the user to choose one before selecting an audio file
		JButton btnSelectAudio = new JButton("Select Audio");
		btnSelectAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (vidLoc.equals("empty")){
					labelW.setVisible(true);
				} else {
					labelW.setVisible(false);
					InitialiseFC(0); 
					boolean t =ChooseFile();
					if (t==true){
						AddAudio();
					}
				}
				
			}
		});
		btnSelectAudio.setAlignmentX(Component.CENTER_ALIGNMENT);
		northPanel.add(btnSelectAudio);
		
		//Opens a new GuiTts window
		JButton btnCreateAudio = new JButton("Create");
		btnCreateAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GuiTts tts = new GuiTts();
                tts.setVisible(true);     
			}
		});
		btnCreateAudio.setAlignmentX(Component.CENTER_ALIGNMENT);
		northPanel.add(btnCreateAudio);		

		//Create an instance of VideoWorker (swingworker) to rewind the video.
		JButton btnRwd = new JButton("<<");
		btnRwd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				allowRewind = true;
				if (!video.isMute()){
					video.mute();
				}
				VideoWorker v = new VideoWorker(video);
				v.execute();
			}
		});
		southPanel.add(btnRwd);

		
		//Replay resumes the video to normal play
		JButton btnPlay = new JButton("Play");
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				allowRewind = false;
				if (video.isMute()){
					video.mute();
				}
				video.setRate(1);
			}
		});
		southPanel.add(btnPlay);
		
		//Forward works by setting the rate to 4x the normal rate of play
		JButton btnFwd = new JButton(">>");
		btnFwd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				allowRewind = false;
				if (!video.isMute()){
					video.mute();
				}
				video.setRate(4);
			}
		});
		southPanel.add(btnFwd);
		
		//Mute using built in function.
		JButton btnMute = new JButton("Mute");
		btnMute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				video.mute();
			}
		});
		southPanel.add(btnMute);
        
        
        //End of GUI Components
		
	}
	
	
	//Starts media file, given that it has been chosen by the user
	private void StartMedia(){
		if (fileLoc == null){
			return;
		} else{
			vidLoc = fileLoc;
			mediaPlayerComponent.getMediaPlayer().playMedia(vidLoc);
		}
		
	}
	
	//Initializes JFileChooser, either audio or video, depending on what integer is passed into the method
	private void InitialiseFC(int type){
		fileChooser = new JFileChooser(new File("c:\\"));
		FileFilter filter;
		if (type == (1)){
			filter = new FileNameExtensionFilter("Video Files", new String[] {"avi","mp4"});
		}else{
			filter = new FileNameExtensionFilter("Audio File", new String[] {"mp3","wav"});
		}
        
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Choose a file");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);		
	}
	
	//Opens the JFileChooser and gets the path of the file chosen 
	private boolean ChooseFile(){
		int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION){
			File fileToOpen = fileChooser.getSelectedFile();
			fileLoc = fileToOpen.getAbsolutePath();
			fileChooser.setVisible(false);
			return true;
		}else if(result == JFileChooser.CANCEL_OPTION){
			fileChooser.setVisible(false);	
		}
        return false;
	}
	
	//Adds audio to the start of the video file using ffmpeg, and plays the created video
	private void AddAudio(){
		String audioLoc = fileLoc;
		String cmd = "ffmpeg -i "+ vidLoc +" -i "+ audioLoc +" -c:v copy -c:a aac -strict experimental -map 0:v:0 -map 1:a:0 output.mp4" ;
		
		File f = new File("output.mp4");
		if(f.exists()){
			f.delete();
		}
		
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = pb.start();
			process.waitFor();
		} catch (IOException | InterruptedException e) {}
		
		mediaPlayerComponent.getMediaPlayer().playMedia("output.mp4");
		
	}
	

	//This VideoWorker class allows rewinding to occur in the background
	public class VideoWorker extends SwingWorker<Void, Integer>{

		private EmbeddedMediaPlayer vid;
		private int fOrB;

		public VideoWorker(EmbeddedMediaPlayer vid){
			this.vid = vid;
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			while(allowRewind){
				vid.skip(-10);
			}
			return null;
		}
		
	}


}
