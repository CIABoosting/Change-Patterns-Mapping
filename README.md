# Change-Patterns-Mapping
Code for TSE articles: **'Change-Patterns Mapping: A Boosting Way for Change Impact Analysis'**

**Author:** Yuan Huang, Jinyu Jiang, Xiapu Luo, Xiangping Chen, Member, IEEE, Zibin Zheng, Senior
Member, IEEE, Nan Jia, and Gang Huang Senior Member, IEEE,


# Overview 
If you try to do the same experiment of this paper, you should follow the following steps:

**STEP 1：** Prepare data ( dataset and testdata )

**STEP 2:** Generate **coreclass** of each commit

**STEP 3:** Generate **coupling relationship** of each commit

**STEP 4:** Generate **corpus** of each commit

**STEP 5:** On the testdata, use JRipples, Rose, Impactminer to generate the original result

**STEP 6:** Run Impactanalysis code to boost the result.

<br/>

# STEP 1
Sample dataset can be find at `./dataset`, these files are used to store change-patterns which may later be used to boost the results.

Sample testdata can be find at `./testdata`, these files are used to do experiments.

**Notice: You'd better use the same file structure to store data if you want to do this experiment with ease.**


<br/>

# STEP 2
See `./coreclass_generation_project` for details

<br/>

# STEP 3
See `./CouplingRel_generation_project` for details

<br/>

# STEP 4
See `./Corpus_generation_project` for details


<br/>

# STEP 5
The output of these tools should organize as follow: 

![avatar](/image/original_result.png)

This file include orginal sorted(according to their probability of being impacted) predicted impacted classes.

<br/>

# STEP 6
See `./ImpactAnalysis_project` for details 