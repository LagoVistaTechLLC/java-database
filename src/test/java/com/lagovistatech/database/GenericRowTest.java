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

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

public class GenericRowTest {
	@Test
	public void RemoveFromChanges() {
		HashMap<String, Object> originals = new HashMap<String, Object>();
		originals.put("Value", true);

		HashMap<String, Object> changes = new HashMap<String, Object>();
		changes.put("Value", false);
		
		StatefulRow row = new StatefulRow(originals, changes);
		
		row.set("Value", true);
		
		assertTrue(!changes.containsKey("Value"));
	}
	@Test
	public void GetOriginal() {
		HashMap<String, Object> originals = new HashMap<String, Object>();
		originals.put("Value", true);

		HashMap<String, Object> changes = new HashMap<String, Object>();
		
		StatefulRow row = new StatefulRow(originals, changes);
				
		assertTrue((Boolean) row.get("Value"));
	}
	@Test
	public void GetChange() {
		HashMap<String, Object> originals = new HashMap<String, Object>();
		originals.put("Value", true);

		HashMap<String, Object> changes = new HashMap<String, Object>();
		originals.put("Value", false);
		
		StatefulRow row = new StatefulRow(originals, changes);
				
		assertTrue(!((Boolean) row.get("Value")));
	}
	@Test
	public void GetChangeOnNew() {
		HashMap<String, Object> originals = new HashMap<String, Object>();

		HashMap<String, Object> changes = new HashMap<String, Object>();
		originals.put("Value", true);
		
		StatefulRow row = new StatefulRow(originals, changes);
				
		assertTrue((Boolean) row.get("Value"));
	}
	@Test
	public void ResetToNullOriginal() {
		HashMap<String, Object> originals = new HashMap<String, Object>();

		HashMap<String, Object> changes = new HashMap<String, Object>();
		changes.put("Value", true);
		
		StatefulRow row = new StatefulRow(originals, changes);
		row.set("Value", null);
				
		assertTrue(changes.size() == 0);
	}
	@Test
	public void ChangeOriginal() {
		HashMap<String, Object> originals = new HashMap<String, Object>();
		originals.put("Value", false);

		HashMap<String, Object> changes = new HashMap<String, Object>();
		
		StatefulRow row = new StatefulRow(originals, changes);
		row.set("Value", true);
				
		assertTrue((Boolean) row.get("Value"));
	}
	@Test
	public void GetNull() {
		HashMap<String, Object> originals = new HashMap<String, Object>();
		HashMap<String, Object> changes = new HashMap<String, Object>();		
		StatefulRow row = new StatefulRow(originals, changes);
				
		assertTrue(row.get("Value") == null);
	}	
}
