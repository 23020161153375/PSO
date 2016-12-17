
/**
* <p> ����Ⱥ</p>
* <p>ע������Ⱥһ�����������Ա�������ܸı�</p>
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
	
	/** 
	* <p>��������Ⱥ�ĳ�Ա</p> 
	* <p>Description: </p> 
	* @param srcParticles ����Դ
	* @param begin ��ʼλ��
	* @param end ����λ��
	* @throws IllegalArgumentException ��� end - begin + 1 != amountOfMembers������amountOfMembers�ǳ�ʼ��ʱ�趨�ĳ�Ա����
	*/
	public void setupMembers(Particle[] srcParticles,int begin,int end ){
		if(end - begin + 1 != amountOfMembers)
			throw new IllegalArgumentException("���� "+ (end - begin + 1) + " ����Ա�����ʼ��ʱ����Ⱥ��  "+  amountOfMembers + "����Ա����@");
		
		for(int i = begin;i <= end ;i ++ )
			//ǳ����
			particles[i - begin] = srcParticles[i] ;		
	}
	
	/** 
	* <p>������Ⱥ��Ӧ��</p> 
	* <p>Description: </p>  
	*/
	public void calcSwarmFitness(){
		double fitness = 0;
		for(int i = 0;i < particles.length;i ++)
			fitness += particles[i].getFitness();
		swarmFitness =  fitness / amountOfMembers;
	}
		
	/** 
	* <p>Sigmaֵ�淶�� </p> 
	* <p>Description: </p> 
	* @param newSigma
	* @return 
	*/
	public static double  normaliseSigma(double newSigma){
		double w = Particle.posBound * 2;
		if(newSigma > w / 4)
			newSigma %= w / 4;	
		return newSigma;
	}
	
	/** 
	* <p>�ҳ���������Ⱥ��������ĸ��� </p> 
	* <p>Description: </p>  
	*/
	public void findCurrentGlobalBest(){
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

	/**
	 * @return the amountOfMembers
	 */
	public int getAmountOfMembers() {
		return amountOfMembers;
	}
	
	public String toString(){
		StringBuilder s = new StringBuilder();
		s.append("��Ա������" + amountOfMembers+ "\tSigma:" + sigma + "\t��Ӧ�ȣ�" + swarmFitness + "\n��Ա:");
		for(int i = 0; i < particles.length;i ++)
			s.append(particles[i] + "\n");
		s.append("��������壺\n" + gBestPosParticle);
		return s.toString();
	}
	
}
