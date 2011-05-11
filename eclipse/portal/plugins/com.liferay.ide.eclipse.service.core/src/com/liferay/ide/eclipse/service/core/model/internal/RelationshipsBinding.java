package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.IColumn;
import com.liferay.ide.eclipse.service.core.model.IEntity;
import com.liferay.ide.eclipse.service.core.model.IRelationship;
import com.liferay.ide.eclipse.service.core.model.IServiceBuilder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListBindingImpl;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelPath;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.internal.MemoryResource;


public class RelationshipsBinding extends ListBindingImpl {

	protected List<Resource> localResources = new ArrayList<Resource>();

	@Override
	public List<Resource> read() {
		List<Resource> resources = new ArrayList<Resource>();

		IEntity thisEntity = getEntity();
		IServiceBuilder root = (IServiceBuilder) this.element().root();

		List<IColumn> primaryColumns = new ArrayList<IColumn>();

		for (IEntity entity : root.getEntities()) {
			if (!(entity.equals(thisEntity))) {
				for (IColumn column : entity.getColumns()) {
					if (column.isPrimary().getContent()) {
						primaryColumns.add(column);
					}
				}
			}
		}

		for (IColumn column : getColumns()) {
			if (!(column.isPrimary().getContent())) {
				for (IColumn primaryColumn : primaryColumns) {
					try {
						if (primaryColumn.getName().getContent().equals(column.getName().getContent()) &&
							primaryColumn.getType().getContent().equals(column.getType().getContent())) {

							IRelationship rel = IRelationship.TYPE.instantiate();
							rel.setName(((IEntity) primaryColumn.parent().parent()).getName().getContent());

							Resource resource = new RelationshipResource(thisEntity.resource(), rel);
							resources.add(resource);
						}
					}
					catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		}

		resources.addAll(localResources);

		return resources;
	}

	protected ModelElementList<IColumn> getColumns() {
		return getEntity().getColumns();
	}

	protected IEntity getEntity() {
		return (IEntity) this.element();
	}

	@Override
	public ModelElementType type(Resource resource) {
		if (resource instanceof RelationshipResource) {
			return IRelationship.TYPE;
		}

		return null;
	}

	@Override
	public Resource add(ModelElementType type) {
		IRelationship rel = IRelationship.TYPE.instantiate();

		Resource resource = new RelationshipResource(getEntity().resource(), rel);
		localResources.add(resource);

		return resource;
	}

	@Override
	public void remove(Resource resource) {
		if (localResources.contains(resource)) {
			localResources.remove(resource);
		}

	}

	@Override
	public void init(IModelElement element, ModelProperty property, String[] params) {
		super.init(element, property, params);

		localResources.clear();

		MemoryResource memory = new MemoryResource(IRelationship.TYPE);
		memory.init(element);

		element.addListener(new ModelPropertyListener() {

			@Override
			public void handlePropertyChangedEvent(ModelPropertyChangeEvent event) {
				handleColumnsChangedEvent(event);
			}
		}, new ModelPath("Columns"));

		for (IColumn column : getColumns()) {
			column.addListener(new ModelPropertyListener() {

				@Override
				public void handlePropertyChangedEvent(ModelPropertyChangeEvent event) {

				}
			}, "Name");
		}
	}

	protected void handleColumnsChangedEvent(ModelPropertyChangeEvent event) {
		((IEntity) event.getModelElement()).getColumns();
	}
}
