# Introduction
This is a project for generating the corpus of a commit.

# Notice
This project must be executed after the coreclass generation.

# Preparation
You need to download projects with commits and organize the file structure as follows:

![image](https://github.com/CIABoosting/Change-Patterns-Mapping/blob/master/image/file_structure.png)

<br/>
The file name means the commit number of the project.

In each commit file, you should organize the file structure as follows:

![image](https://github.com/CIABoosting/Change-Patterns-Mapping/blob/master/image/commit_structure.png)


<br/>
The folder named 'new' stores the new version of the modified files.

The folder named 'old' stores the old version of the modified files.




**\*Of course, the file structure is not a big thing. You can organize it as you wish, if you're willing to modify the code for yourself.**


# Usage
The path for the main file (entry of the project) is 
> src/sysu/tools/WordSpliter.java

You only need to modify the project path as follows(approximately line 428):

![image](https://github.com/CIABoosting/Change-Patterns-Mapping/blob/master/image/Corpus_change_path.png)

After you've done, you can find a file called 'corpus' each commits.

![image](https://github.com/CIABoosting/Change-Patterns-Mapping/blob/master/image/corpus_result.png)


In this file, you can find 'newcode.txt', 'oldcode.txt', which we use to compare the similarity between two coreclasses.

The example can be found on this github repository under the directory called `dataset`.
