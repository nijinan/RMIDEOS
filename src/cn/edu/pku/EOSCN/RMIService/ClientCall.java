package cn.edu.pku.EOSCN.RMIService;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import cn.edu.pku.EOSCN.RMIService.server.CrawlerCreaterService;
import cn.edu.pku.EOSCN.crawler.Crawler;
import cn.edu.pku.EOSCN.entity.CrawlerTask;

public class ClientCall {

	public ClientCall() {
		// TODO Auto-generated constructor stub
	}
	public static void callCrawlerCreater(CrawlerTask crawler){
		CrawlerCreaterService stub;
		try {
			stub = (CrawlerCreaterService) Naming.lookup(CrawlerCreaterService.SERVICE_ADDR);
			stub.start(crawler);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
