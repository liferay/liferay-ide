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

package com.liferay.ide.server.ui.navigator;

import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.BundleAPIException;
import com.liferay.ide.server.core.portal.OsgiBundle;
import com.liferay.ide.server.core.portal.OsgiConnection;
import com.liferay.ide.server.ui.BundlesImages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.IServerModule;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 */
public class BundlesDecorator extends LabelProvider implements ILightweightLabelDecorator
{

    private static final String LOADING = "Loading...";

    private static BundlesDecorator instance;

    private static final String BUNDLES_FOLDER_NAME = "OSGi Bundles";

    public static BundlesDecorator getDefault()
    {
        return instance;
    }

    public BundlesDecorator()
    {
        super();
    }

    protected String combine( String id, String state, String version )
    {
        return " " + state + ", Version: " + version;
    }

    public void decorate( Object element, IDecoration decoration )
    {
        if( element instanceof OsgiBundle )
        {
            OsgiBundle bundle = (OsgiBundle) element;

            if( ! ( bundle instanceof OsgiBundleLoading ) )
            {
                String id = bundle.getId();
                String state = bundle.getState();
                Version version = bundle.getVersion();

                decoration.addSuffix( combine( id, state, version.toString() ) );
            }
        }
        else if( element instanceof BundlesFolder )
        {
            BundlesFolder folder = (BundlesFolder) element;

            IStatus status = folder.getStatus();

            if( status != null )
            {
                if( status.getException() instanceof BundleAPIException )
                {
                    decoration.addSuffix( " [Error Bundle API unavailable.]" );
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
        else if( element instanceof IServerModule )
        {
            final IServerModule module = (IServerModule) element;

            final IServer server = module.getServer();

            if( server.getServerState() == IServer.STATE_STARTED )
            {
                final OsgiConnection osgi = LiferayServerCore.newOsgiConnection( module.getServer() );

                //TODO this chould be cached somehow?
                for( OsgiBundle bundle : osgi.getBundles() )
                {
                    if( module.getModule()[0].getName().equals( bundle.getSymbolicName() ) )
                    {
                        String id = bundle.getId();
                        String state = bundle.getState();
                        Version version = bundle.getVersion();

                        decoration.addSuffix( combine( id, state, version.toString() ) );
                    }
                }
            }
        }
    }

    @Override
    public Image getImage( Object element )
    {
        if( element instanceof BundlesFolder )
        {
            return BundlesImages.IMG_BUNDLES_FOLDER;
        }
        else if( element instanceof OsgiBundle )
        {
            OsgiBundle bundle = (OsgiBundle) element;

            if( bundle instanceof OsgiBundleLoading )
            {
                return BundlesImages.IMG_LOADING;
            }
            else
            {
                return BundlesImages.IMG_BUNDLE;
            }
        }

        return null;
    }

    public StyledString getStyledText( Object element )
    {
        if( element instanceof BundlesFolder )
        {
            return new StyledString( BUNDLES_FOLDER_NAME );
        }
        else if( element instanceof OsgiBundleLoading )
        {
            return new StyledString( LOADING );
        }
        else if( element instanceof OsgiBundle )
        {
            OsgiBundle bundle = (OsgiBundle) element;
            return new StyledString( bundle.getSymbolicName() );
        }
        else
        {
            return null;
        }
    }

    @Override
    public String getText( Object element )
    {
        if( element instanceof BundlesFolder )
        {
            return BUNDLES_FOLDER_NAME;
        }
        else if( element instanceof OsgiBundleLoading )
        {
            return LOADING;
        }
        else if( element instanceof OsgiBundle )
        {
            OsgiBundle definitionNode = (OsgiBundle) element;

            return definitionNode.getSymbolicName();
        }
        else
        {
            return null;
        }
    }

}
