package cn.edu.pku.EOSCN.crawler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.eclipse.core.runtime.Path;

import cn.edu.pku.EOSCN.business.InitBusiness;
import cn.edu.pku.EOSCN.business.ThreadManager;
import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.HtmlDownloader;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.URLExtractor;
import cn.edu.pku.EOSCN.detect.Detector;
import cn.edu.pku.EOSCN.detect.Smeller;
import cn.edu.pku.EOSCN.entity.CrawlerURL;
import cn.edu.pku.EOSCN.entity.Project;

public class EmailSiteCrawler extends Crawler {
	private static int maxdepth = 2;
	private String storageBasePath;
	private String webUrl;
	private Queue<CrawlerURL> urlQueue = new LinkedList<CrawlerURL>();
	private HashSet<String> visitURLSet = new HashSet<String>();
	private HashSet<String> smellURLSet = new HashSet<String>();
	public int runningNum = 0;
	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		storageBasePath = String.format("%s%c%s%c%s", 
				Config.getTempDir(),
				Path.SEPARATOR,
				this.getProject().getName(),
				Path.SEPARATOR,
				this.getClass().getName());
	
		//webUrl = "https://lucene.apache.org/";
		/*TODO get the webUrl*/
	}

	public HashSet<String> getVisitURLSet() {
		return visitURLSet;
	}

	
	@Override
	public void crawl_url() throws Exception {
		/* get the webUrl from google or do nothing */
		CrawlerURL crawlerUrl = new CrawlerURL();
		crawlerUrl.setUrl(webUrl);
		crawlerUrl.setDepth(0);
		crawlerUrl.setDocName("root");
		urlQueue.add(crawlerUrl);
	}
	public void crawl_middle(int id, Crawler crawler){

	}
	@Override
	public void crawl_data(){
		// TODO Auto-generated method stub
		Queue<CrawlerURL> q = ((EmailSiteCrawler)father).getUrlQueue();
		boolean first = true;
		while (true){
			CrawlerURL url;
			synchronized (q){
				if (first) ((EmailSiteCrawler)father).runningNum++;
				first = false;
				url = this.fetchUrl();
				if (url == null) {
					break;
				}
			}
			System.out.println("Thread" + this.subid + "  " + url.getUrl());
			String storagePath = 
					String.format("%s%c%s", 
							this.getStorageBasePath(),Path.SEPARATOR ,
							HtmlDownloader.url2path(url.getUrl()));
			String html = "";
			if (url.getUrl().contains("google")){
				System.out.println(url.getUrl());
			}
			if (this.needLog){
				if (FileUtil.logged(storagePath) && FileUtil.exist(storagePath)){
					html = FileUtil.read(storagePath);
				}else {
					html = HtmlDownloader.downloadOrin(url.getUrl(),null,null);
					FileUtil.write(storagePath,html);
					FileUtil.logging(storagePath);
				}
			}else{
				html = HtmlDownloader.downloadOrin(url.getUrl(),null,null);
				FileUtil.write(storagePath,html);
			}
			if (html == null) continue;
			if (Smeller.smell(html,url.getUrl(), project)){
				System.out.println("smell ++ :" + url.getUrl());
				Detector detector = Smeller.smellEntry(html,url.getUrl(), project);
				if (detector != null){
					if (!this.hasSmell(url)){
						this.addSmell(url);
						Smeller.dispatch(url.getUrl(), project, detector);
					}
				}
				continue;
			}
			if (url.getDepth() >= this.maxdepth) continue;
			List<CrawlerURL> urls = URLExtractor.getAllUrls(html, url.getUrl(), "");
			//System.out.println(html);
			for (CrawlerURL u : urls){
				if (!this.hasUrl(u)){
					u.setDocName(u.getUrl().replaceAll("[<>\\/:*?]", ""));
					if (u.getUrl().contains("google")){
						System.out.println(url.getUrl());
					}
					u.setDepth(url.getDepth() + 1);
					if (Smeller.smell("",u.getUrl(), project)){
						System.out.println("smell ++ :" + u.getUrl());
						Detector detector = Smeller.smellEntry("",u.getUrl(), project);
						if (detector != null){
							if (!this.hasSmell(u)){
								this.addSmell(u);
								Smeller.dispatch(u.getUrl(), project, detector);
							}
						}
					}
					if (HtmlDownloader.getHost(u.getUrl()).contains(project.getHostUrl())){
						this.addUrl(u);
					}
				}
			}
		}		
	}

	
	public CrawlerURL fetchUrl(){
		Queue<CrawlerURL> q = ((EmailSiteCrawler)father).getUrlQueue();
		CrawlerURL ret;
		//synchronized (q){
			while (q.isEmpty()){
				((EmailSiteCrawler)father).runningNum --;
				if (((EmailSiteCrawler)father).runningNum == 0) {
					q.notifyAll();
					return null;
				};
				try {
					q.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					q.notifyAll();
					return null;
				}
				((EmailSiteCrawler)father).runningNum ++;
			}
			ret = q.poll();
		//}
		return ret;
	}
	
	public boolean hasSmell(CrawlerURL url){
		HashSet<String> set = ((EmailSiteCrawler)father).getSmellURLSet();
		return set.contains(url.getUrl());		
	}

	public void addSmell(CrawlerURL url){
		Queue<CrawlerURL> q = ((EmailSiteCrawler)father).getUrlQueue();
		HashSet<String> set = ((EmailSiteCrawler)father).getSmellURLSet();
		synchronized (q){
			if (!set.contains(url.getUrl())){
				//q.add(url);
				set.add(url.getUrl());
				q.notify();
			}
		}
	} 
	
	public boolean hasUrl(CrawlerURL url){
		HashSet<String> set = ((EmailSiteCrawler)father).getVisitURLSet();
		return set.contains(url.getUrl());
	}
	
	public void addUrl(CrawlerURL url){
		Queue<CrawlerURL> q = ((EmailSiteCrawler)father).getUrlQueue();
		HashSet<String> set = ((EmailSiteCrawler)father).getVisitURLSet();
		synchronized (q){
			if (!set.contains(url.getUrl())){
				q.add(url);
				set.add(url.getUrl());
				q.notify();
			}
		}
	} 
	
	public String getStorageBasePath() {
		return storageBasePath;
	}

	public void setStorageBasePath(String storageBasePath) {
		this.storageBasePath = storageBasePath;
	}
	public static void main(String[] args) throws Exception{
		Crawler crawl = new EmailSiteCrawler();
		Project project = new Project();
		//InitBusiness.initEOS();
		ThreadManager.initCrawlerTaskManager();
		project.setOrgName("mozilla");
		project.setProjectName("mozilla");
		project.setName("mozilla");
		project.setHostUrl("lists.mozilla.org");
		crawl.setProject(project);
		crawl.needLog = true;
		crawl.hostwating = true;
		((EmailSiteCrawler)crawl).webUrl = "https://lists.mozilla.org/listinfo";
		crawl.crawlerType = Crawler.MAIN;
		
		
		ThreadManager.addCrawlerTask(crawl);
		crawl.join();
		ThreadManager.finishCrawlerTaskManager();
		System.out.println("ok1");
	}

	public Queue<CrawlerURL> getUrlQueue() {
		return urlQueue;
	}

	public HashSet<String> getSmellURLSet() {
		return smellURLSet;
	}

	public void setSmellURLSet(HashSet<String> smellURLSet) {
		this.smellURLSet = smellURLSet;
	}


}
