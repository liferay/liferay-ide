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
import static com.liferay.ide.kaleo.core.util.KaleoModelUtil.DEFAULT_POINT;

import com.liferay.ide.kaleo.core.KaleoCore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class WorkflowNodeMetadataObject
{
    private boolean terminal = false;
    private Point nodeLocation = new Point();
    private List<TransitionMetadataObject> transitionsMetadata = new ArrayList<TransitionMetadataObject>();

    public WorkflowNodeMetadataObject( String contents )
    {
        if (!empty( contents ))
        {
            initialize(contents);
        }
    }

    public WorkflowNodeMetadataObject()
    {
    }

    @Override
    public boolean equals( Object obj )
    {
        boolean retval = true;

        if (this != obj)
        {
            if (obj instanceof WorkflowNodeMetadataObject)
            {
                WorkflowNodeMetadataObject object = (WorkflowNodeMetadataObject) obj;

                if (this.terminal == object.terminal && this.nodeLocation.equals( object.getNodeLocation() ))
                {
                    for (int i = 0; i < this.transitionsMetadata.size(); i++)
                    {
                        if (!(this.transitionsMetadata.get( i ).equals( object.transitionsMetadata.get( i ) )))
                        {
                            retval = false;
                        }
                    }
                }
                else
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

    public Point getNodeLocation()
    {
        return this.nodeLocation;
    }

    public List<TransitionMetadataObject> getTransitionsMetadata()
    {
        return this.transitionsMetadata;
    }

    @SuppressWarnings( "rawtypes" )
    private void initialize( String contents )
    {
        try
        {
            JSONObject json = new JSONObject( contents );

            if (json.has( "terminal" ))
            {
                this.terminal = json.getBoolean( "terminal" );
            }

            this.nodeLocation = jsonPointToPoint( json );

            if (json.has( "transitions" ))
            {
                JSONObject jsonTransitions = json.getJSONObject( "transitions" );

                Iterator transitionNames = jsonTransitions.keys();

                while(transitionNames.hasNext())
                {
                    String name = transitionNames.next().toString();

                    if( jsonTransitions.has( name ) )
                    {
                        JSONObject jsonTransitionMetadata = jsonTransitions.getJSONObject( name );

                        TransitionMetadataObject transitionMetaObject = new TransitionMetadataObject();

                        transitionMetaObject.setName(name);

                        if( jsonTransitionMetadata.has( "xy" ) )
                        {
                            JSONArray jsonLabelPosition = jsonTransitionMetadata.getJSONArray( "xy" );
                            transitionMetaObject.setLabelPosition( jsonArrayToPoint( jsonLabelPosition ) );
                        }

                        if( jsonTransitionMetadata.has( "bendpoints" ) )
                        {
                            JSONArray jsonBendpoints = jsonTransitionMetadata.getJSONArray( "bendpoints" );

                            for( int i = 0; i < jsonBendpoints.length(); i++ )
                            {
                                JSONArray xy = jsonBendpoints.optJSONArray( i );

                                if( xy != null )
                                {
                                    transitionMetaObject.getBendpoints().add( jsonArrayToPoint( xy ) );
                                }
                            }
                        }

                        this.transitionsMetadata.add( transitionMetaObject );
                    }
                }
            }
        }
        catch (Exception e)
        {
            KaleoCore.logError( "Error loading node metadata object", e );
        }
    }

    private JSONArray pointToJSONPoint(Point point)
    {
        JSONArray jsonXY = new JSONArray();
        jsonXY.put( point.getX() );
        jsonXY.put( point.getY() );
        return jsonXY;
    }

    private Point jsonArrayToPoint( JSONArray jsonArray )
    {
        Point point = DEFAULT_POINT;

        try
        {
            if( jsonArray.length() == 2 && ! jsonArray.isNull( 0 ) && ! jsonArray.isNull( 1 ) )
            {
                point = new Point( jsonArray.getInt( 0 ), jsonArray.getInt( 1 ) );
            }
        }
        catch( JSONException ex )
        {
            KaleoCore.logError( "Invalid JSON syntax", ex );
        }

        return point;
    }

    private Point jsonPointToPoint( JSONObject jsonPoint ) throws JSONException
    {
        if( jsonPoint.has( "xy" ) )
        {
            JSONArray jsonXY = jsonPoint.getJSONArray( "xy" );
            return jsonArrayToPoint( jsonXY );
        }
        else
        {
            return DEFAULT_POINT;
        }
    }

    public String toJSONString() throws JSONException
    {
        JSONObject json = new JSONObject();

        if (this.isTerminal())
        {
            json.put( "terminal", true );
        }

        if (this.nodeLocation != null)
        {
            JSONArray jsonXY = pointToJSONPoint( this.nodeLocation );

            json.put( "xy", jsonXY );
        }

        if (this.transitionsMetadata.size() > 0)
        {
            JSONObject jsonTransitions = new JSONObject();

            for (TransitionMetadataObject transitionMetadata : this.transitionsMetadata)
            {
                String transitionName = transitionMetadata.getName();

                if (!empty(transitionName))
                {
                    JSONObject jsonTransitionMetadata = new JSONObject();

                    JSONArray jsonBendpoints = new JSONArray();

                    for (Point bendpoint : transitionMetadata.getBendpoints())
                    {
                        JSONArray xy = pointToJSONPoint( bendpoint );

                        jsonBendpoints.put( xy );
                    }

                    Point labelPosition = transitionMetadata.getLabelPosition();

                    if( !labelPosition.equals( DEFAULT_POINT ) )
                    {
                        JSONArray xy = pointToJSONPoint( labelPosition );

                        jsonTransitionMetadata.put( "xy", xy );
                    }

                    jsonTransitionMetadata.put( "bendpoints", jsonBendpoints );

                    jsonTransitions.put( transitionName, jsonTransitionMetadata );
                }
            }

            json.put( "transitions", jsonTransitions );
        }

        return json.toString();
    }


    public boolean isTerminal()
    {
        return terminal;
    }


    public void setTerminal( boolean terminal )
    {
        this.terminal = terminal;
    }


    public void setNodeLocation( Point nodeLocation )
    {
        this.nodeLocation = nodeLocation;
    }

}
