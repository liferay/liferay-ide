package com.liferay.mobile.sdk.core;

public class MobileAPI
{
    public EntityAPI[] entities = new EntityAPI[0];
    public String context;

    public MobileAPI( String context )
    {
        this.context = context;
    }

    public MobileAPI( String context, EntityAPI[] entities )
    {
        this.context = context;
        this.entities = entities;
    }
}