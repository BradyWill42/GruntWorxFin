import java.lang.*;
import java.nio.file.Path;
import java.util.Scanner;

import javax.swing.JOptionPane;

import java.io.*;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;

public class Main{
	public static void main(String[] args) throws IOException,
                           AWTException, InterruptedException,
						   UnsupportedFlavorException{


		//creates gruntworx object
		gruntWorx grunt = new gruntWorx();
		grunt.yearPrompt();
		grunt.moveGruntFiles();

		Scanner input = new Scanner(System.in);
		System.out.println("Please choose Locate Type to use: \n 1 - Verito isRunning() Find Method \n 2 - Alt Tab method");
		Integer choice = input.nextInt();

		while(choice != 1 && choice != 2){
			System.out.println("Please choose a valid input.");
			choice = input.nextInt();
		}

		String email;
		System.out.println("Please choose User to use: \n 1 - JJ O'Connor \n 2 - Jenna");
		Integer user = input.nextInt();

		while(user != 1 && user != 2){
			System.out.println("Please choose a valid input.");
			user = input.nextInt();
		}
		input.close();

		if(user == 2){
			email = "email2";
		} else {
			email = "email1";
		}


		if(choice == 1){
			boolean cont = IsRunning.running();

			//Checks to make sure Verito is up and running before continuing
			if(cont){
				//OLD!  USED TO MOVE ALL CLIENTS AT ONCE - NOW JUST DOING ONE CLIENT AT A TIME
				//grunt.moveDirectory();
				grunt.gruntworxLogin(email);
				grunt.createLogFile();
			}
		} else {

			//This is the ALT TAB way to start the program! (JJ)
		
			Robot vm = new Robot();
		
			vm.keyPress(KeyEvent.VK_ALT);
			vm.keyPress(KeyEvent.VK_TAB);
			vm.keyRelease(KeyEvent.VK_TAB);
			vm.keyRelease(KeyEvent.VK_ALT);
			
			//OLD!  USED TO MOVE ALL CLIENTS AT ONCE - NOW JUST DOING ONE CLIENT AT A TIME
			//grunt.moveDirectory();
			grunt.gruntworxLogin(email);
			grunt.createLogFile();

			//This is the END of the ALT TAB way to start the program! (JJ)
		}
		
		
		//clears and deletes gruntworx directory on main machine desktop
		ClearDir clear = new ClearDir();
		File gruntDir = new File(System.getProperty("user.home") + "\\Desktop\\Gruntworx");
		if(gruntDir.exists()){
			clear.deleteDirectoryRecursion(gruntDir.toPath());
		} else {
			System.out.println("Gruntworx Folder does not exist.");
		}

		JOptionPane.showMessageDialog(null, "Check Log File", "DONE", 0);
	}
	
}