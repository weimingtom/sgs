package com.gamalocus.sgs.services.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mysql.jdbc.Statement;
import com.sun.sgs.impl.service.data.DataServiceImpl;
import com.sun.sgs.impl.sharedutil.PropertiesWrapper;
import com.sun.sgs.kernel.ComponentRegistry;
import com.sun.sgs.service.DataService;
import com.sun.sgs.service.Service;
import com.sun.sgs.service.TransactionProxy;

/**
 * The MySQLService.
 * 
 * @author Emanuel Greisen
 * 
 */
public class MySQLService implements Service
{
	/** The name of this class. */
	private static final String CLASSNAME = MySQLService.class.getName();
	/** the logger. */
	private final static Logger logger = Logger.getLogger(MySQLService.class.getName());
	/** The configuration key for the host. */
	private static final String DBHOST_PROP = CLASSNAME + ".dbhost";
	/** The host running the MySQL server. */
	private String dbhost;
	/** The configuration key for the database name. */
	private static final String DBNAME_PROP = CLASSNAME + ".dbname";
	/** The database name. */
	private String dbname;
	/** The configuration key for the user. */
	private static final String DBUSER_PROP = CLASSNAME + ".dbuser";
	/** The user name to connect with. */
	private String dbuser;
	/** The configuration key for the pass. */
	private static final String DBPASS_PROP = CLASSNAME + ".dbpass";
	/** The password to connect with. */
	private String dbpass;
	
	/** Indicates if the game is still running - used by our worker thread to stop. */
	private boolean running = false;

	
    // a proxy providing access to the transaction state
    static TransactionProxy transactionProxy = null;

    // the data service used in the same context
    static DataService dataService;

	/**
	 * The constructor as it is called from SGS.
	 * 
	 * @param properties
	 * @param componentRegistry
	 * @param transProxy
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public MySQLService(Properties properties, ComponentRegistry componentRegistry, TransactionProxy transProxy) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		// Init the driver
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		
		// Read properties
		PropertiesWrapper wrappedProps = new PropertiesWrapper(properties);
		dbhost = wrappedProps.getProperty(DBHOST_PROP, "localhost");
		dbname = wrappedProps.getProperty(DBNAME_PROP, "sgs-server");
		dbuser = wrappedProps.getProperty(DBUSER_PROP, "sgs-user");
		dbpass = wrappedProps.getProperty(DBPASS_PROP, ":-)");
		
		// Get the ResourceCoordinator
		transactionProxy = transProxy;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName()
	{
		return toString();
	}

	@Override
	public String toString()
	{
		return "MySQLService[host:" + dbhost + ",dbname:" + dbname + ",dbuser:" + dbuser + ",dbpass:***]";
	}

	/**
	 * Create our worker thread.
	 */
	public void ready() throws Exception
	{
		System.out.println("MySQLService is ready");
		running = true;
		//This does not work, due to transaction: resource_coordinator.startTask(new MySQLServiceThread(this), this);
	}

	/**
	 * Here we terminate our worker thread, and close our mysql connection.
	 */
	public void shutdown()
	{
		running = false;
	}

	public boolean isRunning()
	{
		return running;
	}
	
	MySQLConnection getConnection()
	{
		try
		{
			// check with db
			Connection connection = DriverManager.getConnection("jdbc:mysql://" + dbhost + "/" + dbname + "?user=" + dbuser + "&password=" + dbpass);
			return new MySQLConnection((com.mysql.jdbc.Connection) connection);
		}
		catch (Exception e)
		{
			logger.log(Level.WARNING, "Could not establish connection with "+toString(), e);
		}
		return null;
	}

	/**
	 * that it is possible to read/write from
	 * @return
	 */
	public boolean testConnection()
	{
		MySQLConnection con = getConnection();
		if(con == null)
		{
			return false;
		}
		// Create a statement
		try
		{
			Statement stmt = (Statement) con.getConnection().createStatement();
			
			// Test the reading of sgs_queries
			stmt.executeQuery("SELECT uuid FROM sgs_queries LIMIT 1");
			
			// Test writing to sgs_queries
			UUID uuid = UUID.randomUUID();
			stmt.executeUpdate("INSERT INTO sgs_queries SET uuid='"+uuid+"', `date`=NOW()");
		}
		catch(SQLException e)
		{
			logger.log(Level.WARNING, "Could not select or insert into sgs_queries", e);
			return false;
		}
		
		return true;
	}
}
