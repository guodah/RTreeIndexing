package com.geofeedia.networks;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIAgentInterface extends Remote{
	public String bind() throws RemoteException;
}
