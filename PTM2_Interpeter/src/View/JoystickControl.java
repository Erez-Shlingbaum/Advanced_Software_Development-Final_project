package View;

import javafx.beans.NamedArg;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;

// IDEA 2 - make some thread check every X time the state of joystick and send command to Simulator

public class JoystickControl extends BorderPane
{
	// graphical elements of joystick
	Circle outerCircle; // background
	Circle innerCircle; // joystick

	DoubleProperty xAxisJoystick; // when moving the joystick left-right
	DoubleProperty yAxisJoystick; // when moving the joystick up-down

	Slider downSlider;
	Slider leftSlider;

	Label downLabel, upLabel, leftLabel, rightLabel;

	// variables used for mouse dragging events - moving the joystick around
	double orgSceneX, orgSceneY, orgTranslateX, orgTranslateY;

	// getters and setters allows to set variables in the FXML (IMPORTANT)
	// there is another way to do that - with @NamedArg("name") in C'tor

	// getters for Joystick properties
	public double getxAxisJoystick() { return xAxisJoystick.get(); }

	public double getyAxisJoystick() { return yAxisJoystick.get(); }

	public double getDownSliderValue() { return downSlider.getValue(); }

	public double getLeftSliderValue() { return leftSlider.getValue(); }

	// getters and setters for FXML usage
	public String getLeftText() {return leftLabel.getText(); }

	public void setLeftText(String text) {leftLabel.setText(text);}

	public String getUpText() {return upLabel.getText(); }

	public void setUpText(String text) {upLabel.setText(text);}

	public String getRightText() {return rightLabel.getText(); }

	public void setRightText(String text) {rightLabel.setText(text);}

	public String getDownText() {return downLabel.getText(); }

	public void setDownText(String text) {downLabel.setText(text);}

	public double getLablesFontSize() {return leftLabel.getFont().getSize(); }

	public void setLablesFontSize(double fontSize)
	{
		Font font = new Font(leftLabel.getFont().getName(), fontSize); // set a new font size, WITHOUT changing fontName (family)
		leftLabel.setFont(font);
		upLabel.setFont(font);
		rightLabel.setFont(font);
		downLabel.setFont(font);
	}

	public String getLablesFontName() {return leftLabel.getFont().getName(); }

	public void setLablesFontName(String fontName)
	{
		Font font = new Font(fontName, leftLabel.getFont().getSize()); // set a new font name, WITHOUT changing fontSize
		leftLabel.setFont(font);
		upLabel.setFont(font);
		rightLabel.setFont(font);
		downLabel.setFont(font);
	}

	// @NamedArg allows initializing the values from the FXML
	public JoystickControl(@NamedArg("outerCircleRadius") double outerCircleRadius,
						   @NamedArg("outerCirclePaint") Paint outerCirclePaint,
						   @NamedArg("innerCirclePaint") Paint innerCirclePaint,
						   @NamedArg("leftSliderMin") double leftSliderMin, @NamedArg("leftSliderMax") double leftSliderMax,
						   @NamedArg("downSliderMin") double downSliderMin, @NamedArg("downSliderMax") double downSliderMax)
	{
		// initializing elements
		outerCircle = new Circle(outerCircleRadius, outerCirclePaint);
		innerCircle = new Circle(outerCircleRadius / 3, innerCirclePaint);

		xAxisJoystick = new SimpleDoubleProperty(0);
		yAxisJoystick = new SimpleDoubleProperty(0);

		downSlider = new Slider(downSliderMin, downSliderMax, 0);
		leftSlider = new Slider(leftSliderMin, leftSliderMax, 0);

		downLabel = new Label();
		leftLabel = new Label();
		upLabel = new Label();
		rightLabel = new Label();

		// setting up sliders ticks
		downSlider.setBlockIncrement(1 / (2 * (downSliderMax - downSliderMin)));
		downSlider.setMajorTickUnit((downSliderMax - downSliderMin) / 5);
		downSlider.setMinorTickCount(2); // how many ticks to show between each major tick

		leftSlider.setBlockIncrement(1 / (2 * (leftSliderMax - leftSliderMin)));
		leftSlider.setMajorTickUnit((leftSliderMax - leftSliderMin) / 5);
		leftSlider.setMinorTickCount(2); // how many ticks to show between each major tick


		// set sliders to show tick marks
		downSlider.setShowTickLabels(true);
		downSlider.setShowTickMarks(true);
		leftSlider.setShowTickLabels(true);
		leftSlider.setShowTickMarks(true);


		// setting up VBox(down label and slider) and HBox(left label and slider)
		StackPane centerBox = new StackPane(outerCircle, innerCircle); // stack pane just put the Items on top of each other
		VBox downBox = new VBox(downLabel, downSlider);
		VBox topBox = new VBox(upLabel); // this is inside a VBox because with out it, the position of the label is not good
		HBox leftBox = new HBox(leftLabel, leftSlider);
		VBox rightBox = new VBox(rightLabel); // this is inside a VBox because with out it, the position of the label is not good

		// setting alignment
		downLabel.setAlignment(Pos.CENTER);
		leftLabel.setAlignment(Pos.CENTER);
		upLabel.setAlignment(Pos.CENTER);
		rightLabel.setAlignment(Pos.CENTER);

		centerBox.setAlignment(Pos.CENTER);
		downBox.setAlignment(Pos.CENTER);
		topBox.setAlignment(Pos.CENTER);
		leftBox.setAlignment(Pos.CENTER);
		rightBox.setAlignment(Pos.CENTER);

		// setting items in their relative position
		super.setCenter(centerBox);
		super.setBottom(downBox);
		super.setLeft(leftBox);
		super.setTop(topBox);
		super.setRight(rightBox);

		// set positioning of items
		leftLabel.setRotate(-90);
		rightLabel.setRotate(90);
		leftSlider.setOrientation(Orientation.VERTICAL);

		// stroke affects the borders of the circles
		outerCircle.setStrokeType(StrokeType.INSIDE);
		innerCircle.setStrokeType(StrokeType.INSIDE);

		outerCircle.setStroke(Color.BLACK);
		innerCircle.setStroke(Color.BLACK);

		// set event handlers for dragging the joystick around
		innerCircle.setOnMousePressed(this::onMousePressed);
		innerCircle.setOnMouseDragged(this::onMouseDrag);
		innerCircle.setOnMouseReleased(this::onMouseReleased);

	/*	new Thread(() -> {
			while (true)
			{
				System.out.println("(" + xAxisJoystick.get() + "," + yAxisJoystick.get() + ")");
				try
				{
					Thread.sleep(250);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}).start();*/
	}

	private void onMousePressed(MouseEvent event)
	{
		((Circle) (event.getSource())).requestFocus();

		orgSceneX = event.getSceneX();
		orgSceneY = event.getSceneY();
		orgTranslateX = ((Circle) (event.getSource())).getTranslateX();
		orgTranslateY = ((Circle) (event.getSource())).getTranslateY();

		event.setDragDetect(true); // makes the GUI respond to dragging event
	}


	private void onMouseDrag(MouseEvent event)
	{
		double offsetX = event.getSceneX() - orgSceneX;
		double offsetY = event.getSceneY() - orgSceneY;
		double newTranslateX = orgTranslateX + offsetX;
		double newTranslateY = orgTranslateY + offsetY;

		Circle sourceCircle = ((Circle) (event.getSource()));

		// check if mouse position is inside the outer circle
		if (iseInsideCircle(newTranslateX, newTranslateY, outerCircle.getTranslateX(), outerCircle.getTranslateY(), outerCircle.getRadius()))
		{
			sourceCircle.setTranslateX(newTranslateX);
			sourceCircle.setTranslateY(newTranslateY);
		} else // mouse is outside the outerCircle, but we still want the joystick to work - so we will move it in the direction of the mouse
		{
			// normalize the vector (from the center of outerCircle to center of innerCircle) (why? because we will get a new vector with distance 1 and the SAME direction)
			Point2D newPosition = normaliseVector(newTranslateX, newTranslateY, outerCircle.getTranslateX(), outerCircle.getTranslateY());

			// setting the new position to be on the circle's radius
			sourceCircle.setTranslateX(newPosition.getX() * outerCircle.getRadius());
			sourceCircle.setTranslateY(newPosition.getY() * outerCircle.getRadius());
		}
		event.setDragDetect(false);

		// update the xy-axis properties
		xAxisJoystick.set((sourceCircle.getTranslateX() - outerCircle.getTranslateX()) / outerCircle.getRadius()); // distance between x values of each circle / radius (gives a number between -1 to 1)

		// TODO: remember that yAxis is upside down - decide if fix y value with (*-1)
		yAxisJoystick.set((sourceCircle.getTranslateY() - outerCircle.getTranslateY()) / outerCircle.getRadius()); // distance between y values of each circle / radius (gives a number between -1 to 1)
	}

	// put joystick back to original position
	private void onMouseReleased(MouseEvent event)
	{
		Circle circle = ((Circle) (event.getSource()));
		circle.setTranslateX(circle.getCenterX());
		circle.setTranslateY(circle.getCenterY());

		xAxisJoystick.set(0);
		yAxisJoystick.set(0);
	}

	// calculates distance between two points
	private static boolean iseInsideCircle(double x1, double y1, double x2, double y2, double circleRadius)
	{
		double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
		return distance <= circleRadius;
	}

	// getting a 1 unit distance vector relative to (relativeX,relativeY) in the direction of (x,y)
	private static Point2D normaliseVector(double x, double y, double relativeX, double relativeY) // if relativesXY=0 then this like normal normalization
	{
		double len = Math.sqrt(Math.pow(x - relativeX, 2) + Math.pow(y - relativeY, 2)); // might not work
		if (len > 0)
			return new Point2D(x / len, y / len);
		return new Point2D(x, y);
	}

}
