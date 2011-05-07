
package com.liferay.ide.eclipse.taglib.ui.model;

import com.liferay.ide.eclipse.taglib.ui.model.internal.PreviewSourceContentProvider;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DependsOn;
import org.eclipse.sapphire.modeling.annotations.DerivedValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.ReadOnly;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlRootBinding;


@GenerateImpl
@XmlRootBinding(elementName = "tag")
public interface ITag extends IModelElement {

	ModelElementType TYPE = new ModelElementType(ITag.class);

	// *** Name ***

	@XmlBinding(path = "name")
	@ReadOnly
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	Value<String> getName();

	// *** Prefix ***

	@XmlBinding(path = "prefix")
	@ReadOnly
	ValueProperty PROP_PREFIX = new ValueProperty(TYPE, "Prefix");

	Value<String> getPrefix();

	// *** Required Attributes ***

	@Type(base = IAttribute.class)
	@ReadOnly
	@XmlListBinding(path = "required", mappings = @XmlListBinding.Mapping(element = "attribute", type = IAttribute.class))
	ListProperty PROP_REQUIRED_ATTRIBUTES = new ListProperty(TYPE, "RequiredAttributes");

	ModelElementList<IAttribute> getRequiredAttributes();

	// *** Event Attributes ***

	@Type(base = IAttribute.class)
	@ReadOnly
	@XmlListBinding(path = "events", mappings = @XmlListBinding.Mapping(element = "attribute", type = IAttribute.class))
	ListProperty PROP_EVENTS = new ListProperty(TYPE, "Events");

	ModelElementList<IAttribute> getEvents();

	// *** Other Attributes ***

	@Type(base = IAttribute.class)
	@ReadOnly
	@XmlListBinding(path = "other", mappings = @XmlListBinding.Mapping(element = "attribute", type = IAttribute.class))
	ListProperty PROP_OTHER_ATTRIBUTES = new ListProperty(TYPE, "OtherAttributes");

	ModelElementList<IAttribute> getOtherAttributes();

	@DerivedValue(service = PreviewSourceContentProvider.class)
	@DependsOn({ "RequiredAttributes/*", "Events/*", "OtherAttributes/*" })
	ValueProperty PROP_PREVIEW = new ValueProperty(TYPE, "Preview");

	Value<String> getPreview();

	@DerivedValue(service = PreviewSourceContentProvider.class)
	@DependsOn({ "RequiredAttributes/*", "Events/*", "OtherAttributes/*" })
	ValueProperty PROP_SOURCE = new ValueProperty(TYPE, "Source");

	Value<String> getSource();
}
