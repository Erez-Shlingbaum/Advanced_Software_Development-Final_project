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

    // These properties will update 4 times per second with the current plane position
    public final DoubleProperty currentPlaneLongitudeX = new SimpleDoubleProperty();
    public final DoubleProperty currentPlaneLatitudeY = new SimpleDoubleProperty();

    // This is the x and y of the 0,0 place in the map, received from the csv file
    final DoubleProperty xCoordinateLongitude = new SimpleDoubleProperty();
    final DoubleProperty yCoordinateLatitude = new SimpleDoubleProperty();
    final DoubleProperty cellSizeInDegrees = new SimpleDoubleProperty();
    final ObjectProperty<double[][]> mapData = new SimpleObjectProperty<>();
    final StringProperty pathToEndCoordinate = new SimpleStringProperty();

    // isClicked property
    final BooleanProperty isMousePressed = new SimpleBooleanProperty(false);

    // Plane properties
    final IntegerProperty planeIndexX = new SimpleIntegerProperty(0);
    final IntegerProperty planeIndexY = new SimpleIntegerProperty(0);
    final IntegerProperty xEndIndex = new SimpleIntegerProperty();
    final IntegerProperty yEndIndex = new SimpleIntegerProperty();     // Indeces in MATRIX landmarks index

    // Canvases
    private final Canvas colorfulMapLayer;
    private final Canvas planeLayer;
    private final Canvas landmarkLayer;

    // Plane details
    private final double planeX = 0;
    private final double planeY = 0;
    private final StringProperty landmarkFileName = new SimpleStringProperty();
    private final Group pathLines = new Group();

    // MVVM Bindings
    private final StringProperty planeFileName = new SimpleStringProperty();

    // Images
    private Image planeImage = null;
    private double maxMap;
    private double minMap;

    // Canvas properties
    private double Height;
    private double Width;
    private double h;
    private double w;

    public MapDisplayer(@NamedArg("landmarkImage") String landmarkFileName,
                        @NamedArg("mapWidth") double mapWidth,
                        @NamedArg("mapHeight") double mapHeight)
    {
        // Initialize the layers
        colorfulMapLayer = new Canvas(mapWidth, mapHeight);
        planeLayer = new Canvas(mapWidth / 10, mapHeight / 10);
        landmarkLayer = new Canvas(mapWidth / 11, mapHeight / 7);

        colorfulMapLayer.setOnMousePressed(this::redrawTarget);

        // Initialize
        this.landmarkFileName.set(landmarkFileName);

        // Listen to changes
        pathToEndCoordinate.addListener((observable, oldVal, newVal) -> redrawPath(pathToEndCoordinate.get()));
        currentPlaneLatitudeY.addListener((observable, oldVal, newVal) -> {
            if (!oldVal.equals(newVal))
            {
                calcPlanePosition(); // Find indeces in matrix for the plane
                redrawPlane();
            }
        });

        mapData.addListener((observable, oldVal, newVal) -> {
            Height = colorfulMapLayer.getHeight();
            h = Height / mapData.get().length;
            Width = colorfulMapLayer.getWidth();
            w = Width / mapData.get()[0].length;
            calcMinMax(newVal);
            redrawMap();
            redrawPlane();
        });

        // Disable plane until we get valid input from flight gear
        planeLayer.setDisable(true);

        super.getChildren().addAll(colorfulMapLayer, planeLayer, landmarkLayer);
    }

    // Generate the relevant variabless
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
        if (currentPlaneLongitudeX.get() == 0 && currentPlaneLatitudeY.get() == 0)
            return;
        planeIndexX.set((int) ((currentPlaneLongitudeX.get() - xCoordinateLongitude.get() - cellSizeInDegrees.get()) / cellSizeInDegrees.get()));
        planeIndexY.set((int) (-1 * (currentPlaneLatitudeY.get() - yCoordinateLatitude.get() - cellSizeInDegrees.get()) / cellSizeInDegrees.get()));
    }

    private void redrawMap()
    {

        GraphicsContext gc = colorfulMapLayer.getGraphicsContext2D();

        // Paint map
        for (int i = 0; i < mapData.get().length; i++)
        {
            for (int j = 0; j < mapData.get()[i].length; j++)
            {
                int color = (int) (255 * ((mapData.get()[i][j] - minMap) / (maxMap - minMap)));
                gc.setFill(rgb(255 - color, color, 0));
                gc.fillRect(j * w, i * h, w, h);
            }
        }
    }

    private void redrawPlane()
    {
        GraphicsContext gc = planeLayer.getGraphicsContext2D();

        // Load plane image if not loaded already
        try
        {
            if (planeImage == null)
                planeImage = new Image(new FileInputStream(planeFileName.get()));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        if (planeIndexX.get() == 0 && planeIndexY.get() == 0)
            return;
        else
            planeLayer.setDisable(false);

        // Paint the plane
        planeLayer.setTranslateX(-planeLayer.getLayoutX() + (planeIndexX.get() * w));
        planeLayer.setTranslateY(-planeLayer.getLayoutY() + (planeIndexY.get() * h));
        gc.drawImage(planeImage, 0, 0, planeLayer.getWidth(), planeLayer.getHeight());
    }

    private void redrawTarget(MouseEvent event)
    {
        GraphicsContext gc = landmarkLayer.getGraphicsContext2D();
        Image landmarkImage = null;
        try
        {
            landmarkImage = new Image(new FileInputStream(new File(landmarkFileName.get())));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        int col = (int) (event.getX() / w);
        int row = (int) (event.getY() / h);

        this.xEndIndex.set(col);
        this.yEndIndex.set(row);

        landmarkLayer.setTranslateX(-landmarkLayer.getLayoutX() + event.getX() - 5);
        landmarkLayer.setTranslateY(-landmarkLayer.getLayoutY() + event.getY() - 30);

        gc.drawImage(landmarkImage, 0, 0, landmarkLayer.getWidth(), landmarkLayer.getHeight());

        // Delete old path
        if (pathLines.getChildren().size() > 2)
            pathLines.getChildren().remove(2, pathLines.getChildren().size());

        isMousePressed.set(true); // There is an event listener bounded to this value...
        isMousePressed.set(false);
    }

    private void redrawPath(String path)
    {
        HashMap<String, int[]> mapStep = new HashMap<>();
        String[] parts = path.split(",");

        mapStep.put("Up", new int[]{0, -1});
        mapStep.put("Down", new int[]{0, 1});
        mapStep.put("Left", new int[]{-1, 0});
        mapStep.put("Right", new int[]{1, 0});

        int[] currentPoint = {planeIndexX.get(), planeIndexY.get()};
        int[] prevPoint = {0, 0};
        int[] moves;


        if (!super.getChildren().contains(pathLines))
        {
            pathLines.getChildren().add(new Canvas(colorfulMapLayer.getWidth(), 0));
            pathLines.getChildren().add(new Canvas(0, colorfulMapLayer.getHeight()));
            super.getChildren().add(pathLines);
        }

        if (pathLines.getChildren().size() > 2)
            pathLines.getChildren().remove(2, pathLines.getChildren().size());


        for (String part : parts)
        {
            // Update previous point
            prevPoint[0] = currentPoint[0];
            prevPoint[1] = currentPoint[1];

            // Calculate current point
            moves = mapStep.get(part);

            currentPoint[0] += moves[0];
            currentPoint[1] += moves[1];

            // Draw line between the previous point to the current point
            Line line = new Line(prevPoint[0] * w, prevPoint[1] * h, currentPoint[0] * w, currentPoint[1] * h);
            line.setStrokeWidth(2);
            pathLines.getChildren().add(line);
        }
    }

    private void calcMinMax(double[][] matrix)
    {
        this.minMap = matrix[0][0];
        this.maxMap = matrix[0][0];
        for (double[] doubles : matrix)
            for (int j = 0; j < doubles.length; j++)
            {
                if (doubles[j] > maxMap)
                    maxMap = doubles[j];
                if (doubles[j] < minMap)
                    minMap = doubles[j];
            }
    }
}