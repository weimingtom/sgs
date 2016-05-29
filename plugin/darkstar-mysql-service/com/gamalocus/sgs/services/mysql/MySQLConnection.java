package com.gamalocus.sgs.services.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

/**
 * This is just a wrapper for a connection to MySQL, making things convenient.
 * 
 * @author Emanuel Greisen
 *
 */
public class MySQLConnection
{
	private Connection connection;

	MySQLConnection(Connection connection)
	{
		this.connection = connection;
	}

	/**
	 * Execute the UPDATE/INSERT/REPLACE if it has not been done before, or return false if it has already been executed.
	 *  
	 * @param query
	 * @return
	 * @throws SQLException 
	 */
	public boolean executeUpdate(MySQLQuery query) throws SQLException
	{
		// Create a statement
		Statement stmt = (Statement) connection.createStatement();
		
		// Start a transaction (in MYSQL)
		stmt.execute("start transaction");
		
		// Check that the query has not been executed already
		if(!stmt.executeQuery("SELECT uuid FROM sgs_queries WHERE uuid LIKE '"+query.uuid+"'").next())
		{
			// Execute the query
			stmt.executeUpdate(query.query);
			
			// Update the executed-state (write it in a table in MySQL)
			stmt.executeUpdate("INSERT INTO sgs_queries SET uuid='"+query.uuid+"', `date`=NOW()");
			
			// Commit the MYSQL-transaction
			stmt.execute("commit");
			return true;
		}
		else
		{
			System.out.println("The query already been performed from somewhere else");
		}
		stmt.execute("rollback");
		return false;
	}

	
	/**
	 * Used the retrieve the ResultSet of a SELECT-statement.
	 * 
	 * @param sql
	 * @return the result
	 * @throws SQLException
	 */
	public ResultSet executeSelect(String sql) throws SQLException
	{
		Statement stmt = (Statement) connection.createStatement();
		return stmt.executeQuery(sql);
	}
	
	Connection getConnection()
	{
		return connection;
	}
}
