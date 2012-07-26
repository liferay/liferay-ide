
package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;

import java.util.Arrays;
import java.util.SortedSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.services.PossibleValuesService;

/**
 * @author Gregory Amerson
 */
public class PortalFilterNamesPossibleValuesService extends PossibleValuesService
{

    private IFile hookFile;

    @Override
    protected void init()
    {
        super.init();

        this.hookFile = this.context().find( IModelElement.class ).adapt( IFile.class );
    }

    @Override
    protected void fillPossibleValues( SortedSet<String> values )
    {
        if( hookFile != null )
        {
            try
            {
                ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( hookFile.getProject() );

                String[] names = liferayRuntime.getServletFilterNames();

                values.addAll( Arrays.asList( names ) );
            }
            catch( Exception e )
            {
            }
        }
    }

}
