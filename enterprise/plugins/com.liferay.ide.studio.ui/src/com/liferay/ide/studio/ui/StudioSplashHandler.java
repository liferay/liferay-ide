/*******************************************************************************
 * Copyright (c) 2007, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.liferay.ide.studio.ui;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.branding.IProductConstants;
import org.eclipse.ui.internal.util.PrefUtil;
import org.eclipse.ui.splash.BasicSplashHandler;

/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class StudioSplashHandler extends BasicSplashHandler
{

    private Font newFont;

    public void init( Shell splash )
    {
        super.init( splash );

        String progressRectString = null;
        String foregroundColorString = null;

        final IProduct product = Platform.getProduct();

        if( product != null )
        {
            progressRectString = product.getProperty( IProductConstants.STARTUP_PROGRESS_RECT );
            foregroundColorString = product.getProperty( IProductConstants.STARTUP_FOREGROUND_COLOR );
        }

        final Rectangle progressRect =
            StringConverter.asRectangle( progressRectString, new Rectangle( 10, 10, 300, 15 ) );
        setProgressRect( progressRect );

        int foregroundColorInteger;

        try
        {
            foregroundColorInteger = Integer.parseInt( foregroundColorString, 16 );
        }
        catch( Exception ex )
        {
            foregroundColorInteger = 0xD2D7FF; // off white
        }

        setForeground( new RGB(
            ( foregroundColorInteger & 0xFF0000 ) >> 16, ( foregroundColorInteger & 0xFF00 ) >> 8,
            foregroundColorInteger & 0xFF ) );

        // the following code will be removed for release time
        if( PrefUtil.getInternalPreferenceStore().getBoolean( "SHOW_BUILDID_ON_STARTUP" ) )
        {
            final String buildId = System.getProperty( "eclipse.buildId", "Unknown Build" );
            final String buildIdRect = product.getProperty( "buildIdRect" );

            final Rectangle buildIdRectangle =
                StringConverter.asRectangle( buildIdRect, new Rectangle( 322, 190, 100, 40 ) );

            final GridLayout layout = new GridLayout( 1, false );
            layout.marginRight = 0;
            layout.horizontalSpacing = 0;
            layout.marginWidth = 0;

            final Composite versionComposite = new Composite( getContent(), SWT.NONE );
            versionComposite.setBounds( buildIdRectangle );
            versionComposite.setLayout( layout );

            final Label idLabel = new Label( versionComposite, SWT.NONE );
            idLabel.setLayoutData( new GridData( SWT.TRAIL, SWT.CENTER, true, true, 1, 1 ) );
            idLabel.setForeground( getForeground() );

            final Font initialFont = idLabel.getFont();
            final FontData[] fontData = initialFont.getFontData();

            for( int i = 0; i < fontData.length; i++ )
            {
                fontData[i].setHeight( 14 );
                fontData[i].setStyle( SWT.BOLD );
            }

            this.newFont = new Font( idLabel.getDisplay(), fontData );
            idLabel.setFont( this.newFont );
            idLabel.setText( buildId );

            versionComposite.layout( true );
        }
        else
        {
            getContent(); // ensure creation of the progress
        }
    }

    @Override
    public void dispose()
    {
        super.dispose();

        if( this.newFont != null && ! this.newFont.isDisposed() )
        {
            this.newFont.dispose();
        }
    }

}
