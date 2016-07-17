package com.google.api.services.samples.youtube.cmdline.partner.cuetone.audio;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.pitch.PitchDetectionResult;

import com.google.api.services.samples.youtube.cmdline.partner.LiveAdsManager;

public class DTMFSignalHandlerTest {
	DTMFSignalHandler handler = new DTMFSignalHandler(new LiveAdsManagerTest(), new LogViewer() {
		@Override
		public void log(String message) {
			System.out.println(message);
		}
		@Override
		public boolean isVerbose() {
			return true;
		}
	});
	
	@Test
	public void testDec() {
		assertEquals(handler.dec('0'), 0);
		assertEquals(handler.dec('9'), 9);
		assertEquals(handler.dec('A'), 10);
		assertEquals(handler.dec('D'), 13);
		assertEquals(handler.dec(' '), 16);
	}

	@Test
	public void testEnc() {
		assertEquals(handler.enc(0), '0');
		assertEquals(handler.enc(9), '9');
		assertEquals(handler.enc(10), 'A');
		assertEquals(handler.enc(13), 'D');
		assertEquals(handler.enc(16), ' ');
	}

	@Test
	public void testMax() {
		int[] chCounter = new int[17];
		Arrays.fill(chCounter, 0);
		assertEquals(' ', handler.max(chCounter));
		chCounter[5] = 2;
		chCounter[6] = 1;
		assertEquals('5', handler.max(chCounter));
		chCounter[16] = 5;
		assertEquals('5', handler.max(chCounter));
		
		Arrays.fill(chCounter, 0);
		chCounter[16] = 5;
		assertEquals(' ', handler.max(chCounter));
	}
	
	@Test
	public void testHandlePitch() {
		AudioEventTest event = new AudioEventTest();
		PitchDetectionResult pitch = new PitchDetectionResult();
		pitch.setProbability(0.82f);	// don't care
		event.setRMS(0.80); 			// don't care
		
		event.setTimeStamp(10.00);
		pitch.setPitch(0);
		assertEquals(' ', handler.handlePitchInternal(pitch, event));
		
		event.setTimeStamp(10.01);
		pitch.setPitch(0);
		assertEquals(' ', handler.handlePitchInternal(pitch, event));
		
		
		// 1:621~7, 239~7, 172~7 
		// 1:625, 2:674, 3:730, A:798
		// 4:397, 5:691, 6:745, B:808
		// 7:410, 8:439, 9:763, C:824
		// *:306, 0:453, #:486, D:844
		
		event.setTimeStamp(10.11);
		pitch.setPitch(172);
		assertEquals('1', handler.handlePitchInternal(pitch, event));
		event.setTimeStamp(10.13);
		pitch.setPitch(1172);	// out of range noise
		assertEquals(' ', handler.handlePitchInternal(pitch, event));
		event.setTimeStamp(10.14);
		pitch.setPitch(172);
		assertEquals('1', handler.handlePitchInternal(pitch, event));
		event.setTimeStamp(10.16);
		pitch.setPitch(691);	// noise
		assertEquals('1', handler.handlePitchInternal(pitch, event));
		
		
		event.setTimeStamp(10.21);
		pitch.setPitch(1172);	// out of range noise
		assertEquals(' ', handler.handlePitchInternal(pitch, event));
		event.setTimeStamp(10.22);
		pitch.setPitch(306);
		assertEquals('*', handler.handlePitchInternal(pitch, event));
		
		// triggers ads insert
		event.setTimeStamp(11.30);
		pitch.setPitch(172);	
		assertEquals('1', handler.handlePitchInternal(pitch, event));
		event.setTimeStamp(11.35);
		pitch.setPitch(176);	
		assertEquals('1', handler.handlePitchInternal(pitch, event));
		event.setTimeStamp(11.40);
		pitch.setPitch(176);	
		assertEquals('1', handler.handlePitchInternal(pitch, event));
		event.setTimeStamp(11.45);
		pitch.setPitch(306);	
		assertEquals('*', handler.handlePitchInternal(pitch, event));
		
		
		// triggers ads insert
		event.setTimeStamp(13.30);
		pitch.setPitch(172);	
		assertEquals('1', handler.handlePitchInternal(pitch, event));
		event.setTimeStamp(13.35);
		pitch.setPitch(176);	
		assertEquals('1', handler.handlePitchInternal(pitch, event));
		event.setTimeStamp(13.40);
		pitch.setPitch(176);	
		assertEquals('1', handler.handlePitchInternal(pitch, event));
		event.setTimeStamp(13.45);
		pitch.setPitch(486);	
		assertEquals('#', handler.handlePitchInternal(pitch, event));
		
	}
	
	class AudioEventTest extends AudioEvent {
		double timeStamp;
		double RMS;
		
		public double getRMS() {
			return RMS;
		}

		public void setRMS(double rms) {
			RMS = rms;
		}

		public double getTimeStamp() {
			return timeStamp;
		}

		public void setTimeStamp(double timeStamp) {
			this.timeStamp = timeStamp;
		}

		public AudioEventTest() {
			super(new TarsosDSPAudioFormat(44100, 16, 1, true, true));
		}
		
		public AudioEventTest(TarsosDSPAudioFormat format) {
			super(format);
		}
	}
	
	class LiveAdsManagerTest extends LiveAdsManager {
		public String insertAds() throws Exception {
			System.out.println("Ads Inserted");
			return "";
		}
	}
}
