package View;


import javafx.beans.property.*;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import static javafx.scene.paint.Color.rgb;

public class MapDisplayer extends StackPane {

    // this is the x and y of the 0,0 place in the map, received from the csv file
    DoubleProperty xCoordinateLongitude;
    DoubleProperty yCoordinateLatitude;
    DoubleProperty cellSizeInDegrees;

    ObjectProperty<double[][]> mapData; // map details- matrix
    StringProperty pathToEndCoordinate; //the result: the path of the plane
    // these properties will update 4 times per second with the current plane position
    public DoubleProperty currentPlaneLongitudeX;
    public DoubleProperty currentPlaneLatitudeY;

    //double canvases variable
    private Canvas colorfulMapLayer;
    private Canvas planeLayer;
    private Canvas xLayer;

    double maxMap;
    double minMap;
    double sizeMap = 0;

    //variables to find the place respectively canvas
    double Height = colorfulMapLayer.getHeight();
    double h = Height / mapData.get().length;
    double Width = colorfulMapLayer.getWidth();
    double w = Width / mapData.get()[0].length;

    //plane details
    /*TODO: change the Landmark according to the CSV file*/
    double planeX = 0;
    double planeY = 0;

    //local variables
    double red = 255, green = 0;
    public DoubleProperty planeIndexX;
    public DoubleProperty planeIndexY;
    private StringProperty xFileName;

    public String getxFileName()
    {
        return xFileName.get();
    }

    public void setxFileName(String xFileName)
    {
        this.xFileName.set(xFileName);
    }

    public MapDisplayer()
    {
        //initialize the layers
        colorfulMapLayer = new Canvas(250, 250);
        planeLayer = new Canvas(250, 250);
        xLayer = new Canvas(250, 250);

        //caring to the movement of the path
        //TODO: connect the  pathToEndCoordinate to the answer of the best road
        // //redrawPath(pathToEndCoordinate.toString());
        //meanwhile:
        redrawPath("Right,Right,Down,Down,Right,Up");


        colorfulMapLayer.setOnMousePressed(this::redrawTarget);

        //binding
        planeFileName = new SimpleStringProperty();
        xFileName = new SimpleStringProperty();

        xCoordinateLongitude = new SimpleDoubleProperty();
        yCoordinateLatitude = new SimpleDoubleProperty();
        cellSizeInDegrees = new SimpleDoubleProperty();

        mapData = new SimpleObjectProperty<>();
        pathToEndCoordinate = new SimpleStringProperty();

        //listen to changes
        pathToEndCoordinate.addListener((observable, oldVal, newVal) -> redrawPath(pathToEndCoordinate.get()));
        currentPlaneLatitudeY.addListener((observable, oldVal, newVal) -> redrawPlane());


        currentPlaneLongitudeX = new SimpleDoubleProperty();
        currentPlaneLatitudeY = new SimpleDoubleProperty();

        //super
        super.getChildren().addAll(colorfulMapLayer, planeLayer);
    }

    private StringProperty planeFileName;

    //generate the relevant variabless
    public double getPlaneX()
    {
        return planeX;
    }

    public double getPlaneY()
    {
        return planeY;
    }

    public String getPlaneFileName()
    {
        return planeFileName.get();
    }

    public void setPlaneFileName(String planeFileName)
    {
        this.planeFileName.set(planeFileName);
    }

    public void setMapData(double[][] mapData, double max, double min)
    {
        this.mapData.set(mapData);
        this.maxMap = max;
        this.minMap = min;
        this.sizeMap = max - min + 1;
        redrawColorfulMap();
    }


    //draw functions
    //draw the background- colorful map
    public void redrawColorfulMap()
    {
        if (mapData != null)
        {

            GraphicsContext gc = colorfulMapLayer.getGraphicsContext2D();

            //pain the colorful map by the values
            for (int i = 0; i < mapData.get().length; i++)
            {
                for (int j = 0; j < mapData.get()[i].length; j++)
                {
                    if (mapData.get()[i][j] < sizeMap / 2)
                    {
                        green = ((255 / (sizeMap / 2)) * mapData.get()[i][j]);
                        gc.setFill(rgb(255, (int) green, 0));
                    }
                    else
                    {
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
        Image planeImage = null;
        try
        {
            planeImage = new Image(new FileInputStream(planeFileName.get()));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        //pain the plane
        gc.drawImage(planeImage, 0, 0, w, h);
    }

    private void redrawTarget(MouseEvent event)
    {
        GraphicsContext gc = xLayer.getGraphicsContext2D();
        Image xImage = null;
        try
            {
                xImage = new Image(new FileInputStream(new File(xFileName.get())));
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

        gc.drawImage(xImage, 0, 0, w, h);

        redrawPath(pathToEndCoordinate.get());
    }

    public void redrawPath(String path)
    {
        //String[] directions = string.split(",");
        HashMap<String, int[]> mapStep = new HashMap<>();
        //separate the String to Array
        String[] parts = path.split(",");
        mapStep.put("Up", new int[]{1, 0});
        mapStep.put("Down", new int[]{-1, 0});
        mapStep.put("Left", new int[]{0, -1});
        mapStep.put("Right", new int[]{0, 1});

        int[] currentPoint = {0, 0};//TODO: calculate the start point by the info from the CSV
        int[] prevPoint = {0, 0};
        int[] moves;

        Group root = new Group();

        for (int i = 0; i < parts.length; i++)
        {
            //updating the previous point
            prevPoint[0] = currentPoint[0];
            prevPoint[1] = currentPoint[1];

            //calculate the current point
            moves = mapStep.get(parts[i]);

            currentPoint[0] += moves[0];
            currentPoint[1] += moves[1];

            //draw line between the previous point to the current point
            Line line = new Line(prevPoint[0], prevPoint[1], currentPoint[0], currentPoint[1]);
            root.getChildren().add(line);
        }
    }
}