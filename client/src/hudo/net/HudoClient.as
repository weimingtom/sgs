/**
* connect server function
* @author sohudo@qq.com
* @datas 2008-09-03
* @version 0.0.1
*/
package hudo.net{
	import flash.events.Event;
	import flash.events.EventDispatcher;
	import flash.events.IOErrorEvent;
	import flash.events.ProgressEvent;
	import flash.events.SecurityErrorEvent;
	import flash.net.Socket;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	import flash.utils.ByteArray;
	import flash.utils.getTimer;
	
	import hudo.net.util.*;
	
	public class HudoClient extends EventDispatcher {
		private var socket:Socket;
		private var Version   :String="0.0.1";
		private var ServerHost:String="localhost";//"221.137.87.209";//
		private var ServerPort:int=23;
		
		private var connected:Boolean;
		public var connecting:Boolean;//正在连接                
		
		private var msgcmd      :int;
		private var msgLen	:int;		//消息长度
		private var msgLenMax	:int=4099;	//收到的消息最大长度=包头+4K
		private var headLen	:int=3;		//消息头长度
		private var isReadHead	:Boolean=true;	//是否已经读了消息头	
		
		
		public var debug:Boolean=true;
		
		private var isautoLogin:Boolean=false;
		public  var islogin:Boolean=false;
		public  var myName:String="";
		private var mypass:String="";

		public var myUserId:int;
		public var playerId:int;
		public var integral:int=0;

		 
		private var sysHandler:SysHandler;
		private var sgsHandler:SgsHandler;
		public var channels:HashMap  = new HashMap();
		public var mychannel:ClientChannel;

		
		public function HudoClient() {
			
			sysHandler = new SysHandler(this);
			sgsHandler = new SgsHandler(this);
			socket=new Socket();			
			// socket.connect(ServerHost,ServerPort);
			socket.addEventListener(Event.CONNECT,funConnect);
			socket.addEventListener(Event.CLOSE,funClose);
			socket.addEventListener(ProgressEvent.SOCKET_DATA,funSocket);			
			 
			socket.addEventListener(IOErrorEvent.IO_ERROR, handleIOError);
			socket.addEventListener(IOErrorEvent.NETWORK_ERROR, handleSocketError);
			socket.addEventListener(SecurityErrorEvent.SECURITY_ERROR, handleSecurityError);
			 
			loadConfig(); 

			
		}
		public function autoconnect(autoLogin:Boolean = false):void	{
                   isautoLogin=autoLogin;
		   this.connect( ServerHost, ServerPort);
			trace("autoconnect " + ServerHost + ":" + ServerPort);		   
		   if( connected ) {
		      autologin();
		   }
		}
		public function connect(ipAdr:String, port:int = 8014):void
		{
			if (!connected)
			{
				initialize()
				this.ServerHost = ipAdr
				this.ServerPort = port
				connecting=true;
				socket.connect(ipAdr, port)
				debugMessage(" Connect: " + ServerHost + "\n");				
			}
			else
				debugMessage("*** ALREADY CONNECTED ***")
		}
		
		public function loadConfig(configFile:String = "config.xml", autoConnect:Boolean = true):void
		{
			//this.autoConnectOnConfigSuccess = autoConnect
			
			var loader:URLLoader = new URLLoader()
			loader.addEventListener(Event.COMPLETE, onConfigLoadSuccess)
			loader.addEventListener(IOErrorEvent.IO_ERROR, onConfigLoadFailure)
			
			loader.load(new URLRequest(configFile));
		}
		private function onConfigLoadSuccess( evt:Event ):void
		{
			var loader:URLLoader = evt.target as URLLoader
			var xmlDoc:XML = new XML( loader.data );

			this.ServerHost =  xmlDoc.ip;
			this.ServerPort = int(xmlDoc.port);
				
			if ( xmlDoc.debug != undefined )
				this.debug = xmlDoc.debug.toLowerCase() == "true" ? true : false		


		}
		
		private function onConfigLoadFailure( evt:IOErrorEvent ):void
		{
			var params:Object = { message:evt.text }
			var hdEvt:HDEvent = new HDEvent( HDEvent.onConfigLoadFailure, params )
			
			dispatchEvent( hdEvt )
			debugMessage("Config Load Failure: " + evt.text)
		}
		
		private function handleIOError(evt:IOErrorEvent):void
		{
			var params:Object = { message:evt.text }
			var hdEvt:HDEvent = new HDEvent( HDEvent.onSocketError, params )
			dispatchEvent( hdEvt )
			debugMessage("IO Error: " + evt.text);
			connecting=false;
			this.connected = false;
		}
		
		private function handleSocketError(evt:SecurityErrorEvent):void
		{
			debugMessage("Socket Error: " + evt.text);
			connecting=false;
			this.connected = false;
		}
		
		private function handleSecurityError(evt:SecurityErrorEvent):void
		{
			debugMessage("Socket Security Error: " + evt.text);
			connecting=false;
			this.connected = false;
		}
		
		public function debugMessage(message:String):void
		{
			if (this.debug)
			{
			  var evt:HDEvent = new HDEvent(HDEvent.onDebugMessage, {message:message})
			  dispatchEvent(evt)
			}
		}
		
		public function disconnect():void
		{
			connected = false;
			connecting=false;
			socket.close();
			sysHandler.dispatchDisconnection();

		}
		public function get isConnected():Boolean
		{
			return this.connected
		}
		
		public function set isConnected(b:Boolean):void
		{
			this.connected = b
		}
		private function funConnect(event:Event):void {
		  connected=true;
		  connecting=false;
		  debugMessage("服务器连接成功！");
		  var evt:HDEvent = new HDEvent(HDEvent.onConnection, {success:true})
		  dispatchEvent(evt)	
		  if( isautoLogin ) {
		    autologin();	
		  }
		}
		
	      private function funClose(event:Event):void
		{
			connecting=false;
			initialize();	
			this.connected = false;

	 		var hde:HDEvent = new HDEvent(HDEvent.onConnectionLost, {})	
	 		dispatchEvent(hde)
			debugMessage("服务器连接失败！");
		}
		
	
		/**
		 * Get the HudoClient Flash API version.
		 * 
		 * @return	API版本号.
		 * 
		 * @example	The following example shows how to trace the HudoClient API version.
		 * 			<code>
		 * 			trace("Current API version: " + HudoClient.getVersion())
		 * 			</code>
		 * 
		 * @version	HudoClient
		 */
		public function getVersion():String
		{
			return this.Version;
		}



		public function autologin() : void
		{
            socketsendlogin();               	
 		}

		public function setlogin(zone:String, name:String, pass:String):void
		{
		    myName=name;
		    mypass=pass;
		}

		public function login(name:String, pass:String):void
		{
		    myName=name;
		    mypass=pass;	
		    autoconnect(true);		
		}
		
  
		public function sendPublicMessage(message:String, roomId:int = -1):void
		{
			if (message=="" ) return		
			
			var xmlMsg:String = Entities.encodeEntities(message) ;
			
			broadroom(CMD.SV_pubMsg,xmlMsg);
		}
		

		public function sendPrivateMessage(recipient:String,message:String):void
		{

			broadroom(CMD.SV_prvMsg,recipient+CMD.SPLIT+message);
		}
		

		public function __logout():void
		{
			initialize(true)
		}
		private function initialize(isLogOut:Boolean = false):void
		{
			// Clear local properties
			this.playerId = -1
			this.myUserId = -1
			
			
			this.islogin=false;
			this.connecting=false;
			// Set connection status
			if (!isLogOut)
			{
			   this.connected = false
	
			}
		}
		
		
		
                public function joinChannel(chaname:String):void
                {
                    socketsend(CMD.Room_Change,chaname);		
                }       

		
		private  function broadroom(cmd:int,value:String):void
		{
		  socketsend(cmd,myName+CMD.SPLIT+value);
		}
		
                public function SendRoleMove(ax:int,ay:int):void 
		{
        	   var buf:ByteArray = new ByteArray();
		   buf.writeShort(SgsProtocol.Role_Move);
        	   buf.writeShort(ax);
		   buf.writeShort(ay);
		   buf.writeUTFBytes(myName);		       
        	   channelSend(mychannel,buf);
		}	

		private function GetStrLen(aStr : String) : int{
                   var aStrByts :ByteArray = new ByteArray();
                   aStrByts.writeUTFBytes(aStr);
                   var alen:int=aStrByts.length;
                   aStrByts=null;
                   return alen;
                }

		public function SendRoleMovepath2(arr:Array):void
		{
        	   var buf:ByteArray = new ByteArray();
		   buf.writeShort(SgsProtocol.Role_MovePath2);
        	   buf.writeShort(GetStrLen(myName));
		   buf.writeUTFBytes(myName);
		       
		   var x:int=arr.length; 
		   var y:int=arr[0].length;
		   var Str:String="";
		   for (var a:int=0; a < x; a++) {
			     	
		    for (var b:int=0;b<y;b++)
		   {			     
                     Str=Str+arr[a][b].toString();
                     if (b<y-1) {
                      Str=Str+CMD.SPLITD;                  
                      }
                   }
                    if (a<x-1) {
                      Str=Str+CMD.SPLITL;
                    }
		   }    
		   buf.writeShort(Str.length);
		   buf.writeUTFBytes(Str);
		   debugMessage("send arr:"+Str);	     
        	   channelSend(mychannel,buf);			
		}
				
		public function SendRoleMovepath(arr:Array):void
		{
        	   var buf:ByteArray = new ByteArray();
		   buf.writeShort(SgsProtocol.Role_MovePath);
        	   buf.writeShort(GetStrLen(myName));
		   buf.writeUTFBytes(myName);
		       
		   var x:int=arr.length; 
		   var Str:String="";
		   for (var a:int=0; a < x; a++) {
		    // buf.writeShort(arr[a].x);	
		    // buf.writeShort(arr[a].y);
		    // buf.writeShort(arr[a].z);
                      Str=Str+arr[a].x.toString()+CMD.SPLITD+arr[a].y.toString()+CMD.SPLITD+arr[a].z.toString();
                      if (a<x-1) {
                      Str=Str+CMD.SPLITL;
                      }
		    }    
		    buf.writeShort(Str.length);
		    buf.writeUTFBytes(Str);
		    debugMessage("send arr:"+Str);     
        	    channelSend(mychannel,buf);			
		 }
		
		private function SendObject(params:Object=null):void 
		{             
		   if(params != null){		
		       var bytes:ByteArray = new ByteArray();
		       bytes.writeObject(params);
		       bytes.compress(); 
		       socketsendarr(SgsProtocol.OBJECT_MESSAGE,bytes);
		   }
                 }  
		
                 private  function socketsendlogin():void
                 {
					 trace("socketsendlogin");
   			var buf:ByteArray = new ByteArray();	
			buf.writeByte(SgsProtocol.VERSION);
			buf.writeUTF(myName);
			buf.writeUTF(mypass); 
                        socketsendarr(SgsProtocol.LOGIN_REQUEST,buf);     
                } 
                private function channelSendMsg(cmd:int,value:String):void
                {
			var message:ByteArray=new ByteArray();
			message.writeShort(cmd);
			message.writeUTFBytes(value);
                       channelSend(mychannel,message);	        	
                 }
                private  function socketsend(cmd:int,value:String):void
		{
			var message:ByteArray=new ByteArray();
			message.writeShort(cmd);
			message.writeUTFBytes(value);
                        socketsendarr(SgsProtocol.SESSION_MESSAGE,message);
                       debugMessage("send cmd:"+cmd.toString()); 
		}	

		private function channelSend(channel:ClientChannel, message:ByteArray):void {
			var buf:ByteArray = new ByteArray();
			buf.writeShort(channel.rawId.length);
			buf.writeBytes(channel.rawId);
			buf.writeBytes(message);
			buf.position = 0;
                       socketsendarr(SgsProtocol.CHANNEL_MESSAGE,buf);
			
		}	
		

		public function logout(force:Boolean=false):void
		{
			if(force)
			{
				socket.close();
			}
			else
			{
				var buf:ByteArray = new ByteArray();	
				buf.writeByte(SgsProtocol.LOGOUT_REQUEST);				
				socket.writeShort(buf.length);
				socket.writeBytes(buf);
				socket.flush();	
			}
		}	
		

                private  function socketsendarr(cmd:int,message:ByteArray):void
		{
		  if (connected)
		  {	
			var buf:ByteArray = new ByteArray();
			buf.writeByte(cmd);
			buf.writeBytes(message);
			socket.writeShort(buf.length);		
			socket.writeBytes(buf);
			socket.flush();
			debugMessage("send sgs:"+cmd.toString()+" 大小"+(buf.length+2));
			buf.position=0;	
			showbyte(buf);
		  }
		}	

		private function showbyte(msg:ByteArray):void 
		{			
				
			var Str:String="";	
			while (msg.bytesAvailable>0)
			 {
			  var a:int=msg.readByte();	
			  Str=Str+a.toString(16)+" ";
			}
			debugMessage("Data:"+Str);
		}		
		private function funSocket(event:ProgressEvent):void
		{
			
			 var intCD:int=0;
			intCD = socket.bytesAvailable;
			 debugMessage("get data:"+socket.bytesAvailable);			 
			 resolvemsg(intCD);
   
	         }
	   
	
	   private function resolvemsg(Lstr:int):void
	   {
			var intCD:int=Lstr;
			var msg:String="";
		       if(isReadHead) {
			 if(intCD >= headLen) {
			  msgLen = socket.readShort();//读长度
			  msgcmd = socket.readByte();//读命令
			  isReadHead=false;
			  intCD=intCD-headLen;
			  msgLen--;
			  }
			 }
			 if(!isReadHead) {
			  if (intCD>=msgLen) {
			 // msg=socket.readMultiByte(msgLen,"utf8");
			  var newMessage:SgsByteArray = new SgsByteArray();	
			  socket.readBytes(newMessage,0,msgLen);				
			  debugMessage("get "+msgcmd+"="+CMD.GetCMDToStr(msgcmd)+":"+msgLen);
			  UpdataClientMsg(msgcmd,newMessage);
			  isReadHead = true;
			  intCD = intCD - msgLen;
			}
		   }	
		   if (intCD >= headLen){
		     debugMessage("拈包处理:"+intCD);
		     resolvemsg(intCD);
             }
	   }

	  private function UpdataClientMsg(msgcmd:int,msg:SgsByteArray):void
	  {
	  	  var handler:IMessageHandler;
	  	  if ((msgcmd>=SgsProtocol.CHANNEL_JOIN) || (msgcmd==SgsProtocol.SESSION_MESSAGE)) {
		     handler= sysHandler;
	  	  }
	  	  else {
		    handler =sgsHandler ; 	  	  	
	  	  }
		    if (handler != null)
		    handler.handleMessage(msg, msgcmd);		  	  
	   }		
	 
 
	}
}