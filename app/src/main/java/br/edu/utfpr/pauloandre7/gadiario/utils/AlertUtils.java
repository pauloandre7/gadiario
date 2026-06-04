package br.edu.utfpr.pauloandre7.gadiario.utils;


import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import br.edu.utfpr.pauloandre7.gadiario.R;

public final class AlertUtils {

    private AlertUtils(){
        // evita que a classe seja usada instanciada;
    }

    public static void showAlert(Context context, int idMessage){

        showAlert(context, context.getString(idMessage), null);
    }

    public static void showAlert(Context context, int idMessage,
                                 DialogInterface.OnClickListener listener){

        // Quando houver constante no resources, utiliza o méthod base para criar o alert.
        showAlert(context, context.getString(idMessage), listener);
    }


    public static void showAlert(Context context, String message,
                                 DialogInterface.OnClickListener listener){
        //  param de listener é para que, caso queira tratar clicks, então passe um
        // listener personalizado

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.common_alertDialog_alertTitle);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setMessage(message);

        /* EXEMPLO DE LISTENER SENDO DEFINIDO NO MOMENTO DO setButton.
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Ação para o botão
            }
        })
         */

        // cria o botão com uma mensagem e o listener para o que fazer.
        builder.setNeutralButton(R.string.common_ok, listener);

        // o Builder recebe todos os parâmetros acima e cria a instância do AlertDialog com .create();
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void confirmAction(Context context, int idMessage,
                                     DialogInterface.OnClickListener listenerYes,
                                     DialogInterface.OnClickListener listenerNo){

        confirmAction(context, context.getString(idMessage), listenerYes, listenerNo);
    }

    public static void confirmAction(Context context, String message,
                                     DialogInterface.OnClickListener listenerYes,
                                     DialogInterface.OnClickListener listenerNo){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        dialogBuilder.setTitle(R.string.common_confirmation);
        dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);

        dialogBuilder.setMessage(message);

        dialogBuilder.setPositiveButton(R.string.common_yes, listenerYes);
        dialogBuilder.setNegativeButton(R.string.common_no, listenerNo);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

    }
}
