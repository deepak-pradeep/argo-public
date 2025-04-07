package example;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.glue.model.*;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class CreateIcebergTable {
    public static void main(String[] args) {
        // Parameters
        String databaseName = "test123";
        String tableName = "test-iceberg-table123";
        String s3Location = "s3://glue-iceberg-s3-test-poc/data/";
        
        // Create Glue client
        GlueClient glueClient = GlueClient.builder()
                .region(Region.US_EAST_1)
                .build();
        
        try {
            // Try to create database
            try {
                DatabaseInput databaseInput = DatabaseInput.builder()
                        .name(databaseName)
                        .description("Iceberg demo database created via Java SDK")
                        .build();
                
                CreateDatabaseRequest createDatabaseRequest = CreateDatabaseRequest.builder()
                        .databaseInput(databaseInput)
                        .build();
                
                glueClient.createDatabase(createDatabaseRequest);
                System.out.println("Database '" + databaseName + "' created.");
            } catch (AlreadyExistsException e) {
                System.out.println("Database '" + databaseName + "' already exists. Will use existing database.");
            }
            
            // Check if table exists, and delete it if it does
            try {
                GetTableRequest getTableRequest = GetTableRequest.builder()
                        .databaseName(databaseName)
                        .name(tableName)
                        .build();
                
                glueClient.getTable(getTableRequest);
                
                // If we get here, the table exists, so delete it
                DeleteTableRequest deleteTableRequest = DeleteTableRequest.builder()
                        .databaseName(databaseName)
                        .name(tableName)
                        .build();
                
                glueClient.deleteTable(deleteTableRequest);
                System.out.println("Existing table '" + tableName + "' deleted.");
            } catch (EntityNotFoundException e) {
                // Table doesn't exist, which is fine
                System.out.println("Table '" + tableName + "' doesn't exist. Will create a new one.");
            }
            
            // Create table
            // Define columns
            List<Column> columns = List.of(
                    Column.builder().name("id").type("int").build(),
                    Column.builder().name("name").type("string").build(),
                    Column.builder().name("event_time").type("timestamp").build()
            );
            
            // Set up SerDe info
            SerDeInfo serDeInfo = SerDeInfo.builder()
                    .serializationLibrary("org.apache.iceberg.mr.hive.HiveIcebergSerDe")
                    .parameters(new HashMap<>())
                    .build();
            
            // Set up storage descriptor
            StorageDescriptor storageDescriptor = StorageDescriptor.builder()
                    .columns(columns)
                    .location(s3Location)
                    .inputFormat("org.apache.iceberg.mr.hive.HiveIcebergInputFormat")
                    .outputFormat("org.apache.iceberg.mr.hive.HiveIcebergOutputFormat")
                    .serdeInfo(serDeInfo)
                    .build();
            
            // Set up table parameters
            Map<String, String> tableParameters = new HashMap<>();
            tableParameters.put("table_type", "ICEBERG");
            tableParameters.put("classification", "iceberg");
            tableParameters.put("EXTERNAL", "TRUE");
            
            // Build table input
            TableInput tableInput = TableInput.builder()
                    .name(tableName)
                    .tableType("ICEBERG")
                    .parameters(tableParameters)
                    .storageDescriptor(storageDescriptor)
                    .build();
            
            // Create table request
            CreateTableRequest createTableRequest = CreateTableRequest.builder()
                    .databaseName(databaseName)
                    .tableInput(tableInput)
                    .build();
            
            glueClient.createTable(createTableRequest);
            System.out.println("Iceberg table '" + tableName + "' created successfully!");
            
        } catch (Exception e) {
            System.err.println("Error creating database or table: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
