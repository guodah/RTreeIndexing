import java.io.IOException;


public class Example1 { // demonstrates declare-or-catch
	public static void foo1() throws IOException // declaration/documentation
	{
		// foo1 found a file is missing
		throw new IOException("file missing"); // action
	}
	
	public static void foo2(){ // catches the exception
		try{
			foo1();
		}catch(IOException e){
			System.exit(1);
		}
	}
	
	public static void foo3() throws IOException // declaration/documentation
	{
		foo1();
	}
}
