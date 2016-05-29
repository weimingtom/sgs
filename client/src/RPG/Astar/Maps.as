/*
=================================================================================================
地图类，功能分为以下几个方面：
1，根据接受的地图数据生成地图，包括各个地图元素和人物。
2，根据鼠标的点击，计算出点击的X和Y值【X和Y对应地图数组的索引】。通过SearchRoad类找出路径，并发出WalkEvent事件，
   由GameHuman类的实例侦听。
	
=================================================================================================
*/
package RPG.Astar{
	import flash.display.Sprite;
	import flash.utils.getDefinitionByName;
	import flash.events.MouseEvent;
    import flash.utils.Dictionary;
	
	import hudo.net.*;
	import org.aswing.JOptionPane;	

	public class Maps extends Sprite {
		private var isoW:uint;//地图元素的宽度
		private var isoH:uint;//地图元素的高度
		private var nowX:uint;//地图中人物的初始X值,X和Y代表地图数组中的索引
		private var nowY:uint;//地图中人物的初始Y值
		private var nowId:uint;//地图编号
		private var mapArray:Array;//地图数组
		private var eleArray:Array;//地图元素数组
		private var walkArray:Array;//定义一个用来接受路径的数组
		private var human:GameHuman;//定义一个人物类
		private var foeman:Foeman;//定义一个敌人类

		private static var client:HudoClient; //BUG:只需初始一次
        private var userSet:Dictionary=new Dictionary() ;
	    public var isLogin:Boolean;
		//=================================================================================================
		////参数含义：地图中人物的X值，人物的Y值，地图数据，元素数据，地图中的出入口数据
		public function Maps(nx:uint,ny:uint,mArray:Array,eleArr:Array,id:uint) {
			nowX=nx;
			nowY=ny;
			mapArray=mArray;
			eleArray=eleArr;
			nowId=id;
			//aclient=client;			
			init();	

		}
		
		public function get sgsClient():HudoClient{
			return client;
		}
		public function set sgsClient(chl:HudoClient):void{
			client = chl;
		}		
		//=================================================================================================
		//生成地图函数
		private function createMaps():void {
			var mapBg:Sprite=new eleArray[0]();
			var mapEle:Sprite;
			mapBg.mouseEnabled=false;
			addChild(mapBg);
			var mapY:uint=mapArray.length;
			var mapX:uint=mapArray[0].length;
			for (var a:uint=0; a < mapY; a++) {
				for (var b:uint=0; b < mapX; b++) {
					if (a==nowX&&b==nowY) {
						human=new GameHuman(nowX,nowY,nowId);
						human.x=(isoW/2)*(nowX-nowY);
						human.y=(isoH/2)*(nowX+nowY);
						x=MapData.STAGE_X/2-human.x;
						y=MapData.STAGE_Y/2-human.y;
						addChild(human);
					}
					//------------------------------------------------------------
					//创建四个不受你控制的人物,如果觉得占用CPU，就注释掉。
					else if(a%8==0&&b%8==0&&a==b){
						//foeman=new Foeman(a,b,mapArray);
						//foeman.x=0;
						//foeman.y=isoH*a;
						//addChild(foeman);
					}
					//------------------------------------------------------------
					mapEle=new eleArray[mapArray[a][b]+1]();
					mapEle.name="element"+b+"_"+a;
					mapEle.x = (isoW/2)*(b-a);
					mapEle.y = (isoH/2)*(b+a);
					mapEle.mouseEnabled=false;
					addChild(mapEle);
				}
			}
			cacheAsBitmap=true;
		}
		//=================================================================================================
		private function init():void {
			isoW=MapData.ISO_W;
			isoH=MapData.ISO_H;
			isLogin=false;
			//this.mouseChildren=false;
			addEventListener(MouseEvent.MOUSE_DOWN,mouseClick);
			createMaps();
		}
		//=================================================================================================
		private function mouseClick(evt:MouseEvent):void {
                        if( client==null ) {
                           //root["Inchat"].text="请先登陆";
			   				write("请先登陆");
							//JOptionPane.showMessageDialog("提示","加入channel失败! 此用户登陆或者网络异常！");
                        }
                        else{
                        	
                         
			//获得单击鼠标时，鼠标的X和Y值
			if(evt.target==this){
			var clickedX:int = Math.floor(evt.localX/MapData.ISO_W+evt.localY/MapData.ISO_H);
			var clickedY:int = Math.floor(evt.localY/MapData.ISO_H-evt.localX/MapData.ISO_W);
			nowX=human.nowXindex;
			nowY=human.nowYindex;
				if (mapArray[clickedY][clickedX]==0) {
					client.SendRoleMove(clickedX,clickedY);
					/*var buf:BeyondoByteArray = new BeyondoByteArray();
					buf.writeByte(Protocol.RoleMOVE);
					buf.writeInt(clickedX);
					buf.writeInt(clickedY);
					//buf.writeInt(nowX);
					//buf.writeInt(nowY);
				        _channel.send(buf);
						*/
                         write("send RoleMOVE : X:"+clickedX+"Y:"+clickedY);
					var walkEvent:WalkEvent=new WalkEvent();
					walkEvent.walkArray=SearchRoad.startSearch(mapArray,clickedX,clickedY,nowX,nowY);
					addEventListener(WalkEvent.WALK_START,human.walking);
					dispatchEvent(walkEvent);
					removeEventListener(WalkEvent.WALK_START,human.walking);
				}
			}
                    }
		}
		public function DelRole(Sid:String):void{
                  if (userSet.propertyIsEnumerable(Sid)) {
		    var temrole:Foeman=Foeman(userSet[Sid].valueOf());	
                   removeChild(temrole); 
                   userSet[Sid]=null;
                   delete userSet[Sid];

		   }
		}
		public function RoleMove(Sid:String,dx:int,dy:int):void{
			var human1:Foeman;
		  if (userSet.propertyIsEnumerable(Sid)==false)		
			{
				human1=new Foeman(dx,dy,mapArray);
				human1.x=(isoW/2)*(dx-dy);
				human1.y=(isoH/2)*(dx+dy);
				addChild(human1);
				userSet[Sid]=human1;
		 }
		 else {
			human1=userSet[Sid];
			var aX:uint=human1.nowXindex;
			var aY:uint=human1.nowYindex;
            var walkEvent:WalkEvent=new WalkEvent();
			walkEvent.walkArray=SearchRoad.startSearch(mapArray,dx,dy,aX,aY);
			addEventListener(WalkEvent.WALK_START,human1.Rolewalking);
			dispatchEvent(walkEvent);
			removeEventListener(WalkEvent.WALK_START,human1.Rolewalking);   
			 
		 }
	    }


		private function write(str:String):void{
			//msgText.text +=str+"\n";
			trace(str);
		}
	}
}