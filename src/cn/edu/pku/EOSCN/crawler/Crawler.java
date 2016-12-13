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
	
	private CrawlerTask crawlTask;
	
	protected static final Logger logger = Logger.getLogger(Crawler.class.getName());
	protected String crawleruuid = UUID.randomUUID().toString();
	private String resourceType;
	public boolean needLog = false;
	public int crawlerType = FULL;
	public int subCrawlerNum = 4;
	public int subCrawlerRun = 4;
	public int subid;
	public boolean isServerTask = true;
	public Crawler father;
	private int status = WAITING;
	private int percentage = 0;
	private String entrys;
	public boolean hostwating = false;
	protected Project project = null;
	public Project getProject() {
		return project;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}
	
	/**
	 * @author 寮犵伒绠�
	 * 榛樿鏋勯�犳柟娉�
	 * 娴嬭瘯鏃惰璋冪敤甯﹀弬鏁扮殑鏋勯�犳柟娉�
	 */
	public Crawler(){
		
	}
	
    @Override
    public final void run() {
        status = IN_PROGRESS;
        try {
        	this.init();
            this.Crawl();
            this.finish();    
		} catch (Exception e) {
			e.printStackTrace();
		}
    } 
    

	public void addPercentageBy(int i) {
		percentage += i;
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
				crawler.project = this.project;
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
		this.crawleruuid = crawleruuid;
	}

	public String getCrawleruuid() {
		return crawleruuid;
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
	public CrawlerTask toCrawlerTask(){
		CrawlerTask crawlerTask = new CrawlerTask();
		crawlerTask.setUuid(crawleruuid);
		crawlerTask.setStatus(status);
		crawlerTask.setEntrys(entrys);
		crawlerTask.setResourceType(resourceType);
		crawlerTask.setProjectUuid(project.getUuid());
		return crawlerTask;
	}

	public String getEntrys() {
		return entrys;
	}

	public void setEntrys(String entrys) {
		this.entrys = entrys;
	}
	
}
