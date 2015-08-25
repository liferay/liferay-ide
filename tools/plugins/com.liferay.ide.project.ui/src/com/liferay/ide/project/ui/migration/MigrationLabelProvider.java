package com.liferay.ide.project.ui.migration;

import blade.migrate.api.Problem;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;

public class MigrationLabelProvider extends StyledCellLabelProvider {

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		StyledString text = new StyledString();


		if (element instanceof String) {
			text.append(element.toString());
		}
		else if (element instanceof Problem ) {
			text.append((String) ((Problem)element).title);
		}

		cell.setText(text.toString());
	    cell.setStyleRanges(text.getStyleRanges());

		super.update(cell);
	}
}
