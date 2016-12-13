package cn.edu.pku.EOSCN.crawler.proxy;

public class ProxyAddress {
	private String IP = "127.0.0.1";
	private int Port = 80;
	public ProxyAddress(String IP, int Port) {
		// TODO Auto-generated constructor stub
		this.setIP(IP);
		this.setPort(Port);
	}
	public String getIP() {
		return IP;
	}
	public void setIP(String iP) {
		IP = iP;
	}
	public int getPort() {
		return Port;
	}
	public void setPort(int port) {
		Port = port;
	}
	
}
