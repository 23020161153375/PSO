import java.util.Random;

/**   
* @Title: Particle.java 
* @Package  
* @Description: TODO(��һ�仰�������ļ���ʲô) 
* @author FlyingFish
* @date 2016-12-13
* @version V1.0   
*/

/**
* <p> Particle</p>
* <p>������Ⱥ�Ż��㷨���ṩ����������ģ�͡�ʹ�ø��������¼�����Ҫע�⣺ </p>
* <br>1 �������Ӷ���֮ǰ����Ҫʹ��{@link #paramInit(int, double, double, double, double)} �������������ռ�ά�ȡ�ѧϰ�����Լ����ӻ��Χ�������ٶȷ�Χ��Щ�������ӹ��е���Ϣ��</br>
* <br>2 ���ú������������ٶȺ�λ�õĹ����У������Զ��������ӵ���Ӧ���Լ����Ӹ�����ʷ���λ�õļ�¼����Щ��Ҫ�������á�</br>
* <br>3 ����{@link #setCurrentPosAsPersonalBest()} ��������ֱ�Ӱѵ�ǰ��������λ�ú����Ӧ����Ӧ����Ϊ������ʷ��Ѽ�¼����������ʵ�����ǲ�������Ѽ�¼��<br>
* @author FlyingFish
* @date 2016-12-13
*/
public class Particle implements Comparable<Particle>{
	
	//�ṩ���ӵġ����ԡ�
	public Random random;

	private double fitness;

	/*Particle��ľ�̬����*/
	//���������ռ��ά��
	static int dimension = 1;
	
	//���徭�顢��ᾭ���ѧϰ����
	static double c1 = 2,c2 = 2;
	
	//ÿһά�ռ��ϵ�λ�÷�Χ[-posBound,posBound]
	//ÿһά�ռ���ٶȵı仯��Χ[-posBound * velBoundFactor,posBound * velBoundFactor]
	static double posBound = 1,velBoundFactor = 0.5;
	
	//���ӵ�λ��ʸ��
	private double[] position;	
	
	//���ӵ��ٶ�ʸ��
	private double[] velocity;
	
	//������ʷ���λ��
	private double[] pBestPos;
	
	//������ʷ���λ�õ���Ӧ��
	private double pBestFitness;
	
	/** 
	* <p>���캯��</p> 
	* @param seed ���������
	*/
	public Particle(long seed){
		position = new double[dimension];
		velocity = new double[dimension];
		pBestPos = new double[dimension];
		
		random = new Random(seed);
	}
	
	/** 
	* <p>��������Ⱥ�ľ�̬����</p> 
	* <p>�ú���Ӧ���ڹ������Ӷ�����ǰ����</p> 
	* @param dimension ���������ռ��ά��
	* @param c1 ���徭��ѧϰ����
	* @param c2 ��ᾭ��ѧϰ����
	* @param posBound ÿһά�ռ��ϵ����ӵ�λ�� |P|< posBound
	* @param velBoundFactor ÿһά�ռ��������ٶ� |V| < posBound * velBoundFactor

	*/
	public static void paramInit(int dimension,double c1,double c2,double posBound,double velBoundFactor){
		Particle.dimension = dimension;
		Particle.c1 = c1;
		Particle.c2 = c2;
		
		//posBound��velBoundFactor ������Ϊ��ֵ
		Particle.posBound = Math.abs(posBound);
		Particle.velBoundFactor = Math.abs(velBoundFactor);
	}
	
	/** 
	* <p>������Χ�ڸ�λ�ú��ٶȸ����ֵ��һ���ڳ�ʼ��ʱ���ã������ﲢ����Ĭ�ϰѵ�ǰλ����Ϊ������ʷ���λ�ü�¼������</p> 
	* <p>Description: </p>  
	*/
	public void init(){
		for(int i = 0; i < dimension;i ++){
			position[i] = (random.nextDouble() * 2 - 1) * posBound; 
			velocity[i] = (random.nextDouble() * 2 - 1) * posBound * velBoundFactor;
		}
	}
	
	/** 
	* <p>�����ӵ�ǰλ����Ϊ�������λ��</p> 
	* <p>�ò��������Ѹ���ĵ�ǰ��Ӧ����Ϊ���������Ӧ�ȣ������ڵ��ø÷���ǰӦȷ���Ѿ����и����˵�ǰ����Ӧ�ȡ�</p>  
	*/
	public void setCurrentPosAsPersonalBest(){
		for(int i = 0;i < dimension;i ++)
			pBestPos[i] = position[i];
		pBestFitness = fitness;
	} 
	
	/** 
	* <p>�����ٶ�</p> 
	* <p>���¹�ʽ���ǲ�������Ȩ�صġ�������Ҫ�ṩ���ڸ�������Ҫѧϰ��λ�ã����λ�üȿ�������������Ⱥ���ҵ���ȫ�����λ�ã�Ҳ����ֻ��Ŀǰ�����Ӻ����ھ������ҵ��ľֲ����λ�á�</p> 
	* @param gBestPos ���λ��
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
	* <p>�����ٶ�</p> 
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
	* <p>���������ٶȸ������ӵ�ǰλ��</p> 
	* <p>ע��÷�����û���Զ�������Ӧ��ֵ������Ҳû�и��¸��������ʷλ�ã���Щ����Ҫ�������á� </p>  
	*/
	public void updatePos(){
		for(int i = 0;i < dimension;i ++)
			position[i] += velocity[i];
		limitPos();
	}
	
	/** 
	* <p>����λ�� </p> 
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
	* <p>�������ٶ� </p> 
	* <p>Description: </p> 
	* @param d ά��
	* @param value ֵ
	*/
	public void setVel(int d,double value){
		this.velocity[d] = value;
		
		//����
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

	/** �Ƚ�����������Ӧ�ȵĴ�С
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
		String info = "λ��: " + showArray(position) + "\t�ٶ�: " + showArray(velocity) 
		+ "\n�������λ��:" + showArray(pBestPos) + "\t��ǰ��Ӧ�ȣ�" + fitness ;
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
