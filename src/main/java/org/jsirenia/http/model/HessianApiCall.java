package org.jsirenia.http.model;


import java.io.Serializable;
import java.util.List;

public class HessianApiCall implements Serializable{
	private static final long serialVersionUID = 1L;
	private String className;
	private String methodName;
	private String moduleName;
	private String serviceUri;
	private List<Object> args;
	private List<String> argTypes;
	//private String hessianApiUri;
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public List<Object> getArgs() {
		return args;
	}
	public void setArgs(List<Object> args) {
		this.args = args;
	}
	/*public String getHessianApiUri() {
		return hessianApiUri;
	}
	public void setHessianApiUri(String hessianApiUri) {
		this.hessianApiUri = hessianApiUri;
	}*/
	public List<String> getArgTypes() {
		return argTypes;
	}
	public void setArgTypes(List<String> argTypes) {
		this.argTypes = argTypes;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getServiceUri() {
		return serviceUri;
	}
	public void setServiceUri(String serviceUri) {
		this.serviceUri = serviceUri;
	}
}
