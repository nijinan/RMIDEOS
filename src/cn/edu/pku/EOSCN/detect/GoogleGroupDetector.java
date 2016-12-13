package cn.edu.pku.EOSCN.detect;

import cn.edu.pku.EOSCN.business.ThreadManager;
import cn.edu.pku.EOSCN.crawler.Crawler;
import cn.edu.pku.EOSCN.crawler.GoogleGroupCrawler;
import cn.edu.pku.EOSCN.crawler.MHonArcCrawler;
import cn.edu.pku.EOSCN.entity.Project;

public class GoogleGroupDetector extends Detector {

	public GoogleGroupDetector() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean detect(String url, String content, Project project) throws Exception {
		// TODO Auto-generated method stub
		if (url.contains("groups.google")) return true;
		return false;
	}

	@Override
	public boolean detectEntry(String url, String content, Project project) throws Exception {
		// TODO Auto-generated method stub
		if (url.contains("groups.google") && url.contains("group")) return true;
		return false;
	}

	@Override
	public void dispatch(String url, Project project) throws Exception {
		// TODO Auto-generated method stub
		Crawler crawl = new GoogleGroupCrawler();
		crawl.setProject(project);
		crawl.needLog = true;
		String name = url.substring(url.lastIndexOf("/")+1);
		((GoogleGroupCrawler)crawl).setForumName(name);
		crawl.crawlerType = Crawler.MAIN;
		ThreadManager.addCrawlerTask(crawl);
	}

}
