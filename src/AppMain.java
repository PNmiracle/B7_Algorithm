import algorithm.SA;
import util.TspProblem;
import util.TspRandomArr;

import java.util.List;
import java.util.Scanner;


public class AppMain {
    public static void main(String[] args) {
        /*一共随机生成n + 2个点, 编号分别为[0...n+1], 其中0为B点(垃圾车起始点), n + 1为终点A(垃圾填埋点),
        其他点[1...n]为垃圾点...*/
        System.out.println("请输入生成的垃圾桶的数目n: ");
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();  //垃圾桶数目n
        /*提高要求:CVRP新添加的参数*/
        int maxCap = 1000;          // 每辆车的最大载重
        System.out.println("请输入垃圾车数量m : ");
        int m = scanner.nextInt();  // B点有m辆垃圾车

        TspProblem problem = TspRandomArr.generate_problem(n); //随机生成n + 2个不重复点的坐标、垃圾的重量(15到20kg),都在100*100的方格内
        /*获取*/
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
        /* ---------------------- 调用模拟退火算法 ------------------------- */
        SA sa = new SA(problem);
        sa.setN(n);
        /*先用贪心的BFS找到一个比较好的种子解(一种组合), 也可以随机产生一个种子解
        int[] route = sa.BFS();*/

        int[] route = new int[n + 2 + m - 1];//Java中数组默认初始化rout[i] = 0; 解的长度route.length=m+1+n
        route[route.length - 1] = n + 1;     //解的最后一位赋值为n+1号结点(终点A), 第一位为0号结点(起点B)

        long before_sa = System.currentTimeMillis();    //开始的毫秒数
        sa.build_random_sequence(route, m);  //生成的解 m-1个0和 [1..n]组成的一个随机排序
        int belta = 10;             //惩罚函数的惩罚系数
        double T0 = 100;            //初始温度
        double alpha = 0.99;        //温度衰减系数
        int maxOutIter = 2000;      //外循环的次数
        int maxInIter = 300;        //内循环次数赋值为一个较大的常量
        /*调用sa_VCRP方法求得的最优路径*/
        int[] route_optimized = sa.sa_CVRP(route, T0, alpha, maxOutIter, maxInIter, weights, maxCap, belta);
        /*打印总路径长度和总路径*/
        int total_dist = sa.cost_distance(route_optimized, weights, maxCap, belta);
        System.out.println("\n总路径长度为: " + total_dist + "\n");
        sa.print_total(route_optimized);

        /*将总路径划分为每辆车的路径*/
        List<List<Integer>> paths_list = sa.split_route(route_optimized, n);
        System.out.println();
        for (int i = 0; i < paths_list.size(); i++) {
            List<Integer> path_i = paths_list.get(i);
            System.out.println("第" + i + "号车的路径:" + path_i);
        }
        long after_sa = System.currentTimeMillis();     //结束的毫秒 数
        long total_time = after_sa - before_sa;         //总用时
        System.out.println("\n模拟退火算法用时: " + (total_time) + "ms");
    }
}
