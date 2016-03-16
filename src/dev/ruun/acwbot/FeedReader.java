package dev.ruun.acwbot;

import java.net.URL;

import org.jibble.pircbot.Colors;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class FeedReader implements Runnable {
	
	private Integer lastTime;
	
	private String feedBase = "http://ruun.nl/acwiki/recentchanges.php";
	private URL feedUrl;
	private ACWBot bot;
	private Integer lastsave;
	
	public FeedReader(ACWBot bot) {
		this.bot = bot;
		this.lastsave = Math.round(System.currentTimeMillis() / 1000);
	}

    public void run() {
    	System.out.println("FeedReader started");
    	
    	this.lastTime = Integer.parseInt(bot.cr.loadProperty("timestamp"));
    	
    	while(true) {
        	this.cycle();
        	try {
				Thread.sleep(10 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public void cycle() {
        HttpResponse<String> apiRequest = null;
        Integer tempLastTime = this.lastTime;
		try
		{
			apiRequest = Unirest.get("http://ruun.nl/acwiki/feed.json").asString();
		}catch (UnirestException e){e.printStackTrace();
		}
        
        if(apiRequest != null) {
        	//System.out.println(apiRequest.getBody());
        	try {
	        	JSONArray ar = new JSONArray(apiRequest.getBody());
	        	for(Integer i = 0; i < ar.length(); i++) {
	        		JSONObject item = ar.getJSONObject(i);
	        		if(item.getInt("timestamp") > this.lastTime) {
	        			System.out.println(item.getInt("timestamp"));
	        			if(item.getInt("timestamp") > tempLastTime)
	        				tempLastTime = item.getInt("timestamp");
	        			
	        			
	        			String guidURL = item.getString("guid").replace("&amp;", "&");
	        			
	        			String publishString = null;
	        			String sDesc = item.getString("shortdesc");
	
	        			switch(item.getString("type")) {
		                    case "FILE_UPLOAD":
		                        publishString = (
		                                item.getString("author") +
		                                Colors.DARK_GREEN + " uploaded file " +
		                                Colors.LIGHT_GRAY + "[" + 
		                                Colors.DARK_GRAY + item.getString("title") + 
		                                Colors.LIGHT_GRAY + "] " +
		                                Colors.BLUE + guidURL);
	        							this.bot.publishFeed(publishString, "#ACWiki-Files");
		                        break;
		                    case "FILE_DELETED":
		                        publishString = (
		                                item.getString("author") +
		                                Colors.DARK_GREEN + " deleted file " +
		                                Colors.LIGHT_GRAY + "[" + 
		                                Colors.DARK_GRAY + item.getString("title") + 
		                                Colors.LIGHT_GRAY + "] " +
		                                Colors.TEAL + sDesc);
	        							this.bot.publishFeed(publishString, "#ACWiki-Files");
		                        break;
		                    case "USER_CREATED":
		                        publishString = (
		                                Colors.LIGHT_GRAY + "[" + 
		                                Colors.DARK_GRAY + item.getString("title") + 
		                                Colors.LIGHT_GRAY + "] " +
		                                Colors.DARK_GREEN + " signed up " +
		                                Colors.BLUE + guidURL
		                                );
		                        break;
		                    case "USER_AVATAR":
		                        publishString = (
		                                Colors.LIGHT_GRAY + "[" + 
		                                Colors.DARK_GRAY + item.getString("title") + 
		                                Colors.LIGHT_GRAY + "] " +
		                                Colors.DARK_GREEN + " changed their avatar " +
		                                Colors.BLUE + guidURL
		                                );
		                        break;
		                    case "PAGE_CREATED":
		                        publishString = (
		                                item.getString("author") +
		                                Colors.DARK_GREEN + " created page " +
		                                Colors.LIGHT_GRAY + "[" + 
		                                Colors.DARK_GRAY + item.getString("title") + 
		                                Colors.LIGHT_GRAY + "] " +
		                                Colors.BLUE + guidURL + " " +
		                                Colors.TEAL + sDesc);
		                        break;
		                    case "PAGE_ROLLBACK":
		                        publishString = (
		                                item.getString("author") +
		                                Colors.DARK_GREEN + " reverted page " +
		                                Colors.LIGHT_GRAY + "[" + 
		                                Colors.DARK_GRAY + item.getString("title") + 
		                                Colors.LIGHT_GRAY + "] " +
		                                Colors.BLUE + guidURL + " " +
		                                Colors.TEAL + sDesc);
		                        break;
		                    case "PAGE_EDITED":
		                        publishString = (
		                                item.getString("author") +
		                                Colors.DARK_GREEN + " edited page " +
		                                Colors.LIGHT_GRAY + "[" + 
		                                Colors.DARK_GRAY + item.getString("title") + 
		                                Colors.LIGHT_GRAY + "] " +
		                                Colors.BLUE + guidURL + " " +
		                                Colors.TEAL + sDesc);
		                        break;
		                    case "PAGE_MOVED":
		                        publishString = (
		                                item.getString("author") +
		                                Colors.DARK_GREEN + " moved page " +
		                                Colors.LIGHT_GRAY + "[" + 
		                                Colors.DARK_GRAY + item.getString("title") + 
		                                Colors.LIGHT_GRAY + "] " +
		                                Colors.BLUE + guidURL + " " +
		                                Colors.TEAL + sDesc);
		                        break;
		                    case "PAGE_DELETED":
		                        publishString = (
		                                item.getString("author") +
		                                Colors.DARK_GREEN + " deleted page " +
		                                Colors.LIGHT_GRAY + "[" + 
		                                Colors.DARK_GRAY + item.getString("title") + 
		                                Colors.LIGHT_GRAY + "] " +
		                                Colors.TEAL + sDesc);
		                        break;
		                    case "BLOCKED":
		                        publishString = (
		                                item.getString("author") +
		                                Colors.DARK_GREEN + " blocked user" +
		                                Colors.TEAL + sDesc);
		                        break;
		                    case "FALLBACKTYPE":
		                        publishString = (
		                                item.getString("author") +
		                                Colors.DARK_GREEN + " did something wrong. " +
		                                Colors.TEAL + "Please notify SIMA");
		                        break;
		                    default:
		                        publishString = (
		                                item.getString("author") +
		                                Colors.DARK_GREEN + " did something wrong. " +
		                                Colors.TEAL + "Please notify SIMA");
		                        break;
	        	    	}
	        			
	        			if(publishString != null) {
	        				System.out.println(publishString);
	        				this.bot.publishFeed(publishString);
	        				
	        		        if(this.lastTime != tempLastTime)
	        		        	System.out.println(this.lastTime + " -> " + tempLastTime);
	        		        this.lastTime = tempLastTime;
	        		        if((Math.round(System.currentTimeMillis() / 1000) - this.lastsave) > 360) {
	        		        	this.bot.cr.writeProperty("timestamp", ""+tempLastTime);
	        		        	this.lastsave = Math.round(System.currentTimeMillis() / 1000);
	        		        }
	        			}
	        		}
	        	}
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
    }
    
    /*public void checkFeed() { 
			String actionColor = Colors.DARK_GREEN;
			String bracketColor = Colors.LIGHT_GRAY;
			String pageColor = Colors.DARK_GRAY;
			String linkColor = Colors.BLUE;
			String descColor = Colors.TEAL;
    }*/

	public String getFeedBase() {
		return feedBase;
	}

	public void setFeedBase(String feedBase) {
		this.feedBase = feedBase;
	}

	public URL getFeedUrl() {
		return feedUrl;
	}

	public void setFeedUrl(URL feedUrl) {
		this.feedUrl = feedUrl;
	}
}