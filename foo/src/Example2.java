import java.io.IOException;
import java.net.SocketException;


public class Example2 {// cannot use subclass exception handler to catch
						// superclass exception
	// background: 
	// 1)Exception is superclass of IOException, 
	// 2)IOException is superclass of SocketException
	
	
	public static void foo1() throws IOException // declaration/documentation
	{
		// foo1 found a file is missing
		throw new IOException("file missing"); // action
	}

	public static void foo2(){
		try{
			foo1(); // compilor error: subclass exception handler cannot 
					// catch a superclass exception
		}catch(SocketException e){
			System.exit(1);
		}
	}
	
}
