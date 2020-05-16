package controller;

import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import entity.Edge;
import entity.Vertex;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class OptimumController {

    @FXML
    GridPane gridPane;
    SmartPlacementStrategy strategy;
    private Stage thisStage;//当前controller的Stage
    private SmartGraphPanel<Vertex, Edge> graphView;
    private Graph<Vertex,Edge> g;
    private Graph<Vertex,Edge> gr;

    private static int n = MainController.n; //边数
    private static int firstDeleteEdge;//删除的边
    private static int[] res;   //合并顺序
    private static Vertex[] vertices = MainController.vertices;//顶点
    private static Edge[] edges = MainController.edges;//边
    static String[] op = MainController.op; //每条边的对应的操作（从1开始计数）
    static int[] v = MainController.v; //每个顶点数值（从1开始计数）
    private boolean[] flag;   //是插入的还


    //当前是第几步
    private int step;

    //生成Stage时生成该Stage的Controller，Controller调用该方法把Stage传过来
    public void setStage(Stage stage) {
        thisStage = stage;
    }

    private Graph<Vertex, Edge> build_flower_graph() {

        g = new GraphEdgeList<>();

        vertices = new Vertex[n + 1];
        for (int i = 1; i <= n; i++) {
            Vertex vertex = new Vertex(i, v[i]);
            vertices[i] = vertex;
            g.insertVertex(vertex);
        }

        edges = new Edge[n + 1];
        for (int i = 1; i <= n; i++) {
            Edge edge = new Edge(i, op[i]);
            edges[i] = edge;
        }

        g.insertEdge(vertices[n], vertices[1], edges[1]);
        for (int i = 1; i <= n - 1; i++) {
            g.insertEdge(vertices[i], vertices[i + 1], edges[i + 1]);
        }

        return g;
    }

    public void exitBtnClicked() {
        thisStage.close();
    }

    @FXML
    void lastClicked(){  //点击上一步
        if(step == 0)return;

    }

    @FXML
    void nextClicked() throws InterruptedException {
        if(step == n)return;
        if(step == 0){  //第一步，删除这条边
            graphView.getStylableEdge(edges[firstDeleteEdge]).setStyle("-fx-stroke: red;");
            graphView.update();
            //TimeUnit.SECONDS.sleep(1);//秒
            g.removeEdge((com.brunomnsilva.smartgraph.graph.Edge<Edge, Vertex>) g.edges().toArray()[firstDeleteEdge-1]);

            g.cleanE();
            g.cleanV();

            //gr = new GraphEdgeList<>();
            for(int i=firstDeleteEdge; i<=n; i++)
                g.insertVertex(vertices[i]);
            for(int i=1; i<firstDeleteEdge; i++)
                g.insertVertex(vertices[i]);
            for(int i=firstDeleteEdge; i<n; i++)
                g.insertEdge(vertices[i],vertices[i+1],edges[i+1]);
            if(firstDeleteEdge != 1)
                g.insertEdge(vertices[n],vertices[1],edges[1]);
            for(int i=1;i<firstDeleteEdge-1;i++)
                g.insertEdge(vertices[i],vertices[i+1],edges[i+1]);


            for(int i=1;i<n;i++){
                if(res[i]>firstDeleteEdge)res[i] -= firstDeleteEdge;
                else
                    res[i] += n-firstDeleteEdge;
            }

            step++;
            graphView.update();
        }
        else {
            //删除这条边
            int de = res[step]; //删除第几条边

            //修改左边顶点的值
            //新建一个顶点，赋值
            int v;
            String s = ((com.brunomnsilva.smartgraph.graph.Edge<Edge, Vertex>) g.edges().toArray()[de-1]).element().operation;
            if(s.equals("+")){
                //v = vertices[leftv].value+vertices[de].value;
                v = ((com.brunomnsilva.smartgraph.graph.Vertex<Vertex>)g.vertices().toArray()[de-1]).element().value
                        + ((com.brunomnsilva.smartgraph.graph.Vertex<Vertex>)g.vertices().toArray()[de]).element().value;
            }
            else{
                //v = vertices[leftv].value*vertices[de].value;
                v = ((com.brunomnsilva.smartgraph.graph.Vertex<Vertex>)g.vertices().toArray()[de-1]).element().value
                        * ((com.brunomnsilva.smartgraph.graph.Vertex<Vertex>)g.vertices().toArray()[de]).element().value;
            }
            //代替靠左的顶点
            ((com.brunomnsilva.smartgraph.graph.Vertex<Vertex>)g.vertices().toArray()[de-1]).element().value = v;


            //如果删除的是最后的一条边,那就直接删除吧
            if(de == n-step){
                g.removeVertex((com.brunomnsilva.smartgraph.graph.Vertex<Vertex>) g.vertices().toArray()[de]);
                graphView.update();
            }

            else{
                //修改下一条边
                Edge[] vs = new Edge[n-step];
                for(int i=0;i<n-step;i++)
                    vs[i] = ((com.brunomnsilva.smartgraph.graph.Edge<Edge, Vertex>) g.edges().toArray()[i]).element();

                g.cleanE();
                for(int i=1;i<de;i++)
                    g.insertEdge(((com.brunomnsilva.smartgraph.graph.Vertex<Vertex>)g.vertices().toArray()[i-1]).element(),
                            ((com.brunomnsilva.smartgraph.graph.Vertex<Vertex>)g.vertices().toArray()[i]).element(),
                            vs[i-1]);


                g.insertEdge(((com.brunomnsilva.smartgraph.graph.Vertex<Vertex>)g.vertices().toArray()[de-1]).element(),
                        ((com.brunomnsilva.smartgraph.graph.Vertex<Vertex>)g.vertices().toArray()[de+1]).element(),
                        vs[de]);

                for(int i=de+2;i<=n-step;i++)
                    g.insertEdge(((com.brunomnsilva.smartgraph.graph.Vertex<Vertex>)g.vertices().toArray()[i-1]).element(),
                            ((com.brunomnsilva.smartgraph.graph.Vertex<Vertex>)g.vertices().toArray()[i]).element(),
                            vs[i-1]);


                //删除这条边
                //g.removeEdge((com.brunomnsilva.smartgraph.graph.Edge<Edge, Vertex>) g.edges().toArray()[de-1]);

                //删除顶点
                g.removeVertex((com.brunomnsilva.smartgraph.graph.Vertex<Vertex>) g.vertices().toArray()[de]);

                //更新res
                for(int i=step+1;i<=n-1;i++)
                    if(res[i]>de)res[i] -= 1;

                graphView.update();
            }
            graphView.update();
            step++;
        }
    }

    public void buildGraph() {

        setStage(thisStage);

        step = 0;
        g = build_flower_graph();
        strategy = new SmartCircularSortedPlacementStrategy();
        graphView = new SmartGraphPanel<>(g, strategy);


//        if (g.numVertices() > 0) {
//            graphView.getStylableVertex("A").setStyle("-fx-fill: gold; -fx-stroke: brown;");
//        }

        gridPane.add(graphView, 0, 0);

        //计算方案,拿到第一次删除的边，和合并顺序
        DP dp = new DP();
        res = dp.run();
        firstDeleteEdge = DP.deleteEdge;


    }

    public void initGraph() {
        graphView.init();
    }
}

