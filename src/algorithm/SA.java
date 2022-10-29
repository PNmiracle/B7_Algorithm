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
     * 给rout[1..n]赋值为[1...n]的不重复的序列,
     * 每次选取之后就要收缩随机数的值域
     */
    // rout.length - m - 1 = n
    public void build_random_sequence(int[] rout, int m) {
        int n = rout.length - m - 1;
        int[] numbers = new int[n];
        // numbers存放[1..n]
        for (int i = 0; i < n; i++) {
            numbers[i] = i + 1;
        }
        int n_dup = n;
        for (int i = 1; i <= n_dup; i++) {
            //Math.random() :[0, 1) 的double
            //r:[0..n - 1] 的整数
            int r = (int) (Math.random() * n);
            rout[i] = numbers[r];
            /**每次生成的下标是有可能重复的，但是由于numbers数组里该下标对应的值每抽中一次，
             就被踢出去了（替换成了n-1位置上的值），所以最终结果并不会重复*/
            numbers[r] = numbers[n - 1];
            n--;
        }
    }


    public int cost_distance(int[] route, int[] weights, int maxCap, int belta) {
        /*计算该种解法(组合)的超重容量总和*/
        int validCap = 0;
        List<List<Integer>> list = SA.split_route(route, n);
        // 遍历所有垃圾车的路径
        for (int i = 0; i < list.size(); i++) {
            //去掉起点和重点, 遍历一辆垃圾车走过路径中包含的所有垃圾桶
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

        /*关键: 增加惩罚项 , 惩罚系数belta = 10*/
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
     * 实现交叉互换，随机出两个不相同随机数，然后交换那两个位置的点
     * 此外还有移位法, 倒置法等
     *
     * @param route
     * @return 输出经过交换的新路径
     */
    public int[] swap(int[] route) {
        Random random = new Random();
        //int r1 = random.nextInt(route.length);   //随机产生一个范围在[0..route.length)的整数
        int r1 = random.nextInt(route.length - 2) + 1;   //随机产生一个范围在[1..route.length - 1)的整数
        //int r2 = random.nextInt(route.length);
        int r2 = random.nextInt(route.length - 2) + 1;
        while (r1 == r2) {
            r2 = random.nextInt(route.length - 2) + 1;   //保证随机数r1, r2不同
        }
        int[] change = copyRout(route);
        int tmp = change[r1];
        change[r1] = change[r2];
        change[r2] = tmp;
        return change;
    }

    /**
     * 模拟退火算法SA
     */
    public int[] sa_CVRP(int[] route, double T0, double alpha, int maxOutIter, int maxInIter, int[] weights, int maxCap, int belta) {

        int[] bestpath, curentpath;
        //t:此刻的温度变量
        double t = T0;
        // 起始时刻:复制种子解rout到curentpath,和 bestpath中
        bestpath = curentpath = copyRout(route);
        Random random = new Random();
        for (int i = 0; i < maxOutIter; i++) {  // 当达到最低温度时停止循环

            System.out.println("第" + (i) + "次迭代, 当前路径的总消耗(带惩罚项)为:" + cost_distance(curentpath, weights, maxCap, belta)); //此行可注释掉, 减少运行时间
            for (int j = 0; j < maxInIter; j++) {
                int[] update_path = swap(curentpath);//在当前解A附近随机产生新解B,此处用交换法
                int delta = cost_distance(update_path, weights, maxCap, belta) - cost_distance(curentpath, weights, maxCap, belta);
                if (delta < 0) {//为负值，即结果成本降低了，则接受
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
        System.out.print("总转运路径：" + route[0] + "号结点(起点B)");
        for (int i = 1; i < route.length - 1; i++) {
            if (route[i] == 0) {
                System.out.print("->" + "0号结点(起点B)");
            } else {
                System.out.print("->" + "垃圾桶" + route[i] + "号");
            }
        }
        System.out.print("->" + route[route.length - 1] + "号结点(终点A)");
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

            //(i + 1) < rout_optimized.length 防止数组越界
            if ((i + 1) < rout_optimized.length && rout_optimized[i + 1] == 0) {
                temp.add(n + 1);
                splited_list.add(temp);
                temp = new ArrayList<>();
                temp.add(0);
            }
            // 若序列的倒数第二位是垃圾桶, 则将该路径直接加入list
            if (i == rout_optimized.length - 2 && rout_optimized[i + 1] != 0) {
                //temp.add(n + 1);
                splited_list.add(temp);
            }
        }
        return splited_list;
    }
}
