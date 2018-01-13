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

package com.liferay.ide.kaleo.ui.editor;

import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdfill;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdhhint;

import com.liferay.ide.kaleo.core.model.IScriptable;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.ui.PropertyEditorPart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.renderers.swt.PropertyEditorRenderer;
import org.eclipse.sapphire.ui.renderers.swt.PropertyEditorRendererFactory;
import org.eclipse.sapphire.ui.renderers.swt.ValuePropertyEditorRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.internal.PartPane;
import org.eclipse.ui.internal.PartSite;
import org.eclipse.ui.internal.WorkbenchPartReference;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class ScriptPropertyEditorRenderer extends ValuePropertyEditorRenderer {

	public ScriptPropertyEditorRenderer(SapphireRenderingContext context, PropertyEditorPart part) {
		super(context, part);

		IScriptable scriptable = context.getPart().getLocalModelElement().nearest(IScriptable.class);

		ScriptLanguageModelPropertyListener listener = new ScriptLanguageModelPropertyListener();

		scriptable.addListener(listener, "ScriptLanguage");
	}

	public static final class Factory extends PropertyEditorRendererFactory {

		@Override
		public PropertyEditorRenderer create(SapphireRenderingContext context, PropertyEditorPart part) {
			return new ScriptPropertyEditorRenderer(context, part);
		}

		@Override
		public boolean isApplicableTo(PropertyEditorPart propertyEditorDefinition) {
			return propertyEditorDefinition.getProperty() instanceof ValueProperty;
		}

	}

	@Override
	protected boolean canScaleVertically() {
		return true;
	}

	@Override
	protected void createContents(Composite parent) {
		PropertyEditorPart part = getPart();

		CreateMainCompositeDelegate compositeDelegate = new CreateMainCompositeDelegate(part) {

			@Override
			public boolean canScaleVertically() {
				return true;
			}

		};

		Composite scriptEditorParent = createMainComposite(parent, compositeDelegate);

		context.adapt(scriptEditorParent);

		scriptEditorParent.setLayout(new FillLayout(SWT.HORIZONTAL));

		scriptEditorParent.setLayoutData(gdhhint(gdfill(), 300));

		_textEditor = new TextEditor();

		IFileEditorInput fileInput = new FileEditorInput(
			context.getPart().getLocalModelElement().adapt(IFile.class).getParent().getFile(new Path("review.txt")));

		IEditorReference ref = new ScriptEditorReference(_textEditor, fileInput);

		IWorkbench workBench = PlatformUI.getWorkbench();

		IEditorSite site = new ScriptEditorSite(ref, _textEditor, workBench.getActiveWorkbenchWindow().getActivePage());

		try {
			_textEditor.init(site, fileInput);
		}
		catch (PartInitException pie) {
			pie.printStackTrace();
		}

		_textEditor.createPartControl(scriptEditorParent);
	}

	@Override
	protected void handleFocusReceivedEvent() {
		super.handleFocusReceivedEvent();
	}

	@Override
	protected void handlePropertyChangedEvent() {
		super.handlePropertyChangedEvent();
	}

	protected class ScriptLanguageModelPropertyListener extends ModelPropertyListener {

		@Override
		public void handlePropertyChangedEvent(ModelPropertyChangeEvent event) {
		}

	}

	private ITextEditor _textEditor;

	private class ScriptEditorReference extends WorkbenchPartReference implements IEditorReference {

		public ScriptEditorReference(IEditorPart editor, IEditorInput input) {
			_editor = editor;
			_editorInput = input;
		}

		public IEditorPart getEditor(boolean restore) {
			return _editor;
		}

		public IEditorInput getEditorInput() throws PartInitException {
			return _editorInput;
		}

		public String getFactoryId() {
			return null;
		}

		public String getName() {
			return null;
		}

		public IWorkbenchPage getPage() {
			IWorkbench workBench = PlatformUI.getWorkbench();

			return workBench.getActiveWorkbenchWindow().getActivePage();
		}

		@Override
		protected PartPane createPane() {
			return null;
		}

		@Override
		protected IWorkbenchPart createPart() {
			return null;
		}

		private IEditorPart _editor;
		private IEditorInput _editorInput;

	}

	private class ScriptEditorSite extends PartSite implements IEditorSite {

		public ScriptEditorSite(IWorkbenchPartReference ref, IWorkbenchPart part, IWorkbenchPage page) {
			super(ref, part, page);
		}

		public IEditorActionBarContributor getActionBarContributor() {
			return null;
		}

		@Override
		public IActionBars getActionBars() {
			IWorkbenchPart wkPart = getPage().getActivePart();

			PartSite partSite = (PartSite)wkPart.getSite();

			IActionBars bars = partSite.getActionBars();

			return bars;
		}

		@Override
		public Shell getShell() {
			IWorkbenchPart wkPart = getPage().getActivePart();

			return wkPart.getSite().getShell();
		}

		public void registerContextMenu(
			MenuManager menuManager, ISelectionProvider selectionProvider, boolean includeEditorInput) {
		}

		public void registerContextMenu(
			String menuId, MenuManager menuManager, ISelectionProvider selectionProvider, boolean includeEditorInput) {
		}

	}

}