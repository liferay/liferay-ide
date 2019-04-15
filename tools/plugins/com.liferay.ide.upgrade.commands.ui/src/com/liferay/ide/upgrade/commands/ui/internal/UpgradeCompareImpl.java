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

package com.liferay.ide.upgrade.commands.ui.internal;

import com.liferay.ide.upgrade.plan.core.UpgradeCompare;

import java.io.File;
import java.io.InputStream;

import java.lang.reflect.InvocationTargetException;

import java.nio.file.Files;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Simon Jiang
 * @author Terry Jia
 * @author Gregory Amerson
 */
@Component(scope = ServiceScope.SINGLETON, service = UpgradeCompare.class)
public class UpgradeCompareImpl implements UpgradeCompare {

	public void openCompareEditor(File soruce, File target) {
		CompareItem sourceCompareItem = new CompareItem(soruce);
		CompareItem targetCompareItem = new CompareItem(target);

		CompareConfiguration compareConfiguration = new CompareConfiguration();

		compareConfiguration.setLeftLabel("Original file");
		compareConfiguration.setRightLabel("Upgraded file");

		CompareEditorInput compareEditorInput = new CompareEditorInput(compareConfiguration) {

			@Override
			protected Object prepareInput(IProgressMonitor monitor)
				throws InterruptedException, InvocationTargetException {

				return new DiffNode(sourceCompareItem, targetCompareItem);
			}

		};

		compareEditorInput.setTitle("Compare ('" + soruce.getName() + "'-'" + target.getName() + "')");

		CompareUI.openCompareEditor(compareEditorInput);
	}

	private class CompareItem implements ITypedElement, IStreamContentAccessor, IModificationDate, IEditableContent {

		public CompareItem(File file) {
			_file = file;
		}

		@Override
		public InputStream getContents() throws CoreException {
			try {
				return Files.newInputStream(_file.toPath());
			}
			catch (Exception e) {
			}

			return null;
		}

		@Override
		public Image getImage() {
			return null;
		}

		@Override
		public long getModificationDate() {
			return 0;
		}

		@Override
		public String getName() {
			return _file.getName();
		}

		@Override
		public String getType() {
			return TEXT_TYPE;
		}

		@Override
		public boolean isEditable() {
			return false;
		}

		@Override
		public ITypedElement replace(ITypedElement dest, ITypedElement src) {
			return null;
		}

		@Override
		public void setContent(byte[] newContent) {
		}

		private File _file;

	}

}