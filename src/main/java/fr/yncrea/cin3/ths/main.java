package fr.yncrea.cin3.ths;

import fr.yncrea.cin3.ths.FFT.Complexe;
import fr.yncrea.cin3.ths.FFT.ComplexeCartesien;
import fr.yncrea.cin3.ths.Neurone.Neurone;
import fr.yncrea.cin3.ths.Neurone.NeuroneHeavyside;
import fr.yncrea.cin3.ths.Neurone.NeuroneSigmoide;
import fr.yncrea.cin3.ths.Neurone.iNeurone;
import fr.yncrea.cin3.ths.son.Son;

import fr.yncrea.cin3.ths.FFT.FFTCplx;

import java.util.Arrays;

public class main {

    public static void main(String[] args) {

        //tableau de string contenant les fichiers audio
        String[] fichiers = {"Canard_2.wav", "Chouette_Hulotte_1.wav"};
        float[][] entree = creationEntree(fichiers);
        float[] sortie = creationSortie(fichiers);

        final iNeurone n = new NeuroneSigmoide(entree[0].length);

        System.out.println("Apprentissage…");
        System.out.println("Nombre de tours : " + n.apprentissage(entree, sortie));

        final Neurone vueNeurone = (Neurone) n;
        System.out.print("Synapses : ");
        for (final float f : vueNeurone.synapses())
            System.out.print(f + " ");
        System.out.print("\nBiais : ");
        System.out.println(vueNeurone.biais());

        // On affiche chaque cas appris
        for (int i = 0; i < entree.length; ++i) {
            // Pour une entrée donnée
            final float[] entrees = entree[i];
            // On met à jour la sortie du neurone
            n.metAJour(entrees);
            // On affiche cette sortie
            System.out.println("Entree " + i + " : " + n.sortie());
        }

        System.out.println("Test de la fonction de reconnaissance juste");
        String[] fichierTest = {"Canard_3.wav"};
        float[][] sonTest = creationEntree(fichierTest);
        for (int i = 0; i < sonTest.length; ++i) {
            n.metAJour(sonTest[i]);
            System.out.println("Entree " + i + " : " + n.sortie());
        }

        System.out.println("Test de la fonction de reconnaissance fausse");
        String[] fichierTest2 = {"Coq_1.wav"};
        float[][] sonTest2 = creationEntree(fichierTest2);
        for (int i = 0; i < sonTest2.length; ++i) {
            n.metAJour(sonTest2[i]);
            System.out.println("Entree " + i + " : " + n.sortie());
        }


    }

    private static float[][] sonTest(String nomFichier) {

        int fi = 4096;

        //Extraction des données du son
        Son son = new Son("build/resources/main/sons/" + nomFichier);
        float[][] entrees = new float[son.donnees().length / fi][fi];

        // Decoupage du son en blocs
        for (int i = 0; i < son.donnees().length / fi; ++i) {
            float[] donnees = son.bloc_deTaille(i, fi);
            Complexe[] signalTest = new Complexe[fi];

            // On transforme les données en signal complexe
            for (int j = 0; j < signalTest.length; ++j) {
                signalTest[j] = new ComplexeCartesien(donnees[j], 0);
            }

            // On applique la FFT sur ce signal
            Complexe[] resultat = FFTCplx.appliqueSur(signalTest);

            // On récupère les modules des résultats
            for (int x = 0; x < resultat.length; ++x) {
                entrees[i][x] = (float) resultat[x].mod();
            }
        }
        return entrees;
    }

    public static float[][] creationEntree(String[] fileNames) {

        float max = 0.0f;


        float[][] entree = new float[0][0];

        for (String fileName : fileNames) {
            float[][] entreeTemp = sonTest(fileName);
            float[][] entreeTemp2 = new float[entree.length + entreeTemp.length][entreeTemp[0].length];
            //remplir le tableau entree avec les deux tableaux entree1 et entree2
            for (int i = 0; i < entree.length; ++i) {
                System.arraycopy(entree[i], 0, entreeTemp2[i], 0, entree[0].length);
            }
            for (int i = 0; i < entreeTemp.length; ++i) {
                System.arraycopy(entreeTemp[i], 0, entreeTemp2[i + entree.length], 0, entreeTemp[0].length);
            }
            entree = entreeTemp2;
        }
        for (float[] floats : entree) {
            for (int j = 0; j < entree[0].length; ++j) {
                if (floats[j] > max) {
                    max = floats[j];
                }
            }
        }
        for (int i = 0; i < entree.length; ++i) {
            for (int j = 0; j < entree[0].length; ++j) {
                entree[i][j] = entree[i][j] / max;
            }
        }
        return entree;
    }

    public static float[] creationSortie(String[] fileNames) {
        float[] sortie = new float[0];

        float[][] entree1 = sonTest(fileNames[0]);
        float[] sortie1 = new float[entree1.length];
        Arrays.fill(sortie1, 1);

        //remplir un tableau de sortie avec des 0 pour tous les autres fichiers en prenant la meme methode
        for (int i = 0; i < fileNames.length; ++i) {
            if (i == 0) {
                sortie = sortie1;
            } else {
                float[][] entreeTemp = sonTest(fileNames[i]);
                float[] sortieTemp = new float[entreeTemp.length];
                Arrays.fill(sortieTemp, 0);
                float[] sortieTemp2 = new float[sortie.length + sortieTemp.length];
                System.arraycopy(sortie, 0, sortieTemp2, 0, sortie.length);
                System.arraycopy(sortieTemp, 0, sortieTemp2, sortie.length, sortieTemp.length);
                sortie = sortieTemp2;
            }
        }

        return sortie;
    }

}