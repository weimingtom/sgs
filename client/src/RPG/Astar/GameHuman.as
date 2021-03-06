﻿/*
=================================================================================================
人物类，功能分为以下几个方面：
1，函数walking 根据传入的路径数据，到达目的地。

=================================================================================================
*/
package RPG.Astar{
	import flash.display.Sprite;
	import flash.display.MovieClip;
	import flash.utils.getDefinitionByName;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.filters.GlowFilter;

	public class GameHuman extends Sprite {
		private var Human:Class;
		private var human:MovieClip;
		private var walkArray:Array;
		private var changeMapArray:Array;
		private var directArray:Array;
		private var nextArray:Array;
		private var oldArray:Array;
		private var step:uint;
		private var flag:uint;
		private var nowX:uint;
		private var nowY:uint;
		private var dirX:int;
		private var dirY:int;
		private var direct:String;
		private var isWalk:Boolean;
		private var walkEnd:Boolean;
		private var isNewWalk:Boolean;

                private var filter:GlowFilter;

		public function GameHuman(nx:uint,ny:uint,id:uint) {
			nowX=nx;
			nowY=ny;
			changeMapArray=MapData.MAP_PASSAGEWAY[id];
			directArray=MapData.DIRECT_ARRAY;
			nextArray=MapData.NEXT_ARRAY;
			init();
			initFilter();
                        this.addEventListener(MouseEvent.MOUSE_OVER,mouseOverHandler);
                        this.addEventListener(MouseEvent.MOUSE_OUT,mouseOutHandler);
		}
                private function initFilter():void{
                        this.filter = new GlowFilter(0xffffff);
                }
                private function mouseOverHandler(e:MouseEvent):void{
                        this.filters = [this.filter];
                }
                private function mouseOutHandler(e:MouseEvent):void{
                        this.filters = null;
                }

		private function init():void {
			Human=getDefinitionByName("Human")  as  Class;
			human=new Human();
			//human.stop();
			isNewWalk=false;
			walkEnd=true;
			step=1;
			flag=0;
			isWalk=true;
			//trace(MapData.DIRECT_ARRAY[changeMapArray[0][3]]+"Stop 111");
			human.gotoAndStop(MapData.DIRECT_ARRAY[changeMapArray[0][3]]+"Stop");
			addChild(human);
		}
		public function walking(evt:WalkEvent):void {
			if (!walkEnd&&flag!=0) {
				isNewWalk=true;
				oldArray=walkArray[step];
			} else {
				flag=0;
			}
			step=1;
			isWalk=true;
			walkArray=evt.walkArray;
			addEventListener(Event.ENTER_FRAME,startMove);
		}
		public function get nowXindex():uint {
			return nowX;
		}
		public function get nowYindex():uint {
			return nowY;
		}
		private function startMove(evt:Event):void {
			if (isNewWalk) {
				if (walkArray[1][0]==oldArray[0]&&walkArray[1][1]==oldArray[1]) {
					subWalk();
				} else {
					sub(-1);
					flag--;
					walkEnd=false;
					if (flag==5) {
						parent.setChildIndex(this,parent.getChildIndex(parent.getChildByName("element" + nowX + "_" + nowY)));
					} else if (flag == 0) {
						this.x=34*(nowX-nowY);
						this.y=17*(nowX+nowY);
						isWalk=true;
						isNewWalk=false;
					}
				}
			} else {
				subWalk();
			}
		}
		//========================================================================================
		private function subWalk():void {
			dirX=walkArray[step][0] - nowX;
			dirY=walkArray[step][1] - nowY;
			sub(1);
			flag++;
			walkEnd=false;
			if (flag==5) {
				//重新计算深度
				parent.setChildIndex(this,parent.getChildIndex(parent.getChildByName("element" + walkArray[step][0] + "_" + walkArray[step][1])));
			} else if (flag == 10) {
				nowX=walkArray[step][0];
				nowY=walkArray[step][1];
				this.x=34*(nowX-nowY);
				this.y=17*(nowX+nowY);
				step++;
				flag=0;
				isWalk=true;
				isNewWalk=false;
			}
			if (step == walkArray.length) {
			       // trace(direct+"Stop 22");
				human.gotoAndStop(direct+"Stop");
				walkEnd=true;//本次行走结束
				changeMap();//检测是否需要更换地图
				this.removeEventListener(Event.ENTER_FRAME,startMove);
			}
		}
		private function sub(sign:int):void {
			this.x+= sign*3.4 * (dirX - dirY);
			this.y+=  sign*1.7 * (dirX + dirY);
			parent.x-=  sign*3.4 * (dirX - dirY);
			parent.y-=  sign*1.7 * (dirX + dirY);
			if (isWalk) {
				isWalk=false;
				for (var i:String in nextArray) {
					if (nextArray[i][0]==sign*dirX&&nextArray[i][1]==sign*dirY) {
						direct=directArray[i];
						//trace(direct+" 33");
						human.gotoAndPlay(direct);
						break;
					}
				}
			}
		}
		//====================================================================================================
		private function changeMap():void {
			for (var i:String in changeMapArray) {
				if (nowX==changeMapArray[i][0]&&nowY==changeMapArray[i][1]) {
					var changeMapEvent:ChangeMapEvent=new ChangeMapEvent();
					changeMapEvent.nextMapId=changeMapArray[i][2];
					dispatchEvent(changeMapEvent);
					break;
				}
			}
		}
	}
}