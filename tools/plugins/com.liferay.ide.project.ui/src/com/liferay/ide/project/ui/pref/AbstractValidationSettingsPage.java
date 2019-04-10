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

package com.liferay.ide.project.ui.pref;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ScrolledPageContent;
import org.eclipse.wst.validation.ValidationFramework;

import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings({"restriction", "rawtypes", "deprecation", "unchecked"})
public abstract class AbstractValidationSettingsPage extends PropertyPreferencePage {

	public AbstractValidationSettingsPage() {
		_fCombos = new ArrayList();
		_fExpandables = new ArrayList();
		_fPreferencesService = Platform.getPreferencesService();
		_fValidation = ValidationFramework.getDefault();
	}

	@Override
	public boolean performOk() {
		if (super.performOk() && shouldRevalidateOnSettingsChange()) {
			MessageBox mb = new MessageBox(
				getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_INFORMATION | SWT.RIGHT);

			mb.setText(Msgs.validation);

			/* Choose which message to use based on if its project or workspace settings */
			String msg = (getProject() == null) ? Msgs.workspaceValidation : Msgs.projectLevelValidation;

			mb.setMessage(msg);

			switch (mb.open()) {
				case SWT.CANCEL:
					return false;
				case SWT.YES:
					ValidateJob job = new ValidateJob(Msgs.validationJob);

					job.schedule();
				case SWT.NO:
				default:
					return true;
			}
		}

		return true;
	}

	/**
	 * Creates a Combo widget in the composite <code>parent</code>. The data in the
	 * Combo is associated with <code>key</code>. The Combo data is generated based
	 * on the integer <code>values</code> where the index of <code>values</code>
	 * corresponds to the index of <code>valueLabels</code>
	 *
	 * @param parent
	 *            the composite to create the combo box in
	 * @param label
	 *            the label to give the combo box
	 * @param key
	 *            the unique key to identify the combo box
	 * @param values
	 *            the values represented by the combo options
	 * @param valueLabels
	 *            the calues displayed in the combo box
	 * @param indent
	 *            how far to indent the combo box label
	 *
	 * @return the generated combo box
	 */
	protected Combo addComboBox(
		Composite parent, String label, String key, int[] values, String[] valueLabels, int indent) {

		GridData gd = new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1);

		gd.horizontalIndent = indent;

		Label labelControl = new Label(parent, SWT.LEFT);

		labelControl.setFont(JFaceResources.getDialogFont());
		labelControl.setText(label);
		labelControl.setLayoutData(gd);

		Combo comboBox = newComboControl(parent, key, values, valueLabels);

		comboBox.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		return comboBox;
	}

	protected void controlChanged(Widget widget) {
		ComboData data = (ComboData)widget.getData();

		if (widget instanceof Combo) {
			data.setIndex(((Combo)widget).getSelectionIndex());
		}
		else {
			return;
		}
	}

	protected Composite createInnerComposite(Composite parent, ExpandableComposite twistie, int columns) {
		Composite inner = new Composite(twistie, SWT.NONE);

		inner.setFont(parent.getFont());
		inner.setLayout(new GridLayout(columns, false));

		twistie.setClient(inner);

		return inner;
	}

	protected Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.LEFT);

		label.setText(text);

		// GridData

		GridData data = new GridData(GridData.FILL);

		data.verticalAlignment = GridData.CENTER;
		data.horizontalAlignment = GridData.FILL;
		label.setLayoutData(data);

		return label;
	}

	protected Text createTextField(Composite parent) {
		Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);

		// GridData

		GridData data = new GridData();

		data.verticalAlignment = GridData.CENTER;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		text.setLayoutData(data);

		return text;
	}

	protected ExpandableComposite createTwistie(Composite parent, String label, int nColumns) {
		ExpandableComposite excomposite = new ExpandableComposite(
			parent, SWT.NONE, ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT);

		excomposite.setText(label);
		excomposite.setExpanded(false);

		FontRegistry faceResources = JFaceResources.getFontRegistry();

		excomposite.setFont(faceResources.getBold(JFaceResources.DIALOG_FONT));

		excomposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, nColumns, 1));
		excomposite.addExpansionListener(
			new ExpansionAdapter() {

				@Override
				public void expansionStateChanged(ExpansionEvent e) {
					expandedStateChanged((ExpandableComposite)e.getSource());
				}

			});

		_fExpandables.add(excomposite);

		_makeScrollableCompositeAware(excomposite);

		return excomposite;
	}

	protected final void expandedStateChanged(ExpandableComposite expandable) {
		ScrolledPageContent parentScrolledComposite = getParentScrolledComposite(expandable);

		if (parentScrolledComposite != null) {
			parentScrolledComposite.reflow(true);
		}
	}

	protected ExpandableComposite getParentExpandableComposite(Control control) {
		Control parent = control.getParent();

		while (!(parent instanceof ExpandableComposite) && (parent != null)) {
			parent = parent.getParent();
		}

		if (parent instanceof ExpandableComposite) {
			return (ExpandableComposite)parent;
		}

		return null;
	}

	protected ScrolledPageContent getParentScrolledComposite(Control control) {
		Control parent = control.getParent();

		while (!(parent instanceof ScrolledPageContent) && (parent != null)) {
			parent = parent.getParent();
		}

		if (parent instanceof ScrolledPageContent) {
			return (ScrolledPageContent)parent;
		}

		return null;
	}

	protected SelectionListener getSelectionListener() {
		if (_fSelectionListener == null) {
			_fSelectionListener = new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					controlChanged(e.widget);
				}

			};
		}

		return _fSelectionListener;
	}

	/**
	 * Creates a combo box and associates the combo data with the combo box.
	 *
	 * @param composite
	 *            the composite to create the combo box in
	 * @param key
	 *            the unique key to identify the combo box
	 * @param values
	 *            the values represented by the combo options
	 * @param valueLabels
	 *            the values displayed in the combo box
	 *
	 * @return the generated combo box
	 */
	protected Combo newComboControl(Composite composite, String key, int[] values, String[] valueLabels) {
		ComboData data = new ComboData(key, values, -1);

		Combo comboBox = new Combo(composite, SWT.READ_ONLY);

		comboBox.setItems(valueLabels);
		comboBox.setData(data);
		comboBox.addSelectionListener(getSelectionListener());
		comboBox.setFont(JFaceResources.getDialogFont());

		_makeScrollableCompositeAware(comboBox);

		int severity = -1;

		if (key != null) {
			severity = _fPreferencesService.getInt(
				getPreferenceNodeQualifier(), key, ValidationMessage.WARNING, createPreferenceScopes());
		}

		if ((severity == ValidationMessage.ERROR) || (severity == ValidationMessage.WARNING) ||
			(severity == ValidationMessage.IGNORE)) {

			data.setSeverity(severity);

			data.originalSeverity = severity;
		}

		if (data.getIndex() >= 0) {
			comboBox.select(data.getIndex());
		}

		_fCombos.add(comboBox);

		return comboBox;
	}

	protected void resetSeverities() {
		IEclipsePreferences defaultContext = new DefaultScope().getNode(getPreferenceNodeQualifier());

		for (int i = 0; i < _fCombos.size(); i++) {
			ComboData data = (ComboData)((Combo)_fCombos.get(i)).getData();

			int severity = defaultContext.getInt(data.getKey(), ValidationMessage.WARNING);

			data.setSeverity(severity);

			((Combo)_fCombos.get(i)).select(data.getIndex());
		}
	}

	protected void restoreSectionExpansionStates(IDialogSettings settings) {
		for (int i = 0; i < _fExpandables.size(); i++) {
			ExpandableComposite excomposite = (ExpandableComposite)_fExpandables.get(i);

			if (settings == null) {
				excomposite.setExpanded(i == 0); // only expand the first node by default
			}
			else {
				excomposite.setExpanded(settings.getBoolean(_SETTINGS_EXPANDED + String.valueOf(i)));
			}
		}
	}

	protected boolean shouldRevalidateOnSettingsChange() {
		Iterator it = _fCombos.iterator();

		while (it.hasNext()) {
			ComboData data = (ComboData)((Combo)it.next()).getData();

			if (data.isChanged()) {
				return true;
			}
		}

		return false;
	}

	protected void storeSectionExpansionStates(IDialogSettings section) {
		for (int i = 0; i < _fExpandables.size(); i++) {
			ExpandableComposite comp = (ExpandableComposite)_fExpandables.get(i);

			section.put(_SETTINGS_EXPANDED + String.valueOf(i), comp.isExpanded());
		}
	}

	protected void storeValues() {
		if (ListUtil.isEmpty(_fCombos)) {
			return;
		}

		Iterator it = _fCombos.iterator();

		IScopeContext[] contexts = createPreferenceScopes();

		while (it.hasNext()) {
			ComboData data = (ComboData)((Combo)it.next()).getData();

			if (data.getKey() != null) {
				IEclipsePreferences eclipsePreferences = contexts[0].getNode(getPreferenceNodeQualifier());

				eclipsePreferences.putInt(data.getKey(), data.getSeverity());
			}
		}

		for (IScopeContext context : contexts) {
			try {
				IEclipsePreferences eclipsePreferences = context.getNode(getPreferenceNodeQualifier());

				eclipsePreferences.flush();
			}
			catch (BackingStoreException bse) {
			}
		}
	}

	private void _makeScrollableCompositeAware(Control control) {
		ScrolledPageContent parentScrolledComposite = getParentScrolledComposite(control);

		if (parentScrolledComposite != null) {
			parentScrolledComposite.adaptChild(control);
		}
	}

	private static final String _SETTINGS_EXPANDED = "expanded";

	private List _fCombos;
	private List _fExpandables;
	private IPreferencesService _fPreferencesService = null;
	private SelectionListener _fSelectionListener;
	private ValidationFramework _fValidation;

	private static class Msgs extends NLS {

		public static String projectLevelValidation;
		public static String validation;
		public static String validationJob;
		public static String workspaceValidation;

		static {
			initializeMessages(AbstractValidationSettingsPage.class.getName(), Msgs.class);
		}

	}

	/**
	 * Performs validation after validation preferences have been modified.
	 */
	private class ValidateJob extends Job {

		public ValidateJob(String name) {
			super(name);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			IStatus status = Status.OK_STATUS;

			try {
				IProject[] projects = null;

				// Changed preferences for a single project, only validate it

				if (getProject() != null) {
					projects = new IProject[] {getProject()};
				}
				else {

					// Get all of the projects in the workspace

					projects = CoreUtil.getAllProjects();
					IEclipsePreferences prefs = null;
					List projectList = new ArrayList();

					// Filter out projects that use project-specific settings or have been closed

					for (IProject project : projects) {
						prefs = new ProjectScope(
							project
						).getNode(
							getPreferenceNodeQualifier()
						);

						if (project.isAccessible() && !prefs.getBoolean(getProjectSettingsKey(), false)) {
							projectList.add(project);
						}
					}

					projects = (IProject[])projectList.toArray(new IProject[projectList.size()]);
				}

				_fValidation.validate(projects, true, false, monitor);
			}
			catch (CoreException ce) {
				status = Status.CANCEL_STATUS;
			}

			return status;
		}

	}

}