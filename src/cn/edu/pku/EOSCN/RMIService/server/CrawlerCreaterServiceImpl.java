package cn.edu.pku.EOSCN.RMIService.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import cn.edu.pku.EOSCN.business.ThreadManager;
import cn.edu.pku.EOSCN.crawler.Crawler;
import cn.edu.pku.EOSCN.entity.CrawlerTask;

public class CrawlerCreaterServiceImpl extends UnicastRemoteObject implements CrawlerCreaterService {

	public CrawlerCreaterServiceImpl() throws RemoteException {
		// TODO Auto-generated constructor stub
		super();
	}

	@Override
	public void start(CrawlerTask crawler) throws RemoteException{
		// TODO Auto-generated method stub
		Crawler crawl = crawler.toCrawler();
		System.out.println(crawl.getResourceType());
		ThreadManager.addCrawlerTask(crawl);
	}

}
