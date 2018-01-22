import java.io.IOException;


public class Example3 {

	public static void foo1() throws Exception // ok to declare a supeclass exception
	{
		// foo1 found a file is missing
		throw new IOException("file missing"); // action
	}
	
	public static void foo2(){
		try{
			foo1(); // compilor error: the caller needs to catch
					// what the method declares (Exception), not what it actually throws
		}catch(IOException e){
			System.exit(1);
		}
	}
	
}
