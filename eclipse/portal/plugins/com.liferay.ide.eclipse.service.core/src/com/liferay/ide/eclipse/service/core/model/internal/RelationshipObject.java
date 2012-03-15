package com.liferay.ide.eclipse.service.core.model.internal;



public class RelationshipObject
{
	String name;
	String foreignKeyColumnName;

	public void setName( String content )
	{
		this.name = content;
	}

	public String getName()
	{
		return this.name;
	}

	public void setForeignKeyColumnName( String content )
	{
		this.foreignKeyColumnName = content;
	}

	public String getForeignKeyColumnName()
	{
		return this.foreignKeyColumnName;
	}

}
