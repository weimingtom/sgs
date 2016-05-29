package hudo.net
{
	import flash.events.Event;
/**
* @author sohudo@qq.com
* @datas 2008-09-03
* @version 0.0.1

 */	
	
	
	public class HDEvent extends Event
	{
		// Public event type constants ...		
	
                public static const onDebugMessage:String = "onDebugMessage"
        

		public static const onConfigLoadFailure:String = "onConfigLoadFailure"
		public static const onConfigLoadSuccess:String = "onConfigLoadSuccess"
		
		public static const onConnection:String = "onConnection"
		public static const onConnectionLost:String = "onConnectionLost"	
		
        

		public static const onLogin:String = "onLogin"
		public static const onLoginRedirect:String  ="onLoginRedirect"
		public static const onLogout:String = "onLogout"
	

		public static const onPrivateMessage:String = "onPrivateMessage"
		public static const onPublicMessage:String = "onPublicMessage"		

                public static const onSocketError:String = "onSocketError"
		
		public static const OnRoleLeav:String          = "onRoleLeav"
		public static const OnRoleJoin:String          = "OnRoleJoin"
		public static const OnRoleMove:String          = "OnRoleMove"
		public static const OnRoleMovePath:String      = "OnRoleMovePath"
		public static const OnRoleMovePath2:String     = "OnRoleMovePath2"
		
		public static const OnChannelJoin:String       = "OnChannelJoin"
		public static const OnChannelLeave:String      = "OnChannelLeave"
		public static const OnObject:String            = "OnObject"
		public static const OnChannelUsers:String      = "OnChannelUsers"
		//--- END OF CONSTANTS -----------------------------------------------------------------------------
		
		
		/**
		 * An object containing all the parameters related to the dispatched event.
		 * See the class constants for details on the specific parameters contained in this object.
		 */		
		public var params:Object
		
		/**
		 * HDEvent contructor.
		 * 
		 * @param	type:	the event's type (see the constants in this class).
		 * @param	params:	the parameters object for the event.
		 * 
		 * @see		#params
		 * 
		 * @exclude
		 */
		public function HDEvent(type:String, params:Object)
		{
			super(type)
			this.params = params
		}
		
		/**
		 * Get a copy of the current instance.
		 * 
		 * @return		a copy of the current instance.
		 * 
		 * @overrides	Event#clone
		 * 
		 * @version	HudoServer Basic / Pro
		 */
		public override function clone():Event
		{
			return new HDEvent(this.type, this.params)
		}
		
		
		/**
		 * Get a string containing all the properties of the current instance.
		 * 
		 * @return		a string representation of the current instance.
		 * 
		 * @overrides	Event#toString
		 * 
		 * @version	HudoServer Basic / Pro
		 */
		public override function toString():String
		{
			return formatToString("HDEvent", "type", "bubbles", "cancelable", "eventPhase", "params")
		}
	}
}