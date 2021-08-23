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

public class RowFactory<R extends Row> implements Factory<R> {
	public RowFactory(Type type) {
		this.type = type;
	}
	
	public enum Type {
		Stateful,
		Versioned
	}

	private static StatefulRowFactory statefulFactory = StatefulRowFactory.instance;
	private static VersionedRowFactory versionedFactory = VersionedRowFactory.instance;
	
	@SuppressWarnings("unchecked")
	public static <R extends Row> R instanciate(Type type) {
		switch(type) {
			case Stateful: return (R) statefulFactory.create();
			case Versioned: return (R) versionedFactory.create();
			default: return null;
		}
	}
	
	private Type type;
	@SuppressWarnings("unchecked")
	public R create() {
		return (R) instanciate(type);
	}
	
}
