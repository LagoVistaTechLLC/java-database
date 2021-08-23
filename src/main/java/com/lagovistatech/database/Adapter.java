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

import java.sql.ResultSet;
import java.util.Map;
import java.util.UUID;

import com.lagovistatech.Guid;


/**
 * The adapter is a database specific module that translate the connection's
 * operations into specific SQL queries for use on the target database system. 
 */
public interface Adapter {
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
	 * Converts a native Java/SQL data type to a valid SQL literal for that data
	 * type as defined by the specific SQL server.
	 * 
	 * @param value Value to be converted to literal.
	 * @return SQL literal string for value.
	 * @throws Exception Any errors.
	 */
	String objectToSql(Object value) throws Exception;
	
	/**
	 * Executes the provides SQL query verbatim.  This query should not return
	 * and data nor results.
	 * 
	 * @param sql Query to execute.
	 * @return Number or rows modified.
	 * @throws Exception Error encountered.
	 */
	long execute(String sql) throws Exception;
	/**
	 * Executes the provided SQL query with the expectation that data or 
	 * results will be returned.
	 * 
	 * @param sql Query to execute.
	 * @return Result of the query.
	 * @throws Exception Error encountered.
	 */
	ResultSet fill(String sql) throws Exception;
	
	/**
	 * Take a literal string and provides a safe SQL literal identifier quoted
	 * string.
	 * 
	 * @param identifier Identifier to quote.
	 * @return Quoted version of literal identifier.
	 * @throws Exception Error encountered.
	 */
	String quoteIdentifier(String identifier) throws Exception;
	/**
	 * Take a literal value string and provide a safe SQL quoted value string.
	 * 
	 * @param literal Literal to quote.
	 * @return Quoted literal SQL value.
	 * @throws Exception Error encountered.
	 */
	String quoteLiteral(String literal) throws Exception;
	
	/**
	 * Generate insert, update, and delete SQL query for stateful row represented
	 * by original and changed values.
	 * 
	 * @param table Table the row belongs to
	 * @param originals Map of original row values
	 * @param changes Map of changed row values
	 * @param isDel If true, the row is to be deleted
	 * @return SQL statement to update row in database
	 * @throws Exception
	 */
	String generateSave(Table<?> table, Map<String, Object> originals, Map<String, Object> changes, boolean isDel) throws Exception;
	/**
	 * Generate insert, update, and delete SQL query for versioned row represented
	 * by original and changed values.
	 * 
	 * @param table Table the row belongs to
	 * @param values Map of row values
	 * @param isDel If true, the row is to be deleted
	 * @return SQL statement to update row in database
	 * @throws Exception
	 */
	String generateSave(Table<?> table, Map<String, Object> values, boolean isDel) throws Exception;
	
	/**
	 * @return Random (version 4) GUID/UUID stored in 6 bytes.
	 * @throws Exception
	 */
	public static byte[] createGuidBytes() { return Guid.computeBytes(); }
	public static UUID createGuidUuid() { return Guid.computeUuid(); }
}
