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
* <p> ����Ⱥ�Ż��㷨</p>
* <p>ʵ���������汾�Ŀ�ܡ�һ���ǻ����汾�ģ���Ҫ�����и����ٶ�ʱ�Ե�ǰȫ�����λ����Ϊ�������ӵ���ᾭ����Դ ����������û�����ӱ��졣</p>
*<p>��һ���Ǵ����ӱ��죨���ݣ��İ汾��������Ӱ���Ӧ�Ƚ��з�Ⱥ��ÿ�����ӻ�ѧϰ������Ⱥ�ĵ�ǰ���λ�á���������汾�ľ���ϸ�ڲμ���ע��</p>
*<p>Ϊ��ʹ�ø��㷨��ֻ��Ҫ�̳и��࣬��ʵ�ֳ��󷽷�{@link #fitness(double[])} ����ִ����ѡ��ܵĶ�Ӧ�������ɡ�<p>
* @see ������, ������, ����,��. һ�ֶ�߶�Эͬ���������Ⱥ�Ż��㷨[J]. ���ѧ��, 2012, 23(7):1805-1815.
* @author FlyingFish
* @date 2016-12-13
*/
public abstract class PSO {
	
	//���е�����
	protected Particle[] particles;

	//��־particles�������ĸ�λ�ö�Ӧ�ĸ���Ⱥ
	private int[] swarmIndex;
	
	//��ǰ����
	protected int currentGen ;
	
	//���ڿ�������Ⱥ�Ĳ���
	protected int dimension;
	protected double c1,c2;
	protected double posBound;
	protected double velBoundFactor;
	
	//��ǰ����ռ�����λ�õ�����
	private Particle gBestPosParticle;
	
	//��������Ż��ı���
	private double[] gBestPos;
	
	//��Ⱥ
	protected ParticleSwarm[] swarm;
	
	//��Ⱥ��Ŀ
	protected int numberOfSwarm;
	
	
	/** 
	* <p>���캯��</p> 
	* <p>���ڻ����������Ⱥ�Ż��㷨 </p> 
	* @param amountOfParticles �ܵ�������
	* @param dimension ά��
	* @param c1 ���徭��ѧϰ����
	* @param c2 ��ᾭ��ѧϰ����
	* @param posBound λ�÷�Χ
	* @param velBoundFactor �ٶȷ�Χ����
	*/
	protected PSO(int amountOfParticles,int dimension,double c1,double c2,double posBound,double velBoundFactor){
		particles = new Particle[amountOfParticles];
		
		//�������
		this.dimension = dimension;
		this.c1 = c1;
		this.c2 = c2;
		this.posBound = posBound;
		this.velBoundFactor = velBoundFactor;
		
		//������������Ⱥ�Ĺ�����Ϣ
		Particle.paramInit(dimension,c1, c2, posBound, velBoundFactor);
		for(int i = 0;i < amountOfParticles;i ++){
			
			//����������
			//particles[i] = new Particle(System.currentTimeMillis());
			particles[i] = new Particle(i);
			
			//�����Ӹ���ֵ
			particles[i].init();
			
			//������Ӧ��
			particles[i].setFitness(fitness(particles[i].getPos()));
			
			//��ʼʱ�ѵ�ǰλ����Ϊ��ʷ���λ��
			particles[i].setCurrentPosAsPersonalBest();
		}
		//����������ӵĳ�ʼ�����ҳ���������Ⱥ��������ĸ���
		findCurrentGlobalBest();
		
		//��ʼ����Ϊ1
		currentGen = 1;
	}
	
	/** 
	* <p>���캯��</p> 
	* <p>���ڱ���������Ⱥ�Ż��㷨</p> 
	* @param amountOfParticles �ܵ�������
	* @param dimension ά��
	* @param c1 ���徭��ѧϰ����
	* @param c2 ��ᾭ��ѧϰ����
	* @param posBound λ�÷�Χ
	* @param velBoundFactor �ٶȷ�Χ����
	* @param numberOfSwarm ��Ⱥ��
	*/
	protected PSO(int amountOfParticles,int dimension,double c1,double c2,double posBound,double velBoundFactor,int numberOfSwarm){
		this(amountOfParticles, dimension, c1, c2, posBound, velBoundFactor);
		
		if(amountOfParticles <= 0 ||numberOfSwarm <= 0 || numberOfSwarm > amountOfParticles  )
			throw new IllegalArgumentException("��������������������Ŀ " + amountOfParticles + " ����Ⱥ��Ŀ " + numberOfSwarm);
		
		//����swarmIndex
		swarmIndex = new int[amountOfParticles];
		
		this.numberOfSwarm = numberOfSwarm;
		swarm = new ParticleSwarm[numberOfSwarm];	
	
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
			
			//��begin��end����������ڵ�i������Ⱥ
			for(int j = begin;j <= end;j ++ )
				swarmIndex[j] = i;
			
			//������Ⱥ������ʼsigmaֵΪposBound
			swarm[i] = new ParticleSwarm(membersPerSwarm,posBound);
								
			begin = end + 1;	
		}
		
		//��ÿ����Ⱥ�����Ա�����Ա
		setupSwarm();
		
		espController = new EscapeController();
	}
	
	
	/** 
	* <p>��Ӧ�Ⱥ��� </p> 
	* <p>��Ӧ�Ⱥ�����������һ������λ�õĺá�����������ԣ�����Լ���������Ӧ�Ⱥ������ص�ֵԽ�ߣ�˵����ǰ����Խ�ӽ����Ž⣬Ҳ��Խ�á���֮��������ص�ֵԽС���������������Ž�ԽԶ�������ӵ����۾�Խ�͡���Ӧ�Ⱥ�����һ���Ǹ�������</p> 
	* @param var ���Ż��ı�����Ҳ�������ӵ�λ�ñ���
	* @return �Ǹ�����Ӧ��ֵ
	*/
	public abstract  double fitness(double[] var);
	
	public void novelMAEPSO(int totalGen){
		//showInitState();
		for(;currentGen <= totalGen;currentGen ++){
			
			//�����˶�
			particlesMove();
			//showMoveInfo();
			
			//�������ݣ����죩
			particlesEscape();
			//System.out.println(espController);			
			
			//������Ⱥ
			setupSwarm();
			
			//����Sigmaֵ
			updateSigma();
			
			//�ҵ���һ��λ����ѵ�����
			//this.findCurrentGlobalBest();
		}
		
		/*���������ӵĸ�����ʷ���λ�����ҵ�ȫ����ʷ���λ�ã���Ϊ�������Ľ��*/
		double best = Double.MIN_VALUE;
		int bestIndex = -1;
		for(int i = 0;i < particles.length;i ++)
			if(particles[i].getPBestFitness() > best){
				best = particles[i].getPBestFitness() ;
				bestIndex = i;
			}
		gBestPos = new double[dimension];
		
		//���յĽ�������gBestPos��
		System.arraycopy(particles[bestIndex].getPBestPos(), 0, gBestPos, 0, dimension);		
	}
	
	/** 
	* <p>������Ⱥ </p> 
	* <p>Ϊÿһ������Ⱥ�����Ա,���������Ⱥ����Ӧ�ȣ��Լ��ҳ���Ⱥ��������ĸ��塣������������sigmaֵ </p>  
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
			
			//�ҳ���Ⱥ��������ĸ���
			swarm[i].findCurrentGlobalBest();
			
			//������Ⱥ��Ӧ��
			swarm[i].calcSwarmFitness();	
			
			begin = end + 1;	
		}			
	}
	
		
	/** 
	* <p>�����˶� </p> 
	* <p>Description: </p>  
	*/
	private void particlesMove(){
		for(int j = 0;j < particles.length;j++){

			//��������λ��
			//�����ȸ���λ�ã��ٸ����ٶ�
			//���Գ�������ٶȱ�����������Ž⾫��
			particles[j].updatePos();
			
			//���������ٶ�
			//ע�⵽����ʹ�õ�������������Ⱥ�����Ÿ���
			particles[j].updateVel(swarm[swarmIndex[j]].getGlobleBestPos());
			//particles[j].updateVel(gBestPosParticle.getPos());			
			
			//����������Ӧ��
			particles[j].setFitness(fitness(particles[j].getPos()));
			
			//����������ʷ����λ��			
			if(particles[j].getFitness() > particles[j].getPBestFitness())//�����Ӧ�������
				particles[j].setCurrentPosAsPersonalBest();
		}
	}
	
	/** 
	* @Fields espController : TODO(�������ݿ�����) 
	*/ 
	private EscapeController espController;
	/** 
	* <p>��������</p> 
	* <p>Description: </p>  
	*/
	private void particlesEscape(){
		espController.particlesEscape();
	}
	
	/** 
	* <p>����Sigmaֵ</p> 
	* <p>Description: </p>  
	*/
	private void updateSigma(){
		//�ֱ��ҳ�������Ⱥ������С�������Ӧ�Ȳ���������Ⱥ��Ӧ��
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
		
		//����ÿ����Ⱥ��sigmaֵ
		System.out.print("Sigma��");
		for(int i = 0;i < swarm.length;i ++){
			double power = (swarm.length * swarm[i].getSwarmFitness() - sumFitness) 
												/ (fitnessMax - fitnessMin);		
			double newSigma = swarm[i].getSigma() * Math.exp(power);
			
			//��ʾSigma�仯
			System.out.print(ParticleSwarm.normaliseSigma(newSigma) + " ");
			
			//���淶�����sigma������Ⱥ
			swarm[i].setSigma(ParticleSwarm.normaliseSigma(newSigma));
		}
		System.out.println();
	}
	
	/**
	* <p> EscapeController</p>
	* <p>�������ݣ����죩��������ע���ֹ��PSO��������δ����ֵʱ���� </p>
	* @author FlyingFish
	* @date 2016-12-14
	*/
	private class EscapeController{
		//������ֵ
		protected  double[] thredholds;			

		//��ʱ�����ݿռ�
		private double[] escapePosition ;
		
		//��¼ÿһά�������ӵ�������ֵ���ۼ�ֵ
		private int[] acumBellowTherdholdTimes; 
		
		//��������ֵ�½��ٶȵı���
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
			//�����еĸ�˹���������У����ñ�׼��˹��̬�ֲ����������ݵõ��ĵ�������Ӧ��
			double maxFitnessND = Double.MIN_NORMAL,temp;
			
			//���ñ�׼��˹��̬�ֲ����������ݵõ��ĵ��ڿռ��dά�ϵ�λ��
			double maxFitnessNDEscapePosDth =0;
					
			for(int d = 0; d < dimension;d ++){
				for(int i = 0;i < particles.length;i ++){
					
					//��i�������ڵ�dά�ռ��ϵ��ٶ�
					double vid = particles[i].getVel(d);
					if(Math.abs(vid ) < thredholds[d]){//�������ٶ�С���ڵ�dά�ռ��ϵ���ֵ
						
						//��dά�ռ����ۼ��ٶȱ��������1
						acumBellowTherdholdTimes[d] ++;
						
						//����ʹ�����еĸ�˹�������ӽ���������
						//ѡȡ���о��������Ӧ�ȵĵ�
						for(int j = 0; j < swarm.length;j ++){
							temp = randomlyEscapeWithND(particles[i],swarm[j].getSigma());
							if(temp > maxFitnessND){//DEBUG
								maxFitnessND = temp;
								
								//ע�⵽ÿ�������ݵ�λ�ö���ʱ�����escapePosition����	
								maxFitnessNDEscapePosDth = escapePosition[d];
							}					
						}
						
						//���þ��ȷֲ�����������
						temp = randomlyEscapeWithAD(particles[i]);
						
						//����������Ӧ�Ȳ�ĵ���dά�ռ��ϵģ��ԣ��������������õ��ڵ�dά�ռ��ϵ��ٶ�
						if(maxFitnessND > temp){
							//System.out.printf("�ٶȱ���(d,i,vid,newVid)=(%3d,%3d,%15.10f,%15.10f)) \n",d,i,vid,maxFitnessNDEscapePosDth - particles[i].getPos(d));
							particles[i].setVel(d, maxFitnessNDEscapePosDth - particles[i].getPos(d));
						}else	{//ע�⵽ÿ�������ݵ�λ�ö���ʱ�����escapePosition����	
							//System.out.printf("�ٶȱ���(d,i,vid,newVid)=(%3d,%3d,%15.10f,%15.10f)) \n",d,i,vid,escapePosition[d] - particles[i].getPos(d));
							particles[i].setVel(d, escapePosition[d] - particles[i].getPos(d));
						}			
					}
				}
				//��dά��ֵ����Ӧ�½�
				if(acumBellowTherdholdTimes[d] > k1){
					acumBellowTherdholdTimes[d] = 0;
					thredholds[d] = thredholds[d] / k2;
				}			
			}//for(d)		
		}
		
		/** 
		* <p>���ñ�׼��˹��̬�ֲ����������� </p> 
		* <p>���ݺ�Ľ����Ҳ�������ӵ�λ�ô����<Code>EscapeController.escapePosition</Code>����</p> 
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
		* <p>���þ��ȷֲ�����������</p> 
		* <p>���ݺ�Ľ����Ҳ�������ӵ�λ�ô����<Code>EscapeController.escapePosition</Code>����</p> 
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
			return new String("��ֵ��" + PSO.showArray(thredholds) 
			+ "\tGֵ��" + PSO.showArray(acumBellowTherdholdTimes)
			+"\n K1��K2��" + k1 + " " + k2);
		}		
	}
	
	/** 
	* <p>���������صĲ��� </p> 
	* <p>�����ӵ�ĳһά�ٶȵ��������ٶ���ֵʱ�����ӻ��ڸ�ά�ռ������»��һ�������ٶȡ����⣬�����ٶ���ֵ������Ӧ�½��ģ�����k1,k2�����������½��Ľ��ࡣ</p> 
	* @param factorT �����ٶ���ֵ = factorT * posBound * velBoundFactor
	* @param k1 k1Խ����ֵ�½��ļ��Խ��
	* @param k2 k2Խ����ֵһ���½���Խ����
	*/
	public void reviseEscapePolicy(double factorT,int k1,int k2){
		espController = new EscapeController(factorT,k1,k2);
	}

	

		
	/** 
	* <p>������������Ⱥ�Ż��㷨��ִ�з��� </p> 
	* <p>Description: </p> 
	* @param totalGen 
	*/
	public void basicPSO(int totalGen){
		for(;currentGen <= totalGen;currentGen ++){
			for(int j = 0;j < particles.length;j ++){
				//���������ٶ�
				particles[j].updateVel(gBestPosParticle.getPos());
				
				//��������λ��
				particles[j].updatePos();
				
				//����������Ӧ��
				particles[j].setFitness(fitness(particles[j].getPos()));
				
				//����������ʷ����λ��			
				if(particles[j].getFitness() > particles[j].getPBestFitness())//�����Ӧ�������
					particles[j].setCurrentPosAsPersonalBest();
			}
			//�ҳ��ô�������ĸ���
			findCurrentGlobalBest();
			
			//������һ
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
	* <p>�õ������Ż��õı���</p> 
	* <p>Ҳ�����������Ⱥ�ҵ������λ�� </p> 
	* @return 
	*/
	public double[] getOptimisedVar(){
		return gBestPos;
	}
	
	/** 
	* <p>�ҳ���һ����������Ⱥ��������ĸ��� </p> 
	* <p>����ᱻ�ŵ�{@linkplain #gBestPosParticle}}�д洢����������Ϊ�������ӵ�ȫ����ᾭ����Դ�� </p>  
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
	
	/*�����ǲ����麯��*/
	
	private void showInitState(){
		System.out.println("��ʼ״̬");
		System.out.println("��̬������" );
		System.out.println("Particle(d,c1,c2,posb,vbf) " 
		+ Particle.dimension +" " + Particle.c1 + " " + Particle.c2 + " " + Particle.posBound + " " + Particle.velBoundFactor) ;
		System.out.println("PSO(d,c1,c2,posb,vbf) " 
		+ dimension +" " +c1 + " " + c2 + " " + posBound + " " + velBoundFactor) ;
		System.out.println("����");
		for(int i = 0; i < particles.length;i ++)
			System.out.println(particles[i]);
		System.out.println("��Ⱥ");
		for(int i = 0;i < swarm.length;i ++)
			System.out.println(swarm[i]);
		System.out.println("��ǩ");;
		System.out.println(showArray(swarmIndex));
		System.out.println("��������� ");;
		System.out.println(espController);		
	}
	
	private void showMoveInfo(){
		System.out.println("\n���º������״̬��");
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
