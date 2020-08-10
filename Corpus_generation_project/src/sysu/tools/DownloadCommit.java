package sysu.tools;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map.Entry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class DownloadCommit {
	public static void downloadJavaFile(String url, int revisionNum, String savedPath) throws SVNException, IOException {
		System.out.println(revisionNum);
		SVNURL repositoryBaseUrl = SVNURL.parseURIEncoded(url);
		SVNRepository repository = SVNRepositoryFactory.create(repositoryBaseUrl);
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager();
		repository.setAuthenticationManager(authManager);

		Collection<SVNLogEntry> logEntries = new LinkedList<SVNLogEntry>();
		repository.log(new String[] { "/" }, logEntries, revisionNum, revisionNum, true, true);

		for (SVNLogEntry logEntry : logEntries) {

			//只下载包含类文件在2-7范围内的提交
			int javaSize = 0;
			for (Entry<String, SVNLogEntryPath> entry : logEntry.getChangedPaths().entrySet()) {
				if(entry.getValue().toString().endsWith(".java"))
					javaSize++;				
			}
			if(javaSize<4) {
				System.out.println("revisionNum: " + revisionNum);
				return;
			}
			
			File dir1 = new File(savedPath+"/"  + revisionNum + "/new/");// 当前版本文件的存储路径
			File dir2 = new File(savedPath+"/"  + revisionNum + "/old/");// 前一个版本文件的存储路径
			File dir3 = new File(savedPath+"/"  + revisionNum + "/commitIfo");//结果文件的存储路径
			if (!dir1.exists()) {
				dir1.mkdirs();
			}
			if (!dir2.exists()) {
				dir2.mkdirs();
			}
			if(!dir3.exists()){
				dir3.mkdirs();
			}
			

			
			
			File logFile = new File(dir3.getPath() + "/" + logEntry.getRevision() + ".txt");
			BufferedWriter output = new BufferedWriter(new FileWriter(logFile));

			
			
			output.write("---------------------------------------------\r\n");

			// 获取修改版本号

			output.write("修订版本号: " + logEntry.getRevision() + "\r\n");

			// 获取提交者

			output.write("提交者: " + logEntry.getAuthor() + "\r\n");

			// 获取提交时间

			output.write("日期: " + logEntry.getDate() + "\r\n");

			// 获取注释信息

			output.write("注释信息: " + logEntry.getMessage() + "\r\n");
			
			//output.write("Revision Properties: " + logEntry. + "\r\n");

			int changedSize = logEntry.getChangedPaths().size();
			
			if (changedSize > 0) {			
				output.write("受影响的文件、目录:\r\n");

				for (Entry<String, SVNLogEntryPath> entry : logEntry.getChangedPaths().entrySet()) {

					output.write(entry.getValue() + "\r\n");
					// 此变量用来存放要查看的文件的属性名/属性值列表。
					SVNProperties fileProperties = new SVNProperties();

					String temp = entry.getValue().toString();
					String filePath = "";// 文件的下载路径
					if (temp.indexOf("(") > 0) {
						filePath = temp.substring(temp.indexOf("/"), temp.indexOf("("));
					} else {
						filePath = temp.substring(temp.indexOf("/"));
					}

					String[] temps = filePath.split("/");
					String srcname = temps[temps.length - 1];// 获得文件名，不含路径

					// 只提取java文件
					if (srcname.endsWith(".java")) {
						if (repository.checkPath(filePath, logEntry.getRevision()) != SVNNodeKind.NONE) {
							// 此输出流用来存放要查看的文件的内容。
							ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();// 当前版本的文件输出流

							repository.getFile(filePath, logEntry.getRevision(), fileProperties, outputStream1);
							BufferedWriter output1 = new BufferedWriter(new FileWriter(dir1 + "/" + srcname));
							output1.write(outputStream1.toString());
							outputStream1.close();

							output1.close();
						}

						if (logEntry.getRevision() > 0
								&& repository.checkPath(filePath, logEntry.getRevision() - 1) != SVNNodeKind.NONE) {
							ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();// 前一个版本的文件输出流

							repository.getFile(filePath, logEntry.getRevision() - 1, fileProperties, outputStream2);
							BufferedWriter output2 = new BufferedWriter(new FileWriter(dir2 + "/" + srcname));
							output2.write(outputStream2.toString());
							outputStream2.close();
							output2.close();
						}
					}
				}

			}
			output.close();

		}

	}

	

	public static void main(String[] args) throws SVNException {

		try {
			/*for(int i=567; i<=4704; i++){
				try {
					CommitDiff.downloadJavaFile("https://svn.code.sf.net/p/jppf-project/code/trunk jppf-project-code", i, "E:/project_data/jppf");
				} catch (SVNException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					continue;
				}
			}*/
			for(int i=1; i<=6000; i++){
				try {
					System.out.println(i+"test");
					CommitDiff.downloadJavaFile("https://sourceforge.net/p/jhotdraw/svn/", i, "E:\\ImpactAnalysis\\data2\\");
					System.out.println("test");
				} catch (SVNException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					System.out.println("error");
					continue;
				}
			}
			
			
			
			
			/*for(int i=9056; i<=9056; i++){
				CommitDiff.downloadJavaFile("https://svn.code.sf.net/p/makagiga/code/trunk/makagiga", i, "D:/log/makagiga");
			}for(int i=9548; i<=9548; i++){
				CommitDiff.downloadJavaFile("https://svn.code.sf.net/p/makagiga/code/trunk/makagiga", i, "D:/log/makagiga");
			}for(int i=9128; i<=9128; i++){
				CommitDiff.downloadJavaFile("https://svn.code.sf.net/p/makagiga/code/trunk/makagiga", i, "D:/log/makagiga");
			}for(int i=9181; i<=9181; i++){
				CommitDiff.downloadJavaFile("https://svn.code.sf.net/p/makagiga/code/trunk/makagiga", i, "D:/log/makagiga");
			}for(int i=9187; i<=9187; i++){
				CommitDiff.downloadJavaFile("https://svn.code.sf.net/p/makagiga/code/trunk/makagiga", i, "D:/log/makagiga");
			}for(int i=9223; i<=9223; i++){
				CommitDiff.downloadJavaFile("https://svn.code.sf.net/p/makagiga/code/trunk/makagiga", i, "D:/log/makagiga");
			}for(int i=9232; i<=9232; i++){
				CommitDiff.downloadJavaFile("https://svn.code.sf.net/p/makagiga/code/trunk/makagiga", i, "D:/log/makagiga");
			}*/
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
