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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.layouttpl.core.descriptor;

import com.liferay.ide.layouttpl.core.LayoutTplCore;
import com.liferay.ide.project.core.BaseValidator;
import com.liferay.ide.project.core.ProjectCorePlugin;
import com.liferay.ide.project.core.ValidationPreferences;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class LiferayLayoutTplDescriptorValidator extends BaseValidator
{

    public static final String TEMPLATE_PATH_ELEMENT = "template-path";

    public static final String THUMBNAIL_PATH_ELEMENT = "thumbnail-path";

    public static final String WAP_TEMPLATE_PATH_ELEMENT = "wap-template-path";

    public static final String MARKER_TYPE = "com.liferay.ide.layouttpl.core.liferayLayoutTplDescriptorMarker";

    public static final String MESSAGE_TEMPLATE_PATH_NOT_FOUND = "The template path {0} was not found in the docroot.";

    public static final String MESSAGE_THUMBNAIL_PATH_NOT_FOUND =
        "The thumbnail path {0} was not found in the docroot.";

    public static final String MESSAGE_WAP_TEMPLATE_PATH_NOT_FOUND =
        "The wap template path {0} was not found in the docroot.";

    public static final String PREFERENCE_NODE_QUALIFIER = ProjectCorePlugin.getDefault().getBundle().getSymbolicName();

    public LiferayLayoutTplDescriptorValidator()
    {
        super();
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public ValidationResult validate( IResource resource, int kind, ValidationState state, IProgressMonitor monitor )
    {
        if( resource.getType() != IResource.FILE )
        {
            return null;
        }

        ValidationResult result = new ValidationResult();

        IFile liferayLayoutTplXml = (IFile) resource;

        if( liferayLayoutTplXml.isAccessible() && ProjectUtil.isLayoutTplProject( resource.getProject() ) )
        {
            IScopeContext[] scopes = new IScopeContext[] { new InstanceScope(), new DefaultScope() };

            ProjectScope projectScope = new ProjectScope( liferayLayoutTplXml.getProject() );

            boolean useProjectSettings =
                projectScope.getNode( PREFERENCE_NODE_QUALIFIER ).getBoolean(
                    ProjectCorePlugin.USE_PROJECT_SETTINGS, false );

            if( useProjectSettings )
            {
                scopes = new IScopeContext[] { projectScope, new InstanceScope(), new DefaultScope() };
            }

            try
            {
                Map<String, Object>[] problems = detectProblems( liferayLayoutTplXml, scopes );

                for( int i = 0; i < problems.length; i++ )
                {
                    ValidatorMessage message =
                        ValidatorMessage.create( problems[i].get( IMarker.MESSAGE ).toString(), resource );
                    message.setType( MARKER_TYPE );
                    message.setAttributes( problems[i] );
                    result.add( message );
                }
            }
            catch( Exception e )
            {
                LayoutTplCore.logError( e );
            }
        }

        return result;
    }

    protected void checkDocrootElements(
        IDOMDocument document, IProject project, IScopeContext[] preferenceScopes, List<Map<String, Object>> problems )
    {

        checkDocrootElement(
            document, TEMPLATE_PATH_ELEMENT, project, PREFERENCE_NODE_QUALIFIER, preferenceScopes,
            ValidationPreferences.LIFERAY_LAYOUTTPL_XML_TEMPLATE_PATH_NOT_FOUND, MESSAGE_TEMPLATE_PATH_NOT_FOUND,
            problems );

        checkDocrootElement(
            document, WAP_TEMPLATE_PATH_ELEMENT, project, PREFERENCE_NODE_QUALIFIER, preferenceScopes,
            ValidationPreferences.LIFERAY_LAYOUTTPL_XML_WAP_TEMPLATE_PATH_NOT_FOUND,
            MESSAGE_WAP_TEMPLATE_PATH_NOT_FOUND, problems );

        checkDocrootElement(
            document, THUMBNAIL_PATH_ELEMENT, project, PREFERENCE_NODE_QUALIFIER, preferenceScopes,
            ValidationPreferences.LIFERAY_LAYOUTTPL_XML_THUMBNAIL_PATH_NOT_FOUND, MESSAGE_THUMBNAIL_PATH_NOT_FOUND,
            problems );
    }

    @SuppressWarnings( "unchecked" )
    protected Map<String, Object>[] detectProblems( IFile liferayLayoutTplXml, IScopeContext[] preferenceScopes )
        throws CoreException
    {

        List<Map<String, Object>> problems = new ArrayList<Map<String, Object>>();

        IStructuredModel liferayLayputTplXmlModel = null;
        IDOMDocument liferayLayoutTplXmlDocument = null;

        try
        {
            liferayLayputTplXmlModel = StructuredModelManager.getModelManager().getModelForRead( liferayLayoutTplXml );

            if( liferayLayputTplXmlModel != null && liferayLayputTplXmlModel instanceof IDOMModel )
            {
                liferayLayoutTplXmlDocument = ( (IDOMModel) liferayLayputTplXmlModel ).getDocument();

                checkDocrootElements(
                    liferayLayoutTplXmlDocument, liferayLayoutTplXml.getProject(), preferenceScopes, problems );
            }
        }
        catch( IOException e )
        {
        }
        finally
        {
            if( liferayLayputTplXmlModel != null )
            {
                liferayLayputTplXmlModel.releaseFromRead();
            }
        }

        Map<String, Object>[] retval = new Map[problems.size()];

        return (Map<String, Object>[]) problems.toArray( retval );
    }

}
