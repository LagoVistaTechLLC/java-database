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

import java.util.LinkedList;

import com.lagovistatech.Factory;
import com.lagovistatech.database.internal.TableImp;

/**
 * Creates tables that contain rows of R type.
 * 
 * @param <R> Row type.
 */
public class TableFactory<R extends Row> implements Factory<Table<R>> {
	/**
	 * Creates a table that contains rows of R type.
	 * 
	 * @param <R> Row type.
	 * @param rowFactory Row factory for use by the created table.
	 * @return Table
	 */
	public static <R extends Row> Table<R> instanciate(Factory<R> rowFactory) {
		return new TableImp<R>(rowFactory, new LinkedList<R>()); 
	}

	/**
	 * Creates a table factory that constructs table that contain rows of R 
	 * type.
	 * 
	 * @param rowFactory Row factory for use by the created table.
	 */
	public TableFactory(RowFactory<R> rowFactory) {
		this.rowFactory = rowFactory;
	}		
	private RowFactory<R> rowFactory = null;
	/**
	 * @return Table that contains rows of R type.
	 */
	public Table<R> create() {
		return new TableImp<R>(rowFactory, new LinkedList<R>());
	}
}
