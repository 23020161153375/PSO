
/**
* <p> ParticleSwarm</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2016-12-13
*/
public class ParticleSwarm {
	private int amountOfMembers;
	private double swarmFitness;
	private Particle gBestPosParticle;
	
	private Particle[] particles;
	private double sigma;
	
	public ParticleSwarm(int amount,double sigma){
		this.amountOfMembers = amount;
		this.sigma = sigma;
		particles = new Particle[amount];	
	}
	
	public void setupMembers(Particle[] srcParticles,int begin,int end ){
		if(end - begin + 1 != amountOfMembers)
			throw new IllegalArgumentException("加入 "+ (end - begin + 1) + " 个成员，与初始化时粒子群有  "+  amountOfMembers + "个成员不符@");
		
		for(int i = begin;i <= end ;i ++ )
			//浅复制
			particles[i - begin] = srcParticles[i] ;		
	}
	
	/** 
	* <p>计算种群适应度</p> 
	* <p>Description: </p>  
	*/
	public void calcSwarmFitness(){
		double fitness = 0;
		for(int i = 0;i < particles.length;i ++)
			fitness += particles[i].getFitness();
		swarmFitness =  fitness / amountOfMembers;
	}
		
	private void normalisedSigma(){
		double w = Particle.posBound * 2;
		if(sigma > w / 4)
			sigma %= w / 4;		
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

	
	/**
	 * @return the sigma
	 */
	public double getSigma() {
		return sigma;
	}

	/**
	 * @param sigma the sigma to set
	 */
	public void setSigma(double sigma) {
		this.sigma = sigma;
	}

	/**
	 * @return the swarmFitness
	 */
	public double getSwarmFitness() {
		return swarmFitness;
	}	
	
	public double[] getGlobleBestPos(){
		return gBestPosParticle.getPos();
	}
	
}
