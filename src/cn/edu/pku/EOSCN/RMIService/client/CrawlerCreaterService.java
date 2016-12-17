package cn.edu.pku.EOSCN.RMIService.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

import cn.edu.pku.EOSCN.entity.CrawlerTask;

public interface CrawlerCreaterService extends Remote {
	public static final String SERVICE_ADDR = "rmi://localhost:9001/CrawlerCreaterService";
	public void start(CrawlerTask crawler)throws RemoteException;
}
