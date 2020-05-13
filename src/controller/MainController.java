package controller;

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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    @FXML
    GridPane gridPane;

    private Stage thisStage;//当前controller的Stage
    private SmartGraphPanel<Vertex, Edge> graphView;
    public List<Vertex> vertexList = new ArrayList<>();

    public List<Edge> edgeList = new ArrayList<>();


    //生成Stage时生成该Stage的Controller，Controller调用该方法把Stage传过来
    public void setStage(Stage stage) {
        thisStage = stage;
    }

    public void exitBtnClicked() {
        thisStage.close();
    }

    private Graph<Vertex, Edge> build_flower_graph() {

        Graph<Vertex, Edge> g = new GraphEdgeList<>();

        for (int i = 0; i < 3; i++) {
            Vertex vertex = new Vertex(i, (int) (100 - Math.random() * 200));
            vertexList.add(vertex);
            g.insertVertex(vertex);
        }

        for (int i = 0; i < 3; i++) {
            double random = Math.random();
            Edge edge = new Edge(i, random > 0.5 ? "*" : "+");
            edgeList.add(edge);
        }

        g.insertEdge(vertexList.get(0), vertexList.get(2), edgeList.get(0));
        g.insertEdge(vertexList.get(0), vertexList.get(1), edgeList.get(1));
        g.insertEdge(vertexList.get(1), vertexList.get(2), edgeList.get(2));

        return g;
    }

    public void buildGraph() {
        Graph<Vertex, Edge> g = build_flower_graph();
        System.out.println(g);

        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        graphView = new SmartGraphPanel<>(g, strategy);

        // 给点设置样式。。
        if (g.numVertices() > 0) {
            graphView.getStylableVertex(vertexList.get(0)).setStyle("-fx-fill: gold; -fx-stroke: brown;");
        }

        gridPane.add(graphView, 0, 0);
    }

    public void initGraph() {
        graphView.init();
    }

    public void onOptimumBtnClicked() throws IOException {
        //动态加载窗口fxml界面
        Stage stage = new Stage();
        //getResource是定位到当前类目录，..jar返回不了，注意大小写。/开头定位到根目录，相当于src
        URL location = getClass().getResource("/fxml/optimum.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root = fxmlLoader.load();


        //获取界面的Controller的实例对象
        OptimumController controller = fxmlLoader.getController();
        controller.setStage(stage);//让控制器获取到Stage
        controller.buildGraph();


        //set Icon
        stage.initStyle(StageStyle.DECORATED);
        stage.getIcons().add(new Image(getClass().getResource("/resources/icon.png").toExternalForm()));
        stage.setTitle("最优方案");
        stage.setScene(new Scene(root, 1024, 700));
        stage.show();
        controller.initGraph();
    }
}

