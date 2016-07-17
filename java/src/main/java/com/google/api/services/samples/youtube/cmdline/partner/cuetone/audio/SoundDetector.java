/*
*      _______                       _____   _____ _____  
*     |__   __|                     |  __ \ / ____|  __ \ 
*        | | __ _ _ __ ___  ___  ___| |  | | (___ | |__) |
*        | |/ _` | '__/ __|/ _ \/ __| |  | |\___ \|  ___/ 
*        | | (_| | |  \__ \ (_) \__ \ |__| |____) | |     
*        |_|\__,_|_|  |___/\___/|___/_____/|_____/|_|     
*                                                         
* -------------------------------------------------------------
*
* TarsosDSP is developed by Joren Six at IPEM, University Ghent
*  
* -------------------------------------------------------------
*
*  Info: http://0110.be/tag/TarsosDSP
*  Github: https://github.com/JorenSix/TarsosDSP
*  Releases: http://0110.be/releases/TarsosDSP/
*  
*  TarsosDSP includes modified source code by various authors,
*  for credits and info, see README.
* 
*/


package com.google.api.services.samples.youtube.cmdline.partner.cuetone.audio;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;

import com.google.api.services.samples.youtube.cmdline.partner.LiveAdsManager;

/**
 * http://www.usmotors.com/TechDocs/ProFacts/Sound-Power-Pressure
 * 
 * @author haebin
 *
 */
public class SoundDetector extends JFrame implements AudioProcessor, LogViewer {
	private static final long serialVersionUID = 3501426880288136245L;

	private final JTextArea textArea;
	ArrayList<Clip> clipList;
	int counter;
	double threshold;
	TargetDataLine line;
	AudioDispatcher dispatcher;
	Mixer currentMixer;
	private final GraphPanel graphPanel;
	SilenceDetector silenceDetector;
	PitchProcessor pitchDetector;
	LiveAdsManager adsManager = new LiveAdsManager();
	
	
	JTextField channel = new JTextField(12);
	JTextField contentOwner = new JTextField(10);
	JTextField video = new JTextField(5);
	
	
	public SoundDetector() {
		
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("YT Live Ads Manager");
		this.threshold = SilenceDetector.DEFAULT_SILENCE_THRESHOLD;
		
		textArea = new JTextArea(10,30);
		textArea.setEditable(false);
		
		JPanel inputPanel = new InputPanel();
		//add(inputPanel);
		inputPanel.addPropertyChangeListener("mixer",
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent arg0) {
						try {
							setNewMixer((Mixer) arg0.getNewValue());
						} catch (Exception e) {
							log(e.getMessage());
						}
					}
				});
				
		//JSlider thresholdSlider = initialzeThresholdSlider();		
		JPanel params = new JPanel(new BorderLayout());
		//params.setBorder(new TitledBorder("2. Set the silence parameters"));
		params.setBorder(new TitledBorder("Controls"));
		
		//JLabel label = new JLabel("Threshold");
		//label.setToolTipText("Energy level when sound is counted (dB SPL).");
		//params.add(label,BorderLayout.NORTH);
		//params.add(thresholdSlider,BorderLayout.CENTER);
		
		JPanel inputAndParamsPanel = new JPanel(new BorderLayout());
		inputAndParamsPanel.add(inputPanel,BorderLayout.NORTH);
		inputAndParamsPanel.add(params,BorderLayout.CENTER);
		
	
		//JPanel liveControl = new JPanel(new BorderLayout());
		
		JPanel live = new JPanel(new FlowLayout());
		JPanel app = new JPanel(new FlowLayout());
		
		JButton login = new JButton("Login");
		login.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					adsManager.login();
				} catch(Exception ie) {
					log(ie.getMessage());
				}
			}});
		
		JButton logout = new JButton("Logout");
		logout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					adsManager.logout();
				} catch (Exception ie) {
					log(ie.getMessage());
				}
			}});
		
		JButton clearLog = new JButton("Clear Log");
		clearLog.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
			}});
		
		JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				adsManager.channelId = channel.getText();
				adsManager.contentOwnerId = contentOwner.getText();
				adsManager.videoId = video.getText();
			}});
		
		contentOwner.setToolTipText("Content Owner ID: Login to CMS and the value of 'o' parameter.");
		channel.setToolTipText("Channel ID starting with UC...");
		video.setToolTipText("Live streaming video ID.");
		
		app.add(login);
		app.add(logout);
		app.add(clearLog);
		
		live.add(contentOwner);
		live.add(channel);
		live.add(video);
		live.add(apply);
		
		params.add(app, BorderLayout.NORTH);
		params.add(live, BorderLayout.SOUTH);
		inputAndParamsPanel.add(params,BorderLayout.SOUTH);

		
		JPanel panelWithTextArea = new JPanel(new BorderLayout());
		panelWithTextArea.add(inputAndParamsPanel,BorderLayout.NORTH);
		panelWithTextArea.add(new JScrollPane(textArea),BorderLayout.CENTER);

		add(panelWithTextArea,BorderLayout.NORTH);
	
		graphPanel = new GraphPanel(threshold);
		graphPanel.setSize(80,100);
		add(graphPanel,BorderLayout.CENTER);
	}
	
	private static class GraphPanel extends JPanel{

		/**
		 * 
		 */
		private static final long serialVersionUID = 5969781241442094359L;
		private double threshold;
		private double maxLevel = -1000;
		private long currentModulo = System.currentTimeMillis()/15000;
		private List<Double> levels;
		private List<Long> startTimes;
		
		public GraphPanel(double defaultThreshold){			
			setThresholdLevel(defaultThreshold);
			levels = new ArrayList<Double>();
			startTimes=new ArrayList<Long>();
			setMinimumSize(new Dimension(80,60)); 
		}
		
		private void setMaxLevel(double newMaxLevel){
			if(newMaxLevel> maxLevel){
				maxLevel=newMaxLevel;
			}
		}
		
		public void setThresholdLevel(double newThreshold){
			threshold=newThreshold;
			repaint();
		}
		
		public void addDataPoint(double level,long ms){
			levels.add(level);
			startTimes.add(ms);
			setMaxLevel(level);
			repaint();
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g); //paint background
			try {
		        g.setColor(Color.BLACK);
				g.fillRect(0, 0,getWidth(), getHeight());
				
				if(System.currentTimeMillis()/15000 > currentModulo){
					currentModulo = System.currentTimeMillis()/15000;
					levels.clear();
					startTimes.clear();
				}
				
				for(int i = 0; i < levels.size();i++){
					g.setColor( levels.get(i) > threshold ? Color.GREEN:Color.ORANGE ); 
					int x = msToXCoordinate(startTimes.get(i));
					int y = levelToYCoordinate(levels.get(i));
					g.drawLine(x, y, x+1, y);
				}
				
				int thresholdYCoordinate = levelToYCoordinate(threshold);
				g.setColor(Color.ORANGE);
				g.drawLine(0, thresholdYCoordinate, getWidth(),thresholdYCoordinate);
				g.drawString(String.valueOf((int)threshold), 0, thresholdYCoordinate + 15);
				
				int maxYCoordinate = levelToYCoordinate(maxLevel);
				g.setColor(Color.RED);
				g.drawLine(0, maxYCoordinate, getWidth(),maxYCoordinate);
				g.drawString(String.valueOf(((int)(maxLevel*100))/100.0), getWidth() - 40, maxYCoordinate + 15);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
		
		private int levelToYCoordinate(double level){
			int inPixels = (int)((120 + level)  / 120 * (getHeight()-1));
			int yCoordinate =  getHeight() - inPixels;
			return yCoordinate;
		}
		
		private int msToXCoordinate(long ms){
			return (int) ((ms % 15000)/15000.0 * getWidth());
		}
				
	}

	private void setNewMixer(Mixer mixer) throws Exception {
		if(dispatcher!= null) {
			dispatcher.stop();
		}
		
		/*
		if(line != null) {
			line.stop();
			line.close();
			line = null;
			System.gc(); // due to java audio stream bug
		}
		*/
		currentMixer = mixer;
		
		float sampleRate = 44100;//44100;
		//int bufferSize = 512;
		int bufferSize = 1024;
		int overlap = 256;
		//int bufferSize = 512;
		//int overlap = 128;
		//int stepSize = 256; // DTMF
		
		log("Started listening with " + Shared.toLocalString(mixer.getMixerInfo().getName()) + "\n\tparams: " + threshold + "dB\n");
		log(mixer.getMixerInfo().getName() + "\n" + mixer.getMixerInfo().getDescription() + "\n");
		
		final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, true);
		final DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);
		line = (TargetDataLine) mixer.getLine(dataLineInfo);
		final int numberOfSamples = bufferSize;
		line.open(format, numberOfSamples);
		line.start();
		final AudioInputStream stream = new AudioInputStream(line);

		JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);
		// create a new dispatcher
		dispatcher = new AudioDispatcher(audioStream, bufferSize, overlap);

		// add a processor, handle percussion event.
		silenceDetector = new SilenceDetector(threshold,false);
		pitchDetector = new PitchProcessor(PitchEstimationAlgorithm.FFT_YIN, sampleRate, bufferSize, new DTMFSignalHandler(adsManager, this));
		
		dispatcher.addAudioProcessor(pitchDetector);
		dispatcher.addAudioProcessor(silenceDetector);
		dispatcher.addAudioProcessor(this);

		// run the dispatcher (on a new thread).
		new Thread(dispatcher,"Audio dispatching").start();
	}

	public static void main(String... strings) throws InterruptedException,
			InvocationTargetException {
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new SoundDetector();
				frame.pack();
				frame.setSize(640,600);
				frame.setVisible(true);
			}
		});
	}

	@Override
	public boolean process(AudioEvent audioEvent) {
		handleSound();
		return true;
	}

	private void handleSound(){
		if(silenceDetector.currentSPL() > threshold) {
			//log(dtmfDetector.);
			//log(new Date() + ": Sound detected at " + System.currentTimeMillis() + ", " + (int)(silenceDetector.currentSPL()) + "dB SPL\n");
		}
		graphPanel.addDataPoint(silenceDetector.currentSPL(), System.currentTimeMillis());		
	}
	
	@Override
	public void processingFinished() {		
		
	}
	
	@Override
	public void log(String message) {
		textArea.append(message + "\n");
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
}
