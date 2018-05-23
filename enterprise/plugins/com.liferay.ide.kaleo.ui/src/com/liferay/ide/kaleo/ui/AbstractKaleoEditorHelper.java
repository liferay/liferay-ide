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

package com.liferay.ide.kaleo.ui;

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.ui.editor.HiddenFileEditorInput;
import com.liferay.ide.kaleo.ui.editor.ScriptPropertyEditorInput;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractKaleoEditorHelper implements IKaleoEditorHelper {

	public IEditorPart createEditorPart(ScriptPropertyEditorInput editorInput, IEditorSite editorSite) {
		IEditorPart editorPart = null;

		try {
			editorPart = new TextEditor();

			editorPart.init(editorSite, editorInput);
		}
		catch (PartInitException pie) {
			KaleoUI.logError("Could not create default script editor.", pie);
		}

		return editorPart;
	}

	public String getContributorName() {
		return _contributorName;
	}

	public String getEditorId() {
		return _editorId;
	}

	public String getFileExtension() {
		return _fileExtension;
	}

	public String getLanguageType() {
		return _languageType;
	}

	public void handleDropFromPalette(IEditorPart activeEditor) {

		// default do nothing

	}

	public void openEditor(ISapphirePart sapphirePart, Element modelElement, ValueProperty valueProperty) {
		try {
			Object content = modelElement.property(valueProperty).content();

			if (content == null) {
				content = "";
			}

			IProject project = sapphirePart.adapt(IProject.class);

			IEditorInput editorInput = modelElement.adapt(IEditorInput.class);

			String name = editorInput.getName();

			Node node = modelElement.nearest(Node.class);

			String nodeName = node.getName().content();

			HiddenFileEditorInput hiddenFileEditorInput = _getHiddenFileEditorInput(
				project, name, nodeName, content.toString());

			IEditorSite editorSite = sapphirePart.adapt(IEditorSite.class);

			IWorkbenchWindow wbWindow = editorSite.getWorkbenchWindow();

			IEditorPart editorPart = wbWindow.getActivePage().openEditor(hiddenFileEditorInput, _editorId);

			ITextEditor textEditor = (ITextEditor)editorPart.getAdapter(ITextEditor.class);

			IDocumentListener documentListener = new IDocumentListener() {

				public void documentAboutToBeChanged(DocumentEvent event) {
				}

				public void documentChanged(DocumentEvent event) {
					String contents = event.getDocument().get();

					modelElement.property(valueProperty).write(contents);
				}

			};

			IDocumentProvider documentProvider = textEditor.getDocumentProvider();

			documentProvider.getDocument(hiddenFileEditorInput).addDocumentListener(documentListener);

			IWorkbenchPartSite wbPartSite = editorPart.getSite();

			IPartListener partListener = new IPartListener() {

				public void partActivated(IWorkbenchPart part) {
				}

				public void partBroughtToTop(IWorkbenchPart part) {
				}

				public void partClosed(IWorkbenchPart part) {
					if ((part != null) && part.equals(editorPart)) {
						new WorkspaceJob("delete temp editor file") {

							@Override
							public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
								try {
									IFile file = hiddenFileEditorInput.getFile();

									file.getParent().delete(true, null);
								}
								catch (CoreException ce) {
								}

								return Status.OK_STATUS;
							}

						}.schedule(100);
					}
				}

				public void partDeactivated(IWorkbenchPart part) {
				}

				public void partOpened(IWorkbenchPart part) {
				}

			};

			wbPartSite.getPage().addPartListener(partListener);
		}
		catch (Exception e) {
			KaleoUI.logError("Error opening editor.", e);
		}
	}

	public void setContributorName(String contributorName) {
		_contributorName = contributorName;
	}

	public void setEditorId(String editorId) {
		_editorId = editorId;
	}

	public void setFileExtension(String fileExtension) {
		_fileExtension = fileExtension;
	}

	public void setLanguageType(String langauge) {
		_languageType = langauge;
	}

	private HiddenFileEditorInput _getHiddenFileEditorInput(
		IProject project, String name, String nodeName, String fileContents) {

		return new HiddenFileEditorInput(_getTemporaryFile(project, name, nodeName, getFileExtension(), fileContents));
	}

	private IFile _getTemporaryFile(
		IProject project, String name, String nodeName, String fileExtension, String fileContents) {

		if (empty(fileContents)) {
			fileContents = "";
		}

		IPath tempScriptFilePath = _getTempScriptFilePath(project, name, nodeName, fileExtension);

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IFile tempFile = workspace.getRoot().getFile(tempScriptFilePath);

		try (ByteArrayInputStream source = new ByteArrayInputStream(fileContents.getBytes("UTF-8"))) {
			if (FileUtil.exists(tempFile)) {
				tempFile.setContents(source, true, false, null);
			}
			else {
				tempFile.create(source, true, null);
			}

			tempFile.setCharset("UTF-8", new NullProgressMonitor());
		}
		catch (CoreException | IOException e) {
			KaleoCore.logError(e);
		}

		return tempFile;
	}

	private String _getTempScriptFileName(String name, String nodeName, String fileExtension) {
		StringBuilder retval = new StringBuilder(/* "." */);

		if (!empty(nodeName)) {
			retval.append(nodeName.replaceAll("![A-Za-z]+", "").replaceAll("\\s+", ""));
		}
		else if (!empty(name)) {
			retval.append(name.replaceAll("![A-Za-z]+", ""));
		}

		retval.append(".").append(fileExtension);

		return retval.toString();
	}

	private IPath _getTempScriptFilePath(IProject project, String name, String nodeName, String fileExtension) {
		IPath retval = null;

		IContainer folder = null;

		String tempScriptFileName = _getTempScriptFileName(name, nodeName, fileExtension);

		Path tempPath = new Path(KALEO_TEMP_PREFIX + System.currentTimeMillis() + "/" + tempScriptFileName);

		if ((project != null) && (tempPath != null)) {
			IFolder[] folders = CoreUtil.getSourceFolders(JavaCore.create(project)).toArray(new IFolder[0]);

			if (ListUtil.isNotEmpty(folders)) {
				folder = folders[0];
			}
			else {
				folder = project;
			}
		}

		if (folder != null) {

			// create a temporary folder that will contain the script

			IFile tempFile = folder.getFile(tempPath);

			try {
				CoreUtil.makeFolders((IFolder)tempFile.getParent());
				retval = tempFile.getFullPath();
			}
			catch (CoreException ce) {
			}
		}

		return retval;
	}

	private String _contributorName;
	private String _editorId;
	private String _fileExtension;
	private String _languageType;

}