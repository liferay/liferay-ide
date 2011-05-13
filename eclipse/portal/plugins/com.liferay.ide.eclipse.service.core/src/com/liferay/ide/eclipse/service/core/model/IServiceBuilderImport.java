package com.liferay.ide.eclipse.service.core.model;

import com.liferay.ide.eclipse.service.core.model.internal.ImportsPathProvider;

import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.BasePathsProvider;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.ValidFileExtensions;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;


@GenerateImpl
@Image(path = "images/file_16x16.gif")
public interface IServiceBuilderImport extends IModelElement {

	ModelElementType TYPE = new ModelElementType(IServiceBuilderImport.class);

	@Type(base = IPath.class)
	@XmlBinding(path = "@file")
	@Label(standard = "file")
	@BasePathsProvider(value = ImportsPathProvider.class)
	@ValidFileSystemResourceType(FileSystemResourceType.FILE)
	@ValidFileExtensions(value = { "xml" })
	@MustExist
	@Required
	ValueProperty PROP_FILE = new ValueProperty(TYPE, "File");

	Value<IPath> getFile();

	void setFile(String value);

	void setFile(IPath value);
}
