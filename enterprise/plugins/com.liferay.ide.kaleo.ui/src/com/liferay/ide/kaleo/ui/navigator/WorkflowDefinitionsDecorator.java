/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.ui.navigator;

import com.liferay.ide.kaleo.core.KaleoAPIException;
import com.liferay.ide.kaleo.ui.KaleoImages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Greg Amerson
 */
public class WorkflowDefinitionsDecorator extends LabelProvider implements ILightweightLabelDecorator
// implements IColorProvider, IFontProvider, IStyledLabelProvider
{

    private static WorkflowDefinitionsDecorator instance;

    private static final String WORKFLOW_DEFINITIONS_FOLDER_NAME = "Kaleo Workflows";

    public static WorkflowDefinitionsDecorator getDefault()
    {
        return instance;
    }

    public WorkflowDefinitionsDecorator()
    {
        super();
    }

    protected String combine( int version, int draftVersion )
    {
        if( draftVersion == -1 )
        {
            return "  [Version: " + version + "]";
        }

        return "  [Version: " + version + ", Draft Version: " + draftVersion + "]";
    }

    public void decorate( Object element, IDecoration decoration )
    {
        if( element instanceof WorkflowDefinitionEntry )
        {
            WorkflowDefinitionEntry definition = (WorkflowDefinitionEntry) element;

            if( !definition.isLoadingNode() )
            {
                int version = definition.getVersion();
                int draftVersion = definition.getDraftVersion();

                decoration.addSuffix( combine( version, draftVersion ) );
            }
        }
        else if( element instanceof WorkflowDefinitionsFolder )
        {
            WorkflowDefinitionsFolder folder = (WorkflowDefinitionsFolder) element;

            IStatus status = folder.getStatus();

            if( status != null )
            {
                if( status.getException() instanceof KaleoAPIException )
                {
                    decoration.addSuffix( "  [Error API unavailable. Ensure kaleo-designer-portlet is deployed.]" );
                    decoration.addOverlay( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                        ISharedImages.IMG_DEC_FIELD_ERROR ) );
                }
                else
                {
                    decoration.addSuffix( "  [" + status.getMessage() + "]" );
                }
            }
            else
            {
                decoration.addSuffix( "" );
            }
        }
    }

    @Override
    public Image getImage( Object element )
    {
        if( element instanceof WorkflowDefinitionsFolder )
        {
            return KaleoImages.IMG_WORKFLOW_DEFINITIONS_FOLDER;
        }
        else if( element instanceof WorkflowDefinitionEntry )
        {
            WorkflowDefinitionEntry definition = (WorkflowDefinitionEntry) element;

            if( definition.isLoadingNode() )
            {
                return KaleoImages.IMG_LOADING;
            }
            else
            {
                return KaleoImages.IMG_WORKFLOW_DEFINITION;
            }
        }

        return null;
    }

    public StyledString getStyledText( Object element )
    {
        if( element instanceof WorkflowDefinitionsFolder )
        {
            return new StyledString( WORKFLOW_DEFINITIONS_FOLDER_NAME );
        }
        else if( element instanceof WorkflowDefinitionEntry )
        {
            WorkflowDefinitionEntry definitionNode = (WorkflowDefinitionEntry) element;
            return new StyledString( definitionNode.getName() );
        }
        else
        {
            return null;
        }
    }

    @Override
    public String getText( Object element )
    {
        if( element instanceof WorkflowDefinitionsFolder )
        {
            return WORKFLOW_DEFINITIONS_FOLDER_NAME;
        }
        else if( element instanceof WorkflowDefinitionEntry )
        {
            WorkflowDefinitionEntry definitionNode = (WorkflowDefinitionEntry) element;

            return definitionNode.getName();
        }
        else
        {
            return null;
        }
    }

}
