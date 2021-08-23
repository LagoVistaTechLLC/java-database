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

import com.lagovistatech.database.Row;
import com.lagovistatech.database.Table;

/**
 * Abstract row provides default implementations of the table and deleted 
 * properties.
 */
public abstract class AbstractRow implements Row {
	private Table<?> table;
	@SuppressWarnings("unchecked")
	/**
	 * @return Table the row belongs to.
	 */
	public <R extends Row> Table<R> getTable() { return (Table<R>) table; }
	/**
	 * @param value Table the row belongs to.
	 */
	public <R extends Row> void setTable(Table<R> value) { table = value; }
	
	private boolean isDeleted = false;
	/**
	 * @return True if row is to be deleted.
	 */
	public boolean getIsDeleted() { return isDeleted; }
	/**
	 * @param value True if row is to be deleted.
	 */
	public void setIsDeleted(boolean value) { isDeleted = value; }
}
