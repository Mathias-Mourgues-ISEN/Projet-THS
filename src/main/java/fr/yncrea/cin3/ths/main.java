package fr.yncrea.cin3.ths;

import fr.yncrea.cin3.ths.FFT.Complexe;
import fr.yncrea.cin3.ths.FFT.ComplexeCartesien;
import fr.yncrea.cin3.ths.Neurone.Neurone;
import fr.yncrea.cin3.ths.Neurone.NeuroneHeavyside;
import fr.yncrea.cin3.ths.Neurone.NeuroneSigmoide;
import fr.yncrea.cin3.ths.Neurone.iNeurone;
import fr.yncrea.cin3.ths.son.Son;

import fr.yncrea.cin3.ths.FFT.FFTCplx;

public class main {

    public static void main(String[] args) {

        //tableau de string contenant les fichiers audio
        String[] fichiers = {"Canard_1.wav", "Chouette_Hulotte_1.wav"};
        processMultipleFiles(fichiers);

        /*
        float[][] entree1 = sonTest("Canard_1.wav");
        float[][] entree2 = sonTest("Chouette_Hulotte_1.wav");
        float[][] entree = new float[entree1.length + entree2.length][entree1[0].length];
        //remplir le tableau entree avec les deux tableaux entree1 et entree2
        for (int i = 0; i < entree1.length; ++i) {
            for (int j = 0; j < entree1[0].length; ++j) {
                entree[i][j] = entree1[i][j];
            }
        }
        for (int i = 0; i < entree2.length; ++i) {
            for (int j = 0; j < entree2[0].length; ++j) {
                entree[i + entree1.length][j] = entree2[i][j];
            }
        }

        float[] sortie = new float[entree1.length + entree2.length];
        for (int i = 0; i < sortie.length; ++i) {
            if (i < entree1.length) {
                sortie[i] = 0;
            } else {
                sortie[i] = 1;
            }
        }

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
*/

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

    public static float[][] processMultipleFiles(String[] fileNames) {
        // Initialiser le maximum comme étant la valeur minimale possible pour un float
        float max = Float.MIN_VALUE;


        // Initialiser un tableau de taille 0 qui sera étendu plus tard
        float[][] entree = new float[0][0];


        for (String fileName : fileNames) {
            float[][] newEntree = sonTest(fileName); // Remplacer "sonTest" par la fonction que vous utilisez pour lire le fichier


            int totalLength = entree.length + newEntree.length;
            int elementLength = newEntree[0].length;
            float[][] concatenatedEntree = new float[totalLength][elementLength];


            // Copier les valeurs existantes
            for (int i = 0; i < entree.length; i++) {
                for (int j = 0; j < entree[0].length; j++) {
                    concatenatedEntree[i][j] = entree[i][j];
                }
            }


            // Copier les nouvelles valeurs et trouver le maximum
            for (int i = 0; i < newEntree.length; i++) {
                for (int j = 0; j < newEntree[0].length; j++) {
                    concatenatedEntree[i + entree.length][j] = newEntree[i][j];
                    if (newEntree[i][j] > max) {
                        max = newEntree[i][j];
                    }
                }
            }

            entree = concatenatedEntree;

            //normaliser entrée entre 0 et 1
            for (int i = 0; i < entree.length; ++i) {
                for (int j = 0; j < entree[0].length; ++j) {
                    entree[i][j] = entree[i][j] / max;
                    //affciher les valeurs normalisées
                    System.out.println(entree[i][j]);
                }
            }
        }
        return entree;
    }
}
