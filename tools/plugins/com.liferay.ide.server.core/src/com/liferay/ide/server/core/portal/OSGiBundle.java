/*******************************************************************************
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
 *
 *******************************************************************************/
package com.liferay.ide.server.core.portal;

public class OSGiBundle {
    final long id;
    final String symbolicName;

    public OSGiBundle(long id, String symbolicName) {
        this.id = id;
        this.symbolicName = symbolicName;
    }

    @Override
    public String toString() {
        return id + " " + symbolicName;
    }

    public String getSymbolicName() {
        return symbolicName;
    }

    public String getId() {
        return id + "";
    }

    public String getState() {
        return "no state";
    }

    public String getVersion() {
        return "0.0.0";
    }
}
