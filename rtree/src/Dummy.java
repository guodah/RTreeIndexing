import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;

public class Dummy {
	public static void main(String args[]) throws FileNotFoundException, InterruptedException{
		
		Thread.sleep(1000);
		System.out.println(args[0]);
		System.out.println("dummy");
		
		Formatter format = new Formatter(new File("test"));
		format.format(args[0]);
		format.close();
	}
}
