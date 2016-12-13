package cn.edu.pku.EOSCN.detect;

import cn.edu.pku.EOSCN.entity.Project;

public abstract class Detector {

	public Detector() {
		// TODO Auto-generated constructor stub
	}
	
	abstract public boolean detect(String url, String content, Project project) throws Exception;
	abstract public boolean detectEntry(String url, String content, Project project) throws Exception;
	abstract public void dispatch(String url, Project project) throws Exception;
}
