package utils;

public class ShapeInstance {

	private Boolean shape_square;
	private Boolean shape_circle;
	private Boolean shape_triangle;
	private Boolean color_yellow;
	private Boolean color_white;
	private Boolean color_red;
	private Boolean border_green;
	private Boolean border_purple;
	private Boolean border_black;
	private Boolean border_blue;
	
	private Boolean label;

	ShapeInstance(String shape, String color, String border, String label){
		
		shape_square = shape.trim().equals("square");
		shape_circle = shape.trim().equals("circle");
		shape_triangle = shape.trim().equals("triangle");
		
		color_yellow = color.trim().equals("yellow");
		color_white = color.trim().equals("white");
		color_red = color.trim().equals("red");
		
		border_green = border.trim().equals("green");
		border_purple = border.trim().equals("purple");
		border_black = border.trim().equals("black");
		border_blue = border.trim().equals("blue");
		
		this.label = label.trim().equals("y=1");
	}
	
	public String diplay() {
		String str= ((this.label) ? "select":"non_select") +" ";
		
		/*
		if(shape_square) str +=("square_1:"+(shape_square ? 1:0) + " ");
		else		str +=("square_0:"+(shape_square ? 0:1) + " ");	
		if(shape_circle) str +=("circle_1:"+(shape_circle ? 1:0) + " ");
		else		str +=("circle_0:"+(shape_circle ? 0:1) + " ");
		if(shape_triangle) str +=("triangle_1:"+(shape_triangle ? 1:0) + " ");
		else		str +=("triangle_0:"+(shape_triangle ? 0:1) + " ");
		
		if(color_yellow) str +=("yellow_1:"+(color_yellow ? 1:0) + " ");
		else		str +=("yellow_0:"+(color_yellow ? 0:1) + " ");
		if(color_white) str +=("white_1:"+(color_white ? 1:0) + " ");
		else 	str +=("white_0:"+(color_white ? 0:1) + " ");
		if(color_red) str +=("red_1:"+(color_red ? 1:0) + " ");
		else 	str +=("red_0:"+(color_red ? 0:1) + " ");
		
		if(border_green) str +=("green_1:"+(border_green ? 1:0) + " ");
		else 	str +=("green_0:"+(border_green ? 0:1) + " ");
		if(border_purple) str +=("purple_1:"+(border_purple ? 1:0) + " ");
		else 	str +=("purple_0:"+(border_purple ? 0:1) + " ");
		if(border_black) str +=("black_1:"+(border_black ? 1:0) + " ");
		else		str +=("black_0:"+(border_black ? 0:1) + " ");
		if(border_blue) str +=("blue_1:"+(border_blue ? 1:0));
		else 	str +=("blue_0:"+(border_blue ? 0:1));
		*/
		
		/**/
		str +=("square_1:"+(shape_square ? 1:0) + " ");
		str +=("square_0:"+(shape_square ? 0:1) + " ");
		str +=("circle_1:"+(shape_circle ? 1:0) + " ");
		str +=("circle_0:"+(shape_circle ? 0:1) + " ");
		str +=("triangle_1:"+(shape_triangle ? 1:0) + " ");
		str +=("triangle_0:"+(shape_triangle ? 0:1) + " ");
		
		str +=("yellow_1:"+(color_yellow ? 1:0) + " ");
		str +=("yellow_0:"+(color_yellow ? 0:1) + " ");
		str +=("white_1:"+(color_white ? 1:0) + " ");
		str +=("white_0:"+(color_white ? 0:1) + " ");
		str +=("red_1:"+(color_red ? 1:0) + " ");
		str +=("red_0:"+(color_red ? 0:1) + " ");
		
		str +=("green_1:"+(border_green ? 1:0) + " ");
		str +=("green_0:"+(border_green ? 0:1) + " ");
		str +=("purple_1:"+(border_purple ? 1:0) + " ");
		str +=("purple_0:"+(border_purple ? 0:1) + " ");
		str +=("black_1:"+(border_black ? 1:0) + " ");
		str +=("black_0:"+(border_black ? 0:1) + " ");
		str +=("blue_1:"+(border_blue ? 1:0) + " ");
		str +=("blue_0:"+(border_blue ? 0:1));
		/**/
		
		return str;
	}
	

	
}

