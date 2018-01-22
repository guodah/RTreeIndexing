import java.io.IOException;


public class Example6 {
	public static void foo1() {
		try{
			System.out.println("no exception at all");
		}catch(IOException e){ // compiler error: no throw, no catch
			System.exit(1);
		}
	}

}
