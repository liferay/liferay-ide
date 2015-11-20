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

    // *** Previou Liferay Location ***

    @AbsolutePath
    @Label(standard = "Previous Liferay location")
    @Type( base = Path.class )
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    @Service( impl = PreviousLiferayLocationValidationService.class )
    ValueProperty PROP_PREVIOUS_LIFERAY_LOCATION = new ValueProperty( TYPE, "PreviousLiferayLocation" );

    Value<Path> getPreviousLiferayLocation();
    void setPreviousLiferayLocation( String value );
    void setPreviousLiferayLocation( Path value );

    // *** New Liferay Name ***

    @Service(impl = NewLiferayNameDVS.class )
    @Label(standard = "New Liferay name")
    ValueProperty PROP_NEW_LIFERAY_NAME = new ValueProperty( TYPE, "NewLiferayName" );

    Value<String> getNewLiferayName();
    void setNewLiferayName( String value );

    // *** New Liferay Location ***

    @AbsolutePath
    @Label(standard = "New Liferay location")
    @Type( base = Path.class )
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    @Service( impl = NewLiferayLocationValidationService.class )
    ValueProperty PROP_NEW_LIFERAY_LOCATION = new ValueProperty( TYPE, "NewLiferayLocation" );

    Value<Path> getNewLiferayLocation();
    void setNewLiferayLocation( String value );
    void setNewLiferayLocation( Path value );

    @DelegateImplementation( CopyPortalSettingsHandler.class )
    Status execute( ProgressMonitor monitor );

    public static class NewLiferayNameDVS extends NameDVS
    {
        public NewLiferayNameDVS()
        {
            super( PROP_NEW_LIFERAY_LOCATION );
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
