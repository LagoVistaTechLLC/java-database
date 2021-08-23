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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is a map of columns (strings) to values (objects).
 */
public class Parameters implements Map<String, Object> {
	private HashMap<String, Object> myMap = new HashMap<String, Object>();

	public int size() { return myMap.size(); }
	public boolean isEmpty() { return myMap.isEmpty(); }

	public boolean containsKey(Object key) { return myMap.containsKey(key); }
	public Set<String> keySet() { return myMap.keySet(); }

	public boolean containsValue(Object value) { return myMap.containsValue(value); }
	public Collection<Object> values() { return myMap.values(); }

	public Object get(Object key) { return myMap.get(key); }
	public Object put(String key, Object value) { return myMap.put(key, value); }
	public void putAll(Map<? extends String, ? extends Object> m) { myMap.putAll(m); }
	public Object remove(Object key) { return myMap.remove(key); }
	public void clear() { myMap.clear(); }

	public Set<Entry<String, Object>> entrySet() { return myMap.entrySet(); }
}
