/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.ui.navigator;

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.kaleo.ui.util.KaleoUtil;

import org.json.JSONObject;

/**
 * @author Gregory Amerson
 */
public class WorkflowDefinitionEntry
{
    public static WorkflowDefinitionEntry createFromJSONObject( JSONObject jsonDefinition )
    {
        WorkflowDefinitionEntry node = new WorkflowDefinitionEntry();

        try
        {
            node.setName( jsonDefinition.getString( "name" ) );

            if( jsonDefinition.has( "title" ) )
            {
                node.setTitle( jsonDefinition.getString( "title" ) );
            }

            if( jsonDefinition.has( "titleMap" ) )
            {
                node.setTitleMap( jsonDefinition.getString( "titleMap" ) );
            }
            else if( !empty( node.getTitle() ) )
            {
                node.setTitleMap( KaleoUtil.createJSONTitleMap( node.getTitle() ) );
            }

            node.setVersion( jsonDefinition.getInt( "version" ) );

            if( jsonDefinition.has( "draftVersion" ) )
            {
                node.setDraftVersion( jsonDefinition.getInt( "draftVersion" ) );
            }

            if( jsonDefinition.has( "companyId" ) )
            {
                node.setCompanyId( jsonDefinition.getLong( "companyId" ) );
            }

            if( jsonDefinition.has( "userId" ) )
            {
                node.setUserId( jsonDefinition.getLong( "userId" ) );
            }

            if( jsonDefinition.has( "groupId" ) )
            {
                node.setGroupId( jsonDefinition.getLong( "groupId" ) );
            }

            node.setContent( jsonDefinition.getString( "content" ) );
        }
        catch( Exception e )
        {

        }

        return node;
    }

    @Override
    public boolean equals( Object obj )
    {
        if( obj != null && obj instanceof WorkflowDefinitionEntry )
        {
            WorkflowDefinitionEntry input = (WorkflowDefinitionEntry) obj;
            return this.companyId == input.getCompanyId() &&
                ( this.content == null ? input.getContent() == null : this.content.equals( input.getContent() ) ) &&
                this.draftVersion == input.getDraftVersion() && this.groupId == input.getGroupId() &&
                ( this.location == null ? input.getLocation() == null : this.location.equals( input.getLocation() ) ) &&
                ( this.name == null ? input.getName() == null : this.name.equals( input.getName() ) ) &&
                ( this.parent == null ? input.getParent() == null : this.parent.equals( input.getParent() ) &&
                        ( this.title == null ? input.getTitle() == null : this.title.equals( input.getTitle() ) ) &&
                        ( this.titleCurrentValue == null? input.getTitleCurrentValue() == null : this.titleCurrentValue.equals( input.getTitleCurrentValue() ) ) &&
                        ( this.titleMap == null ? input.getTitleMap() == null : this.titleMap.equals( input.getTitleMap() ) ) &&
                        this.userId == input.getUserId() && this.version == input.getVersion() );
        }

        return false;
    }

    private long companyId = -1;
    private String content;
    private int draftVersion = -1;
    private long groupId = -1;
    private boolean loadingNode;
    private String location;
    private String name;
    private WorkflowDefinitionsFolder parent;
    private String title;
    private String titleCurrentValue;
    private String titleMap;
    private long userId = -1;
    private int version = -1;

    public long getCompanyId()
    {
        return companyId;
    }

    public String getContent()
    {
        return content;
    }

    public int getDraftVersion()
    {
        return draftVersion;
    }

    public long getGroupId()
    {
        return groupId;
    }

    public String getLocation()
    {
        return this.location;
    }

    public String getName()
    {
        return isLoadingNode() ? "Loading kaleo workflows..." : name;
    }

    public WorkflowDefinitionsFolder getParent()
    {
        return this.parent;
    }

    public String getTitle()
    {
        return title;
    }

    public String getTitleCurrentValue()
    {
        return titleCurrentValue;
    }

    public String getTitleMap()
    {
        return titleMap;
    }

    public long getUserId()
    {
        return userId;
    }

    public int getVersion()
    {
        return version;
    }

    public boolean isLoadingNode()
    {
        return loadingNode;
    }

    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }

    public void setContent( String content )
    {
        this.content = content;
    }

    public void setDraftVersion( int draftVersion )
    {
        this.draftVersion = draftVersion;
    }

    public void setGroupId( long groupId )
    {
        this.groupId = groupId;
    }

    public void setLoadingNode( boolean loadingNode )
    {
        this.loadingNode = loadingNode;
    }

    public void setLocation( String loc )
    {
        this.location = loc;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setParent( WorkflowDefinitionsFolder parent )
    {
        this.parent = parent;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public void setTitleCurrentValue( String titleCurrentValue )
    {
        this.titleCurrentValue = titleCurrentValue;
    }

    public void setTitleMap( String title )
    {
        this.titleMap = title;
    }

    public void setUserId( long userId )
    {
        this.userId = userId;
    }

    public void setVersion( int version )
    {
        this.version = version;
    }

}
