# donations

1. Export the donations Google Sheet into 2 CSVs
    1. One for Checks
    1. One for Cash
1. Copy the 2 CSVs into the `resources` folder. Make sure the file names match. DO NOT COMMIT these CSVs.
1. Run the `ReportGenerator` class, and answer the prompts.
1. Look for the generated `.docx` or `.pdf` file(s) in the `exports` folder.

### To batch convert individual docx to pdf (doesn't work anymore)
1. Open Google Drive website
1. Upload all docx files to `Takeout` folder. Using the website, Drive should convert these to Google Doc format.
1. Open Google Takeout website
1. Deselect all options except Google Drive
1. In advanced options, deselect all folders except the `Takout` folder
1. In advanced options, change the document export format to `PDF`
1. Confirm export and download zip file.