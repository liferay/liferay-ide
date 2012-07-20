/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.project.ui.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.IProjectDefinition;
import com.liferay.ide.project.core.ProjectCorePlugin;
import com.liferay.ide.project.ui.ProjectUIPlugin;
import com.liferay.ide.ui.action.NewWizardAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;
import org.eclipse.ui.PlatformUI;

/**
 * @author Greg Amerson
 */
public class NewPluginProjectDropDownAction extends Action implements IMenuCreator, IWorkbenchWindowPulldownDelegate2
{

    private final class ProjectDefComparator implements Comparator<IProjectDefinition>
    {
        public int compare( IProjectDefinition o1, IProjectDefinition o2 )
        {
            int index1 = o1.getMenuIndex();
            int index2 = o2.getMenuIndex();

            return index1 < index2 ? -1 : index1 > index2 ? 1 : 0;
        }
    }

    protected final static String PL_NEW = "newWizards"; //$NON-NLS-1$
    protected final static String TAG_CLASS = "class"; //$NON-NLS-1$
    protected final static String TAG_NAME = "name";//$NON-NLS-1$
    protected final static String TAG_PARAMETER = "parameter";//$NON-NLS-1$
    protected final static String TAG_VALUE = "value";//$NON-NLS-1$
    protected final static String TAG_WIZARD = "wizard";//$NON-NLS-1$

    protected Menu fMenu;

    protected Shell fWizardShell;

    public NewPluginProjectDropDownAction()
    {
        fMenu = null;
        setMenuCreator( this );
    }

    public void dispose()
    {
    }

    public NewWizardAction[] getNewProjectActions()
    {
        ArrayList<NewWizardAction> containers = new ArrayList<NewWizardAction>();

        IExtensionPoint extensionPoint =
            Platform.getExtensionRegistry().getExtensionPoint( PlatformUI.PLUGIN_ID, PL_NEW );

        if( extensionPoint != null )
        {
            IConfigurationElement[] elements = extensionPoint.getConfigurationElements();

            for( IConfigurationElement element : elements )
            {
                if( element.getName().equals( TAG_WIZARD ) && isProjectWizard( element, getTypeAttribute() ) )
                {
                    containers.add( new NewWizardAction( element ) );

                    IProjectDefinition[] projectDefinitions = ProjectCorePlugin.getProjectDefinitions();

                    List<IProjectDefinition> projectDefList = Arrays.asList( projectDefinitions );

                    Collections.sort( projectDefList, new ProjectDefComparator() );

                    for( IProjectDefinition projectDef : projectDefinitions )
                    {
                        NewWizardAction wizardAction = new NewWizardAction( element );
                        wizardAction.setProjectType( projectDef.getFacetId() );

                        if( projectDef != null )
                        {
                            wizardAction.setImageDescriptor( ImageDescriptor.createFromURL( ProjectUIPlugin.getDefault().getBundle().getEntry(
                                "/icons/n16/" + projectDef.getShortName() + "_new.png" ) ) );
                            wizardAction.setText( wizardAction.getText().replaceAll(
                                "Liferay Plugin", projectDef.getDisplayName() + " Plugin" ) );
                        }

                        containers.add( wizardAction );
                    }
                }
            }
        }

        NewWizardAction[] actions = (NewWizardAction[]) containers.toArray( new NewWizardAction[containers.size()] );

        Arrays.sort( actions );

        return actions;
    }

    public NewWizardAction[] getExtraProjectActions()
    {
        ArrayList<NewWizardAction> containers = new ArrayList<NewWizardAction>();

        IExtensionPoint extensionPoint =
            Platform.getExtensionRegistry().getExtensionPoint( PlatformUI.PLUGIN_ID, PL_NEW );

        if( extensionPoint != null )
        {
            IConfigurationElement[] elements = extensionPoint.getConfigurationElements();

            for( IConfigurationElement element : elements )
            {
                if( element.getName().equals( TAG_WIZARD ) && isProjectWizard( element, getExtraTypeAttribute() ) )
                {
                    containers.add( new NewWizardAction( element ) );
                }
            }
        }

        NewWizardAction[] actions = (NewWizardAction[]) containers.toArray( new NewWizardAction[containers.size()] );

        Arrays.sort( actions );

        return actions;
    }

    public Action getDefaultAction()
    {
        Action[] actions = getNewProjectActions();

        if( actions.length > 0 )
        {
            return actions[0];
        }

        return null;
    }

    public Menu getMenu( Control parent )
    {
        if( fMenu == null )
        {
            fMenu = new Menu( parent );

            NewWizardAction[] actions = getNewProjectActions();

            // Separator separator = null;
            //
            // for (NewWizardAction action : actions) {
            // action.setShell(fWizardShell);
            //
            // ActionContributionItem item = new ActionContributionItem(action);
            // item.fill(fMenu, -1);
            //
            // if (separator == null) {
            // separator = new Separator();
            // separator.fill(fMenu, -1);
            // }
            // }

            // only do the first project action (not the 5 separate ones)
            actions[0].setShell( fWizardShell );
            ActionContributionItem item = new ActionContributionItem( actions[0] );
            item.fill( fMenu, -1 );

            new Separator().fill( fMenu, -1 );

            NewWizardAction importAction = new ImportLiferayProjectWizardAction();
            importAction.setShell( fWizardShell );
            item = new ActionContributionItem( importAction );
            item.fill( fMenu, -1 );

            NewWizardAction[] extraActions = getExtraProjectActions();

            for( NewWizardAction extraAction : extraActions )
            {
                extraAction.setShell( fWizardShell );

                ActionContributionItem extraItem = new ActionContributionItem( extraAction );
                extraItem.fill( fMenu, -1 );
            }
        }

        return fMenu;
    }

    public Menu getMenu( Menu parent )
    {
        return null;
    }

    public void init( IWorkbenchWindow window )
    {
        fWizardShell = window.getShell();
    }

    public void run( IAction action )
    {
        getDefaultAction().run();
    }

    public void selectionChanged( IAction action, ISelection selection )
    {
    }

    // private String[] getTypes(IConfigurationElement element) {
    // IConfigurationElement[] classElements = element.getChildren(TAG_CLASS);
    //
    // if (classElements.length > 0) {
    // for (IConfigurationElement classElement : classElements) {
    // IConfigurationElement[] paramElements =
    // classElement.getChildren(TAG_PARAMETER);
    //
    // for (IConfigurationElement paramElement : paramElements) {
    // if ("types".equals(paramElement.getAttribute(TAG_NAME))) {
    // return paramElement.getAttribute(TAG_VALUE).split(",");
    // }
    // }
    // }
    // }
    //
    // return new String[0];
    // }

    private boolean isProjectWizard( IConfigurationElement element, String typeAttribute )
    {
        IConfigurationElement[] classElements = element.getChildren( TAG_CLASS );

        if( ( !CoreUtil.isNullOrEmpty( typeAttribute ) ) && classElements.length > 0 )
        {
            for( IConfigurationElement classElement : classElements )
            {
                IConfigurationElement[] paramElements = classElement.getChildren( TAG_PARAMETER );

                for( IConfigurationElement paramElement : paramElements )
                {
                    if( typeAttribute.equals( paramElement.getAttribute( TAG_NAME ) ) )
                    {
                        return Boolean.valueOf( paramElement.getAttribute( TAG_VALUE ) ).booleanValue();
                    }
                }
            }
        }

        // old way, deprecated
        if( Boolean.valueOf( element.getAttribute( getTypeAttribute() ) ).booleanValue() )
        {
            return true;
        }

        return false;
    }

    protected String getExtraTypeAttribute()
    {
        return "liferay_extra_project";
    }

    protected String getTypeAttribute()
    {
        return "liferay_project";
    }
}
