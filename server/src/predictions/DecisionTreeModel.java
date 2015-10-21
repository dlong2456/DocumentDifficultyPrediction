package predictions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import weka.classifiers.trees.J48;
//give weka a training file.  ratios + status
//weka generates a model
//given these predicitons I will do this.  Take ratios
//use google docs to get right data, can do analysis offline 
//built non-weka/naive algorithm, then people corrected it. 
//deletes with respect to insertion ratio
//high spellcheck/debug
//generate models from Eclipse training data. Use that model to make the predicitons in Google Docs.
//talk to Kevin
public class DecisionTreeModel {

	private J48 j48Model = new J48();

	private boolean isj48ModelBuilt = false;

	private String WEKA_DATA_FILE_LOCATION = "data/userStudy2010.arff";
	
	public static final String PROGRESS_PREDICTION = "NO";
	public static final String DIFFICULTY_PREDICTION = "YES";


	public DecisionTreeModel() {
	}

	private void buildJ48Model() {
		weka.core.Instances trainingSet;
		URL url;

		try {
			//platform:/plugin/
			InputStream inputStream;
			if (DifficultyPredictionSettings.isReplayMode()) {
				inputStream = new FileInputStream( WEKA_DATA_FILE_LOCATION);
			} else {
			url = new URL(edu.cmu.scs.fluorite.plugin.Activator.getDefault()
					.getDescriptor().getInstallURL(), WEKA_DATA_FILE_LOCATION);
			

//			InputStream inputStream = url.openConnection().getInputStream();
			inputStream = url.openConnection().getInputStream();
			}


			trainingSet = new weka.core.Instances(new BufferedReader(
					new InputStreamReader(inputStream)));
			trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
			j48Model.buildClassifier(trainingSet);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
//TODO: Add focus 
	public void predictSituation(double insertRatio, double debugRatio,
			double navigationRatio, double styleRatio, double deleteRatio) {
//		String predictedValue = "NO";
		String predictedValue = PROGRESS_PREDICTION;
		try {
			// Declare five numeric attributes
			weka.core.Attribute insertPercentageAttribute = new weka.core.Attribute(
					"insertPercentage");
			weka.core.Attribute debugPercentageAttribute = new weka.core.Attribute(
					"debugPercentage");
			weka.core.Attribute navigationPercentageAttribute = new weka.core.Attribute(
					"navigationPercentage");
			weka.core.Attribute stylePercentageAttribute = new weka.core.Attribute(
					"stylePercentage");
			weka.core.Attribute deletePercentageAttribute = new weka.core.Attribute(
					"deletePercentage");

			// Declare the class attribute along with its values
			weka.core.FastVector fvClassVal = new weka.core.FastVector(2);
//			fvClassVal.addElement("YES");
//			fvClassVal.addElement("NO");
			fvClassVal.addElement(DIFFICULTY_PREDICTION);
			fvClassVal.addElement(PROGRESS_PREDICTION);
			weka.core.Attribute ClassAttribute = new weka.core.Attribute(
					"STUCK", fvClassVal);

			// Declare the feature vector
			// should be 6
			weka.core.FastVector fvWekaAttributes = new weka.core.FastVector(4);
			fvWekaAttributes.addElement(insertPercentageAttribute);
			fvWekaAttributes.addElement(debugPercentageAttribute);
			fvWekaAttributes.addElement(navigationPercentageAttribute);
			fvWekaAttributes.addElement(stylePercentageAttribute);
			fvWekaAttributes.addElement(deletePercentageAttribute);
			fvWekaAttributes.addElement(ClassAttribute);

			// Create an empty training set
			weka.core.Instances testingSet = new weka.core.Instances("Rel",
					fvWekaAttributes, 10);

			// Set class index
			testingSet.setClassIndex(5);

			// Create the instance
			weka.core.Instance iExample = new weka.core.Instance(5);

			iExample.setValue(
					(weka.core.Attribute) fvWekaAttributes.elementAt(0),
					navigationRatio);
			iExample.setValue(
					(weka.core.Attribute) fvWekaAttributes.elementAt(1),
					debugRatio);
			iExample.setValue(
					(weka.core.Attribute) fvWekaAttributes.elementAt(2),
					styleRatio);
			iExample.setValue(
					(weka.core.Attribute) fvWekaAttributes.elementAt(3),
					insertRatio);
			iExample.setValue(
					(weka.core.Attribute) fvWekaAttributes.elementAt(4),
					deleteRatio);

			// add the instance
			testingSet.add(iExample);

			if (!isj48ModelBuilt) {
				long startTime = System.currentTimeMillis();
				buildJ48Model();
				isj48ModelBuilt = true;
				System.out.println(" Built J48 model in m:" + (System.currentTimeMillis() - startTime));

				
			}

			double predictedClass = j48Model.classifyInstance(testingSet
					.instance(0));
			predictedValue = testingSet.classAttribute().value(
					(int) predictedClass);
			System.out.println("Predicted Value: " + predictedValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		predictionManager.onPredictionHandOff(predictedValue);
	}
}
