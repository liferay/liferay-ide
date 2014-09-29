/*******************************************************************************
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
 *
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.layouttpl.core.operation;

import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public interface INewLayoutTplDataModelProperties extends IArtifactEditOperationDataModelProperties
{
    public static final String LAYOUT_IMAGE_1_2_1_COLUMN = "INewLayoutTplModelProperties.LAYOUT_IMAGE_1_2_1_COLUMN"; //$NON-NLS-1$

    public static final String LAYOUT_IMAGE_1_2_I_COLUMN = "INewLayoutTplModelProperties.LAYOUT_IMAGE_1_2_I_COLUMN"; //$NON-NLS-1$

    public static final String LAYOUT_IMAGE_1_2_II_COLUMN = "INewLayoutTplModelProperties.LAYOUT_IMAGE_1_2_II_COLUMN"; //$NON-NLS-1$

    public static final String LAYOUT_IMAGE_1_COLUMN = "INewLayoutTplModelProperties.LAYOUT_IMAGE_1_COLUMN"; //$NON-NLS-1$

    public static final String LAYOUT_IMAGE_2_2_COLUMN = "INewLayoutTplModelProperties.LAYOUT_IMAGE_2_2_COLUMN"; //$NON-NLS-1$

    public static final String LAYOUT_IMAGE_2_I_COLUMN = "INewLayoutTplModelProperties.LAYOUT_IMAGE_2_I_COLUMN"; //$NON-NLS-1$

    public static final String LAYOUT_IMAGE_2_II_COLUMN = "INewLayoutTplModelProperties.LAYOUT_IMAGE_2_II_COLUMN"; //$NON-NLS-1$

    public static final String LAYOUT_IMAGE_2_III_COLUMN = "INewLayoutTplModelProperties.LAYOUT_IMAGE_2_III_COLUMN"; //$NON-NLS-1$

    public static final String LAYOUT_IMAGE_3_COLUMN = "INewLayoutTplModelProperties.LAYOUT_IMAGE_3_COLUMN"; //$NON-NLS-1$

    public static final String[] LAYOUT_PROPERTIES = new String[] { LAYOUT_IMAGE_1_COLUMN,
        LAYOUT_IMAGE_1_2_I_COLUMN, LAYOUT_IMAGE_1_2_II_COLUMN, LAYOUT_IMAGE_1_2_1_COLUMN, LAYOUT_IMAGE_2_I_COLUMN,
        LAYOUT_IMAGE_2_II_COLUMN, LAYOUT_IMAGE_2_III_COLUMN, LAYOUT_IMAGE_2_2_COLUMN, LAYOUT_IMAGE_3_COLUMN };

    public static final String LAYOUT_TEMPLATE_FILE = "INewLayoutTplModelProperties.LAYOUT_TEMPLATE_FILE"; //$NON-NLS-1$

    public static final String LAYOUT_TEMPLATE_ID = "INewLayoutTplModelProperties.LAYOUT_TEMPLATE_ID"; //$NON-NLS-1$

    public static final String LAYOUT_TEMPLATE_NAME = "INewLayoutTplModelProperties.LAYOUT_TEMPLATE_NAME"; //$NON-NLS-1$

    public static final String LAYOUT_THUMBNAIL_FILE = "INewLayoutTplModelProperties.LAYOUT_THUMBNAIL_FILE"; //$NON-NLS-1$

    public static final String LAYOUT_TPL_FILE_CREATED = "INewLayoutTplModelProperties.LAYOUT_TPL_FILE_CREATED"; //$NON-NLS-1$

    public static final String LAYOUT_WAP_TEMPLATE_FILE = "INewLayoutTplModelProperties.LAYOUT_WAP_TEMPLATE_FILE"; //$NON-NLS-1$

    public static final String LAYOUTTPL_DESCRIPTOR_TEMPLATE = "com.liferay.ide.templates.layouttpl.descriptor"; //$NON-NLS-1$
}
