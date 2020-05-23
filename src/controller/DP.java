package controller;

import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;

import java.util.Stack;

public class DP {
    static int n = MainController.n; //多边形边数
    static String[] op = MainController.op; //每条边的对应的操作（从1开始计数）
    static int[] v = MainController.v; //每个顶点数值（从1开始计数）
    private static int[][][] m; //m[i][n][1]：第三个维度为0是最小值，为1是最大值
    private static int[][][] cut; //记录合并点的数组，即某条链断开的边的位置，同m用法
    private Stack<Integer> s; //用栈保存合并边的顺序
    static int deleteEdge; //记录最优情况下，第1条删除的边
    private long highestScore; //记录最高得分
    private int minf, maxf;//记录目前确定了i，j，s后的这条链的最大和最小值
    private static int[] res;

    /**
     * 初始化
     **/
    public DP() {//int n, int[] v, String[] op, int[][][] m
        //this.n = n;
        //        this.v = v;
        //        this.op = op;
        //        this.m = m;
        this.cut = new int[n + 1][n + 1][2];
        this.s = new Stack<>();

        DigraphEdgeList digraphEdgeList = new DigraphEdgeList();
    }

    /**
     * 求当前以i开始长度为j，断点为s时，主链的最大和最小值、、、
     **/
    void minMax(int i, int s, int j) {
        int r = (i + s - 1) % n + 1; //断开位置
        int a = m[i][s][0], b = m[i][s][1], c = m[r][j - s][0], d = m[r][j - s][1];
        if (op[r] == "+") {
            minf = a + c;
            maxf = b + d;
        } else {
            int[] e = new int[]{0, a * c, a * d, b * c, b * d};
            minf = e[1];
            maxf = e[1];
            for (int k = 2; k < 5; k++) {
                if (minf > e[k]) minf = e[k];
                if (maxf < e[k]) maxf = e[k];
            }
        }
    }

    /**
     * 填充m[][][]和 cut[][][],找到最高分和第一次删除的边
     **/
    long polyMax() {
        //找到不同起点不同长度能取到最大值和最小值的断点s，即op[(i+s)%n]
        for (int j = 2; j <= n; j++) { //链的长度
            for (int i = 1; i <= n; i++) { //删除第i条边
                m[i][j][0] = 99999;
                m[i][j][1] = -99999;
                for (int s = 1; s < j; s++) { //断开的位置
                    this.minMax(i, s, j);//找到最小和最大值
                    if (m[i][j][0] > minf) {
                        m[i][j][0] = minf;
                        cut[i][j][0] = s; //记录该链取得最小值的断点
                        //cut是点睛之笔，方便从整条链递归地找到断开他的子链的相对位置s
                    }
                    if (m[i][j][1] < maxf) {
                        m[i][j][1] = maxf;
                        cut[i][j][1] = s; //记录该链取得最大值的断点
                    }
                }
            }
        }

        //找到从某个顶点开始能使结果得最大值（即该顶点前面的边：同为i-进行删除）
        highestScore = m[1][n][1];
        deleteEdge = 1;
        for (int i = 2; i <= n; i++) {
            if (highestScore < m[i][n][1]) {
                highestScore = m[i][n][1];
                deleteEdge = i;
            }
        }
        System.out.println("第一条删除的边：" + deleteEdge);

        //将边信息进栈
        getSort(deleteEdge, n, 1);
        //打印在删除第firstDelEdge条边后的最优合并顺序
        int i = 1;
        while (!s.empty()) {
            int p = s.pop();
            System.out.println("第" + i + "次合并的边：" + p);
            res[i] = p;
            i++;
        }

        return highestScore;
    }

    /**
     * 找到已经确认的一整条链了，根据cut将合并过程放进栈s：
     * i ： 起点顶点
     * j ： 链长度
     * maxOrMin ： 这条链是要最大值还是最小值.最大值为1，最小值为0
     **/
    //递归地求这一整条链，已有cut[][][]来记录当起点为i长度为j的取最大和最小的断点，递归地计算，直到最后只剩下一个顶点
    void getSort(int i, int j, int maxOrMin) {
        //已经确定删除哪条边
        int s, r;
        if (j == 1) return; //链中只有一个顶点，直接返回
        if (j == 2) {
            r = i % n + 1;//两个顶点之间的边
            this.s.push(r);
            return; //只有两个顶点时，没有子链，无须递归
        }

        //链中有两个以上的顶点时，将最优的边入栈
        if (maxOrMin == 1) s = cut[i][j][1];
        else s = cut[i][j][0];
        //根据s找到合并边位置
        r = (i + s - 1) % n + 1;
        this.s.push(r);

        //递归求断点左右两边的值
        if (this.op[r] == "+") { //当合并计算为"+"操作时
            if (maxOrMin == 1) { //都取得最大值
                getSort(i, s, 1);
                getSort(r, j - s, 1);
            } else {         //都取得最小值
                getSort(i, s, 0);
                getSort(r, j - s, 0);
            }
        }

        //当合并计算为"*"操作时
        else {
            int a = m[i][s][0], b = m[i][s][1], c = m[r][j - s][0], d = m[r][j - s][1];
            int[] e = new int[]{0, a * c, a * d, b * c, b * d};
            int mergeMax = e[1], mergeMin = e[1];
            //找最大和最小
            for (int k = 2; k <= 4; k++) {
                if (e[k] > mergeMax) mergeMax = e[k];
                if (e[k] < mergeMin) mergeMin = e[k];
            }

            //判断合并得到的父链是取最大还是取最小
            int merge;  //最终合并结果
            if (maxOrMin == 1) merge = mergeMax;
            else merge = mergeMin;

            //根据两条链各取，递归求自链
            if (merge == e[1]) {                     //ac
                getSort(i, s, 0);
                getSort(r, j - s, 0);
            } else if (merge == e[2]) {                 //ad
                getSort(i, s, 0);
                getSort(r, j - s, 1);
            } else if (merge == e[3]) {              //bc
                getSort(i, s, 1);
                getSort(r, j - s, 0);
            } else {                             //bd
                getSort(i, s, 1);
                getSort(r, j - s, 1);
            }
        }
    }

    public static int[] run() {
        //初始化
        m = new int[n + 1][n + 1][2];
        res = new int[n];
        DP poly = new DP();
        for (int i = 1; i <= n; i++) {
            //默认只有顶点本身的值
            m[i][1][0] = m[i][1][1] = v[i];
        }

        long result = poly.polyMax();
        return res;


        //输出结果
        //long result = poly.polyMax();
        //System.out.println("最高分：" + result);
    }
}
