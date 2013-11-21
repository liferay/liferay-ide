
package com.liferay.ide.project.ui;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;

import com.liferay.ide.project.core.util.ProjectUtil;

/**
 * 
 * @author Kuo Zhang
 *
 */
public class HasNonDefaultEncodingLanguageFileTester extends PropertyTester
{

    public HasNonDefaultEncodingLanguageFileTester()
    {
        super();
    }

    public boolean test( Object receiver, String property, Object[] args, Object expectedValue )
    {
        if( receiver instanceof IProject )
        {
           return ProjectUtil.hasNonDefaultEncodingLanguageFile( (IProject) receiver );
        }

        return false;
    }

}
