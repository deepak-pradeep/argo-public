import boto3

glue = boto3.client('glue', region_name='us-east-1')

# Parameters
database_name = 'test123'
table_name = 'test-iceberg-table'
s3_location = 's3://glue-iceberg-s3-test-poc/data/'

glue.create_database(
    DatabaseInput={
        'Name': database_name,
        'Description': 'Iceberg demo database created via Boto3',
    }
)
print(f"Database '{database_name}' created.")

# Create table request
response = glue.create_table(
    DatabaseName=database_name,
    TableInput={
        'Name': table_name,
        'TableType': 'ICEBERG',
        'Parameters': {
            'table_type': 'ICEBERG',
            'classification': 'iceberg',
            'EXTERNAL': 'TRUE'
        },
        'StorageDescriptor': {
            'Columns': [
                {'Name': 'id', 'Type': 'int'},
                {'Name': 'name', 'Type': 'string'},
                {'Name': 'event_time', 'Type': 'timestamp'}
            ],
            'Location': s3_location,
            'InputFormat': 'org.apache.iceberg.mr.hive.HiveIcebergInputFormat',
            'OutputFormat': 'org.apache.iceberg.mr.hive.HiveIcebergOutputFormat',
            'SerdeInfo': {
                'SerializationLibrary': 'org.apache.iceberg.mr.hive.HiveIcebergSerDe',
                'Parameters': {}
            }
        }
    }
)

print(f"Iceberg table '{table_name}' created successfully!")
