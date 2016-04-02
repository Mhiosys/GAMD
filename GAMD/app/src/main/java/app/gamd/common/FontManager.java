package app.gamd.common;

import android.content.Context;
import android.graphics.Typeface;
/**
 * Created by Sigcomt on 31/03/2016.
 */
public class FontManager {

    private static FontManager fontManager;
    private Typeface robotoLight, robotoThin, robotoRegular,museoSans100,museoSans300,museoSans500,museoSans700;

    public FontManager() {

    }

    public static FontManager getSharedInstance() {
        if (fontManager == null) {
            fontManager = new FontManager();
        }
        return fontManager;
    }

    public Typeface getRobotoBold(Context context) {
        if (robotoLight == null)
            robotoLight = Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Bold.ttf");

        return robotoLight;
    }

    public Typeface getRobotoLight(Context context) {
        if (robotoLight == null)
            robotoLight = Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Light.ttf");

        return robotoLight;
    }

    public Typeface getRobotoThin(Context context) {
        if (robotoThin == null)
            robotoThin = Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Thin.ttf");

        return robotoLight;
    }

    public Typeface getRobotoRegular(Context context) {
        if (robotoRegular == null)
            robotoRegular = Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Regular.ttf");
        return robotoRegular;
    }

    public Typeface getMuseoSans100(Context context){
        if(museoSans100==null)
            museoSans100=Typeface.createFromAsset(context.getAssets(),"fonts/MuseoSans_100.otf");
        return museoSans100;
    }

    public Typeface getMuseoSans300(Context context){
        if(museoSans300==null)
            museoSans300=Typeface.createFromAsset(context.getAssets(),"fonts/MuseoSans_300.otf");
        return  museoSans300;
    }

    public Typeface getMuseoSans500(Context context){
        if(museoSans500==null)
            museoSans500=Typeface.createFromAsset(context.getAssets(),"fonts/MuseoSans_500.otf");
        return museoSans500;
    }

    public Typeface getMuseoSans700(Context context){
        if(museoSans700==null)
            museoSans700=Typeface.createFromAsset(context.getAssets(),"fonts/MuseoSans_700.otf");
        return museoSans700;
    }
}

