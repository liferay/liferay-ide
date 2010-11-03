/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.ui;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * @author Greg Amerson
 */
public class DebugGroup extends Group {

	public DebugGroup(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		Point p = super.computeSize(wHint, hHint, changed);
		System.out.println("computeSize " + p);
		return p;
	}

	@Override
	protected void checkSubclass() {
	}

}
