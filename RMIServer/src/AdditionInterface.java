
import java.rmi.*;

public interface AdditionInterface extends Remote{
	public int add(int a, int b) throws RemoteException;
	public void printVar1(Test2 t) throws RemoteException;
}
