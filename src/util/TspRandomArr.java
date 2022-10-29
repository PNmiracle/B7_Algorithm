package util;

import java.util.*;

public class TspRandomArr {
    private static Map<Integer, String> setRd(int n) {
        Random out = new Random(System.currentTimeMillis());
        Map<Integer, String> map = new HashMap();
        while (n > 0) {
            int x = out.nextInt(100);
            int y = out.nextInt(100);
            if (!map.containsValue(x + "," + y))
                map.put(n--, x + "," + y);
        }
        return map;
    }

    /*������ɲ��ظ���n+2������*/
    /*�޸�: ��������������*/
    public static List<int[]> ranCorArr(int n) {
        List<int[]> ranCorArrs = new ArrayList<>();
        int[] xCoors = new int[n + 2];
        int[] yCoors = new int[n + 2];
        int[] weights = new int[n + 2];

        Map<Integer, String> map = setRd(n + 2);
        for (int i = 1; i < map.size() + 1; i++) {
            String s = map.get(i);
            String delimeter = ",";  // ָ���ָ��ַ�
            String[] split = s.split(delimeter);
            xCoors[i - 1] = Integer.parseInt(split[0]);
            yCoors[i - 1] = Integer.parseInt(split[1]);
        }
        /* ---------------------- �����������Ͳ������, ��������Ϊ5kg, ���Ϊ30kg��Ϊ���� ------------------------- */
        Random out = new Random(System.currentTimeMillis());
        for (int i = 0; i < n; i++) {
            weights[i] = out.nextInt(5) + 15;
        }
        ranCorArrs.add(xCoors);
        ranCorArrs.add(yCoors);
        ranCorArrs.add(weights);

        return ranCorArrs;
    }

    public static TspProblem generate_problem(int n) {
        List<int[]> list = TspRandomArr.ranCorArr(n);
        int[] xCoors = list.get(0);
        int[] yCoors = list.get(1);
        int[] weights = list.get(2);
        TspProblem tspProblem = new TspProblem(xCoors, yCoors, weights);
        return tspProblem;
    }
}
