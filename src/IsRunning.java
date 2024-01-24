
import com.sun.jna.platform.win32.User32;

import com.sun.jna.platform.win32.WinDef.HWND;



public class IsRunning {


	/**    
     * Checks if Verito is running. 
	 * If so, Verito is brought to focus.
	 * If not, prints out that Verito is not running.
     * @return boolean of Verito's running status
     */
	public static boolean running() {

        HWND hwnd = User32.INSTANCE.FindWindow("TscShellContainerClass", null); // window class name

		if (hwnd == null) {

			System.out.println("Verito is not running");
            return false;
		}

		else{

			User32.INSTANCE.ShowWindow(hwnd, 9 );        // SW_RESTORE

			User32.INSTANCE.SetForegroundWindow(hwnd);   // bring to front

            System.out.println("Verito brought to focus");
            
            return true;

		}

	}

}
