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

import java.sql.ResultSet;
import java.util.HashMap;

import com.lagovistatech.Factory;
import com.lagovistatech.database.Adapter;
import com.lagovistatech.database.Connection;
import com.lagovistatech.database.Parameters;
import com.lagovistatech.database.Row;
import com.lagovistatech.database.Table;
import com.lagovistatech.database.TableFactory;

public class ConnectionImp implements Connection {
	public ConnectionImp(Adapter adapter) {
		this.adapter = adapter;
	}

	public String getServer() { return adapter.getServer(); }
	public void setServer(String server) { adapter.setServer(server); }

	public int getPort() { return adapter.getPort(); }
	public void setPort(int port) { adapter.setPort(port); }

	public String getDatabase() { return adapter.getDatabase(); }
	public void setDatabase(String database) { adapter.setDatabase(database); }

	public String getUser() { return adapter.getUser(); }
	public void setUser(String user) { adapter.setUser(user); }

	public int getTimeOut() { return adapter.getTimeOut(); }
	public void setTimeOut(int timeOut) { adapter.setTimeOut(timeOut); }

	public void setPassword(String password) { adapter.setPassword(password); }
	
	private Adapter adapter;
	public void setAdapter(Adapter value) { this.adapter = value; }
	public Adapter getAdapter() { return adapter; }
	
	public void open() throws Exception { adapter.open(); }
	public void close() throws Exception { adapter.close(); }

	public long execute(String sql, Parameters params) throws Exception {
		String processed = sql;	
		if(params != null)
			for(String key : params.keySet())
				processed = processed.replace(key, adapter.objectToSql(params.get(key)));
		
		return execute(processed);
	}
	public long execute(String sql) throws Exception {
		return adapter.execute(sql);
	}
	public <R extends Row> Table<R> fill(Factory<R> rowFactory, String sql, Parameters params) throws Exception { 
		String processed = sql;		
		if(params != null)
			for(String key : params.keySet())
				processed = processed.replace(key, adapter.objectToSql(params.get(key)));
		
		return fill(rowFactory, processed);
	}
	public <R extends Row> Table<R> fill(Factory<R> rowFactory, String sql) throws Exception { 
		ResultSet rs = adapter.fill(sql);
		
		
		
		HashMap<String, String> schema = new HashMap<String, String>();
		for(int cnt = 1; cnt <= rs.getMetaData().getColumnCount(); cnt++)
			schema.put(rs.getMetaData().getColumnLabel(cnt), rs.getMetaData().getColumnTypeName(cnt));

		Table<R> ret = TableFactory.instanciate(rowFactory);
		ret.setTableName(rs.getMetaData().getTableName(1));
		ret.setSchema(schema);
		
		while(rs.next()) {
			R row = ret.createRow();
			row.copyFrom(rs);
		}
		
		return ret;
	}
	public <R extends Row> long save(Table<R> table) throws Exception {
		return execute(table.generateSave(adapter));
	}

	public void begin() throws Exception { adapter.execute("BEGIN"); }
	public void commit() throws Exception { adapter.execute("COMMIT"); }
	public void rollback() throws Exception { adapter.execute("ROLLBACK"); }
}
