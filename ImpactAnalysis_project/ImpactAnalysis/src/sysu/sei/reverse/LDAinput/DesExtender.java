package sysu.sei.reverse.LDAinput;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.data.parse.SenseKeyParser;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;

public class DesExtender {

	public static String getSynonyms(IDictionary dict, String input){
		String relatedWord = new String(input);
		POS[] values = POS.values();
		for(int i =0; i < values.length; i++){
	    	if(dict.getIndexWord(input, values[i])!= null){
	    		relatedWord = relatedWord.concat(" "+values[i]);
	    		IIndexWord idxWord =dict.getIndexWord(input, values[i]);
	    		for(int j=0; j <idxWord.getWordIDs().size(); j++ ){	    		
	    			IWordID wordID = idxWord.getWordIDs().get(0) ; // 1st meaning
	    			IWord word = dict.getWord(wordID);	    		
	    			ISynset synset = word.getSynset (); //ISynset是一个词的同义词集的接口
	    			
	    			for(IWord w : synset.getWords()){
	    				if(!w.getLemma().equals(input))
	    					relatedWord = relatedWord.concat(" "+w.getLemma());		    			
	    			}
	    		}	
	    	}	 
		}
		System.out.println(relatedWord);
	    return relatedWord;
	    
	}
	
	public static String getWordLemma(IDictionary dict, String input){
		String lemmaWord = new String(input);
		POS[] values = POS.values();
		if(input != null && input.length() >0){
		    for(int i =0; i < values.length; i++){
				edu.mit.jwi.morph.WordnetStemmer stemmer = new edu.mit.jwi.morph.WordnetStemmer(dict);  			
		    	System.out.println("input --"+input);
				if(input != null && input.length()>1 &&  stemmer.findStems(input, values[i])!= null && stemmer.findStems(input, values[i]).size() > 0){
		    		if(input != stemmer.findStems(input, values[i]).get(0))
		    			lemmaWord = stemmer.findStems(input, values[i]).get(0);
		    		if(lemmaWord.length()<1)
		    			System.out.println("problem when translating "+lemmaWord);
		    	 //	IIndexWord idxWord =dict.getIndexWord(input, values[i]);
		    	
		    	}	 
			}
			System.out.println(lemmaWord);
		}
		
		return lemmaWord;
	    
	}
	
	
	public static void folderCleaner(String folderLocation){
		File folder = new File(folderLocation);
		if(folder.isDirectory()){
			String[] fileList = folder.list();           
			for(int i = 0; i < fileList.length; i++){
				File descriptionFile = new File(folderLocation+"/"+fileList[i]);
				if(!descriptionFile.isDirectory()){
					fileCleaner(folderLocation+"/"+fileList[i]);
				}
			}
		}
		else fileCleaner(folderLocation);
	}
	
	public static void fileCleaner(String fileLocation){
		
		File descriptionFile = new File(fileLocation);
			//		System.out.println(folderLocation+"/"+fileList[i]);
			try {
				BufferedReader br;
				br = new BufferedReader(new FileReader(descriptionFile ));
				String data;
				try {
				
					data = br.readLine();
					while( data!=null){   
					    String word = new String();
					 	while(data.indexOf(" ")>=0){
						    word = data.substring(0, data.indexOf(" "));
						    data = data.substring(data.indexOf(" ")+1);
						 	System.out.println(word); 
						 	
					 	}
					 	word = data;
					 	System.out.println(word); 
						data = br.readLine(); //接着读下一行    
					    
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//一次读入一行，直到读入null为文件结束   	
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   

	}
	    
	    public static void main(String[] args) throws IOException{

		       String wnhome = new String("C:/Program Files (x86)/WordNet/2.1");//System.getenv("WNHOME"); //获取WordNet根目录环境变量WNHOME

		       String path = wnhome + File.separator+ "dict";       

		       File wnDir=new File(path);

		       URL url=new URL("file", null, path);

		       IDictionary dict=new Dictionary(url);

		       dict.open();//打开词典

		      //getSynonyms(dict,"better"); //testing
		      //getWordLemma(dict,"this is not a better java file");
		       getWordLemma(dict,"ss");
		       getWordLemma(dict,"operations");
		      // folderCleaner("e:/a/test/emf2.6.0.txt");
		    }

		 

	}



