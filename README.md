# SocialGeoMovie

Group project titled "SocialGeoMovie" for the course "Aplicações na Web" (Web Applications) for the school year 2016/2017 of the Master's degree in Informatics (Mestrado em Informática) at FCUL/ULisboa

## Project Stack
Web services (REST), Java, HTML, CSS, JavaScript, d3.js, Neo4J, SPARQL, RDFα

## Details
Group project

Development of web app with Java back-end and Neo4J storage which displays  movies details that were consumed via public REST APIs.

Java backend extracts movie data available on the web from IMDb-like websites and DBpedia and stores it on a NoSQL graph database (Neo4J).

Published web services gave access to the data stored in Neo4J,

The front-end consumes the published backend web services to display a rich user experience using front-end in web native technologies (HTML, CSS, JavaScript, d3.js) enriched with linked data (RDF).
User can interact with the movie data is different ways
- view a list of tops movies from all time and get more information about each movie
- see what people are saying about that specific movie on twitter
- view world map of geographical places mentioned in each movie

The goal of the project was the consumption and implementation of (REST) web services and Linked Data

For more information, please check the [attached reports](Reports)

## Contributors
- Alexander José Pereira Fernandes
- André Filipe Bernardes Oliveira
- Ricardo Ferreira Vallejo
- Tânia Sofia Guerreiro Maldonado

My personal contributions (André) to the group project were mainly related with back-end:
- retrieval of the data from the sources
- processing of the received data
- storage in the NoSQL graph database
- publishing of web services to expose the data to the front end

## Note
This is a mirror of the SVN repository (RiouxSVN) used for the development of this application.
