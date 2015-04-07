/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *    Greg Amerson <gregory.amerson@liferay.com>
 *******************************************************************************/

package com.liferay.ide.server.ui.editor;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.portal.PortalServer;
import com.liferay.ide.server.core.portal.PortalServerConstants;
import com.liferay.ide.server.ui.cmd.SetPortalServerPasswordCommand;
import com.liferay.ide.server.ui.cmd.SetPortalServerUsernameCommand;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.server.core.IPublishListener;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.util.PublishAdapter;
import org.eclipse.wst.server.ui.editor.ServerEditorSection;
import org.eclipse.wst.server.ui.internal.ContextIds;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class PortalServerAccountEditorSection extends ServerEditorSection
{

    protected Section section;
    protected PortalServer portalServer;

    protected Hyperlink setDefault;

    protected boolean defaultDeployDirIsSet;

    protected Text password;
    protected Text username;
    protected boolean updating;

    protected PropertyChangeListener listener;
    protected IPublishListener publishListener;

    protected boolean allowRestrictedEditing;

    public PortalServerAccountEditorSection()
    {
    }

    protected void addChangeListeners()
    {
        listener = new PropertyChangeListener()
        {

            public void propertyChange( PropertyChangeEvent event )
            {
                if( updating )
                    return;

                updating = true;

                if( PortalServer.ATTR_USERNAME.equals( event.getPropertyName() ) )
                {
                    String s = (String) event.getNewValue();
                    PortalServerAccountEditorSection.this.username.setText( s );
                    validate();
                }
                else if( PortalServer.ATTR_PASSWORD.equals( event.getPropertyName() ) )
                {
                    String s = (String) event.getNewValue();
                    PortalServerAccountEditorSection.this.password.setText( s );
                    validate();
                }

                updating = false;
            }
        };

        server.addPropertyChangeListener( listener );

        publishListener = new PublishAdapter()
        {

            public void publishFinished( IServer server2, IStatus status )
            {
                boolean flag = false;
                if( status.isOK() && server2.getModules().length == 0 )
                    flag = true;
                if( flag != allowRestrictedEditing )
                {
                    allowRestrictedEditing = flag;
                }
            }
        };

        server.getOriginal().addPublishListener( publishListener );
    }

    public void createSection( Composite parent )
    {
        super.createSection( parent );
        FormToolkit toolkit = getFormToolkit( parent.getDisplay() );

        section =
            toolkit.createSection( parent, ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED |
                ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE );
        section.setText( Msgs.liferayAccount );
        section.setLayoutData( new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL ) );

        Composite composite = toolkit.createComposite( section );
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.marginHeight = 5;
        layout.marginWidth = 10;
        layout.verticalSpacing = 5;
        layout.horizontalSpacing = 15;
        composite.setLayout( layout );
        composite.setLayoutData( new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL ) );
        IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
        whs.setHelp( composite, ContextIds.EDITOR_SERVER );
        whs.setHelp( section, ContextIds.EDITOR_SERVER );
        toolkit.paintBordersFor( composite );
        section.setClient( composite );

        GridData data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );

        Label label = createLabel( toolkit, composite, Msgs.username );
        data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
        label.setLayoutData( data );

        username = toolkit.createText( composite, null );
        username.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
        username.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                if( updating )
                {
                    return;
                }

                updating = true;
                execute( new SetPortalServerUsernameCommand( server, username.getText().trim() ) );
                updating = false;
            }

        } );

        label = createLabel( toolkit, composite, StringPool.EMPTY );
        data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
        label.setLayoutData( data );

        label = createLabel( toolkit, composite, Msgs.password );
        label.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false ) );

        password = toolkit.createText( composite, null, SWT.PASSWORD );
        password.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
        password.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                if( updating )
                {
                    return;
                }

                updating = true;
                execute( new SetPortalServerPasswordCommand( server, password.getText().trim() ) );
                updating = false;
            }

        } );

        setDefault = toolkit.createHyperlink( composite, Msgs.restoreDefaultsLink, SWT.WRAP );
        setDefault.addHyperlinkListener( new HyperlinkAdapter()
        {

            public void linkActivated( HyperlinkEvent e )
            {
                updating = true;

                execute( new SetPortalServerUsernameCommand( server, PortalServerConstants.DEFAULT_USERNAME ) );
                username.setText( PortalServerConstants.DEFAULT_USERNAME );
                execute( new SetPortalServerPasswordCommand( server, StringPool.EMPTY ) );
                password.setText( StringPool.EMPTY );

                updating = false;
                validate();
            }
        } );

        data = new GridData( SWT.FILL, SWT.CENTER, true, false );
        data.horizontalSpan = 3;
        setDefault.setLayoutData( data );

        initialize();
    }

    protected Label createLabel( FormToolkit toolkit, Composite parent, String text )
    {
        Label label = toolkit.createLabel( parent, text );
        label.setForeground( toolkit.getColors().getColor( IFormColors.TITLE ) );
        return label;
    }

    public void dispose()
    {
        if( server != null )
        {
            server.removePropertyChangeListener( listener );
            if( server.getOriginal() != null )
                server.getOriginal().removePublishListener( publishListener );
        }
    }

    public void init( IEditorSite site, IEditorInput input )
    {
        super.init( site, input );

        if( server != null )
        {
            portalServer = (PortalServer) server.loadAdapter( PortalServer.class, null );
            addChangeListeners();
        }
    }

    protected void initialize()
    {
        if( portalServer == null )
        {
            return;
        }

        updating = true;

        username.setText( portalServer.getUsername() );
        password.setText( portalServer.getPassword() );

        updating = false;
        validate();
    }

    protected void validate()
    {
        if( portalServer != null )
        {
            setErrorMessage( null );
        }
    }

    private static class Msgs extends NLS
    {

        public static String liferayAccount;
        public static String password;
        public static String username;
        public static String restoreDefaultsLink;

        static
        {
            initializeMessages( PortalServerAccountEditorSection.class.getName(), Msgs.class );
        }
    }
}
