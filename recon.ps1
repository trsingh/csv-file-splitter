# PowerShell Script: Count rows in CSV files (excluding header)

# Set the folder path (change this if needed)
$folderPath = "C:\finzly-poc\csv-file-splitter\output"

# Get all CSV files in the folder
$csvFiles = Get-ChildItem -Path $folderPath -Filter *.csv

# Initialize grand total
$grandTotal = 0

# Loop through each CSV file
foreach ($file in $csvFiles) {
    # Count lines in the file
    $lineCount = (Get-Content $file.FullName).Count

    # Subtract 1 for the header (if file is not empty)
    $rowCount = if ($lineCount -gt 0) { $lineCount - 1 } else { 0 }

    # Add to grand total
    $grandTotal += $rowCount

    # Print row count per file
    Write-Output "$($file.Name): $rowCount rows"
}

# Print grand total
Write-Output "------------------------------"
Write-Output "Grand Total: $grandTotal rows"
