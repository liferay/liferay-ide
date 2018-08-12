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

package com.liferay.ide.portlet.ui.editor;

import com.liferay.ide.core.model.IBaseModel;
import com.liferay.ide.core.model.IModelChangedEvent;
import com.liferay.ide.portlet.core.PluginPackageModel;
import com.liferay.ide.ui.editor.InputContext;
import com.liferay.ide.ui.form.IDEFormEditor;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings({"rawtypes", "restriction"})
public class PluginPackageInputContext extends InputContext {

	public static final String CONTEXT_ID = "plugin-package-context";

	public PluginPackageInputContext(IDEFormEditor editor, IEditorInput input, boolean primary) {
		super(editor, input, primary);

		create();
	}

	@Override
	public String getId() {
		return CONTEXT_ID;
	}

	@Override
	protected void addTextEditOperation(ArrayList ops, IModelChangedEvent event) {
	}

	@Override
	protected IDocumentProvider createDocumentProvider(IEditorInput input) {
		JavaPlugin plugin = JavaPlugin.getDefault();

		return plugin.getPropertiesFileDocumentProvider();

		// return super.createDocumentProvider(input);

	}

	@Override
	protected IBaseModel createModel(IEditorInput input) throws CoreException {
		PluginPackageModel model = null;

		if (input instanceof IFileEditorInput) {
			IDocument document = getDocumentProvider().getDocument(input);

			IFile file = ((IFileEditorInput)input).getFile();

			model = new PluginPackageModel(file, document, true);

			model.setUnderlyingResource(file);
			model.setCharset(file.getCharset());

			return model;
		}

		return null;
	}

	@Override
	protected String getDefaultCharset() {
		return "UTF-8";
	}

	@Override
	protected String getPartitionName() {
		return null;
	}

}