/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author kamesh.sampath
 */
@GenerateImpl
public interface ISecurityRoleRef extends IModelElement, IIdentifiable, IDescribeable {

	ModelElementType TYPE = new ModelElementType( ISecurityRoleRef.class );

	// *** RoleName ***

	@Label( standard = "Role Name" )
	@Required
	@NoDuplicates
	@Whitespace( trim = true )
	@XmlBinding( path = "role-name" )
	ValueProperty PROP_ROLE_NAME = new ValueProperty( TYPE, "RoleName" );

	Value<String> getRoleName();

	void setRoleName( String value );

	// *** RoleLink ***

	@Label( standard = "Role Link" )
	@NoDuplicates
	@XmlBinding( path = "role-link" )
	@Whitespace( trim = true )
	ValueProperty PROP_ROLE_LINK = new ValueProperty( TYPE, "RoleLink" );

	Value<String> getRoleLink();

	void setRoleLink( String value );

}
