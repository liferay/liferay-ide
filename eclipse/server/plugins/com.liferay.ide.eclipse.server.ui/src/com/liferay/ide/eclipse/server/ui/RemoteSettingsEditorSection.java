package com.liferay.ide.eclipse.server.ui;

import com.liferay.ide.eclipse.server.remote.IRemoteServer;
import com.liferay.ide.eclipse.server.remote.RemoteServer;
import com.liferay.ide.eclipse.server.ui.cmd.SetAdjustDeploymentTimestampCommand;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.server.ui.editor.ServerEditorSection;


public class RemoteSettingsEditorSection extends ServerEditorSection {

	protected Section remoteSettings;
	protected Button adjustTimestamp;
	protected boolean updating;
	protected RemoteServer remoteServer;
	protected PropertyChangeListener listener;

	public RemoteSettingsEditorSection() {
		super();
	}

	public void init( IEditorSite site, IEditorInput input ) {
		super.init( site, input );

		if ( server != null ) {
			remoteServer = (RemoteServer) server.loadAdapter( RemoteServer.class, null );
			addChangeListeners();
		}

		initialize();
	}

	protected void addChangeListeners() {
		listener = new PropertyChangeListener() {

			public void propertyChange( PropertyChangeEvent event ) {
				if ( updating ) {
					return;
				}

				updating = true;

				if ( IRemoteServer.ATTR_ADJUST_DEPLOYMENT_TIMESTAMP.equals( event.getPropertyName() ) ) {
					String s = (String) event.getNewValue();
					adjustTimestamp.setSelection( Boolean.parseBoolean( s ) );
				}

				updating = false;
			}
		};

		server.addPropertyChangeListener( listener );
	}

	@Override
	public void createSection( Composite parent ) {
		FormToolkit toolkit = getFormToolkit( parent.getDisplay() );

		remoteSettings = createSettingsSection( parent, toolkit );
		remoteSettings.setText( "Remote Liferay Settings" );
		remoteSettings.setLayoutData( new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL ) );
		remoteSettings.setDescription( "Specify settings for remote Liferay Portal server." );

		Composite settingsComposite = createSectionComposite( toolkit, remoteSettings );

		adjustTimestamp =
			toolkit.createButton(
				settingsComposite, "Adjust deployment timestamps to GMT timezone (Liferay default)", SWT.CHECK );
		adjustTimestamp.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
		adjustTimestamp.addSelectionListener( new SelectionAdapter() {

			public void widgetSelected( SelectionEvent e ) {
				if ( updating ) {
					return;
				}

				updating = true;
				execute( new SetAdjustDeploymentTimestampCommand( remoteServer, adjustTimestamp.getSelection() ) );
				updating = false;
			}
		} );

		initialize();

	}

	protected void initialize() {
		if ( remoteServer == null || adjustTimestamp == null ) {
			return;
		}

		updating = true;

		boolean adjustGMTOffset = remoteServer.getAdjustDeploymentTimestamp();

		adjustTimestamp.setSelection( adjustGMTOffset );

		updating = false;
	}

	protected Label createLabel( FormToolkit toolkit, Composite parent, String text ) {
		Label label = toolkit.createLabel( parent, text );
		label.setForeground( toolkit.getColors().getColor( IFormColors.TITLE ) );
		return label;
	}

	protected Composite createSectionComposite( FormToolkit toolkit, Section section ) {
		Composite composite = toolkit.createComposite( section );
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 15;
		composite.setLayout( layout );
		composite.setLayoutData( new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL ) );
		toolkit.paintBordersFor( composite );
		section.setClient( composite );
		return composite;
	}

	protected Section createSettingsSection( Composite parent, FormToolkit toolkit ) {
		return toolkit.createSection( parent, ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED |
			ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE );
	}
}
