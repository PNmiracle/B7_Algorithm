import algorithm.SA;
import util.TspProblem;
import util.TspRandomArr;
import util.TspReader;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class AppMain {
    public static void main(String[] args) throws IOException {
        /*һ���������n + 2����, ��ŷֱ�Ϊ[0...N-1], ����0ΪB��(��������ʼ��), n + 1Ϊ�յ�A(���������), ������Ϊ������...*/
        System.out.println("���������ɵ�����Ͱ����Ŀn: ");
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();  //����Ͱ��Ŀn
        int maxCap = 1000; // ÿ�������������
        System.out.println("����������������m : ");
        int m = scanner.nextInt();  // B����15��������

        /* ----------------------���� or �������  ------------------------- */
        //TspProblem problem = TspReader.readTSP("resources/eil51.txt", 12);
        TspProblem problem = TspRandomArr.generate_problem(n);
        int[] xCoors = problem.getxCoors();
        int[] yCoors = problem.getyCoors();
        int[] weights = problem.getWeights();
        System.out.print("������ɵ�n + 2��������:");
        for (int i = 0; i < xCoors.length; i++) {
            if (i == 0 || i == xCoors.length - 1) {
                System.out.print(" " + i + "-" + "(" + xCoors[i] + "," + yCoors[i] + ")");
            } else {
                System.out.print(" " + i + "-" + "(" + xCoors[i] + "," + yCoors[i] + "," + weights[i - 1] + ")");
            }
        }
        SA sa = new SA(problem);
        sa.setN(n);
        /*����̰�ĵ�BFS�ҵ�һ���ȽϺõ����ӽ�(һ�����), Ҳ�����������һ�����ӽ�
        int[] rout = sa.BFS();*/
        // rout.length - m - 1 = n
        int[] rout = new int[n + 2 + m - 1];//Java������Ĭ�ϳ�ʼ��rout[0] = 0;
        rout[rout.length - 1] = n + 1;

        /* ---------------------- ����ģ���˻��㷨 ------------------------- */
        long before_sa = System.currentTimeMillis();
        //���ɵĽ� m-1��0�� [1..n]��ɵ�һ���������
        sa.build_random_sequence(rout, m);
        int belta = 10;
        double T0 = 100;        //��ʼ�¶�
        double alpha = 0.99;        //�¶�˥��ϵ��
        int maxOutIter = 2000;
        int maxInIter = 300;  //��ѭ��������ֵΪһ���ϴ�ĳ���
        int[] rout_optimized = sa.Sa_TSP(rout, T0, alpha, maxOutIter, maxInIter, weights, maxCap);
        sa.print(rout_optimized, weights, maxCap);
        List<List<Integer>> rout_list = sa.split_route(rout_optimized, n);
        System.out.println();
        for (List<Integer> rout_i : rout_list) {
            System.out.println("rout_i = " + rout_i);
        }
        long after_sa = System.currentTimeMillis();
        long total_time = after_sa - before_sa;
        System.out.println("\nģ���˻��㷨��ʱ: " + (total_time) + "ms");
    }
}
