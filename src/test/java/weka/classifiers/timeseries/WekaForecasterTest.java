package weka.classifiers.timeseries;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.Instances;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.timeseries.WekaForecaster;
import weka.classifiers.timeseries.core.TSLagMaker;
import weka.classifiers.timeseries.eval.ErrorModule;
import weka.classifiers.timeseries.eval.MAPEModule;
import weka.classifiers.timeseries.eval.TSEvaluation;

/**
 * Using weka api to forecast
 */
public class WekaForecasterTest {

	static final int testDataLength = 1;
	static final int stepAhead = 1;
	static final boolean artIndex = false;

	WekaForecasterTest() {

	}

	// public static void main(String args[]) {
	// WekaForecasterTest t = new WekaForecasterTest();
	// t.forecast(new File("weka" + File.separator
	// + "org.jfree.chart.annotations.XYDrawableAnnotation.arff"));
	// }

	Boolean allSame(Instances data, int indexOfAtt) {
		Boolean output = true;
		double[] dataTemp = data.attributeToDoubleArray(indexOfAtt);
		for (double single : dataTemp) {
			if (single != dataTemp[0]) {
				output = false;
			}
		}
		return output;
	}

	ArrayList<Double> forecast(File input) {
		ArrayList<Double> result = new ArrayList<Double>();
		try {
			// load the data
			Instances data = new Instances(new BufferedReader(new FileReader(
					input)));
			data.stableSort(data
					.attribute(Main.dataAttributes[Main.dataAttributes.length - 1]));
			Instances train = new Instances(data, 0, data.size()
					- testDataLength);
			Instances test = new Instances(data, data.size() - testDataLength,
					testDataLength);

			for (String attribute : Main.forecastAttributes) {
				// checkDiff(train);
				// new forecaster
				int indexOfAtt = 0;
				for (String s : Main.dataAttributes)
					if (s.equals(attribute)) {
						break;
					} else {
						indexOfAtt++;
					}
				result.add(test.attributeToDoubleArray(indexOfAtt)[0]);
				if (allSame(train, indexOfAtt)) {
					// System.out.println("all same");
					result.add(Double.NaN);
				} else {
					WekaForecaster forecaster = new WekaForecaster();
					forecaster.setFieldsToForecast(attribute);
					if (!artIndex) {
						weka.classifiers.functions.MultilayerPerceptron scheme = new weka.classifiers.functions.MultilayerPerceptron();// SMOreg
																											// ;GaussianProcesses;
																											// LinearRegression;MultilayerPerceptron
					//	scheme.setOptions(weka.core.Utils
					//			.splitOptions("-C 1.0 -N 0 -I \"weka.classifiers.functions.supportVector.RegSMOImproved -T 0.001 -V -P 1.0E-12 -L 0.001 -W 1\" -K \"weka.classifiers.functions.supportVector.NormalizedPolyKernel -E 2.0 -C 250007\""));

						forecaster
								.getTSLagMaker()
								.setTimeStampField(
										Main.dataAttributes[Main.dataAttributes.length - 1]);
						// forecaster.getTSLagMaker().setMinLag(1);
						// forecaster.getTSLagMaker().setMaxLag(4); // monthly
						// data
						// add a quarter of the year indicator field
						forecaster.getTSLagMaker().setAddQuarterOfYear(true);
						forecaster.getTSLagMaker().setAddMonthOfYear(true);
						// forecaster.getTSLagMaker().setAddDayOfWeek(true);
						// forecaster.getTSLagMaker().determinePeriodicity(train,
						// "Date", TSLagMaker.Periodicity.QUARTERLY);
						forecaster.getTSLagMaker().determinePeriodicity(train,Main.dataAttributes[Main.dataAttributes.length - 1],
										null);
						forecaster.setBaseForecaster(scheme);
					} else {
						//use artificial index
						forecaster
						.getTSLagMaker()
						.setTimeStampField(
								"");
						
						weka.classifiers.functions.MultilayerPerceptron scheme = new weka.classifiers.functions.MultilayerPerceptron();
						forecaster.setBaseForecaster(scheme);
					}
					// .splitOptions(" -C 1.0 -N 0 -I \"weka.classifiers.functions.supportVector.RegSMOImproved -T 0.001 -V -P 1.0E-12 -L 0.001 -W 1\" -K \"weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007\""));

					// build the model
					forecaster.buildForecaster(train, System.out);
					// prime the forecaster with enough recent historical data
					// to cover up to the maximum lag.
					forecaster.primeForecaster(train);
				//	if (forecaster.getTSLagMaker().isUsingAnArtificialTimeIndex()) System.out.println("yea");
					// forecast for <stepAhead> units beyond the end of the training data
					// List<List<NumericPrediction>> forecast = forecaster.forecast(stepAhead, System.out);

					// -----evaluation
					// a new evaluation object (evaluation on the training data)
					// TSEvaluation eval = new TSEvaluation(train, test);

					// generate and evaluate predictions for up to 12 stepsahead
					// eval.setHorizon(stepAhead);
					// eval.setEvaluateOnTrainingData(false);
					// prime with enough data to cover our maximum lag
					// eval.setPrimeWindowSize(4);
					// eval.setEvaluationModules("weka.classifiers.timeseries.eval.RMSEModule,weka.classifiers.timeseries.eval.MAPEModule");
					// eval.evaluateForecaster(forecaster, System.out);
					// ------
					// output the predictions. Outer list is over the steps;inner list
	
					// System.out.println(+ forecast.size()+" 1-step-ahead prediction(s): (Name of attribute:Actural,Predicted Value)");
					int countNA = 0;
					// ----
					forecaster.primeForecaster(train);
					List<List<NumericPrediction>> eforecast = forecaster
							.forecast(1, System.out);
					// System.out.print("" + eforecast.get(0).get(0).predicted()+ " ");
					// ----

					// List<List<NumericPrediction>> predLL = eval.getM_predictionsForTestData().get(0).getM_predictions();

					// ErrorModule errormodule = eval.getM_predictionsForTestData().get(0);
					// NumericPrediction predForTarget = errormodule.getM_predictions().get(0).get(0);

					int indexOfFa = 0;
					for (String s : Main.forecastAttributes)
						if (s.equals(attribute)) {
							break;
						} else {
							indexOfFa++;
						}
					Main.count[indexOfFa] += 1;
					result.add((double) Math.round(eforecast.get(0).get(0).predicted()));
					Main.sumErr[indexOfFa] += Math.abs(Math.round(eforecast.get(0).get(0).predicted())- test.attributeToDoubleArray(indexOfAtt)[0]);
				}
				// if (countNA < Main.dataAttributes.length - 2) {
				// System.out.println(eval.toSummaryString());
				// }
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
}