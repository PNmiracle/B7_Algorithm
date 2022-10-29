package util;

import java.util.*;

public class BuildRandom {

    /**
     * ���ɴ��n+2�����ظ����map����
     * @param n
     * @return java.util.Map<java.lang.Integer,java.lang.String>
     */
    private static Map<Integer, String> set_Rd(int n) {
        Random rd = new Random(System.currentTimeMillis());
        Map<Integer, String> map = new HashMap();
        while (n > 0) {
            int x = rd.nextInt(100);        //�������[0..99]������
            int y = rd.nextInt(100);
            if (!map.containsValue(x + "," + y))    //���ַ�������ʽ�洢�����ظ�
                map.put(n--, x + "," + y);
        }
        return map;
    }


    /**
     * ������ɲ��ظ���n+2������������Ͱ������
     * @param n ����Ͱ��Ŀ
     * @return java.util.List<int[]> ����xCoors[],yCoors[]������Ͱ����weights[]�ļ���
     */
    public static List<int[]> build_Rd_Coors_weights(int n) {
        List<int[]> ranList = new ArrayList<>();
        int[] xCoors = new int[n + 2];
        int[] yCoors = new int[n + 2];
        int[] weights = new int[n + 2];

        Map<Integer, String> map = set_Rd(n + 2);   //���ɴ��n+2�����ظ����map����
        for (int i = 1; i < map.size() + 1; i++) {
            String s = map.get(i);
            String delimeter = ",";  // ָ���ָ��ַ�
            String[] split = s.split(delimeter);
            xCoors[i - 1] = Integer.parseInt(split[0]);
            yCoors[i - 1] = Integer.parseInt(split[1]);
        }
/* ---------------------- �����������Ͳ������, ��������Ϊ15kg, ���Ϊ20kg��Ϊ���� ------------------------- */
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
