package com.troy.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

import liuyang.nlp.lda.com.FileUtil;
import liuyang.nlp.lda.conf.ConstantConfig;
import liuyang.nlp.lda.conf.PathConfig;
import liuyang.nlp.lda.main.Documents;
import liuyang.nlp.lda.main.LdaGibbsSampling;
import liuyang.nlp.lda.main.LdaModel;
import liuyang.nlp.lda.main.LdaGibbsSampling.modelparameters;
import sysu.sei.reverse.Comparator.HistoryComparator;
import sysu.sei.reverse.LDAinput.InputCopper;
import com.troy.ui.base.BaseFrame;
import com.troy.ui.model.File4ProgramFile;
import com.troy.ui.tablemodel.ProgramFileTableModel;
import com.troy.ui.util.FileUtil4UI;
import com.troy.ui.util.NumberUtil;
import com.troy.ui.util.TemporarySpace;
/**
 * A {@link ActionListener}, {@link MouseListener} implementation, and
 * {@link BaseFrame} extension for main frames. This entity class consists
 * mostly of mutators to the main frame's state.
 * <p>
 * It allows managing:
 * <ul>
 * <li>action performed; and</li>
 * <li>mouse clicked.</li>
 * </ul>
 * <p>
 * It also allows:
 * <ul>
 * <li>initing main frame.</li>
 * </ul>
 * <p>
 * This class declares the helper classes {@link updateConsoleThread},
 * {@link updateDesignDecisionThread}, and {@link updateLDASubjectThread}
 */
public class MainFrame extends BaseFrame implements ActionListener,MouseListener{
	private static final long serialVersionUID = 1L;
	private static boolean isEnd = false;
	private static int versionNum = 0;
	private JMenuBar jMenuBar;
	private JMenu jMenu1, jMenu2, jMenu3, jMenu4;	
	private JMenuItem jMenuItem1, jMenuItem2, jMenuItem3, jMenuItem4,
	jMenuItem5, jMenuItem6, jMenuItem7, jMenuItem8, jMenuItem9,
	jMenuItem10;
	private JTextArea jTextArea = new JTextArea("请点击\"Browse\"按钮选择文件..."+"\n",8,8);
	private JTextArea jTextArea1 = new JTextArea("design decision info"+"\n",5,5);
	private JTextArea jTextArea2 = new JTextArea("LDA Subject info"+"\n",5,5);
	
	private JTable jTable;
	private JScrollPane jScrollPane;
	private JScrollPane jScrollPane1;
	private JScrollPane jScrollPane2;
	private JScrollPane jScrollPane3;
	
	private JLabel jLabel1 = new JLabel("  Program Name: null");
	private JLabel jLabel2 = new JLabel("  Version Number：  0");
	private JLabel jLabel3 = new JLabel("  Program Path： null");
	
	private JButton jb1 = new JButton("Browse");
	private JButton jb2 = new JButton("Remove");
	private JButton jb3 = new JButton("Confirm");
	
	private JPanel jPanel1 = new JPanel(); // 左侧操作栏
	private JPanel jPanel2 = new JPanel(); // 右侧显示栏

	private JPanel jPanel11 = new JPanel();
	private JPanel jPanel12 = new JPanel();
	
	private JPanel jPanel111 = new JPanel();
	private JPanel jPanel112 = new JPanel();
	
	private JPanel jPanel121 = new JPanel();
	private JPanel jPanel122 = new JPanel();
	private JPanel jPanel123 = new JPanel();
	
	private JPanel jPanel21 = new JPanel();
	private JPanel jPanel22 = new JPanel();
	private JPanel jPanel23 = new JPanel();
	
	private JPanel jPanel211 = new JPanel();
	private JPanel jPanel221 = new JPanel();
	private JPanel jPanel231 = new JPanel();
	
	private JPanel jPanel2111 = new JPanel();
	private JPanel jPanel2211 = new JPanel();
	private JPanel jPanel2311 = new JPanel();
	
	private JPanel jPanel1211 = new JPanel();
	private JPanel jPanel1212 = new JPanel();
	private JPanel jPanel1213 = new JPanel();
	
	//ImageIcon img = new ImageIcon("imag/shu.jpg");
	ImageIcon img1 = new ImageIcon("imag/heng.jpg");
	ImageIcon img2 = new ImageIcon("imag/heng1.jpg");
	private JLabel imageJLabel1 = new JLabel();
	private JLabel imageJLabel2 = new JLabel();
	private JLabel imageJLabel3 = new JLabel();
	
	ProgramFileTableModel programFileTableModel = new ProgramFileTableModel();
	private static List<File4ProgramFile> list = new ArrayList<File4ProgramFile>();
	private String[] filePath;
	private Thread consoleThread;
	private Thread designDecisionThread;
	private Thread LDASubjectThread;
	
	public MainFrame() {
		super("Design Decision of Software");
		init();
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 */	
	
	public void init(){
		
		
		jMenuBar = new JMenuBar();
		this.setJMenuBar(jMenuBar);
		jMenu1 = new JMenu("File");
		jMenu2 = new JMenu("Edit");
		jMenu3 = new JMenu("Set");
		jMenu4 = new JMenu("Help");
		jMenuBar.add(jMenu1);
		jMenuBar.add(jMenu2);
		jMenuBar.add(jMenu3);
		jMenuBar.add(jMenu4);
		
		BorderLayout borderLayout = new BorderLayout();		
		this.setLayout(borderLayout);
		
		GridLayout gridLayout1 = new GridLayout(2,1);
		GridLayout gridLayout2 = new GridLayout(3,1);
		//gridLayout.setVgap(30);
		jPanel1.setBackground(new Color(230,230,230));		
		jPanel2.setBackground(new Color(250,250,250));
		
		jPanel1.setLayout(new BorderLayout());
		jPanel2.setLayout(gridLayout2);
		
		jPanel1.add(jPanel11,BorderLayout.CENTER);
		jPanel11.setBorder(new TitledBorder("Selected Program Versions"));
		jPanel11.setLayout(new BorderLayout());	
		//jPanel11.add(jPanel111,BorderLayout.CENTER);
		jPanel11.add(jPanel112,BorderLayout.SOUTH);
		jPanel112.setBorder(new TitledBorder(""));
		
		jPanel112.add(jb1);		
		jPanel112.add(jb2);
		jb2.setEnabled(false);
		jPanel112.add(jb3);
		
		jPanel1.add(jPanel12,BorderLayout.SOUTH);
		jPanel12.setBorder(new TitledBorder("Console"));
		jPanel12.setLayout(new BorderLayout());
		jPanel12.add(jPanel121,BorderLayout.CENTER);
		jPanel121.setBackground(new Color(250,250,250));
		jPanel121.setLayout(new BorderLayout());
	//	jPanel121.add(jPanel1211,BorderLayout.WEST);
	//	jPanel121.add(jPanel1212,BorderLayout.CENTER);
	//	jPanel121.add(jPanel1213,BorderLayout.EAST);
		
		
		jPanel12.add(jPanel122,BorderLayout.WEST);
		jPanel12.add(jPanel123,BorderLayout.EAST);
		//imageJLabel1.setIcon(img);
		//jPanel122.add(imageJLabel1);
		//jPanel123.add(imageJLabel1);
		
		imageJLabel2.setIcon(img1);
		imageJLabel3.setIcon(img2);
		jPanel2.add(jPanel21);
		jPanel21.setLayout(new BorderLayout());
		jPanel21.setBackground(new Color(245,245,245));
		jPanel21.setBorder(new TitledBorder("Selected Program"));	
		jPanel21.add(jPanel211,BorderLayout.NORTH);
		jPanel211.add(imageJLabel2);
		jPanel21.add(jPanel2111,BorderLayout.CENTER);
		jPanel2111.setBackground(new Color(247,247,247));
		jPanel2111.setLayout(new GridLayout(3, 1));
		jPanel2111.add(jLabel1);
		jPanel2111.add(jLabel2);
		jPanel2111.add(jLabel3);
		
		jPanel2.add(jPanel22);
		jPanel22.setLayout(new BorderLayout());
		jPanel22.setBackground(new Color(245,245,245));
		jPanel22.setBorder(new TitledBorder("Design Decision"));
		jPanel22.add(jPanel221,BorderLayout.NORTH);
		jPanel221.add(imageJLabel2);
		jPanel22.add(jPanel2211,BorderLayout.CENTER);		
		jScrollPane2 = new JScrollPane(jTextArea1);
		jScrollPane2.doLayout();
		jPanel22.add(jScrollPane2);
		
		
		jPanel2.add(jPanel23);
		jPanel23.setLayout(new BorderLayout());
		jPanel23.setBackground(new Color(245,245,245));
		jPanel23.setBorder(new TitledBorder("LDA Subject"));
		jPanel23.add(jPanel231,BorderLayout.NORTH);
		jPanel231.add(imageJLabel2);
		jPanel23.add(jPanel2311,BorderLayout.CENTER);
		jScrollPane3 = new JScrollPane(jTextArea2);
		jScrollPane3.doLayout();
		jPanel23.add(jScrollPane3);
		
		this.add(jPanel1,BorderLayout.CENTER);
		this.add(jPanel2,BorderLayout.EAST);
		
		jb1.addActionListener(this);
		jb3.addActionListener(this);
		
		
		
		jTable = new JTable(programFileTableModel);
		jTable.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2){
					jb2.setEnabled(true);
				}
				
			}
		});
		//jTable.addMouseListener(this);
		jTable.updateUI();
		//SetJTableCenter.setJTableCenter(jTable);
		jScrollPane = new JScrollPane(jTable);
		jTable.setFillsViewportHeight(true);
		jPanel11.add(jScrollPane);

		jScrollPane1 = new JScrollPane(jTextArea);
		jScrollPane1.setWheelScrollingEnabled(true);
		jScrollPane1.doLayout();    // 如果不是有改语句，滚动条会显示在中间
		   
		
		        
		
				
		//jTextArea.setText("test");
		jPanel121.setLayout(new GridLayout(1, 1));
		//jTextArea.setAutoscrolls(true);
		jPanel121.add(jScrollPane1);
	}
	


	public void actionPerformed(ActionEvent e) {//浏览按钮

		if (e.getSource() == jb1){
			
			DecimalFormat df = new DecimalFormat("#.00");//double保留两位小数
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setMultiSelectionEnabled(true);
			jFileChooser.setFileSelectionMode(jFileChooser.DIRECTORIES_ONLY);

			int returnVal = jFileChooser.showOpenDialog(jPanel11);
			 if(returnVal == JFileChooser.APPROVE_OPTION) {	
				  File[] files = jFileChooser.getSelectedFiles();
				  for(File file:files){
					  File4ProgramFile file4PF = new File4ProgramFile();
					  file4PF.setFileName(file.getName());
					  System.out.println("name:"+file.getName());
					  file4PF.setAbsolutePath(file.getAbsolutePath());
					  System.out.println("name:"+file.getAbsolutePath());
					 // file4PF.setSize(NumberUtil.Num4Double((double)file.length()/1024));	
					  file4PF.setSize(String.valueOf(df.format(FileUtil4UI.getDirSize(file))));
					  list.add(file4PF);
				  }
				  
				  programFileTableModel.setList(list);				 
				  jTable = new JTable(programFileTableModel);
				  jScrollPane = new JScrollPane(jTable);
				  
				  
				        
				  jPanel11.add(jScrollPane);
				  
				  String programName = files[0].getName().replaceAll("\\d+","");
				  
				  versionNum += files.length;
				  
				  jLabel1.setText("  Program Name:  "+programName.replaceAll("\\-","").replaceAll("\\.",""));
				  jLabel2.setText("  Version Number： "+versionNum);
				  jLabel2.setForeground(new Color(255,0,0));
				  jLabel3.setText("  Program Path： "+files[0].getAbsolutePath().subSequence(0, files[0].getAbsolutePath().indexOf("\\"))+"\\");



				  jTextArea.setText("");				  
				  jTextArea.setText("文件添加成功,点击“Confirm”继续...");
			   }
			 
			// list.clear();
			 jPanel1.remove(jPanel11);
			 jPanel1.add(jPanel11);	
			 
             
            // jPanel1.validate();
             jPanel11.updateUI();
			 jPanel1.updateUI();
		}
		
		
		if (e.getSource() == jb3){//确定按钮
			if(list.size() != 0){
				if(versionNum<2){
					JOptionPane.showMessageDialog(null,"请选择2个或更多的项目版本","错误",JOptionPane.ERROR_MESSAGE);
					return;
				}
				consoleThread = new updateConsoleThread();// 启动控制台线程
				consoleThread.start();
				designDecisionThread = new updateDesignDecisionThread();
				designDecisionThread.start();
			    LDASubjectThread = new updateLDASubjectThread();
				LDASubjectThread.start();
				
				Object[] object = list.toArray();
				int i = object.length;
				filePath = new String[i];
				for(int j=0;j<i;j++){
					filePath[j] = ((File4ProgramFile)object[j]).getAbsolutePath();
				}
				
			}else{
				System.out.println("选择的文件夹路径不存在！！");
				TemporarySpace.setConsoleString("选择的文件夹路径不存在！！");
				return;
			}
			
			HistoryComparator c = new HistoryComparator();
			c.setPaths(filePath);
			c.generateHistory(c.paths);
			for(int i = 0; i < c.history.size(); i ++){
				c.history.get(i).writeLog("D:/b/change"+(i+25)+".txt");
				c.history.get(i).generateLDAInput("D:/a/version("+i+"vs"+(i+1)+")");
			}
			c.compareHistory();
			
			TemporarySpace.setConsoleString("设计决策已生成,正在进行切词处理...");
			
			InputCopper copper = new InputCopper();							
			copper.filterFolder("D:/a");
			
			TemporarySpace.setConsoleString("切词处理完毕,正在进行LDA主题提取...");
			
		    File file = new File("D:/a");
		    String resultPath = "";
		    File resFile = null;
		    if(file.isDirectory()){
		    	File[] fileList = file.listFiles();
			    for(int i = 0; i < fileList.length; i++){
			    	File file1 = fileList[i];
			    	if(file1.isFile()){
			    		
			    		//String originalDocsPath = "D:/a";
			    		resultPath = PathConfig.LdaResultsPath;
			    		String parameterFile = ConstantConfig.LDAPARAMETERFILE;
			    		
			    		try {
							modelparameters ldaparameters = new modelparameters();
							LdaGibbsSampling.getParametersFromFile(ldaparameters, parameterFile);
							Documents docSet = new Documents();
							docSet.readDocs(file1);//取得当前文件的绝对路径，将此文件作为LDA主题提取对象
							
							System.out.println("wordMap size " + docSet.termToIndexMap.size());
							TemporarySpace.setConsoleString("wordMap size " + docSet.termToIndexMap.size());
							
							resFile = new File(resultPath+file1.getName()+"/");
							FileUtil.mkdir(resFile);
							
							
							LdaModel model = new LdaModel(ldaparameters);
							System.out.println("1 Initialize the model ...");
							TemporarySpace.setConsoleString("1 Initialize the model ...");
							model.initializeModel(docSet);
							System.out.println("2 Learning and Saving the model ...");
							TemporarySpace.setConsoleString("2 Learning and Saving the model ...");
							model.inferenceModel(docSet,resFile.getAbsolutePath());
							System.out.println("3 Output the final model ...");
							TemporarySpace.setConsoleString("3 Output the final model ...");
							model.saveIteratedModel(ldaparameters.iteration, docSet,resFile.getAbsolutePath());
							System.out.println("Done!");
							TemporarySpace.setConsoleString("Done!");
							//docSet.clear();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
			    		
			    	}
			    }
		    }
		    TemporarySpace.setConsoleString("LDA提取主题完毕,请在自动打开的文件夹中查看相应主题词。");
		    
		    FileUtil4UI.OpenTxtFile(resFile.getAbsolutePath());
		    isEnd = true;
		    
		    if(consoleThread.isAlive()){
		    	consoleThread.stop();
		    }
			
			if(designDecisionThread.isAlive()){
				designDecisionThread.stop();
			}
			
			if(LDASubjectThread.isAlive()){
				LDASubjectThread.stop();
			}
			
		}
	}

	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	
	/**
	 * A {@link Thread} extension for update console threads. This boundary class
	 * communicates with the classes {@link TemporarySpace}, and JScrollBar}, and
	 * consists mostly of mutators to the update console thread's state.
	 * <p>
	 * It allows:
	 * <ul>
	 * <li>running update console thread.</li>
	 * </ul>
	 */
	class updateConsoleThread extends Thread{ //更新console的线程
		public void run(){
			while(!isEnd){
				
				if(TemporarySpace.getConsoleString().size()!=0){
					String queueHead = TemporarySpace.getConsoleString().get(0);//取出队首元素
					TemporarySpace.getConsoleString().remove(0);//删除队首元素
					jTextArea.append(queueHead+"\n");	
					jTextArea.paintImmediately(jTextArea.getBounds());
					
					JScrollBar jscrollBar = jScrollPane1.getVerticalScrollBar();//是滚动条处于最下方
					if (jscrollBar != null) {
						jscrollBar.setValue(jscrollBar.getMaximum());
					}

				}
			}
		}
	}
	
	/**
	 * A {@link Thread} extension for update design decision threads. This boundary
	 * class communicates with the classes {@link TemporarySpace}, and JScrollBar},
	 * and consists mostly of mutators to the update design decision thread's state.
	 * <p>
	 * It allows:
	 * <ul>
	 * <li>running update design decision thread.</li>
	 * </ul>
	 */
	class updateDesignDecisionThread extends Thread{
		public void run(){
			while(!isEnd){
				if(TemporarySpace.getDesignDecisionString().size()!=0){					
					String queueHead = TemporarySpace.getDesignDecisionString().get(0);//取出队首元素
					TemporarySpace.getDesignDecisionString().remove(0);//删除队首元素
					jTextArea1.append(queueHead+"\n");	
					jTextArea1.paintImmediately(jTextArea1.getBounds());
					
					JScrollBar jscrollBar = jScrollPane2.getVerticalScrollBar();//是滚动条处于最下方
					if (jscrollBar != null) {
						jscrollBar.setValue(jscrollBar.getMaximum());
					}
				}
			}
		}
	}
	
	/**
	 * A {@link Thread} extension for update LDA subject threads. This boundary
	 * class communicates with the classes {@link TemporarySpace}, and JScrollBar},
	 * and consists mostly of mutators to the update LDA subject thread's state.
	 * <p>
	 * It allows:
	 * <ul>
	 * <li>running update LDA subject thread.</li>
	 * </ul>
	 */
	class updateLDASubjectThread extends Thread{
		public void run(){
			while(!isEnd){
				if(TemporarySpace.getLDASubjectString().size()!=0){					
					String queueHead = TemporarySpace.getLDASubjectString().get(0);//取出队首元素
					TemporarySpace.getLDASubjectString().remove(0);//删除队首元素
					jTextArea2.append(queueHead+"\n");	
					jTextArea2.paintImmediately(jTextArea2.getBounds());
					
					JScrollBar jscrollBar = jScrollPane3.getVerticalScrollBar();//是滚动条处于最下方
					if (jscrollBar != null) {
						jscrollBar.setValue(jscrollBar.getMaximum());
					}
				}
			}
		}
	}
	
	public static void main(String args[]){
//		try{				
//			 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		 }catch(Exception e){
//			 e.printStackTrace();
//		 }
		try{				
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			//org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		MainFrame mainFrame  = new MainFrame();
		mainFrame.showMe();
	}
}
