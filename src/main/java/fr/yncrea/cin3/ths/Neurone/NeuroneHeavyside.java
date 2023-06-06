package fr.yncrea.cin3.ths.Neurone;

public class NeuroneHeavyside extends Neurone
{
	// Fonction d'activation d'un neurone (peut facilement être modifiée par héritage)
	protected float activation(final float valeur) {return valeur >= 0 ? 1.f : 0.f;}
	
	// Constructeur
	public NeuroneHeavyside(final int nbEntrees) {super(nbEntrees);}
}
