package com.example.healthcare;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import static com.example.healthcare.R.id.barChart;


/**
 * A simple {@link Fragment} subclass.
 */
public class BarChart extends Fragment {



    public static com.github.mikephil.charting.charts.BarChart barChart;
    public BarChart() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_bar_chart, container, false);
        barChart = (com.github.mikephil.charting.charts.BarChart) rootView.findViewById(R.id.barChart);
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(2020, 30));
        entries.add(new BarEntry(2021, 300));
        entries.add(new BarEntry(2022, 301));
        entries.add(new BarEntry(2023, 3041));
        entries.add(new BarEntry(2024, 30));

        BarDataSet dataSet = new BarDataSet(entries, "Years");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(16f);

        BarData barData = new BarData(dataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("This is Bar Chart");
        barChart.animateY(2000);

        return rootView;

    }

}
