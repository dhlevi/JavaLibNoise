package ca.dhlevi.libnoise;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ca.dhlevi.libnoise.paint.Hillshader;
import ca.dhlevi.libnoise.paint.PainterUtilities;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DEMTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     */
    public DEMTest(String testName)
    {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite(DEMTest.class);
    }

    /**
     * Rigourous Test :-)
     * 
     * @throws Exception
     */
    public void testApp() throws Exception
    {
        assertTrue(generateShadedImage("c:/test/DEM/dem_alps.jpg", "c:/test/DEM/dem_alps_shaded.jpg"));
        assertTrue(generateShadedImage("c:/test/DEM/dem_australia-2048.jpg", "c:/test/DEM/dem_australia-2048_shaded.jpg"));
        assertTrue(generateShadedImage("c:/test/DEM/dem_earth_8k.jpg", "c:/test/DEM/dem_earth_8k_shaded.jpg"));
        assertTrue(generateShadedImage("c:/test/DEM/dem_test.png", "c:/test/DEM/dem_test_shaded.jpg"));
        assertTrue(generateShadedImage("c:/test/DEM/dem_elev_test2.jpg", "c:/test/DEM/dem_elev_test2_shaded.jpg"));
        assertTrue(generateShadedImage("c:/test/DEM/dem_test2.jpg", "c:/test/DEM/dem_test2_shaded.jpg"));
        assertTrue(generateShadedImage("c:/test/DEM/heightmap_west_us.png", "c:/test/DEM/heightmap_west_us.jpg"));
        assertTrue(generateShadedImage("c:/test/DEM/italy-2048.jpg", "c:/test/DEM/italy-2048_shaded.jpg"));
        assertTrue(generateShadedImage("c:/test/DEM/laurel-hill-DEM.jpg", "c:/test/DEM/laurel-hill-DEM_shaded.jpg"));
        assertTrue(generateShadedImage("c:/test/DEM/n27_e085_1arc_v3.jpg", "c:/test/DEM/n27_e085_1arc_v3_shaded.jpg"));
    }
    
    public static boolean generateShadedImage(String sourcePath, String outputPath) throws IOException
    {
        BufferedImage baseDEM = ImageIO.read(new File(sourcePath));
        int width = baseDEM.getWidth();
        int height = baseDEM.getHeight();

        BufferedImage shadedDEM = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) shadedDEM.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        
        double[][] heightData = new double[width][height];
        // create a height data array
        
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                int rgbCode = baseDEM.getRGB(x, y);
                Color c = new Color(rgbCode);                
                heightData[x][y] =  ((double)c.getRed()) / 255;
            }
        }
        
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {                
                double pixelHeight = heightData[x][y];
                // normalize the pixel heights
                if (pixelHeight > 1)
                    pixelHeight = 1;
                if (pixelHeight < 0)
                    pixelHeight = 0;
                
                Color sourceColor = PainterUtilities.gradient(new Color(88, 162, 113, 255), new Color(203, 201, 134, 255), pixelHeight);
                
                if(pixelHeight == 0.0) sourceColor = new Color(176, 190, 199, 255);
                //else if(pixelHeight <= 0.1) // blue band
                //    sourceColor = PainterUtilities.gradient(new Color(176, 190, 199, 255), new Color(176, 190, 255, 255), pixelHeight);
                else if(pixelHeight > 0.0 && pixelHeight <= 0.8) // green band
                    sourceColor = PainterUtilities.gradient(new Color(110, 130, 100, 255), new Color(167, 189, 170, 255), pixelHeight);
                else // red band
                    sourceColor = PainterUtilities.gradient(new Color(167, 189, 170, 255), new Color(210, 207, 194, 255), pixelHeight);

                Color shade = Hillshader.shadePixel(heightData, width, height, x, y, 40.0, 80.0, 315.0, 1.0, 0).getRGB();
                Color shadedColor = PainterUtilities.blend(sourceColor, shade);
                
                shadedDEM.setRGB(x, y, shadedColor.getRGB());
            }
        }
        
        shadedDEM.flush();

        File outputfile = new File(outputPath);
        outputfile.createNewFile();
        return ImageIO.write(shadedDEM, "png", outputfile);
    }
}
