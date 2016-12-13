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
		for (int i = 0; i < commitsJsonPaths.size(); i++){
			if (i % this.subCrawlerNum == id){
				gitCommitCrawler.commitsJsonPaths.add(this.commitsJsonPaths.get(i));
			}
		}
	}
	
	@Override
	public void crawl_data(){
		// TODO Auto-generated method stub
		for (String commitsJsonPath : this.commitsJsonPaths){
			String s = FileUtil.read(commitsJsonPath);
			JSONArray ja = new JSONArray(s);
			for (int i = 0; i < ja.length(); i++){
				JSONObject jo = (JSONObject)ja.get(i);
				String sha = jo.getString("sha");
				String commitsUrl = 
						String.format("%s/%s/%s?",this.getApiBaseUrl(),"commits",sha);
				String storagePath = 
						String.format("%s%c%s%c%s.json", 
								this.getStorageBasePath(),Path.SEPARATOR,
								"commits",Path.SEPARATOR,
								sha);
				if (this.needLog){
					if (FileUtil.logged(storagePath) && FileUtil.exist(storagePath)){
						continue;
					}else{
						String content = GitApiDownloader.downloadOrin(commitsUrl, null);
						FileUtil.write(storagePath, content);
						FileUtil.logging(storagePath);
					}
				}else{
					String content = GitApiDownloader.downloadOrin(commitsUrl, null);
					FileUtil.write(storagePath, content);
				}
			}
		}
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
		ThreadManager.addCrawlerTask(crawl);
		crawl.join();
		ThreadManager.finishCrawlerTaskManager();
		System.out.println("ok1");
	}

}
