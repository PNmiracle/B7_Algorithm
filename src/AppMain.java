import algorithm.SA;
import util.TspProblem;
import util.TspRandomArr;
import util.TspReader;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class AppMain {
    public static void main(String[] args) throws IOException {
        /*一共随机生成n + 2个点, 编号分别为[0...N-1], 其中0为B点(垃圾车起始点), n + 1为终点A(垃圾填埋点), 其他点为垃圾点...*/
        System.out.println("请输入生成的垃圾桶的数目n: ");
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();  //垃圾桶数目n
        int maxCap = 1000; // 每辆车的最大载重
        System.out.println("请输入垃圾车数量m : ");
        int m = scanner.nextInt();  // B点有15辆垃圾车

        /* ----------------------读入 or 随机生成  ------------------------- */
        //TspProblem problem = TspReader.readTSP("resources/eil51.txt", 12);
        TspProblem problem = TspRandomArr.generate_problem(n);
        int[] xCoors = problem.getxCoors();
        int[] yCoors = problem.getyCoors();
        int[] weights = problem.getWeights();
        System.out.print("随机生成的n + 2个点坐标:");
        for (int i = 0; i < xCoors.length; i++) {
            if (i == 0 || i == xCoors.length - 1) {
                System.out.print(" " + i + "-" + "(" + xCoors[i] + "," + yCoors[i] + ")");
            } else {
                System.out.print(" " + i + "-" + "(" + xCoors[i] + "," + yCoors[i] + "," + weights[i - 1] + ")");
            }
        }
        SA sa = new SA(problem);
        sa.setN(n);
        /*先用贪心的BFS找到一个比较好的种子解(一种组合), 也可以随机产生一个种子解
        int[] rout = sa.BFS();*/
        // rout.length - m - 1 = n
        int[] rout = new int[n + 2 + m - 1];//Java中数组默认初始化rout[0] = 0;
        rout[rout.length - 1] = n + 1;

        /* ---------------------- 调用模拟退火算法 ------------------------- */
        long before_sa = System.currentTimeMillis();
        //生成的解 m-1个0和 [1..n]组成的一个随机排序
        sa.build_random_sequence(rout, m);
        int belta = 10;
        double T0 = 100;        //初始温度
        double alpha = 0.99;        //温度衰减系数
        int maxOutIter = 2000;
        int maxInIter = 300;  //内循环次数赋值为一个较大的常量
        int[] rout_optimized = sa.Sa_TSP(rout, T0, alpha, maxOutIter, maxInIter, weights, maxCap);
        sa.print(rout_optimized, weights, maxCap);
        List<List<Integer>> rout_list = sa.split_route(rout_optimized, n);
        System.out.println();
        for (List<Integer> rout_i : rout_list) {
            System.out.println("rout_i = " + rout_i);
        }
        long after_sa = System.currentTimeMillis();
        long total_time = after_sa - before_sa;
        System.out.println("\n模拟退火算法用时: " + (total_time) + "ms");
    }
}
