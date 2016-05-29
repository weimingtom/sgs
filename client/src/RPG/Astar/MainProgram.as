package RPG.Astar{
	import flash.display.Sprite;
	import flash.geom.Rectangle;
	import flash.net.LocalConnection;
    import flash.events.*;
	import flash.net.*;
	import flash.utils.ByteArray;
	import flash.utils.getTimer;
	
	import hudo.net.*;
//	import org.aswing.AsWingManager;
//	import org.aswing.JOptionPane;

	public class MainProgram extends Sprite {
		private var nowId:uint;
		private var mapArray:Array;//定义存储地图数据二维数组
		private var eleArray:Array;//定义地图元素数组
		private var maps:Maps;//定义一个地图类
		//=================================================================================================
		private var _client:HudoClient;
		//var _host:String = "www.sohudo.com";
		//var _port:int = 1139;
		//var _confName = "config.conf";
		private var myName:String;
		private var mypass:String;
		//private var loginw:LoginWindow
		private var loginw:LoginMC = new LoginMC();
		
		///
		public function MainProgram() {
			if (stage) initial();
			else addEventListener(Event.ADDED_TO_STAGE, initial);
		}

		private function initial(e:Event = null):void 
		{
			removeEventListener(Event.ADDED_TO_STAGE, init);
			// entry point
			
			//AsWingManager.initAsStandard(this.stage);
			//loginw=new LoginWindow();			
			//loginw.Show(this);
			addChild(loginw);
			loginw.btnLogin.addEventListener(MouseEvent.CLICK,onLoginClick);
			init();
			//onLoginClick(null);
		}
		

		private function connect():void{					
			_client.login(myName,mypass);
			write("Login");
		}
		//=================================================================================================
		private function init():void {		
                        _client =new HudoClient();
			//trace(_host+":"+_port);

			_client.addEventListener(HDEvent.onLogin, onLoginSuccess);
			_client.addEventListener(HDEvent.onConnectionLost,onConnectionLost);
		        _client.addEventListener(HDEvent.onLoginRedirect, onLoginRedirect);
		    
			_client.addEventListener(HDEvent.OnChannelJoin,OnCHANNEL_JOIN);

			_client.addEventListener(HDEvent.OnRoleJoin,OnRoleJoin);
		        _client.addEventListener(HDEvent.OnRoleMove,OnRoleMove);
		        _client.addEventListener(HDEvent.OnRoleMovePath2,OnRoleMovePath2);
		    
		        _client.addEventListener(HDEvent.OnRoleLeav,OnRoleLeav);

			_client.addEventListener(HDEvent.onLogout,onLogout);
			
			
			nowId=1;
			initArray();
			//------------------------------------------------------
			//生成一个700*500的矩形显示区域。
			var window:Rectangle = new Rectangle(0, 0, 800, 600);
			scrollRect = window;
			//------------------------------------------------------
		/*
		    Inchat.visible=true;
			Inchat.x=1;
			Inchat.y=577;
			addChild(Inchat);
			Inchat.addEventListener(KeyboardEvent.KEY_DOWN,OnChatKey);
			*/
		}
		private function ShowMap():void{
			//loginw.hide();
			removeChild(loginw);
			trace("ShowMap");
			maps=new Maps(0,0,mapArray,eleArray,nowId);
			addChild(maps);//让地图显示
			addEventListener(ChangeMapEvent.CHANGE_MAP,goNextMap);//自定义ChangeMapEvent事件是由人物GameHuman类的实例发出的。本类是在冒泡阶段捕获。

		}
		public function onLoginClick(e:MouseEvent):void{
			myName = loginw.txtName.text;
			mypass = loginw.txtPassword.text;
			//myName = (Math.random() * getTimer()).toString();
			//mypass = (Math.random() * getTimer()).toString();
			
			trace("onLoginClick");			
			connect();
			
		}
		public function OnChatKey(e:Event):void{
		   /* if(e.keyCode==13 ) 
			{
			  if (Inchat.text==""){
				Inchat.text="请输入名称！";  
			  }
			  else {
			   myName	=Inchat.text;  
			   connect();
			   Inchat.text="";
			  }
		    }
			*/
		}
		
		//=================================================================================================
		//定义加载下一个地图函数。
		private function goNextMap(evt:ChangeMapEvent):void{
			var id:uint = evt.nextMapId;
			removeChild(maps);
			nowId=id;
			initArray();
			maps=new Maps(MapData.MAP_PASSAGEWAY[id][0][0],MapData.MAP_PASSAGEWAY[id][0][1],mapArray,eleArray,nowId);
			addChild(maps);
			//doClearance();
		}
		private function initArray():void{
			mapArray=MapData.createMapData(nowId);
			//trace(mapArray);
			eleArray=MapData.createEleArray(nowId,MapData.MAP_ELEARRAY[nowId][1]);
		}
		//=================================================================================================
		/*垃圾回收机强制调用
		private function doClearance( ) : void {
                        trace("clear");
                        try{
                                new LocalConnection().connect("foo");
                                new LocalConnection().connect("foo");
                        }catch(error : Error){
                                
                        }                        
                }*/
		//sgs callback
		
		        //登陆成功
        public function onLoginSuccess(e:HDEvent):void  {
           if (e.params.success){
               write("登陆服务器成功");
               ShowMap();
           }    
		   else
		     //JOptionPane.showMessageDialog("提示","登陆服务器失败! 此用户登陆或者网络异常！");
              write("登陆服务器失败")	

        }
         public function onConnectionLost(evt:HDEvent):void  
       {
       	write("onConnectionLost");
       } 
        //登陆失败  
        public function onLoginFailure(event:HDEvent):void  {
            //	write("onLoginFailure:" + event.failureMessage);
        }    
        
        //系统重定向
        public function onLoginRedirect(evt:HDEvent):void  {
          write("onLoginRedirect:" + evt.params.host + " :" + evt.params.port);

        } 
		     
        public function onLogout(e:HDEvent):void {
            	write("onLogout()");
        }  
		public function OnCHANNEL_JOIN(e:HDEvent):void {
			write("CHANNEL_JOIN");
			maps.isLogin=true;
			maps.sgsClient=_client;
		}
	
		public function OnRoleJoin(evt:HDEvent):void {
			write("用户加入:"+evt.params.playname);
		    //maps.RoleCreate(evt.params.playname,0,0);
		}
		public function OnRoleMovePath2(evt:HDEvent):void {
			if (_client.myName!=evt.params.playname)
			{  
			 // write("用户寻路:"+evt.params.playname);		
			 // maps.RoleMovePath(evt.params.playname,evt.params.arr);	
			}		
		}
		public function OnRoleMove(evt:HDEvent):void {
			write("用户移动:"+evt.params.playname);								
			if (_client.myName!=evt.params.playname)
			    maps.RoleMove(evt.params.playname,evt.params.rx,evt.params.ry);
			
        }          
        //接收该用户的消息
        public function OnRoleLeav(evt:HDEvent):void {
 			write("用户离开:"+evt.params.playname);
			maps.DelRole(evt.params.playname);							
        }  
		
		private function write(str:String):void{
			//msgText.text +=str+"\n";
			trace(str);
		}
	

			
	}
}