package View;

import ViewModel.ViewModel;
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
            {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15},
            {0,0,0,1,2,4,6,8,10,12,14,12,10,8,6,4},
            {0,1,2,3,4,5,6,7,8,9,6,2,0,0,0,0},
            {15,14,13,12,11,10,9,8,7,6,5,4,3,2,1,0},
            {12,11,10,9,8,7,6,5,4,3,2,1,0,0,0,0},
            {6,7,8,9,10,11,12,13,14,15,0,1,2,3,4,5},
            {6,8,10,12,14,12,10,8,6,4,0,0,0,1,2,4},
            {6,7,8,9,6,2,0,0,0,0,0,1,2,3,4,5},
            {9,8,7,6,5,4,3,2,1,0,15,14,13,12,11,10},
            {6,5,4,3,2,1,0,0,0,0,12,11,10,9,8,7},
            {1,2,3,4,5,6,5,4,3,2,1,0,0,0,0,0},
            {6,6,5,5,4,3,2,1,0,0,0,0,0,0,0,0},
            {7,6,5,4,3,2,2,1,1,1,2,3,4,5,6,7},
            {7,6,5,4,3,2,2,1,1,1,2,3,4,5,6,7}
    };

    @FXML
    MapDisplayer mapDisplayer;


    @Override
    public void initialize(URL location, ResourceBundle resources)/*the controller happens once compared to initialize*/
    {
        mapDisplayer.setMapData(mapData);
    }

    @Override
    public void update(Observable o, Object arg)
    {

    }
}
