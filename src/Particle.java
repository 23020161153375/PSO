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
* <p>在粒子群优化算法中提供基础的粒子模型。使用该类有以下几点需要注意： </p>
* <br>1 构造粒子对象之前，需要使用{@link #paramInit(int, double, double, double, double)} 配置粒子所处空间维度、学习因子以及粒子活动范围、粒子速度范围这些所有粒子共有的信息。</br>
* <br>2 调用函数更新粒子速度和位置的过程中，不会自动更新粒子的适应度以及粒子个体历史最佳位置的记录。这些需要另外设置。</br>
* <br>3 调用{@link #setCurrentPosAsPersonalBest()} 函数可以直接把当前粒子所处位置和其对应的适应度作为个体历史最佳记录。（不管它实际上是不是最最佳记录）<br>
* @author FlyingFish
* @date 2016-12-13
*/
public class Particle implements Comparable<Particle>{
	
	//提供粒子的“个性”
	public Random random;

	private double fitness;

	/*Particle类的静态属性*/
	//粒子所处空间的维度
	static int dimension = 1;
	
	//个体经验、社会经验的学习因子
	static double c1 = 2,c2 = 2;
	
	//每一维空间上的位置范围[-posBound,posBound]
	//每一维空间的速度的变化范围[-posBound * velBoundFactor,posBound * velBoundFactor]
	static double posBound = 1,velBoundFactor = 0.5;
	
	//粒子的位置矢量
	private double[] position;	
	
	//粒子的速度矢量
	private double[] velocity;
	
	//个体历史最佳位置
	private double[] pBestPos;
	
	//个体历史最佳位置的适应度
	private double pBestFitness;
	
	/** 
	* <p>构造函数</p> 
	* @param seed 随机数种子
	*/
	public Particle(long seed){
		position = new double[dimension];
		velocity = new double[dimension];
		pBestPos = new double[dimension];
		
		random = new Random(seed);
	}
	
	/** 
	* <p>配置粒子群的静态参数</p> 
	* <p>该函数应该在构造粒子对象以前调用</p> 
	* @param dimension 粒子所处空间的维度
	* @param c1 个体经验学习因子
	* @param c2 社会经验学习因子
	* @param posBound 每一维空间上的粒子的位置 |P|< posBound
	* @param velBoundFactor 每一维空间上粒子速度 |V| < posBound * velBoundFactor

	*/
	public static void paramInit(int dimension,double c1,double c2,double posBound,double velBoundFactor){
		Particle.dimension = dimension;
		Particle.c1 = c1;
		Particle.c2 = c2;
		
		//posBound，velBoundFactor 不允许为负值
		Particle.posBound = Math.abs(posBound);
		Particle.velBoundFactor = Math.abs(velBoundFactor);
	}
	
	/** 
	* <p>在允许范围内给位置和速度赋随机值。一般在初始化时调用，但这里并不会默认把当前位置作为个体历史最佳位置记录下来。</p> 
	* <p>Description: </p>  
	*/
	public void init(){
		for(int i = 0; i < dimension;i ++){
			position[i] = (random.nextDouble() * 2 - 1) * posBound; 
			velocity[i] = (random.nextDouble() * 2 - 1) * posBound * velBoundFactor;
		}
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
	
	/** 
	* <p>更新速度</p> 
	* <p>更新公式中是不带惯性权重的。更新需要提供现在该粒子需要学习的位置，这个位置既可以是现在粒子群能找到的全局最佳位置，也可能只是目前该粒子和它邻居所能找到的局部最佳位置。</p> 
	* @param gBestPos 最佳位置
	*/
	public void updateVel(double[] gBestPos){
		for(int i = 0;i < dimension;i  ++){
			velocity[i] = velocity[i] 
					+ c1 * random.nextDouble() * (pBestPos[i] - position[i]) 
					+ c2 * random.nextDouble() * (gBestPos[i] - position[i]);
		}
		limitVel();
	}
	
	/** 
	* <p>限制速度</p> 
	* <p>Description: </p>  
	*/
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
	
	/** 
	* <p>限制位置 </p> 
	* <p>Description: </p>  
	*/
	private void limitPos(){
		for(int i = 0;i < dimension;i ++)
			if(position[i] > posBound)
				position[i] = posBound;
			else if(position[i] < -posBound)
				position[i] = -posBound;
	}
	
	/** 
	* <p>设置新速度 </p> 
	* <p>Description: </p> 
	* @param d 维度
	* @param value 值
	*/
	public void setVel(int d,double value){
		this.velocity[d] = value;
		
		//限速
		if(velocity[d] > posBound * velBoundFactor)
			velocity[d] = posBound * velBoundFactor;
		else if(velocity[d] <  (-1) *posBound * velBoundFactor)
			velocity[d] = (-1) * posBound * velBoundFactor;
		
	}
	
	public double getVel(int d){
		return velocity[d];
	}
	
	public void setPos(int d,double value){
		this.position[d] = value;
		
		if(position[d] > posBound)
			position[d] = posBound;
		else if(position[d] < -posBound)
			position[d] = -posBound;
			
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

	/**
	 * @return the pBestPos
	 */
	public double[] getPBestPos() {
		return pBestPos;
	}

	/** 比较两个粒子适应度的大小
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
	
	public String toString(){
		String info = "位置: " + showArray(position) + "\t速度: " + showArray(velocity) 
		+ "\n个体最佳位置:" + showArray(pBestPos) + "\t当前适应度：" + fitness ;
		return info;
	}
	
	private static String showArray(double[] array){
		StringBuilder s = new StringBuilder("[");
		for(int i = 0; i < array.length-1;i ++)
			s.append(array[i] +", ");
		s.append(array[array.length - 1] + "]");
		return s.toString();
	}
	
}
