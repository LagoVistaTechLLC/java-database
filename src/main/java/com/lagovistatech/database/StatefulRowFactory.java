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

import com.lagovistatech.Factory;

/**
 * The row factory is used by the table to create rows of the tables defined row
 * types.
 */
public class StatefulRowFactory implements Factory<StatefulRow> {
	private StatefulRowFactory() {}
	
	public static final StatefulRowFactory instance = new StatefulRowFactory();

	/**
	 * Creates a stateful row.
	 * @return New row.
	 */
	public static StatefulRow instanciate() { return instance.create(); }
	
	/**
	 * Creates a stateful row.
	 * @return New row.
	 */
	public StatefulRow create() {
		return new StatefulRow(new HashMap<String, Object>(), new HashMap<String, Object>());
	}
}
