package cn.edu.pku.EOSCN.crawler;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Path;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.edu.pku.EOSCN.RMIService.ClientCall;
import cn.edu.pku.EOSCN.business.ThreadManager;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.GitApiDownloader;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.URLReader;
import cn.edu.pku.EOSCN.entity.Project;

public class GitCommitsCrawler extends GitCrawler {
	private List<String> commitsJsonPaths = new LinkedList<String>();
	
	public GitCommitsCrawler() {
		// TODO Auto-generated constructor stub
	}
	public void crawl_url() throws Exception{
		String commitsUrl = 
				String.format("%s/%s",this.getApiBaseUrl(),"commits");
		int page = 1;
		while (true){
			String storagePath = 
					String.format("%s%c%s%d%s", 
							this.getStorageBasePath(),Path.SEPARATOR,
							"commits_index",page,".json");
			if (this.needLog){
				if (FileUtil.logged(storagePath)){
					this.commitsJsonPaths.add(storagePath);
					page++;
					continue;
				}
			}
			String url = String.format("%s?page=%d&", commitsUrl,page);
			//String url = String.format("%s?page=%d", commitsUrl,page);
			String content = GitApiDownloader.downloadOrin(url,null);
			if (content.length() == 0) continue;
			if (content.length() < 10) break; 
			this.commitsJsonPaths.add(storagePath);
			if (this.needLog){
				FileUtil.logging(storagePath);
			}
			FileUtil.write(storagePath, content);
			page ++;
		}
	}
	
	@Override
	public void crawl_middle(int id, Crawler crawler) {
		// TODO Auto-generated method stub
		GitCommitsCrawler gitCommitCrawler = (GitCommitsCrawler) crawler;
		StringBuffer s = new StringBuffer("");
		for (int i = 0; i < commitsJsonPaths.size(); i++){
			if (i % this.subCrawlerNum == id){
				s.append(this.commitsJsonPaths.get(i)+"\n");
				gitCommitCrawler.commitsJsonPaths.add(this.commitsJsonPaths.get(i));
			}
		}
		gitCommitCrawler.crawlTask = crawlTask;
		gitCommitCrawler.crawlTask.setEntrys(s.toString());
		gitCommitCrawler.crawlTask.isSlaver = true;
	}
	
	@Override
	public void crawl_data(){
		// TODO Auto-generated method stub
		ClientCall.callCrawlerCreater(this.crawlTask);
	}
	
	public static void main(String args[]) throws InterruptedException{
		Crawler crawl = new GitCommitsCrawler();
		Project project = new Project();
		ThreadManager.initCrawlerTaskManager();
		project.setOrgName("eclipse");
		project.setProjectName("eclipse");
		
		project.setName(String.format("%s-%s",project.getOrgName(), project.getProjectName()));
		crawl.setProject(project);
		crawl.needLog = true;
		crawl.crawlerType = Crawler.MAIN;
		crawl.setResourceType("GitCommits");
		ThreadManager.addCrawlerTask(crawl);
		crawl.join();
		ThreadManager.finishCrawlerTaskManager();
		System.out.println("ok1");
	}

}
