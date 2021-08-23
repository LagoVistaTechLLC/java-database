/*

	Copyright (C) 2021 Lago Vista Technologies LLC

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
	
*/
package com.lagovistatech.database;

import com.lagovistatech.Factory;

/**
 * This represents the center point of the database system pulling together the
 * database specific adapter to load tables, and save the rows contained in 
 * those tables. 
 */
public interface Connection {
	/**
	 * @return Server name/network address to connect to.
	 */
	String getServer();
	/**
	 * @param server Server name/network address to connect to.
	 */
	void setServer(String server);

	/**
	 * @return Alternate port to connect to.
	 */
	int getPort();
	/**
	 * @param port Alternate port to connect to.
	 */
	void setPort(int port);

	/**
	 * @return Database name to connect to.
	 */
	String getDatabase();
	/**
	 * @param database Database name to connect to.
	 */
	void setDatabase(String database);

	/**
	 * @return User name for authenticating connection.
	 */
	String getUser();
	/**
	 * @param user User name for authenticating connection.
	 */
	void setUser(String user);

	/**
	 * @return Timeout for connection and SQL queries.
	 */
	int getTimeOut();
	/**
	 * @param timeOut Timeout for connection and SQL queries.
	 */
	void setTimeOut(int timeOut);

	/**
	 * @param password Password used to authenticate connection.
	 */
	void setPassword(String password);

	/**
	 * @return Database specific adapter used for this connection.
	 */
	Adapter getAdapter();
	/**
	 * @param value Database specific adapter used for this connection.
	 */
	void setAdapter(Adapter value);
	
	/**
	 * Opens the connection using the properties provided.
	 * 
	 * @throws Exception Errors encountered or returned by server.
	 */
	void open() throws Exception;
	/**
	 * Close the connection and release variables.
	 * 
	 * @throws Exception Errors encountered.
	 */
	void close() throws Exception;
	
	/**
	 * Executes the provided SQL query substituting parameters with values for
	 * queries that should not return a result set.
	 * 
	 * @param sql SQL query to execute.
	 * @param params Parameter to be substituted with the literal values
	 * @return The number of rows modified
	 * @throws Exception Error encountered.
	 */
	long execute(String sql, Parameters params) throws Exception;
	/**
	 * Executes the provided SQL query substituting parameters with values for
	 * queries that should not return a result set.
	 * 
	 * @param sql SQL query to execute.
	 * @return The number of rows modified
	 * @throws Exception Error encountered.
	 */
	long execute(String sql) throws Exception;
	/**
	 * Executes the provided SQL query substituting parameters with values for 
	 * queries that should return results.  The results will be used to fill a
	 * target table defined to take generic rows of type R.
	 * 
	 * @param <R> The type of the rows.
	 * @param rowFactory Factory to create rows to populate the table.
	 * @param sql SQL query to execute.
	 * @param params Parameter to be substituted with the literal values
	 * @return The table that was filled.
	 * @throws Exception Error encountered.
	 */
	<R extends Row> Table<R> fill(Factory<R> rowFactory, String sql, Parameters params) throws Exception;
	/**
	 * Executes the provided SQL query substituting parameters with values for 
	 * queries that should return results.  The results will be used to fill a
	 * target table defined to take generic rows of type R.
	 * 
	 * @param <R> The type of the rows.
	 * @param rowFactory Factory to create rows to populate the table.
	 * @param sql SQL query to execute.
	 * @return The table that was filled.
	 * @throws Exception Error encountered.
	 */
	<R extends Row> Table<R> fill(Factory<R> rowFactory, String sql) throws Exception;
	/**
	 * Save the table's rows by either inserting, updating, or deleting the rows
	 * from the database.
	 * 
	 * @param <R> The type of the rows.
	 * @param table Table with rows to operate on.
	 * @return The number of rows modified.
	 * @throws Exception Error encountered.
	 */
	<R extends Row> long save(Table<R> table) throws Exception;
	
	/**
	 * Starts a SQL transaction.
	 * 
	 * @throws Exception Error encountered.
	 */
	void begin() throws Exception;
	/**
	 * Commits the SQL transaction.
	 * 
	 * @throws Exception Error encountered.
	 */
	void commit() throws Exception;
	/**
	 * Rolls back the SQL transaction.
	 * 
	 * @throws Exception Error encountered.
	 */
	void rollback() throws Exception;
}
