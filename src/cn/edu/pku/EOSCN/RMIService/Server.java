package cn.edu.pku.EOSCN.RMIService;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import cn.edu.pku.EOSCN.RMIService.server.CrawlerCreaterService;
import cn.edu.pku.EOSCN.RMIService.server.CrawlerCreaterServiceImpl;
import cn.edu.pku.EOSCN.business.ThreadManager;
 
public class Server {
 
    CrawlerCreaterService stub;
 
    public Server() {
        try {
        	stub = new CrawlerCreaterServiceImpl();
            LocateRegistry.createRegistry(9001);
            Naming.rebind(CrawlerCreaterService.SERVICE_ADDR, stub);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
 
    /**
     * @param args
     */
    public static void main(String[] args) {
    	ThreadManager.initCrawlerTaskManager();
        new Server();
    }
 
}
