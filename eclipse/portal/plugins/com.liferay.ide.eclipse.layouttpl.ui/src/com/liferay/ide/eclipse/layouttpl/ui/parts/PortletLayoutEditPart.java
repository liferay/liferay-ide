package com.liferay.ide.eclipse.layouttpl.ui.parts;

import com.liferay.ide.eclipse.layouttpl.ui.model.ModelElement;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletLayout;
import com.liferay.ide.eclipse.layouttpl.ui.policies.PortletLayoutLayoutEditPolicy;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Panel;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.SWT;


public class PortletLayoutEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener {

	public static final int LAYOUT_MARGIN = 10;

	protected Panel layoutPanel;

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

	@Override
	protected IFigure createFigure() {
		layoutPanel = new Panel() {

			@Override
			public void paint(Graphics graphics) {
				super.paint(graphics);
			}

		};
		layoutPanel.setOpaque(false);
		layoutPanel.setBorder(new MarginBorder(LAYOUT_MARGIN));
		layoutPanel.setBackgroundColor(ColorConstants.lightBlue);
		layoutPanel.setLayoutManager(new GridLayout(1, false));

		return layoutPanel;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new PortletLayoutLayoutEditPolicy());
	}


	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Implement propertyChange method on class PropertyChangeListener
		System.out.println("PropertyChangeListener.propertyChange");

	}

	protected PortletLayout getCastedModel() {
		return (PortletLayout) getModel();
	}

	protected List<ModelElement> getModelChildren() {
		return getCastedModel().getColumns(); // return a list of columns
	}

	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();

		((GraphicalEditPart) getParent()).setLayoutConstraint(this, layoutPanel, new GridData(
			SWT.FILL, SWT.FILL, true, false, 1, 1));
	}

}
