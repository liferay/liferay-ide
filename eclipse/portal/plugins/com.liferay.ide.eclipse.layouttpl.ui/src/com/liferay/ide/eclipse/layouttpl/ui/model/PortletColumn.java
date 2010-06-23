package com.liferay.ide.eclipse.layouttpl.ui.model;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Image;

public class PortletColumn extends ModelElement {

	public static final String SIZE_PROP = "PortletColumn.size";
	public static final String LOCATION_PROP = "PortletColumn.location";

	protected Point location;

	protected Dimension size;

	public Image getIcon() {
		// TODO Implement getIcon method on class PortletColumn
		System.out.println("PortletColumn.getIcon");
		return null;
	}

	public void setLocation(Point newLocation) {
		if (newLocation == null) {
			throw new IllegalArgumentException();
		}
		location.setLocation(newLocation);
		firePropertyChange(LOCATION_PROP, null, location);
	}

	public void setSize(Dimension newSize) {
		if (newSize != null) {
			size.setSize(newSize);
			firePropertyChange(SIZE_PROP, null, size);
		}
	}

	public Point getLocation() {
		return location.getCopy();
	}

	public Dimension getSize() {
		return size.getCopy();
	}

}
