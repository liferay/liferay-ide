/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.FileExtensions;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;

import com.liferay.ide.eclipse.portlet.core.model.internal.ProjectRelativePathService;

/**
 * @author kamesh.sampath
 */
@GenerateImpl
@Image( path = "images/resource_bundle.png" )
public interface IResourceBundle extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IResourceBundle.class );

	// *** ResourceBundle ***
	@Type( base = Path.class )
	@MustExist
	@ValidFileSystemResourceType( FileSystemResourceType.FILE )
	@Service( impl = ProjectRelativePathService.class )
	@FileExtensions( expr = "properties" )
	ValueProperty PROP_RESOURCE_BUNDLE = new ValueProperty( TYPE, "ResourceBundle" );

	Value<Path> getResourceBundle();

	void setResourceBundle( String value );

	void setResourceBundle( Path value );

}
