package View;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MapDisplayer extends Canvas
{
    int[][] mapData;

    public void setMapData(int[][] mapData)
    {
        this.mapData = mapData;
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
                    /*TODO: make it more generic*/
                    if (mapData[i][j] == 0)
                    {
                        gc.setFill(Color.RED);
                        gc.fillRect(j * w, i * h, w, h);
                        gc.strokeText("0", j * w, (i+1) * h);
                    }
                    else if (mapData[i][j] == 1)
                    {
                        gc.setFill(Color.web("#ff3300"));
                        gc.fillRect(j * w, i * h, w, h);
                        gc.strokeText("1", j * w, (i+1) * h);
                    }
                    else if (mapData[i][j] == 2)
                    {
                        gc.setFill(Color.web("#ff8000"));
                        gc.fillRect(j * w, i * h, w, h);
                        gc.strokeText("2", j * w, (i+1) * h);
                    }
                    else if (mapData[i][j] == 3)
                    {
                        gc.setFill(Color.web("#ffbf00"));
                        gc.fillRect(j * w, i * h, w, h);
                        gc.strokeText("3", j * w, (i+1) * h);
                    }
                    else if (mapData[i][j] == 4)
                    {
                        gc.setFill(Color.web("#ffcc00"));
                        gc.fillRect(j * w, i * h, w, h);
                        gc.strokeText("4", j * w, (i+1) * h);
                    }
                    else if (mapData[i][j] == 5)
                    {
                        gc.setFill(Color.web("#e6e600"));
                        gc.fillRect(j * w, i * h, w, h);
                        gc.strokeText("5", j * w, (i+1) * h);
                    }
                    else if (mapData[i][j] == 6)
                    {
                        gc.setFill(Color.web("#bee84c"));
                        gc.fillRect(j * w, i * h, w, h);
                        gc.strokeText("6", j * w, (i+1) * h);
                    }
                    else if (mapData[i][j] == 7)
                    {
                        gc.setFill(Color.web("#caf29d"));
                        gc.fillRect(j * w, i * h, w, h);
                        gc.strokeText("7", j * w, (i+1) * h);
                    }
                    else if (mapData[i][j] == 8)
                    {
                        gc.setFill(Color.web("#99e699"));
                        gc.fillRect(j * w, i * h, w, h);
                        gc.strokeText("8", j * w, (i+1) * h);
                    }
                    else if (mapData[i][j] == 9)
                    {
                        gc.setFill(Color.web("#85e085"));
                        gc.fillRect(j * w, i * h, w, h);
                        gc.strokeText("9", j * w, (i+1) * h);
                    }
                    else if (mapData[i][j] == 10)
                    {
                        gc.setFill(Color.web("#5cd65c"));
                        gc.fillRect(j * w, i * h, w, h);
                        gc.strokeText("10", j * w, (i+1) * h);
                    }
                    else if (mapData[i][j] == 11)
                    {
                        gc.setFill(Color.web("#5cd65c"));
                        gc.fillRect(j * w, i * h, w, h);
                        gc.strokeText("11", j * w, (i+1) * h);
                    }
                    else if (mapData[i][j] == 12)
                    {
                        gc.setFill(Color.web("#33cc33"));
                        gc.fillRect(j * w, i * h, w, h);
                        gc.strokeText("12", j * w, (i+1) * h);
                    }
                    else if (mapData[i][j] == 13)
                    {
                        gc.setFill(Color.web("#33cc33"));
                        gc.fillRect(j * w, i * h, w, h);
                        gc.strokeText("13", j * w, (i+1) * h);
                    }
                    else if (mapData[i][j] == 14)
                    {
                        gc.setFill(Color.web("#00cc00"));
                        gc.fillRect(j * w, i * h, w, h);
                        gc.strokeText("14", j * w, (i+1) * h);
                    }
                    else if (mapData[i][j] == 15)
                    {
                        gc.setFill(Color.web("#00b300"));
                        gc.fillRect(j * w, i * h, w, h);
                        gc.strokeText("15", j * w, (i+1) * h);
                    }
                }
            }
        }
    }
}
