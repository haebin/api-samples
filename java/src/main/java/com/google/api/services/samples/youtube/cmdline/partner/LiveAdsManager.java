/*
 * Copyright (c) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.services.samples.youtube.cmdline.partner;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.samples.youtube.cmdline.Auth;
import com.google.api.services.samples.youtube.cmdline.partner.model.LiveCuepoints;
import com.google.api.services.samples.youtube.cmdline.partner.model.LiveCuepointsSettings;
import com.google.common.collect.Lists;

/**
 * Retrieve a list of a channel's streams, using OAuth 2.0 to authorize
 * API requests.
 *
 * @author Haebin Na
 */
public class LiveAdsManager {
	final static Logger logger = LoggerFactory.getLogger(LiveAdsManager.class);
	
    /**
     * Define a global instance of a YoutubePartner object, which will be used
     * to make YouTube Data API requests.
     */
    private Credential credential = null;
    private static boolean verbose = false;
    
    private static final String DATASTORE = "ads";
    public static final Long DEF_DURATION = 120L;
    public static final int DEF_ADCALLS = 1;
    
    private static OptionParser parser = new OptionParser() {{
    	acceptsAll(Arrays.asList( "o", "owner" ), "Content Owner ID: The value of 'o' parameter on CMS main page.")
			.withRequiredArg().ofType(String.class).describedAs("Content Owner ID");
    	acceptsAll(Arrays.asList( "c", "channel" ), "Channel ID starting with UC.")
    		.withRequiredArg().ofType(String.class).describedAs("Channel ID");
    	acceptsAll(Arrays.asList( "v", "video" ), "Live streaming video ID.")
    		.withRequiredArg().ofType(String.class).describedAs("Video ID");
    	acceptsAll(Arrays.asList( "r", "reset" ), "Reset login credential.");
    	acceptsAll(Arrays.asList( "t", "talkative" ), "Verbose response.");
    	acceptsAll(Arrays.asList( "d", "duration" ), "Ads duration in seconds.")
			.withOptionalArg().ofType(Long.class).describedAs("Ads Duration").defaultsTo(120L);
    	acceptsAll(Arrays.asList( "n", "number" ), "Number of ads calls at once.")
			.withOptionalArg().ofType(Integer.class).describedAs("Number of Ads").defaultsTo(1);
    	accepts( "h", "Show this help." ).forHelp();
    }};
      
    public void login() throws Exception {
    	// This OAuth 2.0 access scope allows for read-only access to the
        // authenticated user's account, but not other types of account access.
        List<String> scopes = Lists.newArrayList(
        		"https://www.googleapis.com/auth/youtube.readonly",
        		"https://www.googleapis.com/auth/youtubepartner");
        credential = Auth.authorize(scopes, DATASTORE);
        
    }
    
    public void logout() throws Exception {
    	FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(
    			new File(System.getProperty("user.home") + "/" + Auth.CREDENTIALS_DIRECTORY));
        fileDataStoreFactory.getDataStore(DATASTORE).clear();
    }
    
    public static void main(String[] args) throws Exception {
    	OptionSet opts = parser.parse(args);
    	if(opts.has("h")) {
    		parser.printHelpOn( System.out );
    		System.exit(0);
    	}
    	
    	if(opts.has("t"))
    		verbose = true;
    	
    	LiveAdsManager manager = new LiveAdsManager();
    	if(opts.has("r"))
    		manager.logout();
    	manager.login();
    	
    	String videoId = (String)opts.valueOf("v");
    	String channelId = (String)opts.valueOf("c");
    	String contentOwnerId = (String)opts.valueOf("o");
    	
    	
    	Long duration = DEF_DURATION;
    	if(opts.has("d"))
    		duration = Long.parseLong((String)opts.valueOf("d"));
    	int adCalls = DEF_ADCALLS;
    	if(opts.has("n"))
    		adCalls = Integer.parseInt((String)opts.valueOf("n"));
    	
    	System.out.print(manager.insertAds(videoId, channelId, contentOwnerId, duration, adCalls));
    }
    
    public String insertAds(String videoId, String channelId, String contentOwnerId, Long duration, int adCalls) throws Exception {
    	// so, you don't need to login every time app restarts.
    	login();
    	
    	YouTubePartner youtubePartner = new YouTubePartner(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential);
        LiveCuepointsSettings setting = new LiveCuepointsSettings();
        setting.setCueType("ad");
        setting.setDurationSecs(duration);
        
        LiveCuepoints content = new LiveCuepoints();
        content.setBroadcastId(videoId);
        content.setSettings(setting);
        
        YouTubePartner.LiveCuepoints.Insert insertRequest = youtubePartner.liveCuepoints().insert(content);
        insertRequest.setChannelId(channelId);
        insertRequest.setOnBehalfOfContentOwner(contentOwnerId);
        
        StringBuffer buf = new StringBuffer();
        LiveCuepoints cuepoint;
        for(int i = 0; i<adCalls; i++) {
        	cuepoint = insertRequest.execute();
        	buf.append(cuepoint.getId()).append(";");
        	
        	// Print information from the API response.
            if(verbose) {
                System.out.println("\n================== Returned Streams ==================\n");
                System.out.println("  - Id: " + cuepoint.getId());
                System.out.println("  - Kind: " + cuepoint.getKind());
                System.out.println("  - Broadcast ID: " + cuepoint.getBroadcastId());
                System.out.println("  - CueType: " + cuepoint.getSettings().getCueType());
                System.out.println("  - CueType: " + cuepoint.getSettings().getDurationSecs());
                System.out.println("\n-------------------------------------------------------\n");
            }
            
           
        }
        return buf.toString();
    }
}
