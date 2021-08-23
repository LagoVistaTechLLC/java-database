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

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import com.lagovistatech.Helpers;
import com.lagovistatech.database.internal.PostgresAdapter;

public class ConnectionTest {
	private Connection createConnection() {
		Connection connection = ConnectionFactory.instanciate();
		connection.setServer("localhost");
		connection.setPort(54320);
		connection.setDatabase("lvtdbtest");
		connection.setUser("postgres");
		connection.setPassword("Welcome123");
		return connection;
	}
	private String createVersionedTable(Connection connection) throws Exception {
		String tableName = "Table " + UUID.randomUUID().toString().toLowerCase().replace("-", "");

		String sql = Files.readString(Paths.get("src", "test", "resources", "versioned-table.sql"));
		sql = sql.replace("%TABLE%", tableName);
		connection.execute(sql);		
		
		return tableName;
	}
	private String createStatefulTable(Connection connection) throws Exception {
		String tableName = "Table " + UUID.randomUUID().toString().toLowerCase().replace("-", "");

		String sql = Files.readString(Paths.get("src", "test", "resources", "stateful-table.sql"));
		sql = sql.replace("%TABLE%", tableName);
		connection.execute(sql);		
		
		return tableName;
	}
	private void deleteTable(Connection connection, String tableName) throws Exception {
		connection.execute("DROP TABLE \"" + tableName + "\"", null);
	}
	
	@Test
	public void Open_Success() throws Exception {
		Connection connection = createConnection();
		try {
			connection.open();
		} 
		finally {
			connection.close();
		}
		
		
		assertTrue(true); 
	}
	@Test
	public void Open_BadHost() throws Exception {
		Connection connection = createConnection();
		connection.setServer("invalid");
		try {
			connection.open();
		} catch(Exception ex) {
			assertTrue(ex.toString().contains("The connection attempt failed."));
			return;
		}
		finally {
			connection.close();
		}
	}
	@Test
	public void Open_NoHost() throws Exception {
		Connection connection = createConnection();
		connection.setServer(null);
		try {
			connection.open();
		} catch(Exception ex) {
			assertTrue(ex.toString().contains("Server required for connection!"));
			return;
		}
		finally {
			connection.close();
		}
	}
	@Test
	public void Open_BadPort() throws Exception {
		Connection connection = createConnection();
		connection.setPort(1);
		try {
			connection.open();
		} catch(Exception ex) {
			assertTrue(ex.toString().contains(
				"Connection to") &&
				ex.toString().contains("Check that the hostname and port are correct and that the postmaster is accepting TCP/IP connections.")
			);
			return;
		}
		finally {
			connection.close();
		}
	}
	@Test
	public void Open_BadUser() throws Exception {
		Connection connection = createConnection();
		connection.setUser("bad");
		try {
			connection.open();
		} 
		catch(Exception ex) {
			assertTrue(ex.toString().contains("FATAL: password authentication failed for user"));
			return;
		}
		finally {
			connection.close();
		}
	}
	@Test
	public void Open_NoUser() throws Exception {
		Connection connection = createConnection();
		connection.setUser(null);
		try {
			connection.open();
		} 
		catch(Exception ex) {
			assertTrue(ex.toString().contains("User required for connection!"));
			return;
		}
		finally {
			connection.close();
		}
	}
	@Test
	public void Open_BadPassword() throws Exception {
		Connection connection = createConnection();
		connection.setPassword("bad");
		try {
			connection.open();
		} 
		catch(Exception ex) {
			assertTrue(ex.toString().contains("FATAL: password authentication failed for user"));
			return;
		}
		finally {
			connection.close();
		}
	}
	@Test
	public void Open_NoPassword() throws Exception {
		Connection connection = createConnection();
		connection.setPassword(null);
		try {
			connection.open();
		} 
		catch(Exception ex) {
			assertTrue(ex.toString().contains("Password required for connection!"));
			return;
		}
		finally {
			connection.close();
		}
	}
	@Test
	public void Open_BadDatabase() throws Exception {
		Connection connection = createConnection();
		connection.setDatabase("bad");
		try {
			connection.open();
		} 
		catch(Exception ex) {
			assertTrue(ex.toString().contains("FATAL: database \"bad\" does not exist"));
			return;
		}
		finally {
			connection.close();
		}
	}
	
	@Test
	public void Execute_NullParamters() throws Exception {
		Connection connection = createConnection();		
		try {
			connection.open();
			connection.execute("CREATE TABLE \"Happy\" ( ID INT ); DROP TABLE \"Happy\";");
		} 
		finally {
			connection.close();
		}
		
		assertTrue(true);
	}
	@Test
	public void Execute_Invalid() throws Exception {
		Connection connection = createConnection();
		try {
			connection.open();
			connection.execute("SOME VERY BAD SQL");
		} 
		catch(Exception ex) {
			assertTrue(ex.toString().contains("org.postgresql.util.PSQLException: ERROR: syntax error at or near"));
		}
		finally {
			connection.close();
		}
	}
	
	@Test
	public void Fill_Good() throws Exception {
		Connection connection = createConnection();
	
		String tableName = null;
		try {
			connection.open();
			tableName = createStatefulTable(connection);
			
			Table<StatefulRow> table = connection.fill(
				StatefulRowFactory.instance, 
				"SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName), 
				null
			);
			
			assertTrue(table.size() > 0);
		} 
		finally {
			deleteTable(connection, tableName);
			connection.close();
		}
	}
	@Test
	public void Fill_Invalid() throws Exception {
		Connection connection = createConnection();
		
		String tableName = null;
		try {
			connection.open();
			tableName = createStatefulTable(connection);
			
			connection.fill(
				StatefulRowFactory.instance, 
				"SOME REALLY BAD QUERY", 
				null
			);
		} 
		catch(Exception ex) {
			assertTrue(ex.toString().contains("org.postgresql.util.PSQLException: ERROR: syntax error at or near"));
		}
		finally {
			deleteTable(connection, tableName);
			connection.close();
		}
	}
	

	@Test
	public void Paramter_ByteArray() throws Exception {
		Connection connection = createConnection();
		
		String column = "Type ByteA";
		
		String tableName = null;
		try {
			connection.open();
			tableName = createStatefulTable(connection);
			
			String sql = "SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName) +
				" WHERE " + connection.getAdapter().quoteIdentifier(column) + "=@Value";

			Parameters params = new Parameters();
			params.put("@Value", Hex.decodeHex("DEADBEEF"));
			
			Table<StatefulRow> table = connection.fill(StatefulRowFactory.instance, sql, params);

			assertArrayEquals(Hex.decodeHex("DEADBEEF"), (byte[]) table.get(0).get(column));
		} 
		finally {
			deleteTable(connection, tableName);
			connection.close();
		}
	}
	@Test
	public void Paramter_Boolean() throws Exception {
		Connection connection = createConnection();
		
		String column = "Type Boolean";
		String tableName = null;
		try {
			connection.open();
			tableName = createStatefulTable(connection);
			
			String sql = "SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName) +
				" WHERE " + connection.getAdapter().quoteIdentifier(column) + "=@Value";

			Parameters params = new Parameters();
			params.put("@Value", true);
			
			Table<StatefulRow> table = connection.fill(StatefulRowFactory.instance, sql, params);

			assertEquals(true, (boolean) table.get(0).get(column));		
		} 
		finally {
			deleteTable(connection, tableName);
			connection.close();
		}		
	}
	@Test
	public void Paramter_Double() throws Exception {
		Connection connection = createConnection();
		
		String column = "Type Double";
		String tableName = null;
		try {
			connection.open();
			tableName = createStatefulTable(connection);
			
			String sql = "SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName) +
				" WHERE " + 
				"@Lower < " + connection.getAdapter().quoteIdentifier(column) +
				" AND " + connection.getAdapter().quoteIdentifier(column) + " < @Upper";

			Parameters params = new Parameters();
			params.put("@Lower", 7.6);
			params.put("@Upper", 7.8);
			
			Table<StatefulRow> table = connection.fill(StatefulRowFactory.instance, sql, params);

			assertTrue(7.6 < ((double) table.get(0).get(column)) && ((double) table.get(0).get(column)) < 7.8);		
		} 
		finally {
			deleteTable(connection, tableName);
			connection.close();
		}
	}
	@Test
	public void Paramter_Float() throws Exception {
		Connection connection = createConnection();
		
		String column = "Type Real";
		String tableName = null;
		try {
			connection.open();
			tableName = createStatefulTable(connection);
			
			String sql = "SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName) +
				" WHERE " + 
				"@Lower < " + connection.getAdapter().quoteIdentifier(column) +
				" AND " + connection.getAdapter().quoteIdentifier(column) + " < @Upper";

			Parameters params = new Parameters();
			params.put("@Lower", 6.5);
			params.put("@Upper", 6.7);
			
			Table<StatefulRow> table = connection.fill(StatefulRowFactory.instance, sql, params);

			assertTrue(6.5 < ((float) table.get(0).get(column)) && ((float) table.get(0).get(column)) < 6.7);		
		} 
		finally {
			deleteTable(connection, tableName);
			connection.close();
		}
	}
	@Test
	public void Paramter_Integer() throws Exception {
		Connection connection = createConnection();
		
		String column = "Type Int";
		String tableName = null;
		try {
			connection.open();
			tableName = createStatefulTable(connection);
			
			String sql = "SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName) +
				" WHERE @Value = " + connection.getAdapter().quoteIdentifier(column);

			Parameters params = new Parameters();
			params.put("@Value", 2);
			
			Table<StatefulRow> table = connection.fill(StatefulRowFactory.instance, sql, params);

			assertTrue(((int) table.get(0).get(column)) == 2);		
		} 
		finally {
			deleteTable(connection, tableName);
			connection.close();
		}
	}
	@Test
	public void Paramter_Long() throws Exception { 
		Connection connection = createConnection();
		
		String column = "Type BigInt";
		String tableName = null;
		try {
			connection.open();
			tableName = createStatefulTable(connection);
			
			String sql = "SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName) +
				" WHERE @Value = " + connection.getAdapter().quoteIdentifier(column);

			Parameters params = new Parameters();
			params.put("@Value", 3);
			
			Table<StatefulRow> table = connection.fill(StatefulRowFactory.instance, sql, params);

			assertTrue(((long) table.get(0).get(column)) == 3);		
		} 
		finally {
			deleteTable(connection, tableName);
			connection.close();
		}
	}
	@Test
	public void Paramter_BigDecimal() throws Exception { 
		Connection connection = createConnection();
		
		String column = "Type Numeric";
		String tableName = null;
		try {
			connection.open();
			tableName = createStatefulTable(connection);
			
			String sql = "SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName) +
				" WHERE @Value = " + connection.getAdapter().quoteIdentifier(column);

			Parameters params = new Parameters();
			params.put("@Value", 5.5);
			
			Table<StatefulRow> table = connection.fill(StatefulRowFactory.instance, sql, params);

			assertTrue(
				((BigDecimal) table.get(0).get(column)).compareTo(BigDecimal.valueOf(5.4)) > 0 && 
				((BigDecimal) table.get(0).get(column)).compareTo(BigDecimal.valueOf(5.6)) < 0
			);		
		} 
		finally {
			deleteTable(connection, tableName);
			connection.close();
		} 
	}
	@Test
	public void Paramter_String() throws Exception { 
		Connection connection = createConnection();
		
		String column = "Type VarChar";
		String tableName = null;
		try {
			connection.open();
			tableName = createStatefulTable(connection);
			
			String sql = "SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName) +
				" WHERE @Value = " + connection.getAdapter().quoteIdentifier(column);

			Parameters params = new Parameters();
			params.put("@Value", "B");
			
			Table<StatefulRow> table = connection.fill(StatefulRowFactory.instance, sql, params);

			assertTrue(((String) table.get(0).get(column)).equals("B"));		
		} 
		finally {
			deleteTable(connection, tableName);
			connection.close();
		} 	
	}
	@Test
	public void Paramter_Date() throws Exception { 
		Connection connection = createConnection();
		
		String column = "Type Date";
		String tableName = null;
		try {
			connection.open();
			tableName = createStatefulTable(connection);
			
			String sql = "SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName) +
				" WHERE @Value = " + connection.getAdapter().quoteIdentifier(column);

			Parameters params = new Parameters();
			params.put("@Value", java.sql.Date.valueOf("2001-12-31"));
			
			Table<StatefulRow> table = connection.fill(StatefulRowFactory.instance, sql, params);

			assertTrue(((java.sql.Date) table.get(0).get(column)).equals(java.sql.Date.valueOf("2001-12-31")));		
		} 
		finally {
			deleteTable(connection, tableName);
			connection.close();
		} 	
	}
	@Test
	public void Paramter_Time() throws Exception { 
		Connection connection = createConnection();
	
		String column = "Type Time";
		String tableName = null;
		try {
			connection.open();
			tableName = createStatefulTable(connection);
			
			String sql = "SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName) +
				" WHERE @From < " + connection.getAdapter().quoteIdentifier(column) +
				" AND " + connection.getAdapter().quoteIdentifier(column) + " < @To";

			Parameters params = new Parameters();
			params.put("@From", java.sql.Time.valueOf("14:59:58"));
			params.put("@To", java.sql.Time.valueOf("15:00:00"));
			
			Table<StatefulRow> table = connection.fill(StatefulRowFactory.instance, sql, params);

			assertTrue(((java.sql.Time) table.get(0).get(column)).equals(new java.sql.Time(75599123)));		
		} 
		finally {
			deleteTable(connection, tableName);
			connection.close();
		} 	
	}
	@Test
	public void Paramter_Timestamp() throws Exception { 
		Connection connection = createConnection();
	
		String column = "Type TimeStamp";
		String tableName = null;
		try {
			connection.open();
			tableName = createStatefulTable(connection);
			
			String sql = "SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName) +
			" WHERE @From < " + connection.getAdapter().quoteIdentifier(column) +
			" AND " + connection.getAdapter().quoteIdentifier(column) + " < @To";

			Parameters params = new Parameters();
			params.put("@From", java.sql.Timestamp.valueOf("2000-12-31 13:01:58"));
			params.put("@To", java.sql.Timestamp.valueOf("2000-12-31 13:02:00"));
			
			Table<StatefulRow> table = connection.fill(StatefulRowFactory.instance, sql, params);

			assertTrue(
				((java.sql.Timestamp) table.get(0).get(column)).compareTo(java.sql.Timestamp.valueOf("2000-12-31 13:01:58")) > 0 &&
				((java.sql.Timestamp) table.get(0).get(column)).compareTo(java.sql.Timestamp.valueOf("2000-12-31 13:02:00")) < 0
			);		
		} 
		finally {
			deleteTable(connection, tableName);
			connection.close();
		} 	
	}
	@Test
	public void Paramter_Invalid() throws Exception { 
		Connection connection = createConnection();
	
		String column = "Type TimeStamp";
		String tableName = null;
		try {
			connection.open();
			tableName = createStatefulTable(connection);
			
			String sql = "SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName) +
			" WHERE @From < " + connection.getAdapter().quoteIdentifier(column) +
			" AND " + connection.getAdapter().quoteIdentifier(column) + " < @To";

			Parameters params = new Parameters();
			params.put("@From", java.sql.Timestamp.valueOf("2000-12-31 13:01:58"));
			params.put("@To", TableFactory.instanciate(StatefulRowFactory.instance));
			
			connection.fill(StatefulRowFactory.instance, sql, params);
		}
		catch(Exception ex) {
			assertTrue(ex.toString().contains("java.lang.Exception: Type 'com.lagovistatech.database.internal.TableImp is not supported!"));
		}
		finally {
			deleteTable(connection, tableName);
			connection.close();
		}  
	}

	@Test
	public void Check_SqlMappings() throws Exception {
		Connection connection = createConnection();
	
		String tableName = null;
		try {
			connection.open();
			tableName = createVersionedTable(connection);
			
			String sql = "SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName) +
				" WHERE " + connection.getAdapter().quoteIdentifier("GUID") + "=@GUID";

			Parameters params = new Parameters();
			params.put("@GUID", UUID.fromString("ea7cbaac-35ee-4547-95eb-3112f16f2cff"));
			
			Table<VersionedRow> table = connection.fill(VersionedRowFactory.instance, sql, params);
			
			for(String column : table.getSchema().keySet()) {
				String sqlType = table.getSchema().get(column);
				if(!PostgresAdapter.sqlToJavaMap.containsKey(sqlType)) {
					fail("Missing type for '" + sqlType + "'!");
					return;
				}
			}
			assertTrue(true);
		} 
		finally {
			deleteTable(connection, tableName);
			connection.close();
		}		
	}	
	
	@Test
	public void Select_Versioned() throws Exception {
		Connection connection = createConnection();

		String tableName = null;
		try {
			connection.open();
			tableName = createVersionedTable(connection);
			
			String sql = "SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName) +
				" WHERE " + connection.getAdapter().quoteIdentifier("GUID") + "=@GUID";

			Parameters params = new Parameters();
			params.put("@GUID", UUID.fromString("ea7cbaac-35ee-4547-95eb-3112f16f2cff"));
			
			Table<VersionedRow> table = connection.fill(VersionedRowFactory.instance, sql, params);

			assertEquals("ea7cbaac-35ee-4547-95eb-3112f16f2cff", table.get(0).getGuid().toString());
			assertEquals(Long.valueOf(1), table.get(0).getVersion());
		} 
		finally {
			deleteTable(connection, tableName);
			connection.close();
		}		
	}	
	@Test
	public void Insert_Versioned() throws Exception {
		Connection connection = createConnection();

		String tableName = null;
		try {
			connection.open();

			tableName = createVersionedTable(connection);			
			
			Table<VersionedRow> table = TableFactory.instanciate(VersionedRowFactory.instance);
			table.setTableName(tableName);
			
			VersionedRow row = table.createRow();
			row.set("Type SmallInt", 10);
			row.set("Type Int", 20);
			row.set("Type BigInt", 30);

			row.set("Type Decimal", 40.04);
			row.set("Type Numeric", 50.05);
			row.set("Type Real", 60.06);
			row.set("Type Double", 70.07);

			row.set("Type SmallSerial", 80);
			row.set("Type BigSerial", 90);
			
//			row.set("Type Money", 100.01);

			row.set("Type Char", "Some Char");
			row.set("Type VarChar", "Some Varing Char");
			row.set("Type Text", "This is a; long text! John's first \"baby\" was\n"
				+ " born!"
			);

			row.set("Type ByteA", new byte[] { 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60 });

			row.set("Type TimeStamp", java.sql.Timestamp.from(Instant.now()));
			row.set("Type Date", java.sql.Date.from(Instant.now()));
			row.set("Type Time", java.sql.Time.from(Instant.now()));

			row.set("Type Boolean", false);
			
			long cnt = connection.save(table);
			assertTrue(cnt == 1);
		}
		finally {
			deleteTable(connection, tableName);
			connection.close();
		}
	}	
	@Test
	public void Update_Versioned() throws Exception {
		Connection connection = createConnection();
	
		String tableName = null;
		try {
			connection.open();
			tableName = createVersionedTable(connection);
			
			// load table
			String sql = "SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName) +
				" WHERE " + connection.getAdapter().quoteIdentifier("Type ByteA") + "=@Value";

			Parameters params = new Parameters();
			params.put("@Value", Hex.decodeHex("DEADBEEF"));

			// get row
			Table<VersionedRow> table = connection.fill(VersionedRowFactory.instance, sql, params);
			VersionedRow row = table.get(0);
			
			// update column
			row.set("Type BigInt", 1024);
			
			// save
			long cnt = connection.save(table);
			
			// reload
			table = connection.fill(VersionedRowFactory.instance, sql, params);
			
			// check
			row = table.get(0);			
			assertTrue(cnt == 1 && Helpers.objectToLong(row.get("Type BigInt")) == 1024);
		}
		finally {
			deleteTable(connection, tableName);
			connection.close();
		}		
	}	
	@Test
	public void Delete_Versioned() throws Exception {
		Connection connection = createConnection();
	
		String tableName = null;
		try {
			connection.open();
			tableName = createVersionedTable(connection);
			
			// load table
			String sql = "SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName) +
				" WHERE " + connection.getAdapter().quoteIdentifier("GUID") + "=@GUID";

			Parameters params = new Parameters();
			params.put("@GUID", UUID.fromString("ea7cbaac-35ee-4547-95eb-3112f16f2cff"));

			// get row
			Table<VersionedRow> table = connection.fill(VersionedRowFactory.instance, sql, params);
			VersionedRow row = table.get(0);
			
			// update
			row.setIsDeleted(true);
			
			// save
			long cnt = connection.save(table);
			
			// reload
			table = connection.fill(VersionedRowFactory.instance, sql, params);
			
			// check
			assertTrue(cnt == 1 && table.size() == 0);
		}
		finally {
			deleteTable(connection, tableName);
			connection.close();
		}			
	}	
	
	@Test
	public void Select_Stateful() throws Exception {
		Connection connection = createConnection();

		String tableName = null;
		try {
			connection.open();
			tableName = createVersionedTable(connection);
			
			String sql = "SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName) +
				" WHERE " + connection.getAdapter().quoteIdentifier("Type ByteA") + "=@Value";

			Parameters params = new Parameters();
			params.put("@Value", Hex.decodeHex("DEADBEEF"));
			
			Table<StatefulRow> table = connection.fill(StatefulRowFactory.instance, sql, params);

			assertArrayEquals(Hex.decodeHex("DEADBEEF"), Helpers.objectToBytes(table.get(0).get("Type ByteA")));
		} 
		finally {
			deleteTable(connection, tableName);
			connection.close();
		}				
	}	
	@Test
	public void Insert_Stateful() throws Exception {
		Connection connection = createConnection();

		String tableName = null;
		try {
			connection.open();

			tableName = createStatefulTable(connection);			

			Table<StatefulRow> table = TableFactory.instanciate(StatefulRowFactory.instance);
			table.setTableName(tableName);
			
			StatefulRow row = table.createRow();
			row.set("Type SmallInt", 10);
			row.set("Type Int", 20);
			row.set("Type BigInt", 30);

			row.set("Type Decimal", 40.04);
			row.set("Type Numeric", 50.05);
			row.set("Type Real", 60.06);
			row.set("Type Double", 70.07);

			row.set("Type SmallSerial", 80);
			row.set("Type BigSerial", 90);
			
//			row.set("Type Money", 100.01);

			row.set("Type Char", "Some Char");
			row.set("Type VarChar", "Some Varing Char");
			row.set("Type Text", "This is a; long text! John's first \"baby\" was\n"
				+ " born!"
			);

			row.set("Type ByteA", new byte[] { 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60 });

			row.set("Type TimeStamp", java.sql.Timestamp.from(Instant.now()));
			row.set("Type Date", java.sql.Date.from(Instant.now()));
			row.set("Type Time", java.sql.Time.from(Instant.now()));

			row.set("Type Boolean", false);
			
			long cnt = connection.save(table);
			assertTrue(cnt == 1);
		}
		finally {
			deleteTable(connection, tableName);
			connection.close();
		}
	}	
	@Test
	public void Update_Stateful() throws Exception {
		Connection connection = createConnection();

		String tableName = null;
		try {
			connection.open();
			tableName = createStatefulTable(connection);
			
			// load table
			String sql = "SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName) +
				" WHERE " + connection.getAdapter().quoteIdentifier("Type ByteA") + "=@Value";

			Parameters params = new Parameters();
			params.put("@Value", Hex.decodeHex("DEADBEEF"));

			// get row
			Table<StatefulRow> table = connection.fill(StatefulRowFactory.instance, sql, params);
			StatefulRow row = table.get(0);
			
			// update column
			row.set("Type BigInt", 1024);
			
			// save
			long cnt = connection.save(table);
			
			// reload
			table = connection.fill(StatefulRowFactory.instance, sql, params);
			
			// check
			row = table.get(0);			
			assertTrue(cnt == 1 && Helpers.objectToLong(row.get("Type BigInt")) == 1024);
		}
		catch(Exception ex) {
			fail(ex.toString());
		}
		finally {
			deleteTable(connection, tableName);
			connection.close();
		}		
	}	
	@Test
	public void UpdateLimit1_Stateful() throws Exception {
		Connection connection = createConnection();

		String tableName = null;
		try {
			connection.open();
			tableName = createStatefulTable(connection);
			
			// load table
			String sql = "SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName) +
				" WHERE " + connection.getAdapter().quoteIdentifier("Type ByteA") + "=@Value";

			Parameters params = new Parameters();
			params.put("@Value", Hex.decodeHex("DEADBEEF"));

			// get row
			Table<StatefulRow> table = connection.fill(StatefulRowFactory.instance, sql, params);
			StatefulRow row = table.get(0);
			
			// create duplicate row
			StatefulRow dupRow = table.createRow();
			for(String column : table.getSchema().keySet())
				dupRow.set(column, row.get(column));
			long cnt = connection.save(table);
			if(cnt != 1)
				throw new Exception("Could not insert duplicate row!");

			// get rows
			table = connection.fill(StatefulRowFactory.instance, sql, params);
			row = table.get(0);

			// update column
			row.set("Type BigInt", 1024);
			
			// save
			cnt = connection.save(table);
			
			// reload
			sql = "SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName) +
				" WHERE " + connection.getAdapter().quoteIdentifier("Type ByteA") + "=@Value" +
				" AND " + connection.getAdapter().quoteIdentifier("Type BigInt") + "=1024";
			table = connection.fill(StatefulRowFactory.instance, sql, params);
			
			// check
			row = table.get(0);			
			assertTrue(cnt == 1 && Helpers.objectToLong(row.get("Type BigInt")) == 1024);
		}
		catch(Exception ex) {
			fail(ex.toString());
		}
		finally {
			deleteTable(connection, tableName);
			connection.close();
		}		
	}	
	@Test
	public void Delete_Stateful() throws Exception {
		Connection connection = createConnection();

		String tableName = null;
		try {
			connection.open();
			tableName = createStatefulTable(connection);
			
			// load table
			String sql = "SELECT * FROM " + connection.getAdapter().quoteIdentifier(tableName) +
				" WHERE " + connection.getAdapter().quoteIdentifier("Type ByteA") + "=@Value";

			Parameters params = new Parameters();
			params.put("@Value", Hex.decodeHex("DEADBEEF"));

			// get row
			Table<StatefulRow> table = connection.fill(StatefulRowFactory.instance, sql, params);
			StatefulRow row = table.get(0);
			
			// update column
			row.setIsDeleted(true);
			
			// save
			long cnt = connection.save(table);
			
			// reload
			table = connection.fill(StatefulRowFactory.instance, sql, params);
			
			// check
			assertTrue(cnt == 1 && table.size() == 0);
		}
		catch(Exception ex) {
			fail(ex.toString());
		}
		finally {
			deleteTable(connection, tableName);
			connection.close();
		}		
	}	
	
//	@Test
//	public void Select_Nulls() { assertTrue(true); }
//	@Test
//	public void Values_To_Nulls() { assertTrue(true); }
//	@Test
//	public void Nulls_To_Values() { assertTrue(true); }
//	
//	@Test
//	public void Save_NotNull_Error() { assertTrue(true); }
//	
//	@Test
//	public void Duplicate_Primary_Key() { assertTrue(true); }
//	@Test
//	public void Foreign_Key_Error() { assertTrue(true); }
//
//	@Test
//	public void Text_Overflow() { assertTrue(true); }
//	@Test
//	public void Blob_Overflow() { assertTrue(true); }
//
//	@Test
//	public void Date_Into_Number() { assertTrue(true); }
//	@Test
//	public void Time_Into_Number() { assertTrue(true); }
//	@Test
//	public void Timestamp_Into_Number() { assertTrue(true); }
//	@Test
//	public void Number_Into_String() { assertTrue(true); }
//	@Test
//	public void String_Into_Number() { assertTrue(true); }
//	
//
//	@Test
//	public void Create_Table() { assertTrue(true); }
//	@Test
//	public void Drop_Table() { assertTrue(true); }
//	@Test
//	public void Create_Schema() { assertTrue(true); }
//	@Test
//	public void Drop_Schema() { assertTrue(true); }
//	@Test
//	public void Create_Database() { assertTrue(true); }
//	@Test
//	public void Drop_Database() { assertTrue(true); }
//
//
//	@Test
//	public void Begin_Insert_Commit_Read() { assertTrue(true); }
//	@Test
//	public void Begin_Insert_Rollback_Read() { assertTrue(true); }
//	@Test
//	public void Begin_Insert_Close_Open_Read() { assertTrue(true); }
//	@Test
//	public void Commit_Without_Begin() { assertTrue(true); }
//	@Test
//	public void Rollback_Without_Begin() { assertTrue(true); }
//	
//	@Test
//	public void Save_Multiple_Rows() { assertTrue(true); }
//
//	@Test
//	public void Execute_Multiple_Statements() { assertTrue(true); }
}
