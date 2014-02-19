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
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.mobile.sdk.core.MobileSDKCore;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.swt.FormComponentPresentation;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;


/**
 * @author Gregory Amerson
 */
public class ComponentAPIsCustomPart extends FormComponentPart
{


    class APIInfo
    {

    }

    class APIsContentProvider implements ITreeContentProvider
    {
        public void dispose()
        {
        }

        public Object[] getChildren( Object parentElement )
        {
            return null;
        }

        public Object[] getElements( Object inputElement )
        {
            return null;
        }

        public Object getParent( Object element )
        {
            return null;
        }

        public boolean hasChildren( Object element )
        {
            return false;
        }

        public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
        {
        }
    }

    class APIsLabelProvider extends LabelProvider
        implements IColorProvider, DelegatingStyledCellLabelProvider.IStyledLabelProvider
    {
        @Override
        public String getText( Object element )
        {
            return super.getText( element );
        }

        public StyledString getStyledText( Object element )
        {
            return null;
        }

        public Color getForeground( Object element )
        {
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

            @Override
            public void render()
            {
                final Composite parent = SWTUtil.createComposite( composite(), 2, 2, GridData.FILL_BOTH );

                this.apisTreeViewer = new CheckboxTreeViewer( parent, SWT.BORDER );

//                this.apisTreeViewer.addCheckStateListener( null );

//                this.apisTreeViewer.addSelectionChangedListener( null );

                this.apisTreeViewer.setContentProvider( new APIsContentProvider() );

                this.apisTreeViewer.setLabelProvider( new DelegatingStyledCellLabelProvider( new APIsLabelProvider() ) );

                final Tree apisTree = this.apisTreeViewer.getTree();
                final GridData apisTreeData = new GridData( SWT.FILL, SWT.FILL, true,  true, 1, 4 );
                apisTreeData.heightHint = 150;
                apisTreeData.widthHint = 400;
                apisTree.setLayoutData( apisTreeData );

                final Button selectAllButton = new Button( parent, SWT.NONE );
                selectAllButton.setText( "Select All" );
                final GridData buttonData = new GridData( SWT.FILL, SWT.TOP, false, false );
                selectAllButton.setLayoutData( buttonData );

                final Button deselectAllButton = new Button( parent, SWT.NONE );
                deselectAllButton.setText( "Deselect All" );
                deselectAllButton.setLayoutData( buttonData );

                final Button refreshButton = new Button( parent, SWT.NONE );
                refreshButton.setText( "Refresh" );
                refreshButton.setLayoutData( buttonData );

                startAPIUpdateThread();
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
                final List<APIInfo> apis = new ArrayList<APIInfo>();

                final MobileSDKLibrariesOp op = getLocalModelElement().nearest( MobileSDKLibrariesOp.class );
                final String url = op.getUrl().content();
                final String username = op.getOmniUsername().content();
                final String password = op.getOmniPassword().content();

                final APIInfo liferayCore = new APIInfo();
                apis.add( liferayCore );

                MobileSDKCore.asdf( url, username, password );
            }
        };
    }

}
