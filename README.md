# JavaLibNoise
A Java implementation of the LibNoise library, with some additional functions added for simulating erosion, pathing rivers, and applying hillshading to the outputs, as well as reprojecting the generated image (if it's a spherical map) to mimic a Mercator or Miller Cylindrical output.

Original libnoise source available at: http://libnoise.sourceforge.net/

You can use the LibNoise library for generating all sorts of useful random noise for a number of interesting applications. My personal favorite is (attempting) the creation of somewhat realish looking terrains.

Running the tester out of the box should produce some lovely images, like so:

Randomly generateed heightmap:
[Heightmap](https://raw.githubusercontent.com/dhlevi/JavaLibNoise/master/ExampleImages/heightmap.png)

And add some style:
[Styled Terrain](https://raw.githubusercontent.com/dhlevi/JavaLibNoise/master/ExampleImages/terrain.png)

If you're just interested in hillshading (for DEM's and the like) and just don't want to use the many other alternatives out there, you can use the code there to produce something like this:

Heightmap:
[Heightmap](https://raw.githubusercontent.com/dhlevi/JavaLibNoise/master/ExampleImages/hillshade_test_hm.png)

Hillshaded Result (with a splash of color)
[Styled Terrain](https://raw.githubusercontent.com/dhlevi/JavaLibNoise/master/ExampleImages/hillshade_test_result.jpg)
