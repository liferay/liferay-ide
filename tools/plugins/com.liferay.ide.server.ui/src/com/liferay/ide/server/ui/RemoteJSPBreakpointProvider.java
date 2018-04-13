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

package com.liferay.ide.server.ui;

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringPool;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jst.jsp.core.text.IJSPPartitions;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.breakpointproviders.JavaStratumBreakpointProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.wst.sse.ui.internal.StructuredResourceMarkerAnnotationModel;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class RemoteJSPBreakpointProvider extends JavaStratumBreakpointProvider {

	@Override
	public IStatus addBreakpoint(IDocument document, IEditorInput input, int editorLineNumber, int offset)
		throws CoreException {

		// check if there is a valid position to set breakpoint

		int pos = _getValidPosition(document, editorLineNumber);
		IStatus status = null;

		if (pos >= 0) {
			IResource res = _getResourceFromInput(input);

			if (res != null) {
				String path = null;

				// IDE-648 IDE-110 get docroot relative path

				IWebProject lrproject = LiferayCore.create(IWebProject.class, res.getProject());

				if (lrproject != null) {
					IFolder webappRoot = lrproject.getDefaultDocrootFolder();

					if (FileUtil.exists(webappRoot)) {
						IPath resPath = res.getFullPath();

						IPath relativePath = resPath.makeRelativeTo(webappRoot.getFullPath());

						if ((relativePath != null) && (relativePath.segmentCount() > 0)) {
							path = "/" + relativePath.toPortableString();
						}
					}

					IBreakpoint point = JDIDebugModel.createStratumBreakpoint(
						res, "JSP", res.getName(), path, _getClassPattern(res), editorLineNumber, pos, pos, 0, true,
						null);

					if (point == null) {
						status = new Status(
							IStatus.ERROR, JSPUIPlugin.ID, IStatus.ERROR, "unsupported input type", null);
					}
				}
			}
			else if (input instanceof IStorageEditorInput) {
				/*
				 * For non-resources, use the workspace root and a coordinated
				 * attribute that is used to
				 * prevent unwanted (breakpoint) markers from being loaded
				 * into the editors.
				 */
				IWorkspace workspace = ResourcesPlugin.getWorkspace();

				res = workspace.getRoot();

				String id = input.getName();

				IStorage storage = ((IStorageEditorInput)input).getStorage();

				IPath storagePath = storage.getFullPath();

				if (input instanceof IStorageEditorInput && (storage != null) && (storage.getFullPath() != null)) {
					id = storagePath.toString();
				}

				Map<String, Object> attributes = new HashMap<>();

				attributes.put(StructuredResourceMarkerAnnotationModel.SECONDARY_ID_KEY, id);

				String path = null;

				IBreakpoint point = JDIDebugModel.createStratumBreakpoint(
					res, "JSP", input.getName(), path, _getClassPattern(res), editorLineNumber, pos, pos, 0, true,
					attributes);

				if (point == null) {
					status = new Status(IStatus.ERROR, JSPUIPlugin.ID, IStatus.ERROR, "unsupported input type", null);
				}
			}
		}
		else {
			status = new Status(IStatus.INFO, JSPUIPlugin.ID, IStatus.INFO, JSPUIMessages.BreakpointNotAllowed, null);
		}

		if (status == null) {
			status = new Status(IStatus.OK, JSPUIPlugin.ID, IStatus.OK, JSPUIMessages.OK, null);
		}

		return status;
	}

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
		throws CoreException {

		initData = data;
	}

	protected Object initData;

	private String _getClassPattern(IResource resource) {
		if (resource != null) {
			String shortName = resource.getName();
			String extension = resource.getFileExtension();

			if ((extension != null) && (extension.length() < shortName.length())) {
				shortName = shortName.substring(0, shortName.length() - extension.length() - 1);
			}

			if (initData instanceof String) {
				String iniData = initData.toString();

				if (iniData.length() > 0) {
					//https://bugs.eclipse.org/bugs/show_bug.cgi?id=154475

					return initData + ",_" + shortName;
				}
			}
			else if ((initData instanceof Map) && resource.isAccessible() && (resource.getType() == IResource.FILE)) {
				IContentTypeManager contentTypeManager = Platform.getContentTypeManager();

				IContentType[] types = contentTypeManager.findContentTypesFor(resource.getName());

				if (types.length == 0) {

					// if failed to find quickly, be more aggressive

					IContentDescription d = null;

					try {

						// optimized description lookup, might not succeed

						d = ((IFile)resource).getContentDescription();

						if (d != null) {
							types = new IContentType[] {d.getContentType()};
						}
					}
					catch (CoreException ce) {
						/*
						 * should not be possible given the accessible and file
						 * type check above
						 */
					}
				}

				// wasn't found earlier

				if (types == null) {
					types = contentTypeManager.findContentTypesFor(resource.getName());
				}

				StringBuffer patternBuffer = new StringBuffer("_" + shortName);

				for (int i = 0; i < types.length; i++) {
					Object pattern = ((Map)initData).get(types[i].getId());

					if (pattern != null) {
						patternBuffer.append(StringPool.COMMA);
						patternBuffer.append(pattern);
					}
				}

				return patternBuffer.toString();
			}
		}

		return "org.apache.jsp*";
	}

	private IResource _getResourceFromInput(IEditorInput input) {
		IResource resource = (IResource)input.getAdapter(IFile.class);

		if (resource == null) {
			resource = (IResource)input.getAdapter(IResource.class);
		}

		return resource;
	}

	private int _getValidPosition(IDocument idoc, int editorLineNumber) {
		int result = -1;

		if (idoc != null) {
			int startOffset = 0;
			int endOffset = 0;

			try {
				IRegion line = idoc.getLineInformation(editorLineNumber - 1);

				startOffset = line.getOffset();

				endOffset = Math.max(line.getOffset(), line.getOffset() + line.getLength());

				String lineText = idoc.get(startOffset, endOffset - startOffset);

				String lt = lineText.trim();

				// blank lines or lines with only an open or close brace or scriptlet tag cannot have a breakpoint

				if (lt.equals("") || lt.equals("{") || lt.equals("}") || lt.equals("<%")) {
					result = -1;
				}
				else {

					// get all partitions for current line

					ITypedRegion[] partitions = null;

					partitions = idoc.computePartitioning(startOffset, endOffset - startOffset);

					for (int i = 0; i < partitions.length; ++i) {
						String type = partitions[i].getType();

						// if found jsp java content, jsp directive tags,custom tags, return that position

						if ((type == IJSPPartitions.JSP_CONTENT_JAVA) || (type == IJSPPartitions.JSP_DIRECTIVE) ||
							(type == IJSPPartitions.JSP_DEFAULT_EL) || (type == IJSPPartitions.JSP_DEFAULT_EL2)) {

							result = partitions[i].getOffset();
						}
					}
				}
			}
			catch (BadLocationException ble) {
				result = -1;
			}
		}

		return result;
	}

}