
package com.liferay.ide.project.ui.upgrade;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;

/**
 * @author Andy Wu
 */
public interface ConvertJspHookOp extends ExecutableElement
{

    ElementType TYPE = new ElementType( ConvertJspHookOp.class );

    // *** Source Jsp Hook Location ***

    @AbsolutePath
    @Label( standard = "Source Jsp Hook Location" )
    @Type( base = Path.class )
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    ValueProperty PROP_SOURCE_JSP_HOOK_LOCATION = new ValueProperty( TYPE, "SourceJspHookLocation" );

    Value<Path> getSourceJspHookLocation();

    void setSourceJspHookLocation( String value );

    void setSourceJspHookLocation( Path value );

    @DelegateImplementation( ConvertJspHookHandler.class )
    Status execute( ProgressMonitor monitor );

}
