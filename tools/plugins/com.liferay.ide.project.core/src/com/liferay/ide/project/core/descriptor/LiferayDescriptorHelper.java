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

package com.liferay.ide.project.core.descriptor;

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.content.ContentTypeManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.osgi.framework.Version;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public abstract class LiferayDescriptorHelper
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

    public static String getDescriptorVersion( IProject project )
    {
        return getDescriptorVersion( project, "6.0.0" );
    }

    public static String getDescriptorVersion( IProject project, final String defaultValue )
    {
        String retval = defaultValue;

        try
        {
            final ILiferayProject lProject = LiferayCore.create( project );

            if( lProject != null )
            {
                final ILiferayPortal portal = lProject.adapt( ILiferayPortal.class );

                if( portal != null )
                {
                    final String versionStr = portal.getVersion();
                    retval = getDescriptorVersionFromPortalVersion( versionStr );
                }
            }
        }
        catch( Exception e )
        {
            LiferayCore.logError( "Could not get liferay runtime.", e ); //$NON-NLS-1$
        }

        if( "0.0.0".equals( retval ) )
        {
            retval = defaultValue;
        }

        return retval;
    }

    protected static String getDescriptorVersionFromPortalVersion( String versionStr )
    {
        final Version version = new Version( versionStr );

        final int major = version.getMajor();
        final int minor = version.getMinor();

        return Integer.toString( major ) + "." + Integer.toString( minor ) + ".0"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    protected ArrayList<IDescriptorOperation> descriptorOperations = new ArrayList<IDescriptorOperation>();

    protected IContentType contentType;

    protected String descriptorPath;

    protected IProject project;

    public LiferayDescriptorHelper()
    {
        addDescriptorOperations();
    }

    public LiferayDescriptorHelper( IProject project )
    {
        this.project = project;
        addDescriptorOperations();
    }

    protected void addDescriptorOperation( IDescriptorOperation operation )
    {
        descriptorOperations.add( operation );
    }

    protected abstract void addDescriptorOperations();

    protected List<Element> getChildElements( Element parent )
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

    public IContentType getContentType()
    {
        return this.contentType;
    }

    public abstract IFile getDescriptorFile();

    protected IFile getDescriptorFile( String fileName )
    {
        if( ! CoreUtil.isLiferayProject( project ) )
        {
            project = CoreUtil.getLiferayProject( project );
        }

        return project == null ? null : LiferayCore.create( project ).getDescriptorFile( fileName );
    }

    public IDescriptorOperation getDescriptorOperation( Class<? extends IDescriptorOperation> type )
    {
        for( IDescriptorOperation operation : descriptorOperations )
        {
            if( type.isAssignableFrom( operation.getClass() ) )
            {
                return operation;
            }
        }

        return null;
    }

    public String getDescriptorPath()
    {
        return this.descriptorPath;
    }

    protected String getDescriptorVersion()
    {
        return getDescriptorVersion( project );
    }

    protected IProject getProject()
    {
        return project;
    }

    protected IStatus removeAllElements( IDOMDocument document, String tagName )
    {
        if( document == null )
        {
            return ProjectCore.createErrorStatus( MessageFormat.format(
                "Could not remove {0} elements: null document", tagName ) );
        }

        NodeList elements = document.getElementsByTagName( tagName );

        try
        {
            if( elements != null && elements.getLength() > 0 )
            {
                for( int i = 0; i < elements.getLength(); i++ )
                {
                    Node element = elements.item( i );
                    element.getParentNode().removeChild( element );
                }
            }
        }
        catch( Exception ex )
        {
            return ProjectCore.
                createErrorStatus(MessageFormat.format( "Could not remove {0} elements", tagName ), ex ); //$NON-NLS-1$
        }

        return Status.OK_STATUS;
    }

    public void setContentType( IContentType type )
    {
        this.contentType = type;
    }

    public void setContentType( String type )
    {
        this.contentType = ContentTypeManager.getInstance().getContentType( type );
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
