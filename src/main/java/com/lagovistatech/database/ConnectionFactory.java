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

import com.lagovistatech.Factory;
import com.lagovistatech.database.internal.ConnectionImp;
import com.lagovistatech.database.internal.PostgresAdapter;

/**
 * Creates a connection for the supported database types - currently PostgreSQL.
 */
public class ConnectionFactory implements Factory<Connection> {
	private ConnectionFactory() {}
	public static ConnectionFactory instance = new ConnectionFactory();
	
	/**
	 * @return A PostgreSQL based connection.
	 */
	public static Connection instanciate() { return instance.create(); }
	
	/**
	 * @return A PostgreSQL based connection.
	 */
	public Connection create() {
		PostgresAdapter adapter = new PostgresAdapter();
		return new ConnectionImp(adapter);
	}
}
