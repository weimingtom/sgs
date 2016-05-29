package thgame;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.ByteBuffer;


public class FlashPolicyProtocolHandler extends IoHandlerAdapter {	
    private static String POLICY_FILE = "<cross-domain-policy>" + 
    	"<site-control permitted-cross-domain-policies=\"all\"/>" +
    	"<allow-access-from domain=\"*\" to-ports=\"*\" />" +
    	"</cross-domain-policy>";	
    
	private Charset ch = Charset.forName("utf-8");     
	private CharsetDecoder decoder = ch.newDecoder();  
	private CharsetEncoder encoder = ch.newEncoder();
    
    public void exceptionCaught(IoSession session, Throwable cause) {
        session.close();
    }
   
	@Override
    public void messageReceived(IoSession session, Object message) {
        try {
			//System.out.println("received...");
			if(! (message instanceof ByteBuffer)) {
				session.close();
				return ;
			}
			ByteBuffer rb = (ByteBuffer) message;
			String str = rb.getString(decoder);			
			//System.out.println(str);			
			if(str.equals("<policy-file-request/>")){
	        	//System.out.println("<policy-file-request/>");
	        	ByteBuffer wb = ByteBuffer.allocate(POLICY_FILE.length());
				wb.putString(POLICY_FILE, encoder);
				wb.flip();
	            session.write(wb);
	        }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
    }
}
	
