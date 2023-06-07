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

import static java.util.Arrays.copyOfRange;

public class main {



    public static void main(String[] args) {

        //tableau de string contenant les fichiers audio
        String[] canardString = {"Canard_2.wav", "Canard_3.wav", "Chouette_Hulotte_1.wav","Chouette_Effraie.wav"};
        final Neurone canard = apprentissageNeuroneSigmoide(canardString, 2, "Canard");

        String[] chouetteString = {"Chouette_Hulotte_1.wav", "Chouette_Hulotte_2.wav", "Coq_1.wav", "BuseAQueue1.wav"};
        final Neurone chouette = apprentissageNeuroneSigmoide(chouetteString, 2, "Chouette");

        Neurone[] reseauNeuronal = {canard, chouette};

        String[] canardTest = {"Canard_1.wav"};
        String[] chouetteTest = {"Chouette_Hulotte_1.wav"};
        fiabiliteNeurone(canard, canardTest, 1);
        fiabiliteNeurone(canard, chouetteTest, 0);
        fiabiliteNeurone(chouette, canardTest, 0);
        fiabiliteNeurone(chouette, chouetteTest, 1);

    }

    /**
     * Transformation de fichiers sons en tableau comportant les modules des FFT de chaque bloc
     * @param nomFichier nom du fichier son
     * @return  tableau de float contenant les modules des FFT de chaque bloc
     */
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

    /**
     * Création d'un tableau d'entrée pour le système d'apprentissage à partir d'un tableau de noms de fichiers
     * @param fileNames liste contenant le nom des fichiers concernés
     * @return un tableau d'entrée concaténant les tableaux d'entrée de chaque fichier
     */
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

    /**
     * Concaténation des sorties des fichiers
     * @param filenames liste des noms de fichiers
     * @param nbreVrai  nombre de fichiers correspondant à un résultat vrai pour le neurone
     * @return tableau de float contenant les sorties des fichiers
     */
    public static float[] concatSorties(String[] filenames, int nbreVrai){
        String[] sortieV = copyOfRange(filenames, 0, nbreVrai);
        String[] sortieF = copyOfRange(filenames, nbreVrai, filenames.length);

        float[] sortieVout = creationSortie(sortieV, 1);
        float[] sortieFout = creationSortie(sortieF, 0);

        //concatenation des deux tableaux
        float[] sortie = new float[sortieVout.length + sortieFout.length];
        System.arraycopy(sortieVout, 0, sortie, 0, sortieVout.length);
        System.arraycopy(sortieFout, 0, sortie, sortieVout.length, sortieFout.length);

        return sortie;
    }

    /**
     * Création d'un tableau de sortie pour le système d'apprentissage à partir d'un tableau de noms de fichiers
     * @param fileNames liste contenant le nom des fichiers concernés
     * @param valeur valeur de sortie
     * @return un tableau de sortie concaténant les tableaux de sortie de chaque fichier
     */
    public static float[] creationSortie(String[] fileNames, int valeur) {
        float[] sortieV = new float[0];

        for (String fileName : fileNames) {
            float[][] entreeTemp = sonTest(fileName);
            float[] sortieTemp = new float[entreeTemp.length];
            Arrays.fill(sortieTemp, valeur);
            float[] sortieTemp2 = new float[sortieV.length + sortieTemp.length];
            System.arraycopy(sortieV, 0, sortieTemp2, 0, sortieV.length);
            System.arraycopy(sortieTemp, 0, sortieTemp2, sortieV.length, sortieTemp.length);
            sortieV = sortieTemp2;
        }
        return sortieV;
    }

    public static Neurone apprentissageNeuroneSigmoide(String[] fileNames, int nbreVrai, String nomOiseau){
        float[][] entree = creationEntree(fileNames);
        float[] sortie = concatSorties(fileNames, nbreVrai);

        final Neurone n = new NeuroneSigmoide(entree[0].length);

        System.out.println("Apprentissage…");
        System.out.println("Nombre de tours : " + n.apprentissage(entree, sortie));

        System.out.print("Synapses : ");
        for (final float f : n.synapses())
            System.out.print(f + " ");
        System.out.print("\nBiais : ");
        System.out.println(n.biais());

        // On affiche chaque cas appris
        for (final float[] entrees : entree) {
            // Pour une entrée donnée
            // On met à jour la sortie du neurone
            n.metAJour(entrees);
            // On affiche cette sortie
        }
        n.setNom(nomOiseau);

        return n;
    }

    public static void fiabiliteNeurone(Neurone n, String[] filename, Integer sortieAttendue) {
        System.out.println("Test du neurone…");
        float total2 = 0;
        float[][] sonTest2 = creationEntree(filename);
        for (float[] floats : sonTest2) {
            n.metAJour(floats);
            total2 += (Math.round(n.sortie()) == sortieAttendue ? 1 : 0);
        }
        System.out.println("Moyenne : " + total2 / sonTest2.length);
    }

    //chercher le nom de l'oiseau dans le reseau neuronal
    public static void chercherOiseau(Neurone[] reseauNeuronal, String[] filename){

        //tester le son avec chaque neurone et selectionner celui qui a le plus de sortie procche de 1
float[] sortie = new float[reseauNeuronal.length];
        for (int i = 0; i < reseauNeuronal.length; ++i) {
            float total = 0;
            float[][] sonTest = creationEntree(filename);
            for (float[] floats : sonTest) {
                reseauNeuronal[i].metAJour(floats);
                total += reseauNeuronal[i].sortie();
            }
            sortie[i] = total / sonTest.length;
        }
        //afficher le total de sortie de chaque neurone
        for (int i = 0; i < sortie.length; ++i) {
            System.out.println(reseauNeuronal[i].getNom() + " : " + sortie[i]);
        }
    }
}