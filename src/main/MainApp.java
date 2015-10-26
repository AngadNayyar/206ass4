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
	
	public static void main(String[] args) {
		new NativeDiscovery().discover();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainApp();
            }
        });
	}
	
	
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
        
        //Embedded media player
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
		
		
        //Components added to the north and south panels
		
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
					labelW.setVisible(false);
				}
				
			}
		});
        btnOpenVideo.setAlignmentX(Component.CENTER_ALIGNMENT);
        northPanel.add(btnOpenVideo);
        
        JButton btnReturnToEditor = new JButton("Return to unedited video");
        btnReturnToEditor.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		video.playMedia(fileVideo.getAbsolutePath());
        		btnOpenVideo.setVisible(true);
        		eastPanel.setVisible(true);
        		btnReturnToEditor.setVisible(false);
        		File del1 = new File(previewPath);
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
		
		//Opens a new GuiTts window
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
		
		JButton btnPreview = new JButton("Preview");
		btnPreview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int mergeTime = 0;
				if (addToCurrentTime){
					mergeTime = currentTime;
				}
				MergeMediaWorker preview = new MergeMediaWorker(fileVideo, fileAudio, mergeTime, overwrite);
				preview.execute();
				previewPath = fileVideo.getParent() + "/preview.avi";
				btnReturnToEditor.setVisible(true);
				eastPanel.setVisible(false);
				btnOpenVideo.setVisible(false);
				btnReturnToEditor.setEnabled(false);
				btnReturnToEditor.setText("Loading... Please Wait!");
			}
		});
		
		JCheckBox chckbxReplaceAudio = new JCheckBox("Overwrite audio");
		chckbxReplaceAudio.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				overwrite = chckbxReplaceAudio.isSelected();
			}
		});
		
		JLabel lblAudioSelected = new JLabel("");
		lblAudioSelected.setEnabled(false);
		GridBagConstraints gbc_lblAudioSelected = new GridBagConstraints();
		gbc_lblAudioSelected.insets = new Insets(0, 0, 5, 0);
		gbc_lblAudioSelected.gridx = 1;
		gbc_lblAudioSelected.gridy = 1;
		eastPanel.add(lblAudioSelected, gbc_lblAudioSelected);
		
        
		GridBagConstraints gbc_chckbxReplaceAudio = new GridBagConstraints();
		gbc_chckbxReplaceAudio.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxReplaceAudio.gridx = 1;
		gbc_chckbxReplaceAudio.gridy = 3;
		eastPanel.add(chckbxReplaceAudio, gbc_chckbxReplaceAudio);
		
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
		GridBagConstraints gbc_btnPreview = new GridBagConstraints();
		btnPreview.setEnabled(false);
		gbc_btnPreview.insets = new Insets(0, 0, 5, 0);
		gbc_btnPreview.gridx = 1;
		gbc_btnPreview.gridy = 5;
		eastPanel.add(btnPreview, gbc_btnPreview);
		
		JButton btnSaveNoPreview = new JButton("Save");
		btnSaveNoPreview.setEnabled(false);
		btnSaveNoPreview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File temp;
				InitialiseFC(1);
				temp = SaveFile();
				SaveVideoWorker saveWorker = new SaveVideoWorker(temp, fileVideo, fileAudio, currentTime, overwrite);
				saveWorker.execute();
			}
		});
		GridBagConstraints gbc_btnSaveNoPreview = new GridBagConstraints();
		gbc_btnSaveNoPreview.insets = new Insets(0, 0, 5, 0);
		gbc_btnSaveNoPreview.gridx = 1;
		gbc_btnSaveNoPreview.gridy = 7;
		eastPanel.add(btnSaveNoPreview, gbc_btnSaveNoPreview);
		
		//Opens a JFileChooser to select audio to be placed over the video
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
						fileAudio = tempFile;
						lblAudioSelected.setText(fileAudio.getName());
						btnPreview.setEnabled(true);
						btnSaveNoPreview.setEnabled(true);
					}
				}
			}
		});
		btnSelectAudio.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		GridBagLayout gbl_southPanel = new GridBagLayout();
		gbl_southPanel.columnWidths = new int[]{40, 0, 40, 40, 40, 81, 73, 91, 40, 0, 0, 0, 0};
		gbl_southPanel.rowHeights = new int[]{30, 41, 0};
		gbl_southPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_southPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		southPanel.setLayout(gbl_southPanel);
		
						
		JLabel lblTime = new JLabel("00:00");
		GridBagConstraints gbc_lblTime = new GridBagConstraints();
		gbc_lblTime.insets = new Insets(0, 0, 5, 5);
		gbc_lblTime.gridx = 1;
		gbc_lblTime.gridy = 0;
		southPanel.add(lblTime, gbc_lblTime);
		
		JSlider sliderVideo = new JSlider();
		sliderVideo.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				video.pause();
				timeUpdate.stop();
			}
			@Override
			public void mouseReleased(MouseEvent arg) {
				float sliderVideoTime = sliderVideo.getValue()/5000.0f;
				video.setPosition(sliderVideoTime);
				video.play();
				timeUpdate.restart();
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
				lblTime.setText(timeMinSec);	
				sliderVideo.setValue((int) (video.getPosition() * 5000.0f));
				
				if (previewnow){
					video.playMedia(previewPath);
					btnReturnToEditor.setEnabled(true);
					btnReturnToEditor.setText("Return to unedited video");
					previewnow = false;
				}
			}
		});
		timeUpdate.start();
				

		//Mute using built in function.
		JButton btnMute = new JButton("Mute");
		btnMute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				video.mute();
			}
		});
		
		//Forward works by setting the rate to 4x the normal rate of play
		JButton btnFwd = new JButton(">>");
		btnFwd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				VideoControls.fastForward(video);
			}
		});
		
				
		//Replay resumes the video to normal play
		JButton btnPlay = new JButton("Play");
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				VideoControls.play(video);
			}
		});
				
		//Create an instance of VideoWorker (swingworker) to rewind the video.
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
				video.setVolume(volume);
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
			filter = new FileNameExtensionFilter("Video Files", new String[] {"avi","mp4"});
		}else{
			filter = new FileNameExtensionFilter("Audio Files", new String[] {"mp3","wav"});
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
