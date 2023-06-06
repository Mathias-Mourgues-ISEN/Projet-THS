package fr.yncrea.cin3.ths.Neurone;

public abstract class Neurone implements iNeurone
{
	// Coefficient de mise à jour des poids,
	// commun (static) à tous les neurones
	private static float eta = 0.1f;
	// Accesseur en écriture seule, permettant de modifier
	// eta pour tous les neurones pendant l'exécution
	public static void fixeCoefApprentissage(final float nouvelEta) {eta = nouvelEta;}

	// Tolérance immuable (final) et générique (car commune à tous les neurones
	// par le mot-clé static) permettant d'accepter la sortie d'un neurone comme valable
	public static final float ToleranceSortie = 1.e-2f;

	// Tableau des poids synaptiques d'un neurone
	private float[] synapses;
	// Biais associé aux poids synaptiques d'un neurone
	private float biais;

	// Valeur de sortie d'un neurone (à "Not A Number" par défaut)
	private float etatInterne = Float.NaN;

	// Fonction d'activation d'un neurone ; à modifier par héritage,
	// c'est d'ailleurs le but ici du qualificateur abstract, qui dit que cette
	// méthode n'est pas implémentée => à faire dans un ou plusieurs classes filles
	// activation est protected car elle n'a pas à être vue de l'extérieur,
	// mais doit être redéfinie dans les classes filles
	protected abstract float activation(final float valeur);

	// Constructeur d'un neurone
	public Neurone(final int nbEntrees)
	{
		synapses = new float[nbEntrees+1];
		// On initialise tous les poids de manière alétoire
		for (int i = 0; i < nbEntrees; ++i)
			synapses[i] = (float)(Math.random()*2.-1.);
		// On initialise le biais de manière aléatoire
		//biais = (float)(Math.random()*2.-1.);
	}

	// Accesseur pour la valeur de sortie
	public float sortie() {return etatInterne;}

	// Donne accès en lecture-écriture aux valeurs des poids synaptiques
	public float[] synapses() {return synapses;}
	// Donne accès en lecture à la valeur du biais
	public float biais() {return biais;}
	// Donne accès en écriture à la valeur du biais
	//public void fixeBiais(final float nouveauBiais) {biais = nouveauBiais;}

	// Calcule la valeur de sortie en fonction des entrées, des poids synaptiques,
	// du biais et de la fonction d'activation
	public void metAJour(final float[] entrees)
	{
		// On démarre en extrayant le biais
		float somme = synapses[synapses.length-1];

		// Puis on ajoute les produits entrée-poids synaptique
		for (int i = 0; i < synapses().length-1; ++i)
			somme += entrees[i]*synapses()[i];

		// On fixe la sortie du neurone relativement à la fonction d'activation
		etatInterne = activation(somme);
	}

	// Fonction d'apprentissage permettant de mettre à jour les valeurs des
	// poids synaptiques ainsi que du biais en fonction de données supervisées
	public int apprentissage(final float[][] entrees, final float[] resultats) {
		int compteurEchecs = 0;


		boolean Drapeau = false;

		while(Drapeau != true){

			Drapeau = true;

			for(int i = 0; i<entrees.length; i++){

				metAJour(entrees[i]);

				if(Math.abs(etatInterne - resultats[i])>ToleranceSortie){

					Drapeau = false;

					for(int k=0; k<synapses.length-1; k++){

						synapses[k] += eta*(resultats[i]-etatInterne)*entrees[i][k];
					}
					synapses[synapses.length-1] += eta*(resultats[i]-etatInterne)*1;
				}
			}
			compteurEchecs++;
		}
		return compteurEchecs;
	}
}
