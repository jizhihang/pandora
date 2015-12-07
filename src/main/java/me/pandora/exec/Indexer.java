package me.pandora.exec;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import me.pandora.io.MultipleFilenameFilter;
import me.pandora.io.Reader;
import me.pandora.util.ArrayOps;
import me.pandora.util.SmartProperties;
import org.apache.log4j.Logger;

/**
 * An image descriptor indexing loader.
 *
 * Run as: mvn exec:java -Dexec.mainClass="me.pandora.exec.Indexer" -Dexec.args="path/to/config.properties"
 *
 * @author Akis Papadopoulos
 */
public class Indexer {

    public static void main(String[] args) {
        Logger logger = null;

        Connection connection = null;
        Statement statement = null;

        try {
            // Loading configuration properties
            SmartProperties props = new SmartProperties();
            props.load(new FileInputStream(args[0]));

            String driver = props.getProperty("index.db.driver");
            String host = props.getProperty("index.db.host");
            String dbname = props.getProperty("index.db.name");
            String username = props.getProperty("index.db.username");
            String password = props.getProperty("index.db.password");
            String inpath = props.getProperty("index.descriptors.input.file.path");
            String extension = props.getProperty("index.descriptors.file.extension");
            List<String> vocabs = props.matchProperties("index.vocab.\\d+");
            String projection = props.getProperty("index.projection.file.path");
            boolean whitening = Boolean.parseBoolean(props.getProperty("index.projection.whitening"));

            String logfile = inpath + " /index.log";

            // Setting up the logger
            System.setProperty("log.file", logfile);
            logger = Logger.getLogger(Indexer.class);

            System.out.println("See logs as: tail -f -n 100 " + logfile);

            logger.info("Configuration loaded");
            logger.info("File: " + args[0]);
            logger.info("Host: " + host);
            logger.info("Database: " + dbname);
            logger.info("Descriptors: " + inpath);
            logger.info("Type: " + extension);

            if (!vocabs.isEmpty()) {
                for (int i = 0; i < vocabs.size(); i++) {
                    logger.info("Vocabulary #" + (i + 1) + ": " + vocabs.get(i));
                }
            }

            if (!projection.isEmpty()) {
                logger.info("Projection: " + projection);
                logger.info("Whitening: " + whitening);
            }

            // Opening a database connection
            Class.forName(driver);

            connection = DriverManager.getConnection(host + "/" + dbname, username, password);

            // Indexing descriptors into the database
            File dirin = new File(inpath);
            MultipleFilenameFilter filter = new MultipleFilenameFilter(extension);
            String[] filenames = dirin.list(filter);

            logger.info("Process started");
            logger.info("Indexing descriptors...");

            // Indexing decriptors
            int descriptorsIndexed = 0;

            for (int i = 0; i < filenames.length; i++) {
                // Reading the descriptor
                double[] vector = Reader.read(dirin.getPath() + "/" + filenames[i], 1);

                Array descriptor = connection.createArrayOf("numeric", ArrayOps.toObject(vector));

                // Extracting the file name used as identifier
                int pos = filenames[i].lastIndexOf(".");
                String id = filenames[i].substring(0, pos);

                // TMP
                id += ".jpg";

                try {
                    // Indexing the descriptor given the image id
                    statement = connection.createStatement();

                    StringBuilder query = new StringBuilder();

                    query.append("UPDATE images ")
                            .append("SET descriptor = '").append(descriptor).append("' ")
                            .append("WHERE id = '").append(id).append("'");

                    descriptorsIndexed += statement.executeUpdate(query.toString());
                } catch (SQLException exc) {
                    logger.error("An error occurred indexing descriptor '" + filenames[i] + "'", exc);
                } finally {
                    if (statement != null) {
                        statement.close();
                    }
                }

                if (i % 100 == 0) {
                    int progress = (i * 100) / filenames.length;
                    logger.info(progress + "%...");
                }
            }
            
            logger.info("100%");

            int vocabsIndexed = 0;

            if (!vocabs.isEmpty()) {
                logger.info("Indexing vocabularies...");
                
                try {
                    // Truncating already stored vocabularies
                    statement = connection.createStatement();

                    StringBuilder query = new StringBuilder();

                    query.append("TRUNCATE TABLE ONLY codebooks");

                    statement.executeUpdate(query.toString());
                } catch (SQLException exc) {
                    logger.error("An error occurred truncating vocabularies", exc);
                } finally {
                    if (statement != null) {
                        statement.close();
                    }
                }

                // Indexing vocabularies
                for (int i = 0; i < vocabs.size(); i++) {
                    // Reading vocabulary codebook
                    double[][] matrix = Reader.read(vocabs.get(i));

                    Array centroids = connection.createArrayOf("numeric", ArrayOps.toObject(matrix));

                    try {
                        statement = connection.createStatement();

                        StringBuilder query = new StringBuilder();

                        query.append("INSERT INTO codebooks (id, centroids) ")
                                .append("VALUES (").append(i + 1).append(", '")
                                .append(centroids).append("')");

                        vocabsIndexed += statement.executeUpdate(query.toString());
                    } catch (SQLException exc) {
                        logger.error("An error occurred indexing vocabulary codebook '" + vocabs.get(i) + "'", exc);
                    } finally {
                        if (statement != null) {
                            statement.close();
                        }
                    }
                }
            }

            if (!projection.isEmpty()) {
                logger.info("Indexing projection...");
                
                try {
                    // Truncating already stored projection reducers
                    statement = connection.createStatement();

                    StringBuilder query = new StringBuilder();

                    query.append("TRUNCATE TABLE ONLY reducers");

                    statement.executeUpdate(query.toString());
                } catch (SQLException exc) {
                    logger.error("An error occurred truncating projection reducers", exc);
                } finally {
                    if (statement != null) {
                        statement.close();
                    }
                }

                // Indexing the projection reducer
                double[][] matrix = Reader.read(projection);

                // Extracting the projection mean vector
                Array mean = connection.createArrayOf("numeric", ArrayOps.toObject(matrix[0]));

                // Extracting the projection subspace eigenvectors
                double[][] eigenvectors = ArrayOps.copy(matrix, 1);

                Array subspace = connection.createArrayOf("numeric", ArrayOps.toObject(eigenvectors));

                try {
                    statement = connection.createStatement();

                    StringBuilder query = new StringBuilder();

                    query.append("INSERT INTO reducers (id, subspace, mean, whiten) ")
                            .append("VALUES (").append(1).append(", '")
                            .append(subspace).append("', '")
                            .append(mean).append("', ")
                            .append(whitening).append(")");

                    statement.executeUpdate(query.toString());
                } catch (SQLException exc) {
                    logger.error("An error occurred indexing projection reducer '" + projection + "'", exc);
                } finally {
                    if (statement != null) {
                        statement.close();
                    }
                }
            }
            
            logger.info("Process completed successfuly");
            logger.info("Descriptors: " + descriptorsIndexed + "/" + filenames.length);
            logger.info("Vocabs: " + vocabsIndexed + "/" + vocabs.size());
            logger.info("Projections: " + (projection.isEmpty() ? "0/1" : "1/1"));
        } catch (Exception exc) {
            if (logger != null) {
                logger.error("An unknown error occurred indexing descriptors, vocabularies and projection reducer", exc);
            } else {
                exc.printStackTrace();
            }
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException exc) {
                    exc.printStackTrace();
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException exc) {
                    exc.printStackTrace();
                }
            }
        }
    }
}
