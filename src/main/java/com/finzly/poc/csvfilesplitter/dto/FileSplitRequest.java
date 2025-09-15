package com.finzly.poc.csvfilesplitter.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FileSplitRequest {
    
    @NotNull(message = "Number of rows per file is required")
    @Min(value = 1, message = "Number of rows per file must be at least 1")
    private Integer rowsPerFile;
    
    @NotBlank(message = "Output directory path is required")
    private String outputDirectory;
    
    private String filePrefix = "split_file";
    
    // Constructors
    public FileSplitRequest() {}
    
    public FileSplitRequest(Integer rowsPerFile, String outputDirectory, String filePrefix) {
        this.rowsPerFile = rowsPerFile;
        this.outputDirectory = outputDirectory;
        this.filePrefix = filePrefix;
    }
    
    // Getters and Setters
    public Integer getRowsPerFile() {
        return rowsPerFile;
    }
    
    public void setRowsPerFile(Integer rowsPerFile) {
        this.rowsPerFile = rowsPerFile;
    }
    
    public String getOutputDirectory() {
        return outputDirectory;
    }
    
    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
    
    public String getFilePrefix() {
        return filePrefix;
    }
    
    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }
}
