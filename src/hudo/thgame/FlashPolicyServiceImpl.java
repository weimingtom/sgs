package thgame;

import com.sun.sgs.kernel.ComponentRegistry;
import com.sun.sgs.service.Service;
import com.sun.sgs.service.TransactionProxy;
import java.util.Properties;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoAcceptorConfig;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

public class FlashPolicyServiceImpl implements Service {
    private static final int PORT = 843;
    private static boolean isBindSuccess = false;
    public FlashPolicyServiceImpl(Properties properties, ComponentRegistry systemRegistry,TransactionProxy txnProxy) throws Exception {
    	System.out.println("FlashPolicyServiceImpl init");
    	
    	IoAcceptor acceptor = new SocketAcceptor();
    	IoAcceptorConfig config = new SocketAcceptorConfig();
        
        try {
			acceptor.bind(new InetSocketAddress(PORT), new FlashPolicyProtocolHandler(), config);
			isBindSuccess = true;
			System.out.println("FlashPolicyServiceImpl Listening on port " + PORT);    
        } catch (IOException e) {
			e.printStackTrace();
		} 
        
    	return;
	}
    
    public String getName() {    	
    	return toString();
    }

    public void ready() throws Exception { 
    	if(isBindSuccess) {
    		System.out.println("FlashPolicyServiceImpl is ready");
    	} else {
    		throw new Exception("只可以开一个SGS");
    	}
    }

    public void shutdown() {
    	System.out.println("FlashPolicyServiceImpl shutdown");
    }
}

