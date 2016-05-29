package thgame;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ChannelListener;
import com.sun.sgs.app.ClientSession;
import java.nio.ByteBuffer;

class THChannelsChannelListener implements Serializable, ChannelListener
{
    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(THChannelsChannelListener.class.getName());

    public void receivedMessage(Channel channel, 
                                ClientSession session, 
                                ByteBuffer message)
    {
    	logger.info("RoleMOVE message from :"+message.capacity());
        if(THProtocol.RoleMOVE == message.getShort()){        	
			int aX = message.getShort();
			int aY = message.getShort();
			String msg=decodeString(message);
        	message.position(0);
        	channel.send(session, message);
        	logger.info("[Channel] RoleMOVE message from :"+msg+" X:"+aX+" Y:"+aY);
        } else {    	
        	message.position(0);	
        	logger.info("[Channel] message from :"+session.getName());
        	channel.send(session, message);
        }
    }
    
    protected static String decodeString(ByteBuffer message) {
        try {
            byte[] bytes = new byte[message.remaining()];
            message.get(bytes);
            return new String(bytes, THProtocol.MSG_UTF);
        } catch (UnsupportedEncodingException e) {
            throw new Error("Required character set " + THProtocol.MSG_UTF +
                " not found", e);
        }
    }    
}
