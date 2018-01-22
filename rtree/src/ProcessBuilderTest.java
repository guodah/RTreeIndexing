import java.io.IOException;


public class ProcessBuilderTest {
	public static void main(String args[]) throws IOException, InterruptedException{
		String separator = System.getProperty("file.separator");
		String classpath = System.getProperty("java.class.path");
		String path = System.getProperty("java.home")
                + separator + "bin" + separator + "java";
		System.out.println(path);
		ProcessBuilder processBuilder = 
                new ProcessBuilder(path, "-cp", 
                classpath, 
                Dummy.class.getName(), "arg0");
		Process process = processBuilder.start();
		System.out.println("main done");
//		System.out.println(process.waitFor());
	}
}
