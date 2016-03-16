package dev.ruun.acwbot;

import java.io.IOException;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

public class ACWBot extends PircBot {
    	    
	public ConfigReader cr;
	
	public void startUp() throws NoConfigException, NumberFormatException, NickAlreadyInUseException, IOException, IrcException {
		if(cr != null) {
			/* Start up */
			this.connect(cr.loadProperty("server"), Integer.parseInt(cr.loadProperty("port")));
			this.changeNick(cr.loadProperty("nickname"));
			String channels = cr.loadProperty("channels");
			if(channels.contains("|")) {
				for(String channel : channels.split("\\|")) {
					this.joinChannel(channel);
					System.out.println("Attempting to join "+ channel);
				}
			} else {
				this.joinChannel(channels);
				System.out.println("Attempting to join "+ channels);
			}
		    
		    (new Thread(new FeedReader(this))).start();
		} else {
			throw new NoConfigException();
		}
	}
	
	public void publishFeed(String message) {
		this.sendMessage(cr.loadProperty("mainchannel"), message);
	}
	
	public void publishFeed(String message, String channel) {
		this.sendMessage(channel, message);
	}
    
    public String trimMessage(String message) {
    	String msg = null;
    	if(message.substring(0,1).equalsIgnoreCase("!")) {
    		msg = message.substring(1);
    	} else if(message.length() > this.getNick().length() + 2) {
    		if(message.substring(0, this.getNick().length() + 2).equalsIgnoreCase(this.getNick() + ", ")) {
    			msg = message.substring(this.getNick().length() + 2);
    		} else if (message.substring(message.length() - this.getNick().length()).equalsIgnoreCase(this.getNick())) {
    			msg = message.substring(0, message.length() - this.getNick().length());
    		} else if(message.substring(0, this.getNick().length() + 1).matches(this.getNick() + " ")) {
    			msg = message.substring(this.getNick().length() + 1);
    		}
    	}
    	System.out.println(msg.trim());
    	return msg.trim();
    }
    
    public String getCommand(String message) {
    	String msg = this.trimMessage(message);
		String[] tParams = msg.split(" ");
		String command = tParams[0];
		return command;
    }
    
    public String[] getParams(String message) {
    	String msg = this.trimMessage(message);
		String[] tParams = msg.split(" ");
		String[] params = new String[tParams.length];
		Boolean first = true;
		Integer i = 0;
		for(String p : tParams) {
			if(first) {
				first = false;
			} else {
				first = false;
				params[i] = p;
				i++;
			}
		}
		return params;
    }
	
	public void onServerResponse(int code, String response) {}
    public void onUserList(String channel, User[] users) {}
    public void onMessage(String channel, String sender, String login, String hostname, String message) {}
    public void onPrivateMessage(String sender, String login, String hostname, String message) {
    	if(sender.equals("iCurse")) {
    		if(message.substring(0, 4).equals("!msg")) {
    			String[] params = getParams(message);
    			String target = params[0];
    			StringBuilder tMsg = new StringBuilder();
    			for(Integer i = 1; i < params.length - 1; i++) {
    				if(i > 1) {
        				tMsg.append(" " + params[i]);
        				System.out.println("Not first: " + params[i]);
    				} else {
        				tMsg.append(params[i]);
        				System.out.println("First: " + params[i]);
    				}
    			}
    			sendMessage(sender, "MESSAGE TO " + target);
    			sendMessage(sender, tMsg.toString());
    			sendMessage(target, tMsg.toString());
    		}
    	} else {
    		sendMessage("iCurse", "<"+ sender + "> " + message);
    	}
    }
    public void onAction(String sender, String login, String hostname, String target, String action) {}
    public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
    	if(!sourceNick.equals("iCurse")) {
    		sendMessage("iCurse", sourceNick + ">"+target + "; " + notice);
    	}
    }
    public void onJoin(String channel, String sender, String login, String hostname) {}
    public void onPart(String channel, String sender, String login, String hostname) {}
    public void onNickChange(String oldNick, String login, String hostname, String newNick) {}
    public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {}
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {}
    public void onTopic(String channel, String topic) {}
    public void onTopic(String channel, String topic, String setBy, long date, boolean changed) {}
    public void onChannelInfo(String channel, int userCount, String topic) {}
}
