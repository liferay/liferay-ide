/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 * Contributors:
 * 		Gregory Amerson - initial implementation
 *******************************************************************************/

package com.liferay.ide.eclipse.properties.ui.editor;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.ui.PartInitException;

/**
 * @author Gregory Amerson
 */
public class PluginPackagePropertiesEditor extends SapphireEditor
{

	private static final String EDITOR_DEFINITION_PATH =
		"com.liferay.ide.eclipse.hook.ui/com/liferay/ide/eclipse/hook/ui/editor/hook-editor.sdef/HookConfigurationPage";

	public static final String ID = "com.liferay.ide.eclipse.hook.ui.editor.HookXmlEditor";

	protected boolean customModelDirty = false;

	/**
	 * 
	 */
	public PluginPackagePropertiesEditor()
	{
		super( ID );
	}

	@Override
	protected IModelElement createModel()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createSourcePages() throws PartInitException
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void createFormPages() throws PartInitException
	{
		// TODO Auto-generated method stub

	}



}
