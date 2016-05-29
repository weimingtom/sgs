package com.gamalocus.sgs.services.mysql;

import java.util.Map.Entry;


import com.gamalocus.sgs.datastructures.SGSLinkedList;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.util.ScalableHashMap;
import com.sun.sgs.service.DataService;
import com.sun.sgs.service.Transaction;

/**
 * This Runnable will just consume MySQL statements in the queues, posting them
 * to the database.
 * 
 * Well, currently this cannot be done, since we cannot access the DataStore: Argh!
 * 
 * @author Emanuel Greisen
 */
public class MySQLServiceThread implements Runnable
{
	private MySQLService service;

	public MySQLServiceThread(MySQLService service)
	{
		this.service = service;
	}

	@SuppressWarnings("unchecked")
	@Deprecated
	public void run()
	{
		while(service.isRunning())
		{
			// TODO: Create a transaction
			//Transaction trans = createTransaction();
			
			// Get the queues
			ScalableHashMap<String, ManagedReference<SGSLinkedList<MySQLQuery>>> queue_map = MySQLManager.getQueueMap();
			for(Entry<String, ManagedReference<SGSLinkedList<MySQLQuery>>> queue_entry : queue_map.entrySet())
			{
				System.out.println("Handling queue: "+queue_entry.getKey());
				SGSLinkedList<MySQLQuery> queue = queue_entry.getValue().get();
				MySQLQuery query;
				while((query = queue.removeFront()) != null)
				{
					System.out.println("Executing: "+query);
				}
			}
			
			// TODO: Commit the transaction
		}
	}
}
