package dev.ruun.acwbot;

import java.io.IOException;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

public class Startup {
	    public static final String VERSION = PircBot._version + " by iCurse";
	    public static final Boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().
	    	    getInputArguments().toString().indexOf("jdwp") >= 0;

	public static void main(String[] args) {
		ACWBot bot = new ACWBot();
		
		try {
			bot.cr = new ConfigReader();
			bot.startUp();
		} catch (NumberFormatException | NoConfigException | IrcException | IOException e) { e.printStackTrace(); }
	}

}
