var url_base = "http://localhost:8080/aw2017/rest";
var url_all_movies = 	url_base+"/movie?include_poster=true";
var url_movie_details = url_base+"/movie/";
var url_geo_movie = 	"http://localhost:8080/aw2017/website/geo.json";
var url_movie_tweets = url_base+"/movie/[ID]/tweets";


var url_admin_movie_trakt = url_base+"/db/movies/trakt";
var url_admin_movie_omdb = url_base+"/db/movies/omdb";
var url_admin_movie_tmdb = url_base+"/db/movies/tmdb";
var url_admin_movie_dbpedia = url_base+"/db/movies/dbpedia";
var url_admin_movie_process = url_base+"/db/movies/process";
var url_admin_cast_trakt = url_base+"/db/people/cast/trakt";
var url_admin_cast_tmdb = url_base+"/db/people/cast/tmdb";
var url_admin_cast_process = url_base+"/db/people/process";
var url_admin_tweets = url_base+"/db/tweets";

var omdb_key = "55808bd4";

var map_markers = {};
var map;

var firebase_initialized = false;
var infowindow;

var urlParam = function(name)
{
    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
    if (results==null)
	{
       return null;
    }
    else
	{
       return results[1] || 0;
    }
}

var searchMovies = function(str) {
	var url_movie_search = url_base+"/movie/search/?compact=false&include_poster=true&value=";

	if (str.length == 0) {
    	$("#movie_list").empty();
		fetch_movies();
        return;
    } else {
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 200) {
            	var result = JSON.parse(this.responseText);
            	$("#movie_list").empty();
            	$.each( result, function( i, item ) 
            			{
            				$([
            					"<span prefix=\'dc: http:\/\/purl.org\/dc\/terms\/ og: http:\/\/ogp.me\/ns#\'><div property='dc:title' class='movie-roll' title='"+item.title+"'>",
            					"	<a href='movie-details.html?id="+item.uri+"'>",
            					"		<img property='og:image' src='"+item.poster+"'>",
            					"	</a>",
            					"</div></span>"
            				].join("\n")).appendTo( "#movie_list" );
            			});
            }
        };
        xmlhttp.open("GET", url_movie_search + str, true);
        xmlhttp.send();
    }
}

var add_search = function(){
	
	$("<div id=\"search_content\">	<form><input type=\"text\" name=\"search\" placeholder=\"Search movie by title...\" id=\"searchbox\" onkeyup=\"searchMovies(this.value)\"></form></div>")
	.prependTo( "#page_content" );
	
	/*
	 $("#searchbox").autocomplete({
		 	serviceUrl: url_movie_search,
		    onSelect: function (suggestion) {
		        alert('You selected: ' + suggestion.value + ', ' + suggestion.data);
		    }
	 });
	 */
	
	$("<div id=\"movie_list\"</div>")
	.appendTo( "#page_content" );
	
}

var fetch_movies = function()
{	
	$.getJSON( url_all_movies)
	.done(function( data ) 
	{
		$.each( data, function( i, item ) 
		{
			$([
				"<span prefix=\'dc: http:\/\/purl.org\/dc\/terms\/ og: http:\/\/ogp.me\/ns#\'><div property='dc:title' class='movie-roll' title='"+item.title+"'>",
				"	<a href='movie-details.html?id="+item.uri+"'>",
				"		<img property='og:image' src='"+item.poster+"'>",
				"	</a>",
				"</div></span>"
			].join("\n")).appendTo( "#movie_list" );
		});
	});

	/*$.get( url_all_movies, function( data )
	{
		var movie_container = $("#page_content");
		for(var i=0; i<data.length; i++)
		{
			
			
			
			
			
			var trakt_id = data[i].id + "";
			trakt_id = "0".repeat(9-trakt_id.length)+trakt_id;
			var poster_id=trakt_id.slice(0, 3) + "/" + trakt_id.slice(3,6)+"/" +trakt_id.slice(6);
			
			var html = "<div class='movie-roll'><img src='https://walter.trakt.tv/images/movies/"+poster_id+"/posters/thumb/9a8494f868.jpg'></div>"
			
		}
		
		$( ".result" ).html( data );
		alert( "Load was performed." );
	});*/
}

var load_movie_details = function()
{
	var uri = urlParam("id");
	$.getJSON( url_movie_details+uri)
	.done(function( data ) 
	{
		$("#poster").attr("src",data.poster);
		$("#poster").attr("title",data.title);
		$("#title").html(data.title);
		$("#tagline").html(data.tagline);
		$("#description").html(data.overview);
		$("#rating").html(data.imdb_rating);
		$("#runtime").html(data.runtime);
		$("#release").html(data.released);
		$("#certification").html(data.certification);
		$("#trailer").attr('src', "https://www.youtube.com/embed/"+data.trailer.substring(data.trailer.indexOf('?v=')+3))
		$("#trakt").attr('href', data.url_trakt);
		$("#imdb").attr('href', data.url_imdb);
		$("#tmdb").attr('href', data.url_tmdb);
		$("#wiki").attr('href', data.url_wikipedia);
		
		var chart = c3.generate(
		{
			bindto: '#rating',
			data: 
			{
				columns: 
				[
					['data1', data.imdb_rating],
					['data2', 10 - data.imdb_rating]
				],
				type : 'donut',
				labels: false,
				colors:
				{
					data1: '#1f77b4',
					data2: '#cccccc'
				}
			},
			donut: {
				title: data.imdb_rating.toFixed(2),
				label: 
				{
					show: false,
					threshold: 0
				},
				expand: false,
				width: 10
			},
			legend:
			{
				show: false
			},
			interaction: 
			{
				enabled: false
			},
			size: 
			{
				width: 80,
				height: 80
			},
		});
		
		
	});
	
	var url = url_movie_tweets;
	url = url.replace("[ID]",uri);
	$.getJSON( url)
	.done(function( data ) 
	{
		var tweet_container = $("#tweet_list");
		for(var i=0; i<data.length;i++)
		{
			var tweet = data[i];
			tweet_container.append(
			"<a resource=\'http:\/\/dbpedia.org\/resource\/Twitter\' href='"+tweet.url+"'>"+
			"	<div prefix=\'dc: http:\/\/purl.org\/dc\/terms\/\' class='movie-roll' style='margin: 10px 0; padding: 5px 30px;'>"+
			"		<h4 property='dc:title'>"+tweet.user+" says:</h4>"+
			"		<div property='dc:description' style='margin-bottom: 10px;' >"+tweet.text+"</div>"+
			"		<div style='display:inline-block; margin-right:10px'>retweets: "+tweet.retweet_count+"</div><div style='display:inline-block;'>"+tweet.date+"</div>"+
			"	</div>"+
			"</a>"
			);
		}
		console.log(data);
	});
	
	var uri = urlParam("id");
	$.getJSON( url_movie_details+uri+"/person/main?include_profile_image=true")
	.done(function( data ) 
	{
		var cast_div = $("#cast_list");
		for(var i=0; i<data.length; i++)
		{
			var d = data[i];
			cast_div.prepend(
			"<a resource=\'http:\/\/dbpedia.org\/resource\/Imdb\' href='"+d.url_trakt+"' style='text-decoration: none;'>"+
			"	<div prefix=\'dc: http:\/\/purl.org\/dc\/terms\/ og: http:\/\/ogp.me\/ns# foaf:http:\/\/xmlns.com\/foaf\/0.1\/\' typeof=\'Person\' class='cast'>"+
			"		<img property='og:image' src='"+((d.profile_image && d.profile_image.indexOf("null") < 0)?d.profile_image:'style/images/people-placeholder.png')+"' title='"+d.name+" as "+d.character+"' style='width: 70px; height: 105px'>"+
			"	</div>"+
			"</a>"
			);
		}
	});
	
	initialize_firebase();
	var comments_ref = firebase.database().ref('comments/' + uri);
	comments_ref.on('child_added', function(snapshot) 
	{
		var comment = snapshot.val();
		
		$("#comment_list").prepend(
		"<div prefix=\'dc: http:\/\/purl.org\/dc\/terms\/\' class='movie-roll movie-comment' style='margin: 10px 0;'>"+
		"	<div property='dc:creator' style='margin-bottom: 10px;'>"+comment.user+" says:</div>"+
		"	<div property='dc:description'>"+comment.comment+"</div>"+
		"</div>"
		);
	});
}

var init_map = function()
{
	var center = {lat: 38.769281, lng: -9.296047};
	map = new google.maps.Map(document.getElementById('map'), 
	{
		zoom: 2,
		center: center
	});
	infowindow = new google.maps.InfoWindow();
	
	// Try HTML5 geolocation.
	if (navigator.geolocation)
	{
		navigator.geolocation.getCurrentPosition(function(position) 
		{
			var pos = 
			{
				lat: position.coords.latitude,
				lng: position.coords.longitude
			};
			map.setCenter(pos);
		}, 
		function() 
		{
			//handleLocationError(true, infoWindow, map.getCenter());
		});
	} 
	else 
	{
		// Browser doesn't support Geolocation
		//handleLocationError(false, infoWindow, map.getCenter());
	}
	
	
	
	$.getJSON(url_geo_movie)
	.done(function( data ) 
	{
		var colors = {};
		
		for(var i=0; i<data.length; i++)
		{
			var d = data[i];
			
			if(!map_markers[d.movie_name])
			{
				map_markers[d.movie_name] = [];
				$("#movie_select").append("<option val='"+d.movie_name+"'>"+d.movie_name+"</option>")
			}
			
			if(!colors[d.movie_name])
				colors[d.movie_name] = '#'+Math.floor(Math.random() * 16777216).toString(16);
	
			var marker = new google.maps.Marker(
			{
				position: {
					lat: parseFloat(d.lat),
					lng: parseFloat(d.lng)
				},
				map: map,
				icon: 
				{
					path: "M27.648 -41.399q0 -3.816 -2.7 -6.516t-6.516 -2.7 -6.516 2.7 -2.7 6.516 2.7 6.516 6.516 2.7 6.516 -2.7 2.7 -6.516zm9.216 0q0 3.924 -1.188 6.444l-13.104 27.864q-0.576 1.188 -1.71 1.872t-2.43 0.684 -2.43 -0.684 -1.674 -1.872l-13.14 -27.864q-1.188 -2.52 -1.188 -6.444 0 -7.632 5.4 -13.032t13.032 -5.4 13.032 5.4 5.4 13.032z",
					scale: 0.6,
					strokeWeight: 1,
					strokeColor: 'black',
					strokeOpacity: 1,
					fillColor: colors[d.movie_name],
					fillOpacity: 1,
				},
				location_name: d.entity_value,
				movie: d.movie_name
			});
			
			google.maps.event.addListener(marker, 'click', function()
			{
				infowindow.setContent("<span prefix=\'dc: http:\/\/purl.org\/dc\/terms\/ vc: http:\/\/www.w3.org\/2006\/vcard\/ns#\' typeof=\'vcard:VCard\'><b property='vc:location'>location:</b></span> "+this.location_name+"<br><b property='dc:title'>Movie:</b> "+this.movie)
				infowindow.open(map, this);
			});

			map_markers[d.movie_name].push(marker);
		}
		
		var columns = [];
		
		for (var key in map_markers) 
		{
			if (map_markers.hasOwnProperty(key)) 
				columns.push([key, map_markers[key].length]);
		}
		
		var chart = c3.generate(
		{
			bindto: '#reference_chart',
			data: 
			{
				// iris data from R
				columns: columns,
				type : 'bar',
				onclick: function (d, i) { console.log("onclick", d, i); },
				onmouseover: function (d, i) { console.log("onmouseover", d, i); },
				onmouseout: function (d, i) { console.log("onmouseout", d, i); },
				colors: colors
			}
		});
	});
	/*
	var marker = new google.maps.Marker(
	{
		position: center,
		map: map
	});*/
	
	
}

var change_map_markers = function()
{
	var selected = $("#movie_select").val();
	
	$.each( map_markers, function(value, x)
	{
		if(value == $("#movie_select").val() || $("#movie_select").val() == 'All')
			for(var i=0; i<map_markers[value].length; i++)
				map_markers[value][i].setMap(map);
		else
			for(var i=0; i<map_markers[value].length; i++)
				map_markers[value][i].setMap(null);
	})
}

var admin_import = 
{
	movies: function()
	{
		var trakt = document.getElementById("opt_trakt").checked;
		
		if(trakt == true)
		{
			/*$("#movie_import_start").toggleClass("loading_button");
			$.when($.get(url_admin_movie_trakt))
			.then($.get(url_admin_movie_omdb))
			.then($.get(url_admin_movie_tmdb))
			.then($.get(url_admin_movie_process))
			.then($("#movie_import_start").toggleClass("loading_button"));
			*/
			
			
			$("#movie_import_start").toggleClass("loading_button");
			$.get(url_admin_movie_trakt, function(data, status)
			{
				var omdb  = document.getElementById("opt_omdb").checked;
				if(omdb == true)
				{
					$.get(url_admin_movie_omdb, function(data, status)
					{
						$.get(url_admin_movie_tmdb, function(data, status)
						{
							$.get(url_admin_movie_dbpedia, function(data, status)
							{
								$.get(url_admin_movie_process, function(data, status)
								{
									$("#movie_import_start").toggleClass("loading_button");
								});
							});
						});
					});
				}
				else
				{
					$.get(url_admin_movie_tmdb, function(data, status)
					{
						$.get(url_admin_movie_dbpedia, function(data, status)
						{
							$.get(url_admin_movie_process, function(data, status)
							{
								$("#movie_import_start").toggleClass("loading_button");
							});
						});
					});
				}	
			})
		}
	},
	cast: function()
	{
		$("#cast_import_start").toggleClass("loading_button");
		$.get(url_admin_cast_trakt, function(data, status)
		{
			$.get(url_admin_cast_tmdb, function(data, status)
			{
				$.get(url_admin_cast_process, function(data, status)
				{
					$("#cast_import_start").toggleClass("loading_button");
				});
			});
		});
	},
	tweet: function()
	{
		$("#tweet_import_start").toggleClass("loading_button");
		$.get(url_admin_tweets, function(data, status)
		{
			$("#tweet_import_start").toggleClass("loading_button");
		});
	}
}

var submit_comment = function()
{
	var user = $("#comment_user").val();
	var comment = $("#comment_text").val();
	var movie = urlParam("id");
	
	if(user == "" || comment == "")
		return;
	
	var comment_ref = firebase.database().ref('comments/' + movie).push();
	comment_ref.set(
	{
		user: user,
		comment: comment,
	});

	//$("#comment_user").val("");
	$("#comment_text").val("");
}

var initialize_firebase = function()
{
	if(firebase_initialized == false)
	{
		var config =
		{
			apiKey: "AIzaSyA6pxczjF5BI8PVHKTihgb3_C8NdwSgMK4",
			authDomain: "aw20162017-c1610.firebaseapp.com",
			databaseURL: "https://aw20162017-c1610.firebaseio.com",
			projectId: "aw20162017-c1610",
			storageBucket: "aw20162017-c1610.appspot.com",
			messagingSenderId: "348514365475"
		};
		firebase.initializeApp(config);
		firebase_initialized = true;
	}
}

$( document ).ready(function() 
{
	initialize_firebase();
});