package edu.cmpe277.smarthealth.ui.Record;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.List;

import edu.cmpe277.smarthealth.R;

public class GraphMarkView extends MarkerView {
    private final TextView content;
    private final List<String> dateList;
    private String graph;

    public GraphMarkView(Context context, int layoutResource, List<String> dateList, String graph) {
        super(context, layoutResource);
        this.dateList = dateList;
        this.graph = graph;

        content = findViewById(R.id.graphContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        super.refreshContent(e, highlight);

        int x = (int) e.getX();
        int y = (int) e.getY();

        switch (graph){
            case "Step":
                String stepDate = dateList.get(x);
                if(y > 0){
                    content.setVisibility(VISIBLE);
                    content.setText(stepDate + ":\n" + y + " steps");
                }
                else{
                    content.setVisibility(GONE);
                }
                break;
            case "Sleep":
                String sleepDate = dateList.get(x);
                if(y > 0) {
                    content.setVisibility(VISIBLE);
                    content.setText(sleepDate + ":\n" + y + " hours");
                }
                else
                    content.setVisibility(GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
