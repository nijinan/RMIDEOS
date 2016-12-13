package cn.edu.pku.EOSCN.detect;

import cn.edu.pku.EOSCN.entity.Project;

public class Smeller {

	public Smeller() {
		// TODO Auto-generated constructor stub
	}

	public static Detector smellEntry(String page, String url, Project project) {
		Detector detector;
		try {
			detector = new MHonArcDetector(); 
			if (detector.detectEntry(url,page, project)){
				//detector.dispatch(url, project);
				return detector;
			}
			detector = new BugzillaDetector(); 
			if (detector.detectEntry(url,page, project)){
				//detector.dispatch(url, project);
				return detector;
			}			
			detector = new GoogleGroupDetector(); 
			if (detector.detectEntry(url,page, project)){
				//detector.dispatch(url, project);
				return detector;
			}			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void dispatch(String url, Project project, Detector detector){
		try {
			detector.dispatch(url, project);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean smell(String page, String url,Project project){
		boolean isothers = false;
		Detector detector;
		try {
			detector = new MHonArcDetector();
			if (detector.detect(url,page, project)){
				return true;
			}
			detector = new GoogleGroupDetector();
			if (detector.detect(url,page, project)){
				return true;
			}			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
}
