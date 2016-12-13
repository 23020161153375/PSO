import java.util.Random;

/**   
* @Title: Particle.java 
* @Package  
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2016-12-13
* @version V1.0   
*/

/**
* <p> Particle</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2016-12-13
*/
public class Particle implements Comparable<Particle>{
	private Random random;

	private double fitness;

	private static int dimension;		
	private double[] position;	
	private double[] velocity;
	
	private static double c1 = 2,c2 = 2;
	static double posBound,velBoundFactor = 0.5;
	private double[] pBestPos;
	private double pBestFitness;
	
	public Particle(long seed){
		position = new double[dimension];
		velocity = new double[dimension];
		pBestPos = new double[dimension];
		
		random = new Random(seed);
	}
	
	public static void paramInit(int dimension,double c1,double c2,double posBound,double velBoundFactor){
		Particle.dimension = dimension;
		Particle.c1 = c1;
		Particle.c2 = c2;
		Particle.posBound = posBound;
		Particle.velBoundFactor = velBoundFactor;
	}
	
	/** 
	* <p>在给定范围内给位置矢量赋随机值</p> 
	* <p>Description: </p>  
	*/
	public void init(){
		for(int i = 0; i < dimension;i ++){
			position[i] = (random.nextDouble() * 2 - 1) * posBound; 
			//velocity[i] = (random.nextDouble() * 2 - 1) * posBound * velBoundFactor;
		}
		//setCurrentPosAsPersonalBest();
	}
	
	/** 
	* <p>将粒子当前位置设为个体最好位置</p> 
	* <p>该操作包括把个体的当前适应度做为个体最佳适应度，所以在调用该方法前应确保已经自行更新了当前的适应度。</p>  
	*/
	public void setCurrentPosAsPersonalBest(){
		for(int i = 0;i < dimension;i ++)
			pBestPos[i] = position[i];
		pBestFitness = fitness;
	} 
	
	public void updateVel(double[] gBestPos){
		for(int i = 0;i < dimension;i  ++){
			velocity[i] = velocity[i] 
					+ c1 * random.nextDouble() * (pBestPos[i] - position[i]) 
					+ c2 * random.nextDouble() * (gBestPos[i] - position[i]);
		}
		limitVel();
	}
	
	private void limitVel(){
		for(int i = 0;i < dimension;i ++)
			if(velocity[i] > posBound * velBoundFactor)
				velocity[i] = posBound * velBoundFactor;
			else if(velocity[i] <  (-1) *posBound * velBoundFactor)
				velocity[i] = (-1) * posBound * velBoundFactor;
	}
	
	/** 
	* <p>根据粒子速度更新粒子当前位置</p> 
	* <p>注意该方法并没有自动更新适应度值，而且也没有更新个体最佳历史位置，这些都需要另行设置。 </p>  
	*/
	public void updatePos(){
		for(int i = 0;i < dimension;i ++)
			position[i] += velocity[i];
		limitPos();
	}
	
	private void limitPos(){
		for(int i = 0;i < dimension;i ++)
			if(position[i] > posBound)
				position[i] = posBound;
			else if(position[i] < -posBound)
				position[i] = -posBound;
	}
	
	public void setVel(int d,double value){
		this.velocity[d] = value;
	}
	
	
	public double getVel(int d){
		return velocity[d];
	}
	
	public void setPos(int d,double value){
		this.position[d] = value;
	}
	
	public double getPos(int d){
		return position[d];
	}
	
	public double[] getPos(){
		return position;
	}

	/**
	 * @return the fitness
	 */
	public double getFitness() {
		return fitness;
	}

	/**
	 * @param fitness the fitness to set
	 */
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	/**
	 * @return the pBestFitness
	 */
	public double getPBestFitness() {
		return pBestFitness;
	}

	/** (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Particle o) {
		// TODO Auto-generated method stub
		double temp = fitness - o.fitness;
		if(temp > 0)
			return 1;
		else if(temp == 0)
			return 0;
		else
			return -1;
	}
	
	
	
	
}
