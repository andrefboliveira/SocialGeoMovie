var url_base = "http://localhost:8080/aw2017/rest";
var url_all_movies = 	url_base+"/movie";
var url_movie_details = url_base+"/movie/";
var url_geo_movie = 	"http://localhost:8080/aw2017/website/geo.json";

var url_admin_movie_trakt = url_base+"/db/movies/trakt";
var url_admin_movie_omdb = url_base+"/db/movies/omdb";
var url_admin_movie_process = url_base+"/db/movies/process";
var url_admin_cast = url_base+"/db/people/cast";
var url_admin_cast_process = url_base+"/db/people/process";
var url_admin_tweets = url_base+"/db/tweets";

var omdb_key = "55808bd4";

var map_markers = {};
var map;

var fetch_movies= function()
{
	$.getJSON( url_all_movies)
	.done(function( data ) 
	{
		$.each( data, function( i, item ) 
		{
			$([
				"<div class='movie-roll' title='"+item.title+"'>",
				"	<a href='movie-details.html?id="+item.uri+"'>",
				"		<img src='"+item.poster+"'>",
				"	</a>",
				"</div>"
			].join("\n")).appendTo( "#page_content" );
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
	var uri = $.urlParam("id");
	$.getJSON( url_movie_details+uri)
	.done(function( data ) 
	{
		$("#poster").attr("src",data.poster);
		$("#title").html(data.title);
		$("#tagline").html(data.tagline);
		$("#description").html(data.overview);
		$("#rating").html(data.imdb_rating);
		$("#runtime").html(data.runtime);
		$("#release").html(data.released);
		$("#certification").html(data.certification);
		$("#trailer").attr('src', "https://www.youtube.com/embed/"+data.trailer.substring(data.trailer.indexOf('?v=')+3))
		$("#trakt").attr('href', data.url_trakt);
		$("#imdb").attr('href', "http://www.imdb.com/title/"+data.id_imdb);
		$("#tmdb").attr('href', "https://www.themoviedb.org/movie/"+data.id_tmdb);
		
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
	
	var uri = $.urlParam("id");
	$.getJSON( url_movie_details+uri+"/people")
	.done(function( data ) 
	{
		var cast_div = $("#cast_list");
		for(var i=0; i<data.length; i++)
		{
			var d = data[i];
			cast_div.append("<a href='"+d.uri+"'>"+d.name+"</a><br>")
		}
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
		for(var i=0; i<data.length; i++)
		{
			var d = data[i];
			
			if(!map_markers[d.movie_name])
			{
				map_markers[d.movie_name] = [];
				$("#movie_select").append("<option val='"+d.movie_name+"'>"+d.movie_name+"</option>")
			}
		
			var marker = new google.maps.Marker(
			{
				position: {
					lat: parseFloat(d.lat),
					lng: parseFloat(d.lng)
				},
				map: map
			});
			
			map_markers[d.movie_name].push(marker);
		}
		console.log(data);
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
			$("#movie_import_start").toggleClass("loading_button");
			$.get(url_admin_movie_trakt, function(data, status)
			{
				var omdb  = document.getElementById("opt_omdb").checked;
				if(omdb == true)
				{
					$.get(url_admin_movie_omdb, function(data, status)
					{
						
						$.get(url_admin_movie_process, function(data, status)
						{
							$("#movie_import_start").toggleClass("loading_button");
						});
					});
				}
				else
				{
					$.get(url_admin_movie_process, function(data, status)
					{
						$("#movie_import_start").toggleClass("loading_button");
					});
				}	
			})
		}
	},
	cast: function()
	{
		$("#cast_import_start").toggleClass("loading_button");
		$.get(url_admin_cast, function(data, status)
		{
			$.get(url_admin_cast_process, function(data, status)
			{
				$("#cast_import_start").toggleClass("loading_button");
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

$.urlParam = function(name)
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