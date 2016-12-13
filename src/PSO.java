import java.util.Arrays;

/**   
* @Title: PSO.java 
* @Package  
* @Description: TODO(��һ�仰�������ļ���ʲô) 
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
		
		//������������Ⱥ�Ĺ�����Ϣ
		Particle.paramInit(dimension,c1, c2, posBound, velBoundFactor);
		for(int i = 0;i < amountOfParticles;i ++){
			
			//����������
			//particles[i] = new Particle(System.currentTimeMillis());
			particles[i] = new Particle(i);
			
			//�����Ӹ���ֵ
			particles[i].init();
			
			//������Ӧ��
			particles[i].setFitness(fitness(particles[i]));
			
			//��ʼʱ�ѵ�ǰλ����Ϊ��ʷ���λ��
			particles[i].setCurrentPosAsPersonalBest();
		}
		//����������ӵĳ�ʼ�����ҳ���������Ⱥ��������ĸ���
		findCurrentGlobalBest();
		
		//��ʼ����Ϊ1
		currentGen = 1;
	}
	
	protected PSO(int amountOfParticles,int dimension,double c1,double c2,double posBound,double velBoundFactor,int numberOfSwarm){
		this(amountOfParticles, dimension, c1, c2, posBound, velBoundFactor);
				
		if(amountOfParticles <= 0 ||numberOfSwarm <= 0 || numberOfSwarm > amountOfParticles  )
			throw new IllegalArgumentException("��������������������Ŀ " + amountOfParticles + " ����Ⱥ��Ŀ " + numberOfSwarm);
		
		this.numberOfSwarm = numberOfSwarm;
		swarm = new ParticleSwarm[numberOfSwarm];	
		
		setupSwarm();
		
		//��ʼʱsigmaͳһ����ΪposBound
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
	* <p>������Ⱥ </p> 
	* <p>Ϊÿһ������Ⱥ�����Ա,���������Ⱥ����Ӧ�ȣ�������������sigmaֵ </p>  
	*/
	private void setupSwarm(){
		int amountOfParticles = particles.length;
		
		//���������Ӱ���Ӧ������
		Arrays.sort(particles);
		
		//�õ�ÿ������Ⱥ��������Ŀ
		int membersPerSwarm ;
		
		//����
		int benefit = amountOfParticles  % numberOfSwarm;
		
		int begin = 0,end ;
		for(int i = 0;i < numberOfSwarm;i ++){
			if(benefit -- > 0)//����ԭ��
				membersPerSwarm = amountOfParticles  / numberOfSwarm +1;
			else
				membersPerSwarm = amountOfParticles  / numberOfSwarm ;
			
			end = begin + membersPerSwarm - 1;
			
			//�����Ա
			swarm[i].setupMembers(particles, begin, end);
			
			//������Ⱥ��Ӧ��
			swarm[i].calcSwarmFitness();	
			
			begin = end + 1;	
		}			
	}
	
	private void particlesMove(){
		for(int j = 0;j < particles.length;j ++){
			//���������ٶ�
			particles[j].updateVel(gBestPosParticle.getPos());
			
			//��������λ��
			particles[j].updatePos();
			
			//����������Ӧ��
			particles[j].setFitness(fitness(particles[j]));
			
			//����������ʷ����λ��			
			if(particles[j].getFitness() > particles[j].getPBestFitness())//�����Ӧ�������
				particles[j].setCurrentPosAsPersonalBest();
		}
	}
	
	private void updateSigma(){
		//�ֱ��ҳ�������Ⱥ������С�������Ӧ�Ȳ���������Ⱥ��Ӧ��
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
		
		//����ÿ����Ⱥ��sigmaֵ
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
		for(int gen = 2;gen < 101;gen ++){//һ��������100��
			for(int j = 0;j < particles.length;j ++){
				//���������ٶ�
				particles[j].updateVel(gBestPosParticle.getPos());
				
				//��������λ��
				particles[j].updatePos();
				
				//����������Ӧ��
				particles[j].setFitness(fitness(particles[j]));
				
				//����������ʷ����λ��			
				if(particles[j].getFitness() > particles[j].getPBestFitness())//�����Ӧ�������
					particles[j].setCurrentPosAsPersonalBest();
			}
			//�ҳ��ô�������ĸ���
			findCurrentGlobalBest();
			
			//������һ
			currentGen++;
		}
		
	}
	
	/** 
	* <p>�ҳ���������Ⱥ��������ĸ��� </p> 
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
