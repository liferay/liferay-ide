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

package com.liferay.ide.portlet.ui.editor;

import com.liferay.ide.core.model.IModelChangedEvent;
import com.liferay.ide.core.model.IModelChangedListener;
import com.liferay.ide.portlet.core.IPluginPackageModel;
import com.liferay.ide.portlet.core.PluginPackageModel;
import com.liferay.ide.ui.form.FormEntry;
import com.liferay.ide.ui.form.FormEntryAdapter;
import com.liferay.ide.ui.form.FormLayoutFactory;
import com.liferay.ide.ui.form.IContextPart;
import com.liferay.ide.ui.form.IDEFormPage;
import com.liferay.ide.ui.form.IDESection;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Greg Amerson
 */
public class PluginPackageGeneralSection extends IDESection implements IContextPart, IModelChangedListener
{

    protected FormEntry authorEntry;
    protected FormEntry changeLogEntry;
    protected FormEntry licensesEntry;
    protected FormEntry moduleGroupIdEntry;
    protected FormEntry moduleIncrementalVersionEntry;
    protected FormEntry nameEntry;
    protected PluginPackageFormPage page;
    protected FormEntry pageUrlEntry;
    protected FormEntry shortDescriptionEntry;
    protected boolean speedFilterEnabledModifying = false;
    protected Button speedFilters;

    protected FormEntry tagsEntry;

    public PluginPackageGeneralSection( PluginPackageFormPage page, Composite parent )
    {
        super( page, parent, ExpandableComposite.TITLE_BAR | Section.DESCRIPTION );

        this.page = page;

        initialize( page.getManagedForm() );

        getSection().clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
        getSection().setData( "part", this );

        createClient( getSection(), page.getEditor().getToolkit() );
    }

    @Override
    public void dispose()
    {
        super.dispose();

        if( getModel() != null )
        {
            getModel().dispose();
        }
    }

    public String getContextId()
    {
        return "plugin-package-general";
    }

    public IDEFormPage getPage()
    {
        return page;
    }

    public boolean isEditable()
    {
        return true;
    }

    public void modelChanged( IModelChangedEvent event )
    {
        if( event != null )
        {
            if( event.getChangeType() == IModelChangedEvent.WORLD_CHANGED )
            {
                this.refresh();
            }
            else
            {
                String changedProperty = event.getChangedProperty();

                Object newValue = event.getNewValue();

                if( changedProperty != null && newValue != null )
                {
                    if( IPluginPackageModel.PROPERTY_NAME.equals( changedProperty ) )
                    {
                        nameEntry.setValue( newValue.toString() );
                    }
                    else if( IPluginPackageModel.PROPERTY_MODULE_GROUP_ID.equals( changedProperty ) )
                    {
                        moduleGroupIdEntry.setValue( newValue.toString() );
                    }
                    else if( IPluginPackageModel.PROPERTY_MODULE_INCREMENTAL_VERSION.equals( changedProperty ) )
                    {
                        moduleIncrementalVersionEntry.setValue( newValue.toString() );
                    }
                    else if( IPluginPackageModel.PROPERTY_TAGS.equals( changedProperty ) )
                    {
                        tagsEntry.setValue( newValue.toString() );
                    }
                    else if( IPluginPackageModel.PROPERTY_CHANGE_LOG.equals( changedProperty ) )
                    {
                        changeLogEntry.setValue( newValue.toString() );
                    }
                    else if( IPluginPackageModel.PROPERTY_PAGE_URL.equals( changedProperty ) )
                    {
                        pageUrlEntry.setValue( newValue.toString() );
                    }
                    else if( IPluginPackageModel.PROPERTY_AUTHOR.equals( changedProperty ) )
                    {
                        authorEntry.setValue( newValue.toString() );
                    }
                    else if( IPluginPackageModel.PROPERTY_LICENSES.equals( changedProperty ) )
                    {
                        licensesEntry.setValue( newValue.toString() );
                    }
                    else if( IPluginPackageModel.PROPERTY_SHORT_DESCRIPTION.equals( changedProperty ) )
                    {
                        shortDescriptionEntry.setValue( newValue.toString() );
                    }
                    else if( IPluginPackageModel.PROPERTY_SPEED_FILTERS_ENABLED.equals( changedProperty ) )
                    {
                        speedFilterEnabledModifying = true;

                        speedFilters.setSelection( Boolean.parseBoolean( newValue.toString() ) );

                        speedFilterEnabledModifying = false;
                    }
                }
            }
        }
    }

    public void refresh()
    {
        if( getModel().getName() != null )
        {
            nameEntry.setValue( getModel().getName(), true );
        }

        if( getModel().getModuleGroupId() != null )
        {
            moduleGroupIdEntry.setValue( getModel().getModuleGroupId(), true );
        }

        if( getModel().getModuleIncrementalVersion() != null )
        {
            moduleIncrementalVersionEntry.setValue( getModel().getModuleIncrementalVersion(), true );
        }

        if( getModel().getTags() != null )
        {
            tagsEntry.setValue( getModel().getTags(), true );
        }

        if( getModel().getChangeLog() != null )
        {
            changeLogEntry.setValue( getModel().getChangeLog(), true );
        }

        if( getModel().getAuthor() != null )
        {
            authorEntry.setValue( getModel().getAuthor(), true );
        }

        if( getModel().getLicenses() != null )
        {
            licensesEntry.setValue( getModel().getLicenses(), true );
        }

        if( getModel().getPageUrl() != null )
        {
            pageUrlEntry.setValue( getModel().getPageUrl(), true );
        }

        if( getModel().getShortDescription() != null )
        {
            shortDescriptionEntry.setValue( getModel().getShortDescription(), true );
        }

        Boolean speedFiltersEnabled = getModel().isSpeedFiltersEnabled();

        speedFilters.setSelection( speedFiltersEnabled != null ? speedFiltersEnabled.booleanValue() : false );

        super.refresh();
    }

    private void configureEntry( FormEntry entry )
    {
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 2;
        gd.widthHint = 50;

        entry.getText().setLayoutData( gd );
        entry.setEditable( isEditable() );
    }

    protected void createAuthorEntry( Composite client, FormToolkit toolkit, IActionBars actionBars )
    {
        authorEntry = new FormEntry( client, toolkit, "Author:", null, false );
        authorEntry.setFormEntryListener( new FormEntryAdapter( this, actionBars )
        {

            public void textValueChanged( FormEntry entry )
            {
                getModel().setAuthor( entry.getValue().trim() );
            }
        } );

        configureEntry( authorEntry );
    }

    protected void createChangeLogEntry( Composite client, FormToolkit toolkit, IActionBars actionBars )
    {
        changeLogEntry = new FormEntry( client, toolkit, "Change Log:", null, false );
        changeLogEntry.setFormEntryListener( new FormEntryAdapter( this, actionBars )
        {

            public void textValueChanged( FormEntry entry )
            {
                getModel().setChangeLog( entry.getValue().trim() );
            }
        } );

        configureEntry( changeLogEntry );
    }

    @Override
    protected void createClient( Section section, FormToolkit toolkit )
    {
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.minimumWidth = 250;
        gd.grabExcessVerticalSpace = true;

        section.setText( "General" );
        section.setDescription( "Specify plugin package properties." );
        section.setLayout( FormLayoutFactory.createClearTableWrapLayout( false, 1 ) );
        section.setLayoutData( gd );

        Composite client = toolkit.createComposite( section );
        client.setLayout( FormLayoutFactory.createSectionClientGridLayout( false, 3 ) );

        IActionBars actionBars = page.getEditor().getEditorSite().getActionBars();

        createNameEntry( client, toolkit, actionBars );
        createChangeLogEntry( client, toolkit, actionBars );
        createModuleGroupIdEntry( client, toolkit, actionBars );
        createPageUrlEntry( client, toolkit, actionBars );
        createModuleIncrementalVersionEntry( client, toolkit, actionBars );
        createAuthorEntry( client, toolkit, actionBars );
        createTagsEntry( client, toolkit, actionBars );
        createLicensesEntry( client, toolkit, actionBars );
        createShortDescriptionEntry( client, toolkit, actionBars );
        createSpeedFiltersEntry( client, toolkit, actionBars );
        toolkit.paintBordersFor( client );

        section.setClient( client );

        getModel().addModelChangedListener( this );
    }

    protected void createLicensesEntry( Composite client, FormToolkit toolkit, IActionBars actionBars )
    {
        licensesEntry = new FormEntry( client, toolkit, "Licenses:", null, false );
        licensesEntry.setFormEntryListener( new FormEntryAdapter( this, actionBars )
        {

            public void textValueChanged( FormEntry entry )
            {
                getModel().setLicenses( entry.getValue().trim() );
            }

        } );

        configureEntry( licensesEntry );
    }

    protected void createModuleGroupIdEntry( Composite client, FormToolkit toolkit, IActionBars actionBars )
    {
        moduleGroupIdEntry = new FormEntry( client, toolkit, "Module Group Id:", null, false );
        moduleGroupIdEntry.setFormEntryListener( new FormEntryAdapter( this, actionBars )
        {

            public void textValueChanged( FormEntry entry )
            {
                getModel().setModuleGroupId( entry.getValue().trim() );
            }

        } );

        configureEntry( moduleGroupIdEntry );
    }

    protected void createModuleIncrementalVersionEntry( Composite client, FormToolkit toolkit, IActionBars actionBars )
    {
        moduleIncrementalVersionEntry = new FormEntry( client, toolkit, "Module Version:", null, false );
        moduleIncrementalVersionEntry.setFormEntryListener( new FormEntryAdapter( this, actionBars )
        {

            public void textValueChanged( FormEntry entry )
            {
                getModel().setModuleIncrementalVersion( entry.getValue().trim() );
            }

        } );

        configureEntry( moduleIncrementalVersionEntry );
    }

    protected void createNameEntry( Composite client, FormToolkit toolkit, IActionBars actionBars )
    {
        nameEntry = new FormEntry( client, toolkit, "Name:", null, false );
        nameEntry.setFormEntryListener( new FormEntryAdapter( this, actionBars )
        {

            public void textValueChanged( FormEntry entry )
            {
                getModel().setName( entry.getValue().trim() );
            }

        } );

        configureEntry( nameEntry );
    }

    protected void createPageUrlEntry( Composite client, FormToolkit toolkit, IActionBars actionBars )
    {
        pageUrlEntry = new FormEntry( client, toolkit, "Page URL:", null, false );
        pageUrlEntry.setFormEntryListener( new FormEntryAdapter( this, actionBars )
        {

            public void textValueChanged( FormEntry entry )
            {
                getModel().setPageUrl( entry.getValue().trim() );
            }

        } );

        configureEntry( pageUrlEntry );
    }

    protected void createShortDescriptionEntry( Composite client, FormToolkit toolkit, IActionBars actionBars )
    {
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 5;
        gd.heightHint = 50;

        shortDescriptionEntry = new FormEntry( client, toolkit, "Short Description:", null, false );
        shortDescriptionEntry.setFormEntryListener( new FormEntryAdapter( this, actionBars )
        {

            public void textValueChanged( FormEntry entry )
            {
                getModel().setShortDescription( entry.getValue().trim() );
            }

        } );
        shortDescriptionEntry.getText().setLayoutData( gd );
        shortDescriptionEntry.setEditable( isEditable() );
    }

    protected void createSpeedFiltersEntry( Composite parent, FormToolkit toolkit, IActionBars actionBars )
    {
        SWTUtil.createLabel( parent, "", 1 );

        GridData td = new GridData();
        td.horizontalSpan = 5;

        speedFilters = toolkit.createButton( parent, "Speed Filters", SWT.CHECK );

        speedFilters.setLayoutData( td );
        speedFilters.setEnabled( isEditable() );
        speedFilters.addSelectionListener( new SelectionAdapter()
        {

            public void widgetSelected( SelectionEvent e )
            {
                if( !speedFilterEnabledModifying )
                {
                    getModel().setSpeedFiltersEnabled( speedFilters.getSelection() );
                }
            }

        } );
    }

    protected void createTagsEntry( Composite client, FormToolkit toolkit, IActionBars actionBars )
    {
        tagsEntry = new FormEntry( client, toolkit, "Tags:", null, false );
        tagsEntry.setFormEntryListener( new FormEntryAdapter( this, actionBars )
        {

            public void textValueChanged( FormEntry entry )
            {
                getModel().setTags( entry.getValue().trim() );
            }

        } );

        configureEntry( tagsEntry );
    }

    protected PluginPackageModel getModel()
    {
        return (PluginPackageModel) getPage().getLiferayFormEditor().getModel();
    }

}
