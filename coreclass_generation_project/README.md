# Introduction
This is a project for creating the coreclass of a commit.

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

The file named 'commit.txt' stores the commit information of this commit.


**\*Of course, the file structure is not a big thing. You can organize it as you wish, if you're willing to modify the code for yourself.**


# Usage
The path for the main file (entry of the project) is 
> src/main/java/sysu/coreclass/CoreClass.java

You only need to modify the project path as follows(approximately line 1034):
![image](https://github.com/CIABoosting/Change-Patterns-Mapping/blob/master/image/coreclass_change_path.png)

After you've done, you can find two files under each commits.
![image](https://github.com/CIABoosting/Change-Patterns-Mapping/blob/master/image/coreclass_result.png)

**coreclass.txt:** store the probability of being coreclass of a java file.

**CoreClassName.txt:** store the result after sorting files from large to small according to the probability of 
becoming a key class.

The example can be found on this github repository under the directory called `dataset`.