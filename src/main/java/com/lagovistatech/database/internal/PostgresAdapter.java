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
package com.lagovistatech.database.internal;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;

import com.lagovistatech.Guid;
import com.lagovistatech.Helpers;
import com.lagovistatech.database.Adapter;
import com.lagovistatech.database.Table;
import com.lagovistatech.database.VersionedRow;

public class PostgresAdapter implements Adapter {
	private String server;
	public String getServer() { return server; }
	public void setServer(String server) { this.server = server; }

	private int port = 5432;
	public int getPort() { return port; }
	public void setPort(int port) { this.port = port; }

	private String database;
	public String getDatabase() { return database; }
	public void setDatabase(String database) { this.database = database; }

	private String user;
	public String getUser() { return user; }
	public void setUser(String user) { this.user = user; }

	private int timeOut = 30;
	public int getTimeOut() { return timeOut; }
	public void setTimeOut(int timeOut) { this.timeOut = timeOut; }

	private String password;
	public void setPassword(String password) { this.password = password; }

    public static final Map<String,String> sqlToJavaMap = Map.ofEntries(
    	Map.entry("int2", "java.lang.Integer"),
    	Map.entry("int4", "java.lang.Integer"),
    	Map.entry("int8", "java.lang.Long"),
    	Map.entry("smallserial", "java.lang.Integer"),
    	Map.entry("bigserial", "java.lang.Long"),

    	Map.entry("numeric", "java.math.BigDecimal"),
    	Map.entry("float4", "java.lang.Float"),
    	Map.entry("float8", "java.lang.Double"),
    	Map.entry("money", "java.lang.Double"),
    	
    	Map.entry("bpchar", "java.lang.String"),
    	Map.entry("varchar", "java.lang.String"),
    	Map.entry("text", "java.lang.String"),
    	
    	Map.entry("bytea", "byte[]"),
    	Map.entry("uuid", "java.util.UUID"),
    	
    	Map.entry("timestamp", "java.sql.Timestamp"),
    	Map.entry("date", "java.sql.Date"),
    	Map.entry("time", "java.sql.Time"),
    	Map.entry("bool", "java.lang.Boolean")
    );	
	
    public String objectToSql(Object value) throws Exception {
		if(value == null)
			return "NULL";
		
		SimpleDateFormat sdf;
		switch(value.getClass().getCanonicalName()) {
			case "java.util.UUID":
				String uuid = ((java.util.UUID) value).toString();
				return this.quoteLiteral(uuid);
			case "byte[]":
				String val = Hex.encodeHexString(Helpers.objectToBytes(value));
				return "DECODE('" + val + "', 'HEX')";
			case "java.lang.Boolean":
				return (Helpers.objectToBoolean(value) ? "TRUE" : "FALSE");
			case "java.lang.Integer":
			case "java.lang.Long":
				return value.toString();
			case "java.lang.Double":
			case "java.lang.Float":
			case "java.math.BigDecimal":
				return value.toString();
			case "java.util.Date":
				java.util.Date dt = (java.util.Date) value;
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
				String dtString = sdf.format(dt);
				return this.quoteLiteral(dtString);
			case "java.sql.Time":
				java.sql.Time tm = (java.sql.Time) value;
				sdf = new SimpleDateFormat("HH:mm:ss.SSS Z");
				String tmString =  sdf.format(tm);
				return this.quoteLiteral(tmString);
			case "java.sql.Date":
			case "java.sql.Timestamp":
				return this.quoteLiteral(value.toString().trim());
			case "java.lang.String":
				return this.quoteLiteral(value.toString().trim());
			default:
				throw new Exception("Type '" + value.getClass().getCanonicalName() + " is not supported!");
		}
	}

	private java.sql.Statement quoteStatement;
	private java.sql.Connection connection;
	public void open() throws Exception {
		if(server == null || server.length() < 1)
			throw new Exception("Server required for connection!");
		if(database == null || database.length() < 1)
			throw new Exception("Database required for connection!");
		if(user == null || user.length() < 1)
			throw new Exception("User required for connection!");
		if(password == null || password.length() < 1)
			throw new Exception("Password required for connection!");
		
		Class.forName("org.postgresql.Driver");
		String jdbc = "jdbc:postgresql://" + server + ":" + port + "/" + database;
		DriverManager.setLoginTimeout(timeOut);
		connection = DriverManager.getConnection(jdbc, user, password);	
		quoteStatement = connection.createStatement();
	}
	public void close() throws Exception {
		quoteStatement = null;
		try { connection.close(); }
		catch(Exception ex) { /* do nothing */ }
		connection = null;
	}

	public long execute(String processed) throws Exception {
		Statement stmt = connection.createStatement();
		stmt.setQueryTimeout(timeOut);
		return stmt.executeLargeUpdate(processed);
	}
	public ResultSet fill(String processed) throws Exception {
		Statement stmt = connection.createStatement();
		stmt.setQueryTimeout(timeOut);
		return stmt.executeQuery(processed);
	}

	public String quoteIdentifier(String identifier) throws Exception {
		if(connection == null)
			throw new Exception("The connection is not open!");
		
		return quoteStatement.enquoteIdentifier(identifier, true); 
	}
	public String quoteLiteral(String literal) throws Exception { 
		if(connection == null)
			throw new Exception("The connection is not open!");

		return quoteStatement.enquoteLiteral(literal); 
	}
	
	public String generateSave(Table<?> table, Map<String, Object> originals, Map<String, Object> changes, boolean isDel) throws Exception {
		boolean isNew = originals.size() < 1;
		boolean isMod = changes.size() > 0;
		
		//	New	Mod	Del
		//	F	F	T	Delete
		//	F	T	T	Delete
		//	F	T	F	Update
		//	T	T	F	Insert
		//	F	F	F	Nothing
		//	T	F	T	Nothing
		//	T	F	F	Nothing
		//	T	T	T	Nothing
		
		if(!isNew && isDel)
			return generateDelete(table, originals, changes);
		else if(!isNew && isMod && !isDel)
			return generateUpdate(table, originals, changes);
		else if(isNew && isMod && !isDel)
			return generateInsert(table, originals, changes);
		else
			return null;
	}
	private String generateInsert(Table<?> table, Map<String, Object> originals, Map<String, Object> changes) throws Exception {
		if(originals.size() > 0)
			throw new Exception("Cannot insert a that has original values!");
		if(changes.size() < 1)
			throw new Exception("Cannot insert a row without changes!");

		String sql = "INSERT INTO " + this.quoteIdentifier(table.getTableName()) + " (%COLUMNS%) VALUES (%VALUES%);\n";

		String columnsSql = "";
		String valuesSql = "";
		for(String key : changes.keySet()) {
			if(columnsSql.length() > 1) {
				columnsSql += ", ";
				valuesSql += ", ";
			}
			
			columnsSql += this.quoteIdentifier(key);
			valuesSql += this.objectToSql(changes.get(key));
		}
		
		sql = sql.replace("%COLUMNS%", columnsSql).replace("%VALUES%", valuesSql);
		
		return sql;		
	}
	private String generateUpdate(Table<?> table, Map<String, Object> originals, Map<String, Object> changes) throws Exception {
		if(originals.size() < 1)
			throw new Exception("Cannot update a new row!");
		if(changes.size() < 1)
			throw new Exception("Cannot update a row without changes!");
		
		String sql = "UPDATE " + this.quoteIdentifier(table.getTableName()) + " SET ";

		
		String changesSql = "";
		for(String key : changes.keySet()) {
			Object value = changes.get(key);
			if(changesSql.length() > 0)
				changesSql += ", ";
			changesSql += this.quoteIdentifier(key) + "=" + this.objectToSql(value);
		}
		sql += changesSql;
		sql += " WHERE CTID IN ( ";
		
		sql += "SELECT CTID FROM " + this.quoteIdentifier(table.getTableName());		
		sql += " WHERE 1=1";
		for(String key : originals.keySet()) {
			Object value = originals.get(key);
			if(value == null)
				sql += " AND " + this.quoteIdentifier(key) + " IS NULL";
			else {
				switch(table.getSchema().get(key)) {
					case "numeric":
					case "float4":
					case "float8":
					case "money":
						sql += " AND " + this.quoteIdentifier(key) + "::NUMERIC=" + this.objectToSql(value) + "::NUMERIC";
						break;
					default:
						sql += " AND " + this.quoteIdentifier(key) + "=" + this.objectToSql(value);
				}
			}
		}
		
		return sql + " LIMIT 1);\n";		
	}
	private String generateDelete(Table<?> table, Map<String, Object> originals, Map<String, Object> changes) throws Exception {
		if(originals.size() < 1)
			throw new Exception("Cannot delete a new row!");
		
		String sql = "DELETE FROM " + this.quoteIdentifier(table.getTableName()) + " WHERE CTID IN ( ";

		sql += "SELECT CTID FROM " + this.quoteIdentifier(table.getTableName()) + " WHERE 1=1 ";
		for(String key : originals.keySet()) {
			Object value = originals.get(key);
			if(value == null)
				sql += " AND " + this.quoteIdentifier(key) + " IS NULL";
			else {
				switch(table.getSchema().get(key)) {
					case "numeric":
					case "float4":
					case "float8":
					case "money":
						sql += " AND " + this.quoteIdentifier(key) + "::NUMERIC=" + this.objectToSql(value) + "::NUMERIC";
						break;
					default:
						sql += " AND " + this.quoteIdentifier(key) + "=" + this.objectToSql(value);
				}
			}
		}
		
		return sql + " LIMIT 1 );\n";
	}

	public String generateSave(Table<?> table, Map<String, Object> values, boolean isDel) throws Exception {
		boolean isMod = values.size() > 0;
		
		if(!values.containsKey(VersionedRow.COLUMN_GUID) || values.get(VersionedRow.COLUMN_GUID) == null)
			values.put(VersionedRow.COLUMN_GUID, Guid.computeUuid());
		if(!values.containsKey(VersionedRow.COLUMN_VERSION) || values.get(VersionedRow.COLUMN_VERSION) == null)
			values.put(VersionedRow.COLUMN_VERSION, 0);
				
		boolean isNew = Helpers.objectToLong(values.get(VersionedRow.COLUMN_VERSION)) < 1; 
					
		//	New	Mod	Del
		//	F	F	T	Delete
		//	F	T	T	Delete
		//	F	T	F	Update
		//	T	T	F	Insert
		//	F	F	F	Nothing
		//	T	F	T	Nothing
		//	T	F	F	Nothing
		//	T	T	T	Nothing
		if(!isNew && isDel)
			return generateDelete(table, values);
		else if(!isNew && isMod && !isDel)
			return generateUpdate(table, values);
		else if(isNew && isMod && !isDel)
			return generateInsert(table, values);
		else
			return null;
	}
	private String generateInsert(Table<?> table, Map<String, Object> values) throws Exception {
		if(values.size() < 0)
			throw new Exception("Cannot insert a that has no values!");

		String sql = "INSERT INTO " + this.quoteIdentifier(table.getTableName()) + " (%COLUMNS%) VALUES (%VALUES%);\n";

		String columnsSql = "";
		String valuesSql = "";
		for(String key : values.keySet()) {
			if(key.equals(VersionedRow.COLUMN_GUID) || key.equals(VersionedRow.COLUMN_VERSION))
				continue;

			if(columnsSql.length() > 1) {
				columnsSql += ", ";
				valuesSql += ", ";
			}			
			columnsSql += this.quoteIdentifier(key);
			valuesSql += this.objectToSql(values.get(key));
		}

		if(columnsSql.length() > 1) {
			columnsSql += ", ";
			valuesSql += ", ";
		}			
		columnsSql += this.quoteIdentifier(VersionedRow.COLUMN_GUID);
		valuesSql += this.objectToSql(values.get(VersionedRow.COLUMN_GUID));
		columnsSql += ", " + this.quoteIdentifier(VersionedRow.COLUMN_VERSION);
		valuesSql += ", " + this.objectToSql(1);
			
		sql = sql.replace("%COLUMNS%", columnsSql).replace("%VALUES%", valuesSql);
		
		return sql;		
	}
	private String generateUpdate(Table<?> table, Map<String, Object> values) throws Exception {
		if(values.size() < 1)
			throw new Exception("Cannot update a row without values!");

		String sql = "UPDATE " + this.quoteIdentifier(table.getTableName()) + " SET ";
		String valuesSql = "";
		for(String key : values.keySet()) {
			if(key.equals(VersionedRow.COLUMN_GUID) || key.equals(VersionedRow.COLUMN_VERSION))
				continue;
			
			Object value = values.get(key);
			if(valuesSql.length() > 0)
				valuesSql += ", ";
			valuesSql += this.quoteIdentifier(key) + "=" + this.objectToSql(value);
		}
		
		if(valuesSql.length() > 0)
			valuesSql += ", ";
		
		valuesSql += this.quoteIdentifier(VersionedRow.COLUMN_VERSION) + "=" 
			+ this.objectToSql(Helpers.objectToLong(values.get(VersionedRow.COLUMN_VERSION)) + 1);
		
		
		sql += valuesSql + " WHERE ";
		sql += this.quoteIdentifier(VersionedRow.COLUMN_VERSION) + "=" + this.objectToSql(values.get(VersionedRow.COLUMN_VERSION));
		sql += " AND " + this.quoteIdentifier(VersionedRow.COLUMN_GUID) + "=" + this.objectToSql(values.get(VersionedRow.COLUMN_GUID));
		
		return sql + ";\n";		
	}
	private String generateDelete(Table<?> table, Map<String, Object> values) throws Exception {
		if(Helpers.objectToLong(values.get(VersionedRow.COLUMN_VERSION)) == 0)
			throw new Exception("Cannot delete a new row!");
		
		String sql = "DELETE FROM " + this.quoteIdentifier(table.getTableName()) + " WHERE ";
		sql += this.quoteIdentifier(VersionedRow.COLUMN_GUID) + "=" + this.objectToSql(values.get(VersionedRow.COLUMN_GUID));
		sql += " AND " + this.quoteIdentifier(VersionedRow.COLUMN_VERSION) + "=" + this.objectToSql(values.get(VersionedRow.COLUMN_VERSION));
		
		return sql + ";\n";
	}
}
