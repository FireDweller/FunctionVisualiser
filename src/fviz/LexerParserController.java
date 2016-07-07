package fviz;

// A class facilitating the access to parser and lexer

import fviz.lexerParser.*;

import java.util.ArrayList;

public class LexerParserController {

    //stores error status
    private Boolean errorFound;
    //stores erro message if there was an error
    private String errorMessage;

    public Boolean getErrorFound() {
        return errorFound;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    //returns array list of points gotten from inserting x values from a given range into a function
    public ArrayList<Double> getPoints(double xStart, double xEnd, double xStep, String function) {

        Parser parser = new Parser();
        ArrayList<Double> points = new ArrayList<>();

        try {
            errorFound = false;
            ExpressionNode expr = parser.parse(function);

            for (double i = xStart; i <= xEnd; i = i + xStep) {
                points.add(i);
                expr.accept(new SetVariable("x", i));
                points.add(expr.getValue());
            }
            //System.out.println("The value of the expression is " + expr.getValue());
        } catch (ParserException e) {
            errorFound = true;
            this.errorMessage = e.getMessage();
            //System.out.println(e.getMessage());
        } catch (EvaluationException e) {
            errorFound = true;
            this.errorMessage = e.getMessage();
            //System.out.println(e.getMessage());
        }
        return points;
    }

    //returns a value of the function when x and the function is given
    public Double getFunctionValue(Double x, String function) {
        Parser parser = new Parser();
        Double y = 0.0;

        try {
            errorFound = false;
            ExpressionNode expr = parser.parse(function);
            expr.accept(new SetVariable("x", x));
            y = expr.getValue();

        } catch (ParserException e) {
            errorFound = true;
            this.errorMessage = e.getMessage();

        } catch (EvaluationException e) {
            errorFound = true;
            this.errorMessage = e.getMessage();

        }
        return y;
    }

    private String returnErrorMessage() {
        return this.errorMessage;
    }

}
