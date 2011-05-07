package com.liferay.ide.eclipse.taglib.ui.model.internal;

import com.liferay.ide.eclipse.taglib.ui.model.IAttribute;
import com.liferay.ide.eclipse.taglib.ui.model.ITag;
import org.eclipse.sapphire.modeling.DerivedValueService;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ListBindingImpl;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.Value;

public final class Tag
    
    extends ModelElement
    implements ITag
    
{
    private ModelElementList<IAttribute> events;
    private Value<String> name;
    private ModelElementList<IAttribute> otherAttributes;
    private Value<String> prefix;
    private Value<String> preview;
    private ModelElementList<IAttribute> requiredAttributes;
    private Value<String> source;
    
    public Tag( final IModelParticle parent, final ModelProperty parentProperty, final Resource resource )
    {
        super( TYPE, parent, parentProperty, resource );
    }
    
    public Tag( final Resource resource )
    {
        super( TYPE, null, null, resource );
    }
    
    public ModelElementList<IAttribute> getEvents()
    {
        synchronized( root() )
        {
            if( this.events == null )
            {
                refresh( PROP_EVENTS, true );
            }
            
            return this.events;
        }
    }
    
    public Value<String> getName()
    {
        synchronized( root() )
        {
            if( this.name == null )
            {
                refresh( PROP_NAME, true );
            }
            
            return this.name;
        }
    }
    
    public ModelElementList<IAttribute> getOtherAttributes()
    {
        synchronized( root() )
        {
            if( this.otherAttributes == null )
            {
                refresh( PROP_OTHER_ATTRIBUTES, true );
            }
            
            return this.otherAttributes;
        }
    }
    
    public Value<String> getPrefix()
    {
        synchronized( root() )
        {
            if( this.prefix == null )
            {
                refresh( PROP_PREFIX, true );
            }
            
            return this.prefix;
        }
    }
    
    public Value<String> getPreview()
    {
        synchronized( root() )
        {
            if( this.preview == null )
            {
                refresh( PROP_PREVIEW, true );
            }
            
            return this.preview;
        }
    }
    
    public ModelElementList<IAttribute> getRequiredAttributes()
    {
        synchronized( root() )
        {
            if( this.requiredAttributes == null )
            {
                refresh( PROP_REQUIRED_ATTRIBUTES, true );
            }
            
            return this.requiredAttributes;
        }
    }
    
    public Value<String> getSource()
    {
        synchronized( root() )
        {
            if( this.source == null )
            {
                refresh( PROP_SOURCE, true );
            }
            
            return this.source;
        }
    }
    
    protected void refreshProperty( ModelProperty property, final boolean force )
    {
        synchronized( root() )
        {
            property = property.refine( this );
            
            if( property == PROP_EVENTS )
            {
                if( this.events == null )
                {
                    if( force == true )
                    {
                        this.events = new ModelElementList<IAttribute>( this, PROP_EVENTS );
                        final ListBindingImpl binding = resource().binding( PROP_EVENTS );
                        this.events.init( binding );
                        refreshPropertyEnabledStatus( PROP_EVENTS );
                    }
                }
                else
                {
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_EVENTS );
                    final boolean notified = this.events.refresh();
                    
                    if( ! notified && propertyEnabledStatusChanged )
                    {
                        notifyPropertyChangeListeners( PROP_EVENTS );
                    }
                }
            }
            else if( property == PROP_NAME )
            {
                if( this.name != null || force == true )
                {
                    final Value<String> oldValue = this.name;
                    
                    final String val = resource().binding( PROP_NAME ).read();
                    
                    this.name = new Value<String>( this, PROP_NAME, PROP_NAME.encodeKeywords( val ) );
                    this.name.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_NAME );
                    
                    if( oldValue != null )
                    {
                        if( this.name.equals( oldValue ) )
                        {
                            this.name = oldValue;
                        }
                        
                        if( this.name != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_NAME );
                        }
                    }
                }
            }
            else if( property == PROP_OTHER_ATTRIBUTES )
            {
                if( this.otherAttributes == null )
                {
                    if( force == true )
                    {
                        this.otherAttributes = new ModelElementList<IAttribute>( this, PROP_OTHER_ATTRIBUTES );
                        final ListBindingImpl binding = resource().binding( PROP_OTHER_ATTRIBUTES );
                        this.otherAttributes.init( binding );
                        refreshPropertyEnabledStatus( PROP_OTHER_ATTRIBUTES );
                    }
                }
                else
                {
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_OTHER_ATTRIBUTES );
                    final boolean notified = this.otherAttributes.refresh();
                    
                    if( ! notified && propertyEnabledStatusChanged )
                    {
                        notifyPropertyChangeListeners( PROP_OTHER_ATTRIBUTES );
                    }
                }
            }
            else if( property == PROP_PREFIX )
            {
                if( this.prefix != null || force == true )
                {
                    final Value<String> oldValue = this.prefix;
                    
                    final String val = resource().binding( PROP_PREFIX ).read();
                    
                    this.prefix = new Value<String>( this, PROP_PREFIX, PROP_PREFIX.encodeKeywords( val ) );
                    this.prefix.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_PREFIX );
                    
                    if( oldValue != null )
                    {
                        if( this.prefix.equals( oldValue ) )
                        {
                            this.prefix = oldValue;
                        }
                        
                        if( this.prefix != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_PREFIX );
                        }
                    }
                }
            }
            else if( property == PROP_PREVIEW )
            {
                if( this.preview != null || force == true )
                {
                    final Value<String> oldValue = this.preview;
                    
                    final String val = service( PROP_PREVIEW, DerivedValueService.class ).getDerivedValue();
                    
                    this.preview = new Value<String>( this, PROP_PREVIEW, PROP_PREVIEW.encodeKeywords( val ) );
                    this.preview.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_PREVIEW );
                    
                    if( oldValue != null )
                    {
                        if( this.preview.equals( oldValue ) )
                        {
                            this.preview = oldValue;
                        }
                        
                        if( this.preview != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_PREVIEW );
                        }
                    }
                }
            }
            else if( property == PROP_REQUIRED_ATTRIBUTES )
            {
                if( this.requiredAttributes == null )
                {
                    if( force == true )
                    {
                        this.requiredAttributes = new ModelElementList<IAttribute>( this, PROP_REQUIRED_ATTRIBUTES );
                        final ListBindingImpl binding = resource().binding( PROP_REQUIRED_ATTRIBUTES );
                        this.requiredAttributes.init( binding );
                        refreshPropertyEnabledStatus( PROP_REQUIRED_ATTRIBUTES );
                    }
                }
                else
                {
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_REQUIRED_ATTRIBUTES );
                    final boolean notified = this.requiredAttributes.refresh();
                    
                    if( ! notified && propertyEnabledStatusChanged )
                    {
                        notifyPropertyChangeListeners( PROP_REQUIRED_ATTRIBUTES );
                    }
                }
            }
            else if( property == PROP_SOURCE )
            {
                if( this.source != null || force == true )
                {
                    final Value<String> oldValue = this.source;
                    
                    final String val = service( PROP_SOURCE, DerivedValueService.class ).getDerivedValue();
                    
                    this.source = new Value<String>( this, PROP_SOURCE, PROP_SOURCE.encodeKeywords( val ) );
                    this.source.init();
                    
                    final boolean propertyEnabledStatusChanged = refreshPropertyEnabledStatus( PROP_SOURCE );
                    
                    if( oldValue != null )
                    {
                        if( this.source.equals( oldValue ) )
                        {
                            this.source = oldValue;
                        }
                        
                        if( this.source != oldValue || propertyEnabledStatusChanged )
                        {
                            notifyPropertyChangeListeners( PROP_SOURCE );
                        }
                    }
                }
            }
        }
    }
    
    public Object read( ModelProperty property )
    {
        property = property.refine( this );
        
        if( property == PROP_EVENTS )
        {
            return getEvents();
        }
        else if( property == PROP_NAME )
        {
            return getName();
        }
        else if( property == PROP_OTHER_ATTRIBUTES )
        {
            return getOtherAttributes();
        }
        else if( property == PROP_PREFIX )
        {
            return getPrefix();
        }
        else if( property == PROP_PREVIEW )
        {
            return getPreview();
        }
        else if( property == PROP_REQUIRED_ATTRIBUTES )
        {
            return getRequiredAttributes();
        }
        else if( property == PROP_SOURCE )
        {
            return getSource();
        }
        
        return super.read( property );
    }
    
}
