
import java.rmi.Naming;

import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;



public class AdditionServer {
	public static void main(String args[]){
		try{
			System.setSecurityManager(new RMISecurityManager());
			Addition Hello = new Addition();
			Naming.rebind("rmi://localhost/ABC", Hello);
//			Registry registry = LocateRegistry.getRegistry();
//			registry.rebind("rmi://localhost/ABC", Hello);
			
	//		AdditionInterface remote = (AdditionInterface)Naming.lookup("rmi://localhost/ABC");
	//		int result=remote.add(9,10);
	//		System.out.println("Result is :"+result);
		}catch(Exception e){
			System.err.println("Addition server failed"+ e);
		}
	}
}	
