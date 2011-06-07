package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.IRelationship;

import org.eclipse.sapphire.modeling.BindingImpl;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.ValueBindingImpl;


public class RelationshipResource extends Resource {

	protected IRelationship base;

	public RelationshipResource(final Resource parent, final IRelationship base) {
		super(parent);
		this.base = base;
	}

	public RelationshipResource(final Resource parent) {
		super(parent);
	}

	@Override
	protected BindingImpl createBinding(ModelProperty property) {
		if (property == IRelationship.PROP_NAME) {
			ValueBindingImpl binding = new ValueBindingImpl() {

				@Override
				public String read() {
					return getBase().getName().getText(false);
				}

				@Override
				public void write(final String value) {
					getBase().setName(value);
				}
			};

			binding.init(element(), IRelationship.PROP_NAME, null);

			return binding;
		}
		else if ( property == IRelationship.PROP_FOREIGN_KEY_COLUMN_NAME ) {
			ValueBindingImpl binding = new ValueBindingImpl() {

				@Override
				public String read() {
					return getBase().getForeignKeyColumnName().getText( false );
				}

				@Override
				public void write( final String value ) {
					getBase().setForeignKeyColumnName( value );
				}
			};

			binding.init( element(), IRelationship.PROP_NAME, null );

			return binding;
		}

		return null;
	}

	public void setBase(IRelationship b) {
		this.base = b;
	}

	public IRelationship getBase() {
		return this.base;
	}

}
