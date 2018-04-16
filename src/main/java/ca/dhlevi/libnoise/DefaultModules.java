package ca.dhlevi.libnoise;

import java.util.ArrayList;
import java.util.List;

import ca.dhlevi.libnoise.Module.QualityMode;
import ca.dhlevi.libnoise.generator.Billow;
import ca.dhlevi.libnoise.generator.Const;
import ca.dhlevi.libnoise.generator.Perlin;
import ca.dhlevi.libnoise.generator.RidgedMultifractal;
import ca.dhlevi.libnoise.generator.Voronoi;
import ca.dhlevi.libnoise.operator.Add;
import ca.dhlevi.libnoise.operator.Blend;
import ca.dhlevi.libnoise.operator.Cache;
import ca.dhlevi.libnoise.operator.Clamp;
import ca.dhlevi.libnoise.operator.ControlPoint;
import ca.dhlevi.libnoise.operator.Curve;
import ca.dhlevi.libnoise.operator.Max;
import ca.dhlevi.libnoise.operator.Multiply;
import ca.dhlevi.libnoise.operator.ScaleBias;
import ca.dhlevi.libnoise.operator.Select;
import ca.dhlevi.libnoise.operator.Terrace;
import ca.dhlevi.libnoise.operator.Turbulence;

public class DefaultModules 
{
	public static Module getContinentNoise(int seed)
    {
    	ScaleBias p1 = new Perlin(4, 2, 0.5, 11, seed + 20, QualityMode.High).scaleBias(0.625, 0.375);
    	
    	List<ControlPoint> controlPoints = new ArrayList<ControlPoint>();
    	controlPoints.add(new ControlPoint(-2,-1.625));
    	controlPoints.add(new ControlPoint(-1,-1.375));
    	controlPoints.add(new ControlPoint(0,-0.375));
    	controlPoints.add(new ControlPoint(0.0625,0.125));
    	controlPoints.add(new ControlPoint(0.125,0.25));
    	controlPoints.add(new ControlPoint(0.25,1));
    	controlPoints.add(new ControlPoint(0.5,0.25));
    	controlPoints.add(new ControlPoint(0.75,0.25));
    	controlPoints.add(new ControlPoint(1,0.5));
    	controlPoints.add(new ControlPoint(2,0.5));
    	
    	Curve p2 = new Perlin(1, 2, 0.5, 14, seed + 21, QualityMode.High).curve(controlPoints);
    	Cache clamped = p1.min(p2).clamp(1, -1).cache();
    	
    	Turbulence distort = clamped.turbulence(15.25, 0.0087912087912088, 13, seed + 22)
					    			.turbulence(47.25, 0.0023054755043228, 12, seed + 23)
					    			.turbulence(95.25, 0.000980632507967639, 11, seed + 24);
    	
    	Cache continentDef = new Select(clamped, distort, clamped, -0.0375, 1000.0375, 0.0625).cache();
    	
    	List<Double> terrControlPoints = new ArrayList<Double>();
    	terrControlPoints.add(-1.0);
    	terrControlPoints.add(-0.75);
    	terrControlPoints.add(-0.375);
    	terrControlPoints.add(1.0);
    	Terrace terr = continentDef.terrace(false, terrControlPoints); // this terrain is a good height point!
    	
    	Cache continentalShelf = new RidgedMultifractal(4.375, 2.2, 16, seed + 130, QualityMode.High)
    								 .scaleBias(-0.125, -0.125)
    								 .add(terr.clamp(0, -0.75))
    								 .cache();
    	
    	Cache baseContinentElev = new Select(continentDef.scaleBias(0.25, 0), continentalShelf, continentDef, -1000.375, 0.03125, 0).cache(); 
    	
    	return terr.cache();
    }
    
    public static Module getSimpleNoise(int seed)
    {
    	return new Select(new Billow(2, 2, 0.5, 6, seed + 10, QualityMode.High).scaleBias(0.125, -0.75), 
    			          new RidgedMultifractal(1, 2, 6, seed, QualityMode.High), 
    			          new Perlin(0.5, 2, 0.25, 6, seed + 20, QualityMode.High), 
    			          0, 1000, 0.125)
    					  .turbulence(4, 0.125, 6, seed + 30)
    					  .cache();
    }
    
    public static Module getDetailedNoise(int seed)
    {
	    List<ControlPoint> baseContinentDef_cuPts = new ArrayList<ControlPoint>();
	    baseContinentDef_cuPts.add(new ControlPoint(-2,-1.625));
	    baseContinentDef_cuPts.add(new ControlPoint(-1,-1.375));
	    baseContinentDef_cuPts.add(new ControlPoint(0,-0.375));
	    baseContinentDef_cuPts.add(new ControlPoint(0.0625,0.125));
	    baseContinentDef_cuPts.add(new ControlPoint(0.125,0.25));
	    baseContinentDef_cuPts.add(new ControlPoint(0.25,1));
	    baseContinentDef_cuPts.add(new ControlPoint(0.5,0.25));
	    baseContinentDef_cuPts.add(new ControlPoint(0.75,0.25));
	    baseContinentDef_cuPts.add(new ControlPoint(1,0.5));
	    baseContinentDef_cuPts.add(new ControlPoint(2,0.5));
	    
	    Curve baseContinentDef_cu = new Perlin(1, 2.208984375, 0.5, 14, seed, QualityMode.Medium)
	    								.curve(baseContinentDef_cuPts);
	    Cache baseContinentDef = new Perlin(4.34375, 2.208984375, 0.5, 11, seed + 1, QualityMode.Medium)
	    		                     .scaleBias(0.625, 0.375)
	    		                     .min(baseContinentDef_cu)
	    		                     .clamp(1, -1)
	    		                     .cache();
	    
	    Turbulence continentDef_tu2 = new Turbulence(baseContinentDef, 15.25, 0.0087912087912088, 13, seed + 10)
	    		                         .turbulence(95.25, 0.000980632507967639, 11, seed + 12)
	    								 .turbulence(47.25, 0.0023054755043228, 12, seed + 11);
	    Cache continentDef = new Select(baseContinentDef, continentDef_tu2, baseContinentDef, -0.0375, 1000.0375, 0.0625).cache();
	    
	    List<Double> terrainTypeDef_tePnts = new ArrayList<Double>();
	    terrainTypeDef_tePnts.add(-1.0);
	    terrainTypeDef_tePnts.add(-0.1875);
	    terrainTypeDef_tePnts.add(1.0);
	    Cache terrainTypeDef = new Turbulence(continentDef, 18.125, 0.0485584218512898, 3, seed + 20)
                                   .terrace(false, terrainTypeDef_tePnts)
                                   .cache();

	    ScaleBias mountainBaseDef_sb0 = new RidgedMultifractal(1723, 2.142578125, 4, seed + 30, QualityMode.Medium)
	    		                            .scaleBias(0.5, 0.375);
	    ScaleBias mountainBaseDef_sb1 = new RidgedMultifractal(367, 2.142578125, 1, seed + 31, QualityMode.High)
	    		                            .scaleBias(-2, -0.5);
	    
	    Cache mountainBaseDef = new Blend(new Const(-1), mountainBaseDef_sb0, mountainBaseDef_sb1)
	    		                             .turbulence(1337, 0.000148588410104012, 4, seed + 32)
	                                         .turbulence(21221, 8.32244480138485E-06, 6, seed + 33)
	                                         .cache();

	    RidgedMultifractal mountainousHigh_rm0 = new RidgedMultifractal(2371, 2.142578125, 3, seed + 40, QualityMode.High);
	    RidgedMultifractal mountainousHigh_rm1 = new RidgedMultifractal(2341, 2.142578125, 3, seed + 41, QualityMode.High);
	    
	    Cache mountainousHigh = new Max(mountainousHigh_rm0, mountainousHigh_rm1)
	    		                    .turbulence(21511, 5.5441284907219E-06, 4, seed + 42)
	    		                    .cache();

	    RidgedMultifractal mountainousLow_rm0 = new RidgedMultifractal(1381, 2.142578125, 8, seed + 50, QualityMode.High);
	    RidgedMultifractal mountainousLow_rm1 = new RidgedMultifractal(1427, 2.142578125, 8, seed + 51, QualityMode.High);
	    Cache mountainousLow = new Multiply(mountainousLow_rm0, mountainousLow_rm1).cache();

	    ScaleBias mountainousTerrain_sb0 = mountainousLow.scaleBias(0.03125, -0.96875);
	    Add mountainousTerrain_ad = mountainousHigh.scaleBias(0.25, 0.25)
	    		                                   .add(mountainBaseDef);
	    Select mountainousTerrain_se = new Select(mountainousTerrain_sb0, mountainousTerrain_ad, mountainBaseDef, -0.5, 999.5, 0.5);
	    
	    Cache mountainousTerrain = mountainousTerrain_se.scaleBias(0.8, 0)
	    		                                        .exponent(1.375)
	    		                                        .cache();

	    ScaleBias hillyTerrain_sb0 = new Billow(1663, 2.162109375, 0.5, 6, seed + 60, QualityMode.High).scaleBias(0.5, 0.5);
	    ScaleBias hillyTerrain_sb1 = new RidgedMultifractal(367.5, 2.162109375, 1, seed + 61, QualityMode.High).scaleBias(-2, -0.5);
	    Blend hillyTerrain_bl = new Blend(new Const(-1), hillyTerrain_sb1, hillyTerrain_sb0);
	    
	    Cache hillyTerrain = hillyTerrain_bl.scaleBias(0.75, -0.25)
	    		                            .exponent(1.375)
	    		                            .turbulence(1531, 5.90981620471603E-05, 4, seed + 62)
	    		                            .turbulence(21617, 8.50853831820232E-06, 6, seed + 63)
	    		                            .cache();

	    ScaleBias plainsTerrain_sb0 = new Billow(1097.5, 2.314453125, 0.5, 8, seed + 70, QualityMode.High).scaleBias(0.5, 0.5);
	    ScaleBias plainsTerrain_sb1 = new Billow(1319.5, 2.314453125, 0.5, 8, seed + 71, QualityMode.High).scaleBias(0.5, 0.5);
	    Cache plainsTerrain = plainsTerrain_sb0.multiply(plainsTerrain_sb1)
	    		                               .scaleBias(2, -1)
	    		                               .cache();

	    ScaleBias badlandsSand_sb1 = new Voronoi(16183.25, 0, seed + 81, true).scaleBias(0.25, 0.25);	    
	    Cache badlandsSand = new RidgedMultifractal(6163.5, 2, 1, seed + 80, QualityMode.High).scaleBias(1, 0)
	    		                                                                              .add(badlandsSand_sb1)
	    		                                                                              .cache();
	    
	    List<ControlPoint> badlandsCliffs_cuPts = new ArrayList<ControlPoint>();
	    badlandsCliffs_cuPts.add(new ControlPoint(-2,-2));
	    badlandsCliffs_cuPts.add(new ControlPoint(-1,-1.25));
	    badlandsCliffs_cuPts.add(new ControlPoint(0,-0.75));
	    badlandsCliffs_cuPts.add(new ControlPoint(0.5,-0.25));
	    badlandsCliffs_cuPts.add(new ControlPoint(0.625,0.875));
	    badlandsCliffs_cuPts.add(new ControlPoint(0.75,1));
	    badlandsCliffs_cuPts.add(new ControlPoint(2,1.25));
	    
	    List<Double> badlandsCliffs_tePts = new ArrayList<Double>();
	    badlandsCliffs_tePts.add(-1.0);
	    badlandsCliffs_tePts.add(-0.875);
	    badlandsCliffs_tePts.add(-0.75);
	    badlandsCliffs_tePts.add(-0.5);
	    badlandsCliffs_tePts.add(0.0);
	    badlandsCliffs_tePts.add(1.0);
	    
	    Cache badlandsCliffs = new Perlin(839, 2.212890625, 0.5, 6, seed + 90, QualityMode.High).curve(badlandsCliffs_cuPts)
	    		                                                                                .clamp(0.875, -999.125)
	    		                                                                                .terrace(false, badlandsCliffs_tePts)
	    		                                                                                .turbulence(16111, 7.06519051286218E-06, 3, seed + 91)
	    		                                                                                .turbulence(36107, 4.72717130796103E-06, 3, seed + 92)
	    		                                                                                .cache();
	    
	    ScaleBias badlandsTerrain_sb = badlandsSand.scaleBias(0.25, -0.75);
	    Cache badlandsTerrain = badlandsCliffs.max(badlandsTerrain_sb).cache();
	    
	    ScaleBias scaledMountainousTerrain_sb0 = mountainousTerrain.scaleBias(0.125, 0.125);
	    ScaleBias scaledMountainousTerrain_sb99 = new Perlin(14.5, 2.142578125, 0.5, 6, seed + 110, QualityMode.High).exponent(1.25)
	    		                                                                                                     .scaleBias(0.25, 1);
	    Cache scaledMountainousTerrain = scaledMountainousTerrain_sb0.multiply(scaledMountainousTerrain_sb99).cache();

	    ScaleBias scaledHillyTerrain_sb0 = hillyTerrain.scaleBias(0.0625, 0.0625);
	    ScaleBias scaledHillyTerrain_sb1 = new Perlin(13.5, 2.162109375, 0.5, 6, seed + 120, QualityMode.High).exponent(1.25).scaleBias(0.5, 1.5);
	    Cache scaledHillyTerrain = scaledHillyTerrain_sb0.multiply(scaledHillyTerrain_sb1).cache();

	    Cache scaledPlainsTerrain = plainsTerrain.scaleBias(0.00390625, 0.0078125).cache();

	    Cache scaledBadlandsTerrain = badlandsTerrain.scaleBias(0.0625, 0.0625).cache();

	    List<Double> continentalShelf_tePts = new ArrayList<Double>();
	    continentalShelf_tePts.add(-1.0);
	    continentalShelf_tePts.add(-0.75);
	    continentalShelf_tePts.add(-0.375);
	    continentalShelf_tePts.add(1.0);
	    
	    Clamp continentalShelf_cl = continentDef.terrace(false, continentalShelf_tePts).clamp(0, -0.75);
	    Cache continentalShelf = new RidgedMultifractal(4.375, 2.208984375, 16, seed + 130, QualityMode.High)
	    							.scaleBias(-0.125, -0.125)
	    							.add(continentalShelf_cl)
	    							.cache();
	    
	    ScaleBias baseContinentElev_sb = continentDef.scaleBias(0.25, 0);
	    Cache baseContinentElev = new Select(baseContinentElev_sb, continentalShelf, continentDef, -1000.375, 0.03125, 0).cache();
	    Cache continentsWithPlains = baseContinentElev.add(scaledPlainsTerrain).cache();
	    
	    List<ControlPoint> riverPositions_cu0Pts = new ArrayList<ControlPoint>();
	    riverPositions_cu0Pts.add(new ControlPoint(-2, 2));
	    riverPositions_cu0Pts.add(new ControlPoint(-1,1));
	    riverPositions_cu0Pts.add(new ControlPoint(-0.125,0.875));
	    riverPositions_cu0Pts.add(new ControlPoint(0,-1));
	    riverPositions_cu0Pts.add(new ControlPoint(1,-1.5));
	    riverPositions_cu0Pts.add(new ControlPoint(2,-2));
	    Curve riverPositions_cu0 = new RidgedMultifractal(18.75, 2.208984375, 1, seed + 100, QualityMode.High).curve(riverPositions_cu0Pts);
	    
	    List<ControlPoint> riverPositions_cu1Pts = new ArrayList<ControlPoint>();
	    riverPositions_cu1Pts.add(new ControlPoint(-2, 2));
	    riverPositions_cu1Pts.add(new ControlPoint(-1,1.5));
	    riverPositions_cu1Pts.add(new ControlPoint(-0.125,0.4375));
	    riverPositions_cu1Pts.add(new ControlPoint(0,0.5));
	    riverPositions_cu1Pts.add(new ControlPoint(1,0.25));
	    riverPositions_cu1Pts.add(new ControlPoint(2,0));
	    Curve riverPositions_cu1 = new RidgedMultifractal(43.25, 2.208984375, 1, seed + 101, QualityMode.High).curve(riverPositions_cu1Pts);
	    
	    Cache riverPoisitions = riverPositions_cu0.min(riverPositions_cu1)
	    		                                  .turbulence(9.25, 0.0173160173160173, 6, seed + 102)
	    		                                  .cache();
	    
	    Add continentsWithHills_ad = baseContinentElev.add(scaledHillyTerrain);
	    Cache continentsWithHills = new Select(continentsWithPlains, continentsWithHills_ad, terrainTypeDef, -1, 1, 0).cache();
	    
	    List<ControlPoint> continentsWithMountains_cuPts = new ArrayList<ControlPoint>();
	    continentsWithMountains_cuPts.add(new ControlPoint(-1, -0.625));
	    continentsWithMountains_cuPts.add(new ControlPoint(0, 0));
	    continentsWithMountains_cuPts.add(new ControlPoint(0.5, 0.0625));
	    continentsWithMountains_cuPts.add(new ControlPoint(1, 0.25));
	    
	    Curve continentsWithMountains_cu = continentDef.curve(continentsWithMountains_cuPts);
	    Add continentsWithMountains_ad1 = baseContinentElev.add(scaledMountainousTerrain).add(continentsWithMountains_cu);
	    Cache continentsWithMountains = new Select(continentsWithHills, continentsWithMountains_ad1, terrainTypeDef, 0.5, 1000.5, 0.25).cache();
	    
	    Perlin continentsWithBadlands_pe = new Perlin(16.5, 2.208984375, 0.5, 2, seed + 140, QualityMode.Medium);
	    Add continentsWithBadlands_ad = baseContinentElev.add(scaledBadlandsTerrain);
	    Select continentsWithBadlands_se = new Select(continentsWithMountains, continentsWithBadlands_ad, continentsWithBadlands_pe, 0.96875, 1000.96875, 0.25);
	    Cache continentsWithBadlands = continentsWithMountains.max(continentsWithBadlands_se).cache();
	    
    	ScaleBias continentsWithRivers_sb = riverPoisitions.scaleBias(0.01171875, -0.01171875);
    	Add continentsWithRivers_ad = continentsWithBadlands.add(continentsWithRivers_sb);
    	Cache continentsWithRivers = new Select(continentsWithBadlands, continentsWithRivers_ad, continentsWithBadlands, 0, 0.25, 0.125).cache();
    	Cache cache = continentsWithRivers.scaleBias(1, 0).cache();
    	
    	return cache;
    }
}
