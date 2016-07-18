package com.google.api.services.samples.youtube.cmdline.partner.cuetone.audio;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import be.tarsos.dsp.util.fft.FFT;

import com.google.api.services.samples.youtube.cmdline.partner.LiveAdsManager;

public class CueApp extends JFrame implements PitchDetectionHandler, LogViewer {
	final static Logger logger = LoggerFactory.getLogger(CueApp.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1775769919742366157L;

	private final SpectrogramPanel panel;
	private AudioDispatcher dispatcher;
	// private Mixer currentMixer;
	private PitchEstimationAlgorithm algo;
	private double pitch;

	// Main features to tune
	private float sampleRate = 44100;
	private int bufferSize = 4096;
	private int overlap = 512;
	// private int bufferSize = 1024;
	// private int overlap = 256;

	private JTextArea logViewer;
	private JTextField channel = new JTextField(12);
	private JTextField contentOwner = new JTextField(10);
	private JTextField video = new JTextField(5);
	private JTextField adsNumber = new JTextField(2);
	private JTextField adsDuration = new JTextField(3);
	private JCheckBox verboseLog = new JCheckBox("Verbose");
	
	private LiveAdsManager adsManager = new LiveAdsManager();
	private PitchDetectionHandler pitchHandler = new DTMFSignalHandler(
			adsManager, this);

	public CueApp() {
		logger.info("YT Live Ads Man started. --------------------------------------------------");
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("YT Live Ads Man");
		panel = new SpectrogramPanel();
		algo = PitchEstimationAlgorithm.FFT_YIN;

		// JPanel pitchDetectionPanel = new PitchDetectionPanel();
		logViewer = new JTextArea(10, 30);
		logViewer.setEditable(false);
		JScrollPane logPanel = new JScrollPane(logViewer);
		logPanel.setBorder(new TitledBorder("Logs"));

		JPanel inputPanel = new InputPanel();

		inputPanel.addPropertyChangeListener("mixer",
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent arg0) {
						try {
							setNewMixer((Mixer) arg0.getNewValue());
						} catch (Exception e) {
							log(e.getMessage());
							logger.error("Failed to select mixer.", e);
						}
					}
				});
		
		
		JButton clearLog = new JButton("Clear Log");
		clearLog.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				logViewer.setText("");
			}});
		verboseLog.setSelected(true);
		
		JPanel logControlPanel = new JPanel(new FlowLayout());
		logControlPanel.add(verboseLog);
		logControlPanel.add(clearLog);
		
		JPanel containerPanel = new JPanel(new BorderLayout());
		JPanel centerPanel = new JPanel(new GridLayout(1, 0));
		centerPanel.add(inputPanel);
		centerPanel.add(logPanel);
		containerPanel.add(centerPanel, BorderLayout.CENTER);
		containerPanel.add(logControlPanel, BorderLayout.SOUTH);
		this.add(containerPanel, BorderLayout.NORTH);
		
		JPanel controllerPanel = new JPanel(new GridLayout(2, 1));
		
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
					logger.error("Failed to login for API.", ie);
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
					logger.error("Failed to clear login credential", ie);
				}
			}});
		
		JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				adsManager.channelId = channel.getText();
				adsManager.contentOwnerId = contentOwner.getText();
				adsManager.videoId = video.getText();
				try {
					adsManager.num = Integer.parseInt(adsNumber.getText());
				    adsManager.duration = Long.parseLong(adsDuration.getText());
				} catch (Exception ie) {
					log(ie.getMessage());
					logger.error("Failed to parse parameter numbers.", ie);
				}
			}});
		
		contentOwner.setToolTipText("Content Owner ID: Login to CMS and the value of 'o' parameter.");
		channel.setToolTipText("Channel ID starting with UC.");
		video.setToolTipText("Live streaming video ID.");
		
		adsDuration.setText(adsManager.duration + "");
		adsNumber.setText(adsManager.num + "");
		
		app.add(login);
		app.add(logout);
		app.add(new JLabel("Ads: Duration"));
		app.add(adsDuration);
		app.add(new JLabel("Podding"));
		app.add(adsNumber);
		
		//app.add(clearLog);
		
		live.add(new JLabel("CO"));
		live.add(contentOwner);
		live.add(new JLabel("CH"));
		live.add(channel);
		live.add(new JLabel("Video"));
		live.add(video);
		live.add(apply);
		
		controllerPanel.add(app, BorderLayout.NORTH);
		controllerPanel.add(live, BorderLayout.SOUTH);
		
		this.add(controllerPanel, BorderLayout.SOUTH);

		JPanel otherContainer = new JPanel(new BorderLayout());
		otherContainer.add(panel, BorderLayout.CENTER);
		otherContainer.setBorder(new TitledBorder("Spectrogram"));

		this.add(otherContainer, BorderLayout.CENTER);
	}
	
	@Override
	public void log(String message) {
		logViewer.append(message + "\n");
		logViewer.setCaretPosition(logViewer.getDocument().getLength());
	}
	
	@Override
	public boolean isVerbose() {
		return verboseLog.isSelected();
	}

	private void setNewMixer(Mixer mixer) throws LineUnavailableException,
			UnsupportedAudioFileException {

		if (dispatcher != null) {
			dispatcher.stop();
		}

		final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true,
				false);
		final DataLine.Info dataLineInfo = new DataLine.Info(
				TargetDataLine.class, format);
		TargetDataLine line;
		line = (TargetDataLine) mixer.getLine(dataLineInfo);
		final int numberOfSamples = bufferSize;
		line.open(format, numberOfSamples);
		line.start();
		final AudioInputStream stream = new AudioInputStream(line);

		JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);
		// create a new dispatcher
		dispatcher = new AudioDispatcher(audioStream, bufferSize, overlap);

		// Mixer currentMixer = mixer;

		// add a processor, handle pitch event.
		dispatcher.addAudioProcessor(new PitchProcessor(algo, sampleRate,
				bufferSize, this));
		dispatcher.addAudioProcessor(fftProcessor);

		// run the dispatcher (on a new thread).
		new Thread(dispatcher, "Audio dispatching").start();
	}

	AudioProcessor fftProcessor = new AudioProcessor() {

		FFT fft = new FFT(bufferSize);
		float[] amplitudes = new float[bufferSize / 2];

		@Override
		public void processingFinished() {
			// TODO Auto-generated method stub
		}

		@Override
		public boolean process(AudioEvent audioEvent) {
			float[] audioFloatBuffer = audioEvent.getFloatBuffer();
			float[] transformbuffer = new float[bufferSize * 2];
			System.arraycopy(audioFloatBuffer, 0, transformbuffer, 0,
					audioFloatBuffer.length);
			fft.forwardTransform(transformbuffer);
			fft.modulus(transformbuffer, amplitudes);
			panel.drawFFT(pitch, amplitudes, fft);
			panel.repaint();
			return true;
		}

	};

	@Override
	public void handlePitch(PitchDetectionResult pitchDetectionResult,
			AudioEvent audioEvent) {
		if (pitchDetectionResult.isPitched()) {
			pitch = pitchDetectionResult.getPitch();
		} else {
			pitch = -1;
		}
		pitchHandler.handlePitch(pitchDetectionResult, audioEvent);
	}

	public static void main(String... strings)
			throws InterruptedException, InvocationTargetException {
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					// ignore failure to set default look en feel;
				}
				JFrame frame = new CueApp();
				frame.pack();
				frame.setSize(640, 600);
				frame.setVisible(true);
			}
		});
	}

}
