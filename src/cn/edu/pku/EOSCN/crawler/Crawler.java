package cn.edu.pku.EOSCN.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import cn.edu.pku.EOSCN.business.CrawlerTaskManager;
import cn.edu.pku.EOSCN.business.ThreadManager;
import cn.edu.pku.EOSCN.entity.CrawlerTask;
import cn.edu.pku.EOSCN.entity.Project;
/**
 * 鎵�鏈夌埇铏渶瑕佸疄鐜拌鎶借薄绫讳腑鐨刢rawl鏂规硶
 * 瀛愮被涓繀椤诲疄鐜版棤鍙傛暟鐨勬瀯閫犳柟娉曪紝urllist鍜宲roject鍙橀噺鐢辩埇铏鐞嗘ā鍧楃粺涓�set
 * @author 寮犵伒绠�
 *
 */
public abstract class Crawler extends Thread{
	public static final int SUCCESS = 1;
	public static final int WAITING = 0;
	public static final int ERROR = 2;
	public static final int IN_PROGRESS = 3;
	
	public static final int MAIN = 0;
	public static final int SUB = 1;
	public static final int FULL = 2;
	
	public CrawlerTask crawlTask = new CrawlerTask();
	
	protected static final Logger logger = Logger.getLogger(Crawler.class.getName());
	public boolean needLog = false;
	public int crawlerType = MAIN;
	public int subCrawlerNum = 3;
	public int subCrawlerRun = 3;
	public int subid;
	public Crawler father;
	private String entrys;
	public boolean hostwating = false;
	public Project getProject() {
		return crawlTask.getProject();
	}

	public String getResourceType() {
		return crawlTask.getResourceType();
	}

	public void setResourceType(String resourceType) {
		crawlTask.setResourceType(resourceType);
	}

	public void setProject(Project project) {
		crawlTask.setProject(project);
	}
	
	public int getStatus() {
		return crawlTask.getStatus();
	}

	public void setStatus(int status) {
		crawlTask.setStatus(status);
	}

	
	public Crawler(){
		
	}
	
    @Override
    public final void run() {
    	setStatus(IN_PROGRESS);
        try {
        	this.init();
            this.Crawl();
            this.finish();    
		} catch (Exception e) {
			e.printStackTrace();
		}
    } 
    
    
	abstract public void init()throws Exception;
	abstract public void crawl_url()throws Exception;
	abstract public void crawl_middle(int id, Crawler crawler);
	abstract public void crawl_data();
	
	public final void Crawl() throws Exception{
		if (this.crawlerType == Crawler.FULL){
			this.crawl_url();
			this.crawl_data();
		}else if (this.crawlerType == Crawler.SUB){
			synchronized (father){
				father.subCrawlerRun --;
			}
			this.crawl_data();
		}else{
			this.crawl_url();
			List<Crawler> subs = new ArrayList<Crawler>(); 
			for (int i = 0; i < subCrawlerNum; i++){
				Crawler crawler = (Crawler)Class.forName(this.getClass().getName()).newInstance();
				crawler.crawlerType = Crawler.SUB;
				crawler.subid = i;				
				crawler.setProject(this.getProject());
				crawler.needLog = true;
				crawler.father = this;
				crawler.entrys = this.entrys;
				this.crawl_middle(i, crawler);
				subs.add(crawler);
				ThreadManager.addCrawlerTask(crawler);
			}
			if (hostwating){
				while (this.subCrawlerRun > 0){
					sleep(10000);
				}
				for (Crawler crawler : subs){
					crawler.join();
				}
			}
			
		}
		//this.finish();
	}

	public void setCrawleruuid(String crawleruuid) {
		crawlTask.setUuid(crawleruuid);
	}

	public String getCrawleruuid() {
		return crawlTask.getUuid();
	}
	
	public final void finish() {
		ThreadManager.removeFinishedTask(this);
		if (this.crawlerType == MAIN){
			//CrawlerTaskManager.reportCompletedTask(crawleruuid);
			next(); 
		}
		
	}
	public void next(){
		
	}

	public String getEntrys() {
		return entrys;
	}

	public void setEntrys(String entrys) {
		this.entrys = entrys;
	}
	
}
