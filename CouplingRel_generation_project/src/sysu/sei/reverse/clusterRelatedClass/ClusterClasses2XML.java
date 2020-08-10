package sysu.sei.reverse.clusterRelatedClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import sysu.sei.reverse.designDecision.ClassChange;

public class ClusterClasses2XML {
	List<ClassChange> relatedClasses = null;
	
	/**
	 * The cluster consisted of classes with related changes wrote into a XML file
	 * Troy
	 * 2014-05-27
	 *@param path:�洢RCV���ļ�·��
	 *@param relatedClasses��this.getAcClassList()�ķ���ֵ
	 */	
	public List<ArrayList<ClassChange>> writeClusterClasses2XML(String path,List<ClassChange> relatedClasses){
		this.relatedClasses = relatedClasses;
		HashMap<ClassChange,ClassChange> relatedClassMap = new HashMap<ClassChange,ClassChange>();
		String[] lable = ClusterClassesWithRelatedChanges.clusterRelatedClasses46Dimen(path);//��ȡrelated classes�ķ����ǩ
		int k=0;
		for(int i=0; i < relatedClasses.size(); i++){
			for(int j =0; j < relatedClasses.size(); j++){
				k++;
			if(i!=j && relatedClasses.get(i).ifRelated(j)){
				//HashMap<Object,Object> twoClassChange = new HashMap<Object,Object>();
				//twoClassChange.put(relatedClasses.get(i), relatedClasses.get(j));
				if(Integer.getInteger(lable[k]) == 2){
					relatedClassMap.put(relatedClasses.get(i), relatedClasses.get(j));
				}												
			}		
		  }
		}
		List<ArrayList<ClassChange>> lis = clusterRelatedClasses(relatedClassMap);
		
		return lis;
	}
	
	public List<ArrayList<ClassChange>> clusterRelatedClasses(HashMap<ClassChange,ClassChange> map){
		List<ClassChange> list4MapKey = new ArrayList<ClassChange>();
		List<ClassChange> list4MapValue = new ArrayList<ClassChange>();
		//List<ArrayList<ClassChange>> list4Clusters = new ArrayList<ArrayList<ClassChange>>();
		
		Set<ClassChange> set = map.keySet();
	       Iterator<ClassChange> it = set.iterator();
	       while(it.hasNext()){	    	   
	    	   ClassChange cc = (ClassChange)it.next();
	    	   list4MapKey.add(cc);
	    	   list4MapValue.add(map.get(cc));
	       }
	       

		return indirectRelatedCluster(list4MapKey,list4MapValue);//��ѡ��ֱ����ؾ��������ؾ���
	}
	/**
	 * ����������ֱ����ص���
	 * @param list4MapKey
	 * @param list4MapValue
	 * @return
	 */
	
	public List<ArrayList<ClassChange>> directRelatedCluster(List<ClassChange> list4MapKey, List<ClassChange> list4MapValue){
		ClassChange ccKey = null;
		ClassChange ccValue = null;
		List<ArrayList<ClassChange>> parentList = new ArrayList<ArrayList<ClassChange>>();
		for(int i=0;i<list4MapKey.size();i++){
			ArrayList<ClassChange> childList = new ArrayList<ClassChange>();
			ccKey = list4MapKey.get(i);
			ccValue = list4MapValue.get(i);
	    	childList.add(ccKey);
	    	childList.add(ccValue);			
			for(int j=i+1;j<list4MapKey.size();j++){
				if(list4MapKey.get(i).equals(list4MapKey.get(j))){   	
			    	ccValue = list4MapValue.get(j);
			    	childList.add(ccValue);
			    	//ɾ��ԭʼ�����бȽϹ�����࣬��ô���������index��ǰ����
			    	list4MapKey.remove(j);
			    	list4MapValue.remove(j);
			    	--j;
				}				
			}
			
	    	//list4MapKey.remove(i);
	    	//list4MapValue.remove(i);
	    	
			parentList.add(childList);
	    }


		return parentList;
	}
	
	/**
	 * ���˾���������ֱ����ص��࣬����������Щ����������ص���
	 * @param a
	 * @param b
	 * @return
	 */
	public List<ArrayList<ClassChange>> indirectRelatedCluster(List<ClassChange> list4MapKey, List<ClassChange> list4MapValue){
		
		ClassChange ccKey = null;
		ClassChange ccValue = null;
		List<ArrayList<ClassChange>> parentList = new ArrayList<ArrayList<ClassChange>>();
		for(int i=0;i<list4MapKey.size();i++){
			ArrayList<ClassChange> childList = new ArrayList<ClassChange>();
			ccKey = list4MapKey.get(i);
			ccValue = list4MapValue.get(i);
	    	childList.add(ccKey);
	    	childList.add(ccValue);			
			for(int j=i+1;j<list4MapKey.size();j++){
				if(list4MapKey.get(i).equals(list4MapKey.get(j))){   	
			    	ccValue = list4MapValue.get(j);
			    	childList.add(ccValue);
			    	//ɾ��ԭʼ�����бȽϹ�����࣬��ô���������index��ǰ���𣿻ᣡ
			    	list4MapKey.remove(j);
			    	list4MapValue.remove(j);
			    	--j;
				}
								
			}
			for(int k=0; k<list4MapKey.size();k++){
				for(int n=1; n<childList.size();n++){//childList��size()�������¼�����಻�ϱ��
					if(list4MapKey.get(k).equals(childList.get(n))){   	
				    	ccValue = list4MapValue.get(k);
				    	childList.add(ccValue);
				    	//ɾ��ԭʼ�����бȽϹ�����࣬��ô���������index��ǰ���𣿻ᣡ
				    	list4MapKey.remove(k);
				    	list4MapValue.remove(k);
				    	--k;
					}		
				}
			}
				    	
			parentList.add(childList);
	    }


		return parentList;
	}

}
