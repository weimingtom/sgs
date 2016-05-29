package org.sjtu.transformers.catchme.core;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.gamalocus.sgs.services.mysql.MySQLManager;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ChannelListener;
import com.sun.sgs.app.ClientSession;

/**
 * channel的消息
 * @author Administrator
 *
 */
public class GameChannelListener implements ChannelListener, Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(GameChannelListener.class.getName());

	@Override
	public void receivedMessage(Channel channel, ClientSession sender, ByteBuffer message) {
		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "Channel message from {0} player on channel {1}", new Object[] { sender.getName(), channel.getName() });
		}

    	logger.info("RoleMOVE message from :"+message.capacity());
    	try {
    		System.out.println(message);
	        if(GameProtocol.RoleMOVE == message.getShort()){        	
				int aX = message.getShort();
				int aY = message.getShort();
				String msg=decodeString(message);
	        	message.position(0);
	        	channel.send(sender, message);
	        	logger.log(Level.INFO, "[Channel] RoleMOVE message from :{0} X:{1} Y:{2}", new Object[]{msg, aX, aY});
	        	performMySQL( "[Channel] RoleMOVE message from :{" + msg + "} X:{"+ aX +"} Y:{" + aY+ "}");
	        } else {
	        	message.position(0);	
	        	logger.log(Level.INFO, "[Channel] message from : {0}", new Object[]{sender.getName()});
	        	channel.send(sender, message);
	        }   
    	} catch (BufferUnderflowException e) {
    		e.printStackTrace();
    	}
	}
	
    protected static String decodeString(ByteBuffer message) {
        try {
            byte[] bytes = new byte[message.remaining()];
            message.get(bytes);
            return new String(bytes, GameProtocol.MSG_UTF);
        } catch (UnsupportedEncodingException e) {
            throw new Error("Required character set " + GameProtocol.MSG_UTF + " not found", e);
        }
    }

    protected void performMySQL(String record) {
    	MySQLManager myman = AppContext.getManager(MySQLManager.class); 
    	//myman.executeSQL("UPDATE account SET credit=20 WHERE id=42", "account updates");
    	myman.executeSQL("log inserts", 
    			"INSERT INTO log (record) values ('" + record + "')");  
    }
}
