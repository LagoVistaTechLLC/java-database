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
public class VersionedRowFactory implements Factory<VersionedRow> {
	private VersionedRowFactory() {}
	
	public static final VersionedRowFactory instance = new VersionedRowFactory();
	/**
	 * Creates a versioned row.
	 * @return New row.
	 */
	public static VersionedRow instanciate() { return instance.create(); }
	
	/**
	 * Creates a versioned row.
	 * @return New row.
	 */
	public VersionedRow create() {
		return new VersionedRow(new HashMap<String, Object>());
	}
}
