# Introduction
This is a project for generating the Coupling Relashionship of a commit.


# Preparation
You need to download projects with commits and organize the file structure as follows:

[picture here]

<br/>
The file name means the commit number of the project.

In each commit file, you should organize the file structure as follows:

[picture here]

<br/>
The folder named 'new' stores the new version of the modified files.

The folder named 'old' stores the old version of the modified files.

The file named 'commit.txt' stores the commit information of this commit.

An example is shown in the picture below, the example can be found on this github repository.
[Example here]


# Usage
The path for the main file (entry of the project) is 
> src/sysu/sei/reverse/Comparator/VersionComparator.java

You only need to modify the project path as follows:
[picture here]

After you've done, you can find a file called 'CouplingResult.txt' under each commit.
[picture here]

In this file, you can find the relashionship between two classes of this commit.

An example is shown in the picture below, the example can be found on this github repository.
[Example here]
