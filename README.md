# donations

1. Export the donations Google Sheet into 2 CSVs
    1. One for Checks
    1. One for Cash
1. These CSVs should be on the Desktop with names 
   1. `donations - check GFCC.csv`  
   1. `donations - cash GFCC.csv`
1. Run the `ReportGenerator` class, and answer the prompts.
1. Look for the generated `.docx` or `.pdf` file(s) in the `exports` folder.

### To generate an executable
1. run `.\gradlw build`
1. unzip the donations zip file in `build/distributions`
1. copy the `Readme.txt` into the unzipped `bin` folder
1. add a shortcut on the Desktop for easy access

### To batch convert individual docx to pdf (doesn't work anymore)
1. Open Google Drive website
1. Upload all docx files to `Takeout` folder. Using the website, Drive should convert these to Google Doc format.
1. Open Google Takeout website
1. Deselect all options except Google Drive
1. In advanced options, deselect all folders except the `Takout` folder
1. In advanced options, change the document export format to `PDF`
1. Confirm export and download zip file.