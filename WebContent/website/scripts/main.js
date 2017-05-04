var url_all_movies = "http://localhost:8080/aw2017/rest/movie";
var url_movie_details = "http://localhost:8080/aw2017/rest/movie/";
var omdb_key = "55808bd4";

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
		$("#rating").html(data.rating);
		$("#runtime").html(data.runtime);
		$("#release").html(data.released);
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
	var map = new google.maps.Map(document.getElementById('map'), 
	{
		zoom: 12,
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
	
	
	var marker = new google.maps.Marker(
	{
		position: center,
		map: map
	});
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