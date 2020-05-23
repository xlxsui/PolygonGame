package controller;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphEdge;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import entity.Edge;
import entity.Vertex;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

public class MainController {
    @FXML
    GridPane gridPane;
    @FXML
    ComboBox comboBox;

    private Stage thisStage;//当前controller的Stage
    private SmartGraphPanel<Vertex, Edge> graphView;
    static Vertex[] vertices;
    static Edge[] edges;
    public static GraphEdgeList<Vertex, Edge> g;
    public static GraphEdgeList<Vertex, Edge> gBack;
    private int step = 0; // 第0步开始

    public static int n = 5; //多边形边数
    static String[] op; //每条边的对应的操作（从1开始计数）
    static int[] v; //每个顶点数值（从1开始计数）


    //生成Stage时生成该Stage的Controller，Controller调用该方法把Stage传过来
    public void setStage(Stage stage) {
        thisStage = stage;
    }

    public void exitBtnClicked() {
        thisStage.close();
    }

    public void onOptimumBtnClicked() throws IOException {
        graphView.update();

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

    public void onBackBtnClicked() {
        if (g.edges != gBack.edges) {
            step--;
            g.edges = gBack.edges;
            g.vertices = gBack.vertices;
            graphView.update();
        } else {
            JFXAlert alert = new JFXAlert(thisStage);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setOverlayClose(false);
            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setHeading(new Label("提示"));
            layout.setBody(new Label("只能撤回一步哟！"));

            JFXButton closeButton = new JFXButton("确定");
            closeButton.setOnAction(event -> alert.hideWithAnimation());

            layout.setActions(closeButton);
            alert.setContent(layout);
            alert.show();
        }
    }

    public void onRestartBtnClicked() {
        String level = comboBox.getValue().toString();
        switch (level) {
            case "level" + 1:
                n = 3;
                break;
            case "level" + 2:
                n = 5;
                break;
            case "level" + 3:
                n = 6;
                break;
            case "level" + 4:
                n = 7;
                break;
            case "level" + 5:
                n = 8;
                break;
            default:
                n = 5;
        }

        // 重建那两个数组，重新刷新g

        //生成随机数据
        String[] opp = {"+", "*"};//运算符数组
        Random ran = new Random();
        //生成随机运算符和顶点数值
        op = new String[n + 1];
        v = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            int whichop = ran.nextInt(2);
            op[i] = opp[whichop];
            int addOr = ran.nextInt(2);
            if (addOr == 0) v[i] = ran.nextInt(10);
            else v[i] = -ran.nextInt(10);
        }

        // g不能赋值改变，可以改里面的值。
        GraphEdgeList<Vertex, Edge> tmp = new GraphEdgeList<>();
        vertices = new Vertex[n + 1];
        for (int i = 1; i <= n; i++) {
            Vertex vertex = new Vertex(i, v[i]);
            vertices[i] = vertex;
            tmp.insertVertex(vertex);
        }

        edges = new Edge[n + 1];
        for (int i = 1; i <= n; i++) {
            Edge edge = new Edge(i, op[i]);
            edges[i] = edge;
        }

        tmp.insertEdge(vertices[n], vertices[1], edges[1]);
        for (int i = 1; i <= n - 1; i++) {
            tmp.insertEdge(vertices[i], vertices[i + 1], edges[i + 1]);
        }


        g.cleanE();
        g.cleanV();
        g.edges = tmp.edges;
        g.vertices = tmp.vertices;
        graphView.update();

    }

    public void onRestoredBtnClicked() {
        step = 0;
        g.cleanE();
        g.cleanV();

        for (int i = 1; i <= n; i++) {
            g.insertVertex(vertices[i]);
        }

        g.insertEdge(vertices[n], vertices[1], edges[1]);
        for (int i = 1; i <= n - 1; i++) {
            g.insertEdge(vertices[i], vertices[i + 1], edges[i + 1]);
        }

        graphView.update();
    }

    private void dealWithEdgeClicked(SmartGraphEdge<Edge, Vertex> edge) {
        gBack = SerializationUtils.clone(g);

        if (step == 0) { // 删除边
            step++;
            System.out.println("deal with edge");
            g.removeEdge(edge.getUnderlyingEdge());
            graphView.update();
        } else { // 合并边
            step++;

            com.brunomnsilva.smartgraph.graph.Edge<Edge, Vertex> currentEdge = edge.getUnderlyingEdge(); //被删除的那条边

            // 计算结果
            int result;
            if (currentEdge.element().operation.equals("+")) {
                result = currentEdge.vertices()[0].element().value + currentEdge.vertices()[1].element().value;
            } else {
                result = currentEdge.vertices()[0].element().value * currentEdge.vertices()[1].element().value;
            }

            //合并,前面边不空默认合并到前面，前面空就合并到后面。
            // 假如后面的点还有边，记录
            boolean rearNotNull = false;
            String op = "";
            if (getFrontRearEdge(g, currentEdge.vertices()[1])[1] != null) {
                rearNotNull = true;
                op = ((com.brunomnsilva.smartgraph.graph.Edge<Edge, Vertex>)
                        getFrontRearEdge(g, currentEdge.vertices()[1])[1]).element().operation;
            }
            //补边
            if (rearNotNull) {
                Edge newEdge = new Edge(currentEdge.element().key, op);
                g.insertEdge(currentEdge.vertices()[0],
                        ((com.brunomnsilva.smartgraph.graph.Edge) getFrontRearEdge(g, currentEdge.vertices()[1])[1]).vertices()[1],
                        newEdge);
            }

            g.removeEdge(currentEdge);
            g.removeVertex(currentEdge.vertices()[1]);
            currentEdge.vertices()[0].element().value = result;

            graphView.update();
        }

        // 搞完了
        if (step == n) {
            int value = ((com.brunomnsilva.smartgraph.graph.Vertex<Vertex>) g.vertices().toArray()[0]).element().value;
            System.out.println("你的得分为：" + value);
            JFXAlert alert = new JFXAlert(thisStage);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setOverlayClose(false);
            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setHeading(new Label("游戏结束了"));
            layout.setBody(new Label("你的得分为：" + value));

            JFXButton confirmButton = new JFXButton("重来");
            confirmButton.setOnAction(event -> {
                alert.hideWithAnimation();
                onRestoredBtnClicked();
            });

            JFXButton closeButton = new JFXButton("取消");
            closeButton.setOnAction(event -> alert.hideWithAnimation());

            layout.setActions(confirmButton, closeButton);
            alert.setContent(layout);
            alert.show();
        }
    }

    public void buildGraph() {
        //生成随机数据
        String[] opp = {"+", "*"};//运算符数组
        Random ran = new Random();
        //生成随机运算符和顶点数值
        op = new String[n + 1];
        v = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            int whichop = ran.nextInt(2);
            op[i] = opp[whichop];
            int addOr = ran.nextInt(2);
            if (addOr == 0) v[i] = ran.nextInt(10);
            else v[i] = -ran.nextInt(10);
        }

        g = buildNewGraph();

        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        graphView = new SmartGraphPanel<>(g, strategy);

//         给点颜色看看
//        if (g.numVertices() > 0) {
//            graphView.getStylableVertex(vertices[1]).setStyle("-fx-fill: gold; -fx-stroke: brown;");
//            graphView.getStylableVertex(vertices[2]).setStyle("-fx-fill: blue; -fx-stroke: brown;");
//        }

        graphView.setEdgeDoubleClickAction(this::dealWithEdgeClicked);

        gridPane.add(graphView, 0, 0);
    }

    public void initGraph() {
        graphView.init();
        graphView.setAutomaticLayout(true);
    }

    private GraphEdgeList<Vertex, Edge> buildNewGraph() {

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

    private Object[]
    getFrontRearEdge(Graph<Vertex, Edge> g, Object o) {
        Object[] array = new Object[2];
        Object front = null;
        Object rear = null;
        com.brunomnsilva.smartgraph.graph.Vertex<Vertex> v = (com.brunomnsilva.smartgraph.graph.Vertex<Vertex>) o;

        //获取边数组
        Object[] es = g.edges().toArray();

        //找点前面的边，边的后面那个点等于这个点。找点后面的边，边的前面面那个点等于这个点。遍历每一条边查看
        for (Object e : es) {
            if (
                    ((com.brunomnsilva.smartgraph.graph.Edge<Edge, Vertex>) e).vertices()[1].element().key
                            == v.element().key
            ) {
                front = e;
            }
            if (
                    ((com.brunomnsilva.smartgraph.graph.Edge<Edge, Vertex>) e).vertices()[0].element().key
                            == v.element().key
            ) {
                rear = e;
            }
        }


        array[0] = front;
        array[1] = rear;
        return array;
    }

}

