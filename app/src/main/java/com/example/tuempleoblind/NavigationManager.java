package com.example.tuempleoblind;


import android.content.Context;
import android.content.Intent;
import android.util.Log;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import java.text.Normalizer;


public class NavigationManager extends AppCompatActivity {
    public static void navigateToDestinationC(Context context, String keyword, FragmentManager fragmentManager, Fragment fragmentActual) {
        Log.d("TAG", "Clase nombre " + context.getClass().getSimpleName());
        if (!context.getClass().getSimpleName().equals("CompanyHome")){
            Intent intent = new Intent(context, CompanyHome.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Esto limpiará la pila de actividades y creará una nueva tarea
            intent.putExtra("keyword", keyword);
            context.startActivity(intent);

        }else{
            if (ifNavigation(keyword, "perfil") ){
                Log.d("TAG", "ProfileCFragment ID: " + R.id.profileCFragment);
                replaceFragmentC(new ProfileCFragment(), fragmentManager);
            }else{
                if (ifNavigation(keyword, "normativa") && !fragmentActual.getClass().getSimpleName().equalsIgnoreCase("ConfigCFragment")){
                    Log.d("TAG", "ProfileCFragment ID: " + R.id.profileCFragment);
                    replaceFragmentC(new ConfigCFragment(), fragmentManager);
                }else{
                    if (ifNavigation(keyword, "menu principal") && !fragmentActual.getClass().getSimpleName().equalsIgnoreCase("HomeCFragment") ) {
                        Intent intent = new Intent(context, CompanyHome.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Esto limpiará la pila de actividades y creará una nueva tarea
                        context.startActivity(intent);
                    } else if (ifNavigation(keyword, "nuevo trabajo")||(ifNavigation(keyword, "nuevo empleo") )&& !context.getClass().getSimpleName().equalsIgnoreCase("NewJob")) {
                        Intent intent = new Intent(context, NewJob.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (ifNavigation(keyword, "editar datos") && !context.getClass().getSimpleName().equals("EditDataProfileC")){
                        Intent intent = new Intent(context, EditDataProfileC.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (ifNavigation(keyword, "politicas seguridad") && !context.getClass().getSimpleName().equals("PoliticalSecurityActivity")) {
                        Intent intent = new Intent(context, PoliticalSecurityActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (ifNavigation(keyword, "acerca de") && !context.getClass().getSimpleName().equals("AboutActivity")) {
                        Intent intent = new Intent(context, AboutActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }

            }
        }
    }

    public static void navigateToDestinationBlind(Context context, String keyword, FragmentManager fragmentManager, Fragment fragmentActual) {
        if (!context.getClass().getSimpleName().equals("HomePageBlind")){
            Intent intent = new Intent(context, HomePageBlind.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Esto limpiará la pila de actividades y creará una nueva tarea
            intent.putExtra("keyword", keyword);
            context.startActivity(intent);

        }else{
            if (ifNavigation(keyword, "perfil") ){
                replaceFragmentBlind(new ProfileBlindFragment(), fragmentManager);
            }else{
                if (ifNavigation(keyword, "prepararme") && !fragmentActual.getClass().getSimpleName().equalsIgnoreCase("ConfigBlindFragment")){
                    replaceFragmentBlind(new ConfigBlindFragment(), fragmentManager);
                }else{
                    if (ifNavigation(keyword, "menu principal") && !fragmentActual.getClass().getSimpleName().equalsIgnoreCase("HomeBlindFragment") ) {
                        Intent intent = new Intent(context, HomePageBlind.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Esto limpiará la pila de actividades y creará una nueva tarea
                        context.startActivity(intent);
                    } else if (ifNavigation(keyword, "editar datos") && !context.getClass().getSimpleName().equals("EditDataProfileBlind")){
                        Intent intent = new Intent(context, EditDataProfileBlind.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (ifNavigation(keyword, "politicas seguridad") && !context.getClass().getSimpleName().equals("PoliticalSecurityActivity")) {
                        Intent intent = new Intent(context, PoliticalSecurityActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (ifNavigation(keyword, "acerca de") && !context.getClass().getSimpleName().equals("AboutActivity")) {
                        Intent intent = new Intent(context, AboutActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }

            }
        }
    }

    private static void replaceFragmentC(Fragment fragment, FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.layoutConstraint, fragment);
        fragmentTransaction.commit();
    }
    private static void replaceFragmentBlind(Fragment fragment, FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.layoutConstraintBlind, fragment);
        fragmentTransaction.commit();
    }
    public static boolean ifNavigation(String user, String expected){

        // Convertir texto del usuario y palabras esperadas a minúsculas para hacer la comparación sin distinguir entre mayúsculas y minúsculas
        user = user.toLowerCase();
        expected = expected.toLowerCase();

        user = eliminarTildes(user);
        expected = eliminarTildes(expected);

        // Dividir el texto del usuario en palabras individuales
        String[] wordsUser = user.split("\\s+");
        String[] expectedWords = expected.split("\\s+");

        int coincidencias = 0;
        for (String word : wordsUser) {
            for (String expectedWord : expectedWords) {
                if (word.equals(expectedWord)) {
                    coincidencias++;
                    break;
                }
            }
        }

        return coincidencias >= expectedWords.length;

    }

    public static String eliminarTildes(String cadena) {
        // Normalizar la cadena y eliminar las tildes
        return Normalizer.normalize(cadena, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }
}
