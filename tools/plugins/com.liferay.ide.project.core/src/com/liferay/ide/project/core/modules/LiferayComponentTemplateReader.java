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
 *******************************************************************************/

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.ExtensionReader;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.templates.AbstractLiferayComponentTemplate;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Simon Jiang
 */
@SuppressWarnings( "rawtypes" )
public class LiferayComponentTemplateReader extends ExtensionReader<IComponentTemplate>
{

    private static final String EXTENSION = "liferayComponentTemplates";
    private static final String COMPONENT_TEMPLATE = "liferayComponentTemplate";
    private static final String COMPONENT_TEMPLATE_DISPLAY_NAME_ELEMENT = "displayName";
    private static final String COMPONENT_TEMPLATE_SHORT_NAME_ELEMENT = "shortName";

    public LiferayComponentTemplateReader()
    {
        super( ProjectCore.PLUGIN_ID, EXTENSION, COMPONENT_TEMPLATE );
    }

    @Override
    protected IComponentTemplate initElement(
        IConfigurationElement configElement, IComponentTemplate componentTemplate )
    {
        final AbstractLiferayComponentTemplate template = (AbstractLiferayComponentTemplate) componentTemplate;

        template.setDisplayName( configElement.getAttribute( COMPONENT_TEMPLATE_DISPLAY_NAME_ELEMENT ) );
        template.setShortName( configElement.getAttribute( COMPONENT_TEMPLATE_SHORT_NAME_ELEMENT ) );

        return template;
    }

    public IComponentTemplate[] getComponentTemplates()
    {
        return getExtensions().toArray( new IComponentTemplate[0] );
    }

}
