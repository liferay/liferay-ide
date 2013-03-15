/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 *      Kamesh Sampath - initial implementation
 *      Gregory Amerson - initial implementation review and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.portlet.ui.navigator;

import com.liferay.ide.portlet.core.model.Portlet;
import com.liferay.ide.portlet.core.model.PortletApp;
import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class PortletsNode
{

    private static final Object[] EMPTY = new Object[] {};

    private PortletApp modelElement = null;
    private PortletResourcesRootNode parent;

    public PortletsNode( PortletResourcesRootNode parent )
    {
        this.parent = parent;
    }

    public Object[] getChildren()
    {
        if( this.getPortletAppModelElement() != null )
        {
            final List<PortletNode> portletNodes = new ArrayList<PortletNode>();

            for( Portlet portlet : this.getPortletAppModelElement().getPortlets() )
            {
                portletNodes.add( new PortletNode( this, portlet ) );
            }

            return portletNodes.toArray( new PortletNode[0] );
        }

        return EMPTY;
    }

    public PortletResourcesRootNode getParent()
    {
        return this.parent;
    }

    private PortletApp getPortletAppModelElement()
    {
        if( this.modelElement == null )
        {
            IFile portletXmlFile = ProjectUtil.getPortletXmlFile( this.parent.getProject() );

            if( portletXmlFile != null && portletXmlFile.exists() )
            {
                try
                {
                    final IStructuredModel portletXmlModel =
                        StructuredModelManager.getModelManager().getModelForRead( portletXmlFile );

                    IModelStateListener listener = new IModelStateListener()
                    {

                        public void modelAboutToBeChanged( IStructuredModel model )
                        {
                        }

                        public void modelAboutToBeReinitialized( IStructuredModel structuredModel )
                        {
                        }

                        public void modelChanged( IStructuredModel model )
                        {
                            refresh();
                        }

                        public void modelDirtyStateChanged( IStructuredModel model, boolean isDirty )
                        {
                            refresh();
                        }

                        public void modelReinitialized( IStructuredModel structuredModel )
                        {
                            refresh();
                        }

                        public void modelResourceDeleted( IStructuredModel model )
                        {
                            refresh();
                        }

                        public void modelResourceMoved( IStructuredModel oldModel, IStructuredModel newModel )
                        {
                            refresh();
                        }

                        private void refresh()
                        {
                            portletXmlModel.removeModelStateListener( this );

                            if( !PortletsNode.this.modelElement.disposed() )
                            {
                                PortletsNode.this.modelElement.dispose();
                            }

                            PortletsNode.this.modelElement = null;
                            PortletsNode.this.parent.refresh();
                        }
                    };

                    portletXmlModel.addModelStateListener( listener );

                    modelElement =
                        PortletApp.TYPE.instantiate( new RootXmlResource( new XmlResourceStore(
                            portletXmlFile.getContents() ) ) );
                }
                catch( Exception e )
                {
                    PortletUIPlugin.logError( e );
                }
            }
        }

        return this.modelElement;
    }

    public boolean hasChildren()
    {
        PortletApp model = getPortletAppModelElement();

        if( model != null )
        {
            return model.getPortlets().size() > 0;
        }

        return false;
    }

}
