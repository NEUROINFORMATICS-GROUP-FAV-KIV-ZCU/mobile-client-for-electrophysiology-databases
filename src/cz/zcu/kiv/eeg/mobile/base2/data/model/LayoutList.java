/***********************************************************************************************************************
 *
 * This file is part of the eeg-database-for-android project

 * ==========================================
 *
 * Copyright (C) 2013 by University of West Bohemia (http://www.zcu.cz/en/)
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * Petr Ježek, Petr Miko
 *
 **********************************************************************************************************************/
package cz.zcu.kiv.eeg.mobile.base2.data.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import java.util.List;

/**
 * Data container for holding layout collection.
 * 
 * @author Jaroslav Hošek
 * 
 */
@Root(name = LayoutList.XML_ROOT)
public class LayoutList {
	public static final String XML_ROOT = "layouts";

	@ElementList(inline = true, required = false)
	private List<Layout> layouts;

	public LayoutList() {
	}

	public List<Layout> getLayouts() {
		return layouts;
	}

	public void setLayouts(List<Layout> layouts) {
		this.layouts = layouts;
	}

	public boolean isAvailable() {
		return layouts != null && !layouts.isEmpty();
	}

	public int size() {
		return layouts.size();
	}
}