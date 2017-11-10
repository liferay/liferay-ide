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

package com.liferay.ide.service.core.model.internal;

/**
 * @author Gregory Amerson
 */
public class RelationshipObject {

	public RelationshipObject() {
	}

	public RelationshipObject(String fromName, String toName) {
		_fromName = fromName;
		_toName = toName;
	}

	public String getFromName() {
		return _fromName;
	}

	public String getToName() {
		return _toName;
	}

	public void setFromName(String fromName) {
		_fromName = fromName;
	}

	public void setToName(String toName) {
		_toName = toName;
	}

	private String _fromName;
	private String _toName;

}