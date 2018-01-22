
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class Addition extends UnicastRemoteObject
	implements AdditionInterface{

	public Addition() throws RemoteException {
		super();
	}
	
	public int add(int a, int b) throws RemoteException{
		int result = a+b;
		return result;
	}
	
	public void printVar1(Test2 t) throws RemoteException{
		System.out.println(t.getVar1());
	}
}
