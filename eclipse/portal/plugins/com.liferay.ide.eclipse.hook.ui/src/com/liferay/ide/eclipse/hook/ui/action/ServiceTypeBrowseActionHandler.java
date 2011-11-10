/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package com.liferay.ide.eclipse.hook.ui.action;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.dialogs.ITypeInfoFilterExtension;
import org.eclipse.jdt.ui.dialogs.ITypeInfoRequestor;
import org.eclipse.jdt.ui.dialogs.TypeSelectionExtension;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireBrowseActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ISapphireActionHandlerDef;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ServiceTypeBrowseActionHandler extends SapphireBrowseActionHandler
{

	public static final String ID = "ServiceType.Browse.Java.Type";
    
    private int browseDialogStyle;
    
	private String kind;

    @Override
    public void init( final SapphireAction action,
                      final ISapphireActionHandlerDef def )
    {
        super.init( action, def );

        setId( ID );
        
		this.browseDialogStyle = IJavaElementSearchConstants.CONSIDER_INTERFACES;

		kind = def.getParam( "kind" );
    }
    
    @Override
    public String browse( final SapphireRenderingContext context )
    {
        final IModelElement element = getModelElement();
        final ModelProperty property = getProperty();
        

        final IProject project = element.adapt( IProject.class );
        
        try 
        {
        	IJavaSearchScope scope= SearchEngine.createJavaSearchScope(new IJavaProject[] { JavaCore.create(project) });
        	
			TypeSelectionExtension extension = null;

			if ( "type".equals( kind ) )
			{
				extension = new TypeSelectionExtension()
				{

					@Override
					public ITypeInfoFilterExtension getFilterExtension()
					{
						return new ITypeInfoFilterExtension()
						{

							public boolean select( ITypeInfoRequestor typeInfoRequestor )
							{
								return typeInfoRequestor.getPackageName().startsWith( "com.liferay" ) &&
									typeInfoRequestor.getTypeName().endsWith( "Service" );
							}
						};
					}
				};
			}
			else if ( "impl".equals( kind ) )
			{
				extension = new TypeSelectionExtension()
				{

					@Override
					public ITypeInfoFilterExtension getFilterExtension()
					{
						return new ITypeInfoFilterExtension()
						{

							public boolean select( ITypeInfoRequestor typeInfoRequestor )
							{
								return typeInfoRequestor.getPackageName().startsWith( "com.liferay" ) &&
									typeInfoRequestor.getTypeName().endsWith( "Wrapper" );
							}
						};
					}
				};
			}

			final SelectionDialog dlg =
				JavaUI.createTypeDialog( context.getShell(), null, scope, this.browseDialogStyle, false, "", extension );

            final String title = property.getLabel( true, CapitalizationType.TITLE_STYLE, false );
			dlg.setTitle( "Select " + title );
            
            
            if (dlg.open() == SelectionDialog.OK) {
                Object results[] = dlg.getResult();
                assert results != null && results.length == 1;
                if (results[0] instanceof IType) {
                    return ((IType) results[0]).getFullyQualifiedName();
                }
            }
        } catch (JavaModelException e) {
            SapphireUiFrameworkPlugin.log( e );
        }
        
        return null;
    }


}