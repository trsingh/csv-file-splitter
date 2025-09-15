package com.finzly.poc.csvfilesplitter.service;

import com.finzly.poc.csvfilesplitter.dto.FileSplitRequest;
import com.finzly.poc.csvfilesplitter.dto.FileSplitResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileSplitterService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileSplitterService.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    public FileSplitResponse splitFile(MultipartFile file, FileSplitRequest request) {
        try {
            // Validate input
            if (file.isEmpty()) {
                return FileSplitResponse.error("Uploaded file is empty");
            }
            
            // Create output directory if it doesn't exist
            Path outputPath = Paths.get(request.getOutputDirectory());
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
                logger.info("Created output directory: {}", outputPath.toAbsolutePath());
            }
            
            // Get original filename without extension
            String originalFilename = file.getOriginalFilename();
            String baseFilename = getBaseFilename(originalFilename);
            String fileExtension = getFileExtension(originalFilename);
            
            // Parse CSV and split
            List<String> createdFiles = new ArrayList<>();
            int totalRowsProcessed = 0;
            int currentFileIndex = 1;
            
            try (InputStream inputStream = file.getInputStream();
                 Reader reader = new InputStreamReader(inputStream);
                 CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(reader)) {
                
                List<String> headers = new ArrayList<>(parser.getHeaderNames());
                List<CSVRecord> currentBatch = new ArrayList<>();
                
                for (CSVRecord record : parser) {
                    currentBatch.add(record);
                    totalRowsProcessed++;
                    
                    if (currentBatch.size() >= request.getRowsPerFile()) {
                        String filename = createSplitFile(outputPath, baseFilename, fileExtension, 
                                                        currentFileIndex, headers, currentBatch, request.getFilePrefix());
                        createdFiles.add(filename);
                        currentBatch.clear();
                        currentFileIndex++;
                    }
                }
                
                // Handle remaining records
                if (!currentBatch.isEmpty()) {
                    String filename = createSplitFile(outputPath, baseFilename, fileExtension, 
                                                    currentFileIndex, headers, currentBatch, request.getFilePrefix());
                    createdFiles.add(filename);
                }
            }
            
            String message = String.format("Successfully split file into %d parts with %d total rows processed", 
                                          createdFiles.size(), totalRowsProcessed);
            
            return FileSplitResponse.success(message, totalRowsProcessed, createdFiles.size(), 
                                           createdFiles, request.getOutputDirectory());
            
        } catch (Exception e) {
            logger.error("Error splitting file: {}", e.getMessage(), e);
            return FileSplitResponse.error("Error splitting file: " + e.getMessage());
        }
    }
    
    private String createSplitFile(Path outputPath, String baseFilename, String fileExtension, 
                                 int fileIndex, List<String> headers, List<CSVRecord> records, 
                                 String filePrefix) throws IOException {
        
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String filename = String.format("%s_%s_part_%03d%s", filePrefix, timestamp, fileIndex, fileExtension);
        Path filePath = outputPath.resolve(filename);
        
        try (FileWriter writer = new FileWriter(filePath.toFile());
             CSVPrinter printer = CSVFormat.DEFAULT.builder().setHeader(headers.toArray(new String[0])).build().print(writer)) {
            
            for (CSVRecord record : records) {
                List<String> values = new ArrayList<>();
                for (int i = 0; i < headers.size(); i++) {
                    values.add(record.get(i));
                }
                printer.printRecord(values);
            }
        }
        
        logger.info("Created split file: {} with {} records", filename, records.size());
        return filename;
    }
    
    private String getBaseFilename(String filename) {
        if (filename == null) {
            return "uploaded_file";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(0, lastDotIndex) : filename;
    }
    
    private String getFileExtension(String filename) {
        if (filename == null) {
            return ".csv";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex) : ".csv";
    }
}
