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

package com.liferay.ide.portlet.core.descriptor;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.project.core.BaseValidator;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.ValidationPreferences;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.IOException;
import java.text.MessageFormat;
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
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Greg Amerson
 * @author Cindy Li
 */
@SuppressWarnings( "restriction" )
public class LiferayPortletDescriptorValidator extends BaseValidator
{

    public static final String CONTROL_PANEL_ENTRY_CLASS_ELEMENT = "control-panel-entry-class"; //$NON-NLS-1$

    public static final String CONTROL_PANEL_ENTRY_WEIGHT_ELEMENT = "control-panel-entry-weight"; //$NON-NLS-1$

    public static final String FOOTER_PORTAL_CSS_ELEMENT = "footer-portal-css"; //$NON-NLS-1$

    public static final String FOOTER_PORTAL_JAVASCRIPT_ELEMENT = "footer-portal-javascript"; //$NON-NLS-1$

    public static final String FOOTER_PORTLET_CSS_ELEMENT = "footer-portlet-css"; //$NON-NLS-1$

    public static final String FOOTER_PORTLET_JAVASCRIPT_ELEMENT = "footer-portlet-javascript"; //$NON-NLS-1$

    public static final String HEADER_PORTAL_CSS_ELEMENT = "header-portal-css"; //$NON-NLS-1$

    public static final String HEADER_PORTAL_JAVASCRIPT_ELEMENT = "header-portal-javascript"; //$NON-NLS-1$

    public static final String HEADER_PORTLET_CSS_ELEMENT = "header-portlet-css"; //$NON-NLS-1$

    public static final String HEADER_PORTLET_JAVASCRIPT_ELEMENT = "header-portlet-javascript"; //$NON-NLS-1$

    public static final String ICON_ELEMENT = "icon"; //$NON-NLS-1$

    public static final String MARKER_TYPE = "com.liferay.ide.portlet.core.liferayPortletDescriptorMarker"; //$NON-NLS-1$

    public static final String MESSAGE_ENTRY_WEIGHT_NOT_VALID = Msgs.specifyValidDouble;

    public static final String MESSAGE_FOOTER_PORTAL_CSS_NOT_FOUND = Msgs.footerPortalCssNotFound;

    public static final String MESSAGE_FOOTER_PORTAL_JAVASCRIPT_NOT_FOUND = Msgs.footerPortalJavascriptNotFound;

    public static final String MESSAGE_FOOTER_PORTLET_CSS_NOT_FOUND = Msgs.footerPortletCssNotFound;

    public static final String MESSAGE_FOOTER_PORTLET_JAVASCRIPT_NOT_FOUND = Msgs.footerPortletJavascriptNotFound;

    public static final String MESSAGE_HEADER_PORTAL_CSS_NOT_FOUND = Msgs.headerPortalCssNotFound;

    public static final String MESSAGE_HEADER_PORTAL_JAVASCRIPT_NOT_FOUND = Msgs.headerPortalJavascriptNotFound;

    public static final String MESSAGE_HEADER_PORTLET_CSS_NOT_FOUND = Msgs.headerPortletCssNotFound;

    public static final String MESSAGE_HEADER_PORTLET_JAVASCRIPT_NOT_FOUND = Msgs.headerPortletJavascriptNotFound;

    public static final String MESSAGE_ICON_NOT_FOUND = Msgs.iconNotFound;

    public static final String MESSAGE_PORTLET_NAME_NOT_FOUND = Msgs.portletNameNotFound;

    public static final String PORTLET_NAME_ELEMENT = "portlet-name"; //$NON-NLS-1$

    public static final String PREFERENCE_NODE_QUALIFIER = ProjectCore.getDefault().getBundle().getSymbolicName();

    public LiferayPortletDescriptorValidator()
    {
        super();
    }

    protected void checkControlPanelEntryWeightElements(
        IDOMDocument liferayPortletXmlDocument, IScopeContext[] preferenceScopes, String validationKey,
        String errorMessage, List<Map<String, Object>> problems )
    {
        NodeList elements = liferayPortletXmlDocument.getElementsByTagName( CONTROL_PANEL_ENTRY_WEIGHT_ELEMENT );

        for( int i = 0; i < elements.getLength(); i++ )
        {
            Node item = elements.item( i );
            String entryWeight = NodeUtil.getTextContent( item );

            if( !CoreUtil.isNumeric( entryWeight ) )
            {
                Map<String, Object> problem =
                    createMarkerValues(
                        PREFERENCE_NODE_QUALIFIER, preferenceScopes, validationKey, (IDOMNode) item,
                        MESSAGE_ENTRY_WEIGHT_NOT_VALID );

                if( problem != null )
                {
                    problems.add( problem );
                }
            }
        }
    }

    protected void checkDocrootElements(
        IDOMDocument document, IProject project, IScopeContext[] preferenceScopes, List<Map<String, Object>> problems )
    {
        checkDocrootElement(
            document, ICON_ELEMENT, project, PREFERENCE_NODE_QUALIFIER, preferenceScopes,
            ValidationPreferences.LIFERAY_PORTLET_XML_RESOURCE_NOT_FOUND, MESSAGE_ICON_NOT_FOUND, problems );

        checkDocrootElement(
            document, HEADER_PORTAL_CSS_ELEMENT, project, PREFERENCE_NODE_QUALIFIER, preferenceScopes,
            ValidationPreferences.LIFERAY_PORTLET_XML_RESOURCE_NOT_FOUND, MESSAGE_HEADER_PORTAL_CSS_NOT_FOUND,
            problems );

        checkDocrootElement(
            document, HEADER_PORTLET_CSS_ELEMENT, project, PREFERENCE_NODE_QUALIFIER, preferenceScopes,
            ValidationPreferences.LIFERAY_PORTLET_XML_RESOURCE_NOT_FOUND,
            MESSAGE_HEADER_PORTLET_CSS_NOT_FOUND, problems );

        checkDocrootElement(
            document, HEADER_PORTAL_JAVASCRIPT_ELEMENT, project, PREFERENCE_NODE_QUALIFIER, preferenceScopes,
            ValidationPreferences.LIFERAY_PORTLET_XML_RESOURCE_NOT_FOUND,
            MESSAGE_HEADER_PORTAL_JAVASCRIPT_NOT_FOUND, problems );

        checkDocrootElement(
            document, HEADER_PORTLET_JAVASCRIPT_ELEMENT, project, PREFERENCE_NODE_QUALIFIER, preferenceScopes,
            ValidationPreferences.LIFERAY_PORTLET_XML_RESOURCE_NOT_FOUND,
            MESSAGE_HEADER_PORTLET_JAVASCRIPT_NOT_FOUND, problems );

        checkDocrootElement(
            document, FOOTER_PORTAL_CSS_ELEMENT, project, PREFERENCE_NODE_QUALIFIER, preferenceScopes,
            ValidationPreferences.LIFERAY_PORTLET_XML_RESOURCE_NOT_FOUND, MESSAGE_FOOTER_PORTAL_CSS_NOT_FOUND,
            problems );

        checkDocrootElement(
            document, FOOTER_PORTLET_CSS_ELEMENT, project, PREFERENCE_NODE_QUALIFIER, preferenceScopes,
            ValidationPreferences.LIFERAY_PORTLET_XML_RESOURCE_NOT_FOUND,
            MESSAGE_FOOTER_PORTLET_CSS_NOT_FOUND, problems );

        checkDocrootElement(
            document, FOOTER_PORTAL_JAVASCRIPT_ELEMENT, project, PREFERENCE_NODE_QUALIFIER, preferenceScopes,
            ValidationPreferences.LIFERAY_PORTLET_XML_RESOURCE_NOT_FOUND,
            MESSAGE_FOOTER_PORTAL_JAVASCRIPT_NOT_FOUND, problems );

        checkDocrootElement(
            document, FOOTER_PORTLET_JAVASCRIPT_ELEMENT, project, PREFERENCE_NODE_QUALIFIER, preferenceScopes,
            ValidationPreferences.LIFERAY_PORTLET_XML_RESOURCE_NOT_FOUND,
            MESSAGE_FOOTER_PORTLET_JAVASCRIPT_NOT_FOUND, problems );
    }

    protected void checkPortletName(
        IDOMDocument portletXmlDocument, Node liferayPortletNameNode, IScopeContext[] preferenceScopes,
        String validationKey, String errorMessage, List<Map<String, Object>> problems )
    {
        NodeList elements = portletXmlDocument.getElementsByTagName( PORTLET_NAME_ELEMENT );

        String liferayPortletName = NodeUtil.getTextContent( liferayPortletNameNode );

        boolean portletNameFound = false;

        for( int i = 0; i < elements.getLength(); i++ )
        {
            Node item = elements.item( i );
            String portletName = NodeUtil.getTextContent( item );

            if( CoreUtil.isEqual( portletName, liferayPortletName ) )
            {
                portletNameFound = true;

                break;
            }
        }

        if( !portletNameFound )
        {
            String msg = MessageFormat.format( errorMessage, new Object[] { liferayPortletName } );

            Map<String, Object> problem =
                createMarkerValues(
                    PREFERENCE_NODE_QUALIFIER, preferenceScopes, validationKey, (IDOMNode) liferayPortletNameNode, msg );

            if( problem != null )
            {
                problems.add( problem );
            }
        }
    }

    protected void checkPortletNameElements(
        IDOMDocument liferayPortletXmlDocument, IDOMDocument portletXmlDocument, IScopeContext[] preferenceScopes,
        List<Map<String, Object>> problems )
    {
        NodeList elements = liferayPortletXmlDocument.getElementsByTagName( PORTLET_NAME_ELEMENT );

        for( int i = 0; i < elements.getLength(); i++ )
        {
            Node liferayPortletNameElement = elements.item( i );

            checkPortletName(
                portletXmlDocument, liferayPortletNameElement, preferenceScopes,
                ValidationPreferences.LIFERAY_PORTLET_XML_REFERENCE_NOT_FOUND, MESSAGE_PORTLET_NAME_NOT_FOUND,
                problems );
        }
    }

    @SuppressWarnings( "unchecked" )
    protected Map<String, Object>[] detectProblems(
        IJavaProject javaProject, IFile liferayPortletXml, IFile portletXml, IScopeContext[] preferenceScopes )
        throws CoreException
    {
        List<Map<String, Object>> problems = new ArrayList<Map<String, Object>>();

        IStructuredModel liferayPortletXmlModel = null;
        IStructuredModel portletXmlModel = null;
        IDOMDocument liferayPortletXmlDocument = null;

        try
        {
            liferayPortletXmlModel = StructuredModelManager.getModelManager().getModelForRead( liferayPortletXml );

            if( liferayPortletXmlModel != null && liferayPortletXmlModel instanceof IDOMModel )
            {
                liferayPortletXmlDocument = ( (IDOMModel) liferayPortletXmlModel ).getDocument();

                checkDocrootElements(
                    liferayPortletXmlDocument, liferayPortletXml.getProject(), preferenceScopes, problems );

                checkAllClassElements(
                    getAllClasseElements( "LiferayPortletClassElements.properties" ), javaProject, liferayPortletXml, //$NON-NLS-1$
                    ValidationPreferences.LIFERAY_PORTLET_XML_TYPE_NOT_FOUND,
                    ValidationPreferences.LIFERAY_PORTLET_XML_TYPE_HIERARCHY_INCORRECT, preferenceScopes,
                    PREFERENCE_NODE_QUALIFIER, problems );

                checkControlPanelEntryWeightElements(
                    liferayPortletXmlDocument, preferenceScopes,
                    ValidationPreferences.LIFERAY_PORTLET_XML_SYNTAX_INVALID, MESSAGE_ENTRY_WEIGHT_NOT_VALID,
                    problems );
            }

            if( portletXml != null && portletXml.exists() )
            {
                portletXmlModel = StructuredModelManager.getModelManager().getModelForRead( portletXml );

                if( portletXmlModel instanceof IDOMModel )
                {
                    IDOMDocument portletXmlDocument = ( (IDOMModel) portletXmlModel ).getDocument();

                    checkPortletNameElements( liferayPortletXmlDocument, portletXmlDocument, preferenceScopes, problems );
                }
            }
        }
        catch( IOException e )
        {
            PortletCore.logError( e );
        }
        finally
        {
            if( liferayPortletXmlModel != null )
            {
                liferayPortletXmlModel.releaseFromRead();
            }

            if( portletXmlModel != null )
            {
                portletXmlModel.releaseFromRead();
            }
        }

        Map<String, Object>[] retval = new Map[problems.size()];

        return (Map<String, Object>[]) problems.toArray( retval );
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

        IFile liferayPortletXml = (IFile) resource;

        IFile portletXml =
            CoreUtil.getDescriptorFile( liferayPortletXml.getProject(), ILiferayConstants.PORTLET_XML_FILE );

        if( liferayPortletXml.isAccessible() && portletXml != null && portletXml.isAccessible() &&
            ProjectUtil.isPortletProject( resource.getProject() ) )
        {
            final IJavaProject javaProject = JavaCore.create( portletXml.getProject() );

            IScopeContext[] scopes = new IScopeContext[] { new InstanceScope(), new DefaultScope() };

            ProjectScope projectScope = new ProjectScope( liferayPortletXml.getProject() );

            boolean useProjectSettings =
                projectScope.getNode( PREFERENCE_NODE_QUALIFIER ).getBoolean(
                    ProjectCore.USE_PROJECT_SETTINGS, false );

            if( useProjectSettings )
            {
                scopes = new IScopeContext[] { projectScope, new InstanceScope(), new DefaultScope() };
            }

            try
            {
                Map<String, Object>[] problems = detectProblems( javaProject, liferayPortletXml, portletXml, scopes );

                for( int i = 0; i < problems.length; i++ )
                {
                    ValidatorMessage message =
                        ValidatorMessage.create( problems[i].get( IMarker.MESSAGE ).toString(), resource );
                    message.setType( MARKER_TYPE );
                    message.setAttributes( problems[i] );
                    result.add( message );
                }

                if( problems.length > 0 )
                {
                    result.setDependsOn( new IResource[] { portletXml } );
                }
            }
            catch( Exception e )
            {
                PortletCore.logError( e );
            }
        }

        return result;
    }

    private static class Msgs extends NLS
    {
        public static String footerPortalCssNotFound;
        public static String footerPortalJavascriptNotFound;
        public static String footerPortletCssNotFound;
        public static String footerPortletJavascriptNotFound;
        public static String headerPortalCssNotFound;
        public static String headerPortalJavascriptNotFound;
        public static String headerPortletCssNotFound;
        public static String headerPortletJavascriptNotFound;
        public static String iconNotFound;
        public static String portletNameNotFound;
        public static String specifyValidDouble;

        static
        {
            initializeMessages( LiferayPortletDescriptorValidator.class.getName(), Msgs.class );
        }
    }
}
