package com.google.api.services.samples.youtube.cmdline.partner.cuetone;

import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;

import com.google.api.services.samples.youtube.cmdline.partner.LiveAdsManager;

public class DTMFSignalHandler implements PitchDetectionHandler {
	final static Logger logger = LoggerFactory.getLogger(DTMFSignalHandler.class);
	
	// Main features to tune
	public double commandWindow = 1; // in sec.
	public double signalWindow = 0.07; // in sec.
	
	private double echoTimeStamp = 0;
	private int signalIdx = 0;
	private char signal[] = {' ', ' ', ' ', ' '};
	
	private static final char[] ADS_START = "111*".toCharArray();
	//private static final String ADS_END = "111#";
	
	private int[] chCounter = new int[17];
	
	private final LiveAdsManager adsManager;
	private final LogViewer logViewer;

	public DTMFSignalHandler(LiveAdsManager adsManager, LogViewer logViewer) {
		this.logViewer = logViewer;
		this.adsManager = adsManager;
	}
	
	int dec(char ch) {
		if(65 <= ch && ch <= 68)
			return ch - 55;
		if(48 <= ch && ch <= 57)
			return ch - 48;
		if(ch == '*')
			return 14;
		if(ch == '#')
			return 15;
		if(ch == ' ')
			return 16;
		return -1;
	}
	
	char enc(int i) {
		if(10 <= i && i <= 13)
			return (char)(i + 55);
		
		if(0 <= i && i <= 9)
			return (char)(i + 48);
		
		switch(i) {	
			case 14:
				return '*';
			case 15:
				return '#';
			default:
				return ' ';
		}
	}
	
	char max(int[] chCounter) {
		// don't care bout ' ' (16th)
		int maxIdx = 0;
		for(int i=1; i<chCounter.length-1; i++) {
			if(chCounter[maxIdx] <= chCounter[i]) {
				maxIdx = i;
			}
		}
		if(chCounter[maxIdx] == 0)
			return ' ';
		return enc(maxIdx);
	}
	
	@Override
	public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
		handlePitchInternal(pitchDetectionResult, audioEvent);
	}
	
	char handlePitchInternal(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent)  {
		if(pitchDetectionResult.getPitch() == -1)
			return ' ';
	
		double timeStamp = audioEvent.getTimeStamp();
		float pitch = pitchDetectionResult.getPitch();
		float probability = pitchDetectionResult.getProbability();
		double rms = audioEvent.getRMS() * 100; // related to volume of the sound
		
		if(pitch < 150 || pitch > 900) {
			// disregard noise, no timestamping
			return ' ';
		}
		
		String log = "";
		if(logViewer.isVerbose()) {
			log = String.format("Pitch detected at %.2fs: %.2fHz ( %.2f probability, RMS: %.5f )", 
					timeStamp, pitch, probability, rms);
			logViewer.log(log);
			logger.info(log);
		}
		
		if(rms < 15) {
			// small volume then skip after logging!
			return ' ';
		}
		
		double timeGap = timeStamp - echoTimeStamp;
		echoTimeStamp = timeStamp;
		char ch = decodeDtmf(pitch);
		if(timeGap > commandWindow) {
			signalIdx = 0; // new signal starts.
			Arrays.fill(signal, ' ');
			signal[0] = ch;
			Arrays.fill(chCounter, 0);
			chCounter[dec(ch)] += 1;
			
			log = String.format("[%d] = '%c' Pitch detected at %.2fs: %.2fHz", 
					signalIdx,signal[signalIdx%signal.length], timeStamp, pitch);
			logViewer.log(log);
			logger.info(log);
		} else if(timeGap > signalWindow) {
			signalIdx++;
			signal[signalIdx%signal.length] = ch;
			Arrays.fill(chCounter, 0);
			chCounter[dec(ch)] += 1;
			
			log = String.format("[%d] = '%c' Pitch detected at %.2fs: %.2fHz", 
					signalIdx,signal[signalIdx%signal.length], timeStamp, pitch);
			logViewer.log(log);
			logger.info(log);
			
			// Full Signal!
			if(signal.length - 1 == signalIdx) {
				log = String.format("#(%tc) COMMAND = [%c|%c|%c|%c]", 
						new Date(), signal[0], signal[1], signal[2], signal[3]);
				logViewer.log(log);
				logger.info(log);
				processCommand();
			}
		} else {
			// ERROR CORRECTION
			// how frequent the pitch is.
			// most frequent one is the one.
			// for tie, larger char stands.
			chCounter[dec(ch)] += 1;
			char chMax = max(chCounter);
			if(chMax != signal[signalIdx%signal.length]) {
				char oldCh = signal[signalIdx%signal.length];
				signal[signalIdx%signal.length] = chMax;
				log = String.format("[%d] = '%c' corrected from '%c' at %.2fs: %.2fHz", 
						signalIdx, chMax, oldCh, timeStamp, pitch);
				logViewer.log(log);
				logger.info(log);
			}
		}
		return signal[signalIdx%signal.length];
	}
	
	private void processCommand() {
		if(Arrays.equals(ADS_START, signal)) {
			try {
				String cuepointId = adsManager.insertAds();
				String log = String.format("#(%tc) %s", new Date(), cuepointId);
				logViewer.log(log);
				logger.info(log);
			} catch (Exception e) {
				logViewer.log(e.getMessage());
				logger.error("Failed to insert ads.", e);
			}
		}
	}
	
	float N1_2 = 172;
	float N1_1 = 239; 
	float N1 = 625, N2 = 674, N3 = 730, A = 798;
	float N4 = 397, N5 = 691, N6 = 745, B = 808;
	float N7 = 410, N8 = 439, N9 = 763, C = 824;
	float Na = 306, N0 = 453, Ns = 486, D = 844;

	// 1:621~20, 239~10, 172~10 
	// 1:625 (605~645), 2:674 (669~679), 3:730 (725~735), A:798 (793~803)
	// 4:397 (392~402), 5:691 (686~696), 6:745 (740~750), B:808 (803~813)
	// 7:410 (405~415), 8:439 (434~444), 9:763 (753~773), C:824 (814~834)
	// *:306 (296~316), 0:453 (448~458), #:486 (476~496), D:844 (834~854)
	float bound = 10;
	private char decodeDtmf(float echo) {
		if(is(echo, N1, bound*2)) return '1';
		if(is(echo, N1_1, bound)) return '1';
		if(is(echo, N1_2, bound)) return '1';	
		if(is(echo, Na, bound)) return '*';
		if(is(echo, Ns, bound)) return '#';
		
		if(is(echo, N2, bound/2)) return '2';
		if(is(echo, N3, bound/2)) return '3';
		if(is(echo, N4, bound/2)) return '4';
		if(is(echo, N5, bound/2)) return '5';
		if(is(echo, N6, bound/2)) return '6';
		if(is(echo, N7, bound/2)) return '7';
		if(is(echo, N8, bound/2)) return '8';
		if(is(echo, N9, bound)) return '9';
		if(is(echo, N0, bound/2)) return '0';
		
		if(is(echo, A, bound/2)) return 'A';
		if(is(echo, B, bound/2)) return 'B';
		if(is(echo, C, bound)) return 'C';
		if(is(echo, D, bound)) return 'D';
		return ' ';
	}
	
	private boolean is(float pitch, float code, float bound) {
		if(code-bound < pitch && pitch < code+bound) {
			return true;
		}
		return false;
	}
}
