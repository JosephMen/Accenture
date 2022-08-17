import groovy.io.FileType
import java.time.LocalDateTime
import java.text.SimpleDateFormat

//Main function
public void main(){

    // Initialize the log file for record all the messages
    File logFile = new File("log.txt")
    def logInputStream = logFile.newPrintWriter() //Input stream of log file

    def date = new Date()
    def sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
    logInputStream.println " - Start date: ${sdf.format(date)}"

    String currentPath = new File("").absolutePath //Current path of .groovy file 
    try {
        // Request and set of input path
        logInputStream.println " - Requesting for input Path"
        String requestInputPathMessage = "Input path directory (Blank for use the current directory): "
        String inputPathRequest = System.console().readLine requestInputPathMessage
        String inputPath = inputPathRequest != "" ? inputPathRequest : currentPath
        logInputStream.println " - The selected path is : $inputPath"

        //Request and validate of input pattern
        logInputStream.println " - Requesting for pattern" //recording in log file
        String inputPattern = System.console().readLine "Pattern to search : "
        if (inputPattern == ""){
            throw new Exception ("Error: You should to enter a pattern")
        }
        logInputStream.println " - Pattern Selected: $inputPattern" //recording in log file

        //Request and validate of input word for replacing
        logInputStream.println " - Requesting for word to replacing" //recording in log file
        String wordForReplace = System.console().readLine "Word for replace the pattern : " 
        if (wordForReplace == ""){
            throw new Exception ("Error: You should to enter a word for replacing")
        }
        logInputStream.println " - Word selected: $wordForReplace" //recording in log file

        // Open directory
        logInputStream.println " - Opening input path directory"
        File filesDirectory = new File(inputPath)
        List<String> listModified = [] // modified list files

        //Validates that enter path is not a file otherwise throw a message
        logInputStream.println " - Validating input path"
        if (filesDirectory.isFile()){
            throw new Exception ("Error: You should to enter a directory not a file !")
        }

        //Files recurse for search all files in the dir and subdirs
        filesDirectory.eachFileRecurse{ file -> 
            if (file.name ==~ /.*\.txt/){ //only .txt files accepted
                logInputStream.println "\n - Working on ${file.name}"
                println ""
                changeFile(inputPattern, wordForReplace, file, listModified, logInputStream) // Call to function 
            }
        }
        // Output for list of modified files
        String requestMessage = "Output path for modified list files? (blank for use the same input path): "
        String modifiedPathRequest = System.console().readLine requestMessage
        String  modifiedFilePath = modifiedPathRequest != "" ? (modifiedPathRequest + "\\modifiedFiles.txt") : (inputPath + "\\modifiedList.txt" )     

        //Saving the list of modified files
        File modifiedFilesList = new File(modifiedFilePath)
        String listFiles = listModified.join("\n")
        modifiedFilesList.write(listFiles)

        //Closing log input stream
        date = new Date()
        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        logInputStream.println " - End date: ${sdf.format(date)}"
        logInputStream.close()

        //
        println "\n\nAll process succes you can find your files modified correctly !"
        println "You can look for details on log.txt file !"

    }catch(Exception e){
        logInputStream.println " - Error find: $e"

        date = new Date()
        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        logInputStream.println " - End date: ${sdf.format(date)}"       
        logInputStream.close()

        println "Exception find: $e"
    }
}

//Input: pattern to search, word for replace, file object, list where save the file if is modified and a log input stream
// Out: none
// change files function 
def void changeFile (String pattern, String word, File file, List<String> listModified, logInputStream) {

    //Initializing structs and setting content
    List<String> lines = file.readLines()
    List<String> newLines = []
    boolean isFoundPatternFirstTime = false

    // Main itering list function 
    lines.eachWithIndex{line, index ->
        if (line.contains(pattern)){
            if (isFoundPatternFirstTime == false){ // record file name if is found the pattern
                savingFile(file) //Backup file
                logPrint(logInputStream, "---------> On File: ${file.name}")
                listModified << file.name
                isFoundPatternFirstTime = true               
            }
            //Change and record line replaced
            logPrint(logInputStream, "Match founded in line $index : $line")
            String lineReplaced = line.replaceAll(pattern, word)
            logPrint(logInputStream, " - Replace result: $lineReplaced")
            newLines << lineReplaced
        }
    }
    //Saving file content changed
    String text = newLines.join("\n")
    file.write(text)
}   

// Input: file
// Out: none
// Desc: Backup original file content on a .bk file
def void savingFile (File file){
    File backupFile = new File(file.absolutePath.replaceAll(file.name, file.name + ".bk"))

    def input = file.newDataInputStream()
    def output = backupFile.newDataOutputStream()

    output << input 

    input.close()
    output.close()
}

//Input: input stream and message
//Out: none
//Record and printing messages for log and console
def void logPrint(logInputStream, String message){
    logInputStream.println "- $message"
    println message
}

main()

