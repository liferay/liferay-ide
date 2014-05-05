/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.project.core.UpgradeProjectHandler;
import com.liferay.ide.project.core.UpgradeProjectHandlerReader;
import com.liferay.ide.project.core.model.NamedItem;
import com.liferay.ide.project.core.model.UpgradeLiferayProjectsOp;
import com.liferay.ide.ui.navigator.AbstractLabelProvider;
import com.liferay.ide.ui.util.UIUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.swt.graphics.Color;

/**
 * @author Simon Jiang
 */
public class ProjectUpgradeActionCheckboxCustomPart extends AbstractCheckboxCustomPart
{
    private static Map<String,String> handlerMaps = new  HashMap<String,String>();

    static
    {
        UpgradeProjectHandlerReader upgradeLiferayProjectActionReader = new UpgradeProjectHandlerReader();
        handlerMaps = getUpgradeHandlers( upgradeLiferayProjectActionReader.getUpgradeActions() );
    }

    class ProjectActionUpgradeLabelProvider extends AbstractLabelProvider implements IColorProvider, IStyledLabelProvider
    {
        public Color getBackground( Object element )
        {
            return null;
        }


        public Color getForeground( Object element )
        {
            return null;
        }

        public StyledString getStyledText( Object element )
        {
            if( element instanceof CheckboxElement )
            {
                return new StyledString( ( ( CheckboxElement ) element ).context );
            }
            return null;

        }

        @Override
        public String getText( Object element )
        {
            if( element instanceof CheckboxElement )
            {
                return ( (CheckboxElement) element ).context;
            }

            return super.getText( element );
        }


        @Override
        protected void initalizeImageRegistry( ImageRegistry registry )
        {
        }

    }

    private static HashMap<String, String> getUpgradeHandlers( List<UpgradeProjectHandler> upgradeActions )
    {
        HashMap<String, String> actionMaps = new HashMap<String,String>();

        for( UpgradeProjectHandler upgradeHandler : upgradeActions)
        {
            actionMaps.put( upgradeHandler.getName(), upgradeHandler.getDescription() );
        }

        return actionMaps;
    }

    @Override
    protected void checkAndUpdateCheckboxElement()
    {
        final List<CheckboxElement> checkboxElementList = new ArrayList<CheckboxElement>();
        handlerMaps.keySet().iterator();
        String  context = null;

        for (String handlerName : handlerMaps.keySet())
        {
            context = handlerMaps.get( handlerName );
            CheckboxElement checkboxElement = new CheckboxElement( handlerName, context );
            checkboxElementList.add( checkboxElement );
        }

        checkboxElements = checkboxElementList.toArray( new CheckboxElement[checkboxElementList.size()]);

        UIUtil.async
        (
            new Runnable()
            {
                public void run()
                {
                    checkBoxViewer.setInput( checkboxElements );
                    Iterator<NamedItem> iterator = op().getSelectedActions().iterator();

                    while( iterator.hasNext() )
                    {
                        final NamedItem upgradeAction = iterator.next();

                        for( CheckboxElement checkboxElement : checkboxElements )
                        {
                            if ( checkboxElement.name.equals( upgradeAction.getName().content() ))
                            {
                                checkBoxViewer.setChecked( checkboxElement, true );
                                break;
                            }
                        }
                    }

                    updateValidation();
                }
            }
        );
    }

    @Override
    protected ElementList<NamedItem> getCheckboxList()
    {
        return op().getSelectedActions();
    }

    @Override
    protected IStyledLabelProvider getLableProvider()
    {
        return new ProjectActionUpgradeLabelProvider();
    }

    @Override
    protected void handleCheckStateChangedEvent( CheckStateChangedEvent event )
    {
        if( event.getSource().equals( checkBoxViewer ) )
        {
            final Object element = event.getElement();

            if( element instanceof CheckboxElement )
            {
                checkBoxViewer.setGrayed( element, false );
            }

            op().getSelectedActions().clear();

            for( CheckboxElement checkboxElement : checkboxElements )
            {
                if( checkBoxViewer.getChecked( checkboxElement ) )
                {
                    final NamedItem newUpgradeAction = op().getSelectedActions().insert();
                    newUpgradeAction.setName( checkboxElement.name );
                }

            }

            updateValidation();
        }
    }

    private UpgradeLiferayProjectsOp op()
    {
        return getLocalModelElement().nearest( UpgradeLiferayProjectsOp.class );
    }

    @Override
    protected void updateValidation()
    {
        retval = Status.createOkStatus();

        if( op().getSelectedActions().size() < 1 )
        {

            retval = Status.createErrorStatus( "At least one upgrade action must be specified " );
        }

        refreshValidation();
    }
}
