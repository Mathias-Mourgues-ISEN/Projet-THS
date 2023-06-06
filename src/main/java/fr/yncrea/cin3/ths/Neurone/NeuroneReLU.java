package fr.yncrea.cin3.ths.Neurone;

public class NeuroneReLU extends Neurone {
    public NeuroneReLU(int nbEntrees) {
        super(nbEntrees);
    }
    @Override
    // Fonction d'activation d'un neurone (peut facilement être modifiée par héritage)
    public float activation(final float valeur) {
        return Math.max(0, valeur);
    }
}
