/*
 * JBoss by Red Hat
 * Copyright 2006-2009, Red Hat Middleware, LLC, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ide.eclipse.freemarker.outline;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.jboss.ide.eclipse.freemarker.editor.Editor;
import org.jboss.ide.eclipse.freemarker.model.Item;
import org.jboss.ide.eclipse.freemarker.model.ItemSet;
import org.jboss.ide.eclipse.freemarker.model.MacroDirective;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class OutlineContentProvider implements ITreeContentProvider {
	private Editor fEditor;

	public OutlineContentProvider(Editor anEditor) {
		fEditor = anEditor;
	}

	public void inputChanged(
		Viewer aViewer,
		Object anOldInput,
		Object aNewInput) {
	}

	public boolean isDeleted(Object anElement) {
		return false;
	}

	public void dispose() {
	}

	public Object[] getElements(Object inputElement) {
		ItemSet itemSet = null;
		if (inputElement instanceof ItemSet)
			itemSet = (ItemSet) inputElement;
		else
			itemSet = fEditor.getItemSet();
		List rootItems = new ArrayList();
		
		rootItems.addAll(fEditor.getItemSet().getMacroDefinitions());
		Item[] items = fEditor.getItemSet().getRootItems();
		for (int i=0; i<items.length; i++) {
			if (!(items[i] instanceof MacroDirective)) 
				rootItems.add(items[i]);
		}
		return rootItems.toArray();
	}

	public Object[] getChildren(Object anElement) {
		if (anElement instanceof Item) {
			if (anElement instanceof MacroDirective) return null;
			Object[] items = ((Item) anElement).getChildItems().toArray(
					new Item[((Item) anElement).getChildItems().size()]);
			List l = new ArrayList(items.length);
			for (int i=0; i<items.length; i++) {
				if (!(items[i] instanceof MacroDirective))
					l.add(items[i]);
			}
			return l.toArray();
		}
		else
			return null;
	}

	public Object getParent(Object anElement) {
		if (anElement instanceof Item)
			return ((Item) anElement).getParentItem();
		else
			return null;
	}

	public boolean hasChildren(Object anElement) {
		if (anElement instanceof Item)
			if (anElement instanceof MacroDirective) return false;
			else return ((Item) anElement).getChildItems().size() > 0;
		else
			return false;
	}
}