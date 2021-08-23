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
import java.util.UUID;

import com.lagovistatech.database.internal.AbstractRow;

/**
 * A versioned row requires a GUID column and a Version column.  The GUID column
 * is the primary key, and the version column is present to provide concurrency
 * guarantees when saving.
 * 
 * The version number should not be modified.  When saved, the row with the 
 * matching GUID and version number will be updated, and the version number will
 * be incremented.
 * 
 * If the version number in the database does not match the version number of
 * the row being saved, no changes will be made.  This is an indication that 
 * between the time of loading the row into the table and saving, the row was
 * modified - the data you are saving could overwrite someone else's changes,
 * or potentially be out of date.
 */
public class VersionedRow extends AbstractRow implements Row, Versioned {
	/**
	 * Primary key column name.
	 */
	public final static String COLUMN_GUID = "GUID";
	/**
	 * Concurrency version number column name.
	 */
	public final static String COLUMN_VERSION = "Version";
	
	/**
	 * Creates a versioned row with the provided column to values map.
	 * 
	 * @param values The map of columns to values.
	 */
	public VersionedRow(HashMap<String, Object> values) {
		this.values = values;
	}
	
	private HashMap<String, Object> values = new HashMap<String, Object>();
	/**
	 * @param column Column of the value to fetch.
	 * @return Value of the specified column.
	 */
	public Object get(String column) {
		if(values.containsKey(column))
			return values.get(column);
		else
			return null;
	}
	public void set(String column, Object value) { values.put(column, value); }

	/**
	 * @return The concurrency version number.
	 */
	public Long getVersion() {
		if(values.containsKey(COLUMN_VERSION))
			return (long) get(COLUMN_VERSION);
		else
			return (long) 0;
	}
	public void setVersion(Long value) { set(COLUMN_VERSION, value); }

	/**
	 * @return The GUID identifying the row.
	 */
	public UUID getGuid() {
		if(values.containsKey(COLUMN_GUID))
			return (UUID) get(COLUMN_GUID);
		else
			return null;
	}
	public void setGuid(UUID value) { set(COLUMN_GUID, value); }
	
	/**
	 * This allows the row to be loaded from a JDBC result set object.
	 * @param rs Result set to copy data from.
	 * @throws Exception
	 */
	public void copyFrom(ResultSet source) throws Exception {
		for(int cnt = 1; cnt < source.getMetaData().getColumnCount(); cnt++)
			set(source.getMetaData().getColumnLabel(cnt), source.getObject(cnt));
	}
	public String generateSave(Adapter adapter) throws Exception {
		return adapter.generateSave(getTable(), values, getIsDeleted());
	}
}
