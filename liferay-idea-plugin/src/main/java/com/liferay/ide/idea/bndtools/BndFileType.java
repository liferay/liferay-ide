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

package com.liferay.ide.idea.bndtools;

import static com.intellij.openapi.fileTypes.impl.AbstractFileType.ELEMENT_HIGHLIGHTING;

import com.intellij.ide.highlighter.custom.SyntaxTable;
import com.intellij.openapi.fileTypes.impl.AbstractFileType;
import com.intellij.openapi.fileTypes.impl.CustomSyntaxTableFileType;
import com.intellij.openapi.vfs.VirtualFile;

import com.liferay.ide.idea.util.LiferayIcons;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.Icon;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Dominik Marks
 * @author Joye Luo
 */
public class BndFileType implements CustomSyntaxTableFileType {

	public static final BndFileType INSTANCE = new BndFileType();

	public BndFileType() {
		try (InputStream inputStream = BndFileType.class.getResourceAsStream("/META-INF/bnd.xml")) {
			if (inputStream != null) {
				SAXBuilder saxBuilder = new SAXBuilder();

				Document document = saxBuilder.build(inputStream);

				Element root = document.getRootElement();

				Element highlighting = root.getChild(ELEMENT_HIGHLIGHTING);

				if (highlighting != null) {
					_syntaxTable = AbstractFileType.readSyntaxTable(highlighting);
				}
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		catch (JDOMException jdome) {
			jdome.printStackTrace();
		}
	}

	@Nullable
	@Override
	public String getCharset(@NotNull VirtualFile file, @NotNull byte[] content) {
		return null;
	}

	@NotNull
	@Override
	public String getDefaultExtension() {
		return "bnd";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "BND file";
	}

	@Nullable
	@Override
	public Icon getIcon() {
		return LiferayIcons.BND_ICON;
	}

	@NotNull
	@Override
	public String getName() {
		return "BND file";
	}

	@Override
	public SyntaxTable getSyntaxTable() {
		return _syntaxTable;
	}

	@Override
	public boolean isBinary() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	private SyntaxTable _syntaxTable = null;

}