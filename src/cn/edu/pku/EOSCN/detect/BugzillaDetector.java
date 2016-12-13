package cn.edu.pku.EOSCN.detect;

import cn.edu.pku.EOSCN.entity.Project;

public class BugzillaDetector extends Detector {

	public BugzillaDetector() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean detect(String url, String content, Project project) throws Exception {
		// TODO Auto-generated method stub
		if (content.contains("query.cgi")) return true;
		return false;
	}
	
	@Override
	public boolean detectEntry(String url, String content, Project project) throws Exception {
		// TODO Auto-generated method stub
		if (content.contains("query.cgi")) return true;
		return false;
	}
	
	@Override
	public void dispatch(String url, Project project) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
