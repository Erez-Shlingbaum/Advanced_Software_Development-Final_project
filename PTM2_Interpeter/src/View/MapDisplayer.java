package View;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import static javafx.scene.paint.Color.rgb;

public class MapDisplayer extends Canvas
{
    int[][] mapData;
    int max;
    int min;
    int size = 0;
    double red = 255, green = 0;

    public void setMapData(int[][] mapData, int max, int min)
    {
        this.mapData = mapData;
        this.max = max;
        this.min = min;
        this.size = max - min + 1;
        redraw();
    }

    public void redraw()
    {
        if (mapData != null)
        {
            double H = getHeight();
            double h = H / mapData.length;
            double W = getWidth();
            double w = W / mapData[0].length;

            GraphicsContext gc = getGraphicsContext2D();

            for (int i = 0; i < mapData.length; i++)
            {
                for (int j = 0; j < mapData[i].length; j++)
                {
                    if (mapData[i][j] < size / 2)
                    {
                        green = ((255 / (double) (size / 2)) * mapData[i][j]);
                        gc.setFill(rgb(255, (int) green, 0));
                    }
                    else
                    {
                        red = (double) 255 - ((255 / (double) (size / 2)) * (double) (mapData[i][j] - 7));
                        gc.setFill(rgb((int) red, 255, 0));

                    }
                    gc.fillRect(j * w, i * h, w, h);
                    gc.strokeText(String.valueOf(mapData[i][j]), j * w, (i + 1) * h);
                }
            }
        }
    }
}
