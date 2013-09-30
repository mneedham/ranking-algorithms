# ranking-algorithms

A clojure library for ranking Champions League football teams based on matches against each other

## Usage
````clojure
user> (def the-matches (ranking-algorithms.uefa/read-from-file "data/cl-matches-2004-2012.json"))#'user/the-matches        
user> (ranking-algorithms.core/print-top-teams-without-round 10 the-matches)
==========================================================
:rank | :team          | :ranking | :wins | :draw | :loses
==========================================================
1     | Barcelona      | 1383.85  | 55    | 25    | 12    
2     | Man. United    | 1343.54  | 49    | 21    | 14    
3     | Chelsea        | 1322.0   | 44    | 27    | 17    
4     | Real Madrid    | 1317.68  | 42    | 14    | 18    
5     | Bayern         | 1306.18  | 42    | 13    | 19    
6     | Arsenal        | 1276.83  | 47    | 21    | 18    
7     | Liverpool      | 1272.52  | 41    | 17    | 17    
8     | Internazionale | 1260.27  | 36    | 18    | 21    
9     | Milan          | 1257.63  | 34    | 22    | 18    
10    | Bordeaux       | 1243.04  | 12    | 3     | 7     
==========================================================
````clojure

## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
