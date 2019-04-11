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

import aQute.bnd.osgi.Domain;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import org.osgi.framework.Version;

/**
 * @author Andy Wu
 * @author Gregory Amerson
 */
public class BladeCLIPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public BladeCLIPreferencePage() {
		super(GRID);

		_prefStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, ProjectCore.PLUGIN_ID);
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return _prefStore;
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		Group group = SWTUtil.createGroup(getFieldEditorParent(), "", 1);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		group.setLayoutData(gd);

		Composite composite = SWTUtil.createComposite(group, 2, 2, GridData.FILL_BOTH);

		addField(new StringFieldEditor(BladeCLI.BLADE_CLI_REPO_URL, "Blade CLI Repo URL:", composite));

		Label currentVersionLabel = new Label(composite, SWT.NONE);

		currentVersionLabel.setText("Current Embedded Blade CLI Version:");

		Text currentBladeVersionText = new Text(composite, SWT.BORDER);

		currentBladeVersionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		currentBladeVersionText.setEditable(false);

		try {
			currentBladeVersionText.setText(_getCurrentBladeVersion());
		}
		catch (Exception e) {
		}

		Label latestVersionLabel = new Label(composite, SWT.NONE);

		latestVersionLabel.setText("Latest Blade Version:");

		Text latestBladeVersionText = new Text(composite, SWT.BORDER);

		latestBladeVersionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		latestBladeVersionText.setEditable(false);
		latestBladeVersionText.setText("Checking...");

		Button updateBladeButton = new Button(group, SWT.PUSH);

		updateBladeButton.setText("Update Embedded Blade to Latest Version");
		updateBladeButton.setEnabled(false);

		Button restoreBladeJarButton = new Button(group, SWT.PUSH);

		restoreBladeJarButton.setText("Restore Embedded Blade CLI to Original Version");

		updateBladeButton.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent event) {
					try {
						File newBladeJar = BladeCLI.fetchBladeJarFromRepo(true);

						if (newBladeJar != null) {
							BladeCLI.addToLocalInstance(newBladeJar);
						}

						currentBladeVersionText.setText(_getCurrentBladeVersion());

						MessageDialog.openInformation(getShell(), "Blade CLI", "Update successful.");

						updateBladeButton.setEnabled(false);
					}
					catch (Exception e) {
						MessageDialog.openError(
							getShell(), "Blade CLI", "Could not update Blade CLI: " + e.getMessage());
					}
				}

			});

		restoreBladeJarButton.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent event) {
					BladeCLI.restoreOriginal();

					try {
						currentBladeVersionText.setText(_getCurrentBladeVersion());

						restoreBladeJarButton.setEnabled(false);
					}
					catch (Exception e) {
					}
				}

			});

		Job job = new Job("Checking latest blade cli version...") {

			@Override
			public IStatus run(IProgressMonitor monitor) {
				try {
					String currentVersion = _getCurrentBladeVersion();

					File updateJar = BladeCLI.fetchBladeJarFromRepo(false);

					Domain domain = Domain.domain(updateJar);

					String newVersion = domain.getBundleVersion();

					final boolean newAvailable =
						new Version(
							newVersion
						).compareTo(
							new Version(currentVersion)
						) > 0;

					UIUtil.async(
						new Runnable() {

							@Override
							public void run() {
								if (!latestBladeVersionText.isDisposed() && !updateBladeButton.isDisposed()) {
									latestBladeVersionText.setText(newVersion);

									updateBladeButton.setEnabled(newAvailable);
								}
							}

						});
				}
				catch (Exception e) {
				}

				return Status.OK_STATUS;
			}

		};

		job.schedule();
	}

	private String _getCurrentBladeVersion() throws BladeCLIException, IOException {
		File file = FileUtil.getFile(BladeCLI.getBladeCLIPath());

		Domain domain = Domain.domain(file);

		Version version = new Version(domain.getBundleVersion());

		return version.toString();
	}

	private final ScopedPreferenceStore _prefStore;

}