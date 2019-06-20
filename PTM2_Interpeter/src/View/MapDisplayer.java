package View;


import javafx.beans.property.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import static javafx.scene.paint.Color.rgb;

public class MapDisplayer extends StackPane
{

    // this is the x and y of the 0,0 place in the map, received from the csv file
    DoubleProperty xCoordinateLongitude; // TODO ask erez what this is ...
    DoubleProperty yCoordinateLatitude;
    DoubleProperty cellSizeInDegrees;
    ObjectProperty<double[][]> mapData; // map details
    StringProperty pathToEndCoordinate;

    //double canvases variable
    private Canvas colorfulMapLayer;
    private Canvas planeLayer;
    double maxMap;
    double minMap;
    double sizeMap = 0;

    //plane details
    /*TODO: change the Landmark according to the CSV file*/
    double planeX = 0;
    double planeY = 0;

    //local variables
    double red = 255, green = 0;

    public MapDisplayer()
    {

        //initialize the layers
        colorfulMapLayer = new Canvas(250, 250);
        planeLayer = new Canvas(250, 250);

        //binding
        planeFileName = new SimpleStringProperty();
        xCoordinateLongitude = new SimpleDoubleProperty();
        yCoordinateLatitude = new SimpleDoubleProperty();
        cellSizeInDegrees = new SimpleDoubleProperty();
        mapData = new SimpleObjectProperty<>();
        pathToEndCoordinate = new SimpleStringProperty();

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
            //variables to find the place respectively canvas
            double Height = colorfulMapLayer.getHeight();
            double h = Height / mapData.get().length;
            double Width = colorfulMapLayer.getWidth();
            double w = Width / mapData.get()[0].length;

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
                    } else
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
        //variables to find the place respectively canvas
        double Height = colorfulMapLayer.getHeight();
        double h = Height / mapData.get().length;
        double Width = colorfulMapLayer.getWidth();
        double w = Width / mapData.get()[0].length;


        GraphicsContext gc = planeLayer.getGraphicsContext2D();
        //picture of the plane
        Image plane = null;
        try
        {
            plane = new Image(new FileInputStream(planeFileName.get()));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();

        }
        //pain the plane
        gc.drawImage(plane, 0, 0, w, h);
    }

    public void calculatePath(String path)
    {
        HashMap<String, int[]> mapStep = new HashMap<>();
        mapStep.put("Up", new int[]{0, -1});
        mapStep.put("Down", new int[]{0, 1});
        mapStep.put("Left", new int[]{-1, 0});
        mapStep.put("Right", new int[]{1, 0});

        int[] point = {0, 0};
        int[] moves;

        for (int i = 0; i < path.length(); i++)
        {
            moves = mapStep.get(path.charAt(i));

            point[0] += moves[0];
            point[1] += moves[1];

            System.out.println("X: " + point[0] + " Y: " + point[1]);
            //start point (100, 10) and end point (10, 110)
            //Line line = new Line(100, 10,   10,   110);
        }
    }
}