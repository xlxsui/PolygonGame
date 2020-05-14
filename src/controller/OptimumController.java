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
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public class OptimumController {

    @FXML
    GridPane gridPane;

    private Stage thisStage;//当前controller的Stage
    private SmartGraphPanel<Vertex, Edge> graphView;
    private Graph<Vertex,Edge> g = MainController.g;

    private static int n = MainController.n; //边数
    private static int firstDeleteEdge;//删除的边
    private static int[] res;   //合并顺序
    private static Vertex[] vertices = MainController.vertices;//顶点
    private static Edge[] edges = MainController.edges;//边

    //当前是第几步
    private int step = 0;


    //生成Stage时生成该Stage的Controller，Controller调用该方法把Stage传过来
    public void setStage(Stage stage) {
        thisStage = stage;
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
            TimeUnit.SECONDS.sleep(1);//秒
            g.removeEdge((com.brunomnsilva.smartgraph.graph.Edge<Edge, Vertex>) g.edges(step).toArray()[firstDeleteEdge-1]);
            graphView.update();
            step++;
        }
        else {
            //删除这条边
            g.removeEdge((com.brunomnsilva.smartgraph.graph.Edge<Edge, Vertex>) g.edges(step).toArray()[res[step]-1]);
            //删除靠右的顶点
            g.removeVertex((com.brunomnsilva.smartgraph.graph.Vertex<Vertex>) g.vertices(step).toArray()[res[step]-1]);
            //修改左边顶点的值

            graphView.update();
            step++;
        }
    }

    public void buildGraph() {
        step = 0;

        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
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

