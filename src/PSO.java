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
* <p> PSO</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2016-12-13
*/
public abstract class PSO {
	
	protected Particle[] particles;	
	protected int currentGen ;
	protected int dimension;
	protected double c1,c2;
	protected double posBound;
	protected double velBoundFactor;
	private Particle gBestPosParticle;
	
	protected ParticleSwarm[] swarm;
	protected int numberOfSwarm;
	//protected int indexOFMax,indexOfMin;
	protected  double[] thredholds;
	

	
	
	protected PSO(int amountOfParticles,int dimension,double c1,double c2,double posBound,double velBoundFactor){
		particles = new Particle[amountOfParticles];
		
		//配置整个粒子群的共有信息
		Particle.paramInit(dimension,c1, c2, posBound, velBoundFactor);
		for(int i = 0;i < amountOfParticles;i ++){
			
			//创建新粒子
			//particles[i] = new Particle(System.currentTimeMillis());
			particles[i] = new Particle(i);
			
			//给粒子赋初值
			particles[i].init();
			
			//设置适应度
			particles[i].setFitness(fitness(particles[i]));
			
			//初始时把当前位置设为历史最佳位置
			particles[i].setCurrentPosAsPersonalBest();
		}
		//完成所有粒子的初始化后，找出整个粒子群中最优秀的个体
		findCurrentGlobalBest();
		
		//初始代数为1
		currentGen = 1;
	}
	
	protected PSO(int amountOfParticles,int dimension,double c1,double c2,double posBound,double velBoundFactor,int numberOfSwarm){
		this(amountOfParticles, dimension, c1, c2, posBound, velBoundFactor);
				
		if(amountOfParticles <= 0 ||numberOfSwarm <= 0 || numberOfSwarm > amountOfParticles  )
			throw new IllegalArgumentException("参数设置有误：总粒子数目 " + amountOfParticles + " 粒子群数目 " + numberOfSwarm);
		
		this.numberOfSwarm = numberOfSwarm;
		swarm = new ParticleSwarm[numberOfSwarm];	
		
		setupSwarm();
		
		//初始时sigma统一设置为posBound
		for(int i = 0;i < numberOfSwarm;i ++)
			swarm[i].setSigma(posBound);
		
		
	}
	
	
	public void novelMAEPSO(int totalGen){
		for(;currentGen <= totalGen;currentGen ++){
			particlesMove();
			setupSwarm();
			updateSigma();
		}
		
		
	}
	
	/** 
	* <p>建立子群 </p> 
	* <p>为每一个粒子群分配成员,并计算出子群的适应度，但不包括计算sigma值 </p>  
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
			
			//计算子群适应度
			swarm[i].calcSwarmFitness();	
			
			begin = end + 1;	
		}			
	}
	
	private void particlesMove(){
		for(int j = 0;j < particles.length;j ++){
			//更新粒子速度
			particles[j].updateVel(gBestPosParticle.getPos());
			
			//更新粒子位置
			particles[j].updatePos();
			
			//更新粒子适应度
			particles[j].setFitness(fitness(particles[j]));
			
			//更新粒子历史最优位置			
			if(particles[j].getFitness() > particles[j].getPBestFitness())//如果适应度提高了
				particles[j].setCurrentPosAsPersonalBest();
		}
	}
	
	private void updateSigma(){
		//分别找出所有子群中有最小、最大适应度并计算总子群适应度
		//int indexMin = 0,indexMax = 0;
		double fitnessMin = swarm[0].getSwarmFitness();
		double fitnessMax = swarm[0].getSwarmFitness();
		double sumFitness = 0;
		for(int i = 1;i < swarm.length;i ++){
			double sfitness = swarm[i].getSwarmFitness();
			if(sfitness < fitnessMin){
				fitnessMin = sfitness;
				//indexMin = i;
			}
			
			if(sfitness > fitnessMax){
				fitnessMax = sfitness;
				//indexMax = i;
			}
			
			sumFitness += sfitness;
		}
		
		//计算每个子群的sigma值
		for(int i = 0;i < swarm.length;i ++){
			double power = (swarm.length * swarm[i].getSwarmFitness() - sumFitness) 
												/ (fitnessMax - fitnessMin);		
			double newSigma = swarm[i].getSigma() * Math.exp(power);
			swarm[i].setSigma(newSigma);
		}	
	}
	
	private void mutate(){
		
	}

	
	public abstract  double fitness(Particle p);
		
	public void basicPSO(){
		for(int gen = 2;gen < 101;gen ++){//一共进化到100代
			for(int j = 0;j < particles.length;j ++){
				//更新粒子速度
				particles[j].updateVel(gBestPosParticle.getPos());
				
				//更新粒子位置
				particles[j].updatePos();
				
				//更新粒子适应度
				particles[j].setFitness(fitness(particles[j]));
				
				//更新粒子历史最优位置			
				if(particles[j].getFitness() > particles[j].getPBestFitness())//如果适应度提高了
					particles[j].setCurrentPosAsPersonalBest();
			}
			//找出该代最优秀的个体
			findCurrentGlobalBest();
			
			//代数加一
			currentGen++;
		}
		
	}
	
	/** 
	* <p>找出整个粒子群中最优秀的个体 </p> 
	* <p>Description: </p>  
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
	
	public double[] getOptimisedVar(){
		return gBestPosParticle.getPos();
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
