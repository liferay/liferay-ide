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
import org.eclipse.sapphire.modeling.ModelElementListener;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelPath;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.internal.MemoryResource;


public class RelationshipsBinding extends ListBindingImpl {

	protected List<Resource> localResources = new ArrayList<Resource>();
	private RelationshipListener listener;

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

		RelationshipResource resource = new RelationshipResource(getEntity().resource(), rel);
		localResources.add(resource);
		// IModelElement e = resource.binding(IRelationship.PROP_NAME).element();
		// resource.element().addListener(listener);

		return resource;
	}

	@Override
	public void remove(Resource resource) {
		if (localResources.contains(resource)) {
			localResources.remove(resource);
			resource.element().removeListener(listener);
		}
	}

	@Override
	public void init(IModelElement element, ModelProperty property, String[] params) {
		super.init(element, property, params);

		listener = new RelationshipListener();

		localResources.clear();

		MemoryResource memory = new MemoryResource(IRelationship.TYPE);
		memory.init(element);

		element.addListener(new ModelPropertyListener() {

			@Override
			public void handlePropertyChangedEvent(ModelPropertyChangeEvent event) {
				handleRelationshipsChangedEvent(event);
			}
		}, new ModelPath("Relationships"));

		
	}

	protected void handleRelationshipsChangedEvent(ModelPropertyChangeEvent event) {
		// loop through relationships and add necessary columns if they don't exist
		IEntity entity = (IEntity) event.getModelElement();
		for (IRelationship rel : entity.getRelationships()) {
			rel.addListener(listener);
		}
	}

	protected void handleNameChangedEvent(ModelPropertyChangeEvent event) {
		// loop through relationships and add necessary columns if they don't exist

	}

	public class RelationshipListener extends ModelElementListener {

		@Override
		public void propertyChanged(ModelPropertyChangeEvent event) {
			IRelationship rel = (IRelationship) event.getModelElement();
			IEntity e = rel.getName().resolve();
			if (e != null) {
				IEntity entity = RelationshipsBinding.this.getEntity();
				for (IColumn col : e.getColumns()) {
					if (col.isPrimary().getContent()) {
						boolean hasForeignKey = false;
						for (IColumn col2 : entity.getColumns()) {
							if ((!col2.isPrimary().getContent()) &&
								col2.getName().getContent().equals(col.getName().getContent())) {
								hasForeignKey = true;
							}
						}
						if (hasForeignKey) {
							entity.getRelationships().remove(rel);
						}
						else {
							IColumn foreignColumn = entity.getColumns().addNewElement();
							foreignColumn.setName(col.getName().getContent());
							foreignColumn.setType(col.getType().getContent());
						}
					}
				}
			}
		}
	}
}
