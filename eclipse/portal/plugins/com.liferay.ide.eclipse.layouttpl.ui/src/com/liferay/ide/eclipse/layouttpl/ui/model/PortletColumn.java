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
package com.liferay.ide.eclipse.layouttpl.ui.model;

import org.eclipse.swt.graphics.Image;

public class PortletColumn extends ModelElement {

	public static final String WEIGHT_PROP = "PortletColumn.weight";

	public static final int DEFAULT_WEIGHT = -1;

	// public static final String SIZE_PROP = "PortletColumn.size";
	// public static final String LOCATION_PROP = "PortletColumn.location";

	protected int weight;

	protected int numId = 0;

	protected boolean first = false;
	protected boolean last = false;

	// protected Point location;

	// protected Dimension size;

	public PortletColumn() {
		super();

		this.weight = DEFAULT_WEIGHT;
	}

	public Image getIcon() {
		// TODO Implement getIcon method on class PortletColumn
		System.out.println("PortletColumn.getIcon");
		return null;
	}

	// public void setLocation(Point newLocation) {
	// if (newLocation == null) {
	// throw new IllegalArgumentException();
	// }
	// location.setLocation(newLocation);
	// firePropertyChange(LOCATION_PROP, null, location);
	// }

	// public void setSize(Dimension newSize) {
	// if (newSize != null) {
	// size.setSize(newSize);
	// firePropertyChange(SIZE_PROP, null, size);
	// }
	// }

	// public Point getLocation() {
	// return location.getCopy();
	// }

	// public Dimension getSize() {
	// return size.getCopy();
	// }


	@Override
	public void removeChild(ModelElement child) {
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		int oldValue = this.weight;
		this.weight = weight;
		firePropertyChange(WEIGHT_PROP, oldValue, this.weight);
	}

	public int getNumId() {
		return numId;
	}

	public void setNumId(int numId) {
		this.numId = numId;
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

}
