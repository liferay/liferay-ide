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

import static org.eclipse.sapphire.ui.forms.PropertyEditorPart.RELATED_CONTROLS;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.gd;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.gdfill;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.gdvalign;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.gdvfill;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.glayout;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.glspacing;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.kaleo.ui.IKaleoEditorHelper;
import com.liferay.ide.kaleo.ui.KaleoUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.ui.assist.internal.PropertyEditorAssistDecorator;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.PropertyEditorDef;
import org.eclipse.sapphire.ui.forms.PropertyEditorPart;
import org.eclipse.sapphire.ui.forms.swt.PropertyEditorPresentation;
import org.eclipse.sapphire.ui.forms.swt.PropertyEditorPresentationFactory;
import org.eclipse.sapphire.ui.forms.swt.SapphireToolBarActionPresentation;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.sapphire.ui.listeners.ValuePropertyEditorListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class ScriptPropertyEditorRenderer extends PropertyEditorPresentation {

	public ScriptPropertyEditorRenderer(FormComponentPart part, SwtPresentation context, Composite composite) {
		super(part, context, composite);
	}

	public static final class Factory extends PropertyEditorPresentationFactory {

		@Override
		public PropertyEditorPresentation create(PropertyEditorPart part, SwtPresentation parent, Composite composite) {
			return new ScriptPropertyEditorRenderer(part, parent, composite);
		}

	}

	@Override
	protected boolean canScaleVertically() {
		return true;
	}

	@Override
	protected void createContents(Composite parent) {
		PropertyEditorPart part = part();

		Element element = part.getLocalModelElement();
		ValueProperty property = part.property().nearest(ValueProperty.class);

		CreateMainCompositeDelegate createMainCompositeDelegate = new CreateMainCompositeDelegate(part) {

			@Override
			public boolean canScaleVertically() {
				return true;
			}

		};

		Composite codeEditorParent = createMainComposite(parent, createMainCompositeDelegate);

		// context.adapt( codeEditorParent );

		int codeEditorParentColumns = 1;
		SapphireToolBarActionPresentation toolBarActionsPresentation = new SapphireToolBarActionPresentation(
			getActionPresentationManager());

		boolean actionsToolBarNeeded = toolBarActionsPresentation.hasActions();

		if (actionsToolBarNeeded) {
			codeEditorParentColumns++;
		}

		codeEditorParent.setLayout(glayout(codeEditorParentColumns, 0, 0, 0, 0));

		Composite nestedComposite = new Composite(codeEditorParent, SWT.NONE);

		nestedComposite.setLayoutData(gdfill());

		// nestedComposite.setLayout( glspacing( glayout( 2, 0, 0 ), 2 ) );

		addControl(nestedComposite);

		PropertyEditorAssistDecorator decorator = createDecorator(nestedComposite);

		decorator.control().setLayoutData(gdvalign(gd(), SWT.TOP));
		decorator.addEditorControl(nestedComposite);

		ScriptPropertyEditorInput editorInput = new ScriptPropertyEditorInput(element, property);
		List<Control> relatedControls = new ArrayList<>();

		try {
			IEditorSite editorSite = part().adapt(IEditorSite.class);

			_editorPart = createEditorPart(editorInput, editorSite);

			_editorPart.createPartControl(nestedComposite);

			Control editorControl = _editorPart.getAdapter(Control.class);

			// need to find the first child of nestedComposite to relayout
			// editor control

			Composite editorControlParent = null;
			Control control = editorControl;

			while ((editorControlParent == null) && (control != null) && !nestedComposite.equals(control.getParent())) {
				control = control.getParent();
			}

			nestedComposite.setLayout(glspacing(glayout(2, 0, 0), 2));
			control.setLayoutData(gdfill());

			decorator.addEditorControl(editorControl, true);

			editorControl.setData(RELATED_CONTROLS, relatedControls);
		}
		catch (Exception e) {
			KaleoUI.logError(e);
		}

		if (actionsToolBarNeeded) {
			ToolBar toolbar = new ToolBar(codeEditorParent, SWT.FLAT | SWT.HORIZONTAL);

			toolbar.setLayoutData(gdvfill());
			toolBarActionsPresentation.setToolBar(toolbar);

			toolBarActionsPresentation.render();

			addControl(toolbar);

			decorator.addEditorControl(toolbar);
			relatedControls.add(toolbar);
		}

		List<Class<?>> listenerClasses = part.getRenderingHint(
			PropertyEditorDef.HINT_LISTENERS, Collections.<Class<?>>emptyList());
		List<ValuePropertyEditorListener> listeners = new ArrayList<>();

		if (ListUtil.isNotEmpty(listenerClasses)) {
			for (Class<?> cl : listenerClasses) {
				try {
					ValuePropertyEditorListener listener = (ValuePropertyEditorListener)cl.newInstance();

					listener.initialize(this);
					listeners.add(listener);
				}
				catch (Exception e) {
					KaleoUI.logError(e);
				}
			}
		}

		ITextEditor textEditor = null;

		if (_editorPart instanceof ITextEditor) {
			textEditor = (ITextEditor)_editorPart;
		}
		else {
			ITextEditor textEdit = _editorPart.getAdapter(ITextEditor.class);

			textEditor = textEdit;
		}

		addControl((Control)textEditor.getAdapter(Control.class));

		IDocumentListener documentListener = new IDocumentListener() {

			public void documentAboutToBeChanged(DocumentEvent event) {
			}

			public void documentChanged(DocumentEvent event) {
				Value<Object> elementProperty = element.property(property);

				elementProperty.write(event.getDocument().get());

				if (ListUtil.isNotEmpty(listeners)) {
					for (ValuePropertyEditorListener listener : listeners) {
						try {
							listener.handleValueChanged();
						}
						catch (Exception e) {
							KaleoUI.logError(e);
						}
					}
				}
			}

		};

		IDocumentProvider documentProvider = textEditor.getDocumentProvider();

		IDocument document = documentProvider.getDocument(_editorPart.getEditorInput());

		document.addDocumentListener(documentListener);
	}

	protected IEditorPart createEditorPart(ScriptPropertyEditorInput editorInput, IEditorSite editorSite) {
		IKaleoEditorHelper scriptEditorHelper = KaleoUI.getKaleoEditorHelper(editorInput.getScriptLanguage());

		if (scriptEditorHelper == null) {
			scriptEditorHelper = new DefaultScriptEditorHelper();
		}

		return scriptEditorHelper.createEditorPart(editorInput, editorSite);
	}

	private IEditorPart _editorPart;

	/**
	 * public static final class Groovy extends PropertyEditorRendererFactory {
	 *
	 * @Override public boolean isApplicableTo( PropertyEditorPart
	 *           propertyEditorDefinition ) { return (
	 *           propertyEditorDefinition.getProperty() instanceof ValueProperty
	 *           ); }
	 *
	 * @Override public PropertyEditorRenderer create( SapphireRenderingContext
	 *           context, PropertyEditorPart part ) { return new
	 *           ScriptPropertyEditorRenderer( context, part,
	 *           ScriptLanguageType.GROOVY ); } }
	 *
	 *           public static class Javascript extends
	 *           PropertyEditorRendererFactory {
	 *
	 * @Override public boolean isApplicableTo( PropertyEditorPart
	 *           propertyEditorDefinition ) { return (
	 *           propertyEditorDefinition.getProperty() instanceof ValueProperty
	 *           ); }
	 *
	 * @Override public PropertyEditorRenderer create( SapphireRenderingContext
	 *           context, PropertyEditorPart part ) { return new
	 *           ScriptPropertyEditorRenderer( context, part,
	 *           ScriptLanguageType.JAVASCRIPT ); } }
	 */
}