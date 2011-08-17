
package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

@GenerateImpl
@Image( path = "images/obj16/preferences.gif" )
public interface IPortletPreference extends IModelElement, IIdentifiable {

	ModelElementType TYPE = new ModelElementType( IPortletPreference.class );

	// *** PortletPreferences ***

	@Type( base = IPreference.class )
	@Label( standard = "Preferences" )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "preference", type = IPreference.class ) )
	ListProperty PROP_PORTLET_PREFERENCES = new ListProperty( TYPE, "PortletPreferences" );

	ModelElementList<IPreference> getPortletPreferences();

	// *** PreferernceValidator ***

	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = { "javax.portlet.PreferenceValidator" } )
	@Label( standard = "Preferernce Validator" )
	@NoDuplicates
	@XmlBinding( path = "preferences-validator" )
	ValueProperty PROP_PREFERERNCE_VALIDATOR = new ValueProperty( TYPE, "PreferernceValidator" );

	ReferenceValue<JavaTypeName, JavaType> getPreferernceValidator();

	void setPreferernceValidator( String portletClass );

	void setPreferernceValidator( JavaTypeName portletClass );

}
