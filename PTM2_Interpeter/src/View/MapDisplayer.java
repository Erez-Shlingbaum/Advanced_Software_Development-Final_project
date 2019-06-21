package View;


import javafx.beans.NamedArg;
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

import static javafx.scene.paint.Color.rgb;

public class MapDisplayer extends StackPane
{

    // this is the x and y of the 0,0 place in the map, received from the csv file
    DoubleProperty xCoordinateLongitude;
    DoubleProperty yCoordinateLatitude;
    DoubleProperty cellSizeInDegrees;

    ObjectProperty<double[][]> mapData; // map details- matrix
    StringProperty pathToEndCoordinate; //the result: the path of the plane
    // these properties will update 4 times per second with the current plane position
    public DoubleProperty currentPlaneLongitudeX = new SimpleDoubleProperty();
    public DoubleProperty currentPlaneLatitudeY = new SimpleDoubleProperty();

    // isClicked property
    BooleanProperty isMousePressed = new SimpleBooleanProperty(false);
    // canvases
    private Canvas colorfulMapLayer;
    private Canvas planeLayer;
    private Canvas landmarkLayer;

    double maxMap;
    double minMap;
    double sizeMap = 0;

    //variables to find the place respectively canvas
    double Height, Width, h, w;

    //plane details
    /*TODO: change the Landmark according to the CSV file*/
    double planeX = 0;
    double planeY = 0;

    //local variables
    double red = 255, green = 0;
    IntegerProperty planeIndexX = new SimpleIntegerProperty();
    IntegerProperty planeIndexY = new SimpleIntegerProperty();
    IntegerProperty xEndIndex = new SimpleIntegerProperty();
    IntegerProperty yEndIndex = new SimpleIntegerProperty();     // indexes in MATRIX landmarks index
    public StringProperty xFileName;
    Group pathLines = new Group();


    public MapDisplayer(@NamedArg("landmarkImage") String xFileName)
    {
        //initialize the layers
        colorfulMapLayer = new Canvas(250, 250);
        planeLayer = new Canvas(250 / 10, 250 / 10);
        landmarkLayer = new Canvas(250 / 11, 250 / 7);

        colorfulMapLayer.setOnMousePressed(this::redrawTarget);

        //binding
        planeFileName = new SimpleStringProperty();
        this.xFileName = new SimpleStringProperty();

        xCoordinateLongitude = new SimpleDoubleProperty();
        yCoordinateLatitude = new SimpleDoubleProperty();
        cellSizeInDegrees = new SimpleDoubleProperty();

        mapData = new SimpleObjectProperty<>();
        pathToEndCoordinate = new SimpleStringProperty();

        // initialize
        this.xFileName.set(xFileName);

        //listen to changes

        pathToEndCoordinate.addListener((observable, oldVal, newVal) -> redrawPath(pathToEndCoordinate.get()));
        currentPlaneLatitudeY.addListener((observable, oldVal, newVal) -> {
            calcPlanePosition(); // find indexes in matrix for the plane
            redrawPlane();
        });
        mapData.addListener((observable, oldVal, newVal) -> {
            Height = colorfulMapLayer.getHeight();
            h = Height / mapData.get().length;
            Width = colorfulMapLayer.getWidth();
            w = Width / mapData.get()[0].length;
            calcMinMax(newVal);
            sizeMap = maxMap - minMap + 1;
            redrawColorfulMap();
            redrawPlane();
        });

        //super
        super.getChildren().addAll(colorfulMapLayer , planeLayer , landmarkLayer);

        //caring to the movement of the path
        //TODO: connect the  pathToEndCoordinate to the answer of the best road
        // //redrawPath(pathToEndCoordinate.toString());
        //meanwhile:
        // redrawPath("Right,Right,Down,Down,Right,Up");
        //planeLayer.layoutXProperty().addListener((A,B,C) -> Thread.dumpStack() );//System.out.println(C));
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

    private void calcPlanePosition()
    {
        planeIndexX.set((int) ((currentPlaneLongitudeX.get() - xCoordinateLongitude.get() - cellSizeInDegrees.get()) / cellSizeInDegrees.get()));
        planeIndexY.set((int) (-1 * (currentPlaneLatitudeY.get() - yCoordinateLatitude.get() - cellSizeInDegrees.get()) / cellSizeInDegrees.get()));
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
                   /* if (mapData.get()[i][j] < maxMap / 2)
                    {
                        green = ((255 / (maxMap / 2)) * mapData.get()[i][j])
                       gc.setFill(rgb(255, (int) green, 0));

                       // gc.setFill(rgb(255 - (int) green, (int) green, 0));

                    }
                    else
                    {
                        red = (double) 255 - ((255 / (2*maxMap)) * (mapData.get()[i][j]));
                        System.out.println("red = " + red);
                        gc.setFill(rgb((int) red, 255, 0));

                        //gc.setFill(rgb((int) red, 255- (int)red, 0));
                    }*/
                    int color = (int) (255 * ((mapData.get()[i][j] - minMap) / (maxMap - minMap)));
                    gc.setFill(rgb(255 - color, color, 0));
                    gc.fillRect(j * w, i * h, w, h);
                    //gc.strokeText(String.valueOf((int) mapData.get()[i][j]), j * w, (i + 1) * h);
                }
            }
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
        planeLayer.setTranslateX(-planeLayer.getLayoutX() + (planeIndexX.get() * w));
        planeLayer.setTranslateY(-planeLayer.getLayoutY() + (planeIndexY.get() * h));
        gc.drawImage(planeImage, 0, 0, planeLayer.getWidth(), planeLayer.getHeight());
    }

    private void redrawTarget(MouseEvent event)
    {
        isMousePressed.set(true);

        GraphicsContext gc = landmarkLayer.getGraphicsContext2D();
        Image landmarkImage = null;
        try
        {
            landmarkImage = new Image(new FileInputStream(new File(xFileName.get())));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        double tmpX = event.getX();
        double tmpY = event.getY();

        int col = (int) (event.getX() / w);//((event.getX() / mapLayer.getWidth()) * matrix.get().length);
        int row = (int) (event.getY() / h);//(int) ((event.getY() / mapLayer.getHeight()) * matrix.get()[0].length);

        this.xEndIndex.set(col);
        this.yEndIndex.set(row);

        landmarkLayer.setTranslateX(-landmarkLayer.getLayoutX() + event.getX()-5);
        landmarkLayer.setTranslateY(-landmarkLayer.getLayoutY() + event.getY()-30);

        gc.drawImage(landmarkImage, 0, 0, landmarkLayer.getWidth(), landmarkLayer.getHeight());

        isMousePressed.set(false);
    }

    public void redrawPath(String path)
    {
        HashMap<String, int[]> mapStep = new HashMap<>();
        //separate the String to Array
        String[] parts = path.split(",");
        mapStep.put("Up", new int[]{0, -1});
        mapStep.put("Down", new int[]{0, 1});
        mapStep.put("Left", new int[]{-1, 0});
        mapStep.put("Right", new int[]{1, 0});

        int[] currentPoint = {0, 0};//TODO: calculate the start point by the info from the CSV
        int[] prevPoint = {0, 0};
        int[] moves;

        pathLines.getChildren().clear();


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
            Line line = new Line(prevPoint[0] * w, prevPoint[1] * h, currentPoint[0] * w, currentPoint[1] * h);
            line.setStrokeWidth(5);
            pathLines.getChildren().add(line);
        }
        super.getChildren().add(pathLines);

    }

    private void calcMinMax(double[][] matrix)
    {
        this.minMap = matrix[0][0];
        this.maxMap = matrix[0][0];
        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[i].length; j++)
            {
                if (matrix[i][j] > maxMap)
                    maxMap = matrix[i][j];
                if (matrix[i][j] < minMap)
                    minMap = matrix[i][j];
            }
    }
}