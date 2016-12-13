package cn.edu.pku.EOSCN.crawler.util.UrlOperation;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import cn.edu.pku.EOSCN.crawler.proxy.ProxyAddress;
import cn.edu.pku.EOSCN.crawler.proxy.ProxyPool;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;

public class GitApiDownloader {
	private static List<String> gitTokens = new LinkedList<String>();
	static {
		gitTokens.add("client_id=fa5191bf55e754d6d25b&client_secret=226fc9193e753ca8f69fd8d9279577e4a9c5448c");
	}
	private static String gitToken = "client_id=fa5191bf55e754d6d25b&client_secret=226fc9193e753ca8f69fd8d9279577e4a9c5448c";

	protected static final Logger logger = Logger.getLogger(GitApiDownloader.class.getName());
	
	public void init(){
		
	}
	public static File downloadFromUrl(String url, String storagePath) {
		String tempPath = storagePath;
		//File tempFile = FileUtil.createFile(tempPath,url.substring(url.lastIndexOf('/') + 1));
		File tempFile = FileUtil.createFile(tempPath);
		logger.info("download url content to file path : " + tempPath);
		
		try {
			URL httpurl = new URL(url);
			FileUtils.copyURLToFile(httpurl,tempFile);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempFile;
	}
	
	private static void changeToken(){
		int id = gitTokens.indexOf(gitToken);
		id++;
		if (id == gitTokens.size()){
			id = 0;
		}
		gitToken = gitTokens.get(id);
	}

	public static String downloadOrin(String _url, Map<String, List<String>> _headers){
		int times = 0;
		int timesP = 20;
		String content;
		//ProxyAddress proxyAddress = ProxyPool.getProxyAddress();
		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		while (true){
			String url = _url + gitToken;
			content = HtmlDownloader.downloadOrin(url,headers,null);
			List<String> list = headers.get("X-RateLimit-Remaining");
			String remain = null;
			if (list != null) remain = list.get(0);
			System.out.println(remain);
			if ((remain != null)&&(remain.equals("0"))){
				times++;
				if (times < gitTokens.size()) {
					changeToken();
					continue;
				}
				try {
					System.out.println("Token has full!");
					Thread.sleep(60*1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				if (content.length() <= 0){
					timesP++;
					//proxyAddress = ProxyPool.getProxyAddress();
					if (timesP < 8) continue;
				}
				break;
			}
		}
		return content;
	}
}
