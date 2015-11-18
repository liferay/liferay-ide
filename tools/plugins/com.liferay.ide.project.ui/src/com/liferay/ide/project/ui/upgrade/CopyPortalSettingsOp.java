package com.liferay.ide.project.ui.upgrade;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;


/**
 * @author Gregory Amerson
 * @author Lovett Li
 */
public interface CopyPortalSettingsOp extends ExecutableElement
{
    ElementType TYPE = new ElementType( CopyPortalSettingsOp.class );

    // *** Source Portal Name ***

    @Label(standard = "Previous Liferay name")
    @Service(impl = SourceLiferayNameDVS.class )
    ValueProperty PROP_SOURCE_LIFERAY_NAME = new ValueProperty( TYPE, "SourceLiferayName" );

    Value<String> getSourceLiferayName();
    void setSourceLiferayName( String value );

    // *** Source Portal Location ***

    @AbsolutePath
    @Label(standard = "Previous Liferay location")
    @Type( base = Path.class )
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    @Service( impl = PreviousLiferayLocationValidationService.class )
    // TODO Add liferay folder validation
    ValueProperty PROP_SOURCE_LIFERAY_LOCATION = new ValueProperty( TYPE, "SourceLiferayLocation" );

    Value<Path> getSourceLiferayLocation();
    void setSourceLiferayLocation( String value );
    void setSourceLiferayLocation( Path value );

    // *** Desitnation Liferay Name ***

    @Service(impl = DestinationLiferayNameDVS.class )
    @Label(standard = "New Liferay name")
    ValueProperty PROP_DESTINATION_LIFERAY_NAME = new ValueProperty( TYPE, "DestinationLiferayName" );

    Value<String> getDestinationLiferayName();
    void setDestinationLiferayName( String value );

    // *** Destination Liferay Location ***

    @AbsolutePath
    @Label(standard = "New Liferay location")
    @Type( base = Path.class )
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    @Service( impl = NewLiferayLocationValidationService.class )
    // TODO Add liferay folder validation
    ValueProperty PROP_DESTINATION_LIFERAY_LOCATION = new ValueProperty( TYPE, "DestinationLiferayLocation" );

    Value<Path> getDestinationLiferayLocation();
    void setDestinationLiferayLocation( String value );
    void setDestinationLiferayLocation( Path value );

    @DelegateImplementation( CopyPortalSettingsHandler.class )
    Status execute( ProgressMonitor monitor );

    public static class DestinationLiferayNameDVS extends NameDVS
    {
        public DestinationLiferayNameDVS()
        {
            super( PROP_DESTINATION_LIFERAY_LOCATION );
        }
    }

    public static class SourceLiferayNameDVS extends NameDVS
    {
        public SourceLiferayNameDVS()
        {
            super( PROP_SOURCE_LIFERAY_LOCATION );
        }
    }

    public static class NameDVS extends DefaultValueService
    {
        private ValueProperty _prop;

        public NameDVS( ValueProperty prop )
        {
            _prop = prop;
        }

        @Override
        protected void initDefaultValueService()
        {
            context( CopyPortalSettingsOp.class ).property( _prop ).attach( new FilteredListener<PropertyContentEvent>()
            {
                protected void handleTypedEvent( PropertyContentEvent arg0 )
                {
                    refresh();
                }
            });
        }

        @Override
        protected String compute()
        {
            Path path = (Path) context( CopyPortalSettingsOp.class ).property( _prop ).content();

            if( path != null )
            {
                return path.lastSegment();
            }
            else
            {
                return null;
            }
        }
    }

}
