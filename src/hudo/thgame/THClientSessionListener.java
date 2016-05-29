package thgame;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Iterator;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ChannelManager;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;

class THClientSessionListener implements Serializable, ClientSessionListener
{
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(THClientSessionListener.class.getName());
    private final ManagedReference<ClientSession> sessionRef;
    private String CHANNEL_NAME="";
    public THClientSessionListener(ClientSession session, ManagedReference<Channel> channel1)
    {
        if (session == null)
            throw new NullPointerException("null session");

        DataManager dataMgr = AppContext.getDataManager();
        sessionRef = dataMgr.createReference(session);
        
        //这里与客户端收消息有关，如果没有会导致客户端无法处理所有移动的消息（都是在channel中处理）
        channel1.get().join(session);
        CHANNEL_NAME=channel1.get().getName();
        logger.info("CHANNEL_NAME from :"+CHANNEL_NAME);
    }

    protected ClientSession getSession() {
        return sessionRef.get();
    }

    public void receivedMessage(ByteBuffer message) {
        ClientSession session = getSession();
        String sessionName = session.getName();
        logger.info("message from :"+sessionName);
        session.send(message);
    }
    
    public  ByteBuffer chSendCMD(String aName,Short num) {	
    	try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(buffer);
			out.writeShort(num);
			out.writeBytes(aName);
			byte[] temp = buffer.toByteArray();	
			logger.info("leav message from :"+aName);
			return ByteBuffer.wrap(temp);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}	
    }
    
    public void ChannelLeav(Channel achannel,ByteBuffer buf){
    	for(Iterator<ClientSession> i=achannel.getSessions();i.hasNext();) {
    		ClientSession client =(ClientSession)i.next();
    		client.send(buf); 	
    	}
    }

    public void disconnected(boolean graceful) {
    	ClientSession session = getSession();
    	String PName=session.getName();
    	ChannelManager channelMgr = AppContext.getChannelManager();
        Channel channel2 = channelMgr.getChannel(CHANNEL_NAME);
        
        ChannelLeav(channel2,chSendCMD(PName,THProtocol.RoleLeav));
        channel2.leave(session);        
        String grace = graceful ? "graceful" : "forced";
        logger.log(Level.INFO, "User {0} has logged out {1}", new Object[] { PName, grace } );
    }
}
