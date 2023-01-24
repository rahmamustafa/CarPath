package gov.iti.gui.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.chart.Axis;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class CarPathController implements Initializable {
    @FXML
    Button btnStart;

    @FXML
    BorderPane paneContainer;

    @FXML
    ImageView directoryIcon;
    @FXML
    ImageView fileIcon;


    PathTransition pathTransition;
    PathTransition pathTransition2;
    boolean isStart=true;
    AudioClip buzzer = new AudioClip(getClass().getResource("/sounds/beeb.mp3").toExternalForm());

    

    Image car = new Image(getClass().getResourceAsStream("/images/sportcar.png"));
    Image carR = new Image(getClass().getResourceAsStream("/images/sportcarR.png"));
    ImageView node1 = new ImageView(car);
    ImageView node2 = new ImageView(car);

    @FXML
    void onStart(ActionEvent event) {
        if(isStart){
            pathTransition.play();
            pathTransition2.play();
            isStart=false;
            btnStart.setText("Stop");
        }
        else{
            pathTransition.pause();
            pathTransition2.pause();
            isStart=true;
            btnStart.setText("Start");

        }
       
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        node1.setFitWidth(100);
        node1.setFitHeight(70);
        node2.setFitWidth(100);
        node2.setFitHeight(70);
        node2.setTranslateY(100);
        node1.setTranslateY(20);
        
        SVGPath backPath = Stroke(70, Color.BLACK);
        SVGPath mainPath = Stroke(5, Color.web("#b9c0c5")); 


        pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(11000));
        pathTransition.setPath(mainPath);
        pathTransition.setNode(node1);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(Timeline.INDEFINITE);

        pathTransition2 = new PathTransition();
        pathTransition2.setDuration(Duration.millis(10000));
        pathTransition2.setPath(mainPath);
        pathTransition2.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition2.setCycleCount(Timeline.INDEFINITE);
        pathTransition2.setNode(node2);

      
        paneContainer.getChildren().addAll(backPath,mainPath,node1, node2);


        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Bounds boundsInScene1 = node1.localToScene(node1.getBoundsInLocal());
                    Bounds boundsInScene2 = node2.localToScene(node2.getBoundsInLocal());
                    if (boundsInScene1.getCenterX() == boundsInScene2.getCenterX()
                           || boundsInScene1.getCenterY() == boundsInScene2.getCenterY()) {
                        System.out.println("ss");
                        Platform.runLater( () -> pathTransition.setRate(pathTransition.getRate()*-1));

                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
        th.setDaemon(true);
        th.start();

        
        onClickReverse();

    }

    void onClickReverse() {
        node1.setOnMouseClicked(mouseEvent -> {
            playSound();
            reverse(node1, pathTransition);
            
        });
        node2.setOnMouseClicked(mouseEvent -> {
            playSound();
            reverse(node2, pathTransition2);
        });
    }

    void changeDirection(ImageView node, int rotate) {
        node.setRotationAxis(Rotate.X_AXIS);
        node.setRotate(rotate);
        node.setRotationAxis(Rotate.Z_AXIS);
    }

    void playSound() {
        buzzer.play();
        
    }
    SVGPath Stroke(int width , Color clr){
        SVGPath svg = new SVGPath();
        svg.setFill(Color.TRANSPARENT);
        svg.setStroke(clr);
        svg.setRotate(180);
        svg.setTranslateY(100);

        svg.setContent(
                "M 787.49,150 C 787.49,203.36 755.56,247.27 712.27,269.5 S 622.17,290.34 582.67,279.16 508.78,246.56 480,223.91 424.93,174.93 400,150 348.85,98.79 320,76.09 256.91,32.03 217.33,20.84 130.62,8.48 87.73,30.5 12.51,96.64 12.51,150 44.44,247.27 87.73,269.5 177.83,290.34 217.33,279.16 291.22,246.56 320,223.91 375.07,174.93 400,150 451.15,98.79 480,76.09 543.09,32.03 582.67,20.84 669.38,8.48 712.27,30.5 787.49,96.64 787.49,150 z");

        double originalWidth = svg.prefWidth(-1);
        double originalHeight = svg.prefHeight(originalWidth);

        double scaleX = 600 / originalWidth;
        double scaleY = 300 / originalHeight;

        svg.setScaleX(scaleX);
        svg.setScaleY(scaleY);

        svg.setStrokeWidth(width);
        svg.setStroke(clr);
        if(width<7)
            svg.getStrokeDashArray().addAll(15d, 15d);

        return svg;
    }
    void reverse(ImageView node, PathTransition path){
        if (path.getRate() > 0)
                node.setImage(carR);
        else
                node.setImage(car);
        path.setRate(path.getRate()*-1);
    }
}
