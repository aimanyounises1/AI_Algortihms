# AI_Algortihms
In this repository you will find implementation of two algoritms in AI field in Java.
Please make sure that you run this repo on a good input and stand for the requirments.

## Bayes Ball's algorithm
The algorithm checks whether two nodes are independent or not.
#### In case don't have evidence variables it's to easy to check if the nodes are independent or not.
### Steps:
* check if there is a path between two nodes.
* path can be represented by common cause connection or causal chain
* if there exits path between those two nodes then they are no possible independent.
#### in case we have evidence variables
<img src="https://github.com/aimanyounises1/AI_Algortihms/blob/master/Bayes_Ball.png" width="400" height="400">
![](https://github.com/aimanyounises1/AI_Algortihms/blob/master/Bayes_Ball.png)
### Steps:
1. Start with initial factors
2. local CPTs instantiated by evidence.
3. If an instantiated CPT becomes one-valued, discard the factor.
* While there are still hidden variables (not Q or evidence):
  * Pick a hidden variable H
  * Join all factors mentioning H
  *  Eliminate (sum out) H
  *   If the factor becomes one-valued, discard the factor
* Join all remaining factors and normalize
## E.g
<img src="https://github.com/aimanyounises1/AI_Algortihms/blob/master/Variable_Elimination.png" width="400" height="400">
![](https://github.com/aimanyounises1/AI_Algortihms/blob/master/Variable_Elimination.png)
