import algorithm.SA;
import util.TspProblem;
import util.TspRandomArr;
import util.TspReader;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class AppMain {
    public static void main(String[] args) throws IOException {
        /*һ���������n + 2����, ��ŷֱ�Ϊ[0...N-1], ����0ΪB��(��������ʼ��), n + 1Ϊ�յ�A(���������), ������Ϊ������*/
        System.out.println("���������ɵ�����Ͱ����Ŀn");
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        //TspProblem problem = TspReader.readTSP("resources/eil51.txt", 12);
        TspProblem problem = TspRandomArr.generate_problem(n);
        int[] xCoors = problem.getxCoors();
        int[] yCoors = problem.getyCoors();
        System.out.print("������ɵ�n + 2��������:");
        for (int i = 0; i < xCoors.length; i++) {
            System.out.print(" " + i + "-" + "(" + xCoors[i] + "," + yCoors[i] + ")");
        }
        SA sa = new SA(problem);
        /*����̰�ĵ�BFS�ҵ�һ���ȽϺõ����ӽ�(һ�����), Ҳ�����������һ�����ӽ�
        int[] rout = sa.BFS();*/

        int[] rout = new int[n + 2];//Java������Ĭ�ϳ�ʼ��rout[0] = 0;
        rout[rout.length - 1] = n + 1;
        sa.build_random_sequence(rout);
        /* ---------------------- ����ģ���˻��㷨 ------------------------- */
        double T0 = 1e6;        //��ʼ�¶�
        double d = 0.99;        //�¶�˥��ϵ��
        double Tk = 1e-6;       //����¶�
        int L = 20 * rout.length;//��ѭ������, Ҳ���Ը�ֵΪһ���ϴ�ĳ���

        int[] rout2 = sa.Sa_TSP(rout, T0, d, Tk, L);
        sa.print(rout2);
    }
}
