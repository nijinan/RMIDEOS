package cn.edu.pku.EOSCN.RMIService.Client;

import java.rmi.Remote;
import java.rmi.RemoteException;

import cn.edu.pku.EOSCN.crawler.Crawler;

public interface CrawlerCreaterService extends Remote {
	public void start(Crawler crawler)throws RemoteException;
}
