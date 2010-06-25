package com.liferay.ide.eclipse.layouttpl.ui.model;

import java.util.ArrayList;
import java.util.List;

public class PortletLayout extends ModelElement {

	public static final String COLUMN_ADDED_PROP = "PortletLayout.ColumnAdded";

	public static final String COLUMN_REMOVED_PROP = "PortletLayout.ColumnRemoved";

	public PortletLayout() {
		super();
	}

	protected List<ModelElement> columns = new ArrayList<ModelElement>();

	public boolean addColumn(PortletColumn s) {
		if (s != null && columns.add(s)) {
			firePropertyChange(COLUMN_ADDED_PROP, null, s);
			return true;
		}

		return false;
	}

	public List<ModelElement> getColumns() {
		return columns;
	}

	public boolean removeColumn(PortletColumn s) {
		if (s != null && columns.remove(s)) {
			firePropertyChange(COLUMN_REMOVED_PROP, null, s);
			return true;
		}

		return false;
	}
}
