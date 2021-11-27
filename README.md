# AI_Algortihms
In this repository you will find implementation of two algoritms in AI field in Java.
Please make sure that you run this repo on a good input and stand for the requirments.

## Bayes Ball's algorithm
The algorithm checks whether two nodes are independent or not.
#### In case don't have evidence variables it's to easy to check if the nodes are independent or not.
### Steps:
* check if there is a path between two nodes.
* path can be represented by common cause connection or causal chain
* if there exits path between those two nodes then they aren't independent.
#### in case we have evidence variables
### Steps:
* Start with initial factors
** local CPTs instantiated by evidence
** If an instantiated CPT becomes one-valued, discard the factor
