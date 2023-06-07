package fr.yncrea.cin3.ths.Neurone;

public class NeuroneSigmoide extends Neurone {

    public NeuroneSigmoide(int nbEntrees) {
        super(nbEntrees);
    }
    @Override
    // Fonction d'activation d'un neurone (peut facilement être modifiée par héritage)
    public float activation(final float valeur) {

        return (float) (1 / (1 + Math.exp(-valeur)));
    }
}
