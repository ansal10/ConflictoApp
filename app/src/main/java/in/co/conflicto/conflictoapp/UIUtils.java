package in.co.conflicto.conflictoapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by ansal on 5/14/17.
 */

public class UIUtils {

    public static void hideKeyBoardFromScreen(AppCompatActivity context){
        if(context.getCurrentFocus()!=null){
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static ProgressDialog getProgressDialogBar(AppCompatActivity context, int style, String message){
        ProgressDialog dialog = new ProgressDialog(context); // this = YourActivity
        dialog.setProgressStyle(style);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        return dialog ;

    }

    public static void startActivity(AppCompatActivity context, Class destActivity){
        Intent intent = new Intent(context, destActivity);
        context.startActivity(intent);

    }
}
