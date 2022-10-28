import algorithm.SA;
import util.TspProblem;
import util.TspRandomArr;
import util.TspReader;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class AppMain {
    public static void main(String[] args) throws IOException {
        /*一共随机生成n + 2个点, 编号分别为[0...N-1], 其中0为B点(垃圾车起始点), n + 1为终点A(垃圾填埋点), 其他点为垃圾点*/
        System.out.println("请输入生成的垃圾桶的数目n");
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        //TspProblem problem = TspReader.readTSP("resources/eil51.txt", 12);
        TspProblem problem = TspRandomArr.generate_problem(n);
        int[] xCoors = problem.getxCoors();
        int[] yCoors = problem.getyCoors();
        System.out.print("随机生成的n + 2个点坐标:");
        for (int i = 0; i < xCoors.length; i++) {
            System.out.print(" " + i + "-" + "(" + xCoors[i] + "," + yCoors[i] + ")");
        }
        SA sa = new SA(problem);
        /*先用贪心的BFS找到一个比较好的种子解(一种组合), 也可以随机产生一个种子解
        int[] rout = sa.BFS();*/

        int[] rout = new int[n + 2];//Java中数组默认初始化rout[0] = 0;
        rout[rout.length - 1] = n + 1;
        sa.build_random_sequence(rout);
        /* ---------------------- 调用模拟退火算法 ------------------------- */
        double T0 = 1e6;        //初始温度
        double d = 0.99;        //温度衰减系数
        double Tk = 1e-6;       //最低温度
        int L = 20 * rout.length;//内循环次数, 也可以赋值为一个较大的常量

        int[] rout2 = sa.Sa_TSP(rout, T0, d, Tk, L);
        sa.print(rout2);
    }
}
