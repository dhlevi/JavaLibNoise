package ca.dhlevi.libnoise.paint;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;

public class Painter 
{
	public static boolean paintHeightMap(double[][] data, String path) throws IOException
	{
		int width = data.length;
        int height = data[0].length;
        
        BufferedImage mapImage = createMapImageBuffer(width, height);

		for(int x = 0 ; x < width; x++)
    	{
    		for(int y = 0; y < height; y++)
    		{
    		    double noiseValue = data[x][y];
    		    if(noiseValue > 1) noiseValue = 1;
    		    if(noiseValue < 0) noiseValue = 0;
    		    
    		    int rgb = (int) Math.round(255 * noiseValue);

    		    Color c = new Color(rgb, rgb, rgb, 255);		    
    		    
    		    mapImage.setRGB(x, y, c.getRGB());
    		}
    	}
		
		return saveBufferedImage(mapImage, path, "heightmap");
	}
	
	public static boolean paintRegionMap(int[][] data, String path, int seed) throws IOException
	{
		int width = data.length;
        int height = data[0].length;
        
		BufferedImage mapImage = createMapImageBuffer(width, height);

		HashMap<Integer, Color> regionColors = new HashMap<Integer, Color>();
		Random rand = new Random(seed);
		
		for(int x = 0 ; x < width; x++)
    	{
    		for(int y = 0; y < height; y++)
    		{
    		    int region = data[x][y];
    		    Color regionColor = null;

    		    if(region == 0)
		    	{
    		    	regionColor = Color.BLACK;
		    	}
    		    else if(regionColors.containsKey(region))
    		    {
    		    	regionColor = regionColors.get(region);
    		    }
    		    else
    		    {
    		    	boolean uniqueColor = false;
    		    	while(!uniqueColor)
    		    	{
    		    		regionColor = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
    		    		uniqueColor = !regionColors.containsValue(regionColor); 
    		    	}
    		    	
    		    	regionColors.put(region, regionColor);
    		    }
    		    
    		    mapImage.setRGB(x, y, regionColor.getRGB());
    		}
    	}
		
		return saveBufferedImage(mapImage, path, "regions");
	}
	
	public static boolean paintTerrainMap(double[][] data, int[][] rivers, double seaLevel, String path, boolean hillshade, boolean shadeWater) throws IOException
	{
		int width = data.length;
        int height = data[0].length;
        
		BufferedImage mapImage = createMapImageBuffer(width, height);
		
		for(int x = 0 ; x < width; x++)
    	{
    		for(int y = 0; y < height; y++)
    		{
    		    double noiseValue = data[x][y];
    		    if(noiseValue > 1) noiseValue = 1;
    		    if(noiseValue < 0) noiseValue = 0;
    		   
    		    // terrain painter
    		    if(noiseValue < seaLevel || rivers[x][y] == 1)
		    	{
    		    	Color c = new Color(153, 217, 242, 255);
    		    	if(shadeWater)
    		    	{
    		    		Color shade = Hillshader.shadePixel(data, width, height, x, y, 0).getRGB();
    		    		c = PainterUtilities.blend(c, shade);  
    		    	}
    		    	
    		    	mapImage.setRGB(x, y, c.getRGB());
		    	}
    		    else
    		    {
	    		    Color c = PainterUtilities.gradient(new Color(0, 102, 0, 255), new Color(220, 220, 220, 255), noiseValue);
	    		    if(hillshade)
	    		    {
	    		    	Color shade = Hillshader.shadePixel(data, width, height, x, y, 0).getRGB();
	    		    	c = PainterUtilities.blend(c, shade);
	    		    }
	    		    
	    		    mapImage.setRGB(x, y, c.getRGB());
    		    }
    		}
    	}
		
		return saveBufferedImage(mapImage, path, "terrain");
	}
	
	private static BufferedImage createMapImageBuffer(int width, int height)
	{
		BufferedImage mapImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) mapImage.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		
		return mapImage;
	}
	
	private static boolean saveBufferedImage(BufferedImage image, String path, String filename) throws IOException
	{
		image.flush();
		
		if(!path.endsWith(File.separator)) path += File.separator;
		
		File outputfile = new File(path + filename + ".png");
		outputfile.createNewFile();
		boolean success = ImageIO.write(image, "png", outputfile);
		
		return success;
	}
}
