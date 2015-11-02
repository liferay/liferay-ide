package com.liferay.ide.ui.workspace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.internal.ide.ChooseWorkspaceData;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;


/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class LaunchWorkspaceMenu extends ExtensionContributionFactory
{

    @Override
    public void createContributionItems( IServiceLocator serviceLocator, IContributionRoot additions )
    {
        final MenuManager menu = new MenuManager( "Launch Workspace" );

        menu.add( new LaunchLiferay7WorkspaceAction() );

        menu.add( new Separator() );

        final IContributionItem[] workspaceHistoryItems = buildWorkspaceHistory( serviceLocator );

        for( IContributionItem workspaceHistoryItem : workspaceHistoryItems )
        {
            if( workspaceHistoryItem instanceof CommandContributionItem )
            {
                final CommandContributionItem commandItem = (CommandContributionItem) workspaceHistoryItem;

                if( "Other...".equals( commandItem.getData().label ) )
                {
                    menu.add( new Separator() );
                }
            }

            menu.add( workspaceHistoryItem );
        }

        additions.addContributionItem( menu, null );
    }

    private IContributionItem[] buildWorkspaceHistory( IServiceLocator serviceLocator )
    {
        final List<IContributionItem> retval = new ArrayList<>();

        final ChooseWorkspaceData chooseWorkspaceData =
            new ChooseWorkspaceData( Platform.getInstanceLocation().getURL() );

        if( chooseWorkspaceData.readPersistedData() )
        {
            final String currentWorkspace = chooseWorkspaceData.getInitialDefault();
            final String[] recentWorkspaces = chooseWorkspaceData.getRecentWorkspaces();

            for( String recentWorkspace : recentWorkspaces )
            {
                if( recentWorkspace != null && !recentWorkspace.equals( currentWorkspace ) )
                {
                    retval.add( newLaunchWorkspaceCommand( serviceLocator, recentWorkspace, recentWorkspace ) );
                }
            }

            retval.add( newLaunchWorkspaceCommand( serviceLocator, "Other...", null ) );
        }

        return retval.toArray( new IContributionItem[0] );
    }

    @SuppressWarnings( "unchecked" )
    private IContributionItem newLaunchWorkspaceCommand(
        IServiceLocator serviceLocator, String label, String workspaceLocation )
    {
        final CommandContributionItemParameter parameter = new CommandContributionItemParameter(
            serviceLocator, "", LaunchWorkspaceHandler.COMMAND_ID, CommandContributionItem.STYLE_PUSH );

        if( workspaceLocation != null )
        {
            parameter.parameters = new HashMap<>();
            parameter.parameters.put( LaunchWorkspaceHandler.PARAM_WORKSPACE_LOCATION, workspaceLocation );
        }

        parameter.label = label;

        final CommandContributionItem launchWorkspaceCommand = new CommandContributionItem( parameter );

        launchWorkspaceCommand.setVisible( true );

        return launchWorkspaceCommand;
    }

}
