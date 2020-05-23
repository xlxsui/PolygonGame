import controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        //动态加载窗口fxml界面
        //getResource是定位到当前类目录，..jar返回不了，注意大小写。/开头定位到根目录，相当于src
        URL location = getClass().getResource("/fxml/main.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root = fxmlLoader.load();


        //获取界面的Controller的实例对象
        MainController controller = fxmlLoader.getController();
        controller.setStage(stage);//让控制器获取到Stage
        controller.buildGraph();

        //set Icon
        stage.initStyle(StageStyle.DECORATED);
        stage.getIcons().add(new Image(getClass().getResource("/resources/icon.png").toExternalForm()));
        stage.setTitle("多边形游戏");
        stage.setScene(new Scene(root, 1024, 700));
        stage.setMinHeight(768);
        stage.setMaxHeight(768);
        stage.setMinWidth(1024);
        stage.setMaxWidth(1024);
        stage.show();
        controller.initGraph();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
