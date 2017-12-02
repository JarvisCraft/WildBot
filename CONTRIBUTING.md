# Conventions
## Lombok
Lombok Project's unctionality should be used as often as possible in order to have all project in one code style and logic. Though it should not be used in case when a no-lombok plain-Java solution is better.
Some cases of this standard are:
### Final variables in methods
Lombok's `val` pseudo-annotation should be used to replace `final VariableType` when the variable'type can be taken from its initialization, e.g.:  
Bad  
`
final int myNum = 2 + 2;
`  
Good  
`
val myNum = 2 + 2;
`
