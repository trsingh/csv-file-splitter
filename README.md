# CSV File Splitter Application

A professional Spring Boot application for splitting large CSV files into smaller, manageable chunks based on configurable row counts.

## Features

- **Web Interface**: Professional HTML interface with drag-and-drop file upload
- **REST API**: Complete REST endpoints for programmatic access
- **Configurable Splitting**: Specify the number of rows per output file
- **Custom Output Directory**: Choose where to save the split files
- **File Naming**: Professional naming convention with timestamps
- **Progress Tracking**: Real-time feedback on processing status
- **File Validation**: Automatic validation of uploaded files

## Technology Stack

- **Backend**: Spring Boot 3.1.5
- **Java Version**: JDK 21
- **Frontend**: HTML5, Bootstrap 5, JavaScript
- **File Processing**: Apache Commons CSV
- **Build Tool**: Maven

## Quick Start

### Prerequisites
- JDK 21 or higher
- Maven 3.6 or higher

### Running the Application

1. **Clone and navigate to the project directory**
   ```bash
   cd csv-file-splitter
   ```

2. **Compile the application**
   ```bash
   mvn clean compile
   ```

3. **Start the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - Web Interface: http://localhost:8080
   - Health Check: http://localhost:8080/api/file-splitter/health

## Usage

### Web Interface

1. Open your browser and navigate to http://localhost:8080
2. **Upload Source File**: Drag and drop or browse to select your CSV file
3. **Configure Records Per File**: Specify how many rows each output file should contain
4. **Set Output Directory**: Choose the destination directory for split files
5. **Set File Prefix**: Customize the prefix for output file names
6. **Click "Split File"** to process

### REST API Endpoints

#### Split File
```http
POST /api/file-splitter/split
Content-Type: multipart/form-data

Parameters:
- sourceFile: The CSV file to split (multipart file)
- rowsPerFile: Number of rows per output file (integer)
- outputDirectory: Destination directory path (string)
- filePrefix: Prefix for output files (string, optional, default: "split_file")
```

#### Validate File
```http
POST /api/file-splitter/validate
Content-Type: multipart/form-data

Parameters:
- file: The file to validate (multipart file)
```

#### Health Check
```http
GET /api/file-splitter/health
```

## Example Usage

### Sample Input File (1000 rows)
If you have a CSV file with 1000 rows and want to split it into files with 50 rows each:

- **Input**: `large_data.csv` (1000 rows)
- **Configuration**: 50 rows per file
- **Output**: 20 files named like:
  - `split_data_20250915_143000_part_001.csv` (50 rows)
  - `split_data_20250915_143000_part_002.csv` (50 rows)
  - ...
  - `split_data_20250915_143000_part_020.csv` (50 rows)

### File Naming Convention
```
{filePrefix}_{timestamp}_part_{fileNumber}.{extension}
```

Example: `split_data_20250915_143000_part_001.csv`

## Configuration

The application can be configured via `application.properties`:

```properties
# Server Configuration
server.port=8080

# File Upload Configuration
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB

# Application Configuration
app.file-splitter.default-output-directory=./output
app.file-splitter.temp-directory=./temp
```

## Directory Structure
```
csv-file-splitter/
├── src/
│   ├── main/
│   │   ├── java/com/finzly/poc/csvfilesplitter/
│   │   │   ├── controller/          # REST controllers
│   │   │   ├── service/             # Business logic
│   │   │   ├── dto/                 # Data transfer objects
│   │   │   └── CsvFileSplitterApplication.java
│   │   └── resources/
│   │       ├── templates/           # Thymeleaf templates
│   │       ├── static/              # Static resources
│   │       └── application.properties
├── output/                          # Default output directory
├── temp/                           # Temporary files directory
├── test-data.csv                   # Sample test file
├── pom.xml
└── README.md
```

## Testing

A sample test file `test-data.csv` with 25 rows is included. You can use this to test the splitting functionality:

1. Upload `test-data.csv`
2. Set rows per file to `5`
3. This will create 5 output files with 5 rows each

## Error Handling

The application includes comprehensive error handling for:
- Invalid file formats
- Missing parameters
- File system errors
- Large file processing
- Network connectivity issues

## API Response Format

### Success Response
```json
{
  "success": true,
  "message": "Successfully split file into 5 parts with 25 total rows processed",
  "totalRowsProcessed": 25,
  "numberOfFilesCreated": 5,
  "createdFiles": [
    "split_data_20250915_143000_part_001.csv",
    "split_data_20250915_143000_part_002.csv",
    ...
  ],
  "outputDirectory": "./output"
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description here"
}
```

## Troubleshooting

### Common Issues

1. **Port Already in Use**: Change the port in `application.properties`
2. **Permission Denied**: Ensure write permissions for the output directory
3. **Large File Upload**: Adjust `spring.servlet.multipart.max-file-size` setting
4. **Java Version**: Ensure JDK 21 is installed and configured

### Logs
Application logs provide detailed information about processing status and any errors encountered.

## License

This project is developed for Finzly POC purposes.
