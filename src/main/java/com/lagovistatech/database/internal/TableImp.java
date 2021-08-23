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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.lagovistatech.Factory;
import com.lagovistatech.database.Adapter;
import com.lagovistatech.database.Row;
import com.lagovistatech.database.Table;

public class TableImp<R extends Row> implements Table<R> {
	public TableImp(Factory<R> rowFactory, List<R> rows) {
		this.rowFactory = rowFactory;
		this.rows = rows;
	}
	
	private String tableName = ""; 
	public String getTableName() {
		try {
			if(tableName.length() < 1) {
				Row row = this.rowFactory.create();
				tableName = (String) row.getClass().getField("TABLE_NAME").get(null);
			}
		}
		catch(Exception ex) { /* do nothing */ }
		return tableName; 
	}
	public void setTableName(String value) { tableName = value; }

	private HashMap<String, String> schema;
	public HashMap<String, String> getSchema() { return schema; }
	public void setSchema(HashMap<String, String> value) { schema = value; }

	private Factory<R> rowFactory;
	private List<R> rows;
	public R createRow() throws Exception {
		R row = rowFactory.create();
		row.setTable(this);
		this.rows.add(row);
		return row; 
	}
	public String generateSave(Adapter adapter) throws Exception {
		String ret = "";
		for(R row : rows) {
			String temp = row.generateSave(adapter);
			if(temp != null)
				ret += temp;
		}
		return ret;
	}


	public int size() { return rows.size(); }
	public boolean isEmpty() { return rows.isEmpty(); }

	public R get(int index) { return rows.get(index); }
	public R set(int index, R row) { return rows.set(index, row); }
	public void add(int index, R row) { rows.add(row); }
	public boolean add(R row) { return rows.add(row); }
	public boolean remove(Object row) { return rows.remove(row); }
	public R remove(int index) { return rows.remove(index); }
	
	public boolean addAll(Collection<? extends R> collection) { return rows.addAll(collection); }
	public boolean addAll(int index, Collection<? extends R> collection) { return rows.addAll(collection); }
	public boolean removeAll(Collection<?> collection) { return rows.removeAll(collection); }
	public boolean retainAll(Collection<?> collection) { return rows.retainAll(collection); }
	public void clear() { rows.clear(); }

	public boolean contains(Object row) { return rows.contains(row); }
	public boolean containsAll(Collection<?> collection) { return rows.containsAll(collection); }

	public int indexOf(Object row) { return rows.indexOf(row); }
	public int lastIndexOf(Object row) { return rows.lastIndexOf(row); }
	
	public Iterator<R> iterator() { return rows.iterator(); }
	public ListIterator<R> listIterator() { return rows.listIterator(); }
	public ListIterator<R> listIterator(int index) { return rows.listIterator(index); }

	public Object[] toArray() { return rows.toArray(); }
	public <T> T[] toArray(T[] target) { return rows.toArray(target); }	
	public List<R> subList(int fromIndex, int toIndex) { return rows.subList(fromIndex, toIndex); }
}
