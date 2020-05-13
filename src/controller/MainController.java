package controller;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
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

public class MainController {
    @FXML
    GridPane gridPane;

    private Stage thisStage;//当前controller的Stage
    private SmartGraphPanel<String, String> graphView;

    //生成Stage时生成该Stage的Controller，Controller调用该方法把Stage传过来
    public void setStage(Stage stage) {
        thisStage = stage;
    }

    public void exitBtnClicked(){
        thisStage.close();
    }

    private Graph<String, String> build_flower_graph() {

        Graph<String, String> g = new GraphEdgeList<>();

        g.insertVertex("A");
        g.insertVertex("B");
        g.insertVertex("C");
        g.insertVertex("D");
        g.insertVertex("E");
        g.insertVertex("F");
        g.insertVertex("G");

        g.insertEdge("A", "B", "1");
        g.insertEdge("A", "C", "2");
        g.insertEdge("A", "D", "3");
        g.insertEdge("A", "E", "4");
        g.insertEdge("A", "F", "5");
        g.insertEdge("A", "G", "6");

        g.insertVertex("H");
        g.insertVertex("I");
        g.insertVertex("J");
        g.insertVertex("K");
        g.insertVertex("L");
        g.insertVertex("M");
        g.insertVertex("N");

        g.insertEdge("H", "I", "7");
        g.insertEdge("H", "J", "8");
        g.insertEdge("H", "K", "9");
        g.insertEdge("H", "L", "10");
        g.insertEdge("H", "M", "11");
        g.insertEdge("H", "N", "12");

        g.insertEdge("A", "H", "0");

        //g.insertVertex("ISOLATED");

        return g;
    }

    public void buildGraph() {
        Graph<String, String> g = build_flower_graph();
        System.out.println(g);

        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        graphView = new SmartGraphPanel<>(g, strategy);

        if (g.numVertices() > 0) {
            graphView.getStylableVertex("A").setStyle("-fx-fill: gold; -fx-stroke: brown;");
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
