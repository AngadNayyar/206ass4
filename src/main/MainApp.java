/** Author: Angad Nayyar upi: anay794
 * This video editor program allows the user to add audio to a video file at any given point.
 * The code is split into two packages, one is "main" which contains the video related part of the code
 * and the other is "festival" which contains the audio related part of the code.*/

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

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import festival.AudioSynthesiser;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JSlider;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;


public class MainApp {
	
	//Declare fields
	public static boolean previewnow;
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private EmbeddedMediaPlayer video;
	private final JFrame frame;
	JFileChooser fileChooser;
	String fileLoc = null;
	String vidLoc= "empty";
	private String previewPath;
	private Timer timeUpdate;
	private int currentTime;
	private File fileVideo;
	private File fileAudio;
	private boolean overwrite;
	private boolean addToCurrentTime;
	
	/*Main method to run the program.*/
	public static void main(String[] args) {
		new NativeDiscovery().discover();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainApp();
            }
        });
	}
	
	//This creates the frame and GUI components for the video player.
	private MainApp(){
		
		//Sets Frame
		frame = new JFrame("VIDIVOX Trailer Editor");
		frame.setLocation(0, 0);
        frame.setSize(1143, 633);
        frame.setMinimumSize(new Dimension(800,195));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.getContentPane().setLayout(new CardLayout(0, 0));
        
        JPanel mainPanel = new JPanel();
		frame.getContentPane().add(mainPanel, "mainPanel");
		mainPanel.setLayout(new BorderLayout(0, 0));
        
        //Embedded media player is created. This allows us to play the video.
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		video = mediaPlayerComponent.getMediaPlayer();
		
        //Adds it to the frame and loads video
        mainPanel.add(mediaPlayerComponent, BorderLayout.CENTER);
        
        //Adds panels on the mainPanel for the buttons to be placed
        JPanel southPanel = new JPanel();
		mainPanel.add(southPanel, BorderLayout.SOUTH);
        
		JPanel eastPanel = new JPanel();
		mainPanel.add(eastPanel, BorderLayout.EAST);
		
		JPanel northPanel = new JPanel();
		mainPanel.add(northPanel, BorderLayout.NORTH);
		northPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		//Warning label if user tries to add sound without selecting a video.
		final JLabel labelW = new JLabel("Please select a video before adding sound");
		labelW.setForeground(Color.RED);
		northPanel.add(labelW);
		labelW.setVisible(false);
		
		//Calls the appropriate methods to open JFileChooser and start the video if a file is chosen
        JButton btnOpenVideo = new JButton("Open Video");
        btnOpenVideo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File tempFile;
				InitialiseFC(1);
				tempFile = ChooseFile();
				if (tempFile != null){
					fileVideo = tempFile;
					video.playMedia(fileVideo.getAbsolutePath());
					video.setRepeat(true);
					labelW.setVisible(false);
				}
				
			}
		});
        btnOpenVideo.setAlignmentX(Component.CENTER_ALIGNMENT);
        northPanel.add(btnOpenVideo);
        
        //Returns to unedited video from preview mode, and displays all the editing buttons again.
        JButton btnReturnToEditor = new JButton("Return to unedited video");
        btnReturnToEditor.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		video.playMedia(fileVideo.getAbsolutePath());
        		video.setRepeat(true); //Allows video to repeat once the end is reached.
        		btnOpenVideo.setVisible(true);
        		eastPanel.setVisible(true);
        		btnReturnToEditor.setVisible(false);
        		File del1 = new File(previewPath); //Delete the preview file once preview is finished.
        		del1.delete();
        	}
        });
        northPanel.add(btnReturnToEditor);
        btnReturnToEditor.setVisible(false);
		GridBagLayout gbl_eastPanel = new GridBagLayout();
		gbl_eastPanel.columnWidths = new int[]{5, 125, 5};
		gbl_eastPanel.rowHeights = new int[]{41, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_eastPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_eastPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		eastPanel.setLayout(gbl_eastPanel);
		
		//Opens a new window which allows the creation of audio via festival to occur.
		JButton btnCreateAudio = new JButton("Create New Audio");
		GridBagConstraints gbc_btnCreateAudio = new GridBagConstraints();
		gbc_btnCreateAudio.insets = new Insets(0, 0, 5, 0);
		gbc_btnCreateAudio.gridx = 1;
		gbc_btnCreateAudio.gridy = 0;
		eastPanel.add(btnCreateAudio, gbc_btnCreateAudio);
		btnCreateAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AudioSynthesiser tts = new AudioSynthesiser();
                tts.setVisible(true);     
			}
		});
		btnCreateAudio.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//Allows preview to occur. This starts a swingworker that creates the preview video. This then turns
		//into preview mode and let's the timer (further down) play the preview video.
		JButton btnPreview = new JButton("Preview");
		btnPreview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int mergeTime = 0;
				if (addToCurrentTime){ //Add to current video time if box is ticked.
					mergeTime = currentTime;
				}
				MergeMediaWorker preview = new MergeMediaWorker(fileVideo, fileAudio, mergeTime, overwrite);
				preview.execute();
				previewPath = fileVideo.getParent() + "/preview.avi"; //Set path to preview file.
				btnReturnToEditor.setVisible(true); //Next few lines turn editor into preview mode.
				eastPanel.setVisible(false);
				btnOpenVideo.setVisible(false);
				btnReturnToEditor.setEnabled(false);
				btnReturnToEditor.setText("Loading... Please Wait!");
			}
		});
		GridBagConstraints gbc_btnPreview = new GridBagConstraints();
		btnPreview.setEnabled(false);
		gbc_btnPreview.insets = new Insets(0, 0, 5, 0);
		gbc_btnPreview.gridx = 1;
		gbc_btnPreview.gridy = 5;
		eastPanel.add(btnPreview, gbc_btnPreview);
		
		//Checkbox to allow user to overwrite audio or not.
		JCheckBox chckbxReplaceAudio = new JCheckBox("Overwrite audio");
		chckbxReplaceAudio.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				overwrite = chckbxReplaceAudio.isSelected();
			}
		});
		GridBagConstraints gbc_chckbxReplaceAudio = new GridBagConstraints();
		gbc_chckbxReplaceAudio.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxReplaceAudio.gridx = 1;
		gbc_chckbxReplaceAudio.gridy = 3;
		eastPanel.add(chckbxReplaceAudio, gbc_chckbxReplaceAudio);
		
		//Text to show which audio file is selected.
		JLabel lblAudioSelected = new JLabel("");
		lblAudioSelected.setEnabled(false);
		GridBagConstraints gbc_lblAudioSelected = new GridBagConstraints();
		gbc_lblAudioSelected.insets = new Insets(0, 0, 5, 0);
		gbc_lblAudioSelected.gridx = 1;
		gbc_lblAudioSelected.gridy = 1;
		eastPanel.add(lblAudioSelected, gbc_lblAudioSelected);		
		
		//Checkbox to add audio to current video time, or to the start of the video.
		JCheckBox chckbxAddToCurrent = new JCheckBox("Add to current time");
		chckbxAddToCurrent.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				addToCurrentTime = chckbxAddToCurrent.isSelected();
			}
		});
		GridBagConstraints gbc_chckbxAddToCurrent = new GridBagConstraints();
		gbc_chckbxAddToCurrent.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxAddToCurrent.gridx = 1;
		gbc_chckbxAddToCurrent.gridy = 4;
		eastPanel.add(chckbxAddToCurrent, gbc_chckbxAddToCurrent);
		
		//Button to merge and save video with audio file.
		JButton btnSaveNoPreview = new JButton("Merge and save");
		btnSaveNoPreview.setEnabled(false);
		btnSaveNoPreview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File temp;
				InitialiseFC(1); //File chooser in save dialog to get desired file name
				temp = SaveFile();
				SaveVideoWorker saveWorker = new SaveVideoWorker(temp, fileVideo, fileAudio, currentTime, overwrite);
				saveWorker.execute(); //Execute swing worker to save video.
			}
		});
		GridBagConstraints gbc_btnSaveNoPreview = new GridBagConstraints();
		gbc_btnSaveNoPreview.insets = new Insets(0, 0, 5, 0);
		gbc_btnSaveNoPreview.gridx = 1;
		gbc_btnSaveNoPreview.gridy = 7;
		eastPanel.add(btnSaveNoPreview, gbc_btnSaveNoPreview);
		
		//Opens a JFileChooser to select audio to be used
        //checks to see if there is a video loaded and tells the user to choose one before selecting an audio file
		JButton btnSelectAudio = new JButton("Select Audio");
		GridBagConstraints gbc_btnSelectAudio = new GridBagConstraints();
		gbc_btnSelectAudio.insets = new Insets(0, 0, 5, 0);
		gbc_btnSelectAudio.gridx = 1;
		gbc_btnSelectAudio.gridy = 2;
		eastPanel.add(btnSelectAudio, gbc_btnSelectAudio);
		btnSelectAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File tempFile;
				if (fileVideo == null){
					labelW.setVisible(true);
				} else {
					labelW.setVisible(false);
					InitialiseFC(0); 
					tempFile = ChooseFile();
					if (tempFile != null){
						fileAudio = tempFile; //set this mp3 file as the mp3 file field for the code
						lblAudioSelected.setText(fileAudio.getName());
						btnPreview.setEnabled(true); //enable preview and save buttons
						btnSaveNoPreview.setEnabled(true);
					}
				}
			}
		});
		btnSelectAudio.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//southPanel layout settings
		GridBagLayout gbl_southPanel = new GridBagLayout();
		gbl_southPanel.columnWidths = new int[]{40, 0, 40, 40, 40, 81, 73, 91, 40, 0, 0, 0, 0};
		gbl_southPanel.rowHeights = new int[]{30, 41, 0};
		gbl_southPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_southPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		southPanel.setLayout(gbl_southPanel);
		
		//Label to display current time				
		JLabel lblTime = new JLabel("00:00");
		GridBagConstraints gbc_lblTime = new GridBagConstraints();
		gbc_lblTime.insets = new Insets(0, 0, 5, 5);
		gbc_lblTime.gridx = 1;
		gbc_lblTime.gridy = 0;
		southPanel.add(lblTime, gbc_lblTime);
		
		//Slider to show where in the video we are up to.
		//This allows the user to move the slider and move through the video.
		JSlider sliderVideo = new JSlider();
		sliderVideo.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				video.pause();
				timeUpdate.stop(); //Pause timer (updater)
			}
			@Override
			public void mouseReleased(MouseEvent arg) {
				float sliderVideoTime = sliderVideo.getValue()/5000.0f;
				video.setPosition(sliderVideoTime);
				video.play();
				timeUpdate.restart(); //Restart timer (updater)
			}
		});
		GridBagConstraints gbc_sliderVideo = new GridBagConstraints();
		gbc_sliderVideo.fill = GridBagConstraints.HORIZONTAL;
		gbc_sliderVideo.gridwidth = 10;
		gbc_sliderVideo.insets = new Insets(0, 0, 5, 0);
		gbc_sliderVideo.gridx = 2;
		gbc_sliderVideo.gridy = 0;
		sliderVideo.setValue(0);
		sliderVideo.setMaximum(5000);
		southPanel.add(sliderVideo, gbc_sliderVideo);
		
		//The timer below checks the elapsed video time and updates the slider every 100ms
		//It also plays the preview video if the previewnow bit has been enabled
		timeUpdate = new Timer(100, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				int min;
				int sec;
				String minString;
				String secString;
				
				currentTime = (int) (video.getTime()/1000); //Get video current time in seconds
				min = (int) (video.getTime()/60000); //Get video time in minutes
				sec = (int) ((video.getTime()/1000)%60); //Get video time in seconds
				minString = "" + min;
				if (min < 10){
					minString = "0" + min;
				}
				secString = "" + sec;
				if (sec < 10){
					secString = "0" + sec;
				}
				String timeMinSec = minString + ":" + secString;
				lblTime.setText(timeMinSec);	//set label to current time 
				sliderVideo.setValue((int) (video.getPosition() * 5000.0f)); //move slider along
				
				if (previewnow){ //if previewnow boolean is true
					video.playMedia(previewPath); //play the preview video
					video.setRepeat(true);
					btnReturnToEditor.setEnabled(true);
					btnReturnToEditor.setText("Return to unedited video");
					previewnow = false;
				}
			}
		});
		timeUpdate.start(); //Start the timer (updater)
				

		//Forward by calling the fast forward function in video controls
		JButton btnMute = new JButton("Mute");
		btnMute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				video.mute();
			}
		});
		
		//Forward by calling the fast forward function in video controls
		JButton btnFwd = new JButton(">>");
		btnFwd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				VideoControls.fastForward(video);
			}
		});
		
				
		//Play/Pause by calling the play/pause function in video controls
		JButton btnPlay = new JButton("> ||");
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				VideoControls.play(video);
			}
		});
				
		//Rewind by calling the rewind function in video controls
		JButton btnRwd = new JButton("<<");
		btnRwd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				VideoControls.rewind(video);
			}
		});
		GridBagConstraints gbc_btnRwd = new GridBagConstraints();
		gbc_btnRwd.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnRwd.insets = new Insets(0, 0, 0, 5);
		gbc_btnRwd.gridx = 2;
		gbc_btnRwd.gridy = 1;
		southPanel.add(btnRwd, gbc_btnRwd);
		GridBagConstraints gbc_btnPlay = new GridBagConstraints();
		gbc_btnPlay.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnPlay.insets = new Insets(0, 0, 0, 5);
		gbc_btnPlay.gridx = 3;
		gbc_btnPlay.gridy = 1;
		southPanel.add(btnPlay, gbc_btnPlay);
		GridBagConstraints gbc_btnFwd = new GridBagConstraints();
		gbc_btnFwd.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnFwd.insets = new Insets(0, 0, 0, 5);
		gbc_btnFwd.gridx = 4;
		gbc_btnFwd.gridy = 1;
		southPanel.add(btnFwd, gbc_btnFwd);
		GridBagConstraints gbc_btnMute = new GridBagConstraints();
		gbc_btnMute.insets = new Insets(0, 0, 0, 5);
		gbc_btnMute.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnMute.gridx = 5;
		gbc_btnMute.gridy = 1;
		southPanel.add(btnMute, gbc_btnMute);
		
		JLabel lblVolume = new JLabel("Volume");
		GridBagConstraints gbc_lblVolume = new GridBagConstraints();
		gbc_lblVolume.insets = new Insets(0, 0, 0, 5);
		gbc_lblVolume.gridx = 7;
		gbc_lblVolume.gridy = 1;
		southPanel.add(lblVolume, gbc_lblVolume);
		
		//Slider to control the video audio levels.
		JSlider sliderVolume = new JSlider();
		GridBagConstraints gbc_sliderVolume = new GridBagConstraints();
		gbc_sliderVolume.insets = new Insets(0, 0, 0, 5);
		gbc_sliderVolume.gridwidth = 3;
		gbc_sliderVolume.gridx = 8;
		gbc_sliderVolume.gridy = 1;
		sliderVolume.setValue(100);
		sliderVolume.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg) {
				int volume = sliderVolume.getValue();	
				video.setVolume(volume); //Set the video volume to slider level
			}
		});
		southPanel.add(sliderVolume, gbc_sliderVolume);
        
        
        //End of GUI Components
		
	}
	
	//Initializes JFileChooser, either audio or video, depending on what integer is passed into the method
	private void InitialiseFC(int type){
		fileChooser = new JFileChooser(new File("c:\\"));
		FileFilter filter;
		if (type == (1)){
			filter = new FileNameExtensionFilter("Video Files", new String[] {"avi","mp4"}); //filter for video files
		}else{
			filter = new FileNameExtensionFilter("Audio Files", new String[] {"mp3","wav"}); //filter for audio files
		}
        
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Choose a file name");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);		
	}
	
	//Opens the JFileChooser and gets the path of the file chosen 
	private File ChooseFile(){
		int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION){
			File fileToOpen = fileChooser.getSelectedFile();
			fileChooser.setVisible(false);
			return fileToOpen;
		}else if(result == JFileChooser.CANCEL_OPTION){
			fileChooser.setVisible(false);	
		}
        return null;
	}
	
	//Opens the JFileChooser and gets the path of the filename desired
	private File SaveFile(){
		int result = fileChooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION){
			File fileToSave = fileChooser.getSelectedFile();
			fileChooser.setVisible(false);
			return fileToSave;
		}else if(result == JFileChooser.CANCEL_OPTION){
			fileChooser.setVisible(false);	
		}
        return null;
	}

	
}
