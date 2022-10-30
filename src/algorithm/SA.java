package algorithm;

import util.CVRP_problem;

import java.util.*;

public class SA {

    CVRP_problem problem;
    public int n;   //垃圾桶数量

    public void setN(int n) {
        this.n = n;
    }

    public SA(CVRP_problem problem) {
        this.problem = problem;
    }

    /**
     * 给route[]创建随机序列(route[0]和route[n+1]不变)
     * 给route[1..倒数第二个]赋值为[1...n]的不重复的序列,
     * 每次选取之后就要收缩随机数的值域
     * @param route 一种解
     * @param m     垃圾车的数量
     * 垃圾车数目 n = route.length - m - 1, route.length = n + m - 1 + 2
     */
    public void build_random_sequence(int[] route, int m) {
        ArrayList<Integer> numbers = new ArrayList<>(route.length - 2);
        for (int i = 0; i < (route.length - 2); i++) {
            numbers.add(i + 1); //numbers动态数组存放[1..route.length - 2]
        }
        for (int i = 1; i <= (route.length - 2); i++) {
            int r = (int) (Math.random() * numbers.size());  //r:随机数的坐标,范围不断缩小[0..numbers.size - 1]
            route[i] = numbers.get(r);
            numbers.remove(r);      //相当于用完一次就划掉
            if (route[i] > n) {     //超出范围的就赋值为0 (截断掉)
                route[i] = 0;
            }
        }
    }

    /**
     * 计算此种解法的 带惩罚项的花费 和 总路径长度
     * @param route         此种解法
     * @param weights       垃圾桶重量数组
     * @param maxCap        垃圾车最大容量
     * @param beta         惩罚系数
     * @return distPenalty[0] 总路径长度, 带惩罚项的总花费
     */
    public int[] dist_penalty(int[] route, int[] weights, int maxCap, int beta) {
        int[] disPenaltyArr = new int[2];
        /*计算该种解法(组合)的超重容量总和*/
        int validCap = 0;
        int cap_temp = maxCap;
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
            maxCap = cap_temp;  //换了下一辆车,容量要及时更新, 不然一直减会出错
        }
        int[][] dist = problem.getDistance();
        int totalDist = 0;
        for (int i = 0; i < list.size(); i++) {
            List<Integer> path_i = list.get(i);
            for (int j = 0; j < path_i.size() - 1; j++) {
                totalDist += dist[path_i.get(j)][path_i.get(j + 1)];
            }
        }

        /*关键: 增加惩罚项 , 惩罚系数belta = 10*/
        disPenaltyArr[0] = totalDist;
        int totalDistPenalty = validCap * beta + totalDist;
        disPenaltyArr[1] = totalDistPenalty;
        return disPenaltyArr;
    }
    /**
     * 复制数组
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
     * 关键: 在旧解A附近产生新解B的方法, 相当于对解进行一个洗牌
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
        int[] change = copy_route(route);
        int tmp = change[r1];
        change[r1] = change[r2];
        change[r2] = tmp;
        return change;
    }

    /**
     * 模拟退火算法SA
     * @param route         初始解
     * @param T0            初始温度
     * @param alpha         温度衰减系数
     * @param maxOutIter    外层循环次数
     * @param maxInIter     内层循环次数
     * @param weights       垃圾桶的重量数组
     * @param maxCap        垃圾车的最大容量
     * @param beta         惩罚系数
     *
     * @return 输出遗传退火算法优化后得到的较优解
     */
    public int[] sa_CVRP(int[] route, double T0, double alpha, int maxOutIter, int maxInIter, int[] weights, int maxCap, int beta) {

        int[] bestpath, curentpath;     //优化的最优路径和当前路径(抽象解)
        double t = T0;                  //t:此刻的温度变量
        bestpath = curentpath = copy_route(route);// 起始时刻:复制种子解route到curentpath,和 bestpath中
        Random rd = new Random();
        for (int i = 0; i < maxOutIter; i++) {  // 设置的外循环次数为1000
            int[] distPenaltyArr = dist_penalty(curentpath, weights, maxCap, beta);
            System.out.println("第" + (i + 1) + "次迭代, 当前路径的总消耗(带惩罚项)为:" + distPenaltyArr[1] + "  总路径长度为:" + distPenaltyArr[0]);        //TODO: 此行可注释掉, 减少运行时间
            for (int j = 0; j < maxInIter; j++) {
                int[] update_path = swap(curentpath);//在当前解A附近随机产生新解B,此处用交换法
                int[] distPenaltyArr_upd = dist_penalty(update_path, weights, maxCap, beta);
                int cost_update = distPenaltyArr_upd[1];    //distPenaltyArr_upd[1]为加上惩罚项的花费
                int[] distPenaltyArr_cur = dist_penalty(curentpath, weights, maxCap, beta);
                int cost_current = distPenaltyArr_cur[1];

                int delta = cost_update - cost_current;
                if (delta < 0) {//为负值，即结果成本降低了，则接受
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
     * 输入当前解打印总路径
     * @param route 输入当前解
     */
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

    /**
     * 将抽象的解切分为具体每辆车的转运路径
     * @param route 输入一个抽象的解
     * @param n     垃圾桶数量
     * @return  splited_list: 每辆车的转运路径构成集合的集合
     */
    public static List<List<Integer>> split_route(int[] route, int n) {
        ArrayList<List<Integer>> splited_list = new ArrayList<>(); //每辆车的转运路径构成集合的集合
        ArrayList<Integer> buffer = new ArrayList<>();      //存放具体每辆车的转运路径的缓冲区
        buffer.add(0);      //具体每辆车的转运路径的第一个元素是起点B
        for (int i = 0; i < route.length; i++) {
            if (route[i] == 0) {
                continue;       //如果抽象解中元素值为0,不添加到具体解的缓冲区
            }
            buffer.add(route[i]);//抽象解中元素值不为0, 则添加到具体解的缓冲区

            //(i + 1) < route.length 防止数组越界
            if ((i + 1) < route.length && route[i + 1] == 0) {
                buffer.add(n + 1);          //将终点A添加到具体解的缓冲区中
                splited_list.add(buffer);   //具体解添加完成, 添加到结果集合list中
                buffer = new ArrayList<>(); //新建一个空的缓冲区
                buffer.add(0);              //新缓冲区添加起点B
            }
            // 若解序列的倒数第二位是垃圾桶, 则将该路径直接加入list
            if (i == route.length - 2 && route[i + 1] != 0) {
                //buffer.add(n + 1); debug结果:下一次循环会将n+1添加到已经加入list的最后的缓冲区中
                splited_list.add(buffer);
            }
        }
        return splited_list;
    }
}
