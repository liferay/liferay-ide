/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
 */

package com.liferay.ide.portlet.ui.editor;

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.model.IModelChangedEvent;
import com.liferay.ide.core.model.IModelChangedListener;
import com.liferay.ide.portlet.core.PluginPackageModel;
import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.ui.editor.InputContext;
import com.liferay.ide.ui.editor.InputContextManager;
import com.liferay.ide.ui.editor.PluginPackageInputContextManager;
import com.liferay.ide.ui.form.IDEFormEditor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.PropertiesFileEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.forms.widgets.BusyIndicator;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@SuppressWarnings({"restriction", "rawtypes"})
public class PluginPackageEditor extends IDEFormEditor implements IModelChangedListener {

	public static final String EDITOR_ID = "com.liferay.ide.eclipse.portlet.ui.editor.pluginpackage";

	public void contextRemoved(InputContext context) {
	}

	@Override
	public void dispose() {
		super.dispose();

		if (_fileChangeListener != null) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();

			workspace.removeResourceChangeListener(_fileChangeListener);

			_fileChangeListener = null;
		}
	}

	@Override
	public void editorContextAdded(InputContext context) {
	}

	@Override
	public Object getAdapter(Class adapterClass) {
		Object adapter = super.getAdapter(adapterClass);

		if (adapter == null) {
			adapter = editor.getAdapter(adapterClass);
		}

		return adapter;
	}

	@Override
	public IFileEditorInput getEditorInput() {
		return (IFileEditorInput)super.getEditorInput();
	}

	public IFile getFile() {
		IEditorInput editorInput = getEditorInput();

		if (editorInput instanceof FileEditorInput) {
			FileEditorInput fileEditorInput = (FileEditorInput)editorInput;

			return fileEditorInput.getFile();
		}

		return null;
	}

	public IPath getPortalDir() {
		try {
			IFile file = getEditorInput().getFile();

			ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, file.getProject());

			ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

			return portal.getAppServerPortalDir();
		}
		catch (Exception e) {
			return null;
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		Assert.isLegal(editorInput instanceof IFileEditorInput, "Invalid Input: Must be IFileEditorInput");

		super.init(site, editorInput);

		setPartName(editorInput.getName());
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	public void modelChanged(IModelChangedEvent event) {
		if (ignoreModelChanges) {
			return;
		}

		PluginPackageModel model = (PluginPackageModel)getModel();

		IDocument doc = model.getDocument();

		IDocumentProvider provider = editor.getDocumentProvider();

		IDocument document = provider.getDocument(getEditorInput());

		document.set(doc.get());
	}

	public void monitoredFileAdded(IFile monitoredFile) {
	}

	public boolean monitoredFileRemoved(IFile monitoredFile) {
		return false;
	}

	protected void addDependenciesFormPage() {
		try {
			int index = addPage(new DependenciesFormPage(this));

			setPageText(index, Msgs.dependencies);
		}
		catch (PartInitException pie) {
			PortletUIPlugin.logError(pie);
		}
	}

	@Override
	protected void addPages() {
		addPluginPackageFormPage();

		// addDependenciesFormPage();

		_addPropertiesEditorPage();

		createFileChangeListener();
	}

	protected void addPluginPackageFormPage() {
		try {
			int index = addPage(new PluginPackageFormPage(this));

			setPageText(index, Msgs.properties);
		}
		catch (PartInitException pie) {
			PortletUIPlugin.logError(pie);
		}
	}

	protected void createFileChangeListener() {
		_fileChangeListener = new IResourceChangeListener() {

			public void resourceChanged(IResourceChangeEvent event) {
				handleFileChangedEvent(event);
			}

		};

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		workspace.addResourceChangeListener(_fileChangeListener, IResourceChangeEvent.POST_CHANGE);
	}

	@Override
	protected InputContextManager createInputContextManager() {
		PluginPackageInputContextManager manager = new PluginPackageInputContextManager(this);

		// manager.setUndoManager(new PluginUndoManager(this));

		return manager;
	}

	@Override
	protected void createResourceContexts(InputContextManager manager, IFileEditorInput input) {
		IFile file = input.getFile();

		if (file.exists()) {
			IEditorInput in = new FileEditorInput(file);

			manager.putContext(in, new PluginPackageInputContext(this, in, true));
		}

		manager.monitorFile(file);
	}

	@Override
	protected String getEditorID() {
		return EDITOR_ID;
	}

	@Override
	protected InputContext getInputContext(Object object) {
		InputContext context = null;

		if (object instanceof IFile) {
			context = fInputContextManager.findContext((IFile)object);
		}

		return context;
	}

	protected void handleFileChangedEvent(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();

		if ((delta != null) && (getFile() != null)) {
			IResourceDelta localDelta = delta.findMember(getFile().getFullPath());

			if (localDelta != null) {
				IWorkbench workbench = PlatformUI.getWorkbench();

				Display display = workbench.getDisplay();

				Runnable run = new Runnable() {

					public void run() {
						if (localDelta.getKind() == IResourceDelta.REMOVED) {
							IWorkbenchPage page = getSite().getPage();

							page.closeEditor(PluginPackageEditor.this, false);
						}
					}

				};

				display.asyncExec(run);
			}
		}
	}

	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);

		if ((lastPageIndex == 1) && (newPageIndex != 1)) {
			IDocumentProvider provider = editor.getDocumentProvider();

			IDocument doc = provider.getDocument(getEditorInput());

			String props = doc.get();

			try (InputStream inputStream = new ByteArrayInputStream(props.getBytes())) {
				ignoreModelChanges = true;

				if (getLastDirtyState()) {
					PluginPackageModel pluginModel = (PluginPackageModel)getModel();

					pluginModel.load(inputStream, false);
				}

				ignoreModelChanges = false;
			}
			catch (CoreException | IOException e) {
				PortletUIPlugin.logError(e);
			}
		}

		lastPageIndex = newPageIndex;
	}

	protected BusyIndicator busyLabel;

	/**
	 * The properties text editor.
	 */
	protected PropertiesFileEditor editor;

	protected boolean ignoreModelChanges = false;
	protected int lastPageIndex = -1;
	protected PluginPackageModel model;

	private void _addPropertiesEditorPage() {
		editor = new PropertiesFileEditor();

		PluginPackageModel pluginModel = (PluginPackageModel)getModel();

		pluginModel.addModelChangedListener(this);

		// editor.setEditorPart(this);

		int index;

		try {
			index = addPage(editor, getEditorInput());

			setPageText(index, Msgs.source);
		}
		catch (PartInitException pie) {
			PortletUIPlugin.logError(pie);
		}
	}

	private IResourceChangeListener _fileChangeListener;

	private static class Msgs extends NLS {

		public static String dependencies;
		public static String properties;
		public static String source;

		static {
			initializeMessages(PluginPackageEditor.class.getName(), Msgs.class);
		}

	}

}