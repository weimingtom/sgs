package hudo.net
{
	import flash.utils.ByteArray;
	/**
	 * SysHandler class: handles "sys" type messages.
	 * 
	 * @version	0.0.1
	 * 
	 * @author	The sohudo Team
	 * 			{@link http://www.sohudo.com}
	 * 
	 * @exclude
	 */
	public class SgsHandler implements IMessageHandler
	{
		private var HdClient:HudoClient
		private var handlersTable:Array
		
		function SgsHandler(HdClient:HudoClient)
		{
			this.HdClient = HdClient	
			handlersTable = []

			handlersTable[SgsProtocol.LOGIN_SUCCESS] 		= this.handleLoginOk
			handlersTable[SgsProtocol.LOGIN_FAILURE] 		= this.handleLoginKo
			handlersTable[SgsProtocol.LOGIN_REDIRECT]       = this.handleLoginRe
			
			handlersTable[SgsProtocol.RECONNECT_SUCCESS]    = this.handleReconnectOK
			handlersTable[SgsProtocol.RECONNECT_FAILURE]    = this.handleReconnectKo
			
			handlersTable[SgsProtocol.LOGOUT_SUCCESS]		= this.handleLogout

			

		}
		
		/**
		 * Handle messages
		 */
		public function handleMessage(msgarr:SgsByteArray, type:int):void
		{
			var fn:Function = handlersTable[type]
			
			if (fn != null)
			{
				fn.apply(this, [msgarr])
			}
			
			else
			{
				trace("Unknown sys command: " + type)
			}
		}
		
		// Handle successfull login
		public function handleLoginOk(o:SgsByteArray):void
		{	
			//o.readBytes(reconnectKey);//获得重连码
			var reconnectKey:ByteArray = new ByteArray();
			o.readBytes(reconnectKey);
                        HdClient.islogin=true;
	
			  var params:Object = {}
			  params.success = true;
			  params.reKey   =reconnectKey;
			 //params.name = name
		         // params.UserId=uid
			  params.error = ""			
			  var evt:HDEvent = new HDEvent(HDEvent.onLogin, params)
			  HdClient.dispatchEvent(evt)			  
		
		}
		

		// Handle successfull login
		public function handleLoginKo(o:SgsByteArray):void
		{

			var params:Object = {}
			params.success = false
			params.error = o.readSgsString();
			
			var evt:HDEvent = new HDEvent(HDEvent.onLogin, params)
			HdClient.dispatchEvent(evt)
		
		}
		
		public function handleLoginRe(o:SgsByteArray):void
		{
     		var newHost:String = o.readSgsString();
     		var newPort:int    = o.readInt();
			var params:Object = {}
			    params.success = false;
     			params.host = newHost;
     			params.port = newPort;
			
			var evt:HDEvent = new HDEvent(HDEvent.onLoginRedirect, params)
			HdClient.dispatchEvent(evt)
		}	
		
		public function handleReconnectOK(o:SgsByteArray):void
		{
  			var reconnectKey:ByteArray = new ByteArray();
			o.readBytes(reconnectKey);
     		//var params:Object = {}
     		//var reconnectKey = new ByteArray();
     		//o.readBytes(reconnectKey);
     		//params.reKey=reconnectKey
			//var evt:HDEvent = new HDEvent(HDEvent.onRECONNECT_SUCCESS, params)
			//HdClient.dispatchEvent(evt)     			
		
		}
		public function handleReconnectKo(o:SgsByteArray):void
		{
			var params:Object = {}
			params.error = o.readSgsString();			
			//var evt:HDEvent = new HDEvent(HDEvent.onRECONNECT_FAILURE, params)
			//HdClient.dispatchEvent(evt)			
		}			
		// Handle successful logout
		public function handleLogout(o:SgsByteArray):void
		{
			HdClient.__logout()			
			var evt:HDEvent = new HDEvent(HDEvent.onLogout, {})
			HdClient.dispatchEvent(evt)
		}


	}
}