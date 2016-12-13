/**   
* @Title: TestPSO.java 
* @Package  
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2016-12-13
* @version V1.0   
*/

/**
* <p> TestPSO</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2016-12-13
*/
public class TestPSO {

	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param args 
	*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] amountOfParticles = { 50 ,100,47,60};
		int[] numberOfSwarm = {5,18,47,9};
		for(int i = 0;i < 4;i ++){
			System.out.println("粒子总数 " + amountOfParticles[i] + " 粒子群数 " + numberOfSwarm[i] );
			setupSwarm(amountOfParticles[i],numberOfSwarm[i]);
		}
	}

	public static void  setupSwarm( int amountOfParticles,int numberOfSwarm){
		
		//得到每个粒子群的粒子数目
		int membersPerSwarm ;
		int benefit = amountOfParticles  % numberOfSwarm;
		
		//决定每个粒子群取哪一些粒子
		int begin = 0,end ;
		for(int i = 0;i < numberOfSwarm;i ++){
			if(benefit -- > 0)
				membersPerSwarm = amountOfParticles  / numberOfSwarm +1;
			else
				membersPerSwarm = amountOfParticles  / numberOfSwarm ;
			
			end = begin + membersPerSwarm - 1;
			
			//分配成员
			System.out.print("["+ begin + " , " + end + "]" + "  ");
			begin = end + 1;	
			}		
			
		System.out.println();		
	}
	
}
