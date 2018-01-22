import java.io.Serializable;



public class Test1 implements Serializable{
	private int var1;
	public Test1(){
		
	}
	public Test1(int var1){
		this.var1 = var1;
	}
	
	public int getVar1(){
		return var1;
	}

}
