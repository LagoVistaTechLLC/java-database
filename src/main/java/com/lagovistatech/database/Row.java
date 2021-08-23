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

/**
 * Each database is made up of tables that contain zero or more rows.  Rows hold
 * values that are defined by the columns of that row.
 */
public interface Row {
	/**
	 * @param <R> Row type.
	 * @return Table this row belongs to.
	 */
	<R extends Row> Table<R> getTable();
	/**
	 * @param <R> Row type.
	 * @param value Table this row belongs to.
	 */
	<R extends Row> void setTable(Table<R> value);
	
	/**
	 * @param column Column of the value to fetch.
	 * @return Value of the specified column.
	 */
	Object get(String column);
	/**
	 * @param column Column of the value to set.
	 * @param value Value to set.
	 */
	void set(String column, Object value);
	
	/**
	 * @return True if row is to be deleted.
	 */
	boolean getIsDeleted();
	/**
	 * @param value True if row is to be deleted.
	 */
	void setIsDeleted(boolean value);

	/**
	 * This allows the row to be loaded from a JDBC result set object.
	 * @param rs Result set to copy data from.
	 * @throws Exception
	 */
	void copyFrom(ResultSet rs) throws Exception;
	/**
	 * Provided a database specific adapter, this will return an SQL query to 
	 * update the rows in the database.
	 * 
	 * @param adapter Database specific adapter.
	 * @return SQL query to insert, update, or delete.
	 * @throws Exception
	 */
	String generateSave(Adapter adapter) throws Exception;
}
