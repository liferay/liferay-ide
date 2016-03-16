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

package com.liferay.ide.gradle.ui.workspace;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.ICommonFilterDescriptor;
import org.eclipse.ui.navigator.INavigatorFilterService;

/**
 * @author Andy Wu
 */
public class ProjectExplorerLayoutUtil
{

    private static String NEST_PARAMETER = "org.eclipse.ui.navigator.resources.nested.enabled";

    public static void setNestedEnabled()
    {
        final String commandId = "org.eclipse.ui.navigator.resources.nested.changeProjectPresentation";

        try
        {
            final ICommandService commandService =
                (ICommandService) PlatformUI.getWorkbench().getService( ICommandService.class );

            final Command command = commandService.getCommand( commandId );

            final IHandler hanlder = command.getHandler();

            final IViewPart projectExplorer =
                PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage().findView(
                    IPageLayout.ID_PROJECT_EXPLORER );

            if( hanlder != null && projectExplorer != null )
            {
                final Map<String, String> map = new HashMap<String, String>();

                map.put( NEST_PARAMETER, "true" );

                IEvaluationContext applicationContext = new EvaluationContext( null, new Object() );

                applicationContext.addVariable( ISources.ACTIVE_PART_NAME, projectExplorer );

                final ExecutionEvent event = new ExecutionEvent( command, map, null, applicationContext );

                execute( event );
            }
        }
        catch( ExecutionException e )
        {
            // ignore errors this is best effort.
        }
    }

    // copy from org.eclipse.ui.internal.navigator.resources.nested.ProjectPresentationHandler (mars)
    private static void execute( ExecutionEvent event ) throws ExecutionException
    {
        IWorkbenchPart part = HandlerUtil.getActivePart( event );

        String nestedProjectsContentProviderExtensionId =
            "org.eclipse.ui.navigator.resources.nested.nestedProjectContentProvider";

        String hideTopLevelProjectIfNested = "org.eclipse.ui.navigator.resources.nested.HideTopLevelProjectIfNested";

        String hideFolderWhenProjectIsShownAsNested =
            "org.eclipse.ui.navigator.resources.nested.HideFolderWhenProjectIsShownAsNested";

        if( part instanceof CommonNavigator )
        {
            CommonNavigator navigator = (CommonNavigator) part;

            boolean previousNest =
                navigator.getNavigatorContentService().getActivationService().isNavigatorExtensionActive(
                    nestedProjectsContentProviderExtensionId );
            String newNestParam = event.getParameter( NEST_PARAMETER );
            boolean newNest = false;

            if( newNestParam != null )
            {
                newNest = Boolean.parseBoolean( newNestParam );
            }

            if( newNest != previousNest )
            {
                ISelection initialSelection = navigator.getCommonViewer().getSelection();
                INavigatorFilterService filterService = navigator.getNavigatorContentService().getFilterService();
                Set<String> filters = new HashSet<String>();

                for( ICommonFilterDescriptor desc : filterService.getVisibleFilterDescriptors() )
                {
                    if( filterService.isActive( desc.getId() ) )
                    {
                        filters.add( desc.getId() );
                    }
                }

                if( newNest )
                {
                    navigator.getNavigatorContentService().getActivationService().activateExtensions(
                        new String[] { nestedProjectsContentProviderExtensionId }, false );
                    filters.add( hideTopLevelProjectIfNested );
                    filters.add( hideFolderWhenProjectIsShownAsNested );
                }
                else
                {
                    navigator.getNavigatorContentService().getActivationService().deactivateExtensions(
                        new String[] { nestedProjectsContentProviderExtensionId }, false );
                    filters.remove( hideTopLevelProjectIfNested );
                    filters.remove( hideFolderWhenProjectIsShownAsNested );
                }

                filterService.activateFilterIdsAndUpdateViewer( filters.toArray( new String[filters.size()] ) );
                navigator.getNavigatorContentService().getActivationService().persistExtensionActivations();
                navigator.getCommonViewer().refresh();
                navigator.getCommonViewer().setSelection( initialSelection );
            }

            HandlerUtil.updateRadioState( event.getCommand(), Boolean.toString( newNest ) );
        }
    }
}
