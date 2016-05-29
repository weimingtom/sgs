package com.gamalocus.sgs.services.mysql;

import java.io.Serializable;
import java.sql.SQLException;


import com.gamalocus.sgs.datastructures.SGSLinkedList;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;

/**
 * This task takes care of executing one SQL statement at each run, on the given queue.
 * @author emanuel
 *
 */
public class MySQLQueryTask implements Task, Serializable
{
	private static final long serialVersionUID = 1009869754389444129L;
	private final ManagedReference<SGSLinkedList<MySQLQuery>> queue_ref;
	
	MySQLQueryTask(SGSLinkedList<MySQLQuery> queue)
	{
		this.queue_ref = AppContext.getDataManager().createReference(queue);
	}
	
	@SuppressWarnings("unchecked")
	public void run() throws Exception
	{
		SGSLinkedList<MySQLQuery> queue = queue_ref.get();
		//System.out.println("Task for queue: "+queue);
		MySQLQuery query = null;
		while((query = queue.removeFront()) != null)
		{
			//System.out.println("Executing["+query.uuid+"]: "+query.query);
			try
			{
				AppContext.getManager(MySQLManager.class).executeUpdate(query);
			}
			catch(SQLException sqlex)
			{
				// This is bad, but not that bad, we just try again later.
				System.err.println("SQL was: "+query.query);
				sqlex.printStackTrace();
				throw new RetryableSQLException(sqlex);
			}
		}
	}
}
