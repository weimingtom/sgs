package hudo.net
{
	/**
	 * Handlers interface.
	 * 
	 * @version	0.0.1
	 * 
	 * @author	The sohudo Team
	 * 			{@link http://www.sohudo.com}
	 * 
	 * @exclude
	 */
	public interface IMessageHandler
	{
		function handleMessage(msgarr:SgsByteArray, type:int):void
		//function handleMessage(msgObj:Object, type:int):void
	}
}