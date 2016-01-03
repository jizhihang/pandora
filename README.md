# Introduction #
Pandora is a computer vision library providing many implementations mostly in the region of **Image Feature Extraction** regarding state of the art methods found so far in scientific papers and open source projects as well. Both local and global feature extractors like,

* SURF, SIFT and
* CEDD, Color Histogram, Scalable Color, Edge Histogram, HOG, PHOG, Tamura Histogram

implemented on a wrapper class mode using external libraries like,

* [BoofCV](http://boofcv.org)
* [LIRE](http://www.lire-project.net)
* [OpenIMAJ](http://www.openimaj.org/)

In the region of **Vectorization** and **Descriptor Aggregation** we implement methods like BOW, VLAD and VLAT for both single or multiple oriented vocabularies. In addition you find also some utility implementations like **Random Permutation** for sampling purposes, **Projection Space Reduction** a dimensionality reduction process based on Principal Component Analysis. This library can be used both as an external library to another project or as an executable software for various purposes like, image feature extraction, descriptors sampling, k-means based aggregation vocabularies, vectorization and descriptor aggregation, projection space and dimensionality reduction on vectors as also for descriptor indexing in database.

# Environment #
You gonna need the following prerequisites already installed before build the project artifact,

* Apache Maven 3+
* Java JDK 7+

# Build #
This library can be used in two possible ways, first as an external library to another project or as an executable in order to extract image features in a batch mode for given image datasets as well as for other operations mentioned before. In order to build the project artifact you need to specify the following command, as you can see in the next code sections both for library or executable purposes,

### Library ###
The following command packages and installs the pandora library in your local maven repository in order to use it in another project,
```
#!maven
mvn clean package install -P full -D maven.test.skip=true
```
then you can use the library in another project just by adding the following dependency bellow,
```
#!maven
<dependency>
 <groupId>me.ext.libs</groupId>
 <artifactId>pandora</artifactId>
 <version>1.1.4-SNAPSHOT</version>
</dependency>
```

### Executable ###
The following command packages and builds the executable of the pandora library in order to be used in image features extraction, sampling, clustering and etc. upon a given image dataset in batch mode,
```
#!maven
mvn clean package -P full -D maven.test.skip=true
```
the result of the build process above is actually a jar file followed by two folders the lib/ where the external dependencies will be stored and the configs/ where you can find the configuration settings provided for each operation. In order to execute the project use one of the following commands despite your system,

* *extract* image features given a dataset of images,
```
#!linux
java -Xmx1024m -jar pandora.jar extract configs/extraction.properties
```

* *sample* a subset of vectors given in text csv form files,
```
#!linux
java -Xmx1024m -jar pandora.jar sample configs/sampler.properties
```

* *create* codebook vocabularies using K-means clustering,
```
#!linux
java -Xmx1024m -jar pandora.jar cluster configs/clustering.properties
```

* *aggregate* local descriptors per image into a fixed size vector using single or multiple codebooks,
```
#!linux
java -Xmx1024m -jar pandora.jar build configs/builder.properties
```

* *compute* the projection space of a vector set using PCA analysis,
```
#!linux
java -Xmx1024m -jar pandora.jar project configs/projector.properties
```

* *reduce* the most prominent components of each vector given the projection sub-space matrix,
```
#!linux
java -Xmx1024m -jar pandora.jar reduce configs/reducer.properties
```

* *index* each descriptor into a database using Postgresql,
```
#!linux
java -Xmx1024m -jar pandora.jar index configs/indexer.properties
```

### Lightweight Version ###
In case of a web application where resources are restricted, you can build a more lightweight artifact. Use the profile *war* in order to instruct maven to ignore unwanted classes as also their heavy in size and memory external transitive dependencies. For more information please read the pom.xml file, under the *war* profile,
```
#!maven
mvn clean package install -P war -D maven.test.skip=true
```
then you can use the library in another project just by adding the dependency bellow given the classifier *war*,
```
#!maven
<dependency>
 <groupId>me.ext.libs</groupId>
 <artifactId>pandora</artifactId>
 <version>1.1.4-SNAPSHOT</version>
 <classifier>war</classifier>
</dependency>
```

Below you can find an example in order to exclude all SIFT feature implementations and their transitive OpenIMAJ dependecies, add the exclusion tags under the *war* profile section,

```
#!maven
<excludes>
 <exclude>**/me/pandora/image/local/DenseSift.java</exclude>
 <exclude>**/me/pandora/image/local/FastSift.java</exclude>
 <exclude>**/me/pandora/image/local/GaussianSift.java</exclude>
 <exclude>**/me/pandora/image/local/GridSift.java</exclude>
 <exclude>**/me/pandora/image/local/Sift.java</exclude>
</excludes>
```

and set the *optional* tag to true, so each transitive dependency related to these classes will not be downloaded into the other project,

```
#!maven
<dependency>
 <groupId>org.openimaj</groupId>
 <artifactId>image-feature-extraction</artifactId>
 <version>1.3.1</version>
 <optional>true</optional>
</dependency>

<dependency>
 <groupId>org.openimaj</groupId>
 <artifactId>image-local-features</artifactId>
 <version>1.3.1</version>
 <optional>true</optional>
</dependency>
```

# Examples #
Please find below a short snippet of code in order to get an easy to start tutorial of how this library can be used in a project as an external dependency. Assuming we are gonna use the SURF feature detector in order to extract SURF features from a given image file,

```
#!java
BufferedImage image = UtilImageIO.loadImage("filepath");

FeatureDetector detector = new Surf(2, 0F, -1, 2, 9, 4, 4, true);

double[][] descriptors = detector.extract(image).getDescriptors();
```

or alternatively you can use the JSON constructor using the [Jackson FasterXML](https://github.com/FasterXML/jackson) library,

```
#!java
BufferedImage image = UtilImageIO.loadImage("filepath");

ObjectMapper mapper = new ObjectMapper();

String settings = "{ "radius": 2, "threshold": 0, "maxFeaturesPerScale": -1, "initialSampleRate": 2, "initialSize": 9, "numberScalesPerOctave": 4, "numberOfOctaves": 4, "slided": true }";

FeatureDetector detector = mapper.readValue(settings, Surf.class);

double[][] descriptors = detector.extract(image).getDescriptors();
```