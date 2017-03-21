package com.liferay.ide.kaleo.ui.editor;

import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gd;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdfill;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdvalign;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdvfill;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.glayout;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.glspacing;

import com.liferay.ide.kaleo.core.model.IScriptable;
import com.liferay.ide.kaleo.core.model.ScriptLanguageType;

import org.eclipse.core.internal.content.ContentTypeManager;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.ui.PropertyEditorPart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.assist.internal.PropertyEditorAssistDecorator;
import org.eclipse.sapphire.ui.renderers.swt.PropertyEditorRenderer;
import org.eclipse.sapphire.ui.renderers.swt.PropertyEditorRendererFactory;
import org.eclipse.sapphire.ui.renderers.swt.ValuePropertyEditorRenderer;
import org.eclipse.sapphire.ui.swt.renderer.SapphireToolBarActionPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.PartPane;
import org.eclipse.ui.internal.PartSite;
import org.eclipse.ui.internal.WorkbenchPartReference;
import org.eclipse.ui.internal.registry.EditorDescriptor;
import org.eclipse.ui.texteditor.ITextEditor;


@SuppressWarnings( "restriction" )
public class CodePropertyEditorRenderer extends ValuePropertyEditorRenderer
{

	class ScriptLanguageModelPropertyListener extends ModelPropertyListener
	{

		@Override
		public void handlePropertyChangedEvent( ModelPropertyChangeEvent event )
		{

		}
	}

	private class ScriptEditorReference extends WorkbenchPartReference implements IEditorReference
	{

		private IEditorInput editorInput;
		private IEditorPart editor;

		public ScriptEditorReference( IEditorPart editor, IEditorInput input )
		{
			super();
			this.editor = editor;
			this.editorInput = input;
		}

		public IWorkbenchPage getPage()
		{
			return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		}

		public String getFactoryId()
		{
			return null;
		}

		public String getName()
		{
			return null;
		}

		public IEditorPart getEditor( boolean restore )
		{
			return editor;
		}

		public IEditorInput getEditorInput() throws PartInitException
		{
			return this.editorInput;
		}

		@Override
		protected IWorkbenchPart createPart()
		{
			return null;
		}

		@Override
		protected PartPane createPane()
		{
			return null;
		}

	}

	private class ScriptEditorSite extends PartSite implements IEditorSite
	{

		public ScriptEditorSite( IWorkbenchPartReference ref, IWorkbenchPart part, IWorkbenchPage page )
		{
			super( ref, part, page );
		}

		@Override
		public Shell getShell()
		{
			return this.getPage().getActivePart().getSite().getShell();
		}

		@Override
		public IActionBars getActionBars()
		{
			IActionBars bars = ( (PartSite) this.getPage().getActivePart().getSite() ).getActionBars();

			return bars;
		}

		public IEditorActionBarContributor getActionBarContributor()
		{
			return null;
		}

		public void registerContextMenu(
			MenuManager menuManager, ISelectionProvider selectionProvider, boolean includeEditorInput )
		{
		}

		public void registerContextMenu(
			String menuId, MenuManager menuManager, ISelectionProvider selectionProvider, boolean includeEditorInput )
		{
		}

	}

	private ITextEditor textEditor;

	public CodePropertyEditorRenderer( SapphireRenderingContext context, PropertyEditorPart part )
	{
		super( context, part );

		IScriptable scriptable = context.getPart().getLocalModelElement().nearest( IScriptable.class );

		ScriptLanguageModelPropertyListener listener = new ScriptLanguageModelPropertyListener();

		scriptable.addListener( listener, "ScriptLanguage" );
	}

	@Override
	protected void createContents( Composite parent )
	{
		final PropertyEditorPart part = getPart();
		final Composite scriptEditorParent = createMainComposite( parent, new CreateMainCompositeDelegate( part )
		{
			@Override
			public boolean canScaleVertically()
			{
				return true;
			}
		} );

		this.context.adapt( scriptEditorParent );

		int textFieldParentColumns = 1;
		final SapphireToolBarActionPresentation toolBarActionsPresentation =
			new SapphireToolBarActionPresentation( getActionPresentationManager() );

		final boolean isActionsToolBarNeeded = toolBarActionsPresentation.hasActions();
		if ( isActionsToolBarNeeded || true )
			textFieldParentColumns++;

		scriptEditorParent.setLayout( glayout( textFieldParentColumns, 0, 0, 0, 0 ) );

		final Composite nestedComposite = new Composite( scriptEditorParent, SWT.NONE );
		nestedComposite.setLayoutData( gdfill() );
		nestedComposite.setLayout( glspacing( glayout( 2, 0, 0 ), 2 ) );
		this.context.adapt( nestedComposite );

		final PropertyEditorAssistDecorator decorator = createDecorator( nestedComposite );

		decorator.control().setLayoutData( gdvalign( gd(), SWT.TOP ) );
		decorator.addEditorControl( nestedComposite );

		// scriptEditorParent.setLayout( new FillLayout( SWT.HORIZONTAL ) );
		// scriptEditorParent.setLayoutData( gdhhint( gdfill(), 300 ) );
        
		final PropertyEditorInput editorInput =
			new PropertyEditorInput( part.getLocalModelElement(), (ValueProperty) part.getProperty() );

		try
		{
			ScriptLanguageType scriptLang =
				context.getPart().getLocalModelElement().nearest( IScriptable.class ).getScriptLanguage().getContent(
					false );
			String fileName =
				scriptLang.getClass().getField( scriptLang.toString() ).getAnnotation( DefaultValue.class ).text();

			IContentDescription contentDescription =
				ContentTypeManager.getInstance().getDescriptionFor(
					editorInput.getStorage().getContents(), fileName, IContentDescription.ALL );
			EditorDescriptor defaultEditor =
				(EditorDescriptor) PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(
					editorInput.getName(), contentDescription.getContentType() );
			this.textEditor = (ITextEditor) defaultEditor.createEditor();
		}
		catch ( Exception e1 )
		{
		}

		IEditorReference ref = new ScriptEditorReference( this.textEditor, editorInput );
		IEditorSite site =
			new ScriptEditorSite(
				ref, this.textEditor, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() );
		try
		{

			this.textEditor.init( site, editorInput );
			this.textEditor.getDocumentProvider().getDocument( editorInput ).addDocumentListener(
				new IDocumentListener()
				{

					public void documentChanged( DocumentEvent event )
					{
						String content = event.getDocument().get();
						part.getLocalModelElement().write( ( (ValueProperty) part.getProperty() ), content );
					}

					public void documentAboutToBeChanged( DocumentEvent event )
					{
					}
				} );

			part.getLocalModelElement().addListener( new ModelPropertyListener()
			{

				@Override
				public void handlePropertyChangedEvent( ModelPropertyChangeEvent event )
				{
					CodePropertyEditorRenderer.this.textEditor.getDocumentProvider().getDocument( editorInput ).set(
						part.getLocalModelElement().read( getProperty() ).getText() );
				}
			}, part.getProperty().getName() );
		}
		catch ( PartInitException e )
		{
			e.printStackTrace();
		}
		
		Control[] prevChildren = scriptEditorParent.getChildren();
		// this.textEditor.createPartControl( scriptEditorParent );
		new Label( scriptEditorParent, SWT.NONE );
		Control[] newChildren = scriptEditorParent.getChildren();

		decorator.addEditorControl( newChildren[prevChildren.length], true );

		if ( isActionsToolBarNeeded )
		{
			final ToolBar toolbar = new ToolBar( scriptEditorParent, SWT.FLAT | SWT.HORIZONTAL );
			toolbar.setLayoutData( gdvfill() );
			toolBarActionsPresentation.setToolBar( toolbar );
			toolBarActionsPresentation.render();
			this.context.adapt( toolbar );
			decorator.addEditorControl( toolbar );
			// relatedControls.add( toolbar );
		}
	}

	@Override
	protected void handlePropertyChangedEvent()
	{
		super.handlePropertyChangedEvent();
	}

	@Override
	protected void handleFocusReceivedEvent()
	{
		super.handleFocusReceivedEvent();
	}

	@Override
	protected boolean canScaleVertically()
	{
		return true;
	}


	public static final class Factory extends PropertyEditorRendererFactory
	{

		@Override
		public boolean isApplicableTo( final PropertyEditorPart propertyEditorDefinition )
		{
			return ( propertyEditorDefinition.getProperty() instanceof ValueProperty );
		}

		@Override
		public PropertyEditorRenderer create( final SapphireRenderingContext context, final PropertyEditorPart part )
		{
			return new CodePropertyEditorRenderer( context, part );
		}
	}

}
