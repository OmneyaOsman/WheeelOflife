package com.omni.wheeeloflife;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.omni.wheeeloflife.ui.CategoryActivity;
import com.omni.wheeeloflife.utils.AppConfig;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setOnClickPendingIntent((R.id.family_btn), onClickButton(AppConfig.FAMILY_CAT , context ,1));
        views.setOnClickPendingIntent((R.id.fun_btn), onClickButton(AppConfig.FUN_CAT , context,2));
        views.setOnClickPendingIntent((R.id.finance_btn), onClickButton(AppConfig.FINANCIAL_CAT , context,3));
        views.setOnClickPendingIntent((R.id.education_btn), onClickButton(AppConfig.EDUCATION_CAT , context,4));
        views.setOnClickPendingIntent((R.id.love_btn), onClickButton(AppConfig.LOVE_CAT , context,5));
        views.setOnClickPendingIntent((R.id.travel_btn), onClickButton(AppConfig.TRAVEL_CAT , context,6));
        views.setOnClickPendingIntent((R.id.religion_btn), onClickButton(AppConfig.RELIGION_CAT , context,7));
        views.setOnClickPendingIntent((R.id.health_btn), onClickButton(AppConfig.HEALTH_CAT , context,8));
        views.setOnClickPendingIntent((R.id.social_life_btn), onClickButton(AppConfig.SOCIAL_LIFE_CAT , context,9));
        views.setOnClickPendingIntent((R.id.career_btn), onClickButton(AppConfig.CAREER_CAT , context,10));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId ,views);


    }

    private static  PendingIntent onClickButton(String category, Context context , int requestCode){
        Intent configIntent = new Intent(context, CategoryActivity.class);
        configIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        configIntent.putExtra("category" , category);
//        configIntent.putExtra("color" , AppConfig.toolbarColor(category));

        return  PendingIntent.getActivity(context, requestCode, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

