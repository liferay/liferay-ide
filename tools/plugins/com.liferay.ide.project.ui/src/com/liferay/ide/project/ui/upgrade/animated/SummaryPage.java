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

package com.liferay.ide.project.ui.upgrade.animated;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;

import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.util.SWTUtil;

/**
 * @author Joye Luo
 */

public class SummaryPage extends Page implements SelectionChangedListener
{

    public SummaryPage( Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style, dataModel, SUMMARY_PAGE_ID, false );

        Composite container = new Composite( this, SWT.NONE );
        container.setLayout( new GridLayout( 2, false ) );
        container.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        tableViewer = new TableViewer( container );
        tableViewer.setContentProvider( new TableViewContentProvider() );
        tableViewer.setLabelProvider( new TableViewLabelProvider() );
        tableViewer.getControl().setBackground( this.getDisplay().getSystemColor( SWT.COLOR_WIDGET_BACKGROUND ) );

        final Table table = tableViewer.getTable();
        final GridData tableData = new GridData( SWT.FILL, SWT.FILL, true, false, 1, 1 );
        tableData.heightHint = 150;
        table.setLayoutData( tableData );
        table.setLinesVisible( false );

        createImages();
    }

    private TableViewer tableViewer;
    private Image imageQuestion;

    private class TableViewElement
    {

        private String pageTitle;
        private Image image;

        public TableViewElement( String pageTitle, Image image )
        {
            this.pageTitle = pageTitle;
            this.image = image;
        }
    }

    private class TableViewContentProvider implements IStructuredContentProvider
    {

        @Override
        public void dispose()
        {
        }

        @Override
        public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
        {
        }

        @Override
        public Object[] getElements( Object inputElement )
        {
            if( inputElement instanceof TableViewElement[] )
            {
                return (TableViewElement[]) inputElement;
            }

            return new Object[] { inputElement };
        }

    }

    class TableViewLabelProvider extends LabelProvider
    {

        @Override
        public Image getImage( Object element )
        {
            TableViewElement tableViewElement = (TableViewElement) element;
            return tableViewElement.image;
        }

        @Override
        public String getText( Object element )
        {
            TableViewElement tableViewElement = (TableViewElement) element;
            return tableViewElement.pageTitle;
        }
    }

    private void createImages()
    {
        imageQuestion = ImageDescriptor.createFromURL(
            ProjectUI.getDefault().getBundle().getEntry( "/images/question.png" ) ).createImage();
    }

    public void createSpecialDescriptor( Composite parent, int style )
    {
        final String descriptor = "Upgrade results are summarised in the following table.\n" +
            "If there are still some steps failed or incompleted, you can go back to finish them.\n" +
            "If all the steps are well-done, congratulations! You have finished the whole upgrade process.\n" +
            "Now you can try to deploy your projects to Liferay portal." +
            "For more upgrade information, please see <a>From Liferay 6 to Liferay 7</a>.";
        String url = "https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-0/from-liferay-6-to-liferay-7";

        Link link = SWTUtil.createHyperLink( this, style, descriptor, 1, url );
        link.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, true, false, 2, 1 ) );
    }

    @Override
    public String getPageTitle()
    {
        return "Summary";
    }

    @Override
    public int getGridLayoutCount()
    {
        return 2;
    }

    @Override
    public boolean getGridLayoutEqualWidth()
    {
        return false;
    }


    @Override
    public void onSelectionChanged( int targetSelection )
    {
        setInput();
    }

    private void setInput()
    {
        List<TableViewElement> TableViewElementList = new ArrayList<TableViewElement>();
        TableViewElement[] tableViewElements;
        int pageNum = UpgradeView.getPageNumber();

        for( int i = 2; i < pageNum - 1; i++ )
        {
            Page page = UpgradeView.getPage( i );
            String pageTitle = page.getPageTitle();
            PageAction pageAction = page.getSelectedAction();
            Image statusImage;

            if( pageAction == null )
            {
                statusImage = imageQuestion;
            }
            else
            {
                statusImage = page.getSelectedAction().getBageImage();
            }

            TableViewElement tableViewElement = new TableViewElement( pageTitle, statusImage );
            TableViewElementList.add( tableViewElement );
        }

        tableViewElements = TableViewElementList.toArray( new TableViewElement[TableViewElementList.size()] );
        tableViewer.setInput( tableViewElements );
    }

}
