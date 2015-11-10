package me.pandora.exec;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import me.pandora.io.MultipleFilenameFilter;
import me.pandora.io.Reader;
import me.pandora.util.VectorTokenizer;
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
            Properties props = new Properties();
            props.load(new FileInputStream(args[0]));
            
            String inpath = props.getProperty("vectors.input.path");
            String extension = props.getProperty("vectors.file.extension");
            String driver = props.getProperty("index.db.driver");
            String host = props.getProperty("index.db.host");
            String dbname = props.getProperty("index.db.name");
            String username = props.getProperty("index.db.username");
            String password = props.getProperty("index.db.password");
            String table = props.getProperty("index.db.table");
            String column = props.getProperty("index.db.table.column");
            String logfile = props.getProperty("log.file.path");

            // Setting up the logger
            System.setProperty("log.file", logfile);
            logger = Logger.getLogger(Indexer.class);
            
            System.out.println("See logs as: tail -f -n 100 " + logfile);
            
            logger.info("Configuration loaded");
            logger.info("File: " + args[0]);
            logger.info("Vectors: " + inpath);
            logger.info("Type: " + extension);
            logger.info("Driver: " + driver);
            logger.info("Host: " + host);
            logger.info("Database: " + dbname);
            logger.info("Table: " + table);
            logger.info("Column: " + column);

            // Opening a database connection
            Class.forName(driver);
            
            connection = DriverManager.getConnection(host + "/" + dbname, username, password);

            // Indexing descriptors into the database
            File dirin = new File(inpath);
            MultipleFilenameFilter filter = new MultipleFilenameFilter(extension);
            String[] filenames = dirin.list(filter);
            
            logger.info("Process started");
            
            int indexed = 0;
            
            for (int i = 0; i < filenames.length; i++) {
                // Reading the descriptor
                double[] vector = Reader.read(dirin.getPath() + "/" + filenames[i], 1);
                
                VectorTokenizer tokenizer = new VectorTokenizer(",");
                
                String descriptor = tokenizer.vectorize(vector);

                // Extracting the file name used as identifier
                int pos = filenames[i].lastIndexOf(".");
                String id = filenames[i].substring(0, pos);
                
                try {
                    // Indexing the descriptor given the image id
                    statement = connection.createStatement();
                    
                    StringBuilder query = new StringBuilder();
                    
                    query.append("UPDATE ").append(table)
                            .append(" SET ").append(column).append(" = '{").append(descriptor).append("}'")
                            .append(" WHERE id = '").append(id).append("'");
                    
                    indexed += statement.executeUpdate(query.toString());
                } catch (SQLException exc) {
                    logger.error("An error occurred indexing descriptor '" + filenames[i], exc);
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
            logger.info("Process completed successfuly");
            logger.info("Vectors: " + filenames.length);
            logger.info("Indexed: " + indexed);
        } catch (Exception exc) {
            if (logger != null) {
                logger.error("An unknown error occurred indexing image descriptors", exc);
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
