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

    /**
     * ����������������̰�ĳ���������·������ÿ�ζ�����ʼ���Ҹ�������ĵ�ȥ̰�ı���
     *
     * @return ����ó��ı����ڵ�˳��
     */
    public int[] BFS() {
        Queue<Integer> q = new LinkedList<>();
        q.add(0);
        int[] vis = new int[problem.getxCoors().length];
        int[] out = new int[problem.getxCoors().length];
        vis[0] = 1;
        int totalDist = 0;
        int index = 1;
        while (!q.isEmpty()) {
            int front = q.poll();
            int min = Integer.MAX_VALUE;
            int sIdx = 0;
            for (int i = 0; i < problem.getxCoors().length; i++) {
                if (vis[i] == 0 && i != front && min > problem.getDistance()[front][i]) {
                    min = problem.getDistance()[front][i];
                    sIdx = i;
                }
            }
            if (min != Integer.MAX_VALUE) {
                vis[sIdx] = 1;
                q.add(sIdx);
                out[index] = sIdx;
                index++;
                totalDist += problem.getDistance()[front][sIdx];
            }
        }
        q = null;
        totalDist += problem.getDistance()[out[out.length - 1]][0];
        return out;
    }

    public int cost_distance(int[] route, int[] weights, int maxCap) {
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

        /*�ؼ�: ���ӳͷ���*/
        dist_penalty += validCap * 10;
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
     * @param rout
     * @return ���������������·��
     */
    public int[] swap(int[] rout) {
        Random random = new Random();
        //int r1 = random.nextInt(rout.length);   //�������һ����Χ��[0..rout.length)������
        int r1 = random.nextInt(rout.length - 2) + 1;   //�������һ����Χ��[1..rout.length - 1)������
        //int r2 = random.nextInt(rout.length);
        int r2 = random.nextInt(rout.length - 2) + 1;
        while (r1 == r2) {
            r2 = random.nextInt(rout.length - 2) + 1;   //��֤�����r1, r2��ͬ
        }
        int[] change = copyRout(rout);
        int tmp = change[r1];
        change[r1] = change[r2];
        change[r2] = tmp;
        return change;
    }

    /**
     * ģ���˻��㷨SA
     *
     * @param route �������ڵ�����·��
     * @param T0    ��ʼ�¶�
     * @return ����õ�������·��
     */
    public int[] Sa_TSP(int[] route, double T0, double alpha, int maxOutIter, int maxInIter, int[] weights, int maxCap) {
        // T0=1e5,d =1-7e-3, Tk=1e-3
        // T0=1e6,d =0.99, Tk=1e-6
        int[] bestpath, curentpath;
        //t:�˿̵��¶ȱ���
        double t = T0;
        // ��ʼʱ��:�������ӽ�rout��curentpath,�� bestpath��
        bestpath = curentpath = copyRout(route);
        Random random = new Random();
        for (int i = 0; i < maxOutIter; i++) {  // ���ﵽ����¶�ʱֹͣѭ��
            for (int j = 0; j < maxInIter; j++) {
                int[] update_path = swap(curentpath);//�ڵ�ǰ��A������������½�B,�˴��ý�����
                int delta = cost_distance(update_path, weights, maxCap) - cost_distance(curentpath, weights, maxCap);
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

    public void print(int rout[], int[] weights, int maxCap) {
        System.out.println("\n��·�����ȣ�" + cost_distance(rout, weights, maxCap));
        System.out.print("��ת��·����" + rout[0] + "(���B)");
        for (int i = 1; i < rout.length - 1; i++) {
            System.out.print("->" + "����Ͱ" + rout[i]);
        }
        System.out.print("->" + rout[rout.length - 1] + "(�յ�A)");
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
