
package com.liferay.ide.eclipse.layouttpl.ui.parts;

import com.liferay.ide.eclipse.layouttpl.ui.draw2d.ColumnFigure;
import com.liferay.ide.eclipse.layouttpl.ui.model.ModelElement;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.eclipse.layouttpl.ui.policies.PortletColumnComponentEditPolicy;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

public class PortletColumnEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener {

	public PortletColumnEditPart() {
		super();
	}

	public void activate() {
		if (!isActive()) {
			super.activate();
			((ModelElement) getModel()).addPropertyChangeListener(this);
		}
	}

	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((ModelElement) getModel()).removePropertyChangeListener(this);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (PortletColumn.SIZE_PROP.equals(prop) || PortletColumn.LOCATION_PROP.equals(prop)) {
			refreshVisuals();
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
			rect.setCornerDimensions(new Dimension(20, 20));
			return rect;
		}
		else {
			throw new IllegalArgumentException();
		}
	}

	protected PortletColumn getCastedModel() {
		return (PortletColumn) getModel();
	}

	protected void refreshVisuals() {
		// ColumnFigure columnFigure = (ColumnFigure) getFigure();
		// columnFigure.setPreferredSize(150, 100);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd.heightHint = 50;
		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), gd);
	}

}
