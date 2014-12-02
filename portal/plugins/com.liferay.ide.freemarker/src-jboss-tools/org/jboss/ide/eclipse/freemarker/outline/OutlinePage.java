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


import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.jboss.ide.eclipse.freemarker.Messages;
import org.jboss.ide.eclipse.freemarker.Plugin;
import org.jboss.ide.eclipse.freemarker.configuration.ConfigurationManager;
import org.jboss.ide.eclipse.freemarker.configuration.ContextValue;
import org.jboss.ide.eclipse.freemarker.editor.Editor;
import org.jboss.ide.eclipse.freemarker.model.Interpolation;
import org.jboss.ide.eclipse.freemarker.model.Item;
import org.jboss.ide.eclipse.freemarker.model.ItemSet;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class OutlinePage extends ContentOutlinePage implements IDoubleClickListener {
	private Editor fEditor;
	private Object fInput;
	private OutlineLabelProvider fLabelProvider;

	public OutlinePage(Editor anEditor) {
		fEditor = anEditor;
	}

	/**
	 * @see org.eclipse.ui.part.IPart#createControl(Composite)
	 */
	public void createControl(Composite aParent) {
		super.createControl(aParent);

		fLabelProvider = new OutlineLabelProvider();

		// Init tree viewer
		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new OutlineContentProvider(fEditor));
		viewer.setLabelProvider(fLabelProvider);
		viewer.addSelectionChangedListener(this);
		viewer.addDoubleClickListener(this);
		if (fInput != null) {
			viewer.setInput(fInput);
		}
		// Refresh outline according to initial cursor position
		update(null);
	}

	public void setInput(Object aInput) {
		fInput = aInput;
		update(null);
	}

	private ItemSet selectedItemSet;
	private Item selectedItem;
	public void update(Item item) {
		if ((null == item && null != selectedItem)
				|| null == selectedItem && null != item
				|| (null != item && null != selectedItem && !item.equals(selectedItem))) {
			if (null == selectedItemSet || !fEditor.getItemSet().equals(selectedItemSet)) {
				TreeViewer viewer = getTreeViewer();
				if (viewer != null) {
					Control control = viewer.getControl();
					if (control != null && !control.isDisposed()) {
						viewer.removeSelectionChangedListener(this);
						control.setRedraw(false);
						viewer.setInput(fInput);
						control.setRedraw(true);
						viewer.expandToLevel(3);
					}
				}
				selectedItemSet = fEditor.getItemSet();
			}
			select(item);
		}
		selectedItem = item;
		selectedItemSet = fEditor.getItemSet();
	}

	public void select (Item item) {
		if (null != item && item.isEndItem())
			item = item.getStartItem();
		if (null != getTreeViewer()) {
			if (null == item)
				getTreeViewer().setSelection(
						new StructuredSelection(new Object[0]), true);
			else if (null != item)
				getTreeViewer().setSelection(
						new StructuredSelection(item), true);
		}
	}

	/**
	 * @see org.eclipse.ui.part.Page#dispose()
	 */
	public void dispose() {
		super.dispose();
	}

	public void doubleClick(DoubleClickEvent event) {
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		Item item = (Item) selection.getFirstElement();
		if (null != item) {
			fEditor.select(item);
		}
	}

	public void makeContributions(IMenuManager menuManager, IToolBarManager toolBarManager, IStatusLineManager statusLineManager) {
		MenuManager menuMgr = new MenuManager();
		Menu menu = menuMgr.createContextMenu(getTreeViewer().getTree());
		
		menuMgr.add(new SetContextEntryAction(this));
		getTreeViewer().getTree().setMenu(menu);
		super.makeContributions(menuManager, toolBarManager, statusLineManager);
	}

	public class SetContextEntryAction extends Action {
		private OutlinePage outlinePage;

		public SetContextEntryAction (OutlinePage outlinePage) {
			this.outlinePage = outlinePage;
			this.setText(Messages.OutlinePage_SetContextClassEntryAction);
			this.setEnabled(true);
		}

		public void runWithEvent(Event event) {
			Tree tree = getTreeViewer().getTree();
			TreeItem[] items = tree.getSelection();
			Document doc = null;
			Interpolation interpolation = null;
			for (int i=0; i<items.length; i++) {
				TreeItem item = items[i];
				if (item.getData() instanceof Interpolation) {
					interpolation = (Interpolation) item.getData();
					try {
						SelectionDialog sd = JavaUI.createTypeDialog(new Shell(), null, SearchEngine.createWorkspaceScope(),
								IJavaElementSearchConstants.CONSIDER_CLASSES_AND_INTERFACES, false);
						sd.open();
						Object[] objects = sd.getResult();
						if (null != objects && objects.length > 0) {
							ConfigurationManager configuration = ConfigurationManager.getInstance(interpolation.getResource().getProject());
							IType type = (IType) objects[0];
							try {
								configuration.addContextValue(
										new ContextValue(interpolation.getFirstToken(), 
												configuration.getClass(type.getFullyQualifiedName()), null), interpolation.getResource());
							}
							catch (ClassNotFoundException e) {
								Plugin.log(e);
							}
						}
					}
					catch (JavaModelException jme) {
						Plugin.error(jme);
					}
				}
			}
		}
	}
}