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
import java.util.HashMap;

import com.lagovistatech.database.internal.AbstractRow;

/**
 * The stateful row keeps track of the rows original values and changes.  This
 * will be used when saving the row.  Stateful rows do not have a unique, nor a
 * primary key, nor a version number requirement.
 */
public class StatefulRow extends AbstractRow implements Row {
	/**
	 * Creates a rows initialized with the provided original and changed values.
	 * 
	 * @param originals Map of columns to original values.
	 * @param changes Map of columns to changed values.
	 */
	public StatefulRow(HashMap<String, Object> originals, HashMap<String, Object> changes) {
		originalsMap = originals;
		changesMap = changes;
	}

	private HashMap<String, Object> originalsMap;
	private HashMap<String, Object> changesMap;
	/**
	 * @param column Column of the value to fetch.
	 * @return Value of the specified column.
	 */
	public Object get(String column) {
		if(changesMap.containsKey(column))
			return changesMap.get(column);
		else if (originalsMap.containsKey(column))
			return originalsMap.get(column);
		else
			return null;
	}
	/**
	 * @param column Column of the value to set.
	 * @param value Value to set.
	 */
	public void set(String column, Object value) {
		boolean isInOrig = originalsMap.containsKey(column);
		boolean isEqOrig = isInOrig && originalsMap.get(column).equals(value);
		boolean isInChg = changesMap.containsKey(column);

		if(isInChg && isEqOrig)
			changesMap.remove(column);
		else if(isInChg && !isInOrig && value == null)
			changesMap.remove(column);
		else
			changesMap.put(column, value);
	}
	
	/**
	 * This allows the row to be loaded from a JDBC result set object.
	 * @param rs Result set to copy data from.
	 * @throws Exception
	 */
	public void copyFrom(ResultSet source) throws Exception {
		for(int cnt = 1; cnt <= source.getMetaData().getColumnCount(); cnt++)
			originalsMap.put(source.getMetaData().getColumnLabel(cnt), source.getObject(cnt));
	}
	/**
	 * Provided a database specific adapter, this will return an SQL query to 
	 * update the rows in the database.
	 * 
	 * @param adapter Database specific adapter.
	 * @return SQL query to insert, update, or delete.
	 * @throws Exception
	 */
	public String generateSave(Adapter adapter) throws Exception {
		return adapter.generateSave(getTable(), originalsMap, changesMap, getIsDeleted());
	}
}
