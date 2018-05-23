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

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.kaleo.core.IKaleoConnection;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.ui.KaleoUI;
import com.liferay.ide.kaleo.ui.KaleoUIPreferenceConstants;
import com.liferay.ide.kaleo.ui.WorkflowDesignerPerspectiveFactory;
import com.liferay.ide.kaleo.ui.navigator.WorkflowDefinitionEntry;
import com.liferay.ide.kaleo.ui.navigator.WorkflowDefinitionsFolder;
import com.liferay.ide.kaleo.ui.util.KaleoUtil;

import java.util.Iterator;
import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.swt.gef.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Gregory Amerson
 */
public class WorkflowDefinitionEditor extends SapphireEditorForXml {

	public static final String EDITOR_ID = "com.liferay.ide.kaleo.ui.editor.workflowDefinition";

	public static final int PROP_NODE_WIZARDS_ENABLED = 0x13303;

	public static int propUpdateVersion = 0x404;

	public WorkflowDefinitionEditor() {
		super(WorkflowDefinition.TYPE, DefinitionLoader.sdef(WorkflowDefinitionEditor.class).page("DiagramPage"));

		String gridVisibleValue = _getPersistentProperty(KaleoCore.GRID_VISIBLE_KEY);

		if (!empty(gridVisibleValue)) {
			_gridVisible = Boolean.parseBoolean(gridVisibleValue);
		}
	}

	@Override
	public <A> A adapt(Class<A> adapterType) {
		if (WorkflowDefinitionEditor.class.equals(adapterType)) {
			return adapterType.cast(this);
		}
		else if (IServer.class.equals(adapterType)) {
			if (getEditorInput() instanceof WorkflowDefinitionEditorInput) {
				IEditorInput editInput = getEditorInput();

				WorkflowDefinitionEditorInput workflowInput = (WorkflowDefinitionEditorInput)editInput;

				WorkflowDefinitionEntry workflowEntry = workflowInput.getWorkflowDefinitionEntry();

				WorkflowDefinitionsFolder workflowFloder = workflowEntry.getParent();

				IServer server = workflowFloder.getParent();

				return adapterType.cast(server);
			}
		}

		return super.adapt(adapterType);
	}

	@Override
	public void dispose() {
		super.dispose();

		if ((getXmlEditor() != null) && (_propertyListener != null)) {
			getXmlEditor().removePropertyListener(_propertyListener);
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		IEditorInput editorInput = getEditorInput();

		/*
		 * if( editorInput instanceof WorkflowDefinitionEditorInput ) {
		 * WorkflowDefinitionEditorInput definitionInput =
		 * (WorkflowDefinitionEditorInput) editorInput;
		 *
		 * try { IRuntime runtime =
		 * definitionInput.getWorkflowDefinitionEntry().getParent().getParent().
		 * getRuntime();
		 *
		 * IWorkflowValidation workflowValidation =
		 * KaleoCore.getWorkflowValidation( runtime );
		 *
		 * Exception error = workflowValidation.validate( new
		 * ByteArrayInputStream(getXmlEditor().getDocumentProvider().
		 * getDocument( definitionInput ).get().getBytes()));
		 *
		 * // ignore errors with empty messages, likely an error in validation
		 * routine itself STUDIO-263 if ((error != null) && !
		 * CoreUtil.isNullOrEmpty( error.getMessage() ) ) {
		 * MessageDialog.openError( Display.getDefault().getActiveShell(),
		 * "Kaleo Workflow Validation", "Unable to save kaleo workflow:\n\n" +
		 * error.getMessage());
		 *
		 * return; } } catch (Exception e) { //do nothing } }
		 */
		if (_diagramEditor != null) {
			_diagramEditor.doSave(monitor);
		}

		super.doSave(monitor);

		if (editorInput instanceof WorkflowDefinitionEditorInput) {
			WorkflowDefinitionEditorInput definitionInput = (WorkflowDefinitionEditorInput)editorInput;

			_saveWorkflowDefinitionEntry(definitionInput);
		}
	}

	@Override
	public void doSaveAs() {
		getXmlEditor().doSaveAs();
	}

	public SapphireDiagramEditor getDiagramEditor() {
		return _diagramEditor;
	}

	public boolean isGridVisible() {
		return _gridVisible;
	}

	public boolean isNodeWizardsEnabled() {
		return _nodeWizardsEnabled;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	public boolean isShowGuides() {
		return _showGuides;
	}

	public void setGridVisible(Boolean gridVisible) {
		_gridVisible = gridVisible;
		_setPersistentProperty(KaleoCore.GRID_VISIBLE_KEY, gridVisible.toString());
	}

	public void setNodeWizardsEnabled(boolean enabled) {
		_nodeWizardsEnabled = enabled;
		_firePropertyChange(PROP_NODE_WIZARDS_ENABLED);
	}

	public void setShowGuides(boolean showGuides) {
		_showGuides = showGuides;
	}

	@Override
	protected void createDiagramPages() throws PartInitException {
		_diagramEditor = new SapphireDiagramEditor(
			this, getModelElement(), DefinitionLoader.sdef(WorkflowDefinitionEditor.class).page("DiagramPage"));

		addEditorPage(0, _diagramEditor);
	}

	@Override
	protected void createFormPages() throws PartInitException {

		// don't create any form page for definition editor

		if (_propertyListener == null) {
			_propertyListener = new PropertyListener();
		}

		getXmlEditor().addPropertyListener(_propertyListener);
	}

	@Override
	protected void createPages() {
		super.createPages();

		try {
			IWorkbench workBench = PlatformUI.getWorkbench();

			IWorkbenchWindow workBenchWindow = workBench.getActiveWorkbenchWindow();

			IWorkbenchPage workBenchPage = workBenchWindow.getActivePage();

			IPerspectiveDescriptor descripter = workBenchPage.getPerspective();

			String id = descripter.getId();

			if (WorkflowDesignerPerspectiveFactory.ID.equals(id)) {
				return;
			}
		}
		catch (Exception e) {
		}

		KaleoUI kaleoUI = KaleoUI.getDefault();

		IPreferenceStore preferenceStore = kaleoUI.getPreferenceStore();

		String perspectiveSwitch = preferenceStore.getString(KaleoUIPreferenceConstants.EDITOR_PERSPECTIVE_SWITCH);

		boolean remember = false;
		boolean openPerspective = false;

		if (MessageDialogWithToggle.PROMPT.equals(perspectiveSwitch)) {
			String descriptFile = "This kind of file is associated with the Kaleo Designer perspective.\n\n";

			String descriptPerspective = "This perspective is designed to support Kaleo Workflow development. ";

			String descriptPlace =
				"It places the Properties and Palette views in optimal location relative to the editor area.\n\n";

			String descriptHint = "Do you want to open this perspective now?";

			MessageDialogWithToggle toggleDialog = MessageDialogWithToggle.openYesNoQuestion(
				getSite().getShell(), "Open Kaleo Designer Perspective?",
				descriptFile + descriptPerspective + descriptPlace + descriptHint, "Remember my decision", false,
				KaleoUI.getDefault().getPreferenceStore(), KaleoUIPreferenceConstants.EDITOR_PERSPECTIVE_SWITCH);

			remember = toggleDialog.getToggleState();
			openPerspective = toggleDialog.getReturnCode() == IDialogConstants.YES_ID;

			if (remember) {
				KaleoUI.getPrefStore().setValue(
					KaleoUIPreferenceConstants.EDITOR_PERSPECTIVE_SWITCH,
					openPerspective ? MessageDialogWithToggle.ALWAYS : MessageDialogWithToggle.NEVER);
			}
		}
		else if (MessageDialogWithToggle.ALWAYS.equals(perspectiveSwitch)) {
			openPerspective = true;
		}

		if (openPerspective) {
			_switchToKaleoDesignerPerspective();
		}
	}

	private void _firePropertyChange(int property) {
		super.firePropertyChange(property);
	}

	private String _getPersistentProperty(QualifiedName key) {
		String retval = null;

		try {
			IFile workspaceFile = getLocalModelElement().adapt(IFile.class);

			retval = workspaceFile.getPersistentProperty(key);
		}
		catch (Exception e) {
		}

		return retval;
	}

	private void _saveWorkflowDefinitionEntry(WorkflowDefinitionEditorInput definitionInput) {
		StructuredTextEditor sourceEditor = getXmlEditor();

		WorkflowDefinitionEntry definition = definitionInput.getWorkflowDefinitionEntry();

		String titleMap = definition.getTitleMap();

		String titleCurrentValue = definition.getTitleCurrentValue();

		IDocumentProvider documentProvider = sourceEditor.getDocumentProvider();

		IDocument document = documentProvider.getDocument(getEditorInput());

		String definitionContent = document.get();

		String[] newTitleMap = new String[1];

		if ((titleCurrentValue != null) && (titleMap != null)) {
			try {
				String localeCode = Locale.getDefault().toString();
				JSONObject jsonTitleMap = new JSONObject(titleMap);

				Iterator<?> keys = jsonTitleMap.keys();

				while ((keys != null) && keys.hasNext()) {
					Object key = keys.next();

					String value = jsonTitleMap.getString(key.toString());

					if ((value != null) && value.contains(titleCurrentValue)) {
						localeCode = key.toString();

						break;
					}
				}

				jsonTitleMap.put(localeCode, titleCurrentValue);

				newTitleMap[0] = jsonTitleMap.toString();
			}
			catch (Exception e) {
			}
		}
		else {
			newTitleMap[0] = definition.getTitleMap();
		}

		if (empty(newTitleMap[0])) {
			try {
				newTitleMap[0] = KaleoUtil.createJSONTitleMap(definition.getTitle());
			}
			catch (JSONException jsone) {
			}
		}

		int draftVersion = definition.getDraftVersion() + 1;

		if (draftVersion == 0) {
			draftVersion = 1;
		}

		int newDraftVersion = draftVersion;

		Job saveWorkflowEntry = new Job("Saving kaleo workflow entry.") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				IStatus retval = Status.OK_STATUS;

				try {
					JSONObject updatedDraftDefinition = null;

					IKaleoConnection kaleoConnection = KaleoCore.getKaleoConnection(definition.getParent().getParent());

					try {
						JSONObject latestDraftDefinition = kaleoConnection.getLatestKaleoDraftDefinition(
							definition.getName(), definition.getVersion(), definition.getCompanyId());

						if (latestDraftDefinition != null) {
							updatedDraftDefinition = kaleoConnection.updateKaleoDraftDefinition(
								definition.getName(), newTitleMap[0], definitionContent,
								latestDraftDefinition.getInt("version"), latestDraftDefinition.getInt("draftVersion"),
								definition.getCompanyId(), definition.getUserId());
						}
						else {
							updatedDraftDefinition = kaleoConnection.addKaleoDraftDefinition(
								definition.getName(), newTitleMap[0], definitionContent, definition.getVersion(),
								newDraftVersion, definition.getUserId(), definition.getGroupId());
						}
					}
					catch (Exception e) {
						updatedDraftDefinition = kaleoConnection.addKaleoDraftDefinition(
							definition.getName(), newTitleMap[0], definitionContent, definition.getVersion(),
							newDraftVersion, definition.getUserId(), definition.getGroupId());
					}

					WorkflowDefinitionEntry newNode = WorkflowDefinitionEntry.createFromJsObject(
						updatedDraftDefinition);

					newNode.setParent(definition.getParent());

					newNode.setCompanyId(definition.getCompanyId());
					newNode.setContent(definitionContent);
					newNode.setDraftVersion(newDraftVersion);
					newNode.setName(definition.getName());
					newNode.setLocation(definition.getLocation());
					newNode.setTitleCurrentValue(titleCurrentValue);
					newNode.setTitleMap(newTitleMap[0]);
					newNode.setUserId(definition.getUserId());
					newNode.setVersion(definition.getVersion());
					newNode.setGroupId(definition.getGroupId());

					Display.getDefault().asyncExec(
						new Runnable() {

							public void run() {
								IEditorInput editInput = getEditorInput();

								WorkflowDefinitionEditorInput workflowEditorInput =
									(WorkflowDefinitionEditorInput)editInput;

								workflowEditorInput.setWorkflowDefinitionEntry(newNode);

								setPartName(workflowEditorInput.getName());

								firePropertyChange(propUpdateVersion);
							}

						});
				}
				catch (Exception e) {
					retval = KaleoUI.createErrorStatus("Could not save kaleo workflow entry.", e);
				}

				return retval;
			}

		};

		saveWorkflowEntry.schedule();
	}

	private void _setPersistentProperty(QualifiedName key, String value) {
		try {
			IFile workspaceFile = getLocalModelElement().adapt(IFile.class);

			workspaceFile.setPersistentProperty(key, value);
		}
		catch (Exception e) {
		}
	}

	private void _switchToKaleoDesignerPerspective() {
		Display.getDefault().asyncExec(
			new Runnable() {

				public void run() {
					IWorkbench workBench = PlatformUI.getWorkbench();

					IPerspectiveRegistry registry = workBench.getPerspectiveRegistry();

					IPerspectiveDescriptor perspective = registry.findPerspectiveWithId(
						WorkflowDesignerPerspectiveFactory.ID);

					IWorkbenchWindow workBenchWindow = getSite().getWorkbenchWindow();

					IWorkbenchPage workBenchPage = workBenchWindow.getActivePage();

					workBenchPage.setPerspective(perspective);
				}

			});
	}

	private SapphireDiagramEditor _diagramEditor;
	private boolean _gridVisible = true;
	private boolean _nodeWizardsEnabled = true;
	private WorkflowDefinitionEditor.PropertyListener _propertyListener;
	private boolean _showGuides = true;

	private class PropertyListener implements IPropertyListener {

		public void propertyChanged(Object source, int propId) {
			switch (propId) {
				case IEditorPart.PROP_INPUT:
					if (source == getXmlEditor()) {
						setInput(getXmlEditor().getEditorInput());
					}

				case IEditorPart.PROP_DIRTY:
					if (source == getXmlEditor()) {
						if (getXmlEditor().getEditorInput() != getEditorInput()) {
							setInput(getXmlEditor().getEditorInput());
							/*
							 * title should always change when input changes.
							 * create runnable for following post call
							 */
							Runnable runnable = new Runnable() {

								public void run() {
									_firePropertyChange(IWorkbenchPart.PROP_TITLE);
								}

							};
							/*
							 * Update is just to post things on the display
							 * queue (thread). We have to do this to get the
							 * dirty property to get updated after other things
							 * on the queue are executed.
							 */
							((Control)getXmlEditor().getAdapter(Control.class)).getDisplay().asyncExec(runnable);
						}
					}

					break;

				case IWorkbenchPart.PROP_TITLE:

					// update the input if the title is changed

					if (source == getXmlEditor()) {
						if (getXmlEditor().getEditorInput() != getEditorInput()) {
							setInput(getXmlEditor().getEditorInput());
						}
					}

					break;

				default:

					// propagate changes. Is this needed? Answer: Yes.

					if (source == getXmlEditor()) {
						_firePropertyChange(propId);
					}

					break;
			}
		}

	}

}