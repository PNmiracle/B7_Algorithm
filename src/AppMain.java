import algorithm.SA;
import util.CVRP_problem;
import util.BuildRandom;

import java.util.List;
import java.util.Scanner;


public class AppMain {
    public static void main(String[] args) {
/* ---------------------- �������n, maxCap, m ------------------------- */
        /*һ���������n + 2����, ��ŷֱ�Ϊ[0...n+1], ����0ΪB��(��������ʼ��), n + 1Ϊ�յ�A(���������),
        ������[1...n]Ϊ������...*/
        System.out.println("���������ɵ�����Ͱ����Ŀn: ");
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();  //����Ͱ��Ŀn
        /*���Ҫ��:CVRP����ӵĲ���*/
        System.out.println("������ÿ�������������������(kg)");
        int maxCap = scanner.nextInt(); // ÿ�������������
        System.out.println("����������������m : ");
        int m = scanner.nextInt();  // B����m��������


/* ---------------------- �����������, ����Ͱ����, ��ӡ ------------------------- */
        CVRP_problem problem = BuildRandom.generate_problem(n); //�������n + 2�����ظ�������ꡢ����������(15��20kg),����100*100�ķ�����
        /*��ȡ����ӡn+2���������, n������Ͱ������*/
        int[] xCoors = problem.getxCoors();
        int[] yCoors = problem.getyCoors();
        int[] weights = problem.getWeights();
        System.out.print("������ɵ�n + 2��������:");
        for (int i = 0; i < xCoors.length; i++) {
            if (i == 0 || i == xCoors.length - 1) {
                System.out.print(" " + i + "-" + "(" + xCoors[i] + "," + yCoors[i] + ")");
            } else {
                System.out.print(" " + i + "-" + "(" + xCoors[i] + "," + yCoors[i] + "," + weights[i] + ")");
            }
        }
        System.out.println();


/* ---------------------- ����ģ���˻��㷨 ------------------------- */
        SA sa = new SA(problem);    //��һ��SA�㷨��Ķ���
        sa.setN(n);                 //������Ͱ������n����ȥ

        int[] route = new int[n + 2 + m - 1];//Java������Ĭ�ϳ�ʼ��rout[i] = 0; ��ĳ���route.length=m+1+n
        route[route.length - 1] = n + 1;     //������һλ��ֵΪn+1�Ž��(�յ�A), ��һλΪ0�Ž��(���B)

        long before_sa = System.currentTimeMillis();    //��ʼ�ĺ�����
        sa.build_random_sequence(route, m);  //���ɵĽ� m-1��0�� [1..n]��ɵ�һ���������
        int belta = 10;             //�ͷ������ĳͷ�ϵ��
        double T0 = 100;            //��ʼ�¶�
        double alpha = 0.99;        //�¶�˥��ϵ��
        int maxOutIter = 1000;      //��ѭ���Ĵ���, ������β��Է���, ����600�����Ҿ��Ѿ��ﵽ�ܹ��Ż��ļ���
        int maxInIter = 300;        //��ѭ��������ֵΪһ���ϴ�ĳ���
        /*����sa_VCRP������õ�����·��*/
        int[] route_optimized = sa.sa_CVRP(route, T0, alpha, maxOutIter, maxInIter, weights, maxCap, belta);
        /*��ӡ��·�����Ⱥ���·��*/
        int total_dist = sa.dist_penalty(route_optimized, weights, maxCap, belta);
        System.out.println("\n��·������Ϊ: " + total_dist + "\n");
        sa.print_total(route_optimized);

        /*����·������Ϊÿ������·��*/
        List<List<Integer>> paths_list = sa.split_route(route_optimized, n);
        System.out.println();
        for (int i = 0; i < paths_list.size(); i++) {
            List<Integer> path_i = paths_list.get(i);
            System.out.println("��" + i + "�ų���·��:" + path_i);
        }
        long after_sa = System.currentTimeMillis();     //�����ĺ��� ��
        long total_time = after_sa - before_sa;         //����ʱ

/* ---------------------- ���ģ���˻��㷨��ʱ ------------------------- */
        System.out.println("\nģ���˻��㷨��ʱ: " + (total_time) + "ms");
    }
}
