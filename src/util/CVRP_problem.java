package util;

public class CVRP_problem {
    private int[] xCoors;
    private int[] yCoors;
    private int[] weights;
    private int[][] distance; // æ‡¿Îæÿ’Û


    public CVRP_problem(int[] xCoors, int[] yCoors) {
        super();
        this.xCoors = xCoors;
        this.yCoors = yCoors;
        buildDistsArrays();
    }
    public CVRP_problem(int[] xCoors, int[] yCoors, int[] weights) {
        super();
        this.xCoors = xCoors;
        this.yCoors = yCoors;
        this.weights = weights;
        buildDistsArrays();
    }

    private void buildDistsArrays() {
        distance = new int[xCoors.length][xCoors.length];
        for (int i = 0; i < xCoors.length - 1; i++) {
            distance[i][i] = 0; // ∂‘Ω«œﬂŒ™0
            for (int j = i + 1; j < xCoors.length; j++) {
                distance[i][j] = EUC_2D_dist(xCoors[i], xCoors[j], yCoors[i], yCoors[j]);
                distance[j][i] = distance[i][j];
            }
        }
    }

    public int[] getxCoors() {
        return xCoors;
    }

    public int[] getyCoors() {
        return yCoors;
    }

    public int[][] getDistance() {
        return distance;
    }

    public int[] getWeights() {
        return weights;
    }

    public static int EUC_2D_dist(int x1, int x2, int y1, int y2) {
        return (int) Math.sqrt(((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
    }

}
