import java.io.IOException;


public class Example5 {
	public static void foo1() throws Exception // declaration/documentation
	{
		// foo1 found a file is missing
		throw new IOException("file missing"); // action
	}
	
	public static void foo2(){ // catches the exception
		try{
			foo1();
		}catch(Exception e){
			System.out.println("catch(Exception e)");
		}catch(IOException e){ // compilor error: superclass exception cataches must not 
								// be prior to subclass class catches
			System.out.println("catch(IOException e)");
		}
	}

}
