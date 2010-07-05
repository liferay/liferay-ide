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

import com.liferay.ide.eclipse.templates.core.ITemplateOperation;
import com.liferay.ide.eclipse.templates.core.TemplatesCore;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

public class LayoutTplDiagram extends ModelElement implements PropertyChangeListener {

	public static final String ROW_ADDED_PROP = "LayoutTplDiagram.RowAdded";

	public static final String ROW_REMOVED_PROP = "LayoutTplDiagram.RowRemoved";

	public LayoutTplDiagram() {
		super();
	}

	protected List<ModelElement> rows = new ArrayList<ModelElement>();

	public boolean addRow(PortletLayout newRow, int index) {
		if (newRow != null) {
			if (index < 0) {
				rows.add(newRow);
			}
			else {
				rows.add(index, newRow);
			}

			newRow.addPropertyChangeListener(this);

			updateColumns();

			firePropertyChange(ROW_ADDED_PROP, null, newRow);

			return true;
		}

		return false;
	}

	protected void updateColumns() {
		int numIdCount = 1;
		for (ModelElement row : rows) {
			List<ModelElement> cols = ((PortletLayout) row).getColumns();
			for (int i = 0; i < cols.size(); i++) {
				PortletColumn col = ((PortletColumn) cols.get(i));
				col.setNumId(numIdCount++);
				if (i == 0 && cols.size() > 1) {
					col.setFirst(true);
				}
				else if (cols.size() > 1 && i == (cols.size() - 1)) {
					col.setLast(true);
				}
			}
		}
	}

	public List<ModelElement> getRows() {
		return rows;
	}

	public boolean removeRow(PortletLayout existingRow) {
		if (existingRow != null && rows.remove(existingRow)) {
			firePropertyChange(ROW_REMOVED_PROP, null, existingRow);
			return true;
		}

		return false;
	}

	public void loadFromFile(IFile file) {
		// TODO Implement loadFromFile method on class LayoutTplDiagram
		System.out.println("LayoutTplDiagram.loadFromFile");

	}

	@Override
	public void removeChild(ModelElement child) {
		if (rows.contains(child)) {
			removeRow((PortletLayout) child);
		}
	}

	public void addRow(PortletLayout newRow) {
		addRow(newRow, -1);
	}

	public void saveToFile(IFile file, IProgressMonitor monitor) {
		ITemplateOperation templateOperation = TemplatesCore.getTemplateOperation("layouttpl.tpl");
		templateOperation.setOutputFile(file);
		try {
			VelocityContext ctx = templateOperation.getContext();
			ctx.put("root", this);
			String name = file.getFullPath().removeFileExtension().lastSegment();
			ctx.put("templateName", name);
			templateOperation.execute(monitor);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (PortletLayout.COLUMN_ADDED_PROP.equals(prop) || PortletLayout.COLUMN_REMOVED_PROP.equals(prop)) {
			updateColumns();
		}
	}
}
