
package com.liferay.ide.eclipse.layouttpl.ui.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;

public class LayoutTplDiagram extends ModelElement {

	public static final String ROW_ADDED_PROP = "LayoutTplDiagram.RowAdded";

	public static final String ROW_REMOVED_PROP = "LayoutTplDiagram.RowRemoved";

	public LayoutTplDiagram() {
		super();
	}

	protected List<ModelElement> rows = new ArrayList<ModelElement>();

	public boolean addRow(PortletLayout s) {
		if (s != null && rows.add(s)) {
			firePropertyChange(ROW_ADDED_PROP, null, s);
			return true;
		}

		return false;
	}

	public List<ModelElement> getRows() {
		return rows;
	}

	public boolean removeRow(PortletLayout s) {
		if (s != null && rows.remove(s)) {
			firePropertyChange(ROW_REMOVED_PROP, null, s);
			return true;
		}

		return false;
	}

	public void loadFromFile(IFile file) {
		// TODO Implement loadFromFile method on class LayoutTplDiagram
		System.out.println("LayoutTplDiagram.loadFromFile");

	}
}
