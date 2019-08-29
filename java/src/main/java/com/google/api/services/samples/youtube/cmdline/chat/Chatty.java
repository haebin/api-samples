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

package com.google.api.services.samples.youtube.cmdline.chat;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.samples.youtube.cmdline.Auth;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveBroadcastListResponse;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;
import com.google.common.collect.Lists;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * React to live streaming chat. Fire key inputs based on the user types and messages.
 *
 * @author Haebin Na
 */
public class Chatty {
	final static Logger logger = LoggerFactory.getLogger(Chatty.class);

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;
    private static Credential credential = null;
    private static Robot robot;
    
    private static final String DATASTORE = "chatter";
    private static HashMap<String, String> reactionMap = new HashMap<String, String>();
    
    private static enum ChatType {
        TEXT,
        SUPERCHAT,
        SUPERSTICKER,
        MEMBERSHIP;
    }
    
    private static OptionParser parser = new OptionParser() {{
    	acceptsAll(Arrays.asList( "v", "video" ), "Live streaming video ID.")
    		.withRequiredArg().ofType(String.class).describedAs("Video ID");
    	acceptsAll(Arrays.asList( "c", "config" ), "Configuration file path for message and key mapping.")
    		.withRequiredArg().ofType(String.class).describedAs("Config");
    	acceptsAll(Arrays.asList( "r", "reset" ), "Reset login credential.");
    	
    	accepts( "h", "Show this help." ).forHelp();
    }};

    /**
     * Listens to chat and react to it in loop. To stop, kill the process.
     *
     * @param args command line args (not used).
     */
    public static void main(String[] args) {
    	
        try {
        	OptionSet opts = parser.parse(args);
        	if(opts.has("h") || args.length == 0) {
        		parser.printHelpOn( System.out );
        		System.exit(0);
        	}
        	
        	String videoId = (String)opts.valueOf("v");
        	String configFilePath = (String)opts.valueOf("c");
        	
        	
        	Properties properties = new Properties();
        	properties.load(new FileReader(configFilePath));
        	
        	while(properties.keys().hasMoreElements()) {
        		String key = (String)properties.keys().nextElement();
        		String val = properties.getProperty(key);
        		
        		String[] keywords = key.split(",");
        		
        		String[] inputs = val.split(",");
        		List<KeyEvent> events = new LinkedList<KeyEvent>();
        		for(String keyEvent: inputs) {
        			
        			
        		}
        		
        		for(String keyword: keywords) {
        			reactionMap.put(keyword, val);
        		}
        	}
        	//Reader reader = Resources.getResourceAsReader(configFilePath);
            //properties.load(reader);
            //*/
        	
        	// reset login credential
        	if(opts.has("r"))
        		logout();
        	login();
        	
        	
        	robot = new Robot();
        	 
            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                    .setApplicationName("ReactToChatServer").build();    
            
           LiveBroadcastListResponse resCast = youtube.liveBroadcasts().list("id,snippet").setId(videoId).execute();
           LiveBroadcast cast = resCast.getItems().get(0);
           String liveChatId = cast.getSnippet().getLiveChatId();
           
           YouTube.LiveChatMessages.List chatReq =
                   youtube.liveChatMessages().list(liveChatId, "id,snippet");
           LiveChatMessageListResponse resChat = null;
           while(true) {
	            // Create a request to list broadcasts.
        	   if(resChat != null && resChat.getNextPageToken() != null)
        		   chatReq.setPageToken(resChat.getNextPageToken());
        	   resChat = chatReq.execute();
           		
           		//Long waitMsec = resChat.getPollingIntervalMillis();
	            List<LiveChatMessage> returnedList = resChat.getItems();
	            for(LiveChatMessage msg : returnedList) {	            	
	            	String text = msg.getSnippet().getTextMessageDetails().getMessageText();
	            	reactToChat(ChatType.TEXT, text);
	            }
	            
	            Thread.sleep(1000);
            }
        } catch (Throwable t) {
        	logger.error("Error occured, halting.", t);
        	// any exception occurs, it will halt.
            System.err.println("Throwable: " + t.getMessage());
            t.printStackTrace();
        }
    }
    
    public static void reactToChat(ChatType type, String text) {
    	switch(type) {
    	case MEMBERSHIP:
    		;
    	case SUPERCHAT:
    		;
    	case SUPERSTICKER:
    		;
    	default:
    		System.out.println(text);
    		
    		
    		;
    	}
    }
    
    private static function clickKey() {
		robot.keyPress(KeyEvent.VK_T);
		robot.keyRelease(KeyEvent.VK_T);
    }
    
    public static void login() throws Exception {
    	// This OAuth 2.0 access scope allows for read-only access to the
        // authenticated user's account, but not other types of account access.
        List<String> scopes = Lists.newArrayList(
        		"https://www.googleapis.com/auth/youtube.readonly");
        credential = Auth.authorize(scopes, DATASTORE);
    }
    
    public static void logout() throws Exception {
    	FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(
    			new File(System.getProperty("user.home") + "/" + Auth.CREDENTIALS_DIRECTORY));
        fileDataStoreFactory.getDataStore(DATASTORE).clear();
    }
}



