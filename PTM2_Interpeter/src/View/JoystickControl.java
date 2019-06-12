package View;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

public class JoystickControl extends Region
{
	Circle outerCircle;
	Circle innerCircle;

	// we only need this to be able to set the circles layoutXY in the FXML
	DoubleProperty posX;
	DoubleProperty posY;

	// variables used for mouse dragging events - moving the joystick around
	double orgSceneX, orgSceneY, orgTranslateX, orgTranslateY;

	// getters and setters for posXY properties so that we can set them in the FXML (IMPORTANT)
	public double getPosX() { return posX.get(); }

	public void setPosX(double posX)
	{
		this.posX.set(posX);
		outerCircle.setLayoutX(posX);
		outerCircle.setLayoutY(posX);
	}

	public double getPosY() { return posY.get(); }

	public void setPosY(double posY)
	{
		this.posY.set(posY);
		innerCircle.setLayoutX(posY);
		innerCircle.setLayoutY(posY);
	}

	public JoystickControl() // TODO: delete after done testing
	{
		this(80, Color.GRAY, Color.BLACK);
	}

	public JoystickControl(int outerCircleRadius, Paint outerCirclePaint, Paint innerCirclePaint)
	{
		posX = new SimpleDoubleProperty();
		posY = new SimpleDoubleProperty();

		outerCircle = new Circle(outerCircleRadius, outerCirclePaint);
		innerCircle = new Circle(outerCircleRadius / 4, innerCirclePaint);

		// stroke affects the borders of the circles
		outerCircle.setStrokeType(StrokeType.INSIDE);
		innerCircle.setStrokeType(StrokeType.INSIDE);

		outerCircle.setStroke(Color.BLACK);
		innerCircle.setStroke(Color.BLACK);

		// set event handlers for dragging the joystick around
		innerCircle.setOnMousePressed(this::onMousePressed);
		innerCircle.setOnMouseDragged(this::onMouseDrag);
		innerCircle.setOnMouseReleased(this::onMouseReleased);

		// add circles as children to the pane we inherited
		super.getChildren().addAll(outerCircle, innerCircle);
	}

	private void onMousePressed(MouseEvent event)
	{
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
		}
		else // mouse is outside the outerCircle, but we still want the joystick to work - so we will move it in the direction of the mouse
		{
			// normalize the vector (from the center of outerCircle to center of innerCircle) (why? because we will get a new vector with distance 1 and the SAME direction)
			Point2D newPosition = normaliseVector(newTranslateX, newTranslateY, outerCircle.getTranslateX(), outerCircle.getTranslateY());

			// setting the new position to be on the circle's radius
			sourceCircle.setTranslateX(newPosition.getX() * outerCircle.getRadius());
			sourceCircle.setTranslateY(newPosition.getY() * outerCircle.getRadius());
		}
		event.setDragDetect(false);

	}

	// put joystick back to original position
	private void onMouseReleased(MouseEvent event)
	{
		Circle circle = ((Circle) (event.getSource()));
		circle.setTranslateX(circle.getCenterX());
		circle.setTranslateY(circle.getCenterY());
	}

	// calculates distance between two points
	private static boolean iseInsideCircle(double x1, double y1, double x2, double y2, double circleRadius)
	{
		double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
		return distance < circleRadius;
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
