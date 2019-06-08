package message_measurement;

import message_measurement.SensorMeasurement;
import org.knowm.xchart.*;
import org.knowm.xchart.style.colors.XChartSeriesColors;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;
import simulation_src_2019.Measurement;

import java.awt.*;
import java.sql.Array;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdminChart{
    public static void plot(ArrayList<SensorMeasurement> measurements, int id){
        ArrayList<Long> x = new ArrayList<Long>();
        ArrayList<Double> y = new ArrayList<Double>();

        ArrayList<Date> xData = new ArrayList<Date>();
        ArrayList<Double> yData = new ArrayList<Double>();

        DateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date date = null;
        for (int i = 1; i <measurements.size(); i++) {

            date = new Date(measurements.get(i).timestamp);
            xData.add(date);
            yData.add(measurements.get(i).value);
        }

        for (int i = 0; i<measurements.size(); i++)
        {
            y.add(measurements.get(i).value);
            x.add(measurements.get(i).timestamp);
        }

      //  XYChart chart = QuickChart.getChart("SimpleChart", "Time","Value", "Value per time", x, y);

        //new SwingWrapper(chart).displayChart();


        String title;
        if (id != -1)
            title = new String("House "+id+" Measurement");
        else
            title = new String("Residence Measurement");

        XYChart chart = new XYChartBuilder().width(800).height(600).title(title).xAxisTitle("Time").yAxisTitle("Watt").build();

        XYSeries series = chart.addSeries("Measurement", xData, yData);
        series.setLineColor(XChartSeriesColors.BLUE);
        series.setMarkerColor(Color.BLACK);
        series.setMarker(SeriesMarkers.CIRCLE);
        series.setLineStyle(SeriesLines.SOLID);

        new SwingWrapper<XYChart>(chart).displayChart();
    }
}