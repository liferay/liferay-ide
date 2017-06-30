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

import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.core.portal.PortalServer;
import com.liferay.ide.server.core.portal.PortalServerConstants;
import com.liferay.ide.server.ui.cmd.SetPortalServerHttpPortCommand;

import java.beans.PropertyChangeEvent;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Terry Jia
 */
public class PortalServerPortsEditorSection extends AbstractPortalServerEditorSection
{

    protected Text httpPort;

    public PortalServerPortsEditorSection()
    {
        super();
    }

    protected void addPropertyListeners( PropertyChangeEvent event )
    {
        if( PortalServer.ATTR_HTTP_PORT.equals( event.getPropertyName() ) )
        {
            String s = (String) event.getNewValue();
            PortalServerPortsEditorSection.this.httpPort.setText( s );
            validate();
        }
    }

    protected void createEditorSection( FormToolkit toolkit, Composite composite )
    {
        Label label = createLabel( toolkit, composite, Msgs.httpPort );
        GridData data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
        label.setLayoutData( data );

        httpPort = toolkit.createText( composite, null );
        httpPort.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
        httpPort.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                if( updating )
                {
                    return;
                }

                updating = true;

                execute( new SetPortalServerHttpPortCommand( server, httpPort.getText().trim() ) );

                updating = false;
            }
        } );
    }

    protected String getSectionLabel()
    {
        return Msgs.ports;
    }

    protected void initProperties()
    {
        httpPort.setText( portalServer.getHttpPort() );
    }

    protected void setDefault()
    {
        execute( new SetPortalServerHttpPortCommand( server, PortalServerConstants.DEFAULT_HTTP_PORT ) );
        httpPort.setText( PortalServerConstants.DEFAULT_HTTP_PORT );
    }

    private static class Msgs extends NLS
    {

        public static String httpPort;
        public static String ports;

        static
        {
            initializeMessages( PortalServerPortsEditorSection.class.getName(), Msgs.class );
        }
    }

    @Override
    protected boolean needCreate()
    {
        PortalRuntime runtime =
            (PortalRuntime) server.getRuntime().loadAdapter( PortalRuntime.class, new NullProgressMonitor() );

        return runtime != null;
    }

}
