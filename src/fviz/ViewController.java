package fviz;

/**
 * Created by Vilius on 16/12/2015.
 * A class controlling and producing the UI outlook
 */

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class ViewController {


    private static String functionString;
    private XYChart.Series tangentLineSeries;
    private XYChart.Series singleTangentPoint;
    private TangentLine t;

    @FXML
    private Label numberOfRoots, minRoot, maxRoot;
    @FXML
    private Label rootText;
    @FXML
    private Label pointInSeries;
    @FXML
    private Label errorMessage;
    @FXML
    private TextField functionInput;
    @FXML
    private LineChart<Double, Double> graph;
    @FXML
    private NumberAxis numberAxisX, numberAxisY;

    //round number to n places
    public static double round(double value, int places) {
        return Math.round(value * Math.pow(10d, places)) / Math.pow(10d, places);
    }


    //get the string containing the function from TextField in FXML file
    @FXML
    private void getFunction() {

        //get input from a TextField or delete text in the TextField
        functionInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                this.functionString = functionInput.getText();
                drawGraph(functionString);
            } else if (e.getCode() == KeyCode.BACK_SPACE && e.isControlDown()) {
                functionInput.setText("");
            }
        });
    }

    @FXML
    private void drawGraph(String functionString) {

        // cleaning the errorMessage label
        errorMessage.setText("");


        //if no value was entered and enter pressed
        if (functionString.isEmpty()) {
            graph.getData().clear();
        } else {

//--Root finding and graph ordinate range setting part-----------------------------------------------------------------------------------------------

            //clearing the labels if they had text
            numberOfRoots.setText("No roots were found");
            minRoot.setText("");
            maxRoot.setText("");
            rootText.setText("");

            // looking for a root
            NewtonRaphsonMethod nrm = new NewtonRaphsonMethod(functionString);
            nrm.findRoots();
            if (nrm.getError() == true) {
                errorMessage.setText(nrm.getErrorMessage());
            } else {

                numberOfRoots.setText("Number of roots found: " + nrm.getNrootsFL().size());
                //find min & max out of the set
                double min = Collections.min(nrm.getNrootsFL());
                minRoot.setText("Highest root: " + Math.round(min * 1000d) / 1000d);
                double max = Collections.max(nrm.getNrootsFL());
                maxRoot.setText("Lowest root: " + Math.round(max * 1000d) / 1000d);

                //creating a formatted string of root values
                Object[] roots = nrm.getNrootsFL().toArray();
                String allRootsInAString = "";
                for (int i = 0; i < roots.length; i++) {
                    allRootsInAString += ("  x[" + (i + 1) + "]= " + roots[i].toString() + ";");
                    if ((i % 2) == 0) {
                        allRootsInAString += "\n";
                    }
                }

                rootText.setText(allRootsInAString);

                //declaring and instantiating an object for handling the lexer and parser
                LexerParserController lexPars = new LexerParserController();

                // in Arraylist points the ordinate and abscissa will interchange. Egz.: x,y,x,y...
                ArrayList<Double> points;

                //if no variable x was entered
                if (!functionString.contains("x")) {
                    numberOfRoots.setText("");
                    minRoot.setText("");
                    maxRoot.setText("");
                    rootText.setText("");
                    points = lexPars.getPoints(-10, 10, 1, functionString);

                }
                //if functions  cos sin exp were entered
                else if (functionString.contains("cos") || functionString.contains("sin")) {
                    points = lexPars.getPoints(-10, +10, 0.01, functionString);

                } else if (functionString.contains("exp")) {
                    numberOfRoots.setText("No roots found");
                    minRoot.setText("");
                    maxRoot.setText("");
                    rootText.setText("");

                    points = lexPars.getPoints(-10, +10, 0.01, functionString);
                } else {
                    //fnd the range and step for function points drawing
                    double mMin = min - Math.abs(min) - Math.abs(max) - 10;
                    double mMax = max + Math.abs(max) + Math.abs(min) + 10;
                    double step = (Math.abs(min - Math.abs(min) - Math.abs(max) - 10) + Math.abs(max +
                            +Math.abs(max) + Math.abs(min) + 10)) / 1000;
                    points = lexPars.getPoints(mMin, mMax, step, functionString);
                }
//-----------------------------------------------------------------------------------------------------------------------------------------

                // setting the error message if lexer or suffered an error
                if (lexPars.getErrorFound() == true) {
                    errorMessage.setText(lexPars.getErrorMessage());
                    graph.getData().clear();
                } else {

                    ObservableList<XYChart.Series<Double, Double>> lineChartData = FXCollections.observableArrayList();
                    LineChart.Series<Double, Double> series = new LineChart.Series<Double, Double>();
                    series.setName("y = " + functionString.replace(" ", ""));

                    //populating the series with data
                    // in ArrayList points the ordinate and abscissa interchange. Egz.: x,y,x,y...
                    for (int i = 0; i < points.size(); i = i + 2) {
                        if (!points.get(i + 1).isNaN()) {
                            series.getData().add(new XYChart.Data(points.get(i), points.get(i + 1)));
                        }
                    }

                    lineChartData.add(series);
                    graph.setCreateSymbols(false);
                    graph.setData(lineChartData);
                    graph.createSymbolsProperty();

//---Part for the tangent line of a function drawing functionality------------------------------------------------------
                    final Axis<Double> xAxisFromCursor = graph.getXAxis();
                    final Axis<Double> yAxisFromCursor = graph.getYAxis();

                    //Define mouse event listeners
                    EventHandler<javafx.scene.input.MouseEvent> onMouseEnteredSeriesListener =
                            (javafx.scene.input.MouseEvent event) -> {
                                ((Node) (event.getSource())).setCursor(javafx.scene.Cursor.HAND);

                                pointInSeries.setText(String.format(
                                        "x = %.01f  y = %.01f",
                                        xAxisFromCursor.getValueForDisplay(event.getX()),
                                        lexPars.getFunctionValue(xAxisFromCursor.getValueForDisplay(event.getX()), functionString)
                                ));
                            };

                    EventHandler<javafx.scene.input.MouseEvent> onMouseMovedInsideSeriesListener =
                            (javafx.scene.input.MouseEvent event) -> {
                                ((Node) (event.getSource())).setCursor(javafx.scene.Cursor.HAND);

                                pointInSeries.setText(String.format(
                                        "x = %.01f  y = %.01f",
                                        xAxisFromCursor.getValueForDisplay(event.getX()),
                                        lexPars.getFunctionValue(xAxisFromCursor.getValueForDisplay(event.getX()), functionString)
                                ));
                            };

                    EventHandler<javafx.scene.input.MouseEvent> onMouseExitedSeriesListener =
                            (javafx.scene.input.MouseEvent event) -> {
                                ((Node) (event.getSource())).setCursor(javafx.scene.Cursor.DEFAULT);
                            };

                    // Instantiate a tangent line object and find the tangent line of the given function through the point
                    // selected with a mouse click on a chart
                    EventHandler<javafx.scene.input.MouseEvent> onMouseClickedSeriesListener =
                            (javafx.scene.input.MouseEvent event) -> {

                                //instantiate an object for finding a tangent line of a function
                                t = new TangentLine();

                                // get the coordinates of the cursor in the chart's x and y axises
                                Double x = xAxisFromCursor.getValueForDisplay(event.getX());
                                //Double y = yAxisFromCursor.getValueForDisplay(event.getY());

                                //Forming an observable list to store all the tics from x number axis in chart
                                ObservableList<Axis.TickMark<Number>> tickListX = numberAxisX.getTickMarks();

                                //getting the range of x Axis
                                Double xAxisRange = (((Double) tickListX.get(tickListX.size() - 1).getValue() - ((Double) tickListX.get(0).getValue())));
                                //System.out.println("xAxisRange" + xAxisRange);
                                //Forming an observable list to store all the tics from x number axis in chart
                                // System.out.println((Double) tickListX.get(0).getValue());
                                //System.out.println((Double) tickListX.get(tickListX.size() - 1).getValue());

                                ObservableList<Axis.TickMark<Number>> tickListY = numberAxisY.getTickMarks();
                                //getting the last tick and the first tick to find the range
                                Double yAxisRange = (((Double) tickListY.get(0).getValue()) - ((Double) tickListY.get(tickListY.size() - 1).getValue()));

                                //get and print the tangent line
                                t.getTangentLine(functionString, x);
                                //find points of the tangent line
                                Double tangentLineLength = xAxisRange / 5;
                                t.findPoints(x, tangentLineLength);


                                //check if there were errors while finding the function
                                if (t.isError() == true) {
                                    errorMessage.setText(t.getErrorMessage());
                                    graph.getData().clear();
                                }

                                //assign the found tangent line points
                                ArrayList<Double> tangentLinePoints = t.getPoints();

                                //remove tangent line of a function and a tangent point series from lineChartData, if there were, before drawing a new one
                                lineChartData.removeAll(tangentLineSeries);
                                lineChartData.removeAll(singleTangentPoint);

                                // creating series for a tangent line
                                tangentLineSeries = new XYChart.Series<>();
                                tangentLineSeries.setName("y = " + t.getTangentLineFunction());

                                //added
                                Double yOfTangentLine = lexPars.getFunctionValue(x, t.getTangentLineFunction());

                                // creating series for a tangent point of a function
                                singleTangentPoint = new XYChart.Series<>();
                                singleTangentPoint.setName("Tangent point (" + round(x, 4) + "; " + round(yOfTangentLine, 4) + ")");
                                singleTangentPoint.getData().add(new XYChart.Data(x, yOfTangentLine));

                                //populating the tangentLine series with data
                                // in ArrayList points the ordinate and abscissa interchange. Egz.: x,y,x,y...
                                for (int i = 0; i < tangentLinePoints.size(); i = i + 2) {
                                    if (!tangentLinePoints.get(i + 1).isNaN()) {
                                        tangentLineSeries.getData().add(new XYChart.Data(tangentLinePoints.get(i), tangentLinePoints.get(i + 1)));
                                    }
                                }

                                lineChartData.add(tangentLineSeries);
                                lineChartData.add(singleTangentPoint);
                                graph.setData(lineChartData);
                                graph.createSymbolsProperty();

                                //makes the tangent line of a function representing series transparent so it would not interfere the detection
                                //of the series representing the function with the cursor
                                tangentLineSeries.getNode().setMouseTransparent(true);
                                singleTangentPoint.getNode().setMouseTransparent(true);
                            };


                    //access LineChart series node and add mouse event listeners
                    series.getNode().setOnMouseEntered(onMouseEnteredSeriesListener);
                    series.getNode().setOnMouseExited(onMouseExitedSeriesListener);
                    series.getNode().setOnMouseClicked(onMouseClickedSeriesListener);
                    series.getNode().setOnMouseMoved(onMouseMovedInsideSeriesListener);
//----------------------------------------------------------------------------------------------------------------------
                }//end of else statement for LexerParserController's method's no error case
            }//end of else statement for NewtonRapsonMethod's method's no error case
        }// end of else statement for the case if String functionString was entered in text field
    }// end drawGraph method

}