package com.liferay.ide.project.core.workspace;

import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import org.eclipse.sapphire.DerivedValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;


/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class HasBundlesDirDerivedValueService extends DerivedValueService
{

    private FilteredListener<PropertyContentEvent> listener;

    @Override
    protected void initDerivedValueService()
    {
        super.initDerivedValueService();

        this.listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().property( ImportLiferayWorkspaceOp.PROP_WORKSPACE_LOCATION ).attach( this.listener );
    }

    @Override
    protected String compute()
    {
        String retval = "false";

        final Path path = op().getWorkspaceLocation().content();

        if( path != null && LiferayWorkspaceUtil.hasBundlesDir( path.toOSString() ) )
        {
            retval = "true";
        }

        return retval;
    }

    private ImportLiferayWorkspaceOp op()
    {
        return context( ImportLiferayWorkspaceOp.class );
    }

}
