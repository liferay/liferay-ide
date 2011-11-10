package com.liferay.ide.eclipse.hook.core.model.internal;

import com.liferay.ide.eclipse.hook.core.model.BeforeAfterFilterType;
import com.liferay.ide.eclipse.hook.core.model.IServletFilterMapping;

import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;


public class BeforeAfterFilterNameBinding extends XmlValueBindingImpl
{

	@Override
	public String read()
	{
		String retval = null;
		XmlElement filterElement = null;

		XmlElement xmlElement = xml();
		BeforeAfterFilterType filterType = getFilterType();

		String filterTypeText = filterType.getText();

		filterElement = xmlElement.getChildElement( filterTypeText, false );

		if ( filterElement != null )
		{
			retval = filterElement.getText();
		}

		return retval;
	}

	@Override
	public void write( String value )
	{
		XmlElement xmlElement = xml();
		BeforeAfterFilterType filterType = getFilterType();

		String filterTypeText = filterType.getText();

		XmlElement filterElement = xmlElement.getChildElement( filterTypeText, true );

		filterElement.setText( value );
	}

	private BeforeAfterFilterType getFilterType()
	{
		IServletFilterMapping servletFilterMapping = element().nearest( IServletFilterMapping.class );
		return servletFilterMapping.getBeforeAfterFilterType().getContent( true );
	}

}
