/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.sdk.pref;

import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.sdk.SDKManager;
import com.liferay.ide.eclipse.sdk.SDKPlugin;
import com.liferay.ide.eclipse.server.core.InstallableRuntime2ConfigurationElement;
import com.liferay.ide.eclipse.server.core.PortalInstallableRuntime2;
import com.liferay.ide.eclipse.server.util.ServerUtil;
import com.liferay.ide.eclipse.ui.util.SWTUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wst.server.core.internal.InstallableRuntime2;
import org.eclipse.wst.server.core.internal.ServerPlugin;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class InstalledSDKsCompostite extends Composite {

	protected class SDKContentProvider implements IStructuredContentProvider {

		public void dispose() {
		}

		public Object[] getElements(Object inputElement) {
			return sdkList.toArray();
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	protected class SDKLabelProvider extends LabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == 0) {
				return SDKPlugin.getDefault().getImage(SDKPlugin.IMG_ICON_SDK_SMALL);
			}

			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			String retval = null;

			if (element instanceof SDK) {
				SDK sdk = (SDK) element;

				switch (columnIndex) {

				case 0:
					retval = sdk.getName();

					break;

				case 1:
					retval = sdk.getVersion();

					break;
				case 2:
					retval = sdk.getLocation().toOSString();

					break;

				// case 3:
				// retval = sdk.getRuntime() != null ? sdk.getRuntime() :
				// "<None>";
				}
			}
			else {
				retval = "";
			}
			return retval;
		}
	}

	protected Button fAddButton;

	protected Button fDownloadButton;

	protected Button fEditButton;

	protected ISelection fPrevSelection;

	protected Button fRemoveButton;

	protected PreferencePage page;

	protected List<SDK> sdkList = new ArrayList<SDK>();

	protected Table table;

	protected CheckboxTableViewer tableViewer;

	public InstalledSDKsCompostite(Composite parent, int style) {
		super(parent, style);

		GridLayout gl = new GridLayout(2, false);

		setLayout(gl);

		createControl(this);
	}

	public SDK getCheckedSDK() {
		Object[] checkedElements = tableViewer.getCheckedElements();

		if (checkedElements != null && checkedElements.length == 1 && checkedElements[0] instanceof SDK) {
			return (SDK) checkedElements[0];
		}

		return null;
	}

	// protected void downloadAndInstallSDK() {
	// final Object mutex = new Object();
	// final File[] installedSDK = new File[1];
	// final String[] sdkInstallDir = new String[1];
	// final File[] downloadedSDKFile = new File[1];
	// final Display display = getShell().getDisplay();
	// display.syncExec(new Runnable(){
	// public void run() {
	// DirectoryDialog dd = new DirectoryDialog(getShell());
	// dd.setText("Choose directory to install SDK:");
	// dd.setMessage("Choose directory to install SDK:");
	// sdkInstallDir[0] = dd.open();
	// }});
	// try {
	// new ProgressMonitorDialog(getShell()).run(false, true, new
	// IRunnableWithProgress() {
	// public void run(final IProgressMonitor monitor) throws
	// InvocationTargetException, InterruptedException {
	// DownloadHelper downloadHelper = new DownloadHelper();
	// try {
	// downloadHelper.downloadHttpFile(
	// "http://downloads.sourceforge.net/project/lportal/Liferay%20Portal/6.0.0/liferay-plugins-sdk-6.0.0.zip?use_mirror=hivelocity",
	// new FileTransferCallback() {
	// // boolean startedTask = false;
	// // long bytesRecieved = 0;
	// public void dataReceived(IIncomingFileTransferReceiveDataEvent event) {
	// // final IIncomingFileTransfer fileTransfer = event.getSource();
	// // if (!startedTask) {
	// // display.asyncExec(new Runnable(){
	// // public void run() {
	// // monitor.beginTask("Downloading SDK...",
	// (int)fileTransfer.getFileLength());
	// // monitor.subTask("Downloading file...");
	// // }});
	// // startedTask = true;
	// // } else {
	// // final int worked = (int)(fileTransfer.getBytesReceived() -
	// bytesRecieved);
	// // bytesRecieved = fileTransfer.getBytesReceived();
	// // display.asyncExec(new Runnable(){
	// // public void run() {
	// // monitor.worked(worked);
	// // System.out.println(worked);
	// // }});
	// // }
	// }
	//
	// public File getFileDownloadLocation(String fileName) {
	// //just download sdk to plugin state location for temp
	// File downloadDir =
	// SDKPlugin.getDefault().getStateLocation().append("sdk_download").toFile();
	// downloadDir.mkdirs();
	//										
	// //if everythign goes ok sdk file should be
	// downloadedSDKFile[0] = new File(downloadDir, fileName);
	// return downloadDir;
	// }
	//									
	// public void transferComplete() {
	// synchronized (mutex) {
	// mutex.notifyAll();
	// }
	// File sdkInstallDirectory = new File(sdkInstallDir[0]);
	// if (downloadedSDKFile[0].exists() && sdkInstallDirectory.exists()) {
	// try {
	// //create a new subdir in install dir that matches filename
	// File installDir = new File(sdkInstallDirectory,
	// downloadedSDKFile[0].getName().replaceAll("\\.zip$", ""));
	// installDir.mkdirs();
	// ZipUtil.unzip(downloadedSDKFile[0],installDir);
	// installedSDK[0] = installDir;
	// if (installedSDK[0].exists()) {
	// final SDK newSdk = SDKManager.getInstance().createSDKFromLocation(new
	// Path(installedSDK[0].getPath()));
	// display.asyncExec(new Runnable(){
	// public void run() {
	// sdkList.add(newSdk);
	// tableViewer.refresh();
	// ensureDefaultSDK();
	// tableViewer.refresh();
	// }});
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// public void handleException(Exception e) {
	// System.out.println(e);
	// e.printStackTrace();
	// }
	// });
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//					
	// synchronized (mutex) {
	// mutex.wait();
	// }
	// }
	// });
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//		
	// }

	public SDK[] getSDKs() {
		return sdkList.toArray(new SDK[sdkList.size()]);
	}

	public ISelection getSelection() {
		return this.tableViewer.getSelection();
	}

	public void setPreferencePage(PreferencePage prefPage) {
		this.page = prefPage;
	}

	protected void addSDK() {
		AddSDKDialog dialog = new AddSDKDialog(this.getShell(), sdkList.toArray(new SDK[0]));
		int retval = dialog.open();

		if (retval == AddSDKDialog.OK) {
			String name = dialog.getName();

			SDK newSDK = SDKManager.getInstance().createSDKFromLocation(new Path(dialog.getLocation()));
			newSDK.setName(name);

			sdkList.add(newSDK);

			this.tableViewer.refresh();

			ensureDefaultSDK();

			this.tableViewer.refresh();
		}
	}

	protected void createControl(Composite parent) {
		SWTUtil.createLabel(parent, "Installed Liferay SDKs:", 2);

		this.table = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 250;
		gd.widthHint = 350;

		this.table.setLayoutData(gd);
		this.table.setFont(parent.getFont());
		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);

		TableColumn column = new TableColumn(this.table, SWT.NULL);
		column.setText("Name");

		int defaultwidth = (gd.widthHint / 3) + 1;
		column.setWidth(defaultwidth);

		column = new TableColumn(this.table, SWT.NULL);
		column.setText("Version");
		column.setWidth(defaultwidth - 60);

		column = new TableColumn(this.table, SWT.NULL);
		column.setText("Location");
		column.setWidth(defaultwidth + 90);

		// column = new TableColumn(this.table, SWT.NULL);
		// column.setText("Runtime");
		// column.setWidth(defaultwidth+30);

		this.tableViewer = new CheckboxTableViewer(this.table);
		this.tableViewer.setLabelProvider(new SDKLabelProvider());
		this.tableViewer.setContentProvider(new SDKContentProvider());

		this.tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent evt) {
				enableButtons();
			}

		});

		this.tableViewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()) {
					setCheckedSDK((SDK) event.getElement());
				}
				else {
					setCheckedSDK(null);
				}
			}
		});

		Composite buttons = SWTUtil.createComposite(parent, 1, 1, GridData.VERTICAL_ALIGN_BEGINNING, 0, 0);

		fAddButton = SWTUtil.createPushButton(buttons, "Add...", null);
		fAddButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event evt) {
				addSDK();
			}

		});

		fEditButton = SWTUtil.createPushButton(buttons, "Edit...", null);
		fEditButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event evt) {
				editSDK(getFirstSelectedSDK());
			}

		});

		fRemoveButton = SWTUtil.createPushButton(buttons, "Remove", null);
		fRemoveButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event evt) {
				removeSelectedSDKs();// remove all selected sdks
			}

		});

		fDownloadButton = SWTUtil.createPushButton(buttons, "Download...", null);
		fDownloadButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				boolean confirm =
					MessageDialog.openConfirm(
						InstalledSDKsCompostite.this.getShell(), "Installed SDKs",
						"Download and install the latest Liferay Plug-ins SDK?");

				if (!confirm) {
					return;
				}

				final InstallableRuntime2 ir =
					(InstallableRuntime2) ServerPlugin.findInstallableRuntime("com.liferay.ide.eclipse.sdk.60");

				DirectoryDialog dialog = new DirectoryDialog(InstalledSDKsCompostite.this.getShell());

				dialog.setMessage("Select SDK installation directory:");

				String selectedDirectory = dialog.open();

				if (selectedDirectory != null) {
					final IPath selectedPath =
						new Path(selectedDirectory).append(((InstallableRuntime2) ir).getArchivePath());

					new Job("Installable SDK runtime update") {

						@Override
						protected IStatus run(IProgressMonitor monitor) {
							URL installableUrl = ServerUtil.checkForLatestInstallableRuntime(ir.getId());

							if (installableUrl == null) {
								try {
									installableUrl = new URL(((InstallableRuntime2) ir).getArchiveUrl());
								}
								catch (MalformedURLException e) {
									return SDKPlugin.createErrorStatus(e);
								}
							}

							InstallableRuntime2ConfigurationElement config =
								new InstallableRuntime2ConfigurationElement(
									(InstallableRuntime2) ir, installableUrl.toString());

							PortalInstallableRuntime2 pir = new PortalInstallableRuntime2(config);

							pir.install(selectedPath);

							final SDK newSDK = SDKManager.getInstance().createSDKFromLocation(selectedPath);

							SDK existingSDK = SDKManager.getSDKByName(newSDK.getName());

							int count = 1;

							String originalName = newSDK.getName();

							while (existingSDK != null) {
								newSDK.setName(originalName + "(" + (count++) + ")");

								existingSDK = SDKManager.getSDKByName(newSDK.getName());
							}

							SDKManager.getInstance().addSDK(newSDK);

							InstalledSDKsCompostite.this.getDisplay().asyncExec(new Runnable() {

								public void run() {
									sdkList.add(newSDK);

									InstalledSDKsCompostite.this.tableViewer.refresh();

									ensureDefaultSDK();

									InstalledSDKsCompostite.this.tableViewer.refresh();
								}
							});

							return Status.OK_STATUS;
						}

					}.schedule();
				}
			}
		});

		setSDKs(SDKManager.getAllSDKs());

		enableButtons();

		fAddButton.setEnabled(true);
	}

	protected void editSDK(SDK sdk) {
		AddSDKDialog dialog = new AddSDKDialog(this.getShell(), sdkList.toArray(new SDK[0]), sdk);

		int retval = dialog.open();

		if (retval == AddSDKDialog.OK) {
			String newName = dialog.getName();

			String newLocation = dialog.getLocation();

			sdk.setName(newName);
			sdk.setLocation(new Path(newLocation));

			this.tableViewer.refresh();

			ensureDefaultSDK();

			this.tableViewer.refresh();
		}
	}

	protected void enableButtons() {
		IStructuredSelection selection = (IStructuredSelection) this.tableViewer.getSelection();

		int selectionCount = selection.size();

		if (selectionCount > 0 && selectionCount <= this.tableViewer.getTable().getItemCount()) {
			Iterator<?> iterator = selection.iterator();

			while (iterator.hasNext()) {
				SDK install = (SDK) iterator.next();

				if (isContributed(install)) {
					fEditButton.setEnabled(false);
					fRemoveButton.setEnabled(false);

					return;
				}
			}

			fEditButton.setEnabled(true);

			fRemoveButton.setEnabled(true);
		}
		else {
			fEditButton.setEnabled(false);

			fRemoveButton.setEnabled(false);
		}
	}

	protected void ensureDefaultSDK() {

		// check if we only have one sdk it is new default
		if (sdkList.size() == 1) {
			setCheckedSDK(sdkList.get(0));
		}
	}

	protected void fireSelectionChanged() {
	}

	protected SDK getFirstSelectedSDK() {
		IStructuredSelection selection = (IStructuredSelection) getSelection();
		Iterator<?> iterator = selection.iterator();

		if (iterator.hasNext()) {
			Object next = iterator.next();

			if (next instanceof SDK) {
				return (SDK) next;
			}
		}

		return null;
	}

	protected boolean isContributed(SDK install) {
		return install.isContributed();
	}

	protected void removeSDKs(SDK[] sdks) {
		IStructuredSelection prev = (IStructuredSelection) getSelection();

		for (int i = 0; i < sdks.length; i++) {
			sdkList.remove(sdks[i]);
		}

		ensureDefaultSDK();

		this.tableViewer.refresh();

		IStructuredSelection curr = (IStructuredSelection) getSelection();

		if (!curr.equals(prev)) {
			SDK[] curSdks = getSDKs();

			if (curr.size() == 0 && curSdks.length == 1) {
				// pick a default SDK automatically
				setSelection(new StructuredSelection(curSdks[0]));
			}
			else {
				fireSelectionChanged();
			}
		}
	}

	protected void removeSelectedSDKs() {
		IStructuredSelection selection = (IStructuredSelection) getSelection();

		SDK[] sdks = new SDK[selection.size()];

		Iterator<?> iterator = selection.iterator();

		int i = 0;

		while (iterator.hasNext()) {
			SDK sdk = null;

			Object next = iterator.next();

			if (next instanceof SDK) {
				sdk = (SDK) next;
			}
			else if (next instanceof IStructuredSelection) {
				sdk = (SDK) ((IStructuredSelection) next).getFirstElement();
			}

			sdks[i++] = sdk;
		}

		removeSDKs(sdks);
	}

	protected void setCheckedSDK(SDK element) {
		if (element == null) {
			setSelection(new StructuredSelection());
		}
		else {
			setSelection(new StructuredSelection(element));

			for (SDK sdk : getSDKs()) {
				sdk.setDefault(false);
			}

			element.setDefault(true);
		}

		if (this.page != null) {
			this.page.getContainer().updateButtons();
		}
	}

	protected void setSDKs(SDK[] sdks) {
		SDK defaultSDK = null;

		sdkList.clear();

		for (SDK sdk : sdks) {
			sdkList.add(sdk);

			if (sdk.isDefault()) {
				defaultSDK = sdk;
			}
		}

		this.tableViewer.setInput(sdks);

		if (defaultSDK != null) {
			setCheckedSDK(defaultSDK);
		}

		this.tableViewer.refresh();
	}

	protected void setSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			if (!selection.equals(fPrevSelection)) {
				fPrevSelection = selection;

				Object sdk = ((IStructuredSelection) selection).getFirstElement();

				if (sdk == null) {
					this.tableViewer.setCheckedElements(new Object[0]);
				}
				else {
					this.tableViewer.setCheckedElements(new Object[] {
						sdk
					});

					this.tableViewer.reveal(sdk);
				}

				fireSelectionChanged();
			}
		}
	}
}
