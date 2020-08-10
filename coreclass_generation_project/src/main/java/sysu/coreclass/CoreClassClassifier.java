package sysu.coreclass;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class CoreClassClassifier implements Serializable {

	private static final long serialVersionUID = 1L;

	private RandomForest classifier;
	private Instances instancesTrain;

	public CoreClassClassifier() {
		classifier = new RandomForest();

		//Resource resource = new ClassPathResource("file/train.arff");

		File trainFile = null;
		trainFile = new File("file/train.arff");
		ArffLoader atf = new ArffLoader();

		try {
			atf.setFile(trainFile);
			instancesTrain = atf.getDataSet();
			instancesTrain.deleteAttributeAt(0);
			instancesTrain.setClassIndex(instancesTrain.numAttributes() - 1);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ArffLoader setFile is failed. by RamdomForest.");
		}
		init();
		train();
	}

	private void init() {
		// classifier.setBagSizePercent(30);
		// classifier.setNumFeatures(6);
		classifier.setNumIterations(300);
	}

	private void train() {
		try {
			classifier.buildClassifier(instancesTrain);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("build classifier error.");
		}
	}

	public List<Double> classify(List<List<Integer>> datas) throws Exception {

		List<Double> result = new ArrayList<Double>();

		String[] attributeNames = new String[] { "classInnerCount", "classOuterCount", "classInnerCount:maxInnerCount",
				"classInnerCount:aveInnerCount", "classOuterCount:maxOuterCount", "classOuterCount:aveOuterCount",
				"classType", "classIndex", "isCoreType1", "isCoreType2", "isCoreType3", "isCoreType4", "isCoreType5",
				"classInnerWeight", "classOuterWeight", "classNum", "methodNum", "methodNum:methodsNum", "newMethodNum",
				"newMethodNum:newMethodsNum", "changeMethodNum", "changeMethodNum:changeMethodsNum", "deleteMethodNum",
				"deleteMethodNum:deleteMethodsNum", "methodsInnerCount", "methodsOuterCount", "statementNum",
				"statementNumWeight", "statementNum:maxStatementNum", "statementNum:aveStatementNum", "newStatementNum",
				"newStatementNum:newStatementsNum", "changeStatementNum", "changeStatementNum:changeStatementsNum",
				"deleteStatementNum", "deleteStatementNum:deleteStatementsNum", "class" };
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for (String attrName : attributeNames) {
			Attribute attribute = new Attribute(attrName);
			attributes.add(attribute);
		}

		Instances dataset = new Instances("test-dataset", attributes, 0);
		for (int i = 0; i < datas.size(); i++) {
			double[] attValues = new double[datas.get(i).size()];
			for (int j = 0; j < datas.get(i).size(); j++) {
				attValues[j] = datas.get(i).get(j);
			}
			Instance instance = new DenseInstance(1.0, attValues);
			dataset.add(instance);
		}

		dataset.setClassIndex(dataset.numAttributes() - 1);

		int num = dataset.numInstances();
		for (int i = 0; i < num; i++) {
			Double d = classifier.classifyInstance(dataset.instance(i));
			result.add(d);
		}

		return result;

	}

	public void saveModel() {
		File file = new File("model.dat");
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.flush();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {

	}

}