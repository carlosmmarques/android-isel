package pt.isel.pdm.li51n.g4.tmdbisel.helpers;

import android.content.Context;
import android.widget.Toast;

public class Toaster {

    public static void makeTextShort(Context ctx, String message){
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    public static void makeTextLong(Context ctx, String message){
        Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
    }
}
