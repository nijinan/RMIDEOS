package cn.edu.pku.EOSCN.crawler.proxy;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.edu.pku.EOSCN.crawler.util.UrlOperation.HtmlDownloader;

public class ProxyPool {
	private static List<ProxyAddress> proxyPool = new LinkedList<ProxyAddress>();
	private static String IPProxy = "http://127.0.0.1:8000/?count=8&types=0";
	private static int FailedTimes = 0;
	private static final int times_threshold = 10;
	static {
		getProxyPool();
	}
	public static void getProxyPool() {
		// TODO Auto-generated constructor stub
		proxyPool.clear();
		FailedTimes = 0;
		String content = HtmlDownloader.downloadOrin(IPProxy, null,null);
		JSONArray jsarr= new JSONArray(content);
		for (int i = 0; i < jsarr.length(); i++){
			JSONObject jsobj = (JSONObject) jsarr.get(i);
			String ip = jsobj.getString("ip");
			int port = jsobj.getInt("port");
			proxyPool.add(new ProxyAddress(ip,port));
		}
	}
	public static ProxyAddress getProxyAddress(){
		int no = (int)(Math.random()*(proxyPool.size()));
		return proxyPool.get(no);
	}
	
	public static void reportFailed(){
		FailedTimes++;
		if (FailedTimes > times_threshold * proxyPool.size()){
			getProxyPool();
		}
	}
	
	public static void main(String args[]){
		ProxyPool.getProxyPool();
		for (ProxyAddress pa : proxyPool){
			System.out.println(pa.getIP()+":"+pa.getPort());
		}
	}
}
