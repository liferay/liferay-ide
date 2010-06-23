
package com.liferay.ide.eclipse.layouttpl.ui.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;

public class LayoutTplDiagram extends ModelElement {

	public static final String CHILD_ADDED_PROP = "LayoutTplDiagram.ChildAdded";
	public static final String CHILD_REMOVED_PROP = "LayoutTplDiagram.ChildRemoved";
	private List<ModelElement> elements = new ArrayList<ModelElement>();

	public boolean addChild(ModelElement s) {
		if (s != null && elements.add(s)) {
			firePropertyChange(CHILD_ADDED_PROP, null, s);
			return true;
		}
		return false;
	}

	public List<ModelElement> getChildren() {
		return elements;
	}

	public boolean removeChild(ModelElement s) {
		if (s != null && elements.remove(s)) {
			firePropertyChange(CHILD_REMOVED_PROP, null, s);
			return true;
		}
		return false;
	}

	public void loadFromFile(IFile file) {
		// TODO Implement loadFromFile method on class LayoutTplDiagram
		System.out.println("LayoutTplDiagram.loadFromFile");

	}
}
