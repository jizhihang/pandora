# Introduction #
Pandora is a computer vision purposed library providing many implementations mostly in the region of **Image Feature Extraction** regarding state of the art methods found so far, many of them based on scientific papers and open source projects as well. Feature detection extractors both local and global like,

* SURF, SIFT and
* CEDD, Color Histogram, Scalable Color, Edge Histogram, HOG, PHOG, Tamura Histogram

are implemented using external libraries like,

* [BoofCV](http://boofcv.org)
* [LIRE](http://www.lire-project.net)
* [OpenIMAJ](http://www.openimaj.org/)

In region of **Vectorization** and **Descriptor Aggregation** are implemented algorithms like BOW, VLAD and VLAT for both single or multiple oriented vocabularies. In addition you find also utility implementations like **Random Permutation** for sampling purposes, **Projection Space Reduction** a dimensionality reduction process based on Principal Component Analysis. This library can be used as well as an executable for various purposes like, image feature extraction, descriptors sampling, k-means based aggregation vocabularies, projection space and vector dimensionality reduction and database descriptor indexing.

# Environment #
You gonna need the following prerequisites already installed before build the project artifact,

* Apache Maven 3+
* Java JDK 7+

# Build #
This library can be used in two possible ways, first as an external lite library to another project or as an executable in order to extract image features for given image datasets and so on. In order to build project artifacts you need to specify the profile aiming the purpose of the artifact, as you can see in the next code sections,

## Library ##
The following command packages and installs an lite version of the pandora library in your local maven repository in order to use in another project (for more ready the pom.xml file,
```
#!maven
mvn clean package install -P lite -D maven.test.skip=true
```

## Executable ##
The following command packages and builds the executable of the pandora library in order to be used in image features extraction, sampling, clustering etc upon a given image dataset,
```
#!maven
mvn clean package -P exec -D maven.test.skip=true
```
the result of the build process above is actually a jar file followed by two folders the lib/ where the external dependencies will be stored and the configs/ where the configuration settings provided for each operation. In order to execute the project use the following command despite your system,

```
#!linux
java -Xmx1024m -jar pandora.jar extract configs/extraction.properties
java -Xmx1024m -jar pandora.jar sample configs/sampler.properties
java -Xmx1024m -jar pandora.jar cluster configs/clustering.properties
java -Xmx1024m -jar pandora.jar build configs/builder.properties
java -Xmx1024m -jar pandora.jar project configs/projector.properties
java -Xmx1024m -jar pandora.jar reduce configs/reducer.properties
java -Xmx1024m -jar pandora.jar index configs/indexer.properties
```