package com.midas.classify.data;

/**
 *
 * @author Diego
 */
public class EvaluationDataSet {

    private Instances trainingDataSet;
    private Instances validationDataSet;

    public EvaluationDataSet(Instances trainingDataSet, Instances validationDataSet) {
        this.trainingDataSet = trainingDataSet;
        this.validationDataSet = validationDataSet;
    }

    public Instances getTrainingDataSet() {
        return trainingDataSet;
    }

    public Instances getValidationDataSet() {
        return validationDataSet;
    }
}
