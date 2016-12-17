/**   
* @Title: BasicPSOTest.java 
* @Package  
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2016-12-13
* @version V1.0   
*/

/**
* <p> 基础版粒子群优化算法</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2016-12-13
*/
public class BasicPSO extends PSO {

	public BasicPSO(){
		super(50,2,2,2,1000,0.1);
	}
	
	public BasicPSO(int amountOfParticles,int dimension,double c1,double c2,double posBound,double velBoundFactor){
		super(amountOfParticles, dimension, c1, c2, posBound, velBoundFactor);
	}


	@Override
	public double fitness(double[] var) {
		// TODO Auto-generated method stub
		return 100000 / (1 +fTablet(var) );
		
	}
	
	public static double fTablet(double[] var){
		double result = 1000000 * var[0] * var[0];
		for(int i = 1; i < var.length;i ++)
			result += var[i] * var[i];
		return result;
	}
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param args 
	*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println(Math.pow(3, 2));
		//BasicPSO pso = new BasicPSO();
		BasicPSO pso = new BasicPSO(100,30,1.5,1.5,100,0.01);		
		pso.basicPSO(6000);
		double[] optVar = pso.getOptimisedVar();
		System.out.println("优化后变量值为：");
		//System.out.println(optVar[0] + " " + optVar[1]);
		for(int i = 0; i < optVar.length;i ++)
			System.out.print(optVar[i] + " ");	
		//System.out.println("结果为 " + pso.function(optVar[0],optVar[1]));;
		System.out.println("\n最终结果：" + fTablet(optVar));		
	}

}
