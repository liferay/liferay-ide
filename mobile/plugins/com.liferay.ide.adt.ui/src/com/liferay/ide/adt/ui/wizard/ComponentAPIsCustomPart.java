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
package com.liferay.ide.adt.ui.wizard;

import com.liferay.ide.adt.core.model.MobileSDKLibrariesOp;
import com.liferay.ide.adt.ui.ADTUI;
import com.liferay.ide.ui.navigator.AbstractLabelProvider;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.mobile.sdk.core.MobileSDKCore;
import com.liferay.mobile.sdk.core.MobileSDKCore.MobileAPI;

import java.util.Arrays;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.swt.FormComponentPresentation;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;


/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class ComponentAPIsCustomPart extends FormComponentPart
{

    private static final String LOADING_MSG = "Loading APIs...";
    private static final String LIFERAY_CORE_API = "Liferay core";
    private static final String ROOT_API = "root api";
    private static final String API = "api";

    class APIsContentProvider implements ITreeContentProvider
    {
        public void dispose()
        {
        }

        public Object[] getChildren( Object parentElement )
        {
            if( parentElement instanceof MobileAPI )
            {
                return ( (MobileAPI) parentElement ).apis;
            }

            return null;
        }

        public Object[] getElements( Object inputElement )
        {
            if( inputElement instanceof MobileAPI[] )
            {
                return (MobileAPI[]) inputElement;
            }

            return new Object[] { inputElement };
        }

        public Object getParent( Object element )
        {
            return null;
        }

        public boolean hasChildren( Object element )
        {
            if( element instanceof MobileAPI )
            {
                return ( (MobileAPI) element ).apis.length > 0;
            }

            return false;
        }

        public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
        {
        }
    }

    class APIsLabelProvider extends AbstractLabelProvider
        implements IColorProvider, DelegatingStyledCellLabelProvider.IStyledLabelProvider
    {
        @Override
        public String getText( Object element )
        {
            if( element instanceof MobileAPI )
            {
                return ( (MobileAPI) element ).name;
            }

            return super.getText( element );
        }


        @Override
        protected void initalizeImageRegistry( ImageRegistry imageRegistry )
        {
            imageRegistry.put(
                LIFERAY_CORE_API,
                ADTUI.imageDescriptorFromPlugin( ADTUI.PLUGIN_ID, "/icons/e16/liferay-core_16x16.png" ) );
            imageRegistry.put(
                ROOT_API,
                ADTUI.imageDescriptorFromPlugin( ADTUI.PLUGIN_ID, "/icons/e16/portlet_16x16.png" ) );
            imageRegistry.put(
                API,
                ADTUI.imageDescriptorFromPlugin( ADTUI.PLUGIN_ID, "/icons/e16/portlet_16x16.png" ) );
        }

        @Override
        public Image getImage( Object element )
        {
            if( element instanceof MobileAPI )
            {
                if( LIFERAY_CORE_API.equals( ( (MobileAPI) element ).name ) )
                {
                    return this.getImageRegistry().get( LIFERAY_CORE_API );
                }
                else
                {
                    return this.getImageRegistry().get( ROOT_API );
                }
            }
            else if( element instanceof String )
            {
                return getImageRegistry().get( API );
            }

            return null;
        }

        public StyledString getStyledText( Object element )
        {
            final StyledString styled = new StyledString();

            if( element instanceof MobileAPI )
            {
                styled.append( ( (MobileAPI) element ).name );
            }
            else if( element instanceof String )
            {
                styled.append( (String) element );
            }

            return styled;
        }

        public Color getForeground( Object element )
        {
            if( LOADING_MSG.equals( element ) )
            {
                return Display.getDefault().getSystemColor( SWT.COLOR_GRAY );
            }

            return null;
        }

        public Color getBackground( Object element )
        {
            return null;
        }
    }

    @Override
    public FormComponentPresentation createPresentation( SwtPresentation parent, Composite composite )
    {
        return new FormComponentPresentation( this, parent, composite )
        {
            private CheckboxTreeViewer apisTreeViewer;

            private MobileAPI[] rootAPIs;

            @Override
            public void render()
            {
                final Composite parent = SWTUtil.createComposite( composite(), 2, 2, GridData.FILL_BOTH );

                this.apisTreeViewer = new CheckboxTreeViewer( parent, SWT.BORDER );

                this.apisTreeViewer.addCheckStateListener
                (
                    new ICheckStateListener()
                    {
                        public void checkStateChanged( CheckStateChangedEvent event )
                        {
                            handleCheckStateChangedEvent( event );
                        }
                } );

//                this.apisTreeViewer.addSelectionChangedListener( null );

                this.apisTreeViewer.setContentProvider( new APIsContentProvider() );

                this.apisTreeViewer.setLabelProvider( new DelegatingStyledCellLabelProvider( new APIsLabelProvider() ) );

                this.apisTreeViewer.setInput( LOADING_MSG );

                final Tree apisTree = this.apisTreeViewer.getTree();
                final GridData apisTreeData = new GridData( SWT.FILL, SWT.FILL, true,  true, 1, 4 );
                apisTreeData.heightHint = 150;
                apisTreeData.widthHint = 400;
                apisTree.setLayoutData( apisTreeData );

                final Button selectAllButton = new Button( parent, SWT.NONE );
                selectAllButton.setText( "Select All" );
                selectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false ) );
                selectAllButton.addListener( SWT.Selection, new Listener()
                {
                    public void handleEvent( Event event )
                    {
                        for( MobileAPI rootAPI : rootAPIs )
                        {
                            apisTreeViewer.setGrayed( rootAPI, false );
                            apisTreeViewer.setSubtreeChecked( rootAPI, true );
                        }
                    }
                } );

                final Button deselectAllButton = new Button( parent, SWT.NONE );
                deselectAllButton.setText( "Deselect All" );
                deselectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false ) );
                deselectAllButton.addListener( SWT.Selection, new Listener()
                {
                    public void handleEvent( Event event )
                    {
                        for( MobileAPI rootAPI : rootAPIs )
                        {
                            apisTreeViewer.setSubtreeChecked( rootAPI, false );
                        }
                    }
                } );

                final Button refreshButton = new Button( parent, SWT.NONE );
                refreshButton.setText( "Refresh" );
                refreshButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false ) );
                refreshButton.addListener( SWT.Selection, new Listener()
                {
                    public void handleEvent( Event event )
                    {
                        apisTreeViewer.setInput( LOADING_MSG );
                        startAPIUpdateThread();
                    }
                } );

                startAPIUpdateThread();
            }

            private void handleCheckStateChangedEvent( CheckStateChangedEvent event )
            {
                if( event.getSource().equals( apisTreeViewer ) )
                {
                    final Object element = event.getElement();

                    if( element instanceof MobileAPI )
                    {
                        apisTreeViewer.setGrayed( element, false );
                        apisTreeViewer.setSubtreeChecked( element, event.getChecked() );
                    }
                    else if( element instanceof String )
                    {
                        for( MobileAPI rootAPI : rootAPIs )
                        {
                            if( Arrays.asList( rootAPI.apis ).contains( (String) element ) )
                            {
                                int apiCount = rootAPI.apis.length;

                                int checkedCount = 0;

                                for( String api : rootAPI.apis )
                                {
                                    if( apisTreeViewer.getChecked( api ) )
                                    {
                                        checkedCount++;
                                    }
                                }

                                if( checkedCount == apiCount )
                                {
                                    apisTreeViewer.setChecked( rootAPI, true );
                                    apisTreeViewer.setGrayed( rootAPI, false );
                                }
                                else if( checkedCount == 0 )
                                {
                                    apisTreeViewer.setChecked( rootAPI, false );
                                }
                                else
                                {
                                    apisTreeViewer.setGrayChecked( rootAPI, true );
                                }

                                break;
                            }
                        }
                    }
                }
            }

            private void startAPIUpdateThread()
            {
                final Thread t = new Thread()
                {
                    public void run()
                    {
                        checkAndUpdateAPIs();
                    }
                };

                t.start();
            }

            private void checkAndUpdateAPIs()
            {
                final MobileSDKLibrariesOp op = getLocalModelElement().nearest( MobileSDKLibrariesOp.class );
                final String url = op.getUrl().content();
                final String username = op.getOmniUsername().content();
                final String password = op.getOmniPassword().content();

                rootAPIs = MobileSDKCore.discoverAPIs( url, username, password );

                UIUtil.async
                (
                    new Runnable()
                    {
                        public void run()
                        {
                            apisTreeViewer.setInput( rootAPIs );

                            if( rootAPIs != null && rootAPIs.length > 0)
                            {
                                for( MobileAPI rootAPI : rootAPIs )
                                {
                                    if( LIFERAY_CORE_API.equals( rootAPI.name ) )
                                    {
                                        apisTreeViewer.setChecked( rootAPI, true );
                                        apisTreeViewer.setGrayed( rootAPI, false );
                                    }
                                }
                            }

                            apisTreeViewer.expandAll();
                        }
                    }
                );
            }
        };
    }

}
