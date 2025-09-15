package com.finzly.poc.csvfilesplitter.controller;

import com.finzly.poc.csvfilesplitter.dto.FileSplitRequest;
import com.finzly.poc.csvfilesplitter.dto.FileSplitResponse;
import com.finzly.poc.csvfilesplitter.service.FileSplitterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/file-splitter")
@Tag(name = "File Splitter", description = "API endpoints for splitting CSV files into smaller chunks")
public class FileSplitterController {
    
    private static final Logger logger = LoggerFactory.getLogger(FileSplitterController.class);
    
    @Autowired
    private FileSplitterService fileSplitterService;
    
    /**
     * Main endpoint to handle file splitting
     */
    @PostMapping(value = "/split", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    @Operation(summary = "Split CSV file into smaller chunks", 
               description = "Upload a CSV file and split it into multiple smaller files based on the specified number of rows per file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File split successfully", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FileSplitResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input parameters", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FileSplitResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FileSplitResponse.class)))
    })
    public ResponseEntity<FileSplitResponse> splitFile(
            @Parameter(description = "CSV file to be split", required = true)
            @RequestParam("sourceFile") MultipartFile file,
            
            @Parameter(description = "Number of rows per output file", required = true, example = "50")
            @RequestParam("rowsPerFile") Integer rowsPerFile,
            
            @Parameter(description = "Directory path where split files will be saved", required = true, example = "./output")
            @RequestParam("outputDirectory") String outputDirectory,
            
            @Parameter(description = "Prefix for output file names", example = "split_data")
            @RequestParam(value = "filePrefix", defaultValue = "split_file") String filePrefix) {
        
        try {
            logger.info("Received file split request - File: {}, Rows per file: {}, Output directory: {}", 
                       file.getOriginalFilename(), rowsPerFile, outputDirectory);
            
            // Validate input parameters
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(FileSplitResponse.error("No file uploaded"));
            }
            
            if (rowsPerFile == null || rowsPerFile <= 0) {
                return ResponseEntity.badRequest()
                    .body(FileSplitResponse.error("Rows per file must be a positive number"));
            }
            
            if (outputDirectory == null || outputDirectory.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(FileSplitResponse.error("Output directory is required"));
            }
            
            // Create request object
            FileSplitRequest request = new FileSplitRequest(rowsPerFile, outputDirectory.trim(), filePrefix);
            
            // Process the file
            FileSplitResponse response = fileSplitterService.splitFile(file, request);
            
            if (response.isSuccess()) {
                logger.info("File split completed successfully: {}", response.getMessage());
                return ResponseEntity.ok(response);
            } else {
                logger.error("File split failed: {}", response.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Unexpected error during file split: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(FileSplitResponse.error("Unexpected error: " + e.getMessage()));
        }
    }
    
    /**
     * Endpoint to validate file before processing
     */
    @PostMapping("/validate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> validateFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("valid", false);
                response.put("message", "No file uploaded");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Basic validation
            String filename = file.getOriginalFilename();
            long fileSize = file.getSize();
            
            if (filename == null || (!filename.toLowerCase().endsWith(".csv") && !filename.toLowerCase().endsWith(".txt"))) {
                response.put("valid", false);
                response.put("message", "Please upload a CSV or TXT file");
                return ResponseEntity.ok(response);
            }
            
            response.put("valid", true);
            response.put("filename", filename);
            response.put("fileSize", fileSize);
            response.put("fileSizeFormatted", formatFileSize(fileSize));
            response.put("message", "File is valid for processing");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error validating file: {}", e.getMessage(), e);
            response.put("valid", false);
            response.put("message", "Error validating file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @ResponseBody
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "File Splitter Service");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Utility method to format file size
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }
}
