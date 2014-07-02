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
package com.liferay.ide.portal.ui.templates;

import org.eclipse.jface.text.templates.SimpleTemplateVariableResolver;
import org.eclipse.jface.text.templates.TemplateContext;


/**
 * @author Gregory Amerson
 */
public class StructuresTemplateVariables
{

    public static class NameSuffix extends SimpleTemplateVariableResolver
    {
        private static int MIN = 10000;
        private static int MAX = 65535;
//        private static Map<Integer, Boolean> used = new HashMap<Integer, Boolean>();

        /**
         * Creates a new user name variable
         */
        public NameSuffix()
        {
            super( "nameSuffix", "Dynamic name suffix" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        /**
         * {@inheritDoc}
         */
        protected String resolve(TemplateContext context)
        {
            int rand = rand();

//            while( used.get( rand ) != null )
//            {
//                rand = rand();
//            }

//            used.put( rand, true );

            return String.valueOf( rand );
        }

        private int rand()
        {
            return MIN + (int)(Math.random() * ((MAX - MIN) + 1));
        }
    }

}
