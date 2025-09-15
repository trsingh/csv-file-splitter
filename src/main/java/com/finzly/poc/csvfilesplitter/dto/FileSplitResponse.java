package com.finzly.poc.csvfilesplitter.dto;

import java.util.List;

public class FileSplitResponse {
    
    private boolean success;
    private String message;
    private int totalRowsProcessed;
    private int numberOfFilesCreated;
    private List<String> createdFiles;
    private String outputDirectory;
    
    // Constructors
    public FileSplitResponse() {}
    
    public FileSplitResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    // Static factory methods
    public static FileSplitResponse success(String message, int totalRows, int filesCreated, List<String> files, String outputDir) {
        FileSplitResponse response = new FileSplitResponse(true, message);
        response.setTotalRowsProcessed(totalRows);
        response.setNumberOfFilesCreated(filesCreated);
        response.setCreatedFiles(files);
        response.setOutputDirectory(outputDir);
        return response;
    }
    
    public static FileSplitResponse error(String message) {
        return new FileSplitResponse(false, message);
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public int getTotalRowsProcessed() {
        return totalRowsProcessed;
    }
    
    public void setTotalRowsProcessed(int totalRowsProcessed) {
        this.totalRowsProcessed = totalRowsProcessed;
    }
    
    public int getNumberOfFilesCreated() {
        return numberOfFilesCreated;
    }
    
    public void setNumberOfFilesCreated(int numberOfFilesCreated) {
        this.numberOfFilesCreated = numberOfFilesCreated;
    }
    
    public List<String> getCreatedFiles() {
        return createdFiles;
    }
    
    public void setCreatedFiles(List<String> createdFiles) {
        this.createdFiles = createdFiles;
    }
    
    public String getOutputDirectory() {
        return outputDirectory;
    }
    
    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
}
