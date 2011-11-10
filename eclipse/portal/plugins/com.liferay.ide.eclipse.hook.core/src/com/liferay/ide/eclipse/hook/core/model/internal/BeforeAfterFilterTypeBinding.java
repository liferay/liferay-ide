package com.liferay.ide.eclipse.hook.core.model.internal;

import com.liferay.ide.eclipse.hook.core.model.BeforeAfterFilterType;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;


public class BeforeAfterFilterTypeBinding extends XmlValueBindingImpl
{

	private String defaultValueText;
	private String localValue;

	@Override
	public void init( IModelElement element, ModelProperty property, String[] params )
	{
		super.init( element, property, params );

		DefaultValue defaultValue = property.getAnnotation( DefaultValue.class );

		this.defaultValueText = defaultValue.text();
	}

	@Override
	public String read()
	{
		// check for existence of before-filter or after-filter elements, if neither exist, then return default value

		XmlElement xmlElement = xml();

		XmlElement beforeFilterElement =
			xmlElement.getChildElement( BeforeAfterFilterType.BEFORE_FILTER.getText(), false );

		if ( beforeFilterElement != null )
		{
			return BeforeAfterFilterType.BEFORE_FILTER.getText();
		}

		XmlElement afterFilterElement =
			xmlElement.getChildElement( BeforeAfterFilterType.AFTER_FILTER.getText(), false );

		if ( afterFilterElement != null )
		{
			return BeforeAfterFilterType.AFTER_FILTER.getText();
		}

		if ( this.localValue != null )
		{
			return this.localValue;
		}

		return this.defaultValueText;
	}

	@Override
	public void write( String value )
	{
		XmlElement xmlElement = xml();

		XmlElement filterElement = xmlElement.getChildElement( BeforeAfterFilterType.BEFORE_FILTER.getText(), false );

		if ( filterElement == null )
		{
			filterElement = xmlElement.getChildElement( BeforeAfterFilterType.AFTER_FILTER.getText(), false );
		}

		String existingFilterValue = null;

		if ( filterElement != null )
		{
			existingFilterValue = filterElement.getText();

			filterElement.remove();

			XmlElement newElement = xmlElement.getChildElement( value, true );

			newElement.setText( existingFilterValue );
		}
		else
		{
			this.localValue = value;
		}

	}

}
