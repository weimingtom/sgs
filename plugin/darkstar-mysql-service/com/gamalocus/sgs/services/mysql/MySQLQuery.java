package com.gamalocus.sgs.services.mysql;

import java.io.Serializable;
import java.util.UUID;

/**
 * This class contains an SQL-query and a UUID to ensure that the SQL is not
 * executed twice.
 * 
 * @author emanuel
 */
class MySQLQuery implements Serializable
{
	private static final long serialVersionUID = -9136184076684212456L;
	String query;
	UUID uuid;

	public MySQLQuery(String query)
	{
		this.query = query;
		this.uuid = UUID.randomUUID();
	}
}