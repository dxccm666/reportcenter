package edu.missouri.cf.projex4.scheduler.jobs;

public class JobClassLoader extends ClassLoader {

	public JobClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class loadClass(String name) throws ClassNotFoundException {
		return super.loadClass(name);
	}

}
