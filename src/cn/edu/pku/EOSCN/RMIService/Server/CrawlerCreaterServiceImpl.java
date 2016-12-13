package cn.edu.pku.EOSCN.RMIService.Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import cn.edu.pku.EOSCN.crawler.Crawler;

public class CrawlerCreaterServiceImpl extends UnicastRemoteObject implements CrawlerCreaterService {

	public CrawlerCreaterServiceImpl() throws RemoteException {
		// TODO Auto-generated constructor stub
		super();
	}

	@Override
	public void start(Crawler crawler) throws RemoteException{
		// TODO Auto-generated method stub

	}

}
