import java.io.Serializable;



public class Test2 extends Test1 implements Serializable{ 
	private int var2;
	
	public Test2(int var1, int var2){
		super(var1);
		this.var2 = var2;
	}
	
	public int getVar2(){
		return var2;
	}
}
