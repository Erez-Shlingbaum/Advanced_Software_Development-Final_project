package View;


import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static javafx.scene.paint.Color.rgb;

public class MapDisplayer extends StackPane {
    //double canvases variable
    private Canvas colorfulMapLayer;
    private Canvas planeLayer;
    public StringProperty pathToEndCoordinate;
    //map details
    ObjectProperty<double[][]> mapData;
    double maxMap;
    double minMap;
    double sizeMap = 0;

    //plane details
    /*TODO: change the Landmark according to the CSV file*/
    double planeX = 0;
    double planeY = 0;

    //local variables
    double red = 255, green = 0;

    public MapDisplayer() {
        //initialization the layers
        colorfulMapLayer = new Canvas(250, 250);
        planeLayer = new Canvas(250 / 10, 250 / 10);

        //binding
        planeFileName = new SimpleStringProperty();
        mapData = new SimpleObjectProperty<>();
        pathToEndCoordinate = new SimpleStringProperty();

        //super
        super.getChildren().addAll(colorfulMapLayer, planeLayer);
    }

    private StringProperty planeFileName;

    //generate the relevant variabless
    public double getPlaneX() {
        return planeX;
    }

    public double getPlaneY() {
        return planeY;
    }

    public String getPlaneFileName() {
        return planeFileName.get();
    }

    public void setPlaneFileName(String planeFileName) {
        this.planeFileName.set(planeFileName);
    }

    public void setMapData(double[][] mapData, double max, double min) {
        this.mapData.set(mapData);
        this.maxMap = max;
        this.minMap = min;
        this.sizeMap = max - min + 1;
        redrawColorfulMap();
    }


    //draw functions
    //draw the background- colorful map
    public void redrawColorfulMap() {
        if (mapData != null) {
            //variables to find the place respectively canvas
            double Height = colorfulMapLayer.getHeight();
            double h = Height / mapData.get().length;
            double Width = colorfulMapLayer.getWidth();
            double w = Width / mapData.get()[0].length;

            GraphicsContext gc = colorfulMapLayer.getGraphicsContext2D();

            //pain the colorful map by the values
            for (int i = 0; i < mapData.get().length; i++) {
                for (int j = 0; j < mapData.get()[i].length; j++) {
                    if (mapData.get()[i][j] < sizeMap / 2) {
                        green = ((255 / (sizeMap / 2)) * mapData.get()[i][j]);
                        gc.setFill(rgb(255, (int) green, 0));
                    } else {
                        red = (double) 255 - ((255 / (sizeMap / 2)) * (mapData.get()[i][j] - 7));
                        gc.setFill(rgb((int) red, 255, 0));

                    }
                    gc.fillRect(j * w, i * h, w, h);
                    gc.strokeText(String.valueOf((int) mapData.get()[i][j]), j * w, (i + 1) * h);
                }
            }
            redrawPlane();
        }
    }

    public void redrawPlane()
    {

        GraphicsContext gc = planeLayer.getGraphicsContext2D();
        //picture of the plane
        Image plane = null;
        try {
            plane = new Image(new FileInputStream(planeFileName.get()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        }
        //pain the plane
        gc.drawImage(plane,0,0, planeLayer.getWidth(), planeLayer.getHeight());
        planeLayer.setTranslateX(planeLayer.getTranslateX()-50);
    }
}
