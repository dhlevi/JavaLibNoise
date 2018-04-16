package ca.dhlevi.libnoise;

public class NoiseFactory 
{
	 private static int UC_BORDER = 1;

     public static float WORLD_SOUTH = -90.0f;
     public static float WORLD_NORTH = 90.0f;
     public static float WORLD_WEST = -180.0f;
     public static float WORLD_EAST = 180.0f;

     public static double generateSphericalPoint(Module module, double lat, double lon, int scale)
     {
         double r = Math.cos(Utilities.degreesToRadians() * lat);
         return module.getValue(r * Math.cos(Utilities.degreesToRadians() * lon), Math.sin(Utilities.degreesToRadians() * lat), r * Math.sin(Utilities.degreesToRadians() * lon), scale);
     }

     public static double[][] generateSpherical(Module module, int width, int height, double south, double north, double west, double east, boolean isNormalized, int scale) throws Exception
     {
         int ucWidth = width + UC_BORDER * 2;
         int ucHeight = height + UC_BORDER * 2;
         double[][] data = new double[ucWidth][ucHeight];

         if (east <= west || north <= south) throw new Exception("Invalid east/west or north/south combination");
         if (module == null) throw new NullPointerException("Generator is null");

         double loe = east - west;
         double lae = north - south;
         double xd = loe / ((double)(width - UC_BORDER));
         double yd = lae / ((double)(height - UC_BORDER));
         double clo = west;

         for (int x = 0; x < ucWidth; x++)
         {
        	 double cla = south;
        	 for (int y = 0; y < ucHeight; y++)
        	 {
        		 //process x, y, cla, clo
        		 double sample = generateSphericalPoint(module, cla, clo, scale);
                 if (isNormalized) sample = (sample + 1) / 2;

                 data[x][y] = (double)sample;
                 cla += yd;
             }
             clo += xd;
         }

         return data;
     }

     public static double generateCylindricalPoint(Module module, double angle, double height, int scale)
     {
         double x = Math.cos(angle * Utilities.degreesToRadians());
         double y = height;
         double z = Math.sin(angle * Utilities.degreesToRadians());

         return module.getValue(x, y, z, scale);
     }

     public static double[][] generateCylindrical(Module module, int width, int height, double angleMin, double angleMax, double heightMin, double heightMax, boolean isNormalized, int scale) throws Exception
     {
         int ucWidth = width + UC_BORDER * 2;
         int ucHeight = height + UC_BORDER * 2;
         double[][] data = new double[ucWidth][ucHeight];

         if (angleMax <= angleMin || heightMax <= heightMin) throw new Exception("Invalid angle or height parameters");
         if (module == null) throw new NullPointerException("Generator is null");

         double ae = angleMax - angleMin;
         double he = heightMax - heightMin;
         double xd = ae / ((double)(width - UC_BORDER));
         double yd = he / ((double)(height - UC_BORDER));
         double ca = angleMin;

         for (int x = 0; x < ucWidth; x++)
         {
             double ch = heightMin;
             for (int y = 0; y < ucHeight; y++)
             {
                 double sample = generateCylindricalPoint(module, ca, ch, scale);
                 if (isNormalized) sample = (sample + 1) / 2;

                 data[x][y] = sample;
                 
                 ch += yd;
             }
             ca += xd;
         }

         return data;
     }

     public static double generatePlanarPoint(Module module, double x, double y, int scale)
     {
         return module.getValue(x, 0.0, y, scale);
     }

     public static double[][] generatePlanar(Module module, int width, int height, double left, double right, double top, double bottom, boolean isSeamless, boolean isNormalized, int scale) throws Exception
     {
         int ucWidth = width + UC_BORDER * 2;
         int ucHeight = height + UC_BORDER * 2;
         double[][] data = new double[ucWidth][ucHeight];

         if (right <= left || bottom <= top) throw new Exception("Invalid right/left or bottom/top combination");
         if (module == null) throw new NullPointerException("Base Module is null");

         double xe = right - left;
         double ze = bottom - top;
         double xd = xe / ((double)width - UC_BORDER);
         double zd = ze / ((double)height - UC_BORDER);
         double xc = left;

         for (int x = 0; x < ucWidth; x++)
         {
             double zc = top;
             for (int y = 0; y < ucHeight; y++)
             {
                 double fv;

                 if (isSeamless) fv = generatePlanarPoint(module, xc, zc, scale);
                 else
                 {
                     double swv = generatePlanarPoint(module, xc, zc, scale);
                     double sev = generatePlanarPoint(module, xc + xe, zc, scale);
                     double nwv = generatePlanarPoint(module, xc, zc + ze, scale);
                     double nev = generatePlanarPoint(module, xc + xe, zc + ze, scale);

                     double xb = 1.0 - ((xc - left) / xe);
                     double zb = 1.0 - ((zc - top) / ze);
                     
                     double z0 = Utilities.interpolateLinear(swv, sev, xb);
                     double z1 = Utilities.interpolateLinear(nwv, nev, xb);

                     fv = Utilities.interpolateLinear(z0, z1, zb);
                 }

                 if (isNormalized) fv = (fv + 1) / 2;

                 data[x][y] = fv;
                 zc += zd;
             }
             xc += xd;
         }

         return data;
     }
}
