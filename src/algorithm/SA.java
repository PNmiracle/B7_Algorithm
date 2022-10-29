package algorithm;

import util.TspProblem;

import java.util.*;

public class SA {

    TspProblem problem;
    public int n;

    public void setN(int n) {
        this.n = n;
    }

    public SA(TspProblem problem) {
        this.problem = problem;
    }

    /**
     * ��rout[1..n]��ֵΪ[1...n]�Ĳ��ظ�������,
     * ÿ��ѡȡ֮���Ҫ�����������ֵ��
     */
    // rout.length - m - 1 = n
    public void build_random_sequence(int[] rout, int m) {
        int n = rout.length - m - 1;
        int[] numbers = new int[n];
        // numbers���[1..n]
        for (int i = 0; i < n; i++) {
            numbers[i] = i + 1;
        }
        int n_dup = n;
        for (int i = 1; i <= n_dup; i++) {
            //Math.random() :[0, 1) ��double
            //r:[0..n - 1] ������
            int r = (int) (Math.random() * n);
            rout[i] = numbers[r];
            /**ÿ�����ɵ��±����п����ظ��ģ���������numbers��������±��Ӧ��ֵÿ����һ�Σ�
             �ͱ��߳�ȥ�ˣ��滻����n-1λ���ϵ�ֵ�����������ս���������ظ�*/
            numbers[r] = numbers[n - 1];
            n--;
        }
    }


    public int cost_distance(int[] route, int[] weights, int maxCap, int belta) {
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
        int dist_penalty = 0;
        for (int i = 0; i < list.size(); i++) {
            List<Integer> path_i = list.get(i);
            for (int j = 0; j < path_i.size() - 1; j++) {
                dist_penalty += dist[path_i.get(j)][path_i.get(j + 1)];
            }
        }

        /*�ؼ�: ���ӳͷ��� , �ͷ�ϵ��belta = 10*/
        dist_penalty += validCap * belta;
        return dist_penalty;
    }

    public int[] copyRout(int[] rout) {
        int[] out = new int[rout.length];
        for (int i = 0; i < rout.length; i++)
            out[i] = rout[i];
        return out;
    }

    /**
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
        int[] change = copyRout(route);
        int tmp = change[r1];
        change[r1] = change[r2];
        change[r2] = tmp;
        return change;
    }

    /**
     * ģ���˻��㷨SA
     */
    public int[] sa_CVRP(int[] route, double T0, double alpha, int maxOutIter, int maxInIter, int[] weights, int maxCap, int belta) {

        int[] bestpath, curentpath;
        //t:�˿̵��¶ȱ���
        double t = T0;
        // ��ʼʱ��:�������ӽ�rout��curentpath,�� bestpath��
        bestpath = curentpath = copyRout(route);
        Random random = new Random();
        for (int i = 0; i < maxOutIter; i++) {  // ���ﵽ����¶�ʱֹͣѭ��

            System.out.println("��" + (i) + "�ε���, ��ǰ·����������(���ͷ���)Ϊ:" + cost_distance(curentpath, weights, maxCap, belta)); //���п�ע�͵�, ��������ʱ��
            for (int j = 0; j < maxInIter; j++) {
                int[] update_path = swap(curentpath);//�ڵ�ǰ��A������������½�B,�˴��ý�����
                int delta = cost_distance(update_path, weights, maxCap, belta) - cost_distance(curentpath, weights, maxCap, belta);
                if (delta < 0) {//Ϊ��ֵ��������ɱ������ˣ������
                    curentpath = update_path;
                    bestpath = update_path;
                } else {
                    double p = Math.exp(-delta / t);
                    if (random.nextDouble() <= p) {
                        curentpath = update_path;
                    }
                }
            }
            t *= alpha;
        }
        return bestpath;
    }

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


    public static List<List<Integer>> split_route(int[] rout_optimized, int n) {
        ArrayList<List<Integer>> splited_list = new ArrayList<>();
        ArrayList<Integer> temp = new ArrayList<>();
        temp.add(0);
        for (int i = 0; i < rout_optimized.length; i++) {
            if (rout_optimized[i] == 0) {
                continue;
            }
            temp.add(rout_optimized[i]);

            //(i + 1) < rout_optimized.length ��ֹ����Խ��
            if ((i + 1) < rout_optimized.length && rout_optimized[i + 1] == 0) {
                temp.add(n + 1);
                splited_list.add(temp);
                temp = new ArrayList<>();
                temp.add(0);
            }
            // �����еĵ����ڶ�λ������Ͱ, �򽫸�·��ֱ�Ӽ���list
            if (i == rout_optimized.length - 2 && rout_optimized[i + 1] != 0) {
                //temp.add(n + 1);
                splited_list.add(temp);
            }
        }
        return splited_list;
    }
}
