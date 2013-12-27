/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 * Contributors:
 *      Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/
package com.liferay.ide.hook.core.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.hook.core.HookCore;
import com.liferay.ide.hook.core.model.CustomJspDir;
import com.liferay.ide.hook.core.model.Hook;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValManager.UseProjectPreferences;
import org.eclipse.wst.validation.internal.ValPrefManagerProject;
import org.eclipse.wst.validation.internal.ValidatorMutable;
import org.eclipse.wst.validation.internal.model.FilterGroup;
import org.eclipse.wst.validation.internal.model.FilterRule;
import org.eclipse.wst.validation.internal.model.ProjectPreferences;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class HookUtil
{

    public static void configureJSPSyntaxValidationExclude( IProject project, IFolder customFolder, boolean isNewRule  )
    {
        try
        {

            Validator[] vals =
                ValManager.getDefault().getValidatorsConfiguredForProject( project, UseProjectPreferences.MustUse );

            ValidatorMutable[] validators = new ValidatorMutable[vals.length];

            for( int i = 0; i < vals.length; i++ )
            {
                validators[i] = new ValidatorMutable( vals[i] );

                if( "org.eclipse.jst.jsp.core.JSPBatchValidator".equals( validators[i].getId() ) ) //$NON-NLS-1$
                {
                    // check for exclude group
                    FilterGroup excludeGroup = null;

                    for( FilterGroup group : validators[i].getGroups() )
                    {
                        if( group.isExclude() )
                        {
                            excludeGroup = group;
                            break;
                        }
                    }

                    String customJSPFolderPattern =
                        customFolder.getFullPath().makeRelativeTo( customFolder.getProject().getFullPath() ).toPortableString();

                    FilterRule folderRule =
                        FilterRule.createFile( customJSPFolderPattern, true, FilterRule.File.FileTypeFolder );

                    if( excludeGroup == null )
                    {
                        if( isNewRule )
                        {
                            excludeGroup = FilterGroup.create( true, new FilterRule[] { folderRule } );
                            validators[i].add( excludeGroup );

                        }
                    }
                    else
                    {
                        boolean hasCustomJSPFolderRule = false;

                        for( FilterRule rule : excludeGroup.getRules() )
                        {
                            if( customJSPFolderPattern.equals( rule.getPattern() ) )
                            {
                                if( !isNewRule )
                                {
                                    FilterGroup newExcludeGroup = FilterGroup.removeRule( excludeGroup, rule );
                                    validators[i].replaceFilterGroup( excludeGroup, newExcludeGroup );
                                }
                                hasCustomJSPFolderRule = true;
                                break;
                            }

                        }
                        if( !hasCustomJSPFolderRule )
                        {
                            validators[i].replaceFilterGroup(
                                excludeGroup, FilterGroup.addRule( excludeGroup, folderRule ) );
                        }

                    }

                }
            }

            ProjectConfiguration pc = ConfigurationManager.getManager().getProjectConfiguration( project );
            pc.setDoesProjectOverride( true );

            ProjectPreferences pp = new ProjectPreferences( project, true, false, null );

            ValPrefManagerProject vpm = new ValPrefManagerProject( project );
            vpm.savePreferences( pp, validators );
        }
        catch( Exception e )
        {
            HookCore.logError( "Unable to configure jsp syntax validation folder exclude rule.", e ); //$NON-NLS-1$
        }
    }
    
    
    
    public static IFolder getCustomJspFolder(Hook hook, IProject project)
    {
        CustomJspDir element = hook.getCustomJspDir().content();

        if( element != null && ( ! element.getValue().empty() ) )
        {
            // IDE-110 IDE-648
            IVirtualFolder webappRoot = CoreUtil.getDocroot( project );

            if( element != null && webappRoot != null )
            {
                org.eclipse.sapphire.modeling.Path customJspDir = element.getValue().content();

                IVirtualFolder customJspFolder = webappRoot.getFolder( customJspDir.toPortableString() );

                for( IContainer folder : customJspFolder.getUnderlyingFolders() )
                {
                    if( folder != null && ! folder.isDerived() )
                    {
                        return (IFolder) folder;
                    }
                }
            }
        }

        return null;
    }

    /**
     * A small utility method used to compute the DTD version
     *
     * @param document
     *            - the document that is loaded by the editor
     */
    public static String getDTDVersion( Document document )
    {
        String dtdVersion = null;
        DocumentType docType = document.getDoctype();

        if( docType != null )
        {
            String publicId = docType.getPublicId();
            String systemId = docType.getSystemId();
            if( publicId != null && systemId != null )
            {
                if( publicId.contains( "6.0.0" ) || systemId.contains( "6.0.0" ) ) //$NON-NLS-1$ //$NON-NLS-2$
                {
                    dtdVersion = "6.0.0"; //$NON-NLS-1$
                }
                else if( publicId.contains( "6.1.0" ) || systemId.contains( "6.1.0" ) ) //$NON-NLS-1$ //$NON-NLS-2$
                {
                    dtdVersion = "6.1.0"; //$NON-NLS-1$
                }
            }
        }

        return dtdVersion;
    }

}
