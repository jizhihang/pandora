# Introduction #
Pandora is an open source computer vision library written in Java and is released under the Apache License 2.0. Source code and other utilities are included in this repository. This document contains only a short brief summary of the project structure as also some tutorials in how to build and use this software. For more up to date information about the project, changelog and issues, please check the links below.

* [Pandora in Action](http://blog-tz3ik.rhcloud.com/shutter/)
* [Repository](https://github.com/tzeikob/pandora)
* [Bug Reports](https://github.com/tzeikob/pandora/issues)
* [Contributors](https://github.com/tzeikob/pandora/graphs/contributors)

Pandora as a computer vision library can be used in any project focusing on information retrieval and especially on content based image retrieval and visual content similarity, in which you have to resolve problems like search for similar images given a query image and a dataset of images. It provides a set of various features like,

* image feature extraction,
* random permutation sampling,
* clustering single and multiple visual vocabularies,
* fixe-size vector aggregation on local descriptors,
* dimensionality reduction,
* projection space analysis,
* batch processing on big image datasets,

which are implemented using various state of the art methods or as modified versions of other open source libraries like [BooCV](https://github.com/lessthanoptimal/BoofCV), [LIRE](https://github.com/dermotte/lire) and [OpenIMAJ](https://github.com/openimaj/openimaj). In image feature extraction it provides various global feature detctors like, CEDD, Scalable Color, Edge Histogram, Tamura, Color Histogram, HOG, PHOG as well as some local like SURF, Color SURF, SIFT, Dense SIFT, Fast SIFT, Gaussian SIFT, Grid SIFT. In the region of vector aggregation of local desrcriptors per image it provides various methods like [BOW](http://link.springer.com/chapter/10.1007%2F978-3-642-33709-3_55), [VLAD](http://ieeexplore.ieee.org/document/6104058/) and [VLAT](http://ieeexplore.ieee.org/document/6467387) for both single and multiple visual vocabularies using k-means clustering. It approaches the dimensionality reduction problem using principal component analysis (PCA) projection to the most dominant eigenvectors. This software can be used in a batch mode on big datasets of given images in order to do a complete image analysis regarding the purpose of your project.

# Building from Source #
## Prerequisities ##

In order to build this project you need the following software pre installed in your system,

* Apache Maven 3+
* Java JDK 7+
* Git

you need also to install the LIRE dependency, which hasn't any public maven repository, so you have to clone and install it in your local maven repository as well,
```
#!linux
git clone git@github.com:dermotte/LIRE.git
cd LIRE/
mvn clean install
```

## Build as an Executable ##
Pandora currently does not offering any option to download binaries, so in case you want to used it as an executable you have to clone and build it in your system.

```
#!linux
git clone git@github.com:tzeikob/pandora.git
cd pandora/
mvn clean package -P exec
```

in the `target/` forlder you will find the `pandora-<version>.jar` file as well as two folders, the `config/` containing the configuration files and the `lib/` as the classpath containing all the external libraries the library depends on. Please read below, in how you can execute this using the command line terminal.

## Build as a Library ##
Pandora currently does not offering any public maven repository, so in order to use it as an external dependency in another project you have to clone and install it in your local maven repository,

```
#!linux
git clone git@github.com:tzeikob/pandora.git
cd pandora/
mvn clean install -P lib
```

for now on you can add it as dependency into other projects like so,

```
#!maven
<dependency>
 <groupId>com.tkb.lib</groupId>
 <artifactId>pandora</artifactId>
 <version>${version}</version>
 <classifier>lib</classifier>
</dependency>
```

in the case you want to add pandora library as binary file in the classpath of your project instead as a maven dependency, you will find in the `target/` folder the `pandora-<version>-lib.jar` binary file, just copy and paste it in the classpath of your project.

# How to Use #
Pandora can be used in two possible ways, as an external dependency to another project or in command line as an executable software in order to extract image features in batch mode given a big dataset of images, as well as for other operations mentioned before like sampling, aggregation etc.

## Extracting SURF descriptors given a dataset ##
The purpose of this tutorial is to extract local descriptors per image from a given dataset of images, using the SURF feature detector wrapper of the BoofCV library. Assuming you've build the project as an executable (see previous section), please follow the instructions below in order to complete the task.

### Configuration Properties ###
First you need to open the extractor's config file `config/extractor.properties` and set properties like the path to the folder containing the image files of the dataset, the path in to which you want to save the local descriptors and the absolute class path of the feature detector used to extract the descriptors. Most of the properties are pretty self explanatory.

```
#!properties
dataset.images.file.path=/path/to/dataset/image/files
descriptions.output.file.path=/path/to/local/descriptors
detector.class.path=com.tkb.pandora.image.boofcv.Surf
```

the absolute class path of the feature detector defined above is actually a `value` matching the `key` of another property in the `detectors` section in the file (read below the list of the available detectors), which contains the configuration parameters of that specific detector, so in case of the SURF you can find that property and modify some parameters in order to tune it up. The notation used in the value of that properties here is the JSON syntax just to make things more readable.

```
#!properties
com.tkb.pandora.image.boofcv.Surf={"radius": 2, ...}
com.tkb.pandora.image.boofcv.ColorSurf={...}
...
com.tkb.pandora.image.lire.TamuraHistogram={...}
```

in the case you want to use another detector you only have to check all the available detectors found in the `detectors` section of the file, choose the detector best suits your needs and copy it's `key` to the property of the detector class path property `detector.class.path`, like so,

```
#!properties
detector.class.path=com.tkb.pandora.image.openimaj.Hog
com.tkb.pandora.image.openimaj.Hog={ "widthBlocks": 5, ...}
```

### Run the extraction task ###
After you finished with the configuration you can now run the extraction task just by running the following command in the terminal window,

```
#!linux
java -Xmx1024m -jar pandora-<version>.jar extract config/extractor.properties
```

### Monitor the Progress ###
In case you want to monitor the progress of the extraction task, you wiil find a log file within the folder where the descriptors will be extracted, so do a tail,

```
#!linux
tail -f -n 100 /path/to/the/log/file
```

## Extracting Tamura descriptors in your project ##
The purposes of this tutorial is to use pandora as an external library in your project in order to extract the Tamura Histogram of a given image. Assuming you have build and install pandora into your local maven repository (see previous section).

First you have to add the maven dependency of the pandora library into the `pom.xml` file of your project, like so,

```
#!maven
<dependency>
 <groupId>com.tkb.lib</groupId>
 <artifactId>pandora</artifactId>
 <version>${version}</version>
 <classifier>lib</classifier>
</dependency>
```

be aware to set the correct value in the `version` tag, then you can add code in order to extract descriptors,

```
#!java
...
BufferedImage image = UtilImageIO.loadImage("/path/to/the/image");
FeatureDetector detector = new TamuraHistogram(true);
Description description = detector.extract(image);
double[][] descriptors = description.getDescriptors();
...
```

# Build a Lite Version #
As written before adding the pandora library as an external dependency into your project will result in the situation, getting a classpath full of the dependencies the pandora project depends on, so you'll end up with a classpath containing many transitive binary files the most of them you don't need. Regarding that the resources in the enviroment an application is running are very limited, you need to eliminate somehow those unwanted transitive dependencies to be excluded from you classpath, without losing any functionality.

The most of these dependencies are linked to the three external libraries mentioned before the BoofCV, LIRE and OpenIMAJ. Excluding dependencies related to these libraries is a tricky process, you need first to make sure which detector belongs to to which external library. Below you can find a short reference table,

List of Detectors |
-|
**BoofCV** |
com.tkb.pandora.image.boofcv.Surf |
com.tkb.pandora.image.boofcv.ColorSurf |
com.tkb.pandora.image.boofcv.Sift |
**LIRE** |
com.tkb.pandora.image.lire.Cedd |
com.tkb.pandora.image.lire.ColorScale |
com.tkb.pandora.image.lire.Edge |
com.tkb.pandora.image.lire.TamuraHistogram |
**OpenIMAJ** |
com.tkb.pandora.image.openimaj.ColorHistogram |
com.tkb.pandora.image.openimaj.DenseSift |
com.tkb.pandora.image.openimaj.FastSift |
com.tkb.pandora.image.openimaj.GaussianSift |
com.tkb.pandora.image.openimaj.GridSift |
com.tkb.pandora.image.openimaj.Hog |
com.tkb.pandora.image.openimaj.Phog |

So let say you only use the SURF detector in your code, then having the reference table above you can exclude the the dependencies of the LIRE and OpenIMAJ libraries but the BoofCV. In order to do this add `exclusion` elements into the dependency element of the pandora library into the `pom.xml` file of your project, like so,

```
#!maven
<dependency>
 <groupId>com.tkb.lib</groupId>
 <artifactId>pandora</artifactId>
 <version>${version}</version>
 <classifier>lib</classifier>
 <exclusions>
  <exclusion>
   <groupId>net.semanticmetadata</groupId>
   <artifactId>lire</artifactId>
  </exclusion>
  <exclusion>
   <groupId>org.openimaj</groupId>
   <artifactId>image-feature-extraction</artifactId>
  </exclusion>
  <exclusion>
   <groupId>org.openimaj</groupId>
   <artifactId>image-local-features</artifactId>
  </exclusion>
 </exclusions>
</dependency>
```

