package org.sjtu.transformers.catchme.app;

import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sjtu.transformers.catchme.core.ClientListenerImpl;
import org.sjtu.transformers.catchme.core.Constants;
import org.sjtu.transformers.catchme.core.GameManager;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.AppListener;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ChannelManager;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.DataManager;

public class AppListenerImpl implements AppListener, Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger("CatchMe App");

	@Override
	public void initialize(Properties props) {
		logger.log(Level.INFO, "Initializing......");
		GameManager gameManager = new GameManager();
		gameManager.createGame(Constants.UNIVERSAL_GAME_NAME);
		
		DataManager dataManager = AppContext.getDataManager();
		dataManager.setBinding(Constants.GAMEMANAGER_BINDING_NAME, gameManager);
	}

	@Override
	public ClientSessionListener loggedIn(ClientSession session) {
		logger.log(Level.INFO, "App检测到login信号：User {0} has logged in", session.getName());
	
        ChannelManager channelMgr = AppContext.getChannelManager();        
        Channel channel = channelMgr.getChannel(Constants.UNIVERSAL_GAME_NAME);
        channel.join(session);
        logger.info("进入channel：CHANNEL_NAME from :" + Constants.UNIVERSAL_GAME_NAME);
        
		return ClientListenerImpl.loggedIn(session);
	}

}
