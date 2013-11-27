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
 *******************************************************************************/

package com.liferay.ide.portlet.ui.pref;

import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.ValidationPreferences;
import com.liferay.ide.ui.pref.AbstractValidationSettingsPage;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ScrolledPageContent;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class PortletValidationSettingsPage extends AbstractValidationSettingsPage
{

    public static final String PORTLET_UI_PROPERTY_PAGE_PROJECT_VALIDATION_ID =
        "com.liferay.ide.portlet.ui.propertyPage.project.validation"; //$NON-NLS-1$

    public static final String VALIDATION_ID = "com.liferay.ide.portlet.ui.validation"; //$NON-NLS-1$

    protected static final Map<Integer, Integer> ERROR_MAP = new HashMap<Integer, Integer>();

    protected static final int[] ERROR_VALUES = new int[] { 1, 2, -1 };

    protected static final String[] ERRORS = new String[] { Msgs.error, Msgs.warning, Msgs.ignore };

    protected static final String SETTINGS_SECTION_NAME = "PortletValidationSeverities"; //$NON-NLS-1$

    static
    {
        ERROR_MAP.put( IMarker.SEVERITY_ERROR, 0 );
        ERROR_MAP.put( IMarker.SEVERITY_WARNING, 1 );
        ERROR_MAP.put( IMarker.SEVERITY_INFO, 2 );
    }

    protected PixelConverter pixelConverter;

    @Override
    public void dispose()
    {
        storeSectionExpansionStates( getDialogSettings().addNewSection( SETTINGS_SECTION_NAME ) );
        super.dispose();
    }

    public void init( IWorkbench workbench )
    {
    }

    @Override
    public boolean performOk()
    {
        boolean result = super.performOk();
        storeValues();
        return result;
    }

    protected Combo createCombo( Composite parent, String label, String key )
    {
        return addComboBox( parent, label, key, ERROR_VALUES, ERRORS, 0 );
    }

    @Override
    protected Control createCommonContents( Composite composite )
    {
        final Composite page = new Composite( composite, SWT.NULL );

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        page.setLayout( layout );

        this.pixelConverter = new PixelConverter( composite );

        final Composite content = createValidationSection( page );

        loadPreferences();
        restoreSectionExpansionStates( getDialogSettings().getSection( SETTINGS_SECTION_NAME ) );

        GridData gridData = new GridData( GridData.FILL, GridData.FILL, true, true );
        gridData.heightHint = pixelConverter.convertHeightInCharsToPixels( 20 );
        content.setLayoutData( gridData );

        return page;
    }

    protected Composite createValidationSection( Composite parent )
    {
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = 0;
        layout.marginWidth = 0;

        final ScrolledPageContent pageContent = new ScrolledPageContent( parent );
        pageContent.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        pageContent.setExpandHorizontal( true );
        pageContent.setExpandVertical( true );

        Composite body = pageContent.getBody();
        body.setLayout( layout );

        GridData gd = new GridData( GridData.FILL, GridData.CENTER, true, false, 2, 1 );
        gd.horizontalIndent = 0;

        Label description = new Label( body, SWT.NONE );
        description.setText( Msgs.selectSeverityLevel );
        description.setFont( pageContent.getFont() );
        description.setLayoutData( gd );

        ExpandableComposite twistie;

        int columns = 3;

        twistie = createTwistie( body, Msgs.portletXMLDescriptor, columns );
        Composite inner = createInnerComposite( parent, twistie, columns );

        createCombo( inner, Msgs.classNotFound, ValidationPreferences.PORTLET_XML_CLASS_NOT_FOUND );

        createCombo( inner, Msgs.incorrectClassHierarchy, ValidationPreferences.PORTLET_XML_INCORRECT_CLASS_HIERARCHY );

        createCombo( inner, Msgs.resourceBundleNotFound, ValidationPreferences.PORTLET_XML_RESOURCE_BUNDLE_NOT_FOUND );

        createCombo( inner, Msgs.resourceBundleEncodingNotDefault, ValidationPreferences.PORTLET_XML_RESOURCE_BUNDLE_ENCODING_NOT_DEFAULT );

        createCombo( inner, Msgs.supportedLocaleEncodingNotDefault, ValidationPreferences.PORTLET_XML_SUPPORTED_LOCALE_ENCODING_NOT_DEFAULT );

        twistie = createTwistie( body, Msgs.liferayPortletXMLDescriptor, columns );
        inner = createInnerComposite( parent, twistie, columns );

        createCombo( inner, Msgs.classNotFound, ValidationPreferences.LIFERAY_PORTLET_XML_CLASS_NOT_FOUND );
        createCombo( inner, Msgs.incorrectClassHierarchy, ValidationPreferences.LIFERAY_PORTLET_XML_INCORRECT_CLASS_HIERARCHY );
        createCombo( inner, Msgs.iconNotFound, ValidationPreferences.LIFERAY_PORTLET_XML_ICON_NOT_FOUND );
        createCombo( inner, Msgs.entryWeightNotValid, ValidationPreferences.LIFERAY_PORTLET_XML_ENTRY_WEIGHT_NOT_VALID );
        createCombo(
            inner, Msgs.headerPortalCssNotFound, ValidationPreferences.LIFERAY_PORTLET_XML_HEADER_PORTAL_CSS_NOT_FOUND );
        createCombo(
            inner, Msgs.headerPortletCssNotFound,
            ValidationPreferences.LIFERAY_PORTLET_XML_HEADER_PORTLET_CSS_NOT_FOUND );
        createCombo(
            inner, Msgs.headerPortalJavascriptNotFound,
            ValidationPreferences.LIFERAY_PORTLET_XML_HEADER_PORTAL_JAVASCRIPT_NOT_FOUND );
        createCombo(
            inner, Msgs.headerPortletJavascriptNotFound,
            ValidationPreferences.LIFERAY_PORTLET_XML_HEADER_PORTLET_JAVASCRIPT_NOT_FOUND );
        createCombo(
            inner, Msgs.footerPortalCssNotFound, ValidationPreferences.LIFERAY_PORTLET_XML_FOOTER_PORTAL_CSS_NOT_FOUND );
        createCombo(
            inner, Msgs.footerPortletCssNotFound,
            ValidationPreferences.LIFERAY_PORTLET_XML_FOOTER_PORTLET_CSS_NOT_FOUND );
        createCombo(
            inner, Msgs.footerPortalJavascriptNotFound,
            ValidationPreferences.LIFERAY_PORTLET_XML_FOOTER_PORTAL_JAVASCRIPT_NOT_FOUND );
        createCombo(
            inner, Msgs.footerPortletJavascriptNotFound,
            ValidationPreferences.LIFERAY_PORTLET_XML_FOOTER_PORTLET_JAVASCRIPT_NOT_FOUND );
        createCombo( inner, Msgs.portletNameNotFound, ValidationPreferences.LIFERAY_PORTLET_XML_PORTLET_NAME_NOT_FOUND );

        twistie = createTwistie( body, Msgs.liferayDisplayXMLDescriptor, columns );
        inner = createInnerComposite( parent, twistie, columns );

        createCombo( inner, Msgs.portletIdNotFound, ValidationPreferences.LIFERAY_DISPLAY_XML_PORTLET_ID_NOT_FOUND );

        createCombo( inner, Msgs.categoryNameInvalid, ValidationPreferences.LIFERAY_DISPLAY_XML_CATEGORY_NAME_INVALID );

        twistie = createTwistie( body, Msgs.liferayHookXMLDescriptor, columns );
        inner = createInnerComposite( parent, twistie, columns );

        createCombo( inner, Msgs.classNotFound, ValidationPreferences.LIFERAY_HOOK_XML_CLASS_NOT_FOUND );
        createCombo( inner, Msgs.incorrectClassHierarchy, ValidationPreferences.LIFERAY_HOOK_XML_INCORRECT_CLASS_HIERARCHY );
        createCombo(
            inner, Msgs.portalPropertiesResourceNotFound,
            ValidationPreferences.LIFERAY_HOOK_XML_PORTAL_PROPERTIES_NOT_FOUND );
        createCombo(
            inner, Msgs.languagePropertiesResourceNotFound,
            ValidationPreferences.LIFERAY_HOOK_XML_LANGUAGE_PROPERTIES_NOT_FOUND );
        createCombo(
            inner, Msgs.languagePropertiesEncodingNotDefault,
            ValidationPreferences.LIFERAY_HOOK_XML_LANGUAGE_PROPERTIES_ENCODING_NOT_DEFAULT );
        createCombo(
            inner, Msgs.customJspDirectoryNotFound, ValidationPreferences.LIFERAY_HOOK_XML_CUSTOM_JSP_DIR_NOT_FOUND );

        twistie = createTwistie( body, Msgs.liferayLayoutTemplatesDescriptor, columns );
        inner = createInnerComposite( parent, twistie, columns );

        createCombo(
            inner, Msgs.templatePathResourceNotFound,
            ValidationPreferences.LIFERAY_LAYOUTTPL_XML_TEMPLATE_PATH_NOT_FOUND );
        createCombo(
            inner, Msgs.wapTemplatePathResourceNotFound,
            ValidationPreferences.LIFERAY_LAYOUTTPL_XML_WAP_TEMPLATE_PATH_NOT_FOUND );
        createCombo(
            inner, Msgs.thumbnailPathResourceNotFound,
            ValidationPreferences.LIFERAY_LAYOUTTPL_XML_THUMBNAIL_PATH_NOT_FOUND );

        twistie = createTwistie( body, Msgs.serviceXMLDescriptor, columns );
        inner = createInnerComposite( parent, twistie, columns );

        createCombo( inner, Msgs.namespaceNotValid, ValidationPreferences.SERVICE_XML_NAMESPACE_INVALID );
        createCombo( inner, Msgs.packagePathNotValid, ValidationPreferences.SERVICE_XML_PACKAGE_PATH_INVALID );

        return parent;
    }

    protected void enableValues()
    {
    }

    protected IDialogSettings getDialogSettings()
    {
        return PortletUIPlugin.getDefault().getDialogSettings();
    }

    @Override
    protected String getPreferenceNodeQualifier()
    {
        return LiferayProjectCore.PLUGIN_ID;
    }

    @Override
    protected String getPreferencePageID()
    {
        return VALIDATION_ID;
    }

    @Override
    protected String getProjectSettingsKey()
    {
        return LiferayProjectCore.USE_PROJECT_SETTINGS;
    }

    @Override
    protected String getPropertyPageID()
    {
        return PORTLET_UI_PROPERTY_PAGE_PROJECT_VALIDATION_ID;
    }

    protected String getQualifier()
    {
        return PortletCore.getDefault().getBundle().getSymbolicName();
    }

    protected void initializeValues()
    {
        // for (Map.Entry<String, Combo> entry : combos.entrySet()) {
        // int val = getPortletCorePreferences().getInt(entry.getKey(), -1);
        // entry.getValue().select(ERROR_MAP.get(val));
        // }
    }

    protected boolean loadPreferences()
    {
        BusyIndicator.showWhile( getControl().getDisplay(), new Runnable()
        {

            public void run()
            {
                initializeValues();
                validateValues();
                enableValues();
            }
        } );
        return true;
    }

    @Override
    protected void performDefaults()
    {
        resetSeverities();
        super.performDefaults();
    }

    protected void validateValues()
    {
        String errorMessage = null;
        setErrorMessage( errorMessage );
        setValid( errorMessage == null );
    }

    private static class Msgs extends NLS
    {
        public static String categoryNameInvalid;
        public static String classNotFound;
        public static String customJspDirectoryNotFound;
        public static String entryWeightNotValid;
        public static String error;
        public static String footerPortalCssNotFound;
        public static String footerPortalJavascriptNotFound;
        public static String footerPortletCssNotFound;
        public static String footerPortletJavascriptNotFound;
        public static String headerPortalCssNotFound;
        public static String headerPortalJavascriptNotFound;
        public static String headerPortletCssNotFound;
        public static String headerPortletJavascriptNotFound;
        public static String iconNotFound;
        public static String ignore;
        public static String incorrectClassHierarchy;
        public static String languagePropertiesResourceNotFound;
        public static String languagePropertiesEncodingNotDefault;
        public static String liferayDisplayXMLDescriptor;
        public static String liferayHookXMLDescriptor;
        public static String liferayLayoutTemplatesDescriptor;
        public static String liferayPortletXMLDescriptor;
        public static String namespaceNotValid;
        public static String packagePathNotValid;
        public static String portalPropertiesResourceNotFound;
        public static String portletIdNotFound;
        public static String portletNameNotFound;
        public static String portletXMLDescriptor;
        public static String resourceBundleNotFound;
        public static String resourceBundleEncodingNotDefault;
        public static String selectSeverityLevel;
        public static String serviceXMLDescriptor;
        public static String supportedLocaleEncodingNotDefault;
        public static String templatePathResourceNotFound;
        public static String thumbnailPathResourceNotFound;
        public static String wapTemplatePathResourceNotFound;
        public static String warning;

        static
        {
            initializeMessages( PortletValidationSettingsPage.class.getName(), Msgs.class );
        }
    }
}
