
package com.liferay.ide.eclipse.layouttpl.ui.parts;

import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.eclipse.layouttpl.ui.model.ModelElement;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Panel;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;


public class LayoutTplDiagramEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener {


	public void activate() {
		if (!isActive()) {
			super.activate();
			((ModelElement) getModel()).addPropertyChangeListener(this);
		}
	}


	protected void createEditPolicies() {
		// disallows the removal of this edit part from its parent
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		// handles constraint changes (e.g. moving and/or resizing) of model
		// elements
		// and creation of new model elements
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new ColumnLayoutEditPolicy());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		Panel backgroundPanel = new Panel();
		backgroundPanel.setBackgroundColor(new Color(null, 10, 10, 10));
		backgroundPanel.setBorder(new MarginBorder(20));
		backgroundPanel.setLayoutManager(new GridLayout(1, false));

		Panel layoutPanel = new Panel();
		layoutPanel.setBackgroundColor(new Color(null, 171, 171, 171));
		layoutPanel.setBorder(new MarginBorder(10));
		layoutPanel.setLayoutManager(new GridLayout(3, false));

		// backgroundPanel.add(layoutPanel, new GridData());

		return layoutPanel;
	}

	protected Display getDisplay() {
		return this.getViewer().getControl().getDisplay();
	}


	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((ModelElement) getModel()).removePropertyChangeListener(this);
		}
	}

	private LayoutTplDiagram getCastedModel() {
		return (LayoutTplDiagram) getModel();
	}


	protected List<ModelElement> getModelChildren() {
		return getCastedModel().getChildren(); // return a list of shapes
	}


	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();

		if (LayoutTplDiagram.CHILD_ADDED_PROP.equals(prop) || LayoutTplDiagram.CHILD_REMOVED_PROP.equals(prop)) {
			refreshChildren();
		}
	}

}
