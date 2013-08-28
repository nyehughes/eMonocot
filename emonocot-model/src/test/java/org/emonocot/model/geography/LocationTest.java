package org.emonocot.model.geography;

import java.util.ArrayList;
import java.util.List;


import org.emonocot.model.constants.Location;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 *
 * @author ben
 *
 */
public class LocationTest {
	
	private static Logger logger = LoggerFactory.getLogger(LocationTest.class);


    /**
     *
     */
    @Test
    public final void testCompareGeography() throws Exception {
        List<Geometry> list = new ArrayList<Geometry>();
        list.add(Location.EUROPE.getEnvelope());
        list.add(Location.AFRICA.getEnvelope());
        list.add(Location.CHINA.getEnvelope());
        list.add(Location.MACARONESIA.getEnvelope());
        list.add(Location.EASTERN_CANADA.getEnvelope());
        list.add(Location.FRA.getEnvelope());
        list.add(Location.ABT.getEnvelope());
        list.add(Location.GRB.getEnvelope());
        list.add(Location.IRE.getEnvelope());
        list.add(Location.ALG.getEnvelope());

        GeometryCollection geometryCollection = new GeometryCollection(
                list.toArray(new Geometry[list.size()]), new GeometryFactory());

        Coordinate[] envelope = geometryCollection.getEnvelope()
                .getCoordinates();
        for (Coordinate c : envelope) {
            logger.debug(Math.round(c.x) + " " + Math.round(c.y));
        }
    }

}
