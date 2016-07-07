package fviz;

import fviz.lexerParser.ExpressionNode;
import fviz.lexerParser.Parser;
import fviz.lexerParser.SetVariable;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Georgios on 1/1/2016.
 * A class for finding roots of a function by using the NewtonRaphsonMethod
 */
public class NewtonRaphsonMethod {

    private LexerParserController lexPar;
    private Boolean error;
    private String errorMessage;
    private Set<Double> NrootsFL;

    private String function;
    private int nOr; // number of roots found


    public NewtonRaphsonMethod(String function) {
        this.error = false;
        this.errorMessage = "";
        NrootsFL = new TreeSet<>();
        nOr = 0;
        this.function = function;
    }


    public double rootFinder(double x) {

        double tolerance = 1e-8, xx = 0;
        double dx = 0;
        //double i= x;//x=Math.PI/2
        int k = 0;
        while (Math.abs(xx - x) > tolerance && k < 100 && f(x) != 0) {
            //dx = (f(x)*d(x)) / (Math.pow(d(x) ,2) - f(x)*d(d(x)));
            dx = f(x) / d(x);
            xx = x;
            x = x - dx;
            k++;
        }
        //round it  to 3 places
        return (double) Math.round(x * 1000d) / 1000d;
    }


    //Method to return the value of function f(x)
    public double f(double x) {
        Parser p = new Parser();
        double y;

        //setting error to false and cleaning the message before using LexerParserConroller
        this.error = false;
        this.errorMessage = "";

        lexPar = new LexerParserController();
        y = lexPar.getFunctionValue(x, this.function);
        if (lexPar.getErrorFound() == true) {

            //checking if there was an error and setting a message
            this.error = true;
            this.errorMessage = lexPar.getErrorMessage();
        }
        return y;
    }

    //Method to return the derivative of f'(x)(value).
    public double d(double x) {
        double d = 0.000001;
        return (f(x + d) - f(x)) / d;
    }

    // method for finding roots
    public void findRoots() {
        NrootsFL.clear();
        this.nOr = 0;
        for (int i = -1000; i <= 1000; i++) {
            //if (i != 0) {
            double found = rootFinder(i);
            if (Math.round(f(found)) == 0) {
                this.NrootsFL.add(found);
            }
            nOr++;
            //}
        }
    }

    public int getnOr() {
        return nOr;
    }

    public Set<Double> getNrootsFL() {
        return NrootsFL;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Boolean getError() {
        return error;
    }


}