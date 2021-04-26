package ca.dhlevi.libnoise.spatial;

import java.util.List;
import ca.dhlevi.libnoise.Point;

public class SpatialUtilities
{
    public static double degreesToRadians(double angle)
    {
        return degreesToRadians() * angle;
    }

    public static double degreesToRadians()
    {
        return Math.PI / 180;
    }
    
    /** spatial utils **/

    // convert an xy to a lat long. Envelope must be in WGS84 Geo
    public static Coordinate pixelsToLatLong(Point pixels, int width, int height, Envelope bbox)
    {
        // get the ranges, but normalize to zero (no negatives)
        double long1 = bbox.getMinX() + 180.0;
        double long2 = bbox.getMaxX() + 180.0;
        double lat1 = bbox.getMinY() + 90.0;
        double lat2 = bbox.getMaxY() + 90.0;

        double longUnits = long2 - long1;
        double latUnits = lat2 - lat1;

        double pixelsPerUnitLong = longUnits / width;
        double pixelsPerUnitLat = latUnits / height;

        double lon = (pixels.getX() * pixelsPerUnitLong) + (long1 - 180.0);
        double lat = (pixels.getY() * pixelsPerUnitLat) + (lat1 - 90.0);

        return new Coordinate(lon, lat);
    }

    public static Point latLongToPixels(Coordinate latLon, int width, int height, Envelope bbox)
    {
        double long1 = bbox.getMinX() + 180.0;
        double long2 = bbox.getMaxX() + 180.0;
        double lat1 = bbox.getMinY() + 90.0;
        double lat2 = bbox.getMaxY() + 90.0;

        double longUnits = long2 - long1;
        double latUnits = lat2 - lat1;

        double pixelsPerUnitLong = width / longUnits;
        double pixelsPerUnitLat = height / latUnits;

        int lon = (int) Math.round((latLon.getX() + 180.0) * pixelsPerUnitLong);
        int lat = (int) Math.round((latLon.getY() +  90.0) * pixelsPerUnitLat);
        // int lon = (int) Math.floor((((long1 - 180) - latLon.getX()) / pixelsPerUnitLong) * -1);
        // int lat = (int) Math.floor((((lat1 - 90) - latLon.getY()) / pixelsPerUnitLat) * -1);

        return new Point(lon, lat);
    }

    // reproject a lon lat to mercator projection X Y    
    public static Coordinate MercatorProjection(int width, int height, Coordinate coord, Envelope bbox)
    {
        double long1 = bbox.getMinX() + 180.0;
        double long2 = bbox.getMaxX() + 180.0;
        double lat1 = bbox.getMinY() + 90.0;
        double lat2 = bbox.getMaxY() + 90.0;

        double longUnits = long2 - long1;
        double latUnits = lat2 - lat1;

        double pixelsPerUnitLong = width / longUnits;
        double pixelsPerUnitLat = height / latUnits;
        
        // get x value
        double x = (coord.getX() + 180.0) * pixelsPerUnitLong;

        // convert from degrees to radians
        double latRad = degreesToRadians(coord.getY());
        
        // get y value
        double mercN = Math.log(Math.tan((Math.PI / 4.0) + (latRad / 2.0)));
        double y = (height / 2.0) - (width * mercN / (2.0 * Math.PI));

        return new Coordinate(x, y);
    }

    // reproject a lon lat to miller cylindrical projection X Y
    public static Coordinate MillerProjection(int width, int height, Coordinate coord, Envelope bbox)
    {
        double lon = degreesToRadians(coord.getX());
        double lat = degreesToRadians(coord.getY());

        double x = (coord.getX() + 180.0) * (width / 360.0);
        double y = ((height / 2.0) - (height / (2.0 * 2.303412543)) * (1.25 * Math.log(Math.tan(0.25 * Math.PI + 0.4 * lat)))) + 34;

        return new Coordinate(x, y);
    }
    
    //crs transformations
    public static Coordinate transformGeoToWebMercator(Coordinate coord)
    {
        double x = coord.getX() * 20037508.34 / 180.0;
        double y = Math.log(Math.tan((90.0 + coord.getY()) * Math.PI / 360.0)) / (Math.PI / 180.0);
        y = y * 20037508.34 / 180.0;
        
        return new Coordinate(x, y);
    }
    
    public static Coordinate transformWebMercatorToGeo(Coordinate coord)
    {
        if (Math.abs(coord.getX()) > 20037508.3427892 || Math.abs(coord.getY()) > 20037508.3427892) return null;

        double semimajorAxis = 6378137.0;  // WGS84 spheriod semimajor axis

        double lat = (1.5707963267948966 - (2.0 * Math.atan(Math.exp((-1.0 * coord.getY()) / semimajorAxis)))) * (180.0 / Math.PI);
        double lon = ((coord.getX() / semimajorAxis) * 57.295779513082323) - ((Math.floor((((coord.getX() / semimajorAxis) * 57.295779513082323) + 180.0) / 360.0)) * 360.0);

        return new Coordinate(lon, lat);
    }
    
    public static final double RADIUS = 6372800;
    public static double haversineDistance(Coordinate coord1, Coordinate coord2)
    {
        return haversineDistance(coord1.getY(), coord1.getX(), coord2.getY(), coord2.getX());
    }
    
    // https://rosettacode.org/wiki/Haversine_formula#Groovy
    // haversine(36.12, -86.67, 33.94, -118.40)
    // result: 2887.25995060711 (kms)
    public static double haversineDistance(double lat1, double lon1, double lat2, double lon2) 
    {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
    
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
 
        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        
        return RADIUS * c; // return distance in metres?
    }    
}
