import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class Database
{
	
	static final String DRIVER = "com.mysql.jdbc.Driver";
	static final String DATABASE = "movie_recommender"; //SET AS YOUR DATABASE
	static final String USERNAME = "root"; //SET AS YOUR USERNAME
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
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + DATABASE + "?useSSL=false", USERNAME, PASSWORD);
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
	
	public void query(String sqlQuery)
	{
		try
		{
			ResultSet result = database.executeQuery(sqlQuery);
			ResultSetMetaData metaData = result.getMetaData();
			
			int cols = metaData.getColumnCount();
			
			while (result.next())
			{
				for(int i = 1; i <= cols; ++i)
				{
					System.out.print(result.getString(i));
					if(i != cols) System.out.print("\t ");
				}
				System.out.println();
			}
		} catch (SQLException e)
		{
			System.out.println("[ERROR] " + e.toString());
			e.printStackTrace();
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
		createUserRatedTable();
		createTaggedMoviesTable();
		createRatedMoviesTable();
	}
	
	public void migrateTables()
	{
		loadData("movies",21);
		loadData("movie_genres",2);
		loadData("movie_directors",3);
		loadData("movie_actors",4);
		loadData("movie_countries",2);
		loadData("movie_locations",5);
		loadData("movie_tags",3);
		loadData("tags",2);
		loadData("user_ratedmovies_timestamps",4);
		loadData("user_taggedmovies_timestamps",4);
		loadData("user_taggedmovies",9);
		loadData("user_ratedmovies",9);
	}
	
	private void loadData(String table, int cols)
	{
		System.out.print("Migrating " + table + " table");
		try
		{	
			BufferedReader br;
			br = new BufferedReader(new FileReader("Rotten Tomatos Dataset/" + table + ".dat"));
			br.readLine();
			
			String line;
			int loadBar = 0;
			
			String sqlEmptyTable = "DELETE FROM " + table + ";";
			database.executeUpdate(sqlEmptyTable);
			
			while ((line = br.readLine()) != null)
			{
				String[] dataChunk = line.replace("\'", "\\'").split("\t");
				StringBuilder sqlInsert = new StringBuilder();
				
				if(dataChunk.length < cols) continue;
				
				sqlInsert.append("INSERT INTO " + table + " VALUES (");
				
				for(int i = 0; i < cols; ++i)
				{
					sqlInsert.append("'");
					if(i < dataChunk.length)
					{
						if(dataChunk[i].equals("\\N")) dataChunk[i] = "0";
						sqlInsert.append(dataChunk[i]);
					}
					if(i == cols -1) sqlInsert.append("');");
					else sqlInsert.append("',");
				}
				
				database.executeUpdate(sqlInsert.toString());
				if(loadBar++ % 4000 == 0) System.out.print(".");
			}
			br.close();
			System.out.println("[OK]");
		} catch (Exception e)
		{
			e.printStackTrace();
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
			
			String sqlCreateTagged = "CREATE TABLE user_taggedmovies_timestamps ("
    		  		+"userID					int	not null,"
    		  		+"movieID   				int,"
    		  		+"tagID  					int,"
    		  		+"timestamp					long,"
    		  		+"primary key (userID, movieID, tagID));";
			
			System.out.print("Creating table user_taggedmovies_timestamps...");
		    database.executeUpdate(sqlCreateTagged);
		    System.out.println("[OK]");
		    
		} catch (Exception e)
		{
			System.out.println("[FAIL] " + e.toString());
		}
	}
	
	private void createUserRatedTable()
	{
		try {
			String sqlDropUserRated = "DROP TABLE IF EXISTS user_ratedmovies_timestamps;";
			database.executeUpdate(sqlDropUserRated);
			
			String sqlCreateUserRatedTable = "create table user_ratedmovies_timestamps ("
	  		  		+"userID						int	not null,"
	  		  		+"movieID   					int,"
	  		  		+"rating  						double,"
	  		  		+"timestamp						long,"
	  		  		+"primary key (userID, movieID));";
			
			System.out.print("Creating table user_ratedmovies_timestamps...");
		    database.executeUpdate(sqlCreateUserRatedTable);
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
