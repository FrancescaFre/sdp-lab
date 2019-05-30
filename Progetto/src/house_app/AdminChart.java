package house_app;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import simulation_src_2019.Measurement;

import java.util.ArrayList;

public class AdminChart{
    public static void plot(ArrayList<Measurement> measurements){
        ArrayList<Long> x = new ArrayList<Long>();
        ArrayList<Double> y = new ArrayList<Double>();
        for (int i = 0; i<measurements.size(); i++)
        {
           y.add(measurements.get(i).getValue());
           x.add(measurements.get(i).getTimestamp());
        }

        XYChart chart = QuickChart.getChart("SimpleChart", "Time","Value", "Value per time", x, y);

        new SwingWrapper(chart).displayChart();

    }
}