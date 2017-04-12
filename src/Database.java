import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Database
{
	
	static final String DRIVER = "com.mysql.jdbc.Driver";  
	static final String LOCATION = "jdbc:mysql://localhost:3306/movie_recommender?useSSL=false"; //YOUR DATABASE NAME
	static final String USERNAME = "root";
	static final String PASSWORD = "windowlicker"; //SET AS YOUR PASSWORD
	   
	Statement database = null;
	Connection connection = null;
	   
	public Database()
	{	
		try{
			//Registering the JDBC DRIVER
			Class.forName(DRIVER);
			
			//Opening the connection to the database
			System.out.print("Connecting to database...");
			connection = DriverManager.getConnection(LOCATION, USERNAME, PASSWORD);
			System.out.println("[OK]");
			
			//Initializing the database
			System.out.print("Initalizing...");
			database = connection.createStatement();
			System.out.println("[OK]");
			
		} catch (Exception e)
		{
			System.out.println("[FAIL] " + e.toString());
		}
	}
	
	public void createTables()
	{
		createMoviesTable();
		createGenresTable();
		createDirectorsTable();
		createActorsTable();
		createCountriesTable();
		createLocationsTable();
		createTagsTable();
		createMovieTagsTable();
		createUserTaggedTable();
		createTaggedMoviesTable();
		createRatedMoviesTable();
	}
	
	public void populateTables()
	{
		//Need to fix a few errors on the commented tables
		
		loadData("movies");
		loadData("movie_genres");
		loadData("movie_directors");
		loadData("movie_actors");
		//loadData("movie_countries");
		//loadData("movie_locations");
		loadData("movie_tags");
		//loadData("user_taggedmovies_timestamps");
		//loadData("user_taggedmovies");
		loadData("user_ratedmovies");
	}
	
	private void loadData(String table)
	{
		System.out.print("Populating " + table + " table");
		try
		{
			BufferedReader br;
			br = new BufferedReader(new FileReader("Rotten Tomatos Dataset/" + table + ".dat"));
			String line;
			
			String sqlEmptyTable = "DELETE FROM " + table + ";";
			database.executeUpdate(sqlEmptyTable);
			
			int loadBar = 0;
			
			br.readLine();
			
			while ((line = br.readLine()) != null)
			{
				String [] dataChunk = line.replace("\'", "\\'").split("\t");
				StringBuilder sqlInsert = new StringBuilder();
				
				sqlInsert.append("INSERT INTO " + table + " VALUES (");
				
				for(int i = 0; i < dataChunk.length; ++i)
				{
					// Special case
					if(dataChunk[i].equals("\\N")) dataChunk[i] = "0";
					
					sqlInsert.append("'" + dataChunk[i] + "'");
					
					if(i == dataChunk.length - 1) sqlInsert.append(");");
					else sqlInsert.append(",");
				}
				database.executeUpdate(sqlInsert.toString());
				if(loadBar++ % 4000 == 0) System.out.print(".");
			}
			br.close();
			System.out.println("[OK]");
		} catch (Exception e)
		{
			System.out.println("[FAIL] " + e.toString());
		}
	}
	
	private void createMoviesTable()
	{
		try {
			String sqlDropMovies = "DROP TABLE IF EXISTS movies;";
			database.executeUpdate(sqlDropMovies);
			
			String sqlCreateMovies = "CREATE TABLE movies ("
	    		+"id							int	not null,"
	    		+"title							varchar(255),"
	    		+"imdbID						int,"
	    		+"spanishTitle					varchar(255),"
	    		+"imdbPictureURL				varchar(255),"
	    		+"year							int,"
	    		+"rtID							varchar(255),"
	    		+"rtAllCriticsRating			double,"
	    		+"rtAllCriticsNumReviews		int,"
	    		+"rtAllCriticsNumFresh			int,"
	    		+"rtAllCriticsNumRotten			int,"
	    		+"rtAllCriticsScore				int,"
	    		+"rtTopCriticsRating			double,"
	    		+"rtTopCriticsNumReviews		int,"
	    		+"rtTopCriticsNumFresh			int,"
	    		+"rtTopCriticsNumRotten			int,"
	    		+"rtTopCriticsScore				int,"
	    		+"rtAudienceRating				double,"
	    		+"rtAudienceNumRatings			int,"
	    		+"rtAudienceScore				double,"
	    		+"rtPictureURL 					varchar(255),"
	    		+"primary key (id));";
			
			System.out.print("Creating table movies...");
		    database.executeUpdate(sqlCreateMovies);
		    System.out.println("[OK]");
	      
		} catch (Exception e)
		{
			System.out.println("[FAIL] " + e.toString());
		}
	}
	
	private void createGenresTable()
	{
		try {
			String sqlDropGenres = "DROP TABLE IF EXISTS movie_genres;";
			database.executeUpdate(sqlDropGenres);
			
			String sqlCreateGenres = "CREATE TABLE movie_genres ("
    		  	+"movieID			int	not null,"
    		  	+"genre 			varchar(255),"
    		  	+"primary key (movieID, genre));";
			
			System.out.print("Creating table movie_genres...");
		    database.executeUpdate(sqlCreateGenres);
		    System.out.println("[OK]");
	      
		} catch (Exception e)
		{
			System.out.println("[FAIL] " + e.toString());
		}
	}
	
	private void createDirectorsTable()
	{
		try {
			String sqlDropDirectors = "DROP TABLE IF EXISTS movie_directors;";
			database.executeUpdate(sqlDropDirectors);
			
			String sqlCreateDirectors = "CREATE TABLE movie_directors ("
    		  	+"movieID				int	not null,"
    		  	+"directorID			varchar(255),"
    		  	+"directorName			varchar(255),"
    		  	+"primary key (directorID, movieID));";
			
			System.out.print("Creating table movie_directors...");
		    database.executeUpdate(sqlCreateDirectors);
		    System.out.println("[OK]");
		    
		} catch (Exception e)
		{
			System.out.println("[FAIL] " + e.toString());
		}
	}
	
	private void createActorsTable()
	{
		try {
			String sqlDropActors = "DROP TABLE IF EXISTS movie_actors;";
			database.executeUpdate(sqlDropActors);
			
			String sqlCreateActors = "CREATE TABLE movie_actors ("
    		  	+"movieID						int not null,"
    		  	+"actorID 						varchar(255),"
    		  	+"actorName						varchar(255),"
    		  	+"ranking						int,"
    		  	+"primary key (actorID, movieID));";
			
			System.out.print("Creating table movie_actors...");
		    database.executeUpdate(sqlCreateActors);
		    System.out.println("[OK]");
		    
		} catch (Exception e)
		{
			System.out.println("[FAIL] " + e.toString());
		}
	}
	
	private void createCountriesTable()
	{
		try {
			String sqlDropCountries = "DROP TABLE IF EXISTS movie_countries;";
			database.executeUpdate(sqlDropCountries);
			
			String sqlCreateCountries = "CREATE TABLE movie_countries ("
    			+"movieID					int	not null,"
    		  	+"country 					varchar(255),"
    		  	+"primary key (movieID));";
			
			System.out.print("Creating table movie_countries...");
		    database.executeUpdate(sqlCreateCountries);
		    System.out.println("[OK]");
		    
		} catch (Exception e)
		{
			System.out.println("[FAIL] " + e.toString());
		}
	}
	
	private void createLocationsTable()
	{
		try {
			String sqlDropLocations = "DROP TABLE IF EXISTS movie_locations;";
			database.executeUpdate(sqlDropLocations);
			
			String sqlCreateLocations = "CREATE TABLE movie_locations ("
    		  	+"movieID						int	not null,"
    		  	+"location1 					varchar(255),"
    		  	+"location2						varchar(255),"
    		  	+"location3						varchar(255),"
    		  	+"location4						varchar(255),"
    		  	+"primary key (movieID, location1, location2, location3, location4));";
			
			System.out.print("Creating table movie_locations...");
		    database.executeUpdate(sqlCreateLocations);
		    System.out.println("[OK]");
		    
		} catch (Exception e)
		{
			System.out.println("[FAIL] " + e.toString());
		}
	}
	
	private void createTagsTable()
	{
		try {
			String sqlDropTags = "DROP TABLE IF EXISTS tags;";
			database.executeUpdate(sqlDropTags);
			
			String sqlCreateTags = "CREATE TABLE tags ("
    		  		+"id					int	not null,"
    		  		+"value 				varchar(255),"
    		  		+"primary key (id));";
			
			System.out.print("Creating table tags...");
		    database.executeUpdate(sqlCreateTags);
		    System.out.println("[OK]");
		    
		} catch (Exception e)
		{
			System.out.println("[FAIL] " + e.toString());
		}
	}
	
	private void createMovieTagsTable()
	{
		try {
			String sqlDropMovieTags = "DROP TABLE IF EXISTS movie_tags;";
			database.executeUpdate(sqlDropMovieTags);
			
			String sqlCreateMovieTags = "CREATE TABLE movie_tags ("
    		  		+"movieID				int	not null,"
    		  		+"tagID 				int,"
    		  		+"tagWeight				int,"
    		  		+"primary key (movieID, tagID));";
			
			System.out.print("Creating table movie_tags...");
		    database.executeUpdate(sqlCreateMovieTags);
		    System.out.println("[OK]");
		    
		} catch (Exception e)
		{
			System.out.println("[FAIL] " + e.toString());
		}
	}
	
	private void createUserTaggedTable()
	{
		try {
			String sqlDropUserTagged = "DROP TABLE IF EXISTS user_taggedmovies_timestamps;";
			database.executeUpdate(sqlDropUserTagged);
			
			String sqlCreateTaggedTable = "CREATE TABLE user_taggedmovies_timestamps ("
    		  		+"userID					int	not null,"
    		  		+"movieID   				int,"
    		  		+"tagID  					int,"
    		  		+"timestamp					long,"
    		  		+"primary key (userID, movieID, tagID));";
			
			System.out.print("Creating table user_taggedmovies_timestamps...");
		    database.executeUpdate(sqlCreateTaggedTable);
		    System.out.println("[OK]");
		    
		} catch (Exception e)
		{
			System.out.println("[FAIL] " + e.toString());
		}
	}
	
	private void createTaggedMoviesTable()
	{
		try {
			String sqlDropUserTaggedMovies = "DROP TABLE IF EXISTS user_taggedmovies;";
			database.executeUpdate(sqlDropUserTaggedMovies);
			
			String sqlTaggedMoviesTable = "CREATE TABLE user_taggedmovies ("
    		  		+"userID					int	not null,"
    		  		+"movieID					int,"
    		  		+"tagID						int,"
    		  		+"date_day					int,"
    		  		+"date_month				int,"
    		  		+"date_year					int,"
    		  		+"date_hour					int,"
    		  		+"date_minute				int,"
    		  		+"date_second				int,"
    		  		+"primary key (userID, movieID, tagID));";
			
			System.out.print("Creating table user_taggedmovies...");
		    database.executeUpdate(sqlTaggedMoviesTable);
		    System.out.println("[OK]");
		    
		} catch (Exception e)
		{
			System.out.println("[FAIL] " + e.toString());
		}
	}
	
	private void createRatedMoviesTable()
	{
		try {
			String sqlDropRatedMovies = "DROP TABLE IF EXISTS user_ratedmovies;";
			database.executeUpdate(sqlDropRatedMovies);
			
			String sqlRatedMovies = "CREATE TABLE user_ratedmovies ("
	  		  		+"userID					int	not null,"
	  		  		+"movieID					int,"
	  		  		+"rating					double,"
	  		  		+"date_day					int,"
	  		  		+"date_month				int,"
	  		  		+"date_year					int,"
	  		  		+"date_hour					int,"
	  		  		+"date_minute				int,"
	  		  		+"date_second				int,"
	  		  		+"primary key (userID, movieID));";
			
			System.out.print("Creating table user_ratedmovies movies...");
		    database.executeUpdate(sqlRatedMovies);
		    System.out.println("[OK]");
		    
		} catch (Exception e)
		{
			System.out.println("[FAIL] " + e.toString());
		}
	}

}
