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

import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
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

    /**
     * Define a global instance of a YoutubePartner object, which will be used
     * to make YouTube Data API requests.
     */
    private YouTubePartner youtubePartner;
    private Credential credential = null;
    private boolean verbose = false;
    
    public String videoId = "";
    public String contentOwnerId = "";
    public String channelId = "";
    
    public void login() throws Exception {
		// This OAuth 2.0 access scope allows for read-only access to the
        // authenticated user's account, but not other types of account access.
        List<String> scopes = Lists.newArrayList(
        		"https://www.googleapis.com/auth/youtube.readonly",
        		"https://www.googleapis.com/auth/youtubepartner");
        credential = Auth.authorize(scopes, "ads");
    }
    
    public void logout() throws Exception {
    	
    }
    
    public static void main(String[] args) throws Exception {
    	LiveAdsManager manager = new LiveAdsManager();
    	manager.login();
    	manager.videoId = "8EozCZN-RvQ";
    	manager.channelId = "UC4E8QkzGeyhvsiGyynr86gQ";
    	manager.contentOwnerId = "lEpRlRElEsHjcMipWZHlVA";
    	
    	while(true) {
			manager.insertAds();
			Thread.sleep(10*60*1000);
    	}
    }
    
    public String insertAds() throws Exception {
        youtubePartner = new YouTubePartner(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential);
        LiveCuepoints content = new LiveCuepoints();
        LiveCuepointsSettings setting = new LiveCuepointsSettings();
        setting.setCueType("ad");
        setting.setDurationSecs(30L);
        content.setBroadcastId(videoId);
        content.setSettings(setting);
        
        YouTubePartner.LiveCuepoints.Insert insertRequest = youtubePartner.liveCuepoints().insert(content);
        insertRequest.setChannelId(channelId);
        insertRequest.setOnBehalfOfContentOwner(contentOwnerId);
        
        LiveCuepoints cuepoint = insertRequest.execute();
        
        // Print information from the API response.
        if(verbose) {
            System.out.println("\n================== Returned Streams ==================\n");
            System.out.println("  - Id: " + cuepoint.getId());
            System.out.println("  - Kind: " + cuepoint.getKind());
            System.out.println("  - Broadcast ID: " + cuepoint.getBroadcastId());
            System.out.println("  - CueType: " + cuepoint.getSettings().getCueType());
            System.out.println("  - CueType: " + cuepoint.getSettings().getDurationSecs());
            System.out.println("\n-------------------------------------------------------------\n");
        }
        
        return cuepoint.getId();
    }
}
