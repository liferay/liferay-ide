/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.eclipse.hook.ui.action;

import com.liferay.ide.eclipse.sdk.ISDKConstants;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphirePropertyEditor;
import org.eclipse.sapphire.ui.SapphirePropertyEditorCondition;

public class FileResourceCondtion extends SapphirePropertyEditorCondition {

	public FileResourceCondtion() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphireCondition#initCondition(org.eclipse.sapphire.ui.ISapphirePart,
	 * java.lang.String)
	 */
	@Override
	protected void initCondition( ISapphirePart part, String parameter ) {
		// TODO Auto-generated method stub
		super.initCondition( part, parameter );
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.sapphire.ui.SapphirePropertyEditorCondition#evaluate(org.eclipse.sapphire.ui.SapphirePropertyEditor)
	 */
	@Override
	protected boolean evaluate( SapphirePropertyEditor part ) {
		final IModelElement element = part.getModelElement();
		final ModelProperty property = part.getProperty();
		if ( property instanceof ValueProperty && Path.class.isAssignableFrom( property.getTypeClass() ) ) {
			final Value<Path> path = element.read( (ValueProperty) property );
			if ( path != null && path != null && path.getText() != null && path.getText().length() > 0 )
			{
				IProject project = element.adapt( IProject.class );
				IResource docRootResource = project.findMember( ISDKConstants.DEFAULT_WEBCONTENT_FOLDER );
				if ( docRootResource != null ) {
					IFolder docrootFolder = (IFolder) docRootResource;
					String fileResourceStr = path.getContent().toPortableString();
					IResource fileResource = docrootFolder.findMember( fileResourceStr );
					return ( fileResource == null );
				}
			}
		}
		return false;
	}
}
