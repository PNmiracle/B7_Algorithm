package util;

import java.util.*;

public class BuildRandom {

    /**
     * 生成存放n+2个不重复点的map集合
     * @param n
     * @return java.util.Map<java.lang.Integer,java.lang.String>
     */
    private static Map<Integer, String> set_Rd(int n) {
        Random rd = new Random(System.currentTimeMillis());
        Map<Integer, String> map = new HashMap();
        while (n > 0) {
            int x = rd.nextInt(100);        //随机生成[0..99]的整数
            int y = rd.nextInt(100);
            if (!map.containsValue(x + "," + y))    //以字符串的形式存储过滤重复
                map.put(n--, x + "," + y);
        }
        return map;
    }


    /**
     * 随机生成不重复的n+2个坐标与垃圾桶的重量
     * @param n 垃圾桶数目
     * @return java.util.List<int[]> 包含xCoors[],yCoors[]和垃圾桶重量weights[]的集合
     */
    public static List<int[]> build_Rd_Coors_weights(int n) {
        List<int[]> ranList = new ArrayList<>();
        int[] xCoors = new int[n + 2];
        int[] yCoors = new int[n + 2];
        int[] weights = new int[n + 2];

        Map<Integer, String> map = set_Rd(n + 2);   //生成存放n+2个不重复点的map集合
        for (int i = 1; i < map.size() + 1; i++) {
            String s = map.get(i);
            String delimeter = ",";  // 指定分割字符
            String[] split = s.split(delimeter);
            xCoors[i - 1] = Integer.parseInt(split[0]);
            yCoors[i - 1] = Integer.parseInt(split[1]);
        }
/* ---------------------- 随机生成垃圾筒的重量, 假设至少为15kg, 最多为20kg且为整数 ------------------------- */
        Random rd = new Random(System.currentTimeMillis());
        for (int i = 1; i <= n; i++) {
            weights[i] = rd.nextInt(5) + 15;
        }
        ranList.add(xCoors);
        ranList.add(yCoors);
        ranList.add(weights);
        return ranList;
    }

    public static CVRP_problem generate_problem(int n) {
        List<int[]> list = BuildRandom.build_Rd_Coors_weights(n);
        int[] xCoors = list.get(0);
        int[] yCoors = list.get(1);
        int[] weights = list.get(2);
        CVRP_problem CVRPproblem = new CVRP_problem(xCoors, yCoors, weights);
        return CVRPproblem;
    }
}
