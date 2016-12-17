import java.util.Arrays;

/**   
* @Title: PSO.java 
* @Package  
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2016-12-13
* @version V1.0   
*/

/**
* <p> 粒子群优化算法</p>
* <p>实现了两个版本的框架。一个是基础版本的，主要特征有更新速度时以当前全局最佳位置作为单个粒子的社会经验来源 ，整个过程没有粒子变异。</p>
*<p>另一个是带粒子变异（逃逸）的版本，会对粒子按适应度进行分群，每个粒子会学习所在子群的当前最佳位置。关于这个版本的具体细节参见附注。</p>
*<p>为了使用该算法，只需要继承该类，并实现抽象方法{@link #fitness(double[])} ，再执行所选框架的对应方法即可。<p>
* @see 陶新民, 刘福荣, 刘玉,等. 一种多尺度协同变异的粒子群优化算法[J]. 软件学报, 2012, 23(7):1805-1815.
* @author FlyingFish
* @date 2016-12-13
*/
public abstract class PSO {
	
	//所有的粒子
	protected Particle[] particles;

	//标志particles数组中哪个位置对应哪个子群
	private int[] swarmIndex;
	
	//当前世代
	protected int currentGen ;
	
	//用于控制粒子群的参数
	protected int dimension;
	protected double c1,c2;
	protected double posBound;
	protected double velBoundFactor;
	
	//当前世代占据最佳位置的粒子
	private Particle gBestPosParticle;
	
	//存放最终优化的变量
	private double[] gBestPos;
	
	//子群
	protected ParticleSwarm[] swarm;
	
	//子群数目
	protected int numberOfSwarm;
	
	
	/** 
	* <p>构造函数</p> 
	* <p>用于基础版的粒子群优化算法 </p> 
	* @param amountOfParticles 总的粒子数
	* @param dimension 维度
	* @param c1 个体经验学习因子
	* @param c2 社会经验学习因子
	* @param posBound 位置范围
	* @param velBoundFactor 速度范围因子
	*/
	protected PSO(int amountOfParticles,int dimension,double c1,double c2,double posBound,double velBoundFactor){
		particles = new Particle[amountOfParticles];
		
		//保存参数
		this.dimension = dimension;
		this.c1 = c1;
		this.c2 = c2;
		this.posBound = posBound;
		this.velBoundFactor = velBoundFactor;
		
		//配置整个粒子群的共有信息
		Particle.paramInit(dimension,c1, c2, posBound, velBoundFactor);
		for(int i = 0;i < amountOfParticles;i ++){
			
			//创建新粒子
			//particles[i] = new Particle(System.currentTimeMillis());
			particles[i] = new Particle(i);
			
			//给粒子赋初值
			particles[i].init();
			
			//设置适应度
			particles[i].setFitness(fitness(particles[i].getPos()));
			
			//初始时把当前位置设为历史最佳位置
			particles[i].setCurrentPosAsPersonalBest();
		}
		//完成所有粒子的初始化后，找出整个粒子群中最优秀的个体
		findCurrentGlobalBest();
		
		//初始代数为1
		currentGen = 1;
	}
	
	/** 
	* <p>构造函数</p> 
	* <p>用于变异版的粒子群优化算法</p> 
	* @param amountOfParticles 总的粒子数
	* @param dimension 维度
	* @param c1 个体经验学习因子
	* @param c2 社会经验学习因子
	* @param posBound 位置范围
	* @param velBoundFactor 速度范围因子
	* @param numberOfSwarm 子群数
	*/
	protected PSO(int amountOfParticles,int dimension,double c1,double c2,double posBound,double velBoundFactor,int numberOfSwarm){
		this(amountOfParticles, dimension, c1, c2, posBound, velBoundFactor);
		
		if(amountOfParticles <= 0 ||numberOfSwarm <= 0 || numberOfSwarm > amountOfParticles  )
			throw new IllegalArgumentException("参数设置有误：总粒子数目 " + amountOfParticles + " 粒子群数目 " + numberOfSwarm);
		
		//声明swarmIndex
		swarmIndex = new int[amountOfParticles];
		
		this.numberOfSwarm = numberOfSwarm;
		swarm = new ParticleSwarm[numberOfSwarm];	
	
		//得到每个粒子群的粒子数目
		int membersPerSwarm ;
		
		//增益
		int benefit = amountOfParticles  % numberOfSwarm;
		
		int begin = 0,end ;
		for(int i = 0;i < numberOfSwarm;i ++){
			if(benefit -- > 0)//抽屉原理
				membersPerSwarm = amountOfParticles  / numberOfSwarm +1;
			else
				membersPerSwarm = amountOfParticles  / numberOfSwarm ;
			
			end = begin + membersPerSwarm - 1;
			
			//从begin到end这段粒子属于第i个粒子群
			for(int j = begin;j <= end;j ++ )
				swarmIndex[j] = i;
			
			//建立子群，赋初始sigma值为posBound
			swarm[i] = new ParticleSwarm(membersPerSwarm,posBound);
								
			begin = end + 1;	
		}
		
		//给每个种群分配成员分配成员
		setupSwarm();
		
		espController = new EscapeController();
	}
	
	
	/** 
	* <p>适应度函数 </p> 
	* <p>适应度函数用于评价一个粒子位置的好、坏。具体而言，我们约定，如果适应度函数返回的值越高，说明当前粒子越接近最优解，也就越好。反之如果它返回的值越小，可能粒子离最优解越远，给粒子的评价就越低。适应度函数是一个非负函数。</p> 
	* @param var 带优化的变量，也就是粒子的位置变量
	* @return 非负的适应度值
	*/
	public abstract  double fitness(double[] var);
	
	public void novelMAEPSO(int totalGen){
		//showInitState();
		for(;currentGen <= totalGen;currentGen ++){
			
			//粒子运动
			particlesMove();
			//showMoveInfo();
			
			//粒子逃逸（变异）
			particlesEscape();
			//System.out.println(espController);			
			
			//建立子群
			setupSwarm();
			
			//更新Sigma值
			updateSigma();
			
			//找到这一代位置最佳的粒子
			//this.findCurrentGlobalBest();
		}
		
		/*从所有粒子的个体历史最佳位置中找到全局历史最佳位置，作为最后输出的结果*/
		double best = Double.MIN_VALUE;
		int bestIndex = -1;
		for(int i = 0;i < particles.length;i ++)
			if(particles[i].getPBestFitness() > best){
				best = particles[i].getPBestFitness() ;
				bestIndex = i;
			}
		gBestPos = new double[dimension];
		
		//最终的结果存放在gBestPos中
		System.arraycopy(particles[bestIndex].getPBestPos(), 0, gBestPos, 0, dimension);		
	}
	
	/** 
	* <p>建立子群 </p> 
	* <p>为每一个粒子群分配成员,并计算出子群的适应度，以及找出子群中最优秀的个体。但不包括计算sigma值 </p>  
	*/
	private void setupSwarm(){
		int amountOfParticles = particles.length;
		
		//对所有粒子按适应度排序
		Arrays.sort(particles);
		
		//得到每个粒子群的粒子数目
		int membersPerSwarm ;
		
		//增益
		int benefit = amountOfParticles  % numberOfSwarm;
		
		int begin = 0,end ;
		for(int i = 0;i < numberOfSwarm;i ++){
			if(benefit -- > 0)//抽屉原理
				membersPerSwarm = amountOfParticles  / numberOfSwarm +1;
			else
				membersPerSwarm = amountOfParticles  / numberOfSwarm ;
			
			end = begin + membersPerSwarm - 1;
			
			
			//分配成员
			swarm[i].setupMembers(particles, begin, end);
			
			//找出子群中最优秀的个体
			swarm[i].findCurrentGlobalBest();
			
			//计算子群适应度
			swarm[i].calcSwarmFitness();	
			
			begin = end + 1;	
		}			
	}
	
		
	/** 
	* <p>粒子运动 </p> 
	* <p>Description: </p>  
	*/
	private void particlesMove(){
		for(int j = 0;j < particles.length;j++){

			//更新粒子位置
			//这里先更新位置，再更新速度
			//可以充分利用速度变异来提高最优解精度
			particles[j].updatePos();
			
			//更新粒子速度
			//注意到这里使用的是粒子所属种群的最优个体
			particles[j].updateVel(swarm[swarmIndex[j]].getGlobleBestPos());
			//particles[j].updateVel(gBestPosParticle.getPos());			
			
			//更新粒子适应度
			particles[j].setFitness(fitness(particles[j].getPos()));
			
			//更新粒子历史最优位置			
			if(particles[j].getFitness() > particles[j].getPBestFitness())//如果适应度提高了
				particles[j].setCurrentPosAsPersonalBest();
		}
	}
	
	/** 
	* @Fields espController : TODO(粒子逃逸控制器) 
	*/ 
	private EscapeController espController;
	/** 
	* <p>粒子逃逸</p> 
	* <p>Description: </p>  
	*/
	private void particlesEscape(){
		espController.particlesEscape();
	}
	
	/** 
	* <p>更新Sigma值</p> 
	* <p>Description: </p>  
	*/
	private void updateSigma(){
		//分别找出所有子群中有最小、最大适应度并计算总子群适应度
		double fitnessMin = swarm[0].getSwarmFitness();
		double fitnessMax = swarm[0].getSwarmFitness();
		double sumFitness = 0;
		for(int i = 1;i < swarm.length;i ++){
			double sfitness = swarm[i].getSwarmFitness();
			if(sfitness < fitnessMin){
				fitnessMin = sfitness;
			}
			
			if(sfitness > fitnessMax){
				fitnessMax = sfitness;
			}
			
			sumFitness += sfitness;
		}
		
		//计算每个子群的sigma值
		System.out.print("Sigma：");
		for(int i = 0;i < swarm.length;i ++){
			double power = (swarm.length * swarm[i].getSwarmFitness() - sumFitness) 
												/ (fitnessMax - fitnessMin);		
			double newSigma = swarm[i].getSigma() * Math.exp(power);
			
			//显示Sigma变化
			System.out.print(ParticleSwarm.normaliseSigma(newSigma) + " ");
			
			//将规范化后的sigma赋给子群
			swarm[i].setSigma(ParticleSwarm.normaliseSigma(newSigma));
		}
		System.out.println();
	}
	
	/**
	* <p> EscapeController</p>
	* <p>粒子逃逸（变异）控制器。注意禁止在PSO各参数还未赋好值时创建 </p>
	* @author FlyingFish
	* @date 2016-12-14
	*/
	private class EscapeController{
		//逃逸阈值
		protected  double[] thredholds;			

		//临时的逃逸空间
		private double[] escapePosition ;
		
		//记录每一维度上粒子低于门阈值得累计值
		private int[] acumBellowTherdholdTimes; 
		
		//控制门阈值下降速度的变量
		private int k1,k2;
		
		public EscapeController(){
			this(0.5,50,2);
		}

		public EscapeController(double thredholdsFactor,int k1,int k2){
			thredholds = new double[dimension];
			for(int i = 0;i < dimension;i ++)
				thredholds[i] = thredholdsFactor * posBound * velBoundFactor;
			escapePosition = new double[dimension];
			acumBellowTherdholdTimes = new int[dimension];
			this.k1 = k1;
			this.k2 = k2;	
		}
		
		
		public void particlesEscape(){
			//在所有的高斯变异算子中，利用标准高斯正态分布进行试逃逸得到的点的最大适应度
			double maxFitnessND = Double.MIN_NORMAL,temp;
			
			//利用标准高斯正态分布进行试逃逸得到的点在空间第d维上的位置
			double maxFitnessNDEscapePosDth =0;
					
			for(int d = 0; d < dimension;d ++){
				for(int i = 0;i < particles.length;i ++){
					
					//第i个粒子在第d维空间上的速度
					double vid = particles[i].getVel(d);
					if(Math.abs(vid ) < thredholds[d]){//如果这个速度小于在第d维空间上的阈值
						
						//第d维空间上累计速度变异次数加1
						acumBellowTherdholdTimes[d] ++;
						
						//依次使用所有的高斯变异算子进行试逃逸
						//选取其中具有最差适应度的点
						for(int j = 0; j < swarm.length;j ++){
							temp = randomlyEscapeWithND(particles[i],swarm[j].getSigma());
							if(temp > maxFitnessND){//DEBUG
								maxFitnessND = temp;
								
								//注意到每次试逃逸的位置都临时存放在escapePosition里面	
								maxFitnessNDEscapePosDth = escapePosition[d];
							}					
						}
						
						//利用均匀分布进行试逃逸
						temp = randomlyEscapeWithAD(particles[i]);
						
						//将两者中适应度差的点在d维空间上的（试）逃逸增量赋给该点在第d维空间上的速度
						if(maxFitnessND > temp){
							//System.out.printf("速度变异(d,i,vid,newVid)=(%3d,%3d,%15.10f,%15.10f)) \n",d,i,vid,maxFitnessNDEscapePosDth - particles[i].getPos(d));
							particles[i].setVel(d, maxFitnessNDEscapePosDth - particles[i].getPos(d));
						}else	{//注意到每次试逃逸的位置都临时存放在escapePosition里面	
							//System.out.printf("速度变异(d,i,vid,newVid)=(%3d,%3d,%15.10f,%15.10f)) \n",d,i,vid,escapePosition[d] - particles[i].getPos(d));
							particles[i].setVel(d, escapePosition[d] - particles[i].getPos(d));
						}			
					}
				}
				//第d维阈值自适应下降
				if(acumBellowTherdholdTimes[d] > k1){
					acumBellowTherdholdTimes[d] = 0;
					thredholds[d] = thredholds[d] / k2;
				}			
			}//for(d)		
		}
		
		/** 
		* <p>利用标准高斯正态分布进行试逃逸 </p> 
		* <p>逃逸后的结果，也就是粒子的位置存放在<Code>EscapeController.escapePosition</Code>里面</p> 
		* @param p
		* @param sigma
		* @return 
		*/
		private double randomlyEscapeWithND (Particle p,double sigma){
			for(int d = 0; d < dimension;d ++){
				escapePosition[d] = p.getPos(d) + p.random.nextGaussian() * sigma;			
			}
			
			return fitness(escapePosition);
		}
		
		/** 
		* <p>利用均匀分布进行试逃逸</p> 
		* <p>逃逸后的结果，也就是粒子的位置存放在<Code>EscapeController.escapePosition</Code>里面</p> 
		* @param p
		* @return 
		*/
		private double randomlyEscapeWithAD(Particle p){
			for(int d = 0; d < dimension;d ++){
				escapePosition[d] = p.getPos(d) + posBound * velBoundFactor * p.random.nextDouble();			
			}		
			return fitness(escapePosition);
		}		
		
		public String toString(){
			return new String("阈值：" + PSO.showArray(thredholds) 
			+ "\tG值：" + PSO.showArray(acumBellowTherdholdTimes)
			+"\n K1，K2：" + k1 + " " + k2);
		}		
	}
	
	/** 
	* <p>变更逃逸相关的参数 </p> 
	* <p>当粒子的某一维速度低于逃逸速度阈值时，粒子会在该维空间上重新获得一个逃逸速度。另外，逃逸速度阈值是自适应下降的，参数k1,k2用来控制其下降的节奏。</p> 
	* @param factorT 逃逸速度阈值 = factorT * posBound * velBoundFactor
	* @param k1 k1越大，阈值下降的间隔越长
	* @param k2 k2越大，阈值一次下降得越明显
	*/
	public void reviseEscapePolicy(double factorT,int k1,int k2){
		espController = new EscapeController(factorT,k1,k2);
	}

	

		
	/** 
	* <p>基础本的粒子群优化算法的执行方法 </p> 
	* <p>Description: </p> 
	* @param totalGen 
	*/
	public void basicPSO(int totalGen){
		for(;currentGen <= totalGen;currentGen ++){
			for(int j = 0;j < particles.length;j ++){
				//更新粒子速度
				particles[j].updateVel(gBestPosParticle.getPos());
				
				//更新粒子位置
				particles[j].updatePos();
				
				//更新粒子适应度
				particles[j].setFitness(fitness(particles[j].getPos()));
				
				//更新粒子历史最优位置			
				if(particles[j].getFitness() > particles[j].getPBestFitness())//如果适应度提高了
					particles[j].setCurrentPosAsPersonalBest();
			}
			//找出该代最优秀的个体
			findCurrentGlobalBest();
			
			//代数加一
			currentGen++;
		}
		double best = Double.MIN_VALUE;
		int bestIndex = -1;
		for(int i = 0;i < particles.length;i ++)
			if(particles[i].getPBestFitness() > best){
				best = particles[i].getPBestFitness() ;
				bestIndex = i;
			}
		gBestPos = new double[dimension];
		System.arraycopy(particles[bestIndex].getPBestPos(), 0, gBestPos, 0, dimension);
		
	}



	/** 
	* <p>得到最终优化好的变量</p> 
	* <p>也就是最后粒子群找到的最佳位置 </p> 
	* @return 
	*/
	public double[] getOptimisedVar(){
		return gBestPos;
	}
	
	/** 
	* <p>找出这一代整个粒子群中最优秀的个体 </p> 
	* <p>结果会被放到{@linkplain #gBestPosParticle}}中存储，它可以作为单个粒子的全局社会经验来源。 </p>  
	*/
	private void findCurrentGlobalBest(){
		double maxFitness = particles[0].getFitness();
		int indexOfElite = 0;
		for(int i = 1;i < particles.length;i ++)
			if(particles[i].getFitness() > maxFitness){
				maxFitness = particles[i].getFitness();
				indexOfElite = i;
			}
		gBestPosParticle = particles[indexOfElite];		
	}
	
	/*以下是测试组函数*/
	
	private void showInitState(){
		System.out.println("初始状态");
		System.out.println("静态参数：" );
		System.out.println("Particle(d,c1,c2,posb,vbf) " 
		+ Particle.dimension +" " + Particle.c1 + " " + Particle.c2 + " " + Particle.posBound + " " + Particle.velBoundFactor) ;
		System.out.println("PSO(d,c1,c2,posb,vbf) " 
		+ dimension +" " +c1 + " " + c2 + " " + posBound + " " + velBoundFactor) ;
		System.out.println("粒子");
		for(int i = 0; i < particles.length;i ++)
			System.out.println(particles[i]);
		System.out.println("子群");
		for(int i = 0;i < swarm.length;i ++)
			System.out.println(swarm[i]);
		System.out.println("标签");;
		System.out.println(showArray(swarmIndex));
		System.out.println("变异控制器 ");;
		System.out.println(espController);		
	}
	
	private void showMoveInfo(){
		System.out.println("\n更新后的粒子状态：");
		for(int i = 0; i < particles.length;i ++)
			System.out.println(particles[i]);		
	}
	
	private  static String showArray(int[] array){
		StringBuilder s = new StringBuilder("[");
		for(int i = 0; i < array.length-1;i ++)
			s.append(array[i] +", ");
		s.append(array[array.length - 1] + "]");
		return s.toString();
	}
	
	private static String showArray(double[] array){
		StringBuilder s = new StringBuilder("[");
		for(int i = 0; i < array.length-1;i ++)
			s.append(array[i] +", ");
		s.append(array[array.length - 1] + "]");
		return s.toString();
	}
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param args 
	*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(Math.exp(1));
		
	}

}
