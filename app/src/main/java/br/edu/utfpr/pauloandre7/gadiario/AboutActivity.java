package br.edu.utfpr.pauloandre7.gadiario;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setTitle(getString(R.string.about_class_text_about));

    }

    public void openAuthorSite(View view){
        openSite("https://pauloandre7.github.io/frontend-curriculum/");
    }

    private void openSite(String url){

        // Criar intent implícita (Intenção de visualização)
        Intent intentOpen = new Intent(Intent.ACTION_VIEW);

        // Colocar a url na intent com o Parser URI
        intentOpen.setData(Uri.parse(url));

        // Verifica se existe alguma acitivty no aparelho que possa receber a intent
        if(intentOpen.resolveActivity(getPackageManager()) != null){
            startActivity(intentOpen);
        }else{
            Toast.makeText(this,
                    R.string.about_class_toastError_noActivityToOpenUrl,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void sendEmailToAuthor(View view){
        sendEmail(new String[]{"paulos.2001@alunos.utfpr.edu.br"},
                        getString(R.string.about_class_text_contactByApp));
    }

    private void sendEmail(String[] addresses, String subject){

        // Intenção de SENDTO para enviar email
        Intent intentOpen = new Intent(Intent.ACTION_SENDTO);

        // Adiciona a constante mailto para indicar que é uma url de email
        intentOpen.setData(Uri.parse("mailto:"));

        // Adiciona os endereços e o assunto nos extras já mapeados pela Intent
        intentOpen.putExtra(Intent.EXTRA_EMAIL, addresses);
        intentOpen.putExtra(Intent.EXTRA_SUBJECT, subject);

        if(intentOpen.resolveActivity(getPackageManager()) != null){
            startActivity(intentOpen);
        }else{
            Toast.makeText(this,
                    R.string.about_class_toastError_noActivityToEmail,
                    Toast.LENGTH_SHORT).show();
        }

    }

    /***********************************************************************************************
     * Exemplo de tratamento de up button na app bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idMenuItem = item.getItemId();

        // a constante android.R.id.home se trata do up button na app bar
        if(idMenuItem == android.R.id.home){
            // Finaliza essa instância e retorna true para voltar para a tela anterior
            finish();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }
     **********************************************************************************************/
}