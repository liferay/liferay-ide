/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.core.templates;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Cindy Li
 */
public class TemplateContext implements ITemplateContext {

	public boolean containsKey(String key) {
		return _context.containsKey(key);
	}

	public Map<String, Object> getMap() {
		return _context;
	}

	public Object put(String key, Object value) {
		return _context.put(key, value);
	}

	private Map<String, Object> _context = new HashMap<>();

}