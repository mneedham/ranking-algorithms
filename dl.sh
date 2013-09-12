#!/bin/sh
rm -rf data/uefa
wget http://www.uefa.com/uefachampionsleague/season=2013/matches/library/fixtures/day={-8,-7,13}/session=1/_matchesbydate.html 
wget http://www.uefa.com/uefachampionsleague/season=2013/matches/library/fixtures/day={-6,-5,-4,-3,-2,-1,1,2,3,4,5,6,9,10,11,12}/session={1,2}/_matchesbydate.html 
wget http://www.uefa.com/uefachampionsleague/season=2013/matches/library/fixtures/day={7,8}/session={1,2,3,4}/_matchesbydate.html 
mv _matchesbydate.html _matchesbydate.html.0
mkdir data/uefa
mv _matchesbydate* data/uefa
