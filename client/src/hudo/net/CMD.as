package hudo.net{
/**
* @author sohudo@qq.com
* @datas 2008-09-03
* @version 0.0.1

* 通信命令
 */
public final class CMD{
  public static const SPLIT:String = "%";//参数分隔符
  public static const SPLITD:String = ",";//同参数属性分隔符
  public static const SPLITL:String = ";";//同参数列表分隔符 


  public static const SV_pubMsg:int  = 1003;//聊天
  public static const SV_prvMsg:int  = 1004;//私聊
  
  public static const UseralLogin:int    = 0x09;//
  public static const Room_Users:int     = 0x10;
  public static const Room_Change:int    = 0x11;//进入新频道
   
  public static function GetCMDToStr(cmd:int):String
  {
   switch ( cmd ) {

 //基本命令 
  case SV_pubMsg   :return  "聊天";
  case SV_prvMsg   :return  "私聊";  

  case UseralLogin :return  "此用户已经登陆";  
  case Room_Users  :return  "用户列表";
  case Room_Change :return  "进入新频道";
       default     : return "未知命令";
   
   }
  }
	}
}