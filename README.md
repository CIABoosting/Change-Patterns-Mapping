# Change-Patterns-Mapping
Code for TSE articles: **'Change-Patterns Mapping: A Boosting Way for Change Impact Analysis'**

**Author:** Yuan Huang, Jinyu Jiang, Xiapu Luo, Xiangping Chen, Member, IEEE, Zibin Zheng, Senior
Member, IEEE, Nan Jia, and Gang Huang Senior Member, IEEE,

If you try to do the same experiment of this paper, you should follow the following steps:

**STEP 1：** Prepare data ( dataset and testdata )

**STEP 2:** Generate **coreclass** of each commit

**STEP 3:** Generate **coupling relationship** of each commit

**STEP 4:** Generate **corpus** of each commit

**STEP 5:** On the testdata, use JRipples, Rose, Impactminer to generate the original result

**STEP 6:** Run Impactanalysis code to boost the result.

<br/>
# STEP 1
pass

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

[picture]

This file include orginal sorted(according to their probability of being impacted) predicted impacted classes.

<br/>
# STEP 6

