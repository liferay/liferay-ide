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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.ui.dialog.CustomProjectSelectionDialog;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.lang.reflect.InvocationTargetException;

import java.net.URL;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.compare.BufferedContent;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValuePropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE.SharedImages;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.ServerUIUtil;

/**
 * @author Andy Wu
 * @author Simon Jiang
 */
public class CustomJspPage extends Page {

	public CustomJspPage(Composite parent, int style, LiferayUpgradeDataModel dataModel) {
		super(parent, style, dataModel, customjspPageId, true);

		Composite container = new Composite(this, SWT.NONE);

		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label = new Label(container, SWT.NONE);

		label.setText("Converted Project Location:");

		_projectLocation = new Text(container, SWT.BORDER);

		_projectLocation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		_projectLocation.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));

		_updateDefaultLocation();

		_projectLocation.addFocusListener(
			new FocusListener() {
	
				@Override
				public void focusGained(FocusEvent e) {
					String input = ((Text)e.getSource()).getText();
	
					if (input.equals(_defaultLocation)) {
						_projectLocation.setText("");
					}
	
					_projectLocation.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
				}
	
				@Override
				public void focusLost(FocusEvent e) {
					String input = ((Text)e.getSource()).getText();
	
					if (CoreUtil.isNullOrEmpty(input)) {
						_projectLocation.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
						_projectLocation.setText(_defaultLocation);
					}
				}
	
			});

		_projectLocation.addModifyListener(
			new ModifyListener() {
	
				public void modifyText(ModifyEvent e) {
					dataModel.setConvertedProjectLocation(_projectLocation.getText());
				}
	
			});

		dataModel.setConvertedProjectLocation(_projectLocation.getText());

		Button browseButton = new Button(container, SWT.PUSH);

		browseButton.setText("Browse...");

		browseButton.addSelectionListener(
			new SelectionAdapter() {
	
				@Override
				public void widgetSelected(SelectionEvent e) {
					final DirectoryDialog dd = new DirectoryDialog(getShell());
	
					dd.setMessage("Select Converted Project Location");
	
					final String selectedDir = dd.open();
	
					if (selectedDir != null) {
						_projectLocation.setText(selectedDir);
					}
				}
	
			});

		Composite buttonContainer = new Composite(this, SWT.NONE);

		buttonContainer.setLayout(new GridLayout(3, false));

		GridData buttonGridData = new GridData(SWT.CENTER, SWT.CENTER, true, true);

		buttonGridData.widthHint = 130;
		buttonGridData.heightHint = 35;

		dataModel.getConvertedProjectLocation().attach(new CustomJspFieldListener());
		dataModel.getConvertLiferayWorkspace().attach(new CustomJspFieldListener());

		Button selectButton = new Button(buttonContainer, SWT.PUSH);

		selectButton.setText("Select Projects");

		selectButton.addSelectionListener(
			new SelectionAdapter() {
	
				@Override
				public void widgetSelected(SelectionEvent e) {
					_runConvertAction();
				}
	
			});

		Button refreshButton = new Button(buttonContainer, SWT.PUSH);

		refreshButton.setText("Refresh Results");

		refreshButton.addSelectionListener(
			new SelectionAdapter() {
	
				@Override
				public void widgetSelected(SelectionEvent e) {
					refreshTreeViews();
				}
	
			});

		Button clearButton = new Button(buttonContainer, SWT.PUSH);

		clearButton.setText("Clear Results");

		clearButton.addSelectionListener(
			new SelectionAdapter() {
	
				@Override
				public void widgetSelected(SelectionEvent e) {
					CustomJspConverter.clearConvertResults();
	
					refreshTreeViews();
				}
	
			});

		SashForm sashForm = new SashForm(this, SWT.HORIZONTAL | SWT.H_SCROLL);

		GridLayout sashLayout = new GridLayout(1, false);

		sashLayout.marginHeight = 0;
		sashLayout.marginWidth = 0;

		sashForm.setLayout(sashLayout);

		GridData sashFormLayoutData = new GridData(GridData.FILL_BOTH);

		sashForm.setLayoutData(sashFormLayoutData);

		_createChildren(sashForm);

		sashForm.setWeights(new int[] {1, 1});

		refreshTreeViews();
	}

	public void compare(
		final String originalFilePath, final String changedFilePath, final String leftLabel, final String rightLabel) {

		CompareConfiguration config = new CompareConfiguration();

		config.setLeftEditable(false);
		config.setLeftLabel(leftLabel);

		config.setRightEditable(false);
		config.setRightLabel(rightLabel);

		CompareEditorInput editorInput = new CompareEditorInput(config) {

			@Override
			public void saveChanges(IProgressMonitor pm) throws CoreException {
				super.saveChanges(pm);

				_changedItem.writeFile();
			}

			@Override
			protected Object prepareInput(IProgressMonitor monitor)
				throws InterruptedException, InvocationTargetException {

				return new DiffNode(_originalItem, _changedItem);
			}

			private CompareItem _changedItem = new CompareItem(changedFilePath);
			private CompareItem _originalItem = new CompareItem(originalFilePath);

		};

		editorInput.setTitle("Compare ('" + originalFilePath + "'-'" + changedFilePath + "')");

		CompareUI.openCompareEditor(editorInput);
	}

	public void createSpecialDescriptor(Composite parent, int style) {
		final String descriptor =
			"This step will help you to convert projects with custom JSP hooks to modules or fragments.";
		String url = "";

		Link link = SWTUtil.createHyperLink(this, style, descriptor, 1, url);

		link.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
	}

	@Override
	public String getPageTitle() {
		return "Convert Custom JSP Hooks";
	}

	public void refreshTreeViews() {
		File[] leftInputs = _getLeftTreeInputs();

		_leftTreeViewer.setInput(leftInputs);

		File[] rightInputs = _getRightTreeInputs();

		_rightTreeViewer.setInput(rightInputs);
	}

	private void _createChildren(Composite container) {
		_createImages();
		_createLeftPart(container);
		_createRightPart(container);
	}

	private Image _createImage(String symbolicName) {
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();

		Image image = sharedImages.getImage(symbolicName);

		if (image.isDisposed()) {
			image = sharedImages.getImageDescriptor(symbolicName).createImage();
		}

		return image;
	}

	private void _createImages() {
		_imageProject = _createImage(SharedImages.IMG_OBJ_PROJECT);
		_imageFolder = _createImage(ISharedImages.IMG_OBJ_FOLDER);
		_imageFile = _createImage(ISharedImages.IMG_OBJ_FILE);
	}

	private void _createLeftPart(Composite parent) {
		ScrolledComposite leftContainer = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);

		Composite leftPart = SWTUtil.createComposite(leftContainer, 1, 1, GridData.FILL_BOTH, 0, 0);

		FillLayout layout = new FillLayout();

		layout.marginHeight = 0;
		layout.marginWidth = 0;

		leftContainer.setLayout(layout);

		leftContainer.setMinSize(410, 200);
		leftContainer.setExpandHorizontal(true);
		leftContainer.setExpandVertical(true);
		leftContainer.setContent(leftPart);

		Label leftLabel = new Label(leftPart, SWT.NONE);

		leftLabel.setText("6.2 Custom JSPs (double-click to compare with 6.2)");

		_leftTreeViewer = new TreeViewer(leftPart, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);

		_leftTreeViewer.getTree().setLayoutData(gd);

		_leftTreeViewer.setContentProvider(new ViewContentProvider());
		_leftTreeViewer.setLabelProvider(new LeftViewLabelProvider());

		_leftTreeViewer.addDoubleClickListener(new DoubleClickExpandListener(_leftTreeViewer));

		_leftTreeViewer.addDoubleClickListener(
			new IDoubleClickListener() {
	
				@Override
				public void doubleClick(DoubleClickEvent event) {
					ISelection selection = event.getSelection();
	
					File file = (File)((ITreeSelection)selection).getFirstElement();
	
					if (file.isDirectory()) {
						return;
					}
	
					if (_is62FileFound(file)) {
						String[] paths = _get62FilePaths(file);
	
						compare(paths[0], paths[1], "6.2 original JSP", "custom JSP");
					}
					else {
						MessageDialog.openInformation(
							Display.getDefault().getActiveShell(), "File not found", "There is no such file in liferay 62");
					}
				}
	
			});

		_leftTreeViewer.setComparator(
			new ViewerComparator() {
	
				@Override
				public int category(Object element) {
					File file = (File)element;
	
					if (file.isDirectory()) {
						return -1;
					}
					else {
						return super.category(element);
					}
				}
	
			});
	}

	private void _createRightPart(Composite parent) {
		ScrolledComposite rightContainer = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);

		Composite rightPart = SWTUtil.createComposite(rightContainer, 1, 1, GridData.FILL_BOTH, 0, 0);

		FillLayout layout = new FillLayout();

		layout.marginHeight = 0;
		layout.marginWidth = 0;

		rightContainer.setLayout(layout);

		rightContainer.setMinSize(410, 200);
		rightContainer.setExpandHorizontal(true);
		rightContainer.setExpandVertical(true);
		rightContainer.setContent(rightPart);

		Label rightLabel = new Label(rightPart, SWT.NONE);

		rightLabel.setText("New JSP (double-click to compare 6.2 with 7.x)");

		_rightTreeViewer = new TreeViewer(rightPart, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);

		_rightTreeViewer.getTree().setLayoutData(gd);

		_rightTreeViewer.setContentProvider(new ViewContentProvider());
		_rightTreeViewer.setLabelProvider(new RightViewLabelProvider());

		_rightTreeViewer.addDoubleClickListener(new DoubleClickExpandListener(_rightTreeViewer));

		_rightTreeViewer.addDoubleClickListener(
			new IDoubleClickListener() {
	
				@Override
				public void doubleClick(DoubleClickEvent event) {
					ISelection selection = event.getSelection();
	
					File file = (File)((ITreeSelection)selection).getFirstElement();
	
					if (file.isDirectory()) {
						return;
					}
	
					if (_is70FileFound(file)) {
						String[] paths = _get70FilePaths(file);
	
						compare(
							paths[0], paths[1], "6.2 original JSP",
							"New 7.x JSP in " + CoreUtil.getProject(file).getName());
					}
					else {
						MessageDialog.openInformation(
							Display.getDefault().getActiveShell(), "file not found", "there is no such file in liferay 7");
					}
				}
	
			});

		_rightTreeViewer.setComparator(
			new ViewerComparator() {
	
				@Override
				public int category(Object element) {
					File file = (File)element;
	
					if (file.isDirectory()) {
						return -1;
					}
					else {
						return super.category(element);
					}
				}
	
			});
	}

	private String[] _get62FilePaths(File file) {
		String filePath = file.getAbsolutePath();

		IProject project = CoreUtil.getProject(file);

		String projectPath = project.getLocation().toOSString();

		String customJsp = CustomJspConverter.getCustomJspPath(projectPath);

		IFolder folder = project.getFolder("docroot/" + customJsp);

		if (!folder.exists()) {
			folder = project.getFolder("src/main/webapp/" + customJsp);
		}

		File folderFile = folder.getLocation().toFile();

		java.nio.file.Path customJspPath = folderFile.toPath();

		java.nio.file.Path relativePath = customJspPath.relativize(file.toPath());

		String[] paths = new String[2];

		paths[0] = null;
		paths[1] = filePath;

		File original62JspFile = new File(
			_getLiferay62ServerRootDirPath(_getLiferay62ServerLocation()) + relativePath.toString());

		if (original62JspFile.exists()) {
			paths[0] = original62JspFile.getAbsolutePath();
		}

		return paths;
	}

	private String[] _get70FilePaths(File file) {
		IFolder resourceFolder = CoreUtil.getProject(file).getFolder(_staticPath);

		File newFile = resourceFolder.getLocation().toFile();

		java.nio.file.Path resourcePath = newFile.toPath();

		java.nio.file.Path relativePath = resourcePath.relativize(file.toPath());

		String[] paths = new String[2];

		IFile original62File = resourceFolder.getFile("/.ignore/" + relativePath.toString() + ".62");
		IFile original70File = resourceFolder.getFile(relativePath.toString());

		if (original62File.exists() && original70File.exists()) {
			paths[0] = original62File.getLocation().toPortableString();
			paths[1] = original70File.getLocation().toPortableString();
		}

		return paths;
	}

	private IRuntime _getExistLiferay70Runtime() {
		Set<IRuntime> liferayRuntimes = ServerUtil.getAvailableLiferayRuntimes();

		for (IRuntime liferayRuntime : liferayRuntimes) {
			File customJspFile = liferayRuntime.getLocation().toFile();
			IRuntimeType runtimeType = liferayRuntime.getRuntimeType();

			if (runtimeType.getId().equals("com.liferay.ide.server.portal.runtime") && customJspFile.exists()) {
				return liferayRuntime;
			}
		}

		return null;
	}

	private List<IProject> _getHookProjects() {
		List<IProject> results = new ArrayList<>();
		IProject[] projects = CoreUtil.getAllProjects();

		for (IProject project : projects) {
			String projectLocation = project.getLocation().toPortableString();

			String customJsp = CustomJspConverter.getCustomJspPath(projectLocation);

			if (!CoreUtil.empty(customJsp)) {
				results.add(project);
			}
		}

		return results;
	}

	private File[] _getLeftTreeInputs() {
		String[] results = CustomJspConverter.getConvertResult(CustomJspConverter.sourcePrefix);

		if (results == null) {
			return null;
		}

		int size = results.length;

		List<File> list = new ArrayList<>();

		for (int i = 0; i < size; i++) {
			String[] contents = results[i].split(":");

			IProject project = CoreUtil.getProject(contents[0]);

			String customJspPath = contents[1];

			IFolder folder = project.getFolder("docroot/" + customJspPath);

			if (!folder.exists()) {
				folder = project.getFolder("src/main/webapp/" + customJspPath);
			}

			IPath location = folder.getLocation();

			if (location != null) {
				list.add(location.toFile());
			}
		}

		if (!list.isEmpty()) {
			return list.toArray(new File[0]);
		}
		else {
			return null;
		}
	}

	private String _getLiferay62ServerLocation() {
		String liferay62ServerLocation = dataModel.getLiferay62ServerLocation().content(true);

		if (liferay62ServerLocation == null) {
			Set<IRuntime> liferayRuntimes = ServerUtil.getAvailableLiferayRuntimes();

			for (IRuntime liferayRuntime : liferayRuntimes) {
				IRuntimeType runtimeType = liferayRuntime.getRuntimeType();

				if (runtimeType.getId().startsWith("com.liferay.ide.server.62.")) {
					IPath runtimeLocation = liferayRuntime.getLocation();

					liferay62ServerLocation = runtimeLocation.removeLastSegments(1).toString();

					return liferay62ServerLocation;
				}
			}

			if (liferay62ServerLocation == null) {
				Boolean openAddLiferaryServerDialog = MessageDialog.openConfirm(
					UIUtil.getActiveShell(), "Could not find Liferay 6.2 Runtime",
					"This process requires Liferay 6.2 Runtime. Click OK to add Liferay 6.2 Runtime.");

				if (openAddLiferaryServerDialog) {
					ServerUIUtil.showNewRuntimeWizard(UIUtil.getActiveShell(), null, null, "com.liferay.");

					return _getLiferay62ServerLocation();
				}
			}
		}

		return liferay62ServerLocation;
	}

	private String _getLiferay62ServerRootDirPath(String serverLocation) {
		if (CoreUtil.empty(serverLocation)) {
			return null;
		}

		File bundleDir = new File(serverLocation);

		String[] names = bundleDir.list(
			new FilenameFilter() {
	
				@Override
				public boolean accept(File dir, String name) {
					if (name.startsWith("tomcat-")) {
						return true;
					}
	
					return false;
				}
	
			});

		if ((names != null) && (names.length == 1)) {
			return serverLocation + "/" + names[0] + "/webapps/ROOT/";
		}
		else {
			return null;
		}
	}

	private IRuntime _getLiferay70Runtime() {
		String serverName = dataModel.getLiferay70ServerName().content();

		IServer server = ServerUtil.getServer(serverName);

		if (server != null) {
			return server.getRuntime();
		}
		else {
			IRuntime liferay70Runtime = _getExistLiferay70Runtime();

			if (liferay70Runtime != null) {
				return liferay70Runtime;
			}
			else {
				Boolean openAddLiferaryServerDialog = MessageDialog.openConfirm(
					UIUtil.getActiveShell(), "Could not find Liferay 7.x Runtime",
					"This process requires 7.x Runtime. Click OK to add Liferay 7.x Runtime.");

				if (openAddLiferaryServerDialog) {
					ServerUIUtil.showNewRuntimeWizard(UIUtil.getActiveShell(), "liferay.bundle", null, "com.liferay.");

					return _getExistLiferay70Runtime();
				}
			}
		}

		return null;
	}

	private File[] _getRightTreeInputs() {
		String[] results = CustomJspConverter.getConvertResult(CustomJspConverter.resultPrefix);

		if (results == null) {
			return null;
		}

		int size = results.length;

		List<File> list = new ArrayList<>();

		for (int i = 0; i < size; i++) {
			File file = new File(results[i], _staticPath);

			if (file.exists()) {
				list.add(file);
			}
		}

		if (!list.isEmpty()) {
			return list.toArray(new File[0]);
		}
		else {
			return null;
		}
	}

	private boolean _is62FileFound(File file) {
		String[] paths = _get62FilePaths(file);

		if (paths[0] != null) {
			return true;
		}
		else {
			return false;
		}
	}

	private boolean _is70FileFound(File file) {
		String[] paths = _get70FilePaths(file);

		if (paths[0] != null) {
			return true;
		}
		else {
			return false;
		}
	}

	private void _runConvertAction() {
		CustomProjectSelectionDialog dialog = new CustomProjectSelectionDialog(UIUtil.getActiveShell());

		dialog.setProjects(_getHookProjects());

		URL imageUrl = bundle.getEntry("/icons/e16/hook.png");

		Image hookImage = ImageDescriptor.createFromURL(imageUrl).createImage();

		dialog.setImage(hookImage);

		dialog.setTitle("Custom JSP Hook Project");
		dialog.setMessage("Select Custom JSP Hook Project");

		List<IProject> hookProjects = new ArrayList<>();

		if (dialog.open() == Window.OK) {
			final Object[] selectedProjects = dialog.getResult();

			if (selectedProjects != null) {
				for (Object project : selectedProjects) {
					if (project instanceof IJavaProject) {
						IJavaProject p = (IJavaProject)project;

						hookProjects.add(p.getProject());
					}
				}
			}
		}

		int size = hookProjects.size();

		if (size < 1) {
			return;
		}

		String[] sourcePaths = new String[size];
		String[] projectNames = new String[size];

		for (int i = 0; i < size; i++) {
			IProject hookProject = hookProjects.get(i);

			sourcePaths[i] = hookProject.getLocation().toOSString();
			projectNames[i] = hookProject.getName();
		}

		CustomJspConverter converter = new CustomJspConverter();

		IRuntime liferay70Runtime = _getLiferay70Runtime();

		if (liferay70Runtime == null) {
			MessageDialog.openError(
				Display.getDefault().getActiveShell(), "Convert Error", "Couldn't find Liferay 7.x Runtime.");

			return;
		}

		String liferay62ServerLocation = _getLiferay62ServerLocation();

		if (liferay62ServerLocation == null) {
			MessageDialog.openError(
				Display.getDefault().getActiveShell(), "Convert Error", "Couldn't find Liferay 6.2 Runtime.");

			return;
		}

		converter.setLiferay70Runtime(liferay70Runtime);
		converter.setLiferay62ServerLocation(liferay62ServerLocation);
		converter.setUi(this);

		Value<Path> convertedProjectLocation = dataModel.getConvertedProjectLocation();

		String targetPath = convertedProjectLocation.content().toPortableString();

		boolean liferayWorkapce = false;

		if (targetPath.equals(_defaultLocation) && _hasLiferayWorkspace) {
			liferayWorkapce = true;
		}

		converter.doExecute(sourcePaths, projectNames, targetPath, liferayWorkapce);
	}

	private void _updateDefaultLocation() {
		IPath workspaceLocation = CoreUtil.getWorkspaceRoot().getLocation();

		_defaultLocation = workspaceLocation.toPortableString();

		try {
			_hasLiferayWorkspace = LiferayWorkspaceUtil.hasWorkspace();

			if (_hasLiferayWorkspace) {
				IProject ws = LiferayWorkspaceUtil.getWorkspaceProject();

				String modulesDir = LiferayWorkspaceUtil.getModulesDir(ws);

				IPath locationPath = ws.getLocation().append(modulesDir);

				_defaultLocation = locationPath.toPortableString();
			}
		}
		catch (CoreException ce) {
		}

		UIUtil.async(
			new Runnable() {
	
				@Override
				public void run() {
					_projectLocation.setText(_defaultLocation);
				}
	
			});
	}

	private static String _defaultLocation;

	private ConvertedProjectLocationValidationService _convertedProjectLocationValidation =
		dataModel.getConvertedProjectLocation().service(ConvertedProjectLocationValidationService.class);
	private boolean _hasLiferayWorkspace = false;
	private Image _imageFile;
	private Image _imageFolder;
	private Image _imageProject;
	private TreeViewer _leftTreeViewer;
	private Text _projectLocation = null;
	private TreeViewer _rightTreeViewer;
	private String _staticPath = "/src/main/resources/META-INF/resources/";

	private class CompareItem extends BufferedContent implements ITypedElement, IModificationDate, IEditableContent {

		public CompareItem(String fileName) {
			_fileName = fileName;
			_time = System.currentTimeMillis();
		}

		public Image getImage() {
			return CompareUI.DESC_CTOOL_NEXT.createImage();
		}

		public long getModificationDate() {
			return _time;
		}

		public String getName() {
			return _fileName;
		}

		public String getType() {
			return ITypedElement.TEXT_TYPE;
		}

		public boolean isEditable() {
			return true;
		}

		public ITypedElement replace(ITypedElement dest, ITypedElement src) {
			return null;
		}

		public void writeFile() {
			_writeFile(_fileName, getContent());
		}

		protected InputStream createStream() throws CoreException {
			try {
				return Files.newInputStream(new File(_fileName).toPath());
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			return new ByteArrayInputStream(new byte[0]);
		}

		private void _writeFile(String fileName, byte[] newContent) {
			OutputStream fos = null;

			try {
				File file = new File(fileName);

				if (file.exists()) {
					file.delete();
				}

				file.createNewFile();

				fos = Files.newOutputStream(file.toPath());

				fos.write(newContent);
				fos.flush();
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
			finally {
				try {
					fos.close();
				}
				catch (IOException ioe) {
					ioe.printStackTrace();
				}

				fos = null;
			}
		}

		private String _fileName;
		private long _time;

	}

	private class CustomJspFieldListener extends Listener {

		@Override
		public void handle(Event event) {
			if (event instanceof ValuePropertyContentEvent) {
				ValuePropertyContentEvent propertyEvetn = (ValuePropertyContentEvent)event;

				Property property = propertyEvetn.property();

				Status validationStatus = Status.createOkStatus();

				if (property.name().equals("ConvertedProjectLocation")) {
					validationStatus = _convertedProjectLocationValidation.compute();

					String message = "ok";

					if (!validationStatus.ok()) {
						message = validationStatus.message();
					}

					PageValidateEvent pe = new PageValidateEvent();

					pe.setMessage(message);
					pe.setType(PageValidateEvent.error);

					triggerValidationEvent(pe);
				}
				else if (property.name().equals("ConvertLiferayWorkspace")) {
					if (dataModel.getConvertLiferayWorkspace().content(true)) {
						_updateDefaultLocation();
					}
				}
			}
		}

	}

	private class DoubleClickExpandListener implements IDoubleClickListener {

		public DoubleClickExpandListener(TreeViewer treeViewer) {
			_treeViewer = treeViewer;
		}

		@Override
		public void doubleClick(DoubleClickEvent event) {
			final IStructuredSelection selection = (IStructuredSelection)event.getSelection();

			if ((selection == null) || selection.isEmpty()) {
				return;
			}

			final Object obj = selection.getFirstElement();

			final ITreeContentProvider provider = (ITreeContentProvider)_treeViewer.getContentProvider();

			if (!provider.hasChildren(obj)) {
				return;
			}

			if (_treeViewer.getExpandedState(obj)) {
				_treeViewer.collapseToLevel(obj, AbstractTreeViewer.ALL_LEVELS);
			}
			else {
				_treeViewer.expandToLevel(obj, 1);
			}
		}

		private TreeViewer _treeViewer;

	}

	private class LeftViewLabelProvider extends StyledCellLabelProvider {

		@Override
		public void update(ViewerCell cell) {
			Object element = cell.getElement();
			StyledString text = new StyledString();

			File file = (File)element;

			if (file.isDirectory()) {
				text.append(_getFileName(file));

				File html = new File(file, "html");

				if (html.exists() && html.isDirectory()) {
					cell.setImage(_imageProject);
				}
				else {
					cell.setImage(_imageFolder);
				}

				String[] files = file.list(
					new FilenameFilter() {
	
						@Override
						public boolean accept(File dir, String name) {
							if (!name.startsWith(".")) {
								return true;
							}
							else {
								return false;
							}
						}
	
					});

				if (files != null) {
					text.append(" (" + files.length + ") ", StyledString.COUNTER_STYLER);
				}
			}
			else {
				cell.setImage(_imageFile);

				text.append(_getFileName(file));

				if (_is62FileFound(file)) {
					text.append("(found)", StyledString.COUNTER_STYLER);
				}
				else {
					text.append("(unfound)", StyledString.DECORATIONS_STYLER);
				}
			}

			cell.setText(text.toString());
			cell.setStyleRanges(text.getStyleRanges());

			super.update(cell);
		}

		private String _getFileName(File file) {
			String name = file.getName();

			File html = new File(file, "html");

			if (html.exists() && html.isDirectory()) {
				return CoreUtil.getProject(html).getName();
			}
			else {
				if (name.isEmpty()) {
					return file.getPath();
				}

				return name;
			}
		}

	}

	private class RightViewLabelProvider extends StyledCellLabelProvider {

		@Override
		public void update(ViewerCell cell) {
			Object element = cell.getElement();
			StyledString text = new StyledString();

			File file = (File)element;

			if (file.isDirectory()) {
				text.append(_getFileName(file));

				if (file.getName().endsWith("resources")) {
					cell.setImage(_imageProject);
				}
				else {
					cell.setImage(_imageFolder);
				}

				String[] files = file.list(
					new FilenameFilter() {
	
						@Override
						public boolean accept(File dir, String name) {
							if (!name.startsWith(".")) {
								return true;
							}
							else {
								return false;
							}
						}
	
					});

				if (files != null) {
					text.append(" (" + files.length + ") ", StyledString.COUNTER_STYLER);
				}
			}
			else {
				cell.setImage(_imageFile);

				text.append(_getFileName(file));

				if (_is70FileFound(file)) {
					text.append("(found)", StyledString.COUNTER_STYLER);
				}
				else {
					text.append("(unfound)", StyledString.DECORATIONS_STYLER);
				}
			}

			cell.setText(text.toString());
			cell.setStyleRanges(text.getStyleRanges());

			super.update(cell);
		}

		private String _getFileName(File file) {
			String name = file.getName();

			if (name.equals("resources")) {
				File parentFile = file.getParentFile().getParentFile();

				File parentParentFile = parentFile.getParentFile().getParentFile();

				return parentParentFile.getParentFile().getName();
			}
			else {
				if (name.isEmpty()) {
					return file.getPath();
				}

				return name;
			}
		}

	}

	private class ViewContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			File file = (File)parentElement;

			File[] files = file.listFiles(
				new FilenameFilter() {
	
					@Override
					public boolean accept(File dir, String name) {
						if (name.startsWith(".")) {
							return false;
						}
	
						return true;
					}
	
				});

			return files;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return (File[])inputElement;
		}

		@Override
		public Object getParent(Object element) {
			File file = (File)element;

			return file.getParentFile();
		}

		@Override
		public boolean hasChildren(Object element) {
			File file = (File)element;

			if (file.isDirectory()) {
				return true;
			}

			return false;
		}

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

	}

}