#!/bin/sh

function oneGroupStageMatchesSpreadOut() {	
	year=$1

	if [ -d "data/uefa/${year}" ]; then
	 	echo "Found matches for ${year} so not downloading"
	else
		echo "Downloading matches for ${year}"
		mkdir -p data/uefa/${year}

		wget --quiet http://www.uefa.com/uefachampionsleague/season=${year}/matches/library/fixtures/day={-8,-7,13}/session=1/_matchesbydate.html 
		wget --quiet http://www.uefa.com/uefachampionsleague/season=${year}/matches/library/fixtures/day={-6,-5,-4,-3,-2,-1,1,2,3,4,5,6,9,10,11,12}/session={1,2}/_matchesbydate.html 
		wget --quiet http://www.uefa.com/uefachampionsleague/season=${year}/matches/library/fixtures/day={7,8}/session={1,2,3,4}/_matchesbydate.html 

		mv _matchesbydate.html _matchesbydate.html.0		
		mv _matchesbydate* data/uefa/${year}		
	fi	
}

function oneGroupStageMatchesBunched() {	
	year=$1

	if [ -d "data/uefa/${year}" ]; then
	 	echo "Found matches for ${year} so not downloading"
	else
		echo "Downloading matches for ${year}"
		mkdir -p data/uefa/${year}

		wget --quiet http://www.uefa.com/uefachampionsleague/season=${year}/matches/library/fixtures/day=13/session=1/_matchesbydate.html 
		wget --quiet http://www.uefa.com/uefachampionsleague/season=${year}/matches/library/fixtures/day={-6,-5,-4,-3,-2,-1,1,2,3,4,5,6,7,8,9,10,11,12}/session={1,2}/_matchesbydate.html 		

		mv _matchesbydate.html _matchesbydate.html.0		
		mv _matchesbydate* data/uefa/${year}		
	fi	
}

function oneGroupStageMatchesBunchedStretchedQualifiers() {	
	year=$1

	if [ -d "data/uefa/${year}" ]; then
	 	echo "Found matches for ${year} so not downloading"
	else
		echo "Downloading matches for ${year}"
		mkdir -p data/uefa/${year}

		wget --quiet http://www.uefa.com/uefachampionsleague/season=${year}/matches/library/fixtures/day={-12,-11,-10,-9,-8,-7,-6,-5,-4,-3,-2,-1,13}/session=1/_matchesbydate.html 
		wget --quiet http://www.uefa.com/uefachampionsleague/season=${year}/matches/library/fixtures/day={1,2,3,4,5,6,7,8,9,10,11,12}/session={1,2}/_matchesbydate.html 		

		mv _matchesbydate.html _matchesbydate.html.0		
		mv _matchesbydate* data/uefa/${year}		
	fi	
}

oneGroupStageMatchesBunchedStretchedQualifiers 2004
oneGroupStageMatchesBunchedStretchedQualifiers 2005
oneGroupStageMatchesBunchedStretchedQualifiers 2006
oneGroupStageMatchesBunched 2008
oneGroupStageMatchesBunched 2009
oneGroupStageMatchesSpreadOut 2010
oneGroupStageMatchesSpreadOut 2011
oneGroupStageMatchesSpreadOut 2012
oneGroupStageMatchesSpreadOut 2013

