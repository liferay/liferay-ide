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

package com.liferay.ide.server.ui.editor;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.ILiferayServerWorkingCopy;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.LiferayServerPort;
import com.liferay.ide.server.core.portal.PortalBundleConfiguration;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.core.portal.PortalServerDelegate;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.ui.cmd.ModifyPortCommand;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.server.util.SocketUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.util.ServerLifecycleAdapter;
import org.eclipse.wst.server.ui.editor.ServerEditorSection;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
public class ConfigurationPortEditorSection extends ServerEditorSection
{

    protected ILiferayServerWorkingCopy liferayServer;
    protected PropertyChangeListener listener;
    protected PortalBundleConfiguration portalBundleConfiguration;
    protected PortalRuntime portalRuntime;
    protected PortalServerDelegate portalSeverDelgate;
    protected Table ports;
    private transient List<PropertyChangeListener> propertyListeners;
    protected boolean updating;
    protected TableViewer viewer;
    private IPath pluginPath = LiferayServerCore.getDefault().getStateLocation();

    public ConfigurationPortEditorSection()
    {
        super();
    }

    protected void addChangeListener()
    {
        listener = new PropertyChangeListener()
        {

            public void propertyChange( PropertyChangeEvent event )
            {
                if( PortalBundleConfiguration.MODIFY_PORT_PROPERTY.equals( event.getPropertyName() ) )
                {
                    final String id = (String) event.getOldValue();
                    final int value = Integer.parseInt( event.getNewValue().toString() );

                    changePortNumber( id, value );
                }
            }
        };

        portalBundleConfiguration.addPropertyChangeListener( listener );
    }

    protected void changePortNumber( String id, int port )
    {
        final TableItem[] items = ports.getItems();

        for( int i = 0; i < items.length; i++ )
        {
            try
            {
                LiferayServerPort serverPort = (LiferayServerPort) items[i].getData();

                if( serverPort.getId().equals( id ) )
                {
                    final LiferayServerPort changedPort = new LiferayServerPort(
                        id, serverPort.getName(), port, serverPort.getProtocol(), serverPort.getStoreLocation() );

                    if( ( !validPort( items[i], changedPort ) ||
                        !SocketUtil.isPortAvailable( String.valueOf( port ) ) && !getOriginalPort( changedPort ) ) )
                    {
                        items[i].setImage( LiferayServerUI.getImage( LiferayServerUI.IMG_PORT_WARNING ) );
                    }
                    else
                    {
                        items[i].setImage( LiferayServerUI.getImage( LiferayServerUI.IMG_PORT ) );

                        getManagedForm().getMessageManager().removeMessage( items[i] );
                    }

                    items[i].setData( changedPort );
                    items[i].setText( 1, port + "" );

                    portalSeverDelgate.applyChange( changedPort, new NullProgressMonitor() );

                    return;
                }
            }
            catch( Exception e )
            {
                LiferayServerUI.logError( e );
            }
        }
    }

    public void createSection( Composite parent )
    {
        super.createSection( parent );

        final FormToolkit toolkit = getFormToolkit( parent.getDisplay() );

        final Section section = toolkit.createSection(
            parent, ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR |
                Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE );

        section.setText( "Ports" );
        section.setDescription( "Modify the server ports." );
        section.setLayoutData( new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL ) );

        // ports
        Composite composite = toolkit.createComposite( section );
        GridLayout layout = new GridLayout();

        layout.marginHeight = 8;
        layout.marginWidth = 8;
        composite.setLayout( layout );
        composite.setLayoutData( new GridData( GridData.VERTICAL_ALIGN_FILL | GridData.FILL_HORIZONTAL ) );
        toolkit.paintBordersFor( composite );
        section.setClient( composite );

        ports = toolkit.createTable( composite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION );
        ports.setHeaderVisible( true );
        ports.setLinesVisible( true );

        TableLayout tableLayout = new TableLayout();

        TableColumn col = new TableColumn( ports, SWT.NONE );
        col.setText( "Port Name" );
        ColumnWeightData colData = new ColumnWeightData( 15, 150, true );
        tableLayout.addColumnData( colData );

        col = new TableColumn( ports, SWT.NONE );
        col.setText( "Port Number" );
        colData = new ColumnWeightData( 8, 80, true );
        tableLayout.addColumnData( colData );

        GridData data = new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL );

        data.widthHint = 230;
        data.heightHint = 100;
        ports.setLayoutData( data );
        ports.setLayout( tableLayout );

        viewer = new TableViewer( ports );
        viewer.setColumnProperties( new String[] { "name", "port" } );

        ColumnViewerToolTipSupport.enableFor( viewer );

        viewer.setLabelProvider( new ColumnLabelProvider()
        {

            @Override
            public String getToolTipText( Object element )
            {
                return getTableItemValidationTooltip( (LiferayServerPort) element );
            }

        } );

        Hyperlink setDefault = toolkit.createHyperlink( composite, "Restore Default Setting", SWT.WRAP );
        setDefault.addHyperlinkListener( new HyperlinkAdapter()
        {
            public void linkActivated( HyperlinkEvent e )
            {
                List<LiferayServerPort> defaultPorts = readDefaultPorts();
                
                for( LiferayServerPort port : defaultPorts )
                {
                    changePortNumber( port.getId(), port.getPort() );
                }
            }
        } );
        initialize();
    }

    public void dispose()
    {
        if( portalBundleConfiguration != null )
        {
            portalBundleConfiguration.removePropertyChangeListener( listener );
        }
    }

    protected void firePropertyChangeEvent( String propertyName, Object oldValue, Object newValue )
    {
        if( propertyListeners == null )
        {
            return;
        }

        final PropertyChangeEvent event = new PropertyChangeEvent( this, propertyName, oldValue, newValue );

        final Iterator<PropertyChangeListener> iterator = propertyListeners.iterator();

        while( iterator.hasNext() )
        {
            try
            {
                final PropertyChangeListener listener = (PropertyChangeListener) iterator.next();

                listener.propertyChange( event );
            }
            catch( Exception e )
            {
                LiferayServerUI.logError( "Error firing property change event", e );
            }
        }
    }

    private boolean getOriginalPort( final LiferayServerPort newPort )
    {
        for( LiferayServerPort port : portalSeverDelgate.getLiferayServerPorts() )
        {
            if( port.getId().equals( newPort.getId() ) )
            {
                return port.getPort() == newPort.getPort();
            }
        }

        return false;
    }

    private String getTableItemValidationTooltip( final LiferayServerPort serverPort )
    {
        boolean portAvailable = SocketUtil.isPortAvailable( String.valueOf( serverPort.getPort() ) );
        boolean originalPort = getOriginalPort( serverPort );

        if( !portAvailable && !originalPort )
        {
            final StringBuffer sb = new StringBuffer();

            sb.append( serverPort.getPort() );
            sb.append( " is being used by other application." );

            return sb.toString();
        }

        final List<String> serverLists = ServerUtil.checkUsingPorts( server.getName(), serverPort );

        if( serverLists.size() > 0 )
        {
            final StringBuffer sb = new StringBuffer();

            sb.append( serverPort.getPort() );
            sb.append( " is being used at: " );

            for( String serverName : serverLists )
            {
                sb.append( "<" );
                sb.append( serverName );
                sb.append( ">" );
            }

            return sb.toString();
        }

        return null;
    }

    public void init( IEditorSite site, IEditorInput input )
    {
        super.init( site, input );

        portalRuntime =
            (PortalRuntime) server.getRuntime().loadAdapter( PortalRuntime.class, new NullProgressMonitor() );

        try
        {
            portalSeverDelgate = (PortalServerDelegate) server.getAdapter( PortalServerDelegate.class );
            portalBundleConfiguration = portalSeverDelgate.initBundleConfiguration();
        }
        catch( Exception e )
        {
        }

        addChangeListener();
        initialize();
        ServerCore.addServerLifecycleListener( new ServerLifecycleAdapter()
        {
            public void serverRemoved( IServer server )
            {
                IPath defaultPortsJson = pluginPath.append( server.getId().replace( " ", "_" ) + "_default_ports.json" );

                if( defaultPortsJson.toFile().exists() )
                {
                    defaultPortsJson.toFile().delete();
                }
            }
        } );
    }

    /**
     * Initialize the fields in this editor.
     */
    protected void initialize()
    {
        try
        {
            if( ports == null )
            {
                return;
            }

            ports.removeAll();

            Iterator<LiferayServerPort> iterator = portalSeverDelgate.getLiferayServerPorts().iterator();

            while( iterator.hasNext() )
            {
                final LiferayServerPort port = (LiferayServerPort) iterator.next();

                final TableItem item = new TableItem( ports, SWT.NONE );

                String portStr = "-";

                if( port.getPort() >= 0 )
                {
                    portStr = port.getPort() + "";
                }

                String[] s = new String[] { port.getName(), portStr };

                item.setText( s );

                if( !validPort( item, port ) )
                {
                    item.setImage( LiferayServerUI.getImage( LiferayServerUI.IMG_PORT_WARNING ) );
                }
                else
                {
                    item.setImage( LiferayServerUI.getImage( LiferayServerUI.IMG_PORT ) );
                }

                item.setData( port );
            }

            if( readOnly )
            {
                viewer.setCellEditors( new CellEditor[] { null, null } );
                viewer.setCellModifier( null );
            }
            else
            {
                setupPortEditors();
            }
            
            saveDefaultPorsts();
        }
        catch( Exception e )
        {
            LiferayServerUI.logError( e );
        }

    }

    @SuppressWarnings( { "unchecked" } )
    private List<LiferayServerPort> readDefaultPorts()
    {
        List<LiferayServerPort> deaultPorts = null;
        IPath defaultPortsJson = pluginPath.append( server.getId().replace( " ", "_" ) + "_default_ports.json" );

        if ( defaultPortsJson.toFile().exists() )
        {
            try
            {
                final ObjectMapper mapper = new ObjectMapper();
                deaultPorts = mapper.readValue( defaultPortsJson.toFile(),  new TypeReference<List<LiferayServerPort>>(){});
            }
            catch( IOException e )
            {
                LiferayServerUI.logError( "Failed to read server default ports inforamion", e );
            }
        }
        return deaultPorts;
    }
    
    private void saveDefaultPorsts()
    {
        IPath defaultPortsJson = pluginPath.append( server.getId().replace( " ", "_" ) + "_default_ports.json" );

        if( !defaultPortsJson.toFile().exists() )
        {
            try
            {
                File defaultPortsFile = new File(defaultPortsJson.toOSString());
                List<LiferayServerPort> liferayServerPorts = portalSeverDelgate.getLiferayServerPorts();
                final ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue( defaultPortsFile, liferayServerPorts );
            }
            catch( IOException e )
            {
               LiferayServerUI.logError( "Failed to save server default ports inforamion", e );
            }
        }
    }

    protected void setupPortEditors()
    {
        viewer.setCellEditors( new CellEditor[] { null, new TextCellEditor( ports ) } );

        ICellModifier cellModifier = new ICellModifier()
        {

            public boolean canModify( Object element, String property )
            {
                return "port".equals( property );
            }

            public Object getValue( Object element, String property )
            {
                final LiferayServerPort sp = (LiferayServerPort) element;

                return sp.getPort() < 0 ? "-" : sp.getPort() + "";
            }

            public void modify( Object element, String property, Object value )
            {
                try
                {
                    final LiferayServerPort sp = (LiferayServerPort) ( (Item) element ).getData();

                    final int port = Integer.parseInt( (String) value );

                    if( sp.getPort() != port )
                    {
                        execute(
                            new ModifyPortCommand( portalBundleConfiguration, portalSeverDelgate, sp.getId(), port ) );
                    }
                }
                catch( Exception ex )
                {
                }
            }
        };

        viewer.setCellModifier( cellModifier );

        if( CoreUtil.isWindows() )
        {
            ports.addSelectionListener( new SelectionAdapter()
            {

                public void widgetSelected( SelectionEvent event )
                {
                    try
                    {
                        viewer.editElement( ports.getItem( ports.getSelectionIndex() ).getData(), 1 );
                    }
                    catch( Exception e )
                    {
                    }
                }
            } );
        }
    }

    private boolean validPort( TableItem item, LiferayServerPort serverPort )
    {
        final int port = serverPort.getPort();

        if( port < 0 || port > 65535 )
        {
            getManagedForm().getMessageManager().addMessage(
                item, "Port must to be a number from 1~65535.", item, IMessageProvider.ERROR );

            return false;
        }

        final List<String> serverLists = ServerUtil.checkUsingPorts( server.getName(), serverPort );

        if( serverLists.size() > 0 )
        {
            StringBuffer sb = new StringBuffer();

            sb.append( serverPort.getPort() );
            sb.append( " is being used at: " );

            for( String serverName : serverLists )
            {
                sb.append( "<" );
                sb.append( serverName );
                sb.append( ">" );
            }

            getManagedForm().getMessageManager().addMessage( item, sb.toString(), null, IMessageProvider.WARNING );

            return false;
        }
        else
        {
            getManagedForm().getMessageManager().removeMessage( item );
        }

        return true;
    }
}
