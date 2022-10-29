package algorithm;

import util.CVRP_problem;

import java.util.*;

public class SA {

    CVRP_problem problem;
    public int n;   //����Ͱ����

    public void setN(int n) {
        this.n = n;
    }

    public SA(CVRP_problem problem) {
        this.problem = problem;
    }

    /**
     * ��route[]�����������(route[0]��route[n+1]����)
     * ��route[1..n]��ֵΪ[1...n]�Ĳ��ظ�������,
     * ÿ��ѡȡ֮���Ҫ�����������ֵ��
     * @param route һ�ֽ�
     * @param m     ������������
     * ��������Ŀ n = route.length - m - 1
     */
    public void build_random_sequence(int[] route, int m) {
        int n = route.length - m - 1;   //�½��ֲ�����n,�����n--���ı��Ա����n��ֵ
        int[] numbers = new int[n];
         /*numbers[0..n-1]˳����[1..n]*/
        for (int i = 0; i < n; i++) {
            numbers[i] = i + 1;
        }
        int n_temp = n;  //��¼��ʱn��ֵ, ��Ϊѭ������, ���ı�n_temp
        for (int i = 1; i <= n_temp; i++) {
            //Math.random() :[0, 1) ��double
            int r = (int) (Math.random() * n);  //r:[0..n - 1] ������
            route[i] = numbers[r];          // ��rout[1..n]��ֵΪ[1...n]�Ĳ��ظ�������,
            /**ÿ�����ɵ��±����п����ظ��ģ���������numbers��������±��Ӧ��ֵÿ����һ�Σ�
             �ͱ��߳�ȥ�ˣ��滻����n-1λ���ϵ�ֵ�����������ս���������ظ�*/
            numbers[r] = numbers[n - 1];
            n--;
        }
    }

    /**
     * ������ֽⷨ����·������(���ͷ���)
     * @param route         ���ֽⷨ
     * @param weights       ����Ͱ��������
     * @param maxCap        �������������
     * @param belta         �ͷ�ϵ��
     * @return distPenalty ���ͷ������·������
     */
    public int dist_penalty(int[] route, int[] weights, int maxCap, int belta) {
        /*������ֽⷨ(���)�ĳ��������ܺ�*/
        int validCap = 0;
        List<List<Integer>> list = SA.split_route(route, n);
        // ����������������·��
        for (int i = 0; i < list.size(); i++) {
            //ȥ�������ص�, ����һ���������߹�·���а�������������Ͱ
            for (int j = 1; j < list.get(i).size() - 1; j++) {
                maxCap -= weights[j];
            }
            if (maxCap < 0) {
                validCap += (-maxCap);
            }
        }
        int[][] dist = problem.getDistance();
        int distPenalty = 0;
        for (int i = 0; i < list.size(); i++) {
            List<Integer> path_i = list.get(i);
            for (int j = 0; j < path_i.size() - 1; j++) {
                distPenalty += dist[path_i.get(j)][path_i.get(j + 1)];
            }
        }

        /*�ؼ�: ���ӳͷ��� , �ͷ�ϵ��belta = 10*/
        distPenalty += validCap * belta;
        return distPenalty;
    }
    /**
     * ��������
     * @param route
     * @return int[]
     */
    public int[] copy_route(int[] route) {
        int[] out = new int[route.length];
        for (int i = 0; i < route.length; i++)
            out[i] = route[i];
        return out;
    }

    /**
     * �ؼ�: �ھɽ�A���������½�B�ķ���, �൱�ڶԽ����һ��ϴ��
     * ʵ�ֽ��滥�����������������ͬ�������Ȼ�󽻻�������λ�õĵ�
     * ���⻹����λ��, ���÷���
     *
     * @param route
     * @return ���������������·��
     */
    public int[] swap(int[] route) {
        Random random = new Random();
        //int r1 = random.nextInt(route.length);   //�������һ����Χ��[0..route.length)������
        int r1 = random.nextInt(route.length - 2) + 1;   //�������һ����Χ��[1..route.length - 1)������
        //int r2 = random.nextInt(route.length);
        int r2 = random.nextInt(route.length - 2) + 1;
        while (r1 == r2) {
            r2 = random.nextInt(route.length - 2) + 1;   //��֤�����r1, r2��ͬ
        }
        int[] change = copy_route(route);
        int tmp = change[r1];
        change[r1] = change[r2];
        change[r2] = tmp;
        return change;
    }

    /**
     * ģ���˻��㷨SA
     * @param route         ��ʼ��
     * @param T0            ��ʼ�¶�
     * @param alpha         �¶�˥��ϵ��
     * @param maxOutIter    ���ѭ������
     * @param maxInIter     �ڲ�ѭ������
     * @param weights       ����Ͱ����������
     * @param maxCap        ���������������
     * @param belta         �ͷ�ϵ��
     *
     * @return ����Ŵ��˻��㷨�Ż���õ��Ľ��Ž�
     */
    public int[] sa_CVRP(int[] route, double T0, double alpha, int maxOutIter, int maxInIter, int[] weights, int maxCap, int belta) {

        int[] bestpath, curentpath;     //�Ż�������·���͵�ǰ·��(�����)
        double t = T0;                  //t:�˿̵��¶ȱ���
        bestpath = curentpath = copy_route(route);// ��ʼʱ��:�������ӽ�route��curentpath,�� bestpath��
        Random rd = new Random();
        for (int i = 0; i < maxOutIter; i++) {  // ���ﵽ����¶�ʱֹͣѭ��
            System.out.println("��" + (i + 1) + "�ε���, ��ǰ·����������(���ͷ���)Ϊ:" + dist_penalty(curentpath, weights, maxCap, belta));        //TODO: ���п�ע�͵�, ��������ʱ��
            for (int j = 0; j < maxInIter; j++) {
                int[] update_path = swap(curentpath);//�ڵ�ǰ��A������������½�B,�˴��ý�����
                int delta = dist_penalty(update_path, weights, maxCap, belta) - dist_penalty(curentpath, weights, maxCap, belta);
                if (delta < 0) {//Ϊ��ֵ��������ɱ������ˣ������
                    curentpath = update_path;
                    bestpath = update_path;
                } else {
                    double p = Math.exp(-delta / t);
                    if (rd.nextDouble() <= p) {
                        curentpath = update_path;
                    }
                }
            }
            t *= alpha;
        }
        return bestpath;
    }

    /**
     * ���뵱ǰ���ӡ��·��
     * @param route ���뵱ǰ��
     */
    public void print_total(int route[]) {
        System.out.print("��ת��·����" + route[0] + "�Ž��(���B)");
        for (int i = 1; i < route.length - 1; i++) {
            if (route[i] == 0) {
                System.out.print("->" + "0�Ž��(���B)");
            } else {
                System.out.print("->" + "����Ͱ" + route[i] + "��");
            }
        }
        System.out.print("->" + route[route.length - 1] + "�Ž��(�յ�A)");
    }

    /**
     * ������Ľ��з�Ϊ����ÿ������ת��·��
     * @param route ����һ������Ľ�
     * @param n     ����Ͱ����
     * @return  splited_list: ÿ������ת��·�����ɼ��ϵļ���
     */
    public static List<List<Integer>> split_route(int[] route, int n) {
        ArrayList<List<Integer>> splited_list = new ArrayList<>(); //ÿ������ת��·�����ɼ��ϵļ���
        ArrayList<Integer> buffer = new ArrayList<>();      //��ž���ÿ������ת��·���Ļ�����
        buffer.add(0);      //����ÿ������ת��·���ĵ�һ��Ԫ�������B
        for (int i = 0; i < route.length; i++) {
            if (route[i] == 0) {
                continue;       //����������Ԫ��ֵΪ0,����ӵ������Ļ�����
            }
            buffer.add(route[i]);//�������Ԫ��ֵ��Ϊ0, ����ӵ������Ļ�����

            //(i + 1) < route.length ��ֹ����Խ��
            if ((i + 1) < route.length && route[i + 1] == 0) {
                buffer.add(n + 1);          //���յ�A��ӵ������Ļ�������
                splited_list.add(buffer);   //�����������, ��ӵ��������list��
                buffer = new ArrayList<>(); //�½�һ���յĻ�����
                buffer.add(0);              //�»�����������B
            }
            // �������еĵ����ڶ�λ������Ͱ, �򽫸�·��ֱ�Ӽ���list
            if (i == route.length - 2 && route[i + 1] != 0) {
                //buffer.add(n + 1); debug���:��һ��ѭ���Ὣn+1��ӵ��Ѿ�����list�����Ļ�������
                splited_list.add(buffer);
            }
        }
        return splited_list;
    }
}
