package com.gamalocus.sgs.services.mysql;

import java.sql.SQLException;

import com.sun.sgs.app.ExceptionRetryStatus;

public class RetryableSQLException extends Exception implements ExceptionRetryStatus
{
	private static final long serialVersionUID = -1813392302930937554L;
	
	RetryableSQLException(SQLException cause)
	{
		super(cause);
	}

	public boolean shouldRetry()
	{
		return true;
	}
}
