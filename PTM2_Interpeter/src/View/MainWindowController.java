package View;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class MainWindowController implements Observer, Initializable
{
    /*TODO: change the example to the CSV file*/
    int[][] mapData =
            {
                    {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
                    {0, 0, 0, 1, 2, 4, 6, 8, 10, 12, 14, 12, 10, 8, 6, 4},
                    {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 6, 2, 0, 0, 0, 0},
                    {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0},
                    {12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 0, 0, 0},
                    {6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 4, 5},
                    {6, 8, 10, 12, 14, 12, 10, 8, 6, 4, 0, 0, 0, 1, 2, 4},
                    {6, 7, 8, 9, 6, 2, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5},
                    {9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 15, 14, 13, 12, 11, 10},
                    {6, 5, 4, 3, 2, 1, 0, 0, 0, 0, 12, 11, 10, 9, 8, 7},
                    {1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1, 0, 0, 0, 0, 0},
                    {6, 6, 5, 5, 4, 3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                    {7, 6, 5, 4, 3, 2, 2, 1, 1, 1, 2, 3, 4, 5, 6, 7},
                    {7, 6, 5, 4, 3, 2, 2, 1, 1, 1, 2, 3, 4, 5, 6, 7}
            };
    int max = findMax();
    int min = findMin();
    @FXML
    MapDisplayer mapDisplayer;


    //initials min
    int findMax()
    {
        int tMax = mapData[0][0];
        for (int i = 0; i < this.mapData.length; i++)
        {
            for (int j = 0; j < this.mapData[i].length; j++)
            {
                if (tMax < this.mapData[i][j])
                    tMax = this.mapData[i][j];
            }
        }
        return tMax;
    }

    //initials max
    int findMin()
    {
        int tMin = mapData[0][0];
        for (int i = 0; i < this.mapData.length; i++)
        {
            for (int j = 0; j < this.mapData[i].length; j++)
            {
                if (this.mapData[i][j] < tMin)
                    tMin = this.mapData[i][j];
            }
        }
        return tMin;
    }

    @Override
    public void initialize(URL location, ResourceBundle
            resources)/*the controller happens once compared to initialize*/
    {
        mapDisplayer.setMapData(mapData, max, min);
    }

    @Override
    public void update(Observable o, Object arg)
    {

    }

    public void onConnectToSimulator(ActionEvent actionEvent)
    {
        //TODO
        // show popup dialog and get (ip,port) from that dialog
        // send viewModel a command to connect to client
    }

    public void onOpenData(ActionEvent actionEvent)
    {
        //TODO
        // show dialog and get csv file path
        // ask viewModel openCSV
        // use mapDisplayer to display the data
        // open a Thread tha 4 times per second will get from the viewmodel the current plane coordinates and update the mapDisplayer.planeLocation
    }

    public void onCalculatePath(ActionEvent actionEvent)
    {
        //TODO
        // show popup dialog and get (ip,port) of the shortestPathServer we did in PTM1
        // ask viewmodel to connect to shortestPathServer with the mapDisplayer.matrix
        // update mapDisplayer with the new path and target location
    }
}
