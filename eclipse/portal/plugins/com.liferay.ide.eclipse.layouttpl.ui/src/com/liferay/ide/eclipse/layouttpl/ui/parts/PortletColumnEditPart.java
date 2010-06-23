
package com.liferay.ide.eclipse.layouttpl.ui.parts;

import com.liferay.ide.eclipse.layouttpl.ui.draw2d.ColumnFigure;
import com.liferay.ide.eclipse.layouttpl.ui.model.ModelElement;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletColumn;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.graphics.Color;


public class PortletColumnEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener {


	public void activate() {
		if (!isActive()) {
			super.activate();
			((ModelElement) getModel()).addPropertyChangeListener(this);
		}
	}


	protected void createEditPolicies() {
		// allow removal of the associated model element
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new PortletColumnComponentEditPolicy());
	}

	protected IFigure createFigure() {
		IFigure f = createFigureForModel();
		f.setOpaque(true); // non-transparent figure
		f.setBackgroundColor(new Color(null, 232, 232, 232));
		return f;
	}


	protected IFigure createFigureForModel() {
		if (getModel() instanceof PortletColumn) {
			RoundedRectangle rect = new ColumnFigure();
			rect.setPreferredSize(150, 100);
			// rect.setMinimumSize(new Dimension(150, 50));
			return rect;
		}
		else {
			throw new IllegalArgumentException();
		}
	}


	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((ModelElement) getModel()).removePropertyChangeListener(this);
		}
	}

	protected PortletColumn getCastedModel() {
		return (PortletColumn) getModel();
	}



	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (PortletColumn.SIZE_PROP.equals(prop) || PortletColumn.LOCATION_PROP.equals(prop)) {
			refreshVisuals();
		}
	}

	protected void refreshVisuals() {
		// ((GraphicalEditPart) getParent()).setLayoutConstraint(this,
		// getFigure(), new GridData(
		// GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_FILL, true,
		// true, 1, 1));

		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), new GridData());

		// ((GraphicalEditPart) getParent()).setLayoutConstraint(this,
		// getFigure(), bounds);
	}
}
