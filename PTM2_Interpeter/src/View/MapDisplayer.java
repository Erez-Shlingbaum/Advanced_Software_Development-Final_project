package View;


import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static javafx.scene.paint.Color.rgb;

public class MapDisplayer extends Canvas
{
    //map details
    double[][] mapData;
    double maxMap;
    double minMap;
    double sizeMap = 0;

    //plane details
    /*TODO: change the Landmark according to the CSV file*/
    double planeX = 0;
    double planeY = 0;

    //local variables
    double red = 255, green = 0;
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
        this.mapData = mapData;
        this.maxMap = max;
        this.minMap = min;
        this.sizeMap = max - min + 1;
        redraw();
    }


    //draw function
    public void redraw()
    {
        if (mapData != null)
        {
            //variables to find the place respectively canvas
            double Height = getHeight();
            double h = Height / mapData.length;
            double Width = getWidth();
            double w = Width / mapData[0].length;

            //plane variable
            Image plane = null;
            try
            {
                plane = new Image(new FileInputStream("./PTM2_Interpeter/Resources/plane.png"));
            } catch (FileNotFoundException e)
            {e.printStackTrace();}

            GraphicsContext gc = getGraphicsContext2D();

            //pain the colorful map by the values
            for (int i = 0; i < mapData.length; i++)
            {
                for (int j = 0; j < mapData[i].length; j++)
                {
                    if (mapData[i][j] < sizeMap / 2)
                    {
                        green = ((255 / (double) (sizeMap / 2)) * mapData[i][j]);
                        gc.setFill(rgb(255, (int) green, 0));
                    }
                    else
                    {
                        red = (double) 255 - ((255 / (double) (sizeMap / 2)) * (double) (mapData[i][j] - 7));
                        gc.setFill(rgb((int) red, 255, 0));

                    }
                    gc.fillRect(j * w, i * h, w, h);
                    gc.strokeText(String.valueOf((int)mapData[i][j]), j * w, (i + 1) * h);
                }
            }

            //pain the plane

            gc.drawImage(plane,planeX * w, planeY * h, w, h);
        }
    }
}
