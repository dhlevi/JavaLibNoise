package ca.dhlevi.libnoise.spatial;

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

        int lon = (int) Math.round((latLon.getX() + (long1 - 180.0)) * pixelsPerUnitLong);
        int lat = (int) Math.round((latLon.getY() + (lat1 - 90.0)) * pixelsPerUnitLat);

        return new Point(lon, lat);
    }

    // reproject a lon lat to mercator projection X Y
    public static Coordinate MercatorProjection(int width, int height, Coordinate coord, Envelope bbox)
    {
        double latitude = coord.getY(); // (φ)
        double longitude = coord.getX(); // (λ)

        // get x value
        double x = (longitude + 180.0) * (width / 360.0);

        // convert from degrees to radians
        double latRad = latitude * Math.PI / 180.0;

        // get y value
        double mercN = Math.log(Math.tan((Math.PI / 4.0) + (latRad / 2.0)));
        double y = (height / 2.0) - (width * mercN / (2.0 * Math.PI));

        return new Coordinate(x, y);
    }

    // reproject a lon lat to miller cylindrical projection X Y
    public static Coordinate MillerProjection(int width, int height, Coordinate coord, Envelope bbox)
    {
        double x;
        double y;

        // double lon = degreesToRadians(coord.X);
        double lat = degreesToRadians(coord.getY());

        x = (coord.getX() + 180.0) * (width / 360.0);

        y = 1.25 * Math.log(Math.tan(0.25 * Math.PI + 0.4 * lat));
        y = (height / 2) - (height / (2 * 2.303412543)) * y;

        y += 34;

        return new Coordinate(x, y);
    }
}
