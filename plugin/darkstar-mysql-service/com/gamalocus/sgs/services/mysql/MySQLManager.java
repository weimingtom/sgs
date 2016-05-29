package com.gamalocus.sgs.services.mysql;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


import com.gamalocus.sgs.datastructures.SGSLinkedList;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.NameNotBoundException;
import com.sun.sgs.app.util.ScalableHashMap;

/**
 * The manager for MySQL lazy synchronization.
 * 
 * @author Emanuel Greisen
 * 
 */
public class MySQLManager
{
	/**
	 * The binding name for the Queue list
	 */
	private static final String QUEUE_MAP_BINDING = MySQLManager.class.getName() + ".queue_map";

	// The period between queues being emptied.
	private static final long QUEUE_PERIOD = 30000;
	
	/**
	 * The service backing this manager.
	 */
	private MySQLService service;

	/**
	 * This is the constructor of the manager, called by SGS.
	 * 
	 * @param service
	 */
	public MySQLManager(MySQLService service)
	{
		this.service = service;
	}

	/**
	 * This will enqueue the SQL to be executed, remember that the actual
	 * execution occurs at a later point in time.
	 * 
	 * @param queue_name -
	 *            The name of the queue this SQL query should be placed in.
	 * @param query -
	 *            The actual SQL string.
	 */
	public void executeSQL(String queue_name, String query)
	{
		SGSLinkedList<MySQLQuery> queue = getQueue(queue_name);
		queue.add(new MySQLQuery(query));
		
		// TODO: here we might try to wake up the Service-thread
	}
	
	/**
	 * This will not cache anything, just execute the update returning true if it has been done before 
	 * @param query
	 * @throws SQLException 
	 */
	boolean executeUpdate(MySQLQuery query) throws SQLException
	{
		return service.getConnection().executeUpdate(query);		
	}

	static SGSLinkedList<MySQLQuery> getQueue(String queue_name)
	{
		ManagedReference<SGSLinkedList<MySQLQuery>> ref = getQueueMap().get(queue_name);
		if (ref != null)
		{
			return ref.get();
		}
		SGSLinkedList<MySQLQuery> queue = new SGSLinkedList<MySQLQuery>(true);
		getQueueMap().put(queue_name, AppContext.getDataManager().createReference(queue));
		AppContext.getTaskManager().schedulePeriodicTask(new MySQLQueryTask(queue), 100, QUEUE_PERIOD);
		return queue;
	}

	@SuppressWarnings("unchecked")
	static ScalableHashMap<String, ManagedReference<SGSLinkedList<MySQLQuery>>> getQueueMap()
	{
		ScalableHashMap<String, ManagedReference<SGSLinkedList<MySQLQuery>>> queue_map = null;
		try
		{
			queue_map = (ScalableHashMap<String, ManagedReference<SGSLinkedList<MySQLQuery>>>)AppContext.getDataManager().getBinding(QUEUE_MAP_BINDING);
		}
		catch (NameNotBoundException nnbe)
		{
			queue_map = new ScalableHashMap<String, ManagedReference<SGSLinkedList<MySQLQuery>>>();
			AppContext.getDataManager().setBinding(QUEUE_MAP_BINDING, queue_map);
		}
		return queue_map;
	}

	public ResultSet executeSelect(String sql) throws SQLException
	{
		return service.getConnection().executeSelect(sql);
	}

}

