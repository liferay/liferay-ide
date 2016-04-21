package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.CoreUtil;

import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;


/**
 * @author Gregory Amerson
 */
public class JavaPackageNameDefaultValueService extends DefaultValueService
{

    @Override
    protected String compute()
    {
        String retval = null;

        final IJavaProject project = context( NewLiferayComponentOp.class ).adapt( IJavaProject.class );

        if( project != null )
        {
            try
            {
                final List<IFolder> srcFolders = CoreUtil.getSourceFolders( project );

                final IPackageFragmentRoot[] roots = project.getAllPackageFragmentRoots();

                if( !CoreUtil.isNullOrEmpty( roots ) )
                {
                    for( IPackageFragmentRoot root : roots )
                    {
                        final IJavaElement[] packages = root.getChildren();

                        if( !CoreUtil.isNullOrEmpty( packages ) )
                        {
                            for( IJavaElement element : packages )
                            {
                                if( element instanceof IPackageFragment )
                                {
                                    final IPackageFragment fragment = (IPackageFragment) element;

                                    boolean isInSourceFolder = false;

                                    for( IFolder srcFolder : srcFolders )
                                    {
                                        if( srcFolder.getFullPath().isPrefixOf( fragment.getPath() ) )
                                        {
                                            isInSourceFolder = true;
                                            break;
                                        }
                                    }

                                    if( isInSourceFolder )
                                    {
                                        final String elementName = fragment.getElementName();

                                        if( !CoreUtil.isNullOrEmpty( elementName ) )
                                        {
                                            retval = elementName;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch( JavaModelException e )
            {
            }
        }

        return retval;
    }

    @Override
    protected void initDefaultValueService()
    {
        super.initDefaultValueService();

        FilteredListener<PropertyContentEvent> listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().property( NewLiferayComponentOp.PROP_PROJECT_NAME ).attach( listener );
    }

    private NewLiferayComponentOp op()
    {
        return context( NewLiferayComponentOp.class );
    }
}
