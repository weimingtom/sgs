package hudo.net
{
	import hudo.net.HudoClient;
	import hudo.net.HDEvent;

	import hudo.net.User;
	import hudo.net.util.Entities;
	import hudo.net.util.ObjectSerializer;
	import hudo.net.CMD;
	import flash.utils.getTimer;
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
	public class SysHandler implements IMessageHandler
	{
		private var HdClient:HudoClient
		private var handlersTable:Array
		
		function SysHandler(HdClient:HudoClient)
		{
			this.HdClient = HdClient	
			handlersTable = []
			
			handlersTable[SgsProtocol.CHANNEL_JOIN] 	= this.ChannelJoin
			handlersTable[SgsProtocol.CHANNEL_LEAVE] 	= this.ChannelLeave
			handlersTable[SgsProtocol.CHANNEL_MESSAGE] 	= this.ChannelMsg
			
			handlersTable[SgsProtocol.SESSION_MESSAGE]  = this.handleSessionMsg
			handlersTable[SgsProtocol.OBJECT_MESSAGE] 	= this.ObjectMsg
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
		
		
		// Handle correct API
		public function ChannelJoin(o:SgsByteArray):void
		{    		
     		var buf:ByteArray = new ByteArray();
     		var achannel :ClientChannel;
     		
     		var channelName:String = o.readSgsString();
     		o.readBytes(buf);
     		    		
    		achannel= new ClientChannel(channelName, buf);
     		HdClient.channels.put(achannel.id, achannel);				
  			var params:Object = {}
			params.channel = achannel;	
			params.channelName=channelName;
			HdClient.mychannel= achannel;			
			var evt:HDEvent = new HDEvent(HDEvent.OnChannelJoin, params)
			HdClient.dispatchEvent(evt)	
		}
		
		
		// Handle obsolete API
		public function ChannelLeave(o:SgsByteArray):void
		{
     		var buf:ByteArray = new ByteArray();
     		var achannel:ClientChannel;
     		
     		o.readBytes(buf);
     		achannel=HdClient.channels.getValue(ClientChannel.bytesToChannelId(buf));
      		if(achannel!=null) {
     			HdClient.channels.remove(achannel.id);
      		}
  			var params:Object = {}
			params.channel = achannel;			
			var evt:HDEvent = new HDEvent(HDEvent.OnChannelLeave, params)
			HdClient.dispatchEvent(evt)	     			
		}

		public function dispatchDisconnection():void
		{
			var evt:HDEvent = new HDEvent(HDEvent.onConnectionLost, null)
			HdClient.dispatchEvent(evt)		
		}
		public function ObjectMsg(o:SgsByteArray):void
		{
		  var bytes:ByteArray = new ByteArray();
		  o.readBytes(bytes, 0, o.bytesAvailable);
		  bytes.uncompress();
		  var evt:HDEvent = new HDEvent(HDEvent.OnObject, bytes.readObject())
		  HdClient.dispatchEvent(evt)
		}		
		
       public function ChannelMsg(o:SgsByteArray):void
       {        
       	  var buf:ByteArray = new ByteArray();
       	  var achannel :ClientChannel;
       	  var cid:int=o.readShort();
       	  o.readBytes(buf,0,cid);
       	  achannel = HdClient.channels.getValue(ClientChannel.bytesToChannelId(buf));
       	
       	  var cmd:int = o.readShort();
       	  var Pname:String='';
       	  if(cmd==SgsProtocol.Role_Move  ) {
 			   var  ax:int   = o.readShort();
			   var  ay:int   = o.readShort();
			   Pname = o.readUTFBytes(o.bytesAvailable);   	  	
       	  	  RoleMove(Pname,ax,ay);
       	  	  return ;
       	  }
       	  if(cmd==SgsProtocol.Role_MovePath  ) {	
       	  	  Role_MovePath(o);
       	  	 return ; 
       	  }      
         if(cmd==SgsProtocol.Role_MovePath2  ) {	
       	  	 Role_MovePath2(o);
       	  	return ; 
       	  }  
 		   if( cmd==SgsProtocol.RoleLeav) {
		   	  Pname=o.readMultiByte(o.bytesAvailable,"utf8"); 
		   	  //Pname=o.readUTFBytes(o.bytesAvailable);
		   	 RoleLeav(Pname);
		   	 return ;
		   }
		         	  
		   if( cmd==SgsProtocol.RoleJoin) {
		   	Pname =o.readMultiByte(o.bytesAvailable,"utf8");
		   	//Pname=o.readUTFBytes(o.bytesAvailable);
		   	RoleJoin(Pname);
		   	return ;
		   }       	   
		   if (cmd==CMD.SV_pubMsg) {
		   	 Pname=o.readUTFBytes(o.bytesAvailable);
		   	 Get_pubMsg(Pname);
		   	 return ;
		   }      	   	  
       }  
       
		public function handleSessionMsg(o:SgsByteArray):void
		{
		   if (o.bytesAvailable<2) 
		   {
		     HdClient.debugMessage("Session 消息失败，长度不够");
		     return ;
		   }
		   var acmd:int = o.readShort();
		   var Pname:String='';
		   if( acmd==SgsProtocol.RoleLeav) {
		   	  Pname=o.readMultiByte(o.bytesAvailable,"utf8"); 
		   	  //Pname=o.readUTFBytes(o.bytesAvailable);
		   	 RoleLeav(Pname);
		   	 return ;
		   }	
		   if( acmd==SgsProtocol.RoleJoin) {
		   	Pname =o.readMultiByte(o.bytesAvailable,"utf8");
		   	//Pname=o.readUTFBytes(o.bytesAvailable);
		   	RoleJoin(Pname);
		   	return ;
		   }
		   if (acmd==CMD.SV_pubMsg) {
		   	 Pname=o.readUTFBytes(o.bytesAvailable);
		   	 Get_pubMsg(Pname);
		   	 return ;
		   }
		  if (acmd==CMD.Room_Users) {
		   	 Pname=o.readMultiByte(o.bytesAvailable,"utf8");
		   	 GetRoom_Users(Pname);		  	
		  } 
		  if (acmd==CMD.UseralLogin){
		  	 HdClient.logout(false);
		  	 trace("此用户已经登陆");
		  }
        }        
        private function RoleMove(pname:String,ax:int,ay:int):void
        {
  			var params:Object = {}
			params.playname = pname;
			params.rx	=ax;
			params.ry	=ay;		
			var evt:HDEvent = new HDEvent(HDEvent.OnRoleMove, params)
			HdClient.dispatchEvent(evt)	      	
        } 
        private function Role_MovePath(o:SgsByteArray):void
        {
   			var params:Object = {}
			params.playname = o.readSgsString();
			var arrstr:String=o.readSgsString();
			
			var arg:Array = arrstr.split(CMD.SPLITL);
			var x:int=arg.length;
			var arr:Array = new Array(x);			

			for(var j:uint=0;j<x;j++)
			{
			  var arrList:Array = arg[j].split(CMD.SPLITD);
			 // var alen:int=arrList.length;
			  var par:Object = {}
				par.x=int(arrList[0]);
				par.y=int(arrList[1]);
				par.z=int(arrList[2]);	
				arr[j]=par;		  
			}
			params.arr	=arr;
			HdClient.debugMessage("get arr:"+arrstr);
			/*	
			var x:int=o.readByte();
			var y:int=o.readByte();
			var arr:Array=new Array(x);
			for (var a:int=0; a < x; a++) {
				var par:Object = {}
				par.x=o.readShort();
				par.y=o.readShort();
				par.z=o.readShort();
				arr[a]=par;
			params.arr	=arr;
			trace(arr.toString());
			*/
			var evt:HDEvent = new HDEvent(HDEvent.OnRoleMovePath, params)
			HdClient.dispatchEvent(evt)	        	
        } 
        private function Role_MovePath2(o:SgsByteArray):void
        {
   			var params:Object = {}
			params.playname = o.readSgsString();
			var arrstr:String=o.readSgsString();
			
			var arg:Array = arrstr.split(CMD.SPLITL);
			var x:int=arg.length;
			var arr:Array=new Array(x);
			
			var OnStr:String;
			for(var j:uint=0;j<x;j++)
			{
			  var arrList:Array = arg[j].split(CMD.SPLITD);
			  var alen:int=arrList.length;
			  	arr[j]=new Array(alen);
			  	for (var b:int=0;b<alen;b++)
			  	 arr[j][b]=int(arrList[b]);	  
			}
			params.arr	=arr;
			HdClient.debugMessage("get arr2:"+arrstr);
			var evt:HDEvent = new HDEvent(HDEvent.OnRoleMovePath2, params)
			HdClient.dispatchEvent(evt)	        	
        }  
        private function RoleJoin(pname:String):void
        {
  			var arg:Array =pname.split(CMD.SPLITD);
  			var params:Object = {}
			params.playname = arg[0];
			params.playID	= int(arg[1]);			
			var evt:HDEvent = new HDEvent(HDEvent.OnRoleJoin, params)
			HdClient.dispatchEvent(evt)	      	
        } 
         private function RoleLeav(pname:String):void
        {
  			var arg:Array =pname.split(CMD.SPLITD);
  			var params:Object = {}
			params.playname = arg[0];
			params.playID	= int(arg[1]);		
			var evt:HDEvent = new HDEvent(HDEvent.OnRoleLeav, params)
			HdClient.dispatchEvent(evt)	      	
        }                   
       private function  Get_pubMsg(amsg:String):void   
       {
       	               var arg:Array = amsg.split(CMD.SPLIT);
  			var params:Object = {}
			params.playname = arg[0];   
			params.msg = arg[1];    	    
 			var evt:HDEvent = new HDEvent(HDEvent.onPublicMessage,params)
			HdClient.dispatchEvent(evt)	       	
       }    
 
 
       private function  GetRoom_Users(amsg:String):void 
       {
       	    var arg:Array = amsg.split(CMD.SPLIT);
       	    var userList:Array;
       	    userList = [];
       	    var x:int=arg.length;
       	    for ( var i:int=0;i<x-1;i++){
       	       var arrList:Array = arg[i].split(CMD.SPLITD);	
       	       var name:String 	= arrList[0];	
       	       var uid:int      = int(arrList[1]);	 
       	       if (name!="") {
       	       var user:User = new User(uid, name)
       	    	userList[i] = user
       	       }
       	    }
  			var params:Object = {}   
			params.Users = userList;    	    
 			var evt:HDEvent = new HDEvent(HDEvent.OnChannelUsers,params)
			HdClient.dispatchEvent(evt)	      	
       }       
                     
	}
}