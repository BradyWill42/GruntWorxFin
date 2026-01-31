import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.*;
import java.nio.file.Files;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;    


public class gruntWorx {
    public Integer workingYear;
    public Integer yearShort;

    /**    
     * Prompts user to input working year as an int.
     * @return void
     */
    public void yearPrompt(){
        Scanner input = new Scanner(System.in);
        System.out.println("Please click after the colon, type the usable year, and press enter:");
        workingYear = input.nextInt();
        yearShort = workingYear % 100;
        System.out.println(yearShort);
    }

    /**    
     * Moves files from Z: drive to new Gruntworx folder on main machine desktop.
     * @return void
     */
    public void moveGruntFiles(){
        //creates gruntworx file on main machine desktop if it does not already exist
        System.out.println("Checking for Gruntworx directory on C: Drive and creating it if it does not exist...");
        if(new File(System.getProperty("user.home") + "\\Desktop\\Gruntworx").exists()){
            ClearDir clear = new ClearDir();
            try {
                //JJ - makes temporary directory of ALL clients being run so can copy during loop from temporary directory on C drive on per client basis
                System.out.println("Deleting Gruntworx folder and reinstalling it...");
                clear.deleteDirectoryRecursion(new File(System.getProperty("user.home") + "\\Desktop\\Gruntworx").toPath());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        new File(System.getProperty("user.home") + "\\Desktop\\Gruntworx").mkdirs();
        System.out.println("Successfully Created Gruntworx Directory.");
        try{  
            System.out.println("Reading Input file...");

            ClientList.readInputFile();

            //loops through names listed in the input file that have been transfered into an arraylist
            for(int j = 0; j < ClientList.getClients().size(); j++){

                //assigns client variable
                Client client = ClientList.getClients().get(j);

                //creates client directory
                System.out.println("Creating " + client.getName() + " Directory in Gruntworx folder...");
                new File(System.getProperty("user.home") + "\\Desktop\\Gruntworx\\" + client.getName() + "\\" + workingYear).mkdirs();
                File acctGruntFile = new File(System.getProperty("user.home") + "\\Desktop\\Gruntworx\\" + client.getName() + "\\" + workingYear);
                System.out.println(client.getName() + " Directory Successfully Created.");

                //creates account directory of the clients private year folder
                //JJ OLD - BEFORE "ToGrunt" folder added 2/21/2023
                //File account = new File("Z:\\" + client.getName() + "\\Private\\" + workingYear);
                File account = new File("Z:\\" + client.getName() + "\\Private\\" + workingYear + "\\ToGrunt\\");
                System.out.println("Copying " + client.getName() + " Directory over from Z: Drive...");
                
                //tries to copy directory over
                try{

                    // Create a filter for ".pdf" files
                    IOFileFilter txtSuffixFilter = FileFilterUtils.suffixFileFilter(".pdf");
                    IOFileFilter txtFiles = FileFilterUtils.andFileFilter(FileFileFilter.FILE, txtSuffixFilter);
                    // Create a filter for either directories or ".pdf" files
                    FileFilter filter = FileFilterUtils.orFileFilter(DirectoryFileFilter.DIRECTORY, txtFiles);

                    File[] accountDirectoryList = account.listFiles();
                    File[] acctGruntFileDirectoryList = acctGruntFile.listFiles();

                    FileUtils.copyDirectory(account, acctGruntFile, txtSuffixFilter, false);
                    System.out.println(client.getName() + " Directory Successfully Copied.");
                } catch(IOException e){
                    e.printStackTrace();
                    System.out.println(client.getName() + " Client does not exist.");
                    System.out.println("Deleting non-existent Client Folder...");
                    FileUtils.deleteDirectory(new File(System.getProperty("user.home") + "\\Desktop\\Gruntworx\\" + client.getName()));
                    client.setBroken(true);
                    System.out.println("Folder Deleted.");
                }
            }
        } 
        catch(IOException e){  
            e.printStackTrace();  
        }

    }

   
    /**    
     * Logs into gruntworx and completes the gruntworx uploads for each of the approved clients in input file. 
     * Finishes by deleting gruntworx folder on virtual machine after finished. 
     * @return void
     */
    public void gruntworxLogin(String email) throws IOException,
                           AWTException, InterruptedException,
						   UnsupportedFlavorException{

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Robot vm = new Robot();

        Thread.sleep(1000);

        System.out.println("Opening Command Prompt on Virtual Desktop...");

        //opens command prompt on virtual machine
        vm.keyPress(KeyEvent.VK_WINDOWS);
        Thread.sleep(1000);
        vm.keyPress(KeyEvent.VK_X);
        vm.keyRelease(KeyEvent.VK_X);
        vm.keyRelease(KeyEvent.VK_WINDOWS);
        Thread.sleep(3000);
        vm.keyPress(KeyEvent.VK_C);
        vm.keyRelease(KeyEvent.VK_C);

        System.out.println("Opened Command Prompt on Virtual Desktop.");

    //______________________
        //Delete Virtual Desktop Gruntworx folder just in case it crashed on previous run (basically clear the system before starting)
        System.out.println("Deleting Gruntworx folder from Virtual Desktop...");

        //after finishing, remove gruntworx folder from virtual machine desktop
        StringSelection str = new StringSelection("rmdir %UserProfile%\\Desktop\\Gruntworx /S/Q");
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(str, null);

        Thread.sleep(1000);

        //pastes into cmd
        vm.keyPress(KeyEvent.VK_CONTROL);
        vm.keyPress(KeyEvent.VK_V);
        vm.keyRelease(KeyEvent.VK_V);
        vm.keyRelease(KeyEvent.VK_CONTROL);

        Thread.sleep(1000);

        //removes folder
        vm.keyPress(KeyEvent.VK_ENTER);
        vm.keyRelease(KeyEvent.VK_ENTER);

        Thread.sleep(4000);

        System.out.println("Deleted Gruntworx folder from Virtual Desktop.");
    //______________________

        Integer counter = 0;
        //Initializes the counter variable for GruntPrintCounter

        //for each client in InputGrunt.txt file, do gruntworx job
        for(int j = 0; j < ClientList.getClients().size(); j++){
            
            Client client = ClientList.getClients().get(j);
            //reads the clients InputGrunt.txt codes (E.G. =nn, =ea)
            client.readFullInput();
            
            
            System.out.println("Codes and Account Name extracted.");

            //makes file variable for current client
            File clientDir = new File(System.getProperty("user.home") + "\\Desktop\\Gruntworx\\" + client.getName());

            //checks if the upload was successful and if the clients folder is in the gruntworx folder on the virtual machine desktop. 
            //also makes sure the client input is not broken. 
            if(Files.exists(clientDir.toPath()) && !client.isBroken()){

                System.out.println("Client Exists, continuing with job.");

                System.out.println("Moving Files Over...");
                
                //creates folder on Desktop/ClientName/Year to allow the gruntworx upload to run smoothly by copying over from C drive/local computer and copies over files
                //only copies over pdfs in directory specified
                str = new StringSelection("Xcopy \\\\tsclient\\C\\Users\\\"" + System.getProperty("user.name") + "\"\\Desktop\\Gruntworx\\\"" + client.getName() + "\" %UserProfile%" + File.separator + "\\Desktop\\Gruntworx /E/H/C/I");
                clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(str, null);

                Thread.sleep(1000);

                //pastes copied string into cmd
                vm.keyPress(KeyEvent.VK_CONTROL);
                Thread.sleep(1000);
                vm.keyPress(KeyEvent.VK_V);
                vm.keyRelease(KeyEvent.VK_V);
                vm.keyRelease(KeyEvent.VK_CONTROL);

                Thread.sleep(1000);

                //enters string into cmd
                vm.keyPress(KeyEvent.VK_ENTER);
                vm.keyRelease(KeyEvent.VK_ENTER);

                //JJ - this is pause that allows files to copy over from local Desktop to Virtual Machine - has to be long because VM is THE WORST!
                //60000 
                Thread.sleep(120000); //2 minutes

                System.out.println("Files Moved.");

                System.out.println("Starting Gruntworx...");

                //copies string to be pasted into cmd which opens gruntworx
                //str = new StringSelection("start D:\\DRAKE23\\DDM\\GRUNTWORX");
                str = new StringSelection("start D:\\DRAKE" + yearShort + "\\DDM\\GRUNTWORX");
                clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(str, null);

                Thread.sleep(2000);

                //paste gruntworx string into cmd
                vm.keyPress(KeyEvent.VK_CONTROL);
                Thread.sleep(1000);
                vm.keyPress(KeyEvent.VK_V);
                vm.keyRelease(KeyEvent.VK_V);
                vm.keyRelease(KeyEvent.VK_CONTROL);

                Thread.sleep(1000);

                //opens gruntworx
                vm.keyPress(KeyEvent.VK_ENTER);
                vm.keyRelease(KeyEvent.VK_ENTER);

                Thread.sleep(12000); //12 seconds
                
                System.out.println("Gruntworx Opened.");

                //FINISHED OPENING GRUNTWORK

                //navigate to the name input box in gruntworx
                for(int i = 0; i < 6; i++){
                    vm.keyPress(KeyEvent.VK_TAB);
                    vm.keyRelease(KeyEvent.VK_TAB);    
                    Thread.sleep(150);
                }

                //Character limit in Gruntworx Input is 48; don't need full name to make Gruntworx work
                String fullString = client.getName();
                
                //check length of name, if longer than 48 chars, take only the first 48 chars
                if(fullString.length() > 48){
                    String adjustedString = fullString.substring(0, 47);
                    str = new StringSelection(adjustedString);
                    clipboard.setContents(str, null);
                } 
                //otherwise continue as normal with full string
                else {
                    str = new StringSelection(fullString);
                    clipboard.setContents(str, null);
                }
                

                Thread.sleep(2000);

                //paste name into gruntworx
                vm.keyPress(KeyEvent.VK_CONTROL);
                Thread.sleep(1000);
                vm.keyPress(KeyEvent.VK_V);
                vm.keyRelease(KeyEvent.VK_V);
                vm.keyRelease(KeyEvent.VK_CONTROL);

                Thread.sleep(1000);

                //submits name into gruntworx
                vm.keyPress(KeyEvent.VK_ENTER);
                vm.keyRelease(KeyEvent.VK_ENTER);

                Thread.sleep(1000);

                //highlight populate in validated box
                for(int i = 0; i < 6; i++){
                    vm.keyPress(KeyEvent.VK_SHIFT);
                    vm.keyPress(KeyEvent.VK_TAB);
                    vm.keyRelease(KeyEvent.VK_TAB);  
                    vm.keyRelease(KeyEvent.VK_SHIFT);
                    Thread.sleep(75);
                }

                Thread.sleep(1000);

                //selects organize
                vm.keyPress(KeyEvent.VK_UP);
                vm.keyRelease(KeyEvent.VK_UP);

                Thread.sleep(1000);

                //selects populate
                vm.keyPress(KeyEvent.VK_DOWN);
                vm.keyRelease(KeyEvent.VK_DOWN);

                Thread.sleep(1000);

                //highlight the expedite button
                for(int i = 0; i < 4; i++){
                    vm.keyPress(KeyEvent.VK_SHIFT);
                    vm.keyPress(KeyEvent.VK_TAB);
                    vm.keyRelease(KeyEvent.VK_TAB);
                    vm.keyRelease(KeyEvent.VK_SHIFT);
                    Thread.sleep(75);
                }


                //checks if client has a special code
                if(client.isEquals()){
                    //if client has a special code, check if the client is expedited
                    if(client.isExpedited()){
                        //if client is expedited
                        //click the expedite button
                        vm.keyPress(KeyEvent.VK_SPACE);
                        vm.keyRelease(KeyEvent.VK_SPACE);
                        Thread.sleep(1000);
                        vm.keyPress(KeyEvent.VK_ENTER);
                        vm.keyRelease(KeyEvent.VK_ENTER);
                        Thread.sleep(1000);
                        System.out.println("Job has been expedited for " + client.getName() + ".");
                    } else {
                        //otherwise dont check expedite button
                        System.out.println("Job is NOT expedited for " + client.getName() + ".");
                    }

                    //tab over to trade type selection area
                    for(int i = 0; i < 1; i++){
                        vm.keyPress(KeyEvent.VK_TAB);
                        vm.keyRelease(KeyEvent.VK_TAB);
                        Thread.sleep(75);
                    }

                    //if client code is addon trades
                    if(client.getTradeType().equals("a")){
                        //select add-on trades option
                        for(int i = 0; i < 2; i++){
                            vm.keyPress(KeyEvent.VK_UP);
                            vm.keyRelease(KeyEvent.VK_UP);
                            Thread.sleep(75);
                        }   
                        System.out.println("Add-On trades has been selected for " + client.getName() + ".");
                    } 
                    //otherwise check if it is summaary trades
                    else if(client.getTradeType().equals("s")){
                        //select summary trades option
                        for(int i = 0; i < 1; i++){
                            vm.keyPress(KeyEvent.VK_UP);
                            vm.keyRelease(KeyEvent.VK_UP);
                            Thread.sleep(75);
                        }   
                        System.out.println("Summary Add-On trades has been selected for " + client.getName() + ".");
                    } else {
                        //if neither add-on or summary trades, select no trades
                        System.out.println("No trades have been selected for " + client.getName() + ".");
                    }
                }

                Thread.sleep(1000);

                System.out.println("Adding files to submit...");

                //selects add files button
                vm.keyPress(KeyEvent.VK_ALT);
                Thread.sleep(1000);
                vm.keyPress(KeyEvent.VK_A);
                vm.keyRelease(KeyEvent.VK_A);
                vm.keyRelease(KeyEvent.VK_ALT);

                Thread.sleep(5000);

                //navigates file explorer popup
                for(int i = 0; i < 3; i++){
                    vm.keyPress(KeyEvent.VK_SHIFT);
                    vm.keyPress(KeyEvent.VK_TAB);
                    vm.keyRelease(KeyEvent.VK_TAB);
                    vm.keyRelease(KeyEvent.VK_SHIFT);
                    Thread.sleep(75);
                }

                Thread.sleep(3000);

                //types in "DESKTOP" and selects it
                String desktop = "DESKTOP";
                char[] deskArray = desktop.toCharArray();
                for (int i = 0; i < deskArray.length; i++){
                    vm.keyPress(deskArray[i]);
                    vm.keyRelease(deskArray[i]);
                    Thread.sleep(75);
                }

                Thread.sleep(3000);

                vm.keyPress(KeyEvent.VK_ENTER);
                vm.keyRelease(KeyEvent.VK_ENTER);

                Thread.sleep(3000);
                
                vm.keyPress(KeyEvent.VK_TAB);
                vm.keyRelease(KeyEvent.VK_TAB);

                Thread.sleep(3000);

                //types in "GruntWorx" and selects it
                String grunt = "GRUNTWORX";
                char[] gruntArray = grunt.toCharArray();
                for (int i = 0; i < gruntArray.length; i++){
                    vm.keyPress(gruntArray[i]);
                    vm.keyRelease(gruntArray[i]);
                    Thread.sleep(75);
                }
      
                Thread.sleep(3000);

                vm.keyPress(KeyEvent.VK_ENTER);
                vm.keyRelease(KeyEvent.VK_ENTER);

                // Thread.sleep(1000);
                
                // //types in clients name and selects it
                // char[] person = client.getName().toUpperCase().toCharArray();
                // for (int i = 0; i < person.length; i++){
                //     vm.keyPress(person[i]);
                //     vm.keyRelease(person[i]);
                //     Thread.sleep(75);
                // }

                // Thread.sleep(1000);

                // vm.keyPress(KeyEvent.VK_ENTER);
                // vm.keyRelease(KeyEvent.VK_ENTER);

                Thread.sleep(3000);

                //types in current year, and selects it
                char[] year = (workingYear.toString()).toCharArray();
                for (int i = 0; i < year.length; i++){
                    vm.keyPress(year[i]);
                    vm.keyRelease(year[i]);
                    Thread.sleep(75);
                }

                Thread.sleep(3000);

                vm.keyPress(KeyEvent.VK_ENTER);
                vm.keyRelease(KeyEvent.VK_ENTER);

                Thread.sleep(3000);

                //selects all available files in folder
                vm.keyPress(KeyEvent.VK_CONTROL);
                Thread.sleep(1000);
                vm.keyPress(KeyEvent.VK_A);
                vm.keyRelease(KeyEvent.VK_A);
                vm.keyRelease(KeyEvent.VK_CONTROL);

                Thread.sleep(3000);

                //adds the selected files
                vm.keyPress(KeyEvent.VK_ENTER);
                vm.keyRelease(KeyEvent.VK_ENTER);

                Thread.sleep(3000);

                System.out.println("Files added successfully.");

                //Enter email address
                for(int i = 0; i < 4; i++){
                    vm.keyPress(KeyEvent.VK_SHIFT);
                    vm.keyPress(KeyEvent.VK_TAB);
                    vm.keyRelease(KeyEvent.VK_TAB);
                    vm.keyRelease(KeyEvent.VK_SHIFT);
                    Thread.sleep(75);
                }

                //string saved to clipboard of email address
                str = new StringSelection(email);
                clipboard.setContents(str, null);

                Thread.sleep(1000);

                //paste new email address
                vm.keyPress(KeyEvent.VK_CONTROL);
                vm.keyPress(KeyEvent.VK_V);
                vm.keyRelease(KeyEvent.VK_V);
                vm.keyRelease(KeyEvent.VK_CONTROL);

                Thread.sleep(1000);

                System.out.println("Submitting Job into Gruntworx...");

                //Clicks submit button
                vm.keyPress(KeyEvent.VK_ALT);
                Thread.sleep(1000);
                vm.keyPress(KeyEvent.VK_S);
                vm.keyRelease(KeyEvent.VK_S);
                vm.keyRelease(KeyEvent.VK_ALT);

                //Start the bypass of the popups
                Thread.sleep(1000);

                //bypass first popup
                vm.keyPress(KeyEvent.VK_ENTER);
                vm.keyRelease(KeyEvent.VK_ENTER);

                Thread.sleep(9000);

                //bypass second popup
                vm.keyPress(KeyEvent.VK_ENTER);
                vm.keyRelease(KeyEvent.VK_ENTER);

                //JJ - Waiting for Gruntworx to load file selected to upload to its system - HAS TO BE LONG - THIS IS A LONG TIME!
                //60000
                Thread.sleep(120000); //2 minutes

                //GruntWorx load cancel
                vm.keyPress(KeyEvent.VK_ALT);
                Thread.sleep(1000);
                vm.keyPress(KeyEvent.VK_C);
                vm.keyRelease(KeyEvent.VK_C);
                vm.keyRelease(KeyEvent.VK_ALT);

                Thread.sleep(1000);

                System.out.println("Job Submitted.");

                //CLOSE GRUNTWORX
                System.out.println("Closing Gruntworx...");

                vm.keyPress(KeyEvent.VK_ALT);
                Thread.sleep(1000);
                vm.keyPress(KeyEvent.VK_X);
                vm.keyRelease(KeyEvent.VK_X);
                vm.keyRelease(KeyEvent.VK_ALT);

                System.out.println("Gruntworx Closed.");

                if(!client.isBroken()){
                    counter++;
                }

                //Delete Virtual Desktop Gruntworx folder
            System.out.println("Deleting Gruntworx folder from Virtual Desktop...");

            //after finishing, remove gruntworx folder from virtual machine desktop
            str = new StringSelection("rmdir %UserProfile%\\Desktop\\Gruntworx /S/Q");
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(str, null);

            Thread.sleep(1000);

            //pastes into cmd
            vm.keyPress(KeyEvent.VK_CONTROL);
            vm.keyPress(KeyEvent.VK_V);
            vm.keyRelease(KeyEvent.VK_V);
            vm.keyRelease(KeyEvent.VK_CONTROL);

            Thread.sleep(1000);

            //removes folder
            vm.keyPress(KeyEvent.VK_ENTER);
            vm.keyRelease(KeyEvent.VK_ENTER);

            Thread.sleep(4000);

            System.out.println("Deleted Gruntworx folder from Virtual Desktop.");

            } else {
                //if client did not appear in gruntworx directory on virtual machine desktop, continue to next client
                System.out.println("Client does not exist, continuing to next client.");
            }

        }

        //Delete Virtual Desktop Gruntworx folder
        System.out.println("Deleting Gruntworx folder from Virtual Desktop...");

        //after finishing, remove gruntworx folder from virtual machine desktop
        str = new StringSelection("rmdir %UserProfile%\\Desktop\\Gruntworx /S/Q");
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(str, null);

        Thread.sleep(1000);

        //pastes into cmd
        vm.keyPress(KeyEvent.VK_CONTROL);
        vm.keyPress(KeyEvent.VK_V);
        vm.keyRelease(KeyEvent.VK_V);
        vm.keyRelease(KeyEvent.VK_CONTROL);

        Thread.sleep(1000);

        //removes folder
        vm.keyPress(KeyEvent.VK_ENTER);
        vm.keyRelease(KeyEvent.VK_ENTER);

        Thread.sleep(4000);

        System.out.println("Deleted Gruntworx folder from Virtual Desktop.");
        addToCounter(counter);

        //Gruntworx folder deleted

        //if you want to Log Out of Virtual Desktop afterwards, uncomment the following code
    /*
        System.out.println("Logging off of Virtual Desktop...");

        str = new StringSelection("start C:\\Windows\\System32\\logoff.exe");
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(str, null);

        Thread.sleep(1000);

        vm.keyPress(KeyEvent.VK_CONTROL);
        vm.keyPress(KeyEvent.VK_V);
        vm.keyRelease(KeyEvent.VK_V);
        vm.keyRelease(KeyEvent.VK_CONTROL);

        Thread.sleep(1000);

        vm.keyPress(KeyEvent.VK_ENTER);
        vm.keyRelease(KeyEvent.VK_ENTER);

        System.out.println("Logged off of Virtual Desktop.");
    */

    }

    /**    
     * Creates log file of gruntworx uploads based on clients in the InputGrunt.txt file.
     * @return void
     */
    public void createLogFile(){
        //Create .txt with info. 
        ClientList.sort();
        for(int i = 0; i < ClientList.getClients().size(); i++){
            ClientList.getClients().get(i).readFullInput();
        }

        //gets current date and time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM.dd.yyy_HH.mm.ss"); 
        LocalDateTime now = LocalDateTime.now();  

        String currentTime = dtf.format(now);  

        //creates log file with date and time in name
        File logFile = new File(System.getProperty("user.dir") + File.separator + currentTime + "_Log_File.txt");


        try {
            logFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //writes the date and time in the log file
            FileWriter write = new FileWriter(logFile.getCanonicalFile(), false);
            PrintWriter printLine = new PrintWriter(write);
            printLine.printf("%s" + "%n", "Log created on " + currentTime + "\n" + "Using InputGrunt.txt as input file." + "\n");
            printLine.printf("%s" + "%n", "Successful Client Uploads to GruntWorx: ");
            
            //writes the successful client uploads to gruntworx
            for(int i = 0; i < ClientList.getApprovedClients().size(); i++){
                printLine.printf("%s" + "%n", ClientList.getApprovedClients().get(i));
            }

            printLine.printf("%s" + "%n", "");
            printLine.printf("%s" + "%n", "Broken Client List: ");
            
            //writes the broken clients that did not upload to gruntworx
            for(int i = 0; i < ClientList.getBrokenClients().size(); i++){
                printLine.printf("%s" + "%n", ClientList.getBrokenClients().get(i));
            }

            printLine.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addToCounter(Integer count) throws IOException{
        File inputFile = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "GruntPrintCounter.txt"); 
        //adds to counter
        FileReader fr = new FileReader(inputFile);   //reads the file  
        BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
        new StringBuffer();
        String number;  
        Integer runs = 0;
        while((number = br.readLine()) != null){
            System.out.println(number);
            runs = Integer.parseInt(number);
        }
        
        System.out.println(runs);
        runs = runs + count;
        br.close();
        fr.close();

        inputFile.delete();

        File newInputFile = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "GruntPrintCounter.txt"); 
        newInputFile.createNewFile();
        FileWriter write = new FileWriter(newInputFile.getCanonicalFile(), false);
        PrintWriter printLine = new PrintWriter(write);

        System.out.println(runs.toString());
        printLine.printf(runs.toString());

        printLine.close();
        write.close();  
        
    }     

     /**    
     * Moves gruntworx directory from main machine desktop to virtual machine desktop.
     * 
     * JJ AND BRADY - 2/19/23 - OLD FUNCTION THAT USED TO MOVE ALL CLIENTS!  NOT BEING USED/CALLED ANYMORE 
     * JUST KEEP HERE FOR RECORDS/IN CASE WE NEED IT AGAIN
     * @return void
     */
    public void moveDirectory(){
        Robot vm;
        try {
            vm = new Robot();

            
    
            //fullscreens verito after it is brought to focus
            // vm.keyPress(KeyEvent.VK_WINDOWS);
            // vm.keyPress(KeyEvent.VK_UP);
            // vm.keyRelease(KeyEvent.VK_UP);
            // vm.keyRelease(KeyEvent.VK_WINDOWS);

            Thread.sleep(2000);

            //TESTING MOVE DIRECTORY USING CMD
            //opens command prompt on virtual machine
            vm.keyPress(KeyEvent.VK_WINDOWS);
            Thread.sleep(1000);
            vm.keyPress(KeyEvent.VK_X);
            vm.keyRelease(KeyEvent.VK_X);
            vm.keyRelease(KeyEvent.VK_WINDOWS);
            Thread.sleep(3000);
            vm.keyPress(KeyEvent.VK_C);
            vm.keyRelease(KeyEvent.VK_C);

            //save to clipboard a string to be pasted into cmd on virtual machine which copies gruntworx folder over from main machine desktop to virtual machine desktop
            StringSelection str = new StringSelection("Xcopy \\\\tsclient\\C\\Users\\\"" + System.getProperty("user.name") + "\"\\Desktop\\Gruntworx\\ %UserProfile%" + File.separator + "\\Desktop\\Gruntworx /E/H/C/I");
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(str, null);

            Thread.sleep(1000);

            //pastes copied string into cmd
            vm.keyPress(KeyEvent.VK_CONTROL);
            vm.keyPress(KeyEvent.VK_V);
            vm.keyRelease(KeyEvent.VK_V);
            vm.keyRelease(KeyEvent.VK_CONTROL);

            Thread.sleep(1000);

            //enters string into cmd
            vm.keyPress(KeyEvent.VK_ENTER);
            vm.keyRelease(KeyEvent.VK_ENTER);

            //delay for copying files over
            //IF THIS IS TOO SHORT YOU CAN CHANGE THIS

            //multiply number of clients by 10 seconds - this gives program time to copy all files based on number of clients run in program
            int clientNum = ClientList.getClients().size();

            System.out.println("Number of Clients: " + clientNum);
            System.out.println("Copying client files from Z drive to load into Gruntworx");
            

            Thread.sleep(15000 * clientNum);

        } catch (AWTException | InterruptedException e) {
            e.printStackTrace();
        }

        
    }


}
