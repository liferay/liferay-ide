/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay IDE ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.core.model.internal;

import static com.liferay.ide.core.util.CoreUtil.empty;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Gregory Amerson
 */
public class TransitionMetadataObject
{
    private String name;
    private Point labelPosition = new Point(-1, -1);
    private List<Point> bendpoints = new ArrayList<Point>();

    public TransitionMetadataObject()
    {
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String n)
    {
        this.name = n;
    }

    public Point getLabelPosition()
    {
        return this.labelPosition;
    }

    public void setLabelPosition( Point p )
    {
        this.labelPosition = p;
    }

    @Override
    public boolean equals( Object obj )
    {
        boolean retval = true;

        if (this != obj)
        {
            if (obj instanceof TransitionMetadataObject)
            {
                try
                {
                    if (!(this.toJSONString().equals( ((TransitionMetadataObject)obj).toJSONString())))
                    {
                        retval = false;
                    }
                }
                catch (Exception e)
                {
                    retval = false;
                }
            }
            else
            {
                retval = false;
            }
        }

        return retval;
    }

    public List<Point> getBendpoints()
    {
        return this.bendpoints;
    }

//    private void initialize( String contents )
//    {
//        try
//        {
//            JSONObject json = new JSONObject( contents );
//
//            if (json.has( "name" ))
//            {
//                this.name = json.getString( "name");
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }


    public String toJSONString() throws JSONException
    {
        JSONObject json = new JSONObject();

        if (!empty(this.name))
        {
            JSONArray jsonBendpoints = new JSONArray();

            for (Point point : this.bendpoints)
            {
                JSONArray xy = new JSONArray();
                xy.put( point.getX() );
                xy.put( point.getY() );

                jsonBendpoints.put( xy );
            }

            json.put( this.name, jsonBendpoints);
        }


        return json.toString();
    }

    @Override
    public String toString()
    {
        try
        {
            return toJSONString();
        }
        catch( JSONException e )
        {
            return super.toString();
        }
    }

}
