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

import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gd;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdfill;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdvalign;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdvfill;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.glayout;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.glspacing;

import com.liferay.ide.kaleo.core.model.IScriptable;
import com.liferay.ide.kaleo.core.model.ScriptLanguageType;

import org.eclipse.core.internal.content.ContentTypeManager;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.ui.PropertyEditorPart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.assist.internal.PropertyEditorAssistDecorator;
import org.eclipse.sapphire.ui.renderers.swt.PropertyEditorRenderer;
import org.eclipse.sapphire.ui.renderers.swt.PropertyEditorRendererFactory;
import org.eclipse.sapphire.ui.renderers.swt.ValuePropertyEditorRenderer;
import org.eclipse.sapphire.ui.swt.renderer.SapphireToolBarActionPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.PartPane;
import org.eclipse.ui.internal.PartSite;
import org.eclipse.ui.internal.WorkbenchPartReference;
import org.eclipse.ui.internal.registry.EditorDescriptor;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class CodePropertyEditorRenderer extends ValuePropertyEditorRenderer {

	class ScriptLanguageModelPropertyListener extends ModelPropertyListener {

		@Override
		public void handlePropertyChangedEvent(ModelPropertyChangeEvent event) {
		}

	}

	public CodePropertyEditorRenderer(SapphireRenderingContext context, PropertyEditorPart part) {
		super(context, part);

		IScriptable scriptable = context.getPart().getLocalModelElement().nearest(IScriptable.class);

		ScriptLanguageModelPropertyListener listener = new ScriptLanguageModelPropertyListener();

		scriptable.addListener(listener, "ScriptLanguage");
	}

	public static final class Factory extends PropertyEditorRendererFactory {

		@Override
		public PropertyEditorRenderer create(SapphireRenderingContext context, PropertyEditorPart part) {
			return new CodePropertyEditorRenderer(context, part);
		}

		@Override
		public boolean isApplicableTo(PropertyEditorPart propertyEditorDefinition) {
			return (propertyEditorDefinition.getProperty() instanceof ValueProperty);
		}

	}

	@Override
	protected boolean canScaleVertically() {
		return true;
	}

	@Override
	protected void createContents(Composite parent) {
		PropertyEditorPart part = getPart();
		Composite scriptEditorParent = createMainComposite(
			parent, new CreateMainCompositeDelegate(part) {

				@Override
				public boolean canScaleVertically() {
					return true;
				}

			});

		context.adapt(scriptEditorParent);

		int textFieldParentColumns = 1;
		SapphireToolBarActionPresentation toolBarActionsPresentation = new SapphireToolBarActionPresentation(
			getActionPresentationManager());

		boolean actionsToolBarNeeded = toolBarActionsPresentation.hasActions();

		if (actionsToolBarNeeded || true)textFieldParentColumns++;

		scriptEditorParent.setLayout(glayout(textFieldParentColumns, 0, 0, 0, 0));

		Composite nestedComposite = new Composite(scriptEditorParent, SWT.NONE);

		nestedComposite.setLayoutData(gdfill());
		nestedComposite.setLayout(glspacing(glayout(2, 0, 0), 2));
		this.context.adapt(nestedComposite);

		PropertyEditorAssistDecorator decorator = createDecorator(nestedComposite);

		decorator.control().setLayoutData(gdvalign(gd(), SWT.TOP));
		decorator.addEditorControl(nestedComposite);

		/*
		 * scriptEditorParent.setLayout( new FillLayout( SWT.HORIZONTAL ) );
		 * scriptEditorParent.setLayoutData( gdhhint( gdfill(), 300 ) );
		 */
		PropertyEditorInput editorInput = new PropertyEditorInput(
			part.getLocalModelElement(), (ValueProperty)part.getProperty());

		try {
			ScriptLanguageType scriptLang = context.getPart().getLocalModelElement().nearest(IScriptable.class)
				.getScriptLanguage().getContent(false);

			String fileName =
				scriptLang.getClass().getField(scriptLang.toString()).getAnnotation(DefaultValue.class).text();

			IContentDescription contentDescription = ContentTypeManager.getInstance()
				.getDescriptionFor(editorInput.getStorage().getContents(), fileName, IContentDescription.ALL);

			EditorDescriptor defaultEditor = (EditorDescriptor)PlatformUI.getWorkbench().getEditorRegistry()
				.getDefaultEditor(editorInput.getName(), contentDescription.getContentType());

			_textEditor = (ITextEditor) defaultEditor.createEditor();
		}
		catch (Exception e1) {
		}

		IEditorReference ref = new ScriptEditorReference(_textEditor, editorInput);
		IEditorSite site = new ScriptEditorSite(
			ref, _textEditor, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage());
		try {
			_textEditor.init(site, editorInput);
			_textEditor.getDocumentProvider().getDocument(editorInput).addDocumentListener(
				new IDocumentListener() {

					public void documentAboutToBeChanged(DocumentEvent event) {
					}

					public void documentChanged(DocumentEvent event) {
						String content = event.getDocument().get();
						part.getLocalModelElement().write(((ValueProperty)part.getProperty()), content);
					}

				});

			ModelPropertyListener modelListener = new ModelPropertyListener() {

				@Override
				public void handlePropertyChangedEvent(ModelPropertyChangeEvent event) {
					CodePropertyEditorRenderer.this.textEditor.getDocumentProvider().getDocument(editorInput)
						.set(part.getLocalModelElement().read(getProperty()).getText());
				}

			};

			part.getLocalModelElement().addListener(modelListener, part.getProperty().getName());
		}
		catch (PartInitException pie) {
			pie.printStackTrace();
		}

		Control[] prevChildren = scriptEditorParent.getChildren();

		// _textEditor.createPartControl( scriptEditorParent );

		new Label(scriptEditorParent, SWT.NONE);
		Control[] newChildren = scriptEditorParent.getChildren();

		decorator.addEditorControl(newChildren[prevChildren.length], true);

		if (actionsToolBarNeeded) {
			ToolBar toolbar = new ToolBar(scriptEditorParent, SWT.FLAT | SWT.HORIZONTAL);

			toolbar.setLayoutData(gdvfill());

			toolBarActionsPresentation.setToolBar(toolbar);

			toolBarActionsPresentation.render();

			context.adapt(toolbar);

			decorator.addEditorControl(toolbar);

			// relatedControls.add( toolbar );

		}
	}

	@Override
	protected void handleFocusReceivedEvent() {
		super.handleFocusReceivedEvent();
	}

	@Override
	protected void handlePropertyChangedEvent() {
		super.handlePropertyChangedEvent();
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
			IWorkbenchPart wbPart = getPage().getActivePart();

			PartSite wkSite = (PartSite)wbPart.getSite();

			IActionBars bars = wkSite.getActionBars();

			return bars;
		}

		@Override
		public Shell getShell() {
			IWorkbenchPart wbPart = getPage().getActivePart();

			return wbPart.getSite().getShell();
		}

		public void registerContextMenu(
			MenuManager menuManager, ISelectionProvider selectionProvider, boolean includeEditorInput) {
		}

		public void registerContextMenu(
			String menuId, MenuManager menuManager, ISelectionProvider selectionProvider, boolean includeEditorInput) {
		}

	}

}