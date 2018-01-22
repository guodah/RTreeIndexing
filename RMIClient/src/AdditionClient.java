
import java.rmi.*;

 
public class AdditionClient {
	public static void main (String[] args) {
		AdditionInterface hello;
		try {
  		    System.setSecurityManager(new RMISecurityManager());
			hello = (AdditionInterface)Naming.lookup("rmi://localhost/ABC");
			int result=hello.add(9,10);
			System.out.println("Result is :"+result);
			hello.printVar1(new Test2(10,10));
			}catch (Exception e) {
				System.out.println("HelloClient exception: " + e);
				}
		}
}