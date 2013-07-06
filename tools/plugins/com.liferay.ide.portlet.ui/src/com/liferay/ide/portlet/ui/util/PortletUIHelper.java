/**
 * 
 */

package com.liferay.ide.portlet.ui.util;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.PropertyEditorPart;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.swt.widgets.Text;

/**
 * @author Kamesh Sampath
 */
public class PortletUIHelper
{

    /**
     * @param text
     * @param project
     * @param searchScope
     */
    public static void addTypeFieldAssistToText(

    PropertyEditorPart propertyEditor, Text text, IProject project, int searchScope )
    {

    }

    /**
     * @return
     */
    public static IProject getProject( ISapphirePart sapphirePart )
    {
        IProject project = null;
        SapphireEditor sapphireEditor = sapphirePart.nearest( SapphireEditor.class );
        IFile editorFile = sapphireEditor.getFile();
        project = editorFile.getProject();
        return project;
    }

    /**
     * @param project
     * @return
     */
    public static IJavaSearchScope getSearchScope( IProject project )
    {
        return getSearchScope( JavaCore.create( project ) );
    }

    /**
     * @param project
     * @return
     */
    public static IJavaSearchScope getSearchScope( IJavaProject project )
    {
        return SearchEngine.createJavaSearchScope( getNonJRERoots( project ) );
    }

    /**
     * @param project
     * @return
     */
    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public static IPackageFragmentRoot[] getNonJRERoots( IJavaProject project )
    {
        ArrayList result = new ArrayList();
        try
        {
            IPackageFragmentRoot[] roots = project.getAllPackageFragmentRoots();
            for( int i = 0; i < roots.length; i++ )
            {
                if( !isJRELibrary( roots[i] ) )
                {
                    result.add( roots[i] );
                }
            }
        }
        catch( JavaModelException e )
        {
        }
        return (IPackageFragmentRoot[]) result.toArray( new IPackageFragmentRoot[result.size()] );
    }

    /**
     * @param root
     * @return
     */
    public static boolean isJRELibrary( IPackageFragmentRoot root )
    {
        try
        {
            IPath path = root.getRawClasspathEntry().getPath();
            if( path.equals( new Path( JavaRuntime.JRE_CONTAINER ) ) ||
                path.equals( new Path( JavaRuntime.JRELIB_VARIABLE ) ) )
            {
                return true;
            }
        }
        catch( JavaModelException e )
        {
        }
        return false;
    }

}
