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
 * Contributors:
 *      Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.hook.core.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.hook.core.HookCore;
import com.liferay.ide.hook.core.dd.HookDescriptorHelper;
import com.liferay.ide.hook.core.model.CustomJspDir;
import com.liferay.ide.hook.core.model.Hook;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.ValManager;
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

    private final static String VALIDATOR_ID = "org.eclipse.jst.jsp.core.JSPBatchValidator";

    public static boolean configureJSPSyntaxValidationExclude(
        IProject project, IFolder customFolder, boolean configureRule )
    {
        boolean retval = false;

        try
        {

            final Validator validator = ValManager.getDefault().getValidator( VALIDATOR_ID, project );

            final ValidatorMutable validatorTable = new ValidatorMutable( validator );

            // check for exclude group
            FilterGroup excludeGroup = null;

            for( FilterGroup group : validatorTable.getGroups() )
            {
                if( group.isExclude() )
                {
                    excludeGroup = group;
                    break;
                }
            }

            final String customJSPFolderPattern =
                customFolder.getFullPath().makeRelativeTo( customFolder.getProject().getFullPath() ).toPortableString();

            final FilterRule folderRule =
                FilterRule.createFile( customJSPFolderPattern, true, FilterRule.File.FileTypeFolder );

            if( excludeGroup == null )
            {
                if( configureRule )
                {
                    excludeGroup = FilterGroup.create( true, new FilterRule[] { folderRule } );
                    validatorTable.add( excludeGroup );
                    retval = true;
                }
            }
            else
            {
                boolean hasCustomJSPFolderRule = false;

                for( FilterRule rule : excludeGroup.getRules() )
                {
                    if( customJSPFolderPattern.equals( rule.getPattern() ) )
                    {
                        if( configureRule )
                        {
                            FilterGroup newExcludeGroup = FilterGroup.removeRule( excludeGroup, rule );
                            validatorTable.replaceFilterGroup(
                                excludeGroup, FilterGroup.addRule( newExcludeGroup, folderRule ) );
                        }
                        hasCustomJSPFolderRule = true;
                        break;
                    }
                }

                if( !hasCustomJSPFolderRule )
                {
                    if( configureRule )
                    {
                        validatorTable.replaceFilterGroup( excludeGroup, FilterGroup.addRule( excludeGroup, folderRule ) );

                        hasCustomJSPFolderRule = true;
                    }
                }
                retval = hasCustomJSPFolderRule;
            }

            if( configureRule )
            {
                final ProjectConfiguration pc = ConfigurationManager.getManager().getProjectConfiguration( project );
                pc.setDoesProjectOverride( true );

                final ProjectPreferences pp = new ProjectPreferences( project, true, false, null );

                final ValPrefManagerProject vpm = new ValPrefManagerProject( project );

                final ValidatorMutable[] validatorTables = new ValidatorMutable[] { validatorTable };
                
                vpm.savePreferences( pp, validatorTables );
            }
        }
        catch( Exception e )
        {
            HookCore.logError( "Unable to configure jsp syntax validation folder exclude rule.", e ); //$NON-NLS-1$
        }

        return retval;

    }

    public static IFolder getCustomJspFolder( Hook hook, IProject project )
    {
        CustomJspDir element = hook.getCustomJspDir().content();

        if( element != null && ( !element.getValue().empty() ) )
        {
            // IDE-110 IDE-648
            IVirtualFolder webappRoot = CoreUtil.getDocroot( project );

            if( element != null && webappRoot != null )
            {
                org.eclipse.sapphire.modeling.Path customJspDir = element.getValue().content();

                IVirtualFolder customJspFolder = webappRoot.getFolder( customJspDir.toPortableString() );

                for( IContainer folder : customJspFolder.getUnderlyingFolders() )
                {
                    if( folder != null && !folder.isDerived() )
                    {
                        return (IFolder) folder;
                    }
                }
            }
        }

        return null;
    }

    public static IPath getCustomJspPath( IProject project )
    {
        final HookDescriptorHelper hookDescriptor = new HookDescriptorHelper( project );
        final String customJSPFolder = hookDescriptor.getCustomJSPFolder( null );

        if( customJSPFolder != null )
        {
            final IFolder docFolder = CoreUtil.getDefaultDocrootFolder( project );

            if( docFolder != null )
            {
                final IPath newPath = Path.fromOSString( customJSPFolder );
                final IPath pathValue = docFolder.getFullPath().append( newPath );

                return pathValue;
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
