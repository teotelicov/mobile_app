package cs.ubbcluj.ro.deliveryservice.chart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

/**
 * Created by Teo on 06.12.2017.
 */

public class PieChart {

    private float procent1;
    private float procent2;
    private float procent3;
    private float procent4;

    public PieChart(float procent1, float procent2, float procent3, float procent4) {
        this.procent1 = procent1;
        this.procent2 = procent2;
        this.procent3 = procent3;
        this.procent4 = procent4;
    }

    public Intent execute(Context context) {

        int[] colors = new int[] { Color.RED, Color.YELLOW, Color.BLUE ,Color.GREEN};
        DefaultRenderer renderer = buildCategoryRenderer(colors);

        CategorySeries categorySeries = new CategorySeries("Prices Chart");
        categorySeries.add("5-10 LEI", procent1);
        categorySeries.add("10-20 LEI", procent2);
        categorySeries.add("20-30 LEI", procent3);
        categorySeries.add("30-40 LEI", procent4);

        return ChartFactory.getPieChartIntent(context, categorySeries, renderer, null);
    }

    protected DefaultRenderer buildCategoryRenderer(int[] colors) {
        DefaultRenderer renderer = new DefaultRenderer();
        for (int color : colors) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(color);
            renderer.addSeriesRenderer(r);
        }
        renderer.setLegendTextSize(50);
        renderer.setLabelsTextSize(40);
        return renderer;
    }
}