import java.io.IOException;


public class Example4 {
	public static void foo1() throws Exception // declaration/documentation
	{
		// foo1 found a file is missing
		throw new IOException("file missing"); // action
	}
	
	public static void foo2(){ // catches the exception
		try{
			foo1();
		}catch(IOException e){
			System.out.println("catch(IOException e)");
		}catch(Exception e){ // try remove me and see what happens
			System.out.println("catch(Exception e)");
		}
	}

	public static void main(String args[]){
		foo2(); // prints "catch(IOException e)"
				// * foo1 declares Exception, but actually throws IOException
				// * foo2 needs to have catch statement for Exception
				// * but at runtime, JVM tries to find an exact match which is IOException
	}
}
