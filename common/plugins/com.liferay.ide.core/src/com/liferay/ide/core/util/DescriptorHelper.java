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

package com.liferay.ide.core.util;

import com.liferay.ide.core.LiferayCore;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class for helping edit XML files in user projects.
 *
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class DescriptorHelper
{

    public abstract class DOMModelEditOperation extends DOMModelOperation
    {
        public DOMModelEditOperation( IFile descriptorFile )
        {
            super( descriptorFile );
        }

        public void createDefaultDescriptor( String templateString, String version )
        {
            String content = MessageFormat.format( templateString, version, version.replace( '.', '_' ) );

            try
            {
                this.file.create( new ByteArrayInputStream( content.getBytes( "UTF-8" ) ), IResource.FORCE, null ); //$NON-NLS-1$
            }
            catch( Exception e )
            {
                LiferayCore.logError( e );
            }
        }

        protected void createDefaultFile()
        {
        }

        public IStatus execute()
        {
            IStatus retval = null;

            if( !this.file.exists() )
            {
                createDefaultFile();
            }

            IDOMModel domModel = null;

            try
            {
                domModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit( this.file );

                domModel.aboutToChangeModel();

                IDOMDocument document = domModel.getDocument();

                retval = doExecute( document );

                domModel.changedModel();

                domModel.save();
            }
            catch( Exception e )
            {
                retval = LiferayCore.createErrorStatus( e );
            }
            finally
            {
                if( domModel != null )
                {
                    domModel.releaseFromEdit();
                }
            }

            return retval;
        }
    }

    protected static abstract class DOMModelOperation
    {
        protected IFile file;

        public DOMModelOperation( IFile descriptorFile )
        {
            this.file = descriptorFile;
        }

        protected abstract IStatus doExecute( IDOMDocument document );

        public abstract IStatus execute();
    }

    protected abstract class DOMModelReadOperation extends DOMModelOperation
    {
        public DOMModelReadOperation( IFile descriptorFile )
        {
            super( descriptorFile );
        }

        public IStatus execute()
        {
            IStatus retval = null;

            if( !this.file.exists() )
            {
                return LiferayCore.createErrorStatus( this.file.getName() + " doesn't exist" ); //$NON-NLS-1$
            }

            IDOMModel domModel = null;

            try
            {
                domModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForRead( this.file );

                IDOMDocument document = domModel.getDocument();

                retval = doExecute( document );
            }
            catch( Exception e )
            {
                retval = LiferayCore.createErrorStatus( e );
            }
            finally
            {
                if( domModel != null )
                {
                    domModel.releaseFromRead();
                }
            }

            return retval;
        }
    }

    public static IFile getDescriptorFile( IProject project, String fileName )
    {
        IFile retval = null;

        if( ! CoreUtil.isLiferayProject( project ) )
        {
            project = CoreUtil.getNestedLiferayProject( project );
        }

        if( project == null )
        {
            return retval;
        }

        final IFolder defaultDocrootFolder = CoreUtil.getDefaultDocrootFolder( project );

        if( defaultDocrootFolder != null && defaultDocrootFolder.exists() )
        {
            retval = defaultDocrootFolder.getFile( new Path( "WEB-INF" ).append( fileName ) );
        }

        if( retval == null )
        {
         // fallback to looping through all virtual folders
            final IVirtualFolder webappRoot = CoreUtil.getDocroot( project );

            if( webappRoot != null )
            {
                for( IContainer container : webappRoot.getUnderlyingFolders() )
                {
                    if( container != null && container.exists() )
                    {
                        final IFile descriptorFile = container.getFile( new Path( "WEB-INF" ).append( fileName ) );

                        if( descriptorFile.exists() )
                        {
                            retval = descriptorFile;
                            break;
                        }
                    }
                }
            }
        }

        return retval;
    }

    protected String descriptorPath;

    protected IProject project;

    public DescriptorHelper()
    {
    }

    public DescriptorHelper( IProject project )
    {
        this.project = project;
    }

    public List<Element> getChildElements( Element parent )
    {
        List<Element> retval = new ArrayList<Element>();

        if( parent != null )
        {
            NodeList children = parent.getChildNodes();

            for( int i = 0; i < children.getLength(); i++ )
            {
                Node child = children.item( i );

                if( child instanceof Element )
                {
                    retval.add( (Element) child );
                }
            }
        }

        return retval;
    }

    protected IFile getDescriptorFile( String fileName )
    {
        return getDescriptorFile( project, fileName );
    }

    public String getDescriptorPath()
    {
        return this.descriptorPath;
    }

    protected IProject getProject()
    {
        return project;
    }

    public void setDescriptorPath( String path )
    {
        this.descriptorPath = path;
    }

    public void setProject( IProject project )
    {
        this.project = project;
    }

}
