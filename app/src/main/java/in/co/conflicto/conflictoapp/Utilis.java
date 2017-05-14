package in.co.conflicto.conflictoapp;

import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

/**
 * Created by ansal on 5/13/17.
 */

public class Utilis {

    public static void log(String mode, String tag, String msg){
        if (mode.toLowerCase().equals("debug")){
            Log.d(tag, msg);
        }else if(mode.toLowerCase().equals("error")){
            Log.e(tag, msg);
        }else if (mode.toLowerCase().equals("info")){
            Log.i(tag, msg);
        }
    }

    public static void exception(Exception ex){
        Log.e("EXC", Arrays.toString(ex.getStackTrace()));
    }

}
