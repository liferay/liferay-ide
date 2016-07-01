
package com.liferay.ide.ui.templates;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.template.java.CompilationUnitContext;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariableResolver;

/**
 * @author Joye Luo
 */
@SuppressWarnings( "restriction" )
public class ServiceClassNameResolver extends TemplateVariableResolver
{

    public ServiceClassNameResolver()
    {
        super( "service_class_name", "get service class name in liferay module project" );
    }

    @Override
    protected String resolve( TemplateContext context )
    {
        String serviceClassName = "";

        if( context instanceof CompilationUnitContext )
        {
            CompilationUnitContext compilationUnitContext = (CompilationUnitContext) context;

            ICompilationUnit unit = compilationUnitContext.getCompilationUnit();

            String typeName = JavaCore.removeJavaLikeExtension( unit.getElementName() );

            IType type = unit.getType( typeName );

            try
            {
                String[] names = type.getSuperInterfaceNames();

                if( names.length != 0 )
                {
                    serviceClassName = names[0];
                }
                else
                {
                    serviceClassName = type.getSuperclassName();
                }
            }
            catch( JavaModelException e )
            {
            }
        }

        return serviceClassName;
    }

    @Override
    protected boolean isUnambiguous( TemplateContext context )
    {
        return true;
    }

}
