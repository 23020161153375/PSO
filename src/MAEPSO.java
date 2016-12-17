/**   
* @Title: MAEPSO.java 
* @Package  
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2016-12-14
* @version V1.0   
*/

/**
* <p> MAEPSO</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2016-12-14
*/
public class MAEPSO extends PSO {
	
	public MAEPSO(){
		super(50,2,2,2,1000,0.1,3);
	}
	
	public MAEPSO(int amountOfParticles,int dimension,double c1,double c2,
			double posBound,double velBoundFactor,int numberOfSwarm){
		super(amountOfParticles,dimension,c1,c2,posBound,velBoundFactor,numberOfSwarm);
	}
	
	/** fitness = 100000 / (1 +fTablet(var) )
	 * @see PSO#fitness(double[])
	 */
	@Override
	public double fitness(double[] var) {
		// TODO Auto-generated method stub
		return 100000 / (1 +fTablet(var) );
	}	

	/** 
	* <p>测试函数 </p> 
	* <p>测试目标是找该函数的最小值 </p> 
	* @param var 多维变量
	* @return 函数值
	*/
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
		//MAEPSO pso = new MAEPSO();
		//MAEPSO pso = new MAEPSO(4,2,1.4,1.4,100,0.1,2);
		//pso.novelMAEPSO(1000);
		MAEPSO pso1 = new MAEPSO(100,30,1.5,1.5,100,0.01,5);
		pso1.reviseEscapePolicy(0.5, 10, 2);
		pso1.novelMAEPSO(6000);

		double[] optVar = pso1.getOptimisedVar();
		System.out.println("\n优化后变量值为：");
		for(int i = 0; i < optVar.length;i ++)
			System.out.print(optVar[i] + " ");
		System.out.println("\n最终结果：" + fTablet(optVar));
		

		
	}



}
