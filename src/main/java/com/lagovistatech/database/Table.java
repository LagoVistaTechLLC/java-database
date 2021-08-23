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

import java.util.HashMap;
import java.util.List;

/**
 * The table contains one or more rows of the specified type.
 * 
 * When a table is filled, the columns and database column type is loaded 
 * into the schema.
 *
 * @param <R> Type of rows the table manages.
 */
public interface Table<R extends Row> extends List<R> {
	/**
	 * @return Name of the table in the database.
	 */
	String getTableName();
	/**
	 * @param value Name of the table in the database.
	 */
	void setTableName(String value);
	
	/** 
	 * @return Map of columns to database data type.
	 */
	HashMap<String, String> getSchema();
	/**
	 * @param value Map of columns to database data type.
	 */
	void setSchema(HashMap<String, String> value);
	
	/**
	 * @return New row that included in the tables row set.
	 * @throws Exception 
	 */
	R createRow() throws Exception;
	
	/**
	 * For each row in the table, this will create a series of SQL statements to
	 * save those rows.
	 * 
	 * @param adapter Database adapter for the connections database type.
	 * @return SQL queries to save the rows.
	 * @throws Exception
	 */
	String generateSave(Adapter adapter) throws Exception;
}
