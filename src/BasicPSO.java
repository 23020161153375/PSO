/**   
* @Title: BasicPSOTest.java 
* @Package  
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2016-12-13
* @version V1.0   
*/

/**
* <p> BasicPSOTest</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2016-12-13
*/
public class BasicPSO extends PSO {

	public BasicPSO(){
		super(50,2,2,2,1000,0.1);
	}

	/** fitness = function(posBound,posBound) - function(p.position[0],p.position[1])    这里posBound = 1000
	 * @see PSO#fitness(Particle)
	 */
	@Override
	public double fitness(Particle p) {
		// TODO Auto-generated method stub
		return 2000000 - Math.pow(p.getPos(0), 2) - Math.pow(p.getPos(1), 2);
	}

	/** 
	* <p>测试函数，找最小值 </p> 
	* <p>function(x1,x2) = x1^2 +x2^2 </p> 
	* @param x1
	* @param x2
	* @return 
	*/
	public double function(double x1,double x2){
		return Math.pow(x1, 2) + Math.pow(x2, 2);
	}
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param args 
	*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println(Math.pow(3, 2));
		BasicPSO pso = new BasicPSO();
		pso.basicPSO();
		double[] optVar = pso.getOptimisedVar();
		System.out.println("优化后变量值为：");
		System.out.println(optVar[0] + " " + optVar[1]);
		System.out.println("结果为 " + pso.function(optVar[0],optVar[1]));;
	}

}
